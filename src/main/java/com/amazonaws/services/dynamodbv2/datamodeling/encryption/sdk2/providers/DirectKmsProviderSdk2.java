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

