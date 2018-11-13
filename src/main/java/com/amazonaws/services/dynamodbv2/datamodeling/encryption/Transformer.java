package com.amazonaws.services.dynamodbv2.datamodeling.encryption;

import com.amazonaws.services.dynamodbv2.datamodeling.encryption.sdk2.EncryptionContextBuilders;

public interface Transformer<T> {
     T transform(T context);
}
