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
