package com.amazonaws.services.dynamodbv2.datamodeling.encryption;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class EncryptionContextSDK2 extends GenericEncryptionContext<AttributeValue> {

    EncryptionContextSDK2(Builder encryptionContextBuilder) {
        super(encryptionContextBuilder);
    }

    public static class Builder extends GenericEncryptionContext.Builder<AttributeValue, Builder> {
        public Builder() {
        }

        public Builder(EncryptionContextSDK2 encryptionContext) {
            super(encryptionContext);
        }

        public EncryptionContextSDK2 build() {
            return new EncryptionContextSDK2(this);
        }
    }
}
