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

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.util.DefaultSdkAutoConstructList;
import software.amazon.awssdk.core.util.DefaultSdkAutoConstructMap;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InternalAttributeValueTranslatorSdk2 implements InternalAttributeValueTranslator<AttributeValue> {
    private static final DefaultSdkAutoConstructList defaultListInstance = DefaultSdkAutoConstructList.getInstance();
    private static final Map defaultMapInstance = DefaultSdkAutoConstructMap.getInstance();

    @Override
    public InternalAttributeValue convertFrom(AttributeValue attributeValue) {
        if (attributeValue == null) {
            return null;
        }
        InternalAttributeValue internalAttributeValue = new InternalAttributeValue();
        if(attributeValue.b() != null) {
            internalAttributeValue.setB(attributeValue.b().asByteBuffer());
        }
        internalAttributeValue.setBOOL(attributeValue.bool());

        if(defaultListInstance.equals(attributeValue.bs())) {
            internalAttributeValue.setBS(defaultListInstance);
        } else if (attributeValue.bs() != null) {
            List<ByteBuffer> byteBuffers = attributeValue.bs().stream()
                    .map((b) -> b.asByteBuffer()).collect(Collectors.toList());
            internalAttributeValue.setBS(byteBuffers);
        }

        if(defaultListInstance.equals(attributeValue.l())) {
            internalAttributeValue.setL(defaultListInstance);
        } else if (attributeValue.l() != null) {
            internalAttributeValue.setL(convertFrom(attributeValue.l()));
        }

        if(defaultMapInstance.equals(attributeValue.m())) {
            internalAttributeValue.setM(defaultMapInstance);
        } else if (attributeValue.m() != null) {
            internalAttributeValue.setM(convertFrom(attributeValue.m()));
        }

        internalAttributeValue.setN(attributeValue.n());
        internalAttributeValue.setNS(attributeValue.ns());
        internalAttributeValue.setNULL(attributeValue.nul());
        internalAttributeValue.setS(attributeValue.s());
        internalAttributeValue.setSS(attributeValue.ss());
        return internalAttributeValue;
    }

    @Override
    public AttributeValue convertFrom(InternalAttributeValue internalAttributeValue) {
        if (internalAttributeValue == null) {
            return null;
        }

        List<ByteBuffer> bs = internalAttributeValue.getBS();
        AttributeValue.Builder builder = AttributeValue.builder();

        if (defaultListInstance.equals(bs)) {
            builder.bs(defaultListInstance);
        } else if (bs == null) {
            builder.bs((List)null);
        } else {
            builder.bs(bs.stream().map(SdkBytes::fromByteBuffer).collect(Collectors.toList()));
        }

        Map<String, InternalAttributeValue> m = internalAttributeValue.getM();
        if (defaultMapInstance.equals(m)) {
            builder.m(defaultMapInstance);
        } else if (m == null) {
            builder.m((Map)null);
        } else {
            builder.m(convertFromInternal(m));
        }

        List<InternalAttributeValue> l = internalAttributeValue.getL();
        if(defaultListInstance.equals(l)) {
            builder.l(defaultListInstance);
        } else if (l == null) {
            builder.l((List)null);
        } else {
            builder.l(convertFromInternal(l));
        }

        ByteBuffer b = internalAttributeValue.getB();
        if (b != null) {
            builder.b(SdkBytes.fromByteBuffer(b));
        }
        // DONT COMMIT THIS CODE PLEASE I BEG YOU, it is missing .ss, .ns, .m, .l at the least...
        return builder
                .bool(internalAttributeValue.getBOOL())
                .n(internalAttributeValue.getN())
                .ns(internalAttributeValue.getNS())
                .s(internalAttributeValue.getS())
                .ss(internalAttributeValue.getS())
                .build();
    }
}
