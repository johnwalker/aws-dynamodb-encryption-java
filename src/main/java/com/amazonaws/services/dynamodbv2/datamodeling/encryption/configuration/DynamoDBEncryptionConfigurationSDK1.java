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
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.EncryptionContext;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.providers.EncryptionMaterialsProvider;

import java.util.function.UnaryOperator;

public interface DynamoDBEncryptionConfigurationSDK1 extends DynamoDBEncryptionConfiguration<EncryptionContext, EncryptionMaterialsProvider> {

    static Builder builder() {
        return new DynamoDBEncryptionConfigurationSDK1Impl.DynamoDBEncryptionConfigurationSDK1ImplBuilder();
    }

    default Builder toBuilder() {
        return new DynamoDBEncryptionConfigurationSDK1Impl.DynamoDBEncryptionConfigurationSDK1ImplBuilder(this);
    }

    interface Builder {
        /**
         * Get the name of the DynamoDB field used to store the signature.
         * Defaults to {@value EncryptionConstants#DEFAULT_SIGNATURE_FIELD}.
         *
         * @return the name of the DynamoDB field used to store the signature
         */
        Builder withSignatureFieldName(String signatureFieldName);

        Builder withMaterialDescriptionFieldName(String descriptionFieldName);

        Builder withDescriptionBase(String descriptionBase);

        Builder withEncryptionContextTransformer(UnaryOperator<EncryptionContext> transformer);

        Builder withEncryptionMaterialsProvider(EncryptionMaterialsProvider encryptionMaterialsProvider);

        DynamoDBEncryptionConfigurationSDK1 build();
    }


}
