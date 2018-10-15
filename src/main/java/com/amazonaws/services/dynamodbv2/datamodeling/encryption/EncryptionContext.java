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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.materials.DecryptionMaterials;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.materials.EncryptionMaterials;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.providers.EncryptionMaterialsProvider;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.s3.model.Encryption;

/**
 * This class serves to provide additional useful data to
 * {@link EncryptionMaterialsProvider}s so they can more intelligently select
 * the proper {@link EncryptionMaterials} or {@link DecryptionMaterials} for
 * use. Any of the methods are permitted to return null.
 * <p>
 * For the simplest cases, all a developer needs to provide in the context are:
 * <ul>
 * <li>TableName</li>
 * <li>HashKeyName</li>
 * <li>RangeKeyName (if present)</li>
 * </ul>
 * 
 * This class is immutable.
 * 
 * @author Greg Rubin 
 */
public final class EncryptionContext extends GenericEncryptionContext<AttributeValue> {

    EncryptionContext(Builder encryptionContextBuilder) {
        super(encryptionContextBuilder);
    }

    public static class Builder extends GenericEncryptionContext.Builder<AttributeValue, Builder> {
        public Builder() {
        }

        public Builder(EncryptionContext encryptionContext) {
            super(encryptionContext);
        }

        public EncryptionContext build() {
            return new EncryptionContext(this);
        }
    }

}
