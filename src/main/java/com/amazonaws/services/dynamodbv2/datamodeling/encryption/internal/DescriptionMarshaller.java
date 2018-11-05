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

public class DescriptionMarshaller {
    private static final Charset UTF8 = StandardCharsets.UTF_8;
    private static final int CURRENT_DESCRIPTION_MARSHALLER_VERSION = 0;
    final int descriptionMarshallerVersion;

    public DescriptionMarshaller() {
        this.descriptionMarshallerVersion = CURRENT_DESCRIPTION_MARSHALLER_VERSION;

    }

    public DescriptionMarshaller(int descriptionMarshallerVersion) {
        this.descriptionMarshallerVersion = descriptionMarshallerVersion;
    }

    /**
     * Marshalls the <code>description</code> into bytes by outputting
     * each key (modified UTF-8) followed by its value (also in modified UTF-8).
     *
     * @param description
     * @return the description encoded as an AttributeValue with a ByteBuffer value
     * @see java.io.DataOutput#writeUTF(String)
     */
    public byte[] marshallDescription(Map<String, String> description) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(bos);
            out.writeInt(descriptionMarshallerVersion);
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
     * @see #marshallDescription(Map)
     */
    public Map<String, String> unmarshallDescription(ByteBuffer byteBuffer) {
        byteBuffer.mark();
        try (DataInputStream in = new DataInputStream(
                new ByteBufferInputStream(byteBuffer)) ) {
            Map<String, String> result = new HashMap<String, String>();
            int version = in.readInt();
            if (version != this.descriptionMarshallerVersion) {
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

}
