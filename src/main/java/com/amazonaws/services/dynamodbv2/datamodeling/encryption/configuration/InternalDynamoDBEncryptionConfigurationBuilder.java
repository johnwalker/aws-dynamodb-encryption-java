package com.amazonaws.services.dynamodbv2.datamodeling.encryption.configuration;

import com.amazonaws.services.dynamodbv2.datamodeling.encryption.internal.InternalEncryptionMaterialsProvider;

import java.util.function.Function;

public interface InternalDynamoDBEncryptionConfigurationBuilder<T,
        M extends InternalEncryptionMaterialsProvider<T>,
        B extends InternalDynamoDBEncryptionConfigurationBuilder,
        C extends InternalDynamoDBEncryptionConfiguration<T, M, B>> {
    B withSignatureFieldName(String signatureFieldName);

    B withMaterialDescriptionFieldName(String materialDescriptionFieldName);

    B withDescriptionBase(String descriptionBase);

    B withEncryptionContextTransformer(Function<T, T> transformer);

    B withEncryptionMaterialsProvider(M encryptionMaterialsProvider);

    B withEncryptionContext(T encryptionContext);

    C build();
}
