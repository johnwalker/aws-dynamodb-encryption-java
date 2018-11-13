package com.amazonaws.services.dynamodbv2.datamodeling.encryption;

public interface EncryptionContextTransformer {
    <T extends EncryptionTransformerContext<EncryptionContext>> T transformEncryptionContext(T context);
}
