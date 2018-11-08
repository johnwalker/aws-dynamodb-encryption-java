package com.amazonaws.services.dynamodbv2.datamodeling.encryption.sdk2;

import com.amazonaws.services.dynamodbv2.datamodeling.encryption.EncryptionContext;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

public class EncryptionContextBuilders {
    public interface SDK1Builders {
        interface Builder extends GenericBuilder<Builder, EncryptionContext, SDK1Builders.BuilderInternalAPI> {
        }

        interface BuilderInternalAPI extends GenericBuilder.GenericBuilderInternalAPI<Builder, BuilderInternalAPI, com.amazonaws.services.dynamodbv2.model.AttributeValue> {
        }
    }

    public interface SDK2Builders {
        interface Builder extends GenericBuilder<Builder, EncryptionContextSDK2, SDK2Builders.BuilderInternalAPI> {
        }

        interface BuilderInternalAPI extends GenericBuilder.GenericBuilderInternalAPI<Builder, BuilderInternalAPI, AttributeValue> {
        }
    }

    public interface GenericBuilder<B, E, B2 extends GenericBuilder.GenericBuilderInternalAPI<B, ?, ?>> {
        B withTableName(String tableName);

        B withModeledClass(Class<?> modeledClass);

        B withDeveloperContext(Object developerContext);

        B withHashKeyName(String hashKeyName);

        B withRangeKeyName(String rangeKeyName);

        B withMaterialDescription(Map<String, String> materialDescription);

        B2 internalAPI();

        E build();

        interface GenericBuilderInternalAPI<B, P, T> {
            P withAttributeValues(Map<String, T> attributeValues);

            B publicAPI();
        }
    }

    public interface GenericEncryptionContext<T, B> {
        String getTableName(String tableName);

        Class<?> getModeledClass(Class<?> modeledClass);

        Object getDeveloperContext(Object developerContext);

        String getHashKeyName(String hashKeyName);

        String getRangeKeyName(String rangeKeyName);

        Map<String, String> getMaterialDescription(Map<String, String> materialDescription);

        Map<String, T> getAttributeValues();

        B toBuilder();
    }
}
