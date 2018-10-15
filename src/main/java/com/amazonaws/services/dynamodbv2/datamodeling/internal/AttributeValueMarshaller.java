/*
 * Copyright 2014 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
package com.amazonaws.services.dynamodbv2.datamodeling.internal;

import java.nio.ByteBuffer;

import com.amazonaws.services.dynamodbv2.datamodeling.encryption.internal.InternalAttributeValueTranslator;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.internal.InternalAttributeValue;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.internal.InternalAttributeValueMarshaller;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.internal.InternalAttributeValueTranslatorSdk1;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

/**
 * @author Greg Rubin 
 */
public class AttributeValueMarshaller {
    static InternalAttributeValueTranslatorSdk1 translatorSdk1 = new InternalAttributeValueTranslatorSdk1();

    private AttributeValueMarshaller() {
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
     * @return the serialized AttributeValue
     * @see java.io.DataInput
     */
    public static ByteBuffer marshall(final AttributeValue attributeValue) {
        InternalAttributeValue internalAttributeValue = translatorSdk1.convertFrom(attributeValue);
        return InternalAttributeValueMarshaller.marshall(internalAttributeValue);
    }

    /**
     * @see #marshall(AttributeValue)
     */
    public static AttributeValue unmarshall(final ByteBuffer plainText) {
        InternalAttributeValue internalAttributeValue = InternalAttributeValueMarshaller.unmarshall(plainText);
        return translatorSdk1.convertFrom(internalAttributeValue);
    }

}
