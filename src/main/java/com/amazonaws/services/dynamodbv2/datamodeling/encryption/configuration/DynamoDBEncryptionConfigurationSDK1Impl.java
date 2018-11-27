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

import com.amazonaws.services.dynamodbv2.datamodeling.encryption.EncryptionContext;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.providers.EncryptionMaterialsProvider;

public class DynamoDBEncryptionConfigurationSDK1Impl extends InternalDynamoDBEncryptionConfigurationImpl<EncryptionContext,
        EncryptionMaterialsProvider,
        DynamoDBEncryptionConfigurationSDK1.Builder,
        DynamoDBEncryptionConfigurationSDK1>
        implements DynamoDBEncryptionConfigurationSDK1 {

    private DynamoDBEncryptionConfigurationSDK1Impl() {}

    private DynamoDBEncryptionConfigurationSDK1Impl(BuilderImpl builder) {
        super(builder);
    }

    public static Builder builder() {
        return new BuilderImpl(new DynamoDBEncryptionConfigurationSDK1Impl());
    }

    @Override
    public Builder toBuilder() {
        return new BuilderImpl(this);
    }

    public static class BuilderImpl extends InternalDynamoDBEncryptionConfigurationBuilderImpl<EncryptionContext,
            EncryptionMaterialsProvider,
            DynamoDBEncryptionConfigurationSDK1.Builder,
            DynamoDBEncryptionConfigurationSDK1>
            implements DynamoDBEncryptionConfigurationSDK1.Builder {

        BuilderImpl(DynamoDBEncryptionConfigurationSDK1Impl configuration) {
            super(configuration);
        }

        @Override
        public DynamoDBEncryptionConfigurationSDK1 build() {
            return new DynamoDBEncryptionConfigurationSDK1Impl(this);
        }
    }
}
