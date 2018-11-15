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
package com.amazonaws.services.dynamodbv2.datamodeling.encryption.configuration;

import com.amazonaws.services.dynamodbv2.datamodeling.encryption.EncryptionConstants;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.EncryptionContext;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.providers.EncryptionMaterialsProvider;

import java.util.function.UnaryOperator;

class DynamoDBEncryptionConfigurationSDK1Impl implements DynamoDBEncryptionConfigurationSDK1 {
    private String descriptionBase;
    private String signingAlgorithmHeader;
    private String symModeHeader;

    private String materialDescriptionFieldName;
    private String signatureFieldName;
    private UnaryOperator<EncryptionContext> encryptionContextTransformer;
    private EncryptionMaterialsProvider encryptionMaterialsProvider;

    DynamoDBEncryptionConfigurationSDK1Impl() {
        this.descriptionBase = EncryptionConstants.DEFAULT_DESCRIPTION_BASE;
        this.signingAlgorithmHeader = EncryptionConstants.DEFAULT_SIGNING_ALGORITHM_HEADER;
        this.symModeHeader = EncryptionConstants.DEFAULT_SYM_MODE_HEADER;

        this.materialDescriptionFieldName = EncryptionConstants.DEFAULT_METADATA_FIELD;
        this.signatureFieldName = EncryptionConstants.DEFAULT_SIGNATURE_FIELD;
        this.encryptionContextTransformer = null;
        this.encryptionMaterialsProvider = null;
    }

    DynamoDBEncryptionConfigurationSDK1Impl(DynamoDBEncryptionConfigurationSDK1ImplBuilder builder) {
        this.descriptionBase = builder.descriptionBase;
        this.signingAlgorithmHeader = builder.descriptionBase + EncryptionConstants.HELPER_CONSTANT_SIGNING_ALG;
        this.symModeHeader = builder.descriptionBase + EncryptionConstants.HELPER_CONSTANT_SYM_MODE;

        this.materialDescriptionFieldName = builder.materialDescriptionFieldName;
        this.signatureFieldName = builder.signatureFieldName;
        this.encryptionContextTransformer = builder.encryptionContextTransformer;
        this.encryptionMaterialsProvider = builder.encryptionMaterialsProvider;
    }

    @Override
    public String getSignatureFieldName() {
        return signatureFieldName;
    }

    @Override
    public String getMaterialDescriptionFieldName() {
        return materialDescriptionFieldName;
    }

    @Override
    public String getSigningAlgorithmHeader() {
        return signingAlgorithmHeader;
    }

    @Override
    public String getDescriptionBase() {
        return descriptionBase;
    }

    @Override
    public String getSymModeHeader() {
        return symModeHeader;
    }

    @Override
    public UnaryOperator<EncryptionContext> getEncryptionContextTransformer() {
        return encryptionContextTransformer;
    }

    @Override
    public EncryptionMaterialsProvider getEncryptionMaterialsProvider() {
        return encryptionMaterialsProvider;
    }

    static final class DynamoDBEncryptionConfigurationSDK1ImplBuilder implements DynamoDBEncryptionConfigurationSDK1.Builder {

        private String descriptionBase;
        private String signatureFieldName;
        private String materialDescriptionFieldName;
        private EncryptionMaterialsProvider encryptionMaterialsProvider;
        private UnaryOperator<EncryptionContext> encryptionContextTransformer;

        DynamoDBEncryptionConfigurationSDK1ImplBuilder() {
            this(new DynamoDBEncryptionConfigurationSDK1Impl());
        }

        DynamoDBEncryptionConfigurationSDK1ImplBuilder(DynamoDBEncryptionConfigurationSDK1 configuration) {
            this.descriptionBase = configuration.getDescriptionBase();
            this.signatureFieldName = configuration.getSignatureFieldName();
            this.materialDescriptionFieldName = configuration.getMaterialDescriptionFieldName();
            this.encryptionContextTransformer = configuration.getEncryptionContextTransformer();
            this.encryptionMaterialsProvider = configuration.getEncryptionMaterialsProvider();
        }

        @Override
        public Builder withSignatureFieldName(String signatureFieldName) {
            this.signatureFieldName = signatureFieldName;
            return this;
        }

        @Override
        public Builder withMaterialDescriptionFieldName(String materialDescriptionFieldName) {
            this.materialDescriptionFieldName = materialDescriptionFieldName;
            return this;
        }

        @Override
        public Builder withDescriptionBase(String descriptionBase) {
            this.descriptionBase = descriptionBase;
            return this;
        }

        @Override
        public Builder withEncryptionContextTransformer(UnaryOperator<EncryptionContext> transformer) {
            this.encryptionContextTransformer = transformer;
            return this;
        }

        @Override
        public Builder withEncryptionMaterialsProvider(EncryptionMaterialsProvider encryptionMaterialsProvider) {
            this.encryptionMaterialsProvider = encryptionMaterialsProvider;
            return this;
        }

        @Override
        public DynamoDBEncryptionConfigurationSDK1 build() {
            return new DynamoDBEncryptionConfigurationSDK1Impl(this);
        }
    }

}
