package com.amazonaws.services.dynamodbv2.datamodeling.encryption;

import com.amazonaws.services.dynamodbv2.datamodeling.encryption.internal.InternalAttributeValueTranslator;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.providers.EncryptionMaterialsProvider;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import java.util.function.Supplier;

public class DynamoDBEncryptor extends GenericDynamoDBEncryptor<AttributeValue, EncryptionContext, EncryptionContext.Builder> {
    protected DynamoDBEncryptor(EncryptionMaterialsProvider provider, String descriptionBase) {
        super(provider, descriptionBase, (EncryptionContext encryptionContext) -> new EncryptionContext.Builder(encryptionContext));
    }

    public static DynamoDBEncryptor getInstance(EncryptionMaterialsProvider provider, String descriptionbase) {
        return new DynamoDBEncryptor(provider, descriptionbase);
    }

    public static DynamoDBEncryptor getInstance(EncryptionMaterialsProvider provider) {
        return getInstance(provider, DEFAULT_DESCRIPTION_BASE);
    }

}
