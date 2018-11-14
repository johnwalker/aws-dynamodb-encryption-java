package com.amazonaws.services.dynamodbv2.datamodeling.encryption;

import com.amazonaws.services.dynamodbv2.datamodeling.encryption.sdk2.EncryptionContextSDK2;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.sdk2.providers.EncryptionMaterialsProviderSdk2;

public interface DynamoDBEncryptionConfigurationSDK2 extends DynamoDBEncryptionConfiguration<EncryptionContextSDK2, EncryptionMaterialsProviderSdk2> {

}
