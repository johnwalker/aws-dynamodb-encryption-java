package com.amazonaws.services.dynamodbv2.datamodeling.encryption;

import com.amazonaws.services.dynamodbv2.datamodeling.encryption.internal.InternalAttributeValueTranslatorSdk2;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.providers.EncryptionMaterialsProviderSdk2;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class DynamoDBEncryptorSdk2 extends GenericDynamoDBEncryptor<AttributeValue, EncryptionContextSDK2, EncryptionContextSDK2.Builder> {
    protected DynamoDBEncryptorSdk2(EncryptionMaterialsProviderSdk2 provider, String descriptionBase) {
        super(provider, descriptionBase,
                (EncryptionContextSDK2 encryptionContextSDK2) -> new EncryptionContextSDK2.Builder(encryptionContextSDK2),
                new InternalAttributeValueTranslatorSdk2());
    }

    public static DynamoDBEncryptorSdk2 getInstance(EncryptionMaterialsProviderSdk2 provider, String descriptionbase) {
        return new DynamoDBEncryptorSdk2(provider, descriptionbase);
    }

    public static DynamoDBEncryptorSdk2 getInstance(EncryptionMaterialsProviderSdk2 provider) {
        return getInstance(provider, DEFAULT_DESCRIPTION_BASE);
    }

}
