package com.amazonaws.services.dynamodbv2.datamodeling.encryption;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import com.amazonaws.services.dynamodbv2.datamodeling.encryption.sdk2.EncryptionContextBuilders;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.sdk2.providers.EncryptionMaterialsProviderSdk2;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.sdk2.providers.SymmetricStaticProviderSdk2;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.sdk2.CryptoInterceptor;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.sdk2.DynamoDBEncryptorSdk2;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.sdk2.EncryptionContextSDK2;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.amazonaws.services.dynamodbv2.datamodeling.encryption.materials.DecryptionMaterials;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.materials.EncryptionMaterials;
import com.amazonaws.services.dynamodbv2.datamodeling.internal.Utils;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;

public class DynamoDBEncryptorSdk2Test {

    private static SecretKey encryptionKey;
    private static SecretKey macKey;

    private InstrumentedEncryptionMaterialsProvider prov;
    private DynamoDBEncryptorSdk2 encryptor;
    private Map<String, AttributeValue> attribs;
    private EncryptionContextSDK2 context;

    @BeforeClass
    public static void setUpClass() throws Exception {
        KeyGenerator aesGen = KeyGenerator.getInstance("AES");
        aesGen.init(128, Utils.getRng());
        encryptionKey = aesGen.generateKey();

        KeyGenerator macGen = KeyGenerator.getInstance("HmacSHA256");
        macGen.init(256, Utils.getRng());
        macKey = macGen.generateKey();
    }

    @Before
    public void setUp() throws Exception {
        prov = new InstrumentedEncryptionMaterialsProvider(
                new SymmetricStaticProviderSdk2(encryptionKey, macKey,
                        Collections.<String, String> emptyMap()));
        DynamoDBEncryptionConfiguration encryptionConfiguration = new DefaultDynamoDBEncryptionConfiguration();
        encryptionConfiguration.setDescriptionBase("encryptor-");

        encryptor = new DynamoDBEncryptorSdk2(prov,encryptionConfiguration);

        attribs = new HashMap<String, AttributeValue>();
        attribs.put("intValue", AttributeValue.builder().n("123").build());
        attribs.put("stringValue", AttributeValue.builder().s("Hello world!").build());
        attribs.put("byteArrayValue", AttributeValue.builder().b(SdkBytes.fromByteArray(new byte[] {0, 1, 2, 3, 4, 5})).build());
        attribs.put("stringSet", AttributeValue.builder().ss("Goodbye", "Cruel", "World", "?").build());
        attribs.put("intSet", AttributeValue.builder().ns("1", "200", "10", "15", "0").build());
        attribs.put("hashKey", AttributeValue.builder().n("5").build());
        attribs.put("rangeKey", AttributeValue.builder().n("7").build());
        attribs.put("version", AttributeValue.builder().n("0").build());

        context = EncryptionContextSDK2.builder()
                .withTableName("TableName")
                .withHashKeyName("hashKey")
                .withRangeKeyName("rangeKey")
                .build();
    }

    private void assertAttrEquals(AttributeValue o1, AttributeValue o2) {
        Assert.assertEquals(o1.b(), o2.b());
        assertSetsEqual(o1.bs(), o2.bs());
        Assert.assertEquals(o1.n(), o2.n());
        assertSetsEqual(o1.ns(), o2.ns());
        Assert.assertEquals(o1.s(), o2.s());
        assertSetsEqual(o1.ss(), o2.ss());
    }

    private <T> void assertSetsEqual(Collection<T> c1, Collection<T> c2) {
        Assert.assertFalse(c1 == null ^ c2 == null);
        if (c1 != null) {
            Set<T> s1 = new HashSet<T>(c1);
            Set<T> s2 = new HashSet<T>(c2);
            Assert.assertEquals(s1, s2);
        }
    }

    @Test
    public void testThatCrazyDynamoStuffEXCLAMATIONMARK() throws ExecutionException, InterruptedException {
        KeyGenerator aesGen = null;
        try {
            aesGen = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        aesGen.init(128, Utils.getRng());
        SecretKey encryptionKey = aesGen.generateKey();

        KeyGenerator macGen = null;
        try {
            macGen = KeyGenerator.getInstance("HmacSHA256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        macGen.init(256, Utils.getRng());
        SecretKey macKey = macGen.generateKey();

        DynamoDBEncryptorSdk2 encryptorSdk2 = new DynamoDBEncryptorSdk2(new SymmetricStaticProviderSdk2(encryptionKey, macKey, Collections.emptyMap()));

        DynamoDbAsyncClient client = DynamoDbAsyncClient.builder()
                .overrideConfiguration(ClientOverrideConfiguration.builder()
                        .addExecutionInterceptor(new CryptoInterceptor(encryptorSdk2))
                        .build())
                .build();

        Map<String, AttributeValue> item = new HashMap<>();
        item.put("hk", AttributeValue.builder().s("wuw").build());
        item.put("sk", AttributeValue.builder().s("such").build());
        item.put("test1", AttributeValue.builder().s("WOT").build());
        item.put("test2", AttributeValue.builder().s("WOW!!!").build());
        item.put("test3", AttributeValue.builder().s("INVISIBLE!!!").build());
        PutItemRequest putItemRequest = PutItemRequest.builder().item(item).tableName("Channels").build();
        CompletableFuture<PutItemResponse> putItemResponseCompletableFuture = client.putItem(putItemRequest);
        PutItemResponse putItemResponse = putItemResponseCompletableFuture.get();

    }

//    @Test
//    public void testSetSignatureFieldName() {
//        assertNotNull(encryptor.getSignatureFieldName());
//        encryptor.setSignatureFieldName("A different value");
//        assertEquals("A different value", encryptor.getSignatureFieldName());
//    }
    private static final class InstrumentedEncryptionMaterialsProvider implements EncryptionMaterialsProviderSdk2 {
        private final EncryptionMaterialsProviderSdk2 delegate;
        private final ConcurrentHashMap<String, AtomicInteger> calls = new ConcurrentHashMap<>();

        public InstrumentedEncryptionMaterialsProvider(EncryptionMaterialsProviderSdk2 delegate) {
            this.delegate = delegate;
        }

        @Override
        public DecryptionMaterials getDecryptionMaterials(EncryptionContextSDK2 context) {
            incrementMethodCount("getDecryptionMaterials()");
            return delegate.getDecryptionMaterials(context);
        }

        @Override
        public EncryptionMaterials getEncryptionMaterials(EncryptionContextSDK2 context) {
            incrementMethodCount("getEncryptionMaterials(EncryptionContext context)");
            return delegate.getEncryptionMaterials(context);
        }

        @Override
        public void refresh() {
            incrementMethodCount("refresh()");
            delegate.refresh();
        }

        public int getCallCount(String method) {
            AtomicInteger count = calls.get(method);
            if (count != null) {
                return count.intValue();
            } else {
                return 0;
            }
        }

        @SuppressWarnings("unused")
        public void resetCallCounts() {
            calls.clear();
        }

        private void incrementMethodCount(String method) {
            AtomicInteger oldValue = calls.putIfAbsent(method, new AtomicInteger(1));
            if (oldValue != null) {
                oldValue.incrementAndGet();
            }
        }
    }

}