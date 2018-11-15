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
package com.amazonaws.services.dynamodbv2.datamodeling.encryption.sdk2;

import com.amazonaws.services.dynamodbv2.datamodeling.encryption.EncryptionFlags;
import software.amazon.awssdk.core.SdkRequest;
import software.amazon.awssdk.core.SdkResponse;
import software.amazon.awssdk.core.interceptor.Context;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.interceptor.ExecutionInterceptor;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.security.GeneralSecurityException;
import java.util.*;

public class CryptoInterceptor implements ExecutionInterceptor {

    private final DynamoDBEncryptorSdk2 encryptorSdk2;

    public CryptoInterceptor(DynamoDBEncryptorSdk2 encryptorSdk2) {
        this.encryptorSdk2 = encryptorSdk2;
    }

    @Override
    public SdkRequest modifyRequest(Context.ModifyRequest context, ExecutionAttributes executionAttributes) {
        SdkRequest request = context.request();
        String tableName = null;
        if (request instanceof PutItemRequest) {
            // TODO: Pull EncryptionFlags via ExecutionAttributes. Right now, it is empty.
            Map<String, Set<EncryptionFlags>> attributeEncryptionFlags = new HashMap<>();
            PutItemRequest putRequest = (PutItemRequest) request;
            try {
                Map<String, AttributeValue> encryptedAttributes = encryptorSdk2.encryptRecord(
                        putRequest.item(),
                        attributeEncryptionFlags,
                        EncryptionContextSDK2.builder()
                                .withTableName(tableName)
                                .internalAPI()
                                .withAttributeValues(putRequest.item())
                                .publicAPI()
                                .build());
                return putRequest.toBuilder().item(encryptedAttributes).build();
            } catch (GeneralSecurityException e) {
                throw new RuntimeException(e);
            }
        }
        return context.request();
    }

    @Override
    public SdkResponse modifyResponse(Context.ModifyResponse context, ExecutionAttributes executionAttributes) {
        DynamoDbRequest request = (DynamoDbRequest) context.request();
        SdkResponse response = context.response();
        if (response instanceof GetItemResponse) {
            // TODO: Pull EncryptionFlags via ExecutionAttributes. Right now, it is empty.
            Map<String, Set<EncryptionFlags>> attributeEncryptionFlags = new HashMap<>();
            // TODO: Pull TableName via ExecutionAttributes
            GetItemResponse getItemResponse = (GetItemResponse) response;
            try {
                Map<String, AttributeValue> decryptedAttributes = encryptorSdk2.decryptRecord(
                        getItemResponse.item(),
                        attributeEncryptionFlags,
                        EncryptionContextSDK2.builder()
                                .internalAPI()
                                .withAttributeValues(getItemResponse.item())
                                .publicAPI().build());
                return getItemResponse.toBuilder().item(decryptedAttributes).build();
            } catch (GeneralSecurityException e) {
                throw new RuntimeException(e);
            }

        }
        return context.response();
    }

    private String getTableName(SdkRequest sdkRequest) {
        if (sdkRequest instanceof GetItemRequest) {
            return ((GetItemRequest) sdkRequest).tableName();
        }
        if (sdkRequest instanceof PutItemRequest) {
            return ((PutItemRequest) sdkRequest).tableName();
        }
        return null;
    }
}
