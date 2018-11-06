package com.amazonaws.services.dynamodbv2.datamodeling.encryption.internal;

import org.junit.Before;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class DescriptionMarshallerTest {

   byte[] expectedBytes;
    Map<String, String> expectedDescription;

    @Before
    public void setup() {
        expectedDescription = new HashMap<>();
        expectedDescription.put("Steve walks warily down the street", "with the brim pulled way down low");
        expectedDescription.put("Ain't no sound but the sound of his feet", "machine guns ready to go");
        expectedBytes = Base64.getDecoder().decode("AAAAAAAAAChBaW4ndCBubyBzb3VuZCBidXQgdGh"
                + "lIHNvdW5kIG9mIGhpcyBmZWV0AAAAGG1hY2hpbmUgZ3Vucy"
                + "ByZWFkeSB0byBnbwAAACJTdGV2ZSB3YWxrcyB3YXJpbHkgZG93biB0aGUgc3RyZWV0AAAAIXdpdGggdGhlIGJy"
                + "aW0gcHVsbGVkIHdheSBkb3duIGxvdw");
    }

    @Test
    public void testRoundTrip() {
        DescriptionMarshaller descriptionMarshaller = new DescriptionMarshaller();
        byte[] marshalledBytes = descriptionMarshaller.marshallDescription(expectedDescription);

        Map<String, String> unmarshalledDescription = descriptionMarshaller.unmarshallDescription(ByteBuffer.wrap(marshalledBytes));

        assertEquals(expectedDescription, unmarshalledDescription);
        assertArrayEquals(expectedBytes, marshalledBytes);
    }

    /**
     * This asserts that the current version of the DynamoDB encryption format is 0. It's intentionally hardcoded to
     * catch accidental changes to the version.
     */
    @Test
    public void testDefaultVersion() {
        DescriptionMarshaller descriptionMarshaller = new DescriptionMarshaller();
        assertEquals(descriptionMarshaller.getDynamoDBEncryptionFormatVersion(), 0);
    }

    @Test
    public void testCustomVersion() {
        DescriptionMarshaller descriptionMarshaller = new DescriptionMarshaller(9999);
        assertEquals(descriptionMarshaller.getDynamoDBEncryptionFormatVersion(), 9999);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeserializeDifferentVersion() {
        DescriptionMarshaller descriptionMarshaller = new DescriptionMarshaller(9999);
        assertEquals(descriptionMarshaller.getDynamoDBEncryptionFormatVersion(), 9999);
        descriptionMarshaller.unmarshallDescription(ByteBuffer.wrap(expectedBytes));
    }

}