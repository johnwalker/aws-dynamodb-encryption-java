package com.amazonaws.services.dynamodbv2.datamodeling.encryption.sdk2;

import com.amazonaws.services.dynamodbv2.datamodeling.encryption.internal.InternalEncryptionContext;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class EncryptionContextSDK2 extends InternalEncryptionContext<AttributeValue, EncryptionContextSDK2.Builder> {

    EncryptionContextSDK2(Builder encryptionContextBuilder) {
        super(encryptionContextBuilder);
    }

    public static class Builder extends InternalEncryptionContext.Builder<AttributeValue, EncryptionContextSDK2, Builder> {
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
