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

import com.amazonaws.services.dynamodbv2.datamodeling.internal.ByteBufferInputStream;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Internal API used for marshalling maps of material descriptions to bytes, and vice
 * versa. The version number of the format is included alongside the serialized material
 * descriptions, and validated on read.
 **/
public class DescriptionMarshaller {
    private static final Charset UTF8 = StandardCharsets.UTF_8;

    /**
     * The default version of the serialization format used for records.
     */
    private static final int DYNAMODB_ENCRYPTION_FORMAT_VERSION = 0;

    /**
     * The version of the serialization format used for the record. By default,
     * it is {@value DYNAMODB_ENCRYPTION_FORMAT_VERSION}.
     */
    final int dynamoDBEncryptionFormatVersion;

    public DescriptionMarshaller() {
        this(DYNAMODB_ENCRYPTION_FORMAT_VERSION);
    }

    public DescriptionMarshaller(int dynamoDBEncryptionFormatVersion) {
        this.dynamoDBEncryptionFormatVersion = dynamoDBEncryptionFormatVersion;
    }

    /**
     * Marshalls the <code>description</code> into bytes by outputting
     * each key followed by its value. Both the key and the value are
     * encoded in modified UTF-8.
     *
     * @param description the map to be serialized
     * @return the description encoded as bytes
     * @see java.io.DataInput modified UTF-8
     */
    public byte[] marshallDescription(Map<String, String> description) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(bos);
            out.writeInt(getDynamoDBEncryptionFormatVersion());
            for (Map.Entry<String, String> entry : description.entrySet()) {
                byte[] bytes = entry.getKey().getBytes(UTF8);
                out.writeInt(bytes.length);
                out.write(bytes);
                bytes = entry.getValue().getBytes(UTF8);
                out.writeInt(bytes.length);
                out.write(bytes);
            }
            out.close();
            return bos.toByteArray();
        } catch (IOException ex) {
            // Due to the objects in use, an IOException is not possible.
            throw new RuntimeException("Unexpected exception", ex);
        }
    }

    /**
     * Unmarshalls the <code>byteBuffer</code> into a description map
     *
     * @param byteBuffer the modified UTF-8 encoded bytes of a description
     * @return the map that was originally serialized through marshallDescription
     * @see #marshallDescription(Map)
     * @see java.io.DataInput modified UTF-8
     */
    public Map<String, String> unmarshallDescription(ByteBuffer byteBuffer) {
        byteBuffer.mark();
        try (DataInputStream in = new DataInputStream(
                new ByteBufferInputStream(byteBuffer)) ) {
            Map<String, String> result = new HashMap<String, String>();
            int version = in.readInt();
            if (version != getDynamoDBEncryptionFormatVersion()) {
                throw new IllegalArgumentException("Unsupported description version");
            }

            String key, value;
            int keyLength, valueLength;
            try {
                while(in.available() > 0) {
                    keyLength = in.readInt();
                    byte[] bytes = new byte[keyLength];
                    if (in.read(bytes) != keyLength) {
                        throw new IllegalArgumentException("Malformed description");
                    }
                    key = new String(bytes, UTF8);
                    valueLength = in.readInt();
                    bytes = new byte[valueLength];
                    if (in.read(bytes) != valueLength) {
                        throw new IllegalArgumentException("Malformed description");
                    }
                    value = new String(bytes, UTF8);
                    result.put(key, value);
                }
            } catch (EOFException eof) {
                throw new IllegalArgumentException("Malformed description", eof);
            }
            return result;
        } catch (IOException ex) {
            // Due to the objects in use, an IOException is not possible.
            throw new RuntimeException("Unexpected exception", ex);
        } finally {
            byteBuffer.reset();
        }
    }


    /**
     * @return {@link #dynamoDBEncryptionFormatVersion}
     */
    int getDynamoDBEncryptionFormatVersion() {
        return this.dynamoDBEncryptionFormatVersion;
    }
}
