package com.amazonaws.services.dynamodbv2.datamodeling.encryption.internal;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class InternalAttributeValueTranslatorTest {


    // TODO: wow, so lazy.
    @Test
    public void testTripFromInternalToSdk1AttributeValue() {
        InternalAttributeValueTranslatorSdk1 translatorSdk1 = new InternalAttributeValueTranslatorSdk1();

        Boolean aBoolean = Boolean.TRUE;
        String aString = "S";
        String aString2 = "S2";
        byte[] bytes1 = "suchbytes".getBytes(StandardCharsets.UTF_8);
        ByteBuffer aByteBuffer = ByteBuffer.wrap(bytes1);
        List<String> aStringSet = new ArrayList<>();
        List<String> aNumberSet = new ArrayList<>();
        List<ByteBuffer> aBinarySet = new ArrayList<>();
        Map<String, InternalAttributeValue> m = new HashMap<>();
        List<InternalAttributeValue> l = new ArrayList<>();

        InternalAttributeValue internalAttributeValue = new InternalAttributeValue();
        internalAttributeValue.setBOOL(aBoolean);
        internalAttributeValue.setB(aByteBuffer);
        internalAttributeValue.setS(aString);
        internalAttributeValue.setN(aString2);
        internalAttributeValue.setNULL(aBoolean);
        internalAttributeValue.setNS(aNumberSet);
        internalAttributeValue.setSS(aStringSet);
        internalAttributeValue.setBS(aBinarySet);
        internalAttributeValue.setM(m);
        internalAttributeValue.setL(l);

        AttributeValue attributeValue = translatorSdk1.convertFrom(internalAttributeValue);

        assertEquals(aBoolean, attributeValue.getBOOL());
        assertEquals(aString, attributeValue.getS());
        assertEquals(aString2, attributeValue.getN());
        assertEquals(aBoolean, attributeValue.isNULL());
        assertEquals(aNumberSet, attributeValue.getNS());
        assertEquals(aStringSet, attributeValue.getSS());
        assertEquals(aBinarySet, attributeValue.getBS());
        assertEquals(aByteBuffer, attributeValue.getB());
    }

    @Test
    public void testTripFromSdk1ToInternalAttributeValue() {
        InternalAttributeValueTranslatorSdk1 translatorSdk1 = new InternalAttributeValueTranslatorSdk1();

        Boolean aBoolean = Boolean.TRUE;
        String aString = "S";
        String aString2 = "S2";
        byte[] bytes1 = "suchbytes".getBytes(StandardCharsets.UTF_8);
        ByteBuffer aByteBuffer = ByteBuffer.wrap(bytes1);
        List<String> aStringSet = new ArrayList<>();
        List<String> aNumberSet = new ArrayList<>();
        List<ByteBuffer> aBinarySet = new ArrayList<>();
        Map<String, AttributeValue> m = new HashMap<>();
        List<AttributeValue> l = new ArrayList<>();

        AttributeValue attributeValue = new AttributeValue();
        attributeValue.setBOOL(aBoolean);
        attributeValue.setB(aByteBuffer);
        attributeValue.setS(aString);
        attributeValue.setN(aString2);
        attributeValue.setNULL(aBoolean);
        attributeValue.setNS(aNumberSet);
        attributeValue.setSS(aStringSet);
        attributeValue.setBS(aBinarySet);
        attributeValue.setM(m);
        attributeValue.setL(l);

        InternalAttributeValue internalAttributeValue = translatorSdk1.convertFrom(attributeValue);

        assertEquals(aBoolean, internalAttributeValue.getBOOL());
        assertEquals(aString, internalAttributeValue.getS());
        assertEquals(aString2, internalAttributeValue.getN());
        assertEquals(aBoolean, internalAttributeValue.isNULL());
        assertEquals(aNumberSet, internalAttributeValue.getNS());
        assertEquals(aStringSet, internalAttributeValue.getSS());
        assertEquals(aBinarySet, internalAttributeValue.getBS());
        assertEquals(aByteBuffer, internalAttributeValue.getB());
    }

}