package com.amazonaws.services.dynamodbv2.datamodeling.encryption.internal;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.junit.Test;
import org.junit.experimental.theories.suppliers.TestedOn;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class InternalAttributeTranslatorTest {
    private Boolean aBoolean = Boolean.TRUE;
    private String aString = "S";
    private String aString2 = "S2";
    private ByteBuffer aByteBuffer;
    private List<String> aStringSet;
    private List<String> aNumberSet;
    private List<ByteBuffer> aBinarySet;
    private Map<String, InternalAttributeValue> m;
    private List<InternalAttributeValue> l;

    // TODO: wow, so lazy.
    @Test
    public void testRoundTrip() {
        InternalAttributeValue internalAttributeValue = new InternalAttributeValue();
        internalAttributeValue.setBOOL(aBoolean);
        internalAttributeValue.setS(aString);
        internalAttributeValue.setN(aString2);
        internalAttributeValue.setNULL(aBoolean);
        internalAttributeValue.setNS(aNumberSet);
        internalAttributeValue.setSS(aStringSet);
        internalAttributeValue.setBS(aBinarySet);
        internalAttributeValue.setM(m);
        internalAttributeValue.setL(l);

        AttributeValue attributeValue = InternalAttributeTranslator.convertFrom(internalAttributeValue);

        assertEquals(aBoolean, attributeValue.getBOOL());
        assertEquals(aString, attributeValue.getS());
        assertEquals(aString2, attributeValue.getN());
        assertEquals(aBoolean, attributeValue.isNULL());
        assertEquals(aNumberSet, attributeValue.getNS());
        assertEquals(aStringSet, attributeValue.getSS());
        assertEquals(aBinarySet, attributeValue.getBS());
    }

}