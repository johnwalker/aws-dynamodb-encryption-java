package com.amazonaws.services.dynamodbv2.datamodeling.encryption;

import com.amazonaws.services.dynamodbv2.datamodeling.encryption.providers.SymmetricStaticProviderSdk2;
import com.amazonaws.services.dynamodbv2.datamodeling.internal.Utils;
import software.amazon.awssdk.core.SdkRequest;
import software.amazon.awssdk.core.interceptor.Context;
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

    public CryptoInterceptor() throws NoSuchAlgorithmException {

        KeyGenerator aesGen = KeyGenerator.getInstance("AES");
        aesGen.init(128, Utils.getRng());
        SecretKey encryptionKey = aesGen.generateKey();

        KeyGenerator macGen = KeyGenerator.getInstance("HmacSHA256");
        macGen.init(256, Utils.getRng());
        SecretKey macKey = macGen.generateKey();
        encryptorSdk2 = DynamoDBEncryptorSdk2.getInstance(new SymmetricStaticProviderSdk2(encryptionKey, macKey, Collections.emptyMap()));
    }


    @Override
    public SdkRequest modifyRequest(Context.ModifyRequest context, ExecutionAttributes executionAttributes) {
        SdkRequest request = context.request();
        if (request instanceof PutItemRequest) {
            PutItemRequest putRequest = (PutItemRequest) request;
            Map<String, Set<EncryptionFlags>> attributeEncryptionFlags = new HashMap<>();
            Set<EncryptionFlags> encryptionFlags = new HashSet<>();
            encryptionFlags.add(EncryptionFlags.ENCRYPT);

            Set<EncryptionFlags> signFlags = new HashSet<>();
            encryptionFlags.add(EncryptionFlags.SIGN);
            attributeEncryptionFlags.put("test2", signFlags);
            try {
                Map<String, AttributeValue> encryptedAttributes = encryptorSdk2.encryptRecord(putRequest.item(), attributeEncryptionFlags, new EncryptionContextSDK2.Builder().withAttributeValues(putRequest.item()).withTableName("huh").build());
                return putRequest.toBuilder().item(encryptedAttributes).build();
            } catch (GeneralSecurityException e) {
                throw new RuntimeException(e);
            }

        }
        return context.request();
    }
}
