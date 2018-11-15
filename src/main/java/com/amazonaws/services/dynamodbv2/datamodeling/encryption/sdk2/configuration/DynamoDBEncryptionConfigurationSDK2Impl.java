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
package com.amazonaws.services.dynamodbv2.datamodeling.encryption.sdk2.configuration;

import com.amazonaws.services.dynamodbv2.datamodeling.encryption.EncryptionConstants;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.sdk2.EncryptionContextSDK2;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.sdk2.providers.EncryptionMaterialsProviderSdk2;

import java.util.function.UnaryOperator;

public class DynamoDBEncryptionConfigurationSDK2Impl implements DynamoDBEncryptionConfigurationSDK2 {

    private String symModeHeader;
    private String signatureFieldName;
    private String materialDescriptionFieldName;
    private String signingAlgorithmHeader;
    private String descriptionBase;
    private UnaryOperator<EncryptionContextSDK2> encryptionContextTransformer;
    private EncryptionMaterialsProviderSdk2 encryptionMaterialsProviderSdk2;

    public DynamoDBEncryptionConfigurationSDK2Impl() {
        this.signatureFieldName = EncryptionConstants.DEFAULT_SIGNATURE_FIELD;
        this.materialDescriptionFieldName = EncryptionConstants.DEFAULT_METADATA_FIELD;
        this.signingAlgorithmHeader = EncryptionConstants.DEFAULT_SIGNING_ALGORITHM_HEADER;
        this.symModeHeader = EncryptionConstants.DEFAULT_SYM_MODE_HEADER;
        this.descriptionBase = EncryptionConstants.DEFAULT_DESCRIPTION_BASE;
        this.encryptionContextTransformer = null;
        this.encryptionMaterialsProviderSdk2 = null;
    }

    @Override
    public String getSignatureFieldName() {
        return signatureFieldName;
    }

    public void setSignatureFieldName(String signatureFieldName) {
        this.signatureFieldName = signatureFieldName;
    }

    @Override
    public String getMaterialDescriptionFieldName() {
        return materialDescriptionFieldName;
    }

    public void setMaterialDescriptionFieldName(String materialDescriptionFieldName) {
        this.materialDescriptionFieldName = materialDescriptionFieldName;
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

    public void setDescriptionBase(String descriptionBase) {
        this.descriptionBase = descriptionBase;
        this.symModeHeader = descriptionBase + EncryptionConstants.HELPER_CONSTANT_SYM_MODE;
        this.signingAlgorithmHeader = descriptionBase + EncryptionConstants.HELPER_CONSTANT_SIGNING_ALG;
    }

    public void setEncryptionContextTransformer(UnaryOperator<EncryptionContextSDK2> encryptionContextTransformer) {
        this.encryptionContextTransformer = encryptionContextTransformer;
    }

    @Override
    public UnaryOperator<EncryptionContextSDK2> getEncryptionContextTransformer() {
        return encryptionContextTransformer;
    }

    public void setEncryptionMaterialsProvider(EncryptionMaterialsProviderSdk2 encryptionMaterialsProvider) {
        this.encryptionMaterialsProviderSdk2 = encryptionMaterialsProvider;
    }

    @Override
    public EncryptionMaterialsProviderSdk2 getEncryptionMaterialsProvider() {
        return encryptionMaterialsProviderSdk2;
    }
}
