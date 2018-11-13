/*
 * Copyright 2014 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.amazonaws.services.dynamodbv2.datamodeling.encryption.internal;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import com.amazonaws.services.dynamodbv2.datamodeling.AttributeEncryptor;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.DelegatedKey;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.DynamoDBEncryptionConfiguration;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.EncryptionFlags;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.Transformer;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.materials.DecryptionMaterials;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.materials.EncryptionMaterials;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.sdk2.EncryptionContextBuilders;
import com.amazonaws.services.dynamodbv2.datamodeling.internal.Utils;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

/**
 * The low-level API used by {@link AttributeEncryptor} to perform crypto
 * operations on the record attributes.
 * 
 * @author Greg Rubin 
 */
public class InternalDynamoDBEncryptor<T,
        U extends EncryptionContextBuilders.GenericEncryptionContext<T, V>,
        V extends EncryptionContextBuilders.GenericBuilder<V, W, U>,
        W extends EncryptionContextBuilders.GenericBuilder.GenericBuilderInternalAPI<V, W, T>> {
    private static final String DEFAULT_SIGNATURE_ALGORITHM = "SHA256withRSA";
    private static final Charset UTF8 = Charset.forName("UTF-8");
    private static final String SYMMETRIC_ENCRYPTION_MODE = "/CBC/PKCS5Padding";
    private static final ConcurrentHashMap<String, Integer> BLOCK_SIZE_CACHE = new ConcurrentHashMap<>();
    private static final Function<String, Integer> BLOCK_SIZE_CALCULATOR = (transformation) -> {
        try {
            final Cipher c = Cipher.getInstance(transformation);
            return c.getBlockSize();
        } catch (final GeneralSecurityException ex) {
            throw new IllegalArgumentException("Algorithm does not exist", ex);
        }
    };

    private final DescriptionMarshaller descriptionMarshaller;
    private InternalEncryptionMaterialsProvider<U> encryptionMaterialsProvider;
    private final InternalAttributeValueTranslator<T> internalAttributeValueTranslator;

    public InternalDynamoDBEncryptor(InternalEncryptionMaterialsProvider<U> provider,
                                     InternalAttributeValueTranslator<T> internalAttributeValueTranslator,
                                     DescriptionMarshaller descriptionMarshaller) {
        this.encryptionMaterialsProvider = provider;
        this.internalAttributeValueTranslator = internalAttributeValueTranslator;
        this.descriptionMarshaller = descriptionMarshaller;
    }

    /**
     * Returns a decrypted version of the provided DynamoDb record. The signature is verified across
     * all provided fields. All fields (except those listed in <code>doNotEncrypt</code> are
     * decrypted.
     * 
     * @param itemAttributes
     *            the DynamoDbRecord
     * @param context
     *            additional information used to successfully select the encryption materials and
     *            decrypt the data. This should include (at least) the tableName and the
     *            materialDescription.
     * @param doNotDecrypt
     *            those fields which should not be encrypted
     * @return a plaintext version of the DynamoDb record
     * @throws SignatureException
     *             if the signature is invalid or cannot be verified
     * @throws GeneralSecurityException
     */
    public Map<String, T> decryptAllFieldsExcept(Map<String, T> itemAttributes,
                                                 U context,
                                                 DynamoDBEncryptionConfiguration<U> encryptionConfiguration,
                                                 String... doNotDecrypt) throws GeneralSecurityException {
        return decryptAllFieldsExcept(itemAttributes, context, encryptionConfiguration, Arrays.asList(doNotDecrypt));
    }
    
    /**
     * @see #decryptAllFieldsExcept(Map, U, String...)
     */
    public Map<String, T> decryptAllFieldsExcept(
            Map<String, T> itemAttributes,
            U context,
            DynamoDBEncryptionConfiguration<U> encryptionConfiguration,
            Collection<String> doNotDecrypt)
            throws GeneralSecurityException {
        Map<String, Set<EncryptionFlags>> attributeFlags = allDecryptionFlagsExcept(
                itemAttributes, encryptionConfiguration, doNotDecrypt);
        return decryptRecord(itemAttributes, attributeFlags, context, encryptionConfiguration);
    }

    /**
     * Returns the decryption flags for all item attributes except for those
     * explicitly specified to be excluded.
     * @param doNotDecrypt fields to be excluded
     */
    public Map<String, Set<EncryptionFlags>> allDecryptionFlagsExcept(
            Map<String, T> itemAttributes, DynamoDBEncryptionConfiguration<U> encryptionConfiguration,
            String ... doNotDecrypt) {
        return allDecryptionFlagsExcept(itemAttributes, encryptionConfiguration, Arrays.asList(doNotDecrypt));
    }

    /**
     * Returns the decryption flags for all item attributes except for those
     * explicitly specified to be excluded.
     * @param doNotDecrypt fields to be excluded
     */
    public Map<String, Set<EncryptionFlags>> allDecryptionFlagsExcept(
            Map<String, T> itemAttributes,
            DynamoDBEncryptionConfiguration<U> encryptionConfiguration,
            Collection<String> doNotDecrypt) {
        Map<String, Set<EncryptionFlags>> attributeFlags = new HashMap<String, Set<EncryptionFlags>>();

        for (String fieldName : doNotDecrypt) {
            attributeFlags.put(fieldName, EnumSet.of(EncryptionFlags.SIGN));
        }

        for (String fieldName : itemAttributes.keySet()) {
            if (!attributeFlags.containsKey(fieldName) && 
                    !fieldName.equals(encryptionConfiguration.getMaterialDescriptionFieldName()) &&
                    !fieldName.equals(encryptionConfiguration.getSignatureFieldName())) {
                attributeFlags.put(fieldName,
                        EnumSet.of(EncryptionFlags.ENCRYPT, EncryptionFlags.SIGN));
            }
        }
        return attributeFlags;
    }
    
    /**
     * Returns an encrypted version of the provided DynamoDb record. All fields are signed. All fields
     * (except those listed in <code>doNotEncrypt</code>) are encrypted.
     * @param itemAttributes a DynamoDb Record
     * @param context
     *            additional information used to successfully select the encryption materials and
     *            encrypt the data. This should include (at least) the tableName.
     * @param doNotEncrypt those fields which should not be encrypted 
     * @return a ciphertext version of the DynamoDb record
     * @throws GeneralSecurityException
     */
    public Map<String, T> encryptAllFieldsExcept(Map<String, T> itemAttributes,
            U context, DynamoDBEncryptionConfiguration<U> encryptionConfiguration, String... doNotEncrypt) throws GeneralSecurityException {
        
        return encryptAllFieldsExcept(itemAttributes, context, encryptionConfiguration, Arrays.asList(doNotEncrypt));
    }
    
    public Map<String, T> encryptAllFieldsExcept(
            Map<String, T> itemAttributes,
            U context,
            DynamoDBEncryptionConfiguration<U> encryptionConfiguration,
            Collection<String> doNotEncrypt)
            throws GeneralSecurityException {
        Map<String, Set<EncryptionFlags>> attributeFlags = allEncryptionFlagsExcept(
                itemAttributes, doNotEncrypt);
        return encryptRecord(itemAttributes, attributeFlags, context, encryptionConfiguration);
    }

    /**
     * Returns the encryption flags for all item attributes except for those
     * explicitly specified to be excluded.
     * @param doNotEncrypt fields to be excluded
     */
    public Map<String, Set<EncryptionFlags>> allEncryptionFlagsExcept(
            Map<String, T> itemAttributes,
            String ...doNotEncrypt) {
        return allEncryptionFlagsExcept(itemAttributes, Arrays.asList(doNotEncrypt));
    }

    /**
     * Returns the encryption flags for all item attributes except for those
     * explicitly specified to be excluded.
     * @param doNotEncrypt fields to be excluded
     */
    public Map<String, Set<EncryptionFlags>> allEncryptionFlagsExcept(
            Map<String, T> itemAttributes,
            Collection<String> doNotEncrypt) {
        Map<String, Set<EncryptionFlags>> attributeFlags =
            new HashMap<String, Set<EncryptionFlags>>();
        for (String fieldName : doNotEncrypt) {
            attributeFlags.put(fieldName, EnumSet.of(EncryptionFlags.SIGN));
        }

        for (String fieldName : itemAttributes.keySet()) {
            if (!attributeFlags.containsKey(fieldName)) {
                attributeFlags.put(fieldName,
                        EnumSet.of(EncryptionFlags.ENCRYPT, EncryptionFlags.SIGN));
            }
        }
        return attributeFlags;
    }

    public Map<String, T> decryptRecord(
            Map<String, T> itemAttributes,
            Map<String, Set<EncryptionFlags>> attributeFlags,
            U context,
            DynamoDBEncryptionConfiguration<U> encryptionConfiguration) throws GeneralSecurityException {
        if (attributeFlags.isEmpty()) {
            return itemAttributes;
        }
        // Copy to avoid changing anyone elses objects
        itemAttributes = new HashMap<String, T>(itemAttributes);

        Map<String, String> materialDescription = Collections.emptyMap();
        DecryptionMaterials materials;
        SecretKey decryptionKey;

        InternalDynamoDBSigner signer = InternalDynamoDBSigner.getInstance(DEFAULT_SIGNATURE_ALGORITHM, Utils.getRng());
        Map<String, InternalAttributeValue> internalAttributeValueMap = internalAttributeValueTranslator.convertFrom(itemAttributes);

        if (internalAttributeValueMap.containsKey(encryptionConfiguration.getMaterialDescriptionFieldName())) {
            materialDescription = unmarshallDescription(internalAttributeValueMap.get(encryptionConfiguration.getMaterialDescriptionFieldName()));
        }
        // Copy the material description and attribute values into the context
        context = context.toBuilder()
                .withMaterialDescription(materialDescription)
                .internalAPI()
                .withAttributeValues(itemAttributes)
                .publicAPI()
                .build();

        Transformer<U> encryptionContextTransformer = encryptionConfiguration.getEncryptionContextTransformer();
        if(encryptionContextTransformer != null) {
            context = encryptionContextTransformer.transform(context);
        }

        materials = encryptionMaterialsProvider.getDecryptionMaterials(context);
        decryptionKey = materials.getDecryptionKey();
        if (materialDescription.containsKey(encryptionConfiguration.getSigningAlgorithmHeader())) {
            String signingAlg = materialDescription.get(encryptionConfiguration.getSigningAlgorithmHeader());
            signer = InternalDynamoDBSigner.getInstance(signingAlg, Utils.getRng());
        }
        
        ByteBuffer signature;
        if (!internalAttributeValueMap.containsKey(encryptionConfiguration.getSignatureFieldName()) || internalAttributeValueMap.get(encryptionConfiguration.getSignatureFieldName()).getB() == null) {
            signature = ByteBuffer.allocate(0);
        } else {
            signature = internalAttributeValueMap.get(encryptionConfiguration.getSignatureFieldName()).getB().asReadOnlyBuffer();
        }
        internalAttributeValueMap.remove(encryptionConfiguration.getSignatureFieldName());

        String associatedData = "TABLE>" + context.getTableName() + "<TABLE";
        signer.verifySignature(internalAttributeValueMap, attributeFlags, associatedData.getBytes(UTF8),
                materials.getVerificationKey(), signature);
        internalAttributeValueMap.remove(encryptionConfiguration.getMaterialDescriptionFieldName());

        actualDecryption(internalAttributeValueMap, attributeFlags, decryptionKey, materialDescription, encryptionConfiguration);
        return internalAttributeValueTranslator.convertFromInternal(internalAttributeValueMap);
    }

    /**
     * Returns the encrypted (and signed) record, which is a map of item
     * attributes. There is no side effect on the input parameters upon calling
     * this method.
     * 
     * @param itemAttributes
     *            the input record
     * @param attributeFlags
     *            the corresponding encryption flags
     * @param context
     *            encryption context
     * @return a new instance of item attributes encrypted as necessary
     * @throws GeneralSecurityException
     *             if failed to encrypt the record
     */
    public Map<String, T> encryptRecord(
            Map<String, T> itemAttributes,
            Map<String, Set<EncryptionFlags>> attributeFlags,
            U context,
            DynamoDBEncryptionConfiguration<U> encryptionConfiguration) throws GeneralSecurityException {
        if (attributeFlags.isEmpty()) {
            return itemAttributes;
        }
        // Copy to avoid changing anyone elses objects
        itemAttributes = new HashMap<String, T>(itemAttributes);

        // Copy the attribute values into the context
        context = context.toBuilder().internalAPI().withAttributeValues(itemAttributes).publicAPI().build();

        Transformer<U> encryptionContextTransformer = encryptionConfiguration.getEncryptionContextTransformer();
        if(encryptionContextTransformer != null) {
            context = encryptionContextTransformer.transform(context);
        }


        if (encryptionConfiguration.getEncryptionContextTransformer() != null) {
            context = encryptionConfiguration.getEncryptionContextTransformer().transform(context);
        }

        EncryptionMaterials materials = encryptionMaterialsProvider.getEncryptionMaterials(context);
        // We need to copy this because we modify it to record other encryption details
        Map<String, String> materialDescription = new HashMap<String, String>(
                materials.getMaterialDescription());
        SecretKey encryptionKey = materials.getEncryptionKey();

        Map<String, InternalAttributeValue> internalAttributeValueMap = internalAttributeValueTranslator.convertFrom(itemAttributes);
        actualEncryption(internalAttributeValueMap, attributeFlags, materialDescription, encryptionKey, encryptionConfiguration);

        // The description must be stored after encryption because its data
        // is necessary for proper decryption.
        final String signingAlgo = materialDescription.get(encryptionConfiguration.getSigningAlgorithmHeader());
        InternalDynamoDBSigner signer;
        if (signingAlgo != null) {
            signer = InternalDynamoDBSigner.getInstance(signingAlgo, Utils.getRng());
        } else {
            signer = InternalDynamoDBSigner.getInstance(DEFAULT_SIGNATURE_ALGORITHM, Utils.getRng());
        }

        if (materials.getSigningKey() instanceof PrivateKey ) {
            materialDescription.put(encryptionConfiguration.getSigningAlgorithmHeader(), signer.getSigningAlgorithm());
        }
        if (!materialDescription.isEmpty()) {
            InternalAttributeValue internalAttributeValue = marshallDescription(materialDescription);
            internalAttributeValueMap.put(encryptionConfiguration.getMaterialDescriptionFieldName(), internalAttributeValue);
        }

        String associatedData = "TABLE>" + context.getTableName() + "<TABLE";
        byte[] signature = signer.calculateSignature(internalAttributeValueMap, attributeFlags,
                associatedData.getBytes(UTF8), materials.getSigningKey());

        InternalAttributeValue signatureAttribute = new InternalAttributeValue();
        signatureAttribute.setB(ByteBuffer.wrap(signature));
        internalAttributeValueMap.put(encryptionConfiguration.getSignatureFieldName(), signatureAttribute);

        return internalAttributeValueTranslator.convertFromInternal(internalAttributeValueMap);
    }
    
    private void actualDecryption(Map<String, InternalAttributeValue> itemAttributes,
                                  Map<String, Set<EncryptionFlags>> attributeFlags, SecretKey encryptionKey,
                                  Map<String, String> materialDescription,
                                  DynamoDBEncryptionConfiguration<U> encryptionConfiguration) throws GeneralSecurityException {
        final String encryptionMode = encryptionKey != null ?  encryptionKey.getAlgorithm() +
                    materialDescription.get(encryptionConfiguration.getSymModeHeader()) : null;
        Cipher cipher = null;
        int blockSize = -1;

        for (Map.Entry<String, InternalAttributeValue> entry: itemAttributes.entrySet()) {
            Set<EncryptionFlags> flags = attributeFlags.get(entry.getKey());
            if (flags != null && flags.contains(EncryptionFlags.ENCRYPT)) {
                if (!flags.contains(EncryptionFlags.SIGN)) {
                    throw new IllegalArgumentException("All encrypted fields must be signed. Bad field: " + entry.getKey());
                }
                ByteBuffer plainText;
                ByteBuffer cipherText = entry.getValue().getB().asReadOnlyBuffer();
                cipherText.rewind();
                if (encryptionKey instanceof DelegatedKey) {
                    plainText = ByteBuffer.wrap(((DelegatedKey)encryptionKey).decrypt(InternalByteBufferUtils.toByteArray(cipherText), null, encryptionMode));
                } else {
                    if (cipher == null) {
                        blockSize = getBlockSize(encryptionMode);
                        cipher = Cipher.getInstance(encryptionMode);
                    }
                    byte[] iv = new byte[blockSize];
                    cipherText.get(iv);
                    cipher.init(Cipher.DECRYPT_MODE, encryptionKey, new IvParameterSpec(iv), Utils.getRng());
                    plainText = ByteBuffer.allocate(cipher.getOutputSize(cipherText.remaining()));
                    cipher.doFinal(cipherText, plainText);
                    plainText.rewind();
                }
                entry.setValue(InternalAttributeValueMarshaller.unmarshall(plainText));
            }
        }
    }

    public static int getBlockSize(final String encryptionMode) {
        return BLOCK_SIZE_CACHE.computeIfAbsent(encryptionMode, BLOCK_SIZE_CALCULATOR);
    }

    /**
     * This method has the side effect of replacing the plaintext
     * attribute-values of "itemAttributes" with ciphertext attribute-values
     * (which are always in the form of ByteBuffer) as per the corresponding
     * attribute flags.
     */
    private void actualEncryption(Map<String, InternalAttributeValue> itemAttributes,
                                  Map<String, Set<EncryptionFlags>> attributeFlags,
                                  Map<String, String> materialDescription,
                                  SecretKey encryptionKey,
                                  DynamoDBEncryptionConfiguration<U> encryptionConfiguration) throws GeneralSecurityException {
        String encryptionMode = null;
        if (encryptionKey != null) {
            materialDescription.put(encryptionConfiguration.getSymModeHeader(),
                    SYMMETRIC_ENCRYPTION_MODE);
            encryptionMode = encryptionKey.getAlgorithm() + SYMMETRIC_ENCRYPTION_MODE;
        }
        Cipher cipher = null;
        int blockSize = -1;

        for (Map.Entry<String, InternalAttributeValue> entry: itemAttributes.entrySet()) {
            Set<EncryptionFlags> flags = attributeFlags.get(entry.getKey());
            if (flags != null && flags.contains(EncryptionFlags.ENCRYPT)) {
                if (!flags.contains(EncryptionFlags.SIGN)) {
                    throw new IllegalArgumentException("All encrypted fields must be signed. Bad field: " + entry.getKey());
                }
                ByteBuffer plainText = InternalAttributeValueMarshaller.marshall(entry.getValue());
                plainText.rewind();
                ByteBuffer cipherText;
                if (encryptionKey instanceof DelegatedKey) {
                    DelegatedKey dk = (DelegatedKey) encryptionKey;
                    cipherText = ByteBuffer.wrap(
                            dk.encrypt(InternalByteBufferUtils.toByteArray(plainText), null, encryptionMode));
                } else {
                    if (cipher == null) {
                        blockSize = getBlockSize(encryptionMode);
                        cipher = Cipher.getInstance(encryptionMode);
                    }
                    // Encryption format: <iv><ciphertext>
                    // Note a unique iv is generated per attribute
                    cipher.init(Cipher.ENCRYPT_MODE, encryptionKey, Utils.getRng());
                    cipherText = ByteBuffer.allocate(blockSize + cipher.getOutputSize(plainText.remaining()));
                    cipherText.position(blockSize);
                    cipher.doFinal(plainText, cipherText);
                    cipherText.flip();
                    final byte[] iv = cipher.getIV();
                    if (iv.length != blockSize) {
                        throw new IllegalStateException(String.format("Generated IV length (%d) not equal to block size (%d)",
                                iv.length, blockSize));
                    }
                    cipherText.put(iv);
                    cipherText.rewind();
                }
                // Replace the plaintext attribute value with the encrypted content
                InternalAttributeValue internalAttributeValue = new InternalAttributeValue();
                internalAttributeValue.setB(cipherText);
                entry.setValue(internalAttributeValue);
            }
        }
    }
    
    /**
     * Marshalls the <code>description</code> into a ByteBuffer by outputting
     * each key (modified UTF-8) followed by its value (also in modified UTF-8).
     *
     * @param description
     * @return the description encoded as an AttributeValue with a ByteBuffer value
     * @see java.io.DataOutput#writeUTF(String)
     */
    private InternalAttributeValue marshallDescription(Map<String, String> description) {
        byte[] bytes = descriptionMarshaller.marshallDescription(description);
        InternalAttributeValue internalAttributeValue = new InternalAttributeValue();
        internalAttributeValue.setB(ByteBuffer.wrap(bytes));
        return internalAttributeValue;
    }

    /**
     * @see #marshallDescription(Map)
     */
    private Map<String, String> unmarshallDescription(InternalAttributeValue attributeValue) {
        return descriptionMarshaller.unmarshallDescription(attributeValue.getB());
    }
}
