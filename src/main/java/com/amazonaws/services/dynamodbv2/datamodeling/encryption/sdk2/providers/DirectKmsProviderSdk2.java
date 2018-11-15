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
package com.amazonaws.services.dynamodbv2.datamodeling.encryption.sdk2.providers;

import com.amazonaws.services.dynamodbv2.datamodeling.encryption.materials.DecryptionMaterials;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.materials.EncryptionMaterials;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.sdk2.EncryptionContextSDK2;
import com.amazonaws.services.kms.model.GenerateDataKeyRequest;
import software.amazon.awssdk.awscore.AwsRequest;
import software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration;
import software.amazon.awssdk.core.SdkRequest;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.KmsClientBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * No user agent support
 */
public class DirectKmsProviderSdk2 implements EncryptionMaterialsProviderSdk2 {

    private final KmsClient kmsClient;

    public DirectKmsProviderSdk2(KmsClient kmsClient) {
        this.kmsClient = kmsClient;

    }
    @Override
    public DecryptionMaterials getDecryptionMaterials(EncryptionContextSDK2 context) {
        final Map<String, String> materialDescription = context.getMaterialDescription();

        final Map<String, String> ec = new HashMap<>();

        return null;
    }

    @Override
    public EncryptionMaterials getEncryptionMaterials(EncryptionContextSDK2 context) {
        return null;
    }

    @Override
    public void refresh() {

    }
}

