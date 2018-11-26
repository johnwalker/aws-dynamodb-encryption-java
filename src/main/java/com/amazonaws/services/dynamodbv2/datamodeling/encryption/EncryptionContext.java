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

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.materials.DecryptionMaterials;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.materials.EncryptionMaterials;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.providers.EncryptionMaterialsProvider;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.sdk2.EncryptionContextBuilders;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
public final class EncryptionContext implements EncryptionContextBuilders.GenericEncryptionContext<AttributeValue, EncryptionContextBuilders.SDK1Builders.Builder> {
    private final String tableName;
    private final Map<String, AttributeValue> attributeValues;
    private final Class<?> modeledClass;
    private final Object developerContext;
    private final String hashKeyName;
    private final String rangeKeyName;
    private final Map<String, String> materialDescription;

    private EncryptionContext(Builder builder) {
        tableName = builder.getTableName();
        attributeValues = builder.getAttributeValues();
        modeledClass = builder.getModeledClass();
        developerContext = builder.getDeveloperContext();
        hashKeyName = builder.getHashKeyName();
        rangeKeyName = builder.getRangeKeyName();
        materialDescription = builder.getMaterialDescription();
    }

    private EncryptionContext(BuilderImpl encryptionContextBuilder) {
        this.tableName = encryptionContextBuilder.tableName;
        this.attributeValues = encryptionContextBuilder.attributeValues;
        this.modeledClass = encryptionContextBuilder.modeledClass;
        this.developerContext = encryptionContextBuilder.developerContext;
        this.hashKeyName = encryptionContextBuilder.hashKeyName;
        this.rangeKeyName = encryptionContextBuilder.rangeKeyName;
        this.materialDescription = encryptionContextBuilder.materialDescription;
    }


    /**
     * Returns the name of the DynamoDB Table this record is associated with.
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * Returns the DynamoDB record about to be encrypted/decrypted.
     */
    public Map<String, AttributeValue> getAttributeValues() {
        return attributeValues;
    }

    /**
     * When used for an object mapping layer (such as {@link DynamoDBMapper})
     * this represents the class being mapped to/from DynamoDB.
     */
    public Class<?> getModeledClass() {
        return modeledClass;
    }

    /**
     * This object has no meaning (and will not be set or examined) by any core libraries.
     * It exists to allow custom object mappers and data access layers to pass
     * data to {@link EncryptionMaterialsProvider}s through the {@link DynamoDBEncryptor}.
     */
    public Object getDeveloperContext() {
        return developerContext;
    }

    /**
     * Returns the name of the HashKey attribute for the record to be encrypted/decrypted.
     */
    public String getHashKeyName() {
        return hashKeyName;
    }

    /**
     * Returns the name of the RangeKey attribute for the record to be encrypted/decrypted.
     */
    public String getRangeKeyName() {
        return rangeKeyName;
    }

    public Map<String, String> getMaterialDescription() {
        return materialDescription;
    }

    /**
     * BuilderImpl class for {@link EncryptionContext}.
     * Mutable objects (other than <code>developerContext</code>) will undergo
     * a defensive copy prior to being stored in the builder.
     *
     * This class is <em>not</em> thread-safe.
     *
     * @deprecated use EncryptionContext.builder() for creating builders,
     *             and encryptionContext.toBuilder() for creating builders based off an
     *             instance of EncryptionContext
     *
     */

    public EncryptionContextBuilders.SDK1Builders.Builder toBuilder() {
        return new BuilderImpl(this);
    }

    public static EncryptionContextBuilders.SDK1Builders.Builder builder() {
        return new BuilderImpl();
    }

    /**
     * BuilderImpl class for {@link EncryptionContext}.
     * Mutable objects (other than <code>developerContext</code>) will undergo
     * a defensive copy prior to being stored in the builder.
     *
     * This class is <em>not</em> thread-safe.
     */
    public static final class Builder {
        private String tableName = null;
        private Map<String, AttributeValue> attributeValues = null;
        private Class<?> modeledClass = null;
        private Object developerContext = null;
        private String hashKeyName = null;
        private String rangeKeyName = null;
        private Map<String, String> materialDescription = null;

        /**
         * Defaults all fields to <code>null</code>.
         */
        public Builder() {
        }

        /**
         * Copy constructor.
         * This will perform a shallow copy of the <code>DeveloperContext</code>.
         */
        public Builder(EncryptionContext context) {
            tableName = context.getTableName();
            attributeValues = context.getAttributeValues();
            modeledClass = context.getModeledClass();
            developerContext = context.getDeveloperContext();
            hashKeyName = context.getHashKeyName();
            rangeKeyName = context.getRangeKeyName();
            materialDescription = context.getMaterialDescription();
        }

