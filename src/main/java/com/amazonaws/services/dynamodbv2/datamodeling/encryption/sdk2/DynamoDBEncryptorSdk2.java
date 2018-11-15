/*
 * Copyright 2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
package com.amazonaws.services.dynamodbv2.datamodeling.encryption.sdk2;

import com.amazonaws.services.dynamodbv2.datamodeling.encryption.sdk2.configuration.DynamoDBEncryptionConfigurationSDK2;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.EncryptionFlags;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.internal.DescriptionMarshaller;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.internal.InternalAttributeValueTranslatorSdk2;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.internal.InternalDynamoDBEncryptor;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.security.GeneralSecurityException;
import java.security.SignatureException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class DynamoDBEncryptorSdk2 {
    private InternalDynamoDBEncryptor<AttributeValue,
            EncryptionContextSDK2,
            EncryptionContextBuilders.SDK2Builders.Builder,
            EncryptionContextBuilders.SDK2Builders.BuilderInternalAPI,
            DynamoDBEncryptionConfigurationSDK2> internalEncryptor;
    private static DescriptionMarshaller DESCRIPTION_MARSHALLER = new DescriptionMarshaller();
    private DynamoDBEncryptionConfigurationSDK2 encryptionConfiguration;

    public DynamoDBEncryptorSdk2(DynamoDBEncryptionConfigurationSDK2 encryptionConfiguration) {
        this.encryptionConfiguration = encryptionConfiguration;
        internalEncryptor = new InternalDynamoDBEncryptor<>(new InternalAttributeValueTranslatorSdk2(), DESCRIPTION_MARSHALLER);
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
                                                              EncryptionContextSDK2 context, String... doNotDecrypt)
            throws GeneralSecurityException, SignatureException {
        return decryptAllFieldsExcept(itemAttributes, context, Arrays.asList(doNotDecrypt));
    }

    /**
     * @see #decryptAllFieldsExcept(Map, EncryptionContextSDK2, String...)
     */
    public Map<String, AttributeValue> decryptAllFieldsExcept(
            Map<String, AttributeValue> itemAttributes,
            EncryptionContextSDK2 context, Collection<String> doNotDecrypt)
            throws GeneralSecurityException {
        Map<String, Set<EncryptionFlags>> attributeFlags = allDecryptionFlagsExcept(
                itemAttributes, doNotDecrypt);
        return decryptRecord(itemAttributes, attributeFlags, context, encryptionConfiguration);
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
        return internalEncryptor.allDecryptionFlagsExcept(itemAttributes, encryptionConfiguration, doNotDecrypt);
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
                                                              EncryptionContextSDK2 context, String... doNotEncrypt) throws GeneralSecurityException {

        return encryptAllFieldsExcept(itemAttributes, context, Arrays.asList(doNotEncrypt));
    }

    public Map<String, AttributeValue> encryptAllFieldsExcept(
            Map<String, AttributeValue> itemAttributes,
            EncryptionContextSDK2 context,
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
            EncryptionContextSDK2 context) throws GeneralSecurityException {
        return decryptRecord(itemAttributes, attributeFlags, context, encryptionConfiguration);
    }

    public Map<String, AttributeValue> decryptRecord(
            Map<String, AttributeValue> itemAttributes,
            Map<String, Set<EncryptionFlags>> attributeFlags,
            EncryptionContextSDK2 context,
            DynamoDBEncryptionConfigurationSDK2 encryptionConfiguration) throws GeneralSecurityException {
        return internalEncryptor.decryptRecord(itemAttributes, attributeFlags, context, encryptionConfiguration);
    }

    public Map<String, AttributeValue> encryptRecord(
            Map<String, AttributeValue> itemAttributes,
            Map<String, Set<EncryptionFlags>> attributeFlags,
            EncryptionContextSDK2 context) throws GeneralSecurityException {
        return encryptRecord(itemAttributes, attributeFlags, context, encryptionConfiguration);
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
     * @param encryptionConfiguration
     *            encryption configuration
     * @return a new instance of item attributes encrypted as necessary
     * @throws GeneralSecurityException
     *             if failed to encrypt the record
     */
    public Map<String, AttributeValue> encryptRecord(
            Map<String, AttributeValue> itemAttributes,
            Map<String, Set<EncryptionFlags>> attributeFlags,
            EncryptionContextSDK2 context,
            DynamoDBEncryptionConfigurationSDK2 encryptionConfiguration) throws GeneralSecurityException {
        return internalEncryptor.encryptRecord(itemAttributes, attributeFlags, context, encryptionConfiguration);
    }

    protected static int getBlockSize(final String encryptionMode) {
        return InternalDynamoDBEncryptor.getBlockSize(encryptionMode);
    }
}