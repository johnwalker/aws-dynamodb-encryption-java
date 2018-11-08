package com.amazonaws.services.dynamodbv2.datamodeling.encryption.sdk2;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.DynamoDBEncryptor;
import com.amazonaws.services.s3.model.EncryptionMaterialsProvider;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class EncryptionContextSDK2 {
    private String tableName = null;
    private Map<String, AttributeValue> attributeValues = null;
    private Class<?> modeledClass = null;
    private Object developerContext = null;
    private String hashKeyName = null;
    private String rangeKeyName = null;
    private Map<String, String> materialDescription = null;

    EncryptionContextSDK2(BuilderImpl encryptionContextBuilder) {
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

    public EncryptionContextBuilders.SDK2Builders.Builder toBuilder() {
        return new BuilderImpl(this);
    }

    static EncryptionContextBuilders.SDK2Builders.Builder builder() {
        return new BuilderImpl();
    }

    static class BuilderImpl implements EncryptionContextBuilders.SDK2Builders.Builder, EncryptionContextBuilders.SDK2Builders.BuilderInternalAPI {
        private String tableName = null;
        private Map<String, AttributeValue> attributeValues = null;
        private Class<?> modeledClass = null;
        private Object developerContext = null;
        private String hashKeyName = null;
        private String rangeKeyName = null;
        private Map<String, String> materialDescription = null;

        public BuilderImpl() {
        }

        public BuilderImpl(EncryptionContextSDK2 encryptionContextSDK2) {
            this.tableName = encryptionContextSDK2.getTableName();
            this.attributeValues = encryptionContextSDK2.getAttributeValues();
            this.modeledClass = encryptionContextSDK2.getModeledClass();
            this.developerContext = encryptionContextSDK2.getDeveloperContext();
            this.hashKeyName = encryptionContextSDK2.getHashKeyName();
            this.rangeKeyName = encryptionContextSDK2.getRangeKeyName();
            this.materialDescription = encryptionContextSDK2.getMaterialDescription();
        }

        public BuilderImpl publicAPI() {
            return this;
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

        public BuilderImpl internalAPI() {
            return this;
        }

        public BuilderImpl withAttributeValues(Map<String, AttributeValue> attributeValues) {
            this.attributeValues = Collections.unmodifiableMap(
                    new HashMap<String, AttributeValue>(attributeValues));
            return this;
        }

        @Override
        public EncryptionContextSDK2 build() {
            return new EncryptionContextSDK2(this);
        }
    }
}
