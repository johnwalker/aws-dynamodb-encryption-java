package com.amazonaws.services.dynamodbv2.datamodeling.encryption;

import com.amazonaws.services.dynamodbv2.datamodeling.encryption.providers.EncryptionMaterialsProviderSdk2;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.providers.SymmetricStaticProviderSdk2;
import com.amazonaws.services.dynamodbv2.datamodeling.internal.Utils;
import software.amazon.awssdk.core.SdkRequest;
import software.amazon.awssdk.core.interceptor.Context;
import software.amazon.awssdk.core.interceptor.ExecutionAttribute;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.interceptor.ExecutionInterceptor;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class CryptoInterceptor implements ExecutionInterceptor {

    private final DynamoDBEncryptorSdk2 encryptorSdk2;

    public CryptoInterceptor(DynamoDBEncryptorSdk2 encryptorSdk2) {
        this.encryptorSdk2 = encryptorSdk2;
    }

    @Override
    public SdkRequest modifyRequest(Context.ModifyRequest context, ExecutionAttributes executionAttributes) {
        SdkRequest request = context.request();
        if (request instanceof PutItemRequest) {
            // TODO: Pull EncryptionFlags via ExecutionAttributes. Right now, it is empty.
            Map<String, Set<EncryptionFlags>> attributeEncryptionFlags = new HashMap<>();
            // TODO: Pull TableName via ExecutionAttributes
            PutItemRequest putRequest = (PutItemRequest) request;
            try {
                Map<String, AttributeValue> encryptedAttributes = encryptorSdk2.encryptRecord(
                        putRequest.item(),
                        attributeEncryptionFlags,
                        new EncryptionContextSDK2.Builder()
                                .withAttributeValues(putRequest.item())
                                .withTableName(putRequest.tableName())
                                .build());
                return putRequest.toBuilder().item(encryptedAttributes).build();
            } catch (GeneralSecurityException e) {
                throw new RuntimeException(e);
            }

        }
        return context.request();
    }
}
