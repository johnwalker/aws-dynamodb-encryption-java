package com.amazonaws.services.dynamodbv2.datamodeling.encryption.internal;
import com.amazonaws.services.dynamodbv2.datamodeling.internal.ByteBufferInputStream;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InternalAttributeValueMarshaller {

    private static final Charset UTF8 = Charset.forName("UTF-8");
    private static final int TRUE_FLAG = 1;
    private static final int FALSE_FLAG = 0;

    private InternalAttributeValueMarshaller() {
        // Prevent instantiation
    }

    /**
     * Marshalls the data using a TLV (Tag-Length-Value) encoding. The tag may be 'b', 'n', 's',
     * '?', '\0' to represent a ByteBuffer, Number, String, Boolean, or Null respectively. The tag
     * may also be capitalized (for 'b', 'n', and 's',) to represent an array of that type. If an
     * array is stored, then a four-byte big-endian integer is written representing the number of
     * array elements. If a ByteBuffer is stored, the length of the buffer is stored as a four-byte
     * big-endian integer and the buffer then copied directly. Both Numbers and Strings are treated
     * identically and are stored as UTF8 encoded Unicode, proceeded by the length of the encoded
     * string (in bytes) as a four-byte big-endian integer. Boolean is encoded as a single byte, 0
     * for <code>false</code> and 1 for <code>true</code> (and so has no Length parameter). The
     * Null tag ('\0') takes neither a Length nor a Value parameter.
     *
     * The tags 'L' and 'M' are for the document types List and Map respectively. These are encoded
     * recursively with the Length being the size of the collection. In the case of List, the value
     * is a Length number of marshalled AttributeValues. If the case of Map, the value is a Length
     * number of AttributeValue Pairs where the first must always have a String value.
     *
     * This implementation does <em>not</em> recognize loops. If an AttributeValue contains itself
     * (even indirectly) this code will recurse infinitely.
     *
     * @param attributeValue
     * @return the serialized InternalAttributeValue
     * @see java.io.DataInput
     */
    public static ByteBuffer marshall(final InternalAttributeValue attributeValue) {
        try (ByteArrayOutputStream resultBytes = new ByteArrayOutputStream();
             DataOutputStream out = new DataOutputStream(resultBytes);) {
            marshall(attributeValue, out);
            out.close();
            resultBytes.close();
            return ByteBuffer.wrap(resultBytes.toByteArray());
        } catch (final IOException ex) {
            // Due to the objects in use, an IOException is not possible.
            throw new RuntimeException("Unexpected exception", ex);
        }
    }

    private static void marshall(final InternalAttributeValue attributeValue, final DataOutputStream out)
            throws IOException {

        if (attributeValue.getB() != null) {
            out.writeChar('b');
            writeBytes(attributeValue.getB(), out);
        } else if (attributeValue.getBS() != null) {
            out.writeChar('B');
            writeBytesList(attributeValue.getBS(), out);
        } else if (attributeValue.getN() != null) {
            out.writeChar('n');
            writeString(trimZeros(attributeValue.getN()), out);
        } else if (attributeValue.getNS() != null) {
            out.writeChar('N');

            final List<String> ns = new ArrayList<String>(attributeValue.getNS().size());
            for (final String n : attributeValue.getNS()) {
                ns.add(trimZeros(n));
            }
            writeStringList(ns, out);
        } else if (attributeValue.getS() != null) {
            out.writeChar('s');
            writeString(attributeValue.getS(), out);
        } else if (attributeValue.getSS() != null) {
            out.writeChar('S');
            writeStringList(attributeValue.getSS(), out);
        } else if (attributeValue.getBOOL() != null) {
            out.writeChar('?');
            out.writeByte((attributeValue.getBOOL() ? TRUE_FLAG : FALSE_FLAG));
        } else if (Boolean.TRUE.equals(attributeValue.getNULL())) {
            out.writeChar('\0');
        } else if (attributeValue.getL() != null) {
            final List<InternalAttributeValue> l = attributeValue.getL();
            out.writeChar('L');
            out.writeInt(l.size());
            for (final InternalAttributeValue attr : l) {
                if (attr == null) {
                    throw new NullPointerException(
                            "Encountered null list entry value while marshalling attribute value "
                                    + attributeValue);
                }
                marshall(attr, out);
            }
        } else if (attributeValue.getM() != null) {
            final Map<String, InternalAttributeValue> m = attributeValue.getM();
            final List<String> mKeys = new ArrayList<String>(m.keySet());
            Collections.sort(mKeys);
            out.writeChar('M');
            out.writeInt(m.size());
            for (final String mKey : mKeys) {
                marshall(new InternalAttributeValue().withS(mKey), out);

                final InternalAttributeValue mValue = m.get(mKey);

                if (mValue == null) {
                    throw new NullPointerException(
                            "Encountered null map value for key "
                                    + mKey
                                    + " while marshalling attribute value "
                                    + attributeValue);
                }
                marshall(mValue, out);
            }
        } else {
            throw new IllegalArgumentException("A seemingly empty AttributeValue is indicative of invalid input or potential errors");
        }
    }

    /**
     * @see #marshall(InternalAttributeValue)
     */
    public static InternalAttributeValue unmarshall(final ByteBuffer plainText) {
        try (final DataInputStream in = new DataInputStream(
                new ByteBufferInputStream(plainText.asReadOnlyBuffer()))) {
            return unmarshall(in);
        } catch (IOException ex) {
            // Due to the objects in use, an IOException is not possible.
            throw new RuntimeException("Unexpected exception", ex);
        }
    }

    private static InternalAttributeValue unmarshall(final DataInputStream in) throws IOException {
        char type = in.readChar();
        InternalAttributeValue result = new InternalAttributeValue();
        switch (type) {
            case '\0':
                result.setNULL(Boolean.TRUE);
                break;
            case 'b':
                result.setB(readBytes(in));
                break;
            case 'B':
                result.setBS(readBytesList(in));
                break;
            case 'n':
                result.setN(readString(in));
                break;
            case 'N':
                result.setNS(readStringList(in));
                break;
            case 's':
                result.setS(readString(in));
                break;
            case 'S':
                result.setSS(readStringList(in));
                break;
            case '?':
                final byte boolValue = in.readByte();

                if (boolValue == TRUE_FLAG) {
                    result.setBOOL(Boolean.TRUE);
                } else if (boolValue == FALSE_FLAG) {
                    result.setBOOL(Boolean.FALSE);
                } else {
                    throw new IllegalArgumentException("Improperly formatted data");
                }
                break;
            case 'L':
                final int lCount = in.readInt();
                final List<InternalAttributeValue> l = new ArrayList<InternalAttributeValue>(lCount);
                for (int lIdx = 0; lIdx < lCount; lIdx++) {
                    l.add(unmarshall(in));
                }
                result.setL(l);
                break;
            case 'M':
                final int mCount = in.readInt();
                final Map<String, InternalAttributeValue> m = new HashMap<String, InternalAttributeValue>();
                for (int mIdx = 0; mIdx < mCount; mIdx++) {
                    final InternalAttributeValue key = unmarshall(in);
                    if (key.getS() == null) {
                        throw new IllegalArgumentException("Improperly formatted data");
                    }
                    InternalAttributeValue value = unmarshall(in);
                    m.put(key.getS(), value);
                }
                result.setM(m);
                break;
            default:
                throw new IllegalArgumentException("Unsupported data encoding");
        }

        return result;
    }

    private static String trimZeros(final String n) {
        BigDecimal number = new BigDecimal(n);
        if (number.compareTo(BigDecimal.ZERO) == 0) {
            return "0";
        }
        return number.stripTrailingZeros().toPlainString();
    }

    private static void writeStringList(List<String> values, final DataOutputStream out) throws IOException {
        final List<String> sorted = new ArrayList<String>(values);
        Collections.sort(sorted);
        out.writeInt(sorted.size());
        for (final String v : sorted) {
            writeString(v, out);
        }
    }

    private static List<String> readStringList(final DataInputStream in) throws IOException,
            IllegalArgumentException {
        final int nCount = in.readInt();
        List<String> ns = new ArrayList<String>(nCount);
        for (int nIdx = 0; nIdx < nCount; nIdx++) {
            ns.add(readString(in));
        }
        return ns;
    }

    private static void writeString(String value, final DataOutputStream out) throws IOException {
        final byte[] bytes = value.getBytes(UTF8);
        out.writeInt(bytes.length);
        out.write(bytes);
    }

    private static String readString(final DataInputStream in) throws IOException,
            IllegalArgumentException {
        byte[] bytes;
        int length;
        length = in.readInt();
        bytes = new byte[length];
        if(in.read(bytes) != length) {
            throw new IllegalArgumentException("Improperly formatted data");
        }
        String tmp = new String(bytes, UTF8);
        return tmp;
    }

    private static void writeBytesList(List<ByteBuffer> values, final DataOutputStream out) throws IOException {
        final List<ByteBuffer> sorted = new ArrayList<ByteBuffer>(values);
        Collections.sort(sorted);
        out.writeInt(sorted.size());
        for (final ByteBuffer v : sorted) {
            writeBytes(v, out);
        }
    }

    private static List<ByteBuffer> readBytesList(final DataInputStream in) throws IOException {
        final int bCount = in.readInt();
        List<ByteBuffer> bs = new ArrayList<ByteBuffer>(bCount);
        for (int bIdx = 0; bIdx < bCount; bIdx++) {
            bs.add(readBytes(in));
        }
        return bs;
    }

    private static void writeBytes(ByteBuffer value, final DataOutputStream out) throws IOException {
        value = value.asReadOnlyBuffer();
        value.rewind();
        out.writeInt(value.remaining());
        while (value.hasRemaining()) {
            out.writeByte(value.get());
        }
    }

    private static ByteBuffer readBytes(final DataInputStream in) throws IOException {
        final int length = in.readInt();
        final byte[] buf = new byte[length];
        in.readFully(buf);
        return ByteBuffer.wrap(buf);
    }
}
