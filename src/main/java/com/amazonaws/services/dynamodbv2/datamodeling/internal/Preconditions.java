package com.amazonaws.services.dynamodbv2.datamodeling.internal;

public class Preconditions {

    private Preconditions() {}

    public static <T> void assertNotNull(T object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }
}
