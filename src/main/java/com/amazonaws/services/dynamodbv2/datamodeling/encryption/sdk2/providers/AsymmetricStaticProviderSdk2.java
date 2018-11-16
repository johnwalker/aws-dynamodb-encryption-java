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
package com.amazonaws.services.dynamodbv2.datamodeling.encryption.sdk2.providers;

import com.amazonaws.services.dynamodbv2.datamodeling.encryption.providers.WrappedMaterialsProvider;

import javax.crypto.SecretKey;
import java.security.KeyPair;
import java.util.Collections;
import java.util.Map;

/**
 * This is a thin wrapper around the {@link WrappedMaterialsProvider}, using
 * the provided <code>encryptionKey</code> for wrapping and unwrapping the
 * record key.
 *
 * @see WrappedMaterialsProvider
 */
public class AsymmetricStaticProviderSdk2 extends WrappedMaterialsProviderSdk2 {
    public AsymmetricStaticProviderSdk2(KeyPair encryptionKey, KeyPair signingPair) {
        this(encryptionKey, signingPair, Collections.<String, String>emptyMap());
    }

    public AsymmetricStaticProviderSdk2(KeyPair encryptionKey, SecretKey macKey) {
        this(encryptionKey, macKey, Collections.<String, String>emptyMap());
    }

    public AsymmetricStaticProviderSdk2(KeyPair encryptionKey, KeyPair signingPair, Map<String, String> description) {
        super(encryptionKey.getPublic(), encryptionKey.getPrivate(), signingPair, description);
    }

    public AsymmetricStaticProviderSdk2(KeyPair encryptionKey, SecretKey macKey, Map<String, String> description) {
        super(encryptionKey.getPublic(), encryptionKey.getPrivate(), macKey, description);
    }
}
