package com.amazonaws.services.dynamodbv2.datamodeling.encryption;

import java.util.function.UnaryOperator;

public interface Transformer<T> extends UnaryOperator<T> {
    public T transform(T encryptionContext);
}
