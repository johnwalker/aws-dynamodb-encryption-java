package com.amazonaws.services.dynamodbv2.datamodeling.encryption;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.materials.DecryptionMaterials;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.materials.EncryptionMaterials;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.providers.EncryptionMaterialsProvider;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

public class GenericEncryptionContext<T> {
    private final String tableName;
    private final Map<String, T> attributeValues;
    private final Class<?> modeledClass;
    private final Object developerContext;
    private final String hashKeyName;
    private final String rangeKeyName;
    private final Map<String, String> materialDescription;

    GenericEncryptionContext(Builder builder) {
        tableName = builder.getTableName();
        attributeValues = builder.getAttributeValues();
        modeledClass = builder.getModeledClass();
        developerContext = builder.getDeveloperContext();
        hashKeyName = builder.getHashKeyName();
        rangeKeyName = builder.getRangeKeyName();
        materialDescription = builder.getMaterialDescription();
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
    public Map<String, T> getAttributeValues() {
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
     * Builder class for {@link EncryptionContext}.
     * Mutable objects (other than <code>developerContext</code>) will undergo
     * a defensive copy prior to being stored in the builder.
     *
     * This class is <em>not</em> thread-safe.
     */
    public static class Builder<U, V extends Builder<U, V>> {
        private String tableName = null;
        private Map<String, U> attributeValues = null;
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
        public Builder(GenericEncryptionContext<U> context) {
            tableName = context.getTableName();
            attributeValues = context.getAttributeValues();
            modeledClass = context.getModeledClass();
            developerContext = context.getDeveloperContext();
            hashKeyName = context.getHashKeyName();
            rangeKeyName = context.getRangeKeyName();
            materialDescription = context.getMaterialDescription();
        }

        public GenericEncryptionContext build() {
            return new GenericEncryptionContext(this);
        }

        public V withTableName(String tableName) {
            this.tableName = tableName;
            return (V) this;
        }

        public V withAttributeValues(Map<String, U> attributeValues) {
            this.attributeValues = Collections.unmodifiableMap(
                    new HashMap<String, U>(attributeValues));
            return (V) this;
        }

        public V withModeledClass(Class<?> modeledClass) {
            this.modeledClass = modeledClass;
            return (V) this;
        }

        public V withDeveloperContext(Object developerContext) {
            this.developerContext = developerContext;
            return (V) this;
        }

        public V withHashKeyName(String hashKeyName) {
            this.hashKeyName = hashKeyName;
            return (V) this;
        }

        public V withRangeKeyName(String rangeKeyName) {
            this.rangeKeyName = rangeKeyName;
            return (V) this;
        }

        public V withMaterialDescription(Map<String, String> materialDescription) {
            this.materialDescription = Collections.unmodifiableMap(
                    new HashMap<String, String>(materialDescription));
            return (V) this;
        }

        public String getTableName() {
            return tableName;
        }

        public Map<String, U> getAttributeValues() {
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

    @Override
    public String toString() {
        return "EncryptionContext [tableName=" + tableName + ", attributeValues=" + attributeValues
                + ", modeledClass=" + modeledClass + ", developerContext=" + developerContext
                + ", hashKeyName=" + hashKeyName + ", rangeKeyName=" + rangeKeyName
                + ", materialDescription=" + materialDescription + "]";
    }
}