        public EncryptionContext build() {
            return new EncryptionContext(this);
        }

        public Builder withTableName(String tableName) {
            this.tableName = tableName;
            return this;
        }

        public Builder withAttributeValues(Map<String, AttributeValue> attributeValues) {
            this.attributeValues = Collections.unmodifiableMap(
                    new HashMap<String, AttributeValue>(attributeValues));
            return this;
        }

        public Builder withModeledClass(Class<?> modeledClass) {
            this.modeledClass = modeledClass;
            return this;
        }

        public Builder withDeveloperContext(Object developerContext) {
            this.developerContext = developerContext;
            return this;
        }

        public Builder withHashKeyName(String hashKeyName) {
            this.hashKeyName = hashKeyName;
            return this;
        }

        public Builder withRangeKeyName(String rangeKeyName) {
            this.rangeKeyName = rangeKeyName;
            return this;
        }

        public Builder withMaterialDescription(Map<String, String> materialDescription) {
            this.materialDescription = Collections.unmodifiableMap(
                    new HashMap<String, String>(materialDescription));
            return this;
        }

        public String getTableName() {
            return tableName;
        }

        public Map<String, AttributeValue> getAttributeValues() {
            return attributeValues;
        }

        public Class<?> getModeledClass() {
            return modeledClass;
        }

        public Object getDeveloperContext() {
            return developerContext;
        }

        public String getHashKeyName() {
            return hashKeyName;
        }

        public String getRangeKeyName() {
            return rangeKeyName;
        }

        public Map<String, String> getMaterialDescription() {
            return materialDescription;
        }
    }

    static final class BuilderImpl implements EncryptionContextBuilders.SDK1Builders.Builder,
            EncryptionContextBuilders.SDK1Builders.BuilderInternalAPI {
        private String tableName = null;
        private Map<String, AttributeValue> attributeValues = null;
        private Class<?> modeledClass = null;
        private Object developerContext = null;
        private String hashKeyName = null;
        private String rangeKeyName = null;
        private Map<String, String> materialDescription = null;

        /**
         * Defaults all fields to <code>null</code>.
         */
        public BuilderImpl() {
        }

        /**
         * Copy constructor.
         * This will perform a shallow copy of the <code>DeveloperContext</code>.
         */
        public BuilderImpl(EncryptionContext context) {
            tableName = context.getTableName();
            attributeValues = context.getAttributeValues();
            modeledClass = context.getModeledClass();
            developerContext = context.getDeveloperContext();
            hashKeyName = context.getHashKeyName();
            rangeKeyName = context.getRangeKeyName();
            materialDescription = context.getMaterialDescription();
        }

        public EncryptionContext build() {
            return new EncryptionContext(this);
        }

        public BuilderImpl withTableName(String tableName) {
            this.tableName = tableName;
            return this;
        }

        public BuilderImpl withModeledClass(Class<?> modeledClass) {
            this.modeledClass = modeledClass;
            return this;
        }

        public BuilderImpl withDeveloperContext(Object developerContext) {
            this.developerContext = developerContext;
            return this;
        }

        public BuilderImpl withHashKeyName(String hashKeyName) {
            this.hashKeyName = hashKeyName;
            return this;
        }

        public BuilderImpl withRangeKeyName(String rangeKeyName) {
            this.rangeKeyName = rangeKeyName;
            return this;
        }

        public BuilderImpl withMaterialDescription(Map<String, String> materialDescription) {
            this.materialDescription = Collections.unmodifiableMap(
                    new HashMap<String, String>(materialDescription));
            return this;
        }

        @Override
        public EncryptionContextBuilders.SDK1Builders.BuilderInternalAPI internalAPI() {
            return this;
        }

        @Override
        public EncryptionContextBuilders.SDK1Builders.BuilderInternalAPI withAttributeValues(Map<String, AttributeValue> attributeValues) {
            this.attributeValues = Collections.unmodifiableMap(
                    new HashMap<String, AttributeValue>(attributeValues));
            return this;
        }

        @Override
        public EncryptionContextBuilders.SDK1Builders.Builder publicAPI() {
            return this;
        }
    }


    @Override
    public String toString() {
        return "EncryptionContext [tableName=" + tableName + ", attributeValues=" + attributeValues
                + ", modeledClass=" + modeledClass + ", developerContext=" + developerContext
                + ", hashKeyName=" + hashKeyName + ", rangeKeyName=" + rangeKeyName
                + ", materialDescription=" + materialDescription + "]";
    }
}
