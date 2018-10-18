package com.amazonaws.services.dynamodbv2.datamodeling.encryption.internal;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.stream.Collectors;

public class InternalAttributeValueTranslatorSdk2 implements InternalAttributeValueTranslator<AttributeValue> {
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
        if(attributeValue.bs() != null) {
            List<ByteBuffer> byteBuffers = attributeValue.bs().stream()
                    .map((b) -> b.asByteBuffer()).collect(Collectors.toList());
            internalAttributeValue.setBS(byteBuffers);
        }
        internalAttributeValue.setL(convertFrom(attributeValue.l()));
        internalAttributeValue.setM(convertFrom(attributeValue.m()));
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
        List<SdkBytes> sdkBytesList;
        if (internalAttributeValue.getBS() != null) {
            sdkBytesList = internalAttributeValue.getBS().stream().map(SdkBytes::fromByteBuffer).collect(Collectors.toList());
        } else {
            sdkBytesList = null;
        }
        SdkBytes sdkBytes;
        ByteBuffer b = internalAttributeValue.getB();
        if (b != null) {
            sdkBytes = SdkBytes.fromByteBuffer(b);
        } else {
            sdkBytes = null;
        }
        return AttributeValue.builder()
                .b(sdkBytes)
                .bool(internalAttributeValue.getBOOL())
                .bs(sdkBytesList)
                .l(convertFromInternal(internalAttributeValue.getL()))
                .m(convertFromInternal(internalAttributeValue.getM()))
                .n(internalAttributeValue.getN())
                .ns(internalAttributeValue.getNS())
                .s(internalAttributeValue.getS())
                .ss(internalAttributeValue.getSS()).build();
    }
}
