/*
 * Copyright 2014 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
package com.amazonaws.services.dynamodbv2.datamodeling.encryption;

import com.amazonaws.services.dynamodbv2.datamodeling.encryption.internal.InternalAttributeValue;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.internal.InternalAttributeValueMarshaller;
import com.amazonaws.services.dynamodbv2.datamodeling.internal.Utils;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Greg Rubin 
 */
// NOTE: This class must remain thread-safe.
class GenericDynamoDBSigner {
    private static final ConcurrentHashMap<String, GenericDynamoDBSigner> cache =
            new ConcurrentHashMap<String, GenericDynamoDBSigner>();

    protected static final Charset UTF8 = Charset.forName("UTF-8");
    private final SecureRandom rnd;
    private final SecretKey hmacComparisonKey;
    private final String signingAlgorithm;

    /**
     * @param signingAlgorithm
     *            is the algorithm used for asymmetric signing (ex:
     *            SHA256withRSA). This is ignored for symmetric HMACs as that
     *            algorithm is fully specified by the key.
     */
    static GenericDynamoDBSigner getInstance(String signingAlgorithm, SecureRandom rnd) {
        GenericDynamoDBSigner result = cache.get(signingAlgorithm);
        if (result == null) {
            result = new GenericDynamoDBSigner(signingAlgorithm, rnd);
            cache.putIfAbsent(signingAlgorithm, result);
        }
        return result;
    }

    /**
     * @param signingAlgorithm
     *            is the algorithm used for asymmetric signing (ex:
     *            SHA256withRSA). This is ignored for symmetric HMACs as that
     *            algorithm is fully specified by the key.
     */
    private GenericDynamoDBSigner(String signingAlgorithm, SecureRandom rnd) {
        if (rnd == null) {
            rnd = Utils.getRng();
        }
        this.rnd = rnd;
        this.signingAlgorithm = signingAlgorithm;
        // Shorter than the output of SHA256 to avoid weak keys.
        // http://cs.nyu.edu/~dodis/ps/h-of-h.pdf
        // http://link.springer.com/chapter/10.1007%2F978-3-642-32009-5_21
        byte[] tmpKey = new byte[31];
        rnd.nextBytes(tmpKey);
        hmacComparisonKey = new SecretKeySpec(tmpKey, "HmacSHA256");
    }

    void verifySignature(Map<String, InternalAttributeValue> itemAttributes, Map<String, Set<EncryptionFlags>> attributeFlags,
                         byte[] associatedData, Key verificationKey, ByteBuffer signature) throws GeneralSecurityException {
        if (verificationKey instanceof DelegatedKey) {
            DelegatedKey dKey = (DelegatedKey)verificationKey;
            byte[] stringToSign = calculateStringToSign(itemAttributes, attributeFlags, associatedData);
            if (!dKey.verify(stringToSign, toByteArray(signature), dKey.getAlgorithm())) {
                throw new SignatureException("Bad signature");
            }
        } else if (verificationKey instanceof SecretKey) {
            byte[] calculatedSig = calculateSignature(itemAttributes, attributeFlags, associatedData, (SecretKey)verificationKey);
            if (!safeEquals(signature, calculatedSig)) {
                throw new SignatureException("Bad signature");
            }
        } else if (verificationKey instanceof PublicKey) {
            PublicKey integrityKey = (PublicKey)verificationKey;
            byte[] stringToSign = calculateStringToSign(itemAttributes, attributeFlags, associatedData);
            Signature sig = Signature.getInstance(getSigningAlgorithm());
            sig.initVerify(integrityKey);
            sig.update(stringToSign);
            if (!sig.verify(toByteArray(signature))) {
                throw new SignatureException("Bad signature");
            }
        } else {
            throw new IllegalArgumentException("No integrity key provided");
        }
    }

