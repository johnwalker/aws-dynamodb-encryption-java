package com.amazonaws.services.dynamodbv2.datamodeling.encryption.internal;

import java.nio.ByteBuffer;

public class InternalByteBufferUtils {
    public static byte[] toByteArray(ByteBuffer buffer) {
        buffer = buffer.duplicate();
        // We can only return the array directly if:
        // 1. The ByteBuffer exposes an array
        // 2. The ByteBuffer starts at the beginning of the array
        // 3. The ByteBuffer uses the entire array
        if (buffer.hasArray() && buffer.arrayOffset() == 0) {
            byte[] result = buffer.array();
            if (buffer.remaining() == result.length) {
                return result;
            }
        }

        byte[] result = new byte[buffer.remaining()];
        buffer.get(result);
        return result;
    }

}
