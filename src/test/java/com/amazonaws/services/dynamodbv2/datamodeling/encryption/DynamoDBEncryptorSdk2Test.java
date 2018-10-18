package com.amazonaws.services.dynamodbv2.datamodeling.encryption;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.SignatureException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import com.amazonaws.services.dynamodbv2.datamodeling.encryption.internal.InternalByteBufferUtils;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.providers.EncryptionMaterialsProviderSdk2;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.providers.SymmetricStaticProviderSdk2;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.amazonaws.services.dynamodbv2.datamodeling.encryption.materials.DecryptionMaterials;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.materials.EncryptionMaterials;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.providers.EncryptionMaterialsProvider;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.providers.SymmetricStaticProvider;
import com.amazonaws.services.dynamodbv2.datamodeling.internal.Utils;
import com.amazonaws.services.dynamodbv2.testing.AttrMatcher;
import org.junit.Before;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import static org.junit.Assert.*;

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
        encryptor = DynamoDBEncryptorSdk2.getInstance(prov, "encryptor-");

        attribs = new HashMap<String, AttributeValue>();
        attribs.put("intValue", AttributeValue.builder().n("123").build());
        attribs.put("stringValue", AttributeValue.builder().s("Hello world!").build());
        attribs.put("byteArrayValue", AttributeValue.builder().b(SdkBytes.fromByteArray(new byte[] {0, 1, 2, 3, 4, 5})).build());
        attribs.put("stringSet", AttributeValue.builder().ss("Goodbye", "Cruel", "World", "?").build());
        attribs.put("intSet", AttributeValue.builder().ns("1", "200", "10", "15", "0").build());
        attribs.put("hashKey", AttributeValue.builder().n("5").build());
        attribs.put("rangeKey", AttributeValue.builder().n("7").build());
        attribs.put("version", AttributeValue.builder().n("0").build());

        context = new EncryptionContextSDK2.Builder()
                .withTableName("TableName")
                .withHashKeyName("hashKey")
                .withRangeKeyName("rangeKey")
                .build();
    }

    @Test
    public void fullEncryption() throws GeneralSecurityException {
        Map<String, AttributeValue> encryptedAttributes =
                encryptor.encryptAllFieldsExcept(Collections.unmodifiableMap(attribs), context, "hashKey", "rangeKey", "version");
        // assertThat(encryptedAttributes, AttrMatcher.invert(attribs));

        Map<String, AttributeValue> decryptedAttributes =
                encryptor.decryptAllFieldsExcept(Collections.unmodifiableMap(encryptedAttributes), context, "hashKey", "rangeKey", "version");
        // assertThat(decryptedAttributes, AttrMatcher.match(attribs));

        // Make sure keys and version are not encrypted
        assertAttrEquals(attribs.get("hashKey"), encryptedAttributes.get("hashKey"));
        assertAttrEquals(attribs.get("rangeKey"), encryptedAttributes.get("rangeKey"));
        assertAttrEquals(attribs.get("version"), encryptedAttributes.get("version"));

        // Make sure String has been encrypted (we'll assume the others are correct as well)
        assertTrue(encryptedAttributes.containsKey("stringValue"));
        assertNull(encryptedAttributes.get("stringValue").s());
        assertNotNull(encryptedAttributes.get("stringValue").b());

        // Make sure we're calling the proper getEncryptionMaterials method
        assertEquals("Wrong getEncryptionMaterials() called",
                1, prov.getCallCount("getEncryptionMaterials(EncryptionContext context)"));
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
    public void testSetSignatureFieldName() {
        assertNotNull(encryptor.getSignatureFieldName());
        encryptor.setSignatureFieldName("A different value");
        assertEquals("A different value", encryptor.getSignatureFieldName());
    }
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