package com.amazonaws.services.dynamodbv2.datamodeling.encryption;

import com.amazonaws.services.dynamodbv2.datamodeling.encryption.internal.InternalAttributeValueTranslator;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.providers.EncryptionMaterialsProvider;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

public class DynamoDBEncryptor extends GenericDynamoDBEncryptor<AttributeValue, InternalAttributeValueTranslator<AttributeValue>> {
    protected DynamoDBEncryptor(EncryptionMaterialsProvider provider, String descriptionBase) {
        super(provider, descriptionBase);
    }

    public static DynamoDBEncryptor getInstance(EncryptionMaterialsProvider provider, String descriptionbase) {
        return new DynamoDBEncryptor(provider, descriptionbase);
    }

    public static DynamoDBEncryptor getInstance(EncryptionMaterialsProvider provider) {
        return getInstance(provider, DEFAULT_DESCRIPTION_BASE);
    }

}
