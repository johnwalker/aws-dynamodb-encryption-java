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
package com.amazonaws.services.dynamodbv2.datamodeling.encryption;

import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.SignatureException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.amazonaws.services.dynamodbv2.datamodeling.AttributeEncryptor;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.internal.DescriptionMarshaller;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.internal.InternalAttributeValueTranslatorSdk1;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.internal.InternalDynamoDBEncryptor;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.providers.EncryptionMaterialsProvider;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.sdk2.EncryptionContextBuilders;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.util.CollectionUtils;
import software.amazon.awssdk.utils.StringUtils;

import static com.amazonaws.services.dynamodbv2.datamodeling.encryption.EncryptionConstants.DEFAULT_DESCRIPTION_BASE;
import static com.amazonaws.services.dynamodbv2.datamodeling.encryption.EncryptionConstants.DEFAULT_METADATA_FIELD;
import static com.amazonaws.services.dynamodbv2.datamodeling.encryption.EncryptionConstants.DEFAULT_SIGNATURE_FIELD;

/**
 * The low-level API used by {@link AttributeEncryptor} to perform crypto
 * operations on the record attributes.
 *
 * @author Greg Rubin
 */
public class DynamoDBEncryptor implements DynamoDBEncryptionConfiguration {
    private String signatureFieldName = DEFAULT_SIGNATURE_FIELD;
    private String materialDescriptionFieldName = DEFAULT_METADATA_FIELD;

    private EncryptionMaterialsProvider encryptionMaterialsProvider;
    private final String descriptionBase;
    private final String symmetricEncryptionModeHeader;
    private final String signingAlgorithmHeader;
    private InternalDynamoDBEncryptor<AttributeValue, EncryptionContext, EncryptionContextBuilders.SDK1Builders.Builder, EncryptionContextBuilders.SDK1Builders.BuilderInternalAPI> internalEncryptor;
    private static DescriptionMarshaller DESCRIPTION_MARSHALLER = new DescriptionMarshaller();

    public static final String DEFAULT_SIGNING_ALGORITHM_HEADER = EncryptionConstants.DEFAULT_SIGNING_ALGORITHM_HEADER;

    protected DynamoDBEncryptor(EncryptionMaterialsProvider provider, String descriptionBase) {
        this.encryptionMaterialsProvider = provider;
        this.descriptionBase = descriptionBase;
        symmetricEncryptionModeHeader = this.descriptionBase + "sym-mode";
        signingAlgorithmHeader = this.descriptionBase + "signingAlg";
        internalEncryptor = new InternalDynamoDBEncryptor<>(provider, descriptionBase,
                new InternalAttributeValueTranslatorSdk1(), new DescriptionMarshaller(), (DynamoDBEncryptionConfiguration) this);
    }

    public static DynamoDBEncryptor getInstance(EncryptionMaterialsProvider provider, String descriptionbase) {
        return new DynamoDBEncryptor(provider, descriptionbase);
    }

