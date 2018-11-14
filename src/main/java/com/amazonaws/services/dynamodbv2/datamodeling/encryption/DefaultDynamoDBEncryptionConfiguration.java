package com.amazonaws.services.dynamodbv2.datamodeling.encryption;

import com.amazonaws.services.dynamodbv2.datamodeling.encryption.sdk2.EncryptionContextSDK2;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.sdk2.providers.EncryptionMaterialsProviderSdk2;

public class DefaultDynamoDBEncryptionConfiguration implements DynamoDBEncryptionConfigurationSDK2 {

    private String symModeHeader;
    private String signatureFieldName;
    private String materialDescriptionFieldName;
    private String signingAlgorithmHeader;
    private String descriptionBase;
    private Transformer<EncryptionContextSDK2> encryptionContextTransformer;
    private EncryptionMaterialsProviderSdk2 encryptionMaterialsProviderSdk2;

    public DefaultDynamoDBEncryptionConfiguration() {
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

    public void setEncryptionContextTransformer(Transformer<EncryptionContextSDK2> encryptionContextTransformer) {
        this.encryptionContextTransformer = encryptionContextTransformer;

    }

    @Override
    public Transformer<EncryptionContextSDK2> getEncryptionContextTransformer() {
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
