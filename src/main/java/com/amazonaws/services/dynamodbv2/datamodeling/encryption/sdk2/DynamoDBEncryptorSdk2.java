package com.amazonaws.services.dynamodbv2.datamodeling.encryption.sdk2;

import com.amazonaws.services.dynamodbv2.datamodeling.encryption.DefaultDynamoDBEncryptionConfiguration;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.internal.DescriptionMarshaller;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.internal.InternalDynamoDBEncryptor;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.internal.InternalAttributeValueTranslatorSdk2;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.sdk2.providers.EncryptionMaterialsProviderSdk2;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class DynamoDBEncryptorSdk2 extends InternalDynamoDBEncryptor<AttributeValue, EncryptionContextSDK2, EncryptionContextBuilders.SDK2Builders.Builder> {
    protected DynamoDBEncryptorSdk2(EncryptionMaterialsProviderSdk2 provider, String descriptionBase) {
        super(provider, descriptionBase,
                (EncryptionContextSDK2 encryptionContextSDK2) -> new EncryptionContextBuilders.SDK2Builders.Builder(encryptionContextSDK2),
                new InternalAttributeValueTranslatorSdk2(), new DescriptionMarshaller(), new DefaultDynamoDBEncryptionConfiguration());
    }

    public static DynamoDBEncryptorSdk2 getInstance(EncryptionMaterialsProviderSdk2 provider, String descriptionbase) {
        return new DynamoDBEncryptorSdk2(provider, descriptionbase);
    }

    public static DynamoDBEncryptorSdk2 getInstance(EncryptionMaterialsProviderSdk2 provider) {
        return getInstance(provider, DEFAULT_DESCRIPTION_BASE);
    }

}
