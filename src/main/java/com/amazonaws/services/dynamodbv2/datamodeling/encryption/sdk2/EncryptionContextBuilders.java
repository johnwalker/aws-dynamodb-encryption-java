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
