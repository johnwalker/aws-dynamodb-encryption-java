package com.amazonaws.services.dynamodbv2.datamodeling.encryption.internal;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import java.util.*;

public class InternalAttributeValueTranslator
{
    public static InternalAttributeValue convertFrom(AttributeValue attributeValue) {
        if (attributeValue == null) {
            return null;
        }
        InternalAttributeValue internalAttributeValue = new InternalAttributeValue();
        internalAttributeValue.setB(attributeValue.getB());
        internalAttributeValue.setBOOL(attributeValue.getBOOL());
        internalAttributeValue.setBS(attributeValue.getBS());
        internalAttributeValue.setL(convertFrom(attributeValue.getL()));
        internalAttributeValue.setM(convertFrom(attributeValue.getM()));
        internalAttributeValue.setN(attributeValue.getN());
        internalAttributeValue.setNS(attributeValue.getNS());
        internalAttributeValue.setNULL(attributeValue.getNULL());
        internalAttributeValue.setS(attributeValue.getS());
        internalAttributeValue.setSS(attributeValue.getSS());
        return internalAttributeValue;
    }

    public static Collection<InternalAttributeValue> convertFrom(Collection<AttributeValue> attributeValues) {
        if (attributeValues == null) {
            return null;
        }
        Collection<InternalAttributeValue> internalAttributeValues = new ArrayList<>();
        for(AttributeValue attributeValue : attributeValues) {
            internalAttributeValues.add(convertFrom(attributeValue));
        }
        return internalAttributeValues;
    }

    public static Map<String, InternalAttributeValue> convertFrom(Map<String, AttributeValue> stringAttributeValueMap) {
        if (stringAttributeValueMap == null) {
            return null;
        }
        Map<String, InternalAttributeValue> internalAttributeValueMap = new HashMap<>();
        for(Map.Entry<String, AttributeValue> entry : stringAttributeValueMap.entrySet()) {
            internalAttributeValueMap.put(entry.getKey(), convertFrom(entry.getValue()));
        }
        return internalAttributeValueMap;
    }

    public static AttributeValue convertFrom(InternalAttributeValue internalAttributeValue) {
        if (internalAttributeValue == null) {
            return null;
        }
        AttributeValue attributeValue = new AttributeValue();
        attributeValue.setB(internalAttributeValue.getB());
        attributeValue.setBOOL(internalAttributeValue.getBOOL());
        attributeValue.setBS(internalAttributeValue.getBS());
        attributeValue.setL(convertFromInternal(internalAttributeValue.getL()));
        attributeValue.setM(convertFromInternal(internalAttributeValue.getM()));
        attributeValue.setN(internalAttributeValue.getN());
        attributeValue.setNS(internalAttributeValue.getNS());
        attributeValue.setNULL(internalAttributeValue.getNULL());
        attributeValue.setS(internalAttributeValue.getS());
        attributeValue.setSS(internalAttributeValue.getSS());
        return attributeValue;
    }

    public static Collection<AttributeValue> convertFromInternal(Collection<InternalAttributeValue> internalAttributeValues) {
        if (internalAttributeValues == null) {
            return null;
        }
        Collection<AttributeValue> attributeValues = new ArrayList<>();
        for(InternalAttributeValue internalAttributeValue : internalAttributeValues) {
            attributeValues.add(convertFrom(internalAttributeValue));
        }
        return attributeValues;
    }

    public static Map<String, AttributeValue> convertFromInternal(Map<String, InternalAttributeValue> stringInternalAttributeValueMap) {
        if (stringInternalAttributeValueMap == null) {
            return null;
        }
        Map<String, AttributeValue> stringAttributeValueMap = new HashMap<>();
        for(Map.Entry<String, InternalAttributeValue> entry : stringInternalAttributeValueMap.entrySet()) {
            stringAttributeValueMap.put(entry.getKey(), convertFrom(entry.getValue()));
        }
        return stringAttributeValueMap;
    }
}
