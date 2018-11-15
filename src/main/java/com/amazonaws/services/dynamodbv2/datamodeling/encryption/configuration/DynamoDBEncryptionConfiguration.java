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

import java.util.function.UnaryOperator;

public interface DynamoDBEncryptionConfiguration<T, U extends InternalEncryptionMaterialsProvider<T>> {
    /**
     * Get the name of the DynamoDB field used to store the signature.
     * Defaults to {@value EncryptionConstants#DEFAULT_SIGNATURE_FIELD}.
     *
     * @return the name of the DynamoDB field used to store the signature
     */
    String getSignatureFieldName();

    /**
     * Set the name of the DynamoDB field used to store the signature.
     * Defaults to {@value EncryptionConstants#DEFAULT_SIGNING_ALGORITHM_HEADER}
     *
     * @param signatureFieldName
     */

    /**
     * Get the name of the DynamoDB field used to store metadata used by the
     * DynamoDBEncryptedMapper. Defaults to {@value EncryptionConstants#DEFAULT_METADATA_FIELD}.
     *
     * @return the name of the DynamoDB field used to store metadata used by the
     *         DynamoDBEncryptedMapper
     */
    String getMaterialDescriptionFieldName();

    /**
     * Set the name of the DynamoDB field used to store metadata used by the
     * DynamoDBEncryptedMapper
     *
     * @param materialDescriptionFieldName
     */

    String getSigningAlgorithmHeader();
    String getSymModeHeader();

    /**
     * Get the name of the DynamoDB field used to store the signature.
     * Defaults to {@value EncryptionConstants#DEFAULT_SIGNATURE_FIELD}.
     *
     * @return the name of the DynamoDB field used to store the signature
     */
    String getDescriptionBase();

    /**
     * Set the name of the DynamoDB field used to store the signature.
     * Defaults to {@value EncryptionConstants#DEFAULT_SIGNING_ALGORITHM_HEADER}
     *
     * @param signatureFieldName
     */


    UnaryOperator<T> getEncryptionContextTransformer();


    U getEncryptionMaterialsProvider();
}