    public static DynamoDBEncryptor getInstance(EncryptionMaterialsProvider provider) {
        return getInstance(provider, DEFAULT_DESCRIPTION_BASE);
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
    public Map<String, AttributeValue> decryptAllFieldsExcept(Map<String, AttributeValue> itemAttributes,
                                                              EncryptionContext context, String... doNotDecrypt) throws GeneralSecurityException {
        return decryptAllFieldsExcept(itemAttributes, context, Arrays.asList(doNotDecrypt));
    }

    /**
     * @see #decryptAllFieldsExcept(Map, EncryptionContext, String...)
     */
    public Map<String, AttributeValue> decryptAllFieldsExcept(
            Map<String, AttributeValue> itemAttributes,
            EncryptionContext context, Collection<String> doNotDecrypt)
            throws GeneralSecurityException {
        Map<String, Set<EncryptionFlags>> attributeFlags = allDecryptionFlagsExcept(
                itemAttributes, doNotDecrypt);
        return decryptRecord(itemAttributes, attributeFlags, context);
    }

    /**
     * Returns the decryption flags for all item attributes except for those
     * explicitly specified to be excluded.
     * @param doNotDecrypt fields to be excluded
     */
    public Map<String, Set<EncryptionFlags>> allDecryptionFlagsExcept(
            Map<String, AttributeValue> itemAttributes,
            String ... doNotDecrypt) {
        return allDecryptionFlagsExcept(itemAttributes, Arrays.asList(doNotDecrypt));
    }

    /**
     * Returns the decryption flags for all item attributes except for those
     * explicitly specified to be excluded.
     * @param doNotDecrypt fields to be excluded
     */
    public Map<String, Set<EncryptionFlags>> allDecryptionFlagsExcept(
            Map<String, AttributeValue> itemAttributes,
            Collection<String> doNotDecrypt) {
        return internalEncryptor.allDecryptionFlagsExcept(itemAttributes, doNotDecrypt);
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
    public Map<String, AttributeValue> encryptAllFieldsExcept(Map<String, AttributeValue> itemAttributes,
                                                              EncryptionContext context, String... doNotEncrypt) throws GeneralSecurityException {

        return encryptAllFieldsExcept(itemAttributes, context, Arrays.asList(doNotEncrypt));
    }

    public Map<String, AttributeValue> encryptAllFieldsExcept(
            Map<String, AttributeValue> itemAttributes,
            EncryptionContext context,
            Collection<String> doNotEncrypt)
            throws GeneralSecurityException {
        Map<String, Set<EncryptionFlags>> attributeFlags = allEncryptionFlagsExcept(
                itemAttributes, doNotEncrypt);
        return encryptRecord(itemAttributes, attributeFlags, context);
    }

    /**
     * Returns the encryption flags for all item attributes except for those
     * explicitly specified to be excluded.
     * @param doNotEncrypt fields to be excluded
     */
    public Map<String, Set<EncryptionFlags>> allEncryptionFlagsExcept(
            Map<String, AttributeValue> itemAttributes,
            String ...doNotEncrypt) {
        return allEncryptionFlagsExcept(itemAttributes, Arrays.asList(doNotEncrypt));
    }

    /**
     * Returns the encryption flags for all item attributes except for those
     * explicitly specified to be excluded.
     * @param doNotEncrypt fields to be excluded
     */
    public Map<String, Set<EncryptionFlags>> allEncryptionFlagsExcept(
            Map<String, AttributeValue> itemAttributes,
            Collection<String> doNotEncrypt) {
        return internalEncryptor.allEncryptionFlagsExcept(itemAttributes, doNotEncrypt);
    }

    public Map<String, AttributeValue> decryptRecord(
            Map<String, AttributeValue> itemAttributes,
            Map<String, Set<EncryptionFlags>> attributeFlags,
            EncryptionContext context) throws GeneralSecurityException {
        return internalEncryptor.decryptRecord(itemAttributes, attributeFlags, context);
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
    public Map<String, AttributeValue> encryptRecord(
            Map<String, AttributeValue> itemAttributes,
            Map<String, Set<EncryptionFlags>> attributeFlags,
            EncryptionContext context) throws GeneralSecurityException {
        return internalEncryptor.encryptRecord(itemAttributes, attributeFlags, context);
    }

    protected static int getBlockSize(final String encryptionMode) {
        return InternalDynamoDBEncryptor.getBlockSize(encryptionMode);
    }

    /**
     * Get the name of the DynamoDB field used to store the signature.
     * Defaults to {@value EncryptionConstants#DEFAULT_SIGNATURE_FIELD}.
     *
     * @return the name of the DynamoDB field used to store the signature
     */
    public String getSignatureFieldName() {
        return signatureFieldName;
    }

    /**
     * Set the name of the DynamoDB field used to store the signature.
     * Defaults to {@value EncryptionConstants#DEFAULT_SIGNING_ALGORITHM_HEADER}
     *
     * @param signatureFieldName
     */
    public void setSignatureFieldName(final String signatureFieldName) {
        this.signatureFieldName = signatureFieldName;
    }

    /**
     * Get the name of the DynamoDB field used to store metadata used by the
     * DynamoDBEncryptedMapper. Defaults to {@value EncryptionConstants#DEFAULT_METADATA_FIELD}.
     *
     * @return the name of the DynamoDB field used to store metadata used by the
     *         DynamoDBEncryptedMapper
     */
    public String getMaterialDescriptionFieldName() {
        return materialDescriptionFieldName;
    }

    /**
     * Set the name of the DynamoDB field used to store metadata used by the
     * DynamoDBEncryptedMapper
     *
     * @param materialDescriptionFieldName
     */
    public void setMaterialDescriptionFieldName(final String materialDescriptionFieldName) {
        this.materialDescriptionFieldName = materialDescriptionFieldName;
    }

    /**
     * Marshalls the <code>description</code> into a ByteBuffer by outputting
     * each key (modified UTF-8) followed by its value (also in modified UTF-8).
     *
     * @param description
     * @return the description encoded as an AttributeValue with a ByteBuffer value
     * @see java.io.DataOutput#writeUTF(String)
     */
    protected static AttributeValue marshallDescription(Map<String, String> description) {
        byte[] bytes = DESCRIPTION_MARSHALLER.marshallDescription(description);
        AttributeValue result = new AttributeValue();
        result.setB(ByteBuffer.wrap(bytes));
        return result;
    }

    public String getSigningAlgorithmHeader() {
        return signingAlgorithmHeader;
    }
    /**
     * @see #marshallDescription(Map)
     */
    protected static Map<String, String> unmarshallDescription(AttributeValue attributeValue) {
        return DESCRIPTION_MARSHALLER.unmarshallDescription(attributeValue.getB());
    }
}
