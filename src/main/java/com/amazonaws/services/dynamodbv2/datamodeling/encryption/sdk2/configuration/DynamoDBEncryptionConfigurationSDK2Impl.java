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
package com.amazonaws.services.dynamodbv2.datamodeling.encryption.sdk2.configuration;

import com.amazonaws.services.dynamodbv2.datamodeling.encryption.configuration.InternalDynamoDBEncryptionConfigurationImpl;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.sdk2.EncryptionContextSDK2;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.sdk2.providers.EncryptionMaterialsProviderSdk2;


public class DynamoDBEncryptionConfigurationSDK2Impl extends InternalDynamoDBEncryptionConfigurationImpl<EncryptionContextSDK2,
        EncryptionMaterialsProviderSdk2,
        DynamoDBEncryptionConfigurationSDK2.Builder,
        DynamoDBEncryptionConfigurationSDK2>
        implements DynamoDBEncryptionConfigurationSDK2 {

    private DynamoDBEncryptionConfigurationSDK2Impl() {}

    private DynamoDBEncryptionConfigurationSDK2Impl(BuilderImpl builder) {
        super(builder);
    }

    public static Builder builder() {
        return new BuilderImpl(new DynamoDBEncryptionConfigurationSDK2Impl());
    }

    @Override
    public Builder toBuilder() {
        return new BuilderImpl(this);
    }

    public static class BuilderImpl extends InternalDynamoDBEncryptionConfigurationBuilderImpl<EncryptionContextSDK2,
            EncryptionMaterialsProviderSdk2,
            DynamoDBEncryptionConfigurationSDK2.Builder,
            DynamoDBEncryptionConfigurationSDK2>
            implements DynamoDBEncryptionConfigurationSDK2.Builder {

        public BuilderImpl(DynamoDBEncryptionConfigurationSDK2Impl configurationSDK2) {
            super(configurationSDK2);
        }

        @Override
        public DynamoDBEncryptionConfigurationSDK2 build() {
            return new DynamoDBEncryptionConfigurationSDK2Impl(this);
        }
    }
}
