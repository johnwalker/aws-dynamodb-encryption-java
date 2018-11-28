package com.amazonaws.services.dynamodbv2.datamodeling.encryption.configuration;

import com.amazonaws.services.dynamodbv2.datamodeling.encryption.internal.InternalEncryptionMaterialsProvider;

import java.util.Map;
import java.util.function.Function;

public interface InternalDynamoDBEncryptionConfigurationBuilder<T,
        M extends InternalEncryptionMaterialsProvider<T>,
        B extends InternalDynamoDBEncryptionConfigurationBuilder,
        C extends InternalDynamoDBEncryptionConfiguration<T, M, B>> {
    B withSignatureFieldName(String signatureFieldName);

    B withMaterialDescriptionFieldName(String materialDescriptionFieldName);

    B withDescriptionBase(String descriptionBase);

    B withEncryptionContextOverrideOperator(Function<T, T> encryptionContextOverrideOperator);

    B withEncryptionMaterialsProvider(M encryptionMaterialsProvider);

    B withEncryptionContext(T encryptionContext);

    B withAttributeEncryptionActionOverrides(Map<String, AttributeEncryptionAction> encryptionActionOverrides);

    B withDefaultAttributeEncryptionAction(AttributeEncryptionAction defaultAttributeEncryptionAction);

    C build();
}
