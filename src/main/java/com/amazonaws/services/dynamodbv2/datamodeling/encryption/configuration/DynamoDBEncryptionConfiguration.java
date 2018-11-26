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
package com.amazonaws.services.dynamodbv2.datamodeling.encryption.configuration;

import com.amazonaws.services.dynamodbv2.datamodeling.encryption.EncryptionConstants;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.internal.InternalEncryptionMaterialsProvider;

import java.util.Map;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public interface DynamoDBEncryptionConfiguration<T, U extends InternalEncryptionMaterialsProvider<T>> {
    /**
     *
     * @return the name of the DynamoDB field used to store the signature.
     *         Defaults to {@value EncryptionConstants#DEFAULT_SIGNATURE_FIELD}.
     */
    String getSignatureFieldName();

    /**
     * @return the name of the DynamoDB field used to store metadata used by the
     *         DynamoDBEncryptedMapper Defaults to {@value EncryptionConstants#DEFAULT_METADATA_FIELD}.
     */
    String getMaterialDescriptionFieldName();

    /**
     * @return the name of the material description field that stores the signing algorithm header
     *         Defaults to {@value EncryptionConstants#DEFAULT_METADATA_FIELD}.
     */
    String getSigningAlgorithmHeader();

    /**
     * @return the name of the material description field that stores the symmetric mode header
     *         Defaults to {@value EncryptionConstants#DEFAULT_METADATA_FIELD}.
     */
    String getSymModeHeader();

    /**
     * @return the name of the DynamoDB field used to store the signature.
     *          Defaults to {@value EncryptionConstants#DEFAULT_SIGNATURE_FIELD}.
     */
    String getDescriptionBase();

    /**
     * @return Get the operator thats used to override anything applied by the DynamoDBEncryptor
     */
    Function<T, T> getEncryptionContextTransformer();

    /**
     * @return the original EncryptionContext that is supplied to the DynamoDBEncryptor
     */
    T getEncryptionContext();

    /**
     * @return the overridden actions that should be applied by the DynamoDBEncryptor to each
     * attribute
     */
    Map<String, AttributeEncryptionAction> getAttributeEncryptionActionOverrides();

    /**
     * @return the action that should be applied by the DynamoDBEncryptor in the event that the
     * attribute doesn't have an AttributeAction
     *
     * Default action: ENCRYPT_AND_SIGN
     */
    AttributeEncryptionAction getDefaultAttributeEncryptionAction();

    /**
     * @param attributeName the name of the attribute with an associated AttributeEncryptionAction
     * @return the AttributeEncryptionAction that should be applied to a given attribute
     */
    AttributeEncryptionAction getAttributeEncryptionAction(String attributeName);

    /**
     * @return the materials provider used to retrieve encryption materials for encrypting
     * or decrypting the record
     */
    U getEncryptionMaterialsProvider();
}
