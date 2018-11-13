package com.amazonaws.services.dynamodbv2.datamodeling.encryption;

public interface EncryptionTransformerContext<U> {
    U getEncryptionContext();
}
