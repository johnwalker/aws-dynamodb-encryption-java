package com.amazonaws.services.dynamodbv2.datamodeling.encryption.internal;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public interface InternalAttributeValueTranslator<T> {

    InternalAttributeValue convertFrom(T attributeValue);
    T convertFrom(InternalAttributeValue internalAttributeValue);

    default Collection<InternalAttributeValue> convertFrom(Collection<T> attributeValues) {
        if (attributeValues == null) {
            return null;
        }
        Collection<InternalAttributeValue> internalAttributeValues = new ArrayList<>();
        for(T attributeValue : attributeValues) {
            internalAttributeValues.add(convertFrom(attributeValue));
        }
        return internalAttributeValues;
    }

    default Map<String, InternalAttributeValue> convertFrom(Map<String, T> stringAttributeValueMap) {
        if (stringAttributeValueMap == null) {
            return null;
        }
        Map<String, InternalAttributeValue> internalAttributeValueMap = new HashMap<>();
        for(Map.Entry<String, T> entry : stringAttributeValueMap.entrySet()) {
            internalAttributeValueMap.put(entry.getKey(), convertFrom(entry.getValue()));
        }
        return internalAttributeValueMap;
    }

    default Collection<T> convertFromInternal(Collection<InternalAttributeValue> internalAttributeValues) {
        if (internalAttributeValues == null) {
            return null;
        }
        Collection<T> attributeValues = new ArrayList<>();
        for(InternalAttributeValue internalAttributeValue : internalAttributeValues) {
            attributeValues.add(convertFrom(internalAttributeValue));
        }
        return attributeValues;
    }

    default Map<String, T> convertFromInternal(Map<String, InternalAttributeValue> stringInternalAttributeValueMap) {
        if (stringInternalAttributeValueMap == null) {
            return null;
        }
        Map<String, T> stringAttributeValueMap = new HashMap<>();
        for(Map.Entry<String, InternalAttributeValue> entry : stringInternalAttributeValueMap.entrySet()) {
            stringAttributeValueMap.put(entry.getKey(), convertFrom(entry.getValue()));
        }
        return stringAttributeValueMap;
    }
}
