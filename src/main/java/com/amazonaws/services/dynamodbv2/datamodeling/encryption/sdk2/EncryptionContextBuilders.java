package com.amazonaws.services.dynamodbv2.datamodeling.encryption.sdk2;

import com.amazonaws.services.dynamodbv2.datamodeling.encryption.EncryptionContext;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

public class EncryptionContextBuilders {
    public interface SDK1Builders {
        interface Builder extends GenericBuilder<Builder, SDK1Builders.BuilderInternalAPI, EncryptionContext> {
        }

        interface BuilderInternalAPI extends GenericBuilder.GenericBuilderInternalAPI<Builder, BuilderInternalAPI, com.amazonaws.services.dynamodbv2.model.AttributeValue> {
        }
    }

    public interface SDK2Builders {
        interface Builder extends GenericBuilder<Builder, SDK2Builders.BuilderInternalAPI, EncryptionContextSDK2> {
        }

        interface BuilderInternalAPI extends GenericBuilder.GenericBuilderInternalAPI<Builder, BuilderInternalAPI, AttributeValue> {
        }
    }

    public interface GenericBuilder<B, B2 extends GenericBuilder.GenericBuilderInternalAPI<B, ?, ?>, E> {
        B withTableName(String tableName);

        B withModeledClass(Class<?> modeledClass);

        B withDeveloperContext(Object developerContext);

        B withHashKeyName(String hashKeyName);

        B withRangeKeyName(String rangeKeyName);

        B withMaterialDescription(Map<String, String> materialDescription);

        B2 internalAPI();

        E build();

        interface GenericBuilderInternalAPI<B, B2, T> {
            B2 withAttributeValues(Map<String, T> attributeValues);
            B publicAPI();
        }
    }

    public interface GenericEncryptionContext<T, B> {
        String getTableName();

        Class<?> getModeledClass();

        Object getDeveloperContext();

        String getHashKeyName();

        String getRangeKeyName();

        Map<String, String> getMaterialDescription();

        Map<String, T> getAttributeValues();

        B toBuilder();
    }
}
