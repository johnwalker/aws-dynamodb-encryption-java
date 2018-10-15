package com.amazonaws.services.dynamodbv2.datamodeling.encryption.internal;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public interface InternalAttributeValueTranslator<T> {

    InternalAttributeValue convertFrom(T attributeValue);

    Collection<InternalAttributeValue> convertFrom(Collection<T> attributeValues);

    Map<String, InternalAttributeValue> convertFrom(Map<String, T> stringAttributeValueMap);

    T convertFrom(InternalAttributeValue internalAttributeValue);

    Collection<T> convertFromInternal(Collection<InternalAttributeValue> internalAttributeValues);

    Map<String, T> convertFromInternal(Map<String, InternalAttributeValue> stringInternalAttributeValueMap);
}
