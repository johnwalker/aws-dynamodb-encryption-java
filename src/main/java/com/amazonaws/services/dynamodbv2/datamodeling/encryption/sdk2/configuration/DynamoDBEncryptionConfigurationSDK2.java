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

import com.amazonaws.services.dynamodbv2.datamodeling.encryption.configuration.InternalDynamoDBEncryptionConfiguration;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.configuration.InternalDynamoDBEncryptionConfigurationBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.configuration.InternalDynamoDBEncryptionConfigurationImpl;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.sdk2.EncryptionContextSDK2;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.sdk2.providers.EncryptionMaterialsProviderSdk2;

public interface DynamoDBEncryptionConfigurationSDK2 extends InternalDynamoDBEncryptionConfiguration<EncryptionContextSDK2, EncryptionMaterialsProviderSdk2, DynamoDBEncryptionConfigurationSDK2.Builder> {
    interface Builder extends InternalDynamoDBEncryptionConfigurationBuilder<EncryptionContextSDK2, EncryptionMaterialsProviderSdk2, Builder, DynamoDBEncryptionConfigurationSDK2> {

    }

    static DynamoDBEncryptionConfigurationSDK2.Builder builder() {
        return DynamoDBEncryptionConfigurationSDK2Impl.builder();
    }
}
