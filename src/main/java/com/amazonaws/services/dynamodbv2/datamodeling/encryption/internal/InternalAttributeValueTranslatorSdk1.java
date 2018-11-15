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

import com.amazonaws.services.dynamodbv2.model.AttributeValue;

public class InternalAttributeValueTranslatorSdk1 implements InternalAttributeValueTranslator<AttributeValue> {
    public InternalAttributeValue convertFrom(AttributeValue attributeValue) {
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

    public AttributeValue convertFrom(InternalAttributeValue internalAttributeValue) {
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
}
