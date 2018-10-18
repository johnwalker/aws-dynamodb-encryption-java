package com.amazonaws.services.dynamodbv2.datamodeling.encryption;

import com.amazonaws.services.dynamodbv2.datamodeling.encryption.internal.InternalAttributeValueTranslatorSdk1;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.providers.EncryptionMaterialsProvider;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;


public class DynamoDBEncryptor extends GenericDynamoDBEncryptor<AttributeValue, EncryptionContext, EncryptionContext.Builder> {
    protected DynamoDBEncryptor(EncryptionMaterialsProvider provider, String descriptionBase) {
        super(provider, descriptionBase,
                (EncryptionContext encryptionContext) -> new EncryptionContext.Builder(encryptionContext),
                new InternalAttributeValueTranslatorSdk1());
    }

    public static DynamoDBEncryptor getInstance(EncryptionMaterialsProvider provider, String descriptionbase) {
        return new DynamoDBEncryptor(provider, descriptionbase);
    }

    public static DynamoDBEncryptor getInstance(EncryptionMaterialsProvider provider) {
        return getInstance(provider, DEFAULT_DESCRIPTION_BASE);
    }

}
