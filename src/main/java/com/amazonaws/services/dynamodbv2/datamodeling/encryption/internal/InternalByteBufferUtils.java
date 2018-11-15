/*
 * Copyright 2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
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