    static byte[] calculateStringToSign(Map<String, InternalAttributeValue> itemAttributes,
            Map<String, Set<EncryptionFlags>> attributeFlags, byte[] associatedData)
            throws NoSuchAlgorithmException {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            List<String> attrNames = new ArrayList<String>(itemAttributes.keySet());
            Collections.sort(attrNames);
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            if (associatedData != null) {
                out.write(sha256.digest(associatedData));
            } else {
                out.write(sha256.digest());
            }
            sha256.reset();

            for (String name : attrNames) {
                Set<EncryptionFlags> set = attributeFlags.get(name);
                if(set != null && set.contains(EncryptionFlags.SIGN)) {
                    InternalAttributeValue tmp = itemAttributes.get(name);
                    out.write(sha256.digest(name.getBytes(UTF8)));
                    sha256.reset();
                    if (set.contains(EncryptionFlags.ENCRYPT)) {
                        sha256.update("ENCRYPTED".getBytes(UTF8));
                    } else {
                        sha256.update("PLAINTEXT".getBytes(UTF8));
                    }
                    out.write(sha256.digest());

                    sha256.reset();

                    sha256.update(InternalAttributeValueMarshaller.marshall(tmp));
                    out.write(sha256.digest());
                    sha256.reset();
                }
            }
            return out.toByteArray();
        } catch (IOException ex) {
            // Due to the objects in use, an IOException is not possible.
            throw new RuntimeException("Unexpected exception", ex);
        }
    }

    /**
     * The itemAttributes have already been encrypted, if necessary, before the
     * signing.
     */
    byte[] calculateSignature(
            Map<String, InternalAttributeValue> itemAttributes,
            Map<String, Set<EncryptionFlags>> attributeFlags,
            byte[] associatedData, Key key) throws GeneralSecurityException {
        if (key instanceof DelegatedKey) {
            return calculateSignature(itemAttributes, attributeFlags, associatedData, (DelegatedKey) key);
        } else if (key instanceof SecretKey) {
            return calculateSignature(itemAttributes, attributeFlags, associatedData, (SecretKey) key);
        } else if (key instanceof PrivateKey) {
            return calculateSignature(itemAttributes, attributeFlags, associatedData, (PrivateKey) key);
        } else {
            throw new IllegalArgumentException("No integrity key provided");
        }
    }

    byte[] calculateSignature(Map<String, InternalAttributeValue> itemAttributes,
            Map<String, Set<EncryptionFlags>> attributeFlags, byte[] associatedData,
            DelegatedKey key) throws GeneralSecurityException {
        byte[] stringToSign = calculateStringToSign(itemAttributes, attributeFlags, associatedData);
        return key.sign(stringToSign, key.getAlgorithm());
    }

    byte[] calculateSignature(Map<String, InternalAttributeValue> itemAttributes,
            Map<String, Set<EncryptionFlags>> attributeFlags, byte[] associatedData,
            SecretKey key) throws GeneralSecurityException {
        if (key instanceof DelegatedKey) {
            return calculateSignature(itemAttributes, attributeFlags, associatedData, (DelegatedKey)key);
        }
        byte[] stringToSign = calculateStringToSign(itemAttributes, attributeFlags, associatedData);
        Mac hmac = Mac.getInstance(key.getAlgorithm());
        hmac.init(key);
        hmac.update(stringToSign);
        return hmac.doFinal();
    }

    byte[] calculateSignature(Map<String, InternalAttributeValue> itemAttributes,
            Map<String, Set<EncryptionFlags>> attributeFlags, byte[] associatedData,
            PrivateKey key) throws GeneralSecurityException {
        byte[] stringToSign = calculateStringToSign(itemAttributes, attributeFlags, associatedData);
        Signature sig = Signature.getInstance(signingAlgorithm);
        sig.initSign(key, rnd);
        sig.update(stringToSign);
        return sig.sign();
    }

    String getSigningAlgorithm() {
        return signingAlgorithm;
    }

    /**
     * Constant-time equality check.
     */
    private boolean safeEquals(ByteBuffer signature, byte[] calculatedSig) {
        try {
            signature.rewind();
            Mac hmac = Mac.getInstance(hmacComparisonKey.getAlgorithm());
            hmac.init(hmacComparisonKey);
            hmac.update(signature);
            byte[] signatureHash = hmac.doFinal();

            hmac.reset();
            hmac.update(calculatedSig);
            byte[] calculatedHash = hmac.doFinal();

            return MessageDigest.isEqual(signatureHash, calculatedHash);
        } catch (GeneralSecurityException ex) {
            // We've hardcoded these algorithms, so the error should not be possible.
            throw new RuntimeException("Unexpected exception", ex);
        }
    }

    private static byte[] toByteArray(ByteBuffer buffer) {
        if (buffer.hasArray()) {
            byte[] result = buffer.array();
            buffer.rewind();
            return result;
        } else {
            byte[] result = new byte[buffer.remaining()];
            buffer.get(result);
            buffer.rewind();
            return result;
        }
    }
}
