package com.amazonaws.services.dynamodbv2.datamodeling.encryption.sdk2;

import com.amazonaws.services.dynamodbv2.datamodeling.encryption.internal.InternalEncryptionContext;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

public class EncryptionContextSDK2 {

    EncryptionContextSDK2(Builder encryptionContextBuilder) {
    }

    private static class BuilderImpl implements Builder {

        public BuilderImpl() {
        }

        public BuilderImpl(EncryptionContextSDK2 encryptionContextSDK2) {

        }

        @Override
        public EncryptionContextSDK2 build() {
            return null;
        }

        @Override
        public Builder withTableName(String tableName) {
            return null;
        }

        @Override
        public Builder withAttributeValues(Map<String, AttributeValue> attributeValues) {
            return null;
        }

        @Override
        public Builder withModeledClass(Class<?> modeledClass) {
            return null;
        }

        @Override
        public Builder withDeveloperContext(Object developerContext) {
            return null;
        }

        @Override
        public Builder withHashKeyName(String hashKeyName) {
            return null;
        }

        @Override
        public Builder withRangeKeyName(String rangeKeyName) {
            return null;
        }

        @Override
        public Builder withMaterialDescription(Map<String, String> materialDescription) {
            return null;
        }
    }

    public Builder toBuilder() {
        return new BuilderImpl(this);

    }

    static Builder builder() {
        return new BuilderImpl();
    }

    public interface Builder extends InternalGenericBuilder<Builder, AttributeValue, EncryptionContextSDK2> {
    }

    public interface GenericBuilder<B> {
        B withTableName(String tableName);

        B withModeledClass(Class<?> modeledClass);

        B withDeveloperContext(Object developerContext);

        B withHashKeyName(String hashKeyName);

        B withRangeKeyName(String rangeKeyName);

        B withMaterialDescription(Map<String, String> materialDescription);
    }

    public interface InternalGenericBuilder<B, T, E> extends GenericBuilder {
        B withAttributeValues(Map<String, T> attributeValues);
        E build();
    }





}
