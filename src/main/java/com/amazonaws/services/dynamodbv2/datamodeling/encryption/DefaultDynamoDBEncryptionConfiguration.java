package com.amazonaws.services.dynamodbv2.datamodeling.encryption;

public class DefaultDynamoDBEncryptionConfiguration implements DynamoDBEncryptionConfiguration {

    private String symModeHeader;
    private String signatureFieldName;
    private String materialDescriptionFieldName;
    private String signingAlgorithmHeader;
    private String descriptionBase;

    public DefaultDynamoDBEncryptionConfiguration() {
        this.signatureFieldName = EncryptionConstants.DEFAULT_SIGNATURE_FIELD;
        this.materialDescriptionFieldName = EncryptionConstants.DEFAULT_METADATA_FIELD;
        this.signingAlgorithmHeader = EncryptionConstants.DEFAULT_SIGNING_ALGORITHM_HEADER;
        this.symModeHeader = EncryptionConstants.DEFAULT_SYM_MODE_HEADER;
        this.descriptionBase = EncryptionConstants.DEFAULT_DESCRIPTION_BASE;
    }

    @Override
    public String getSignatureFieldName() {
        return signatureFieldName;
    }

    @Override
    public void setSignatureFieldName(String signatureFieldName) {
        this.signatureFieldName = signatureFieldName;

    }

    @Override
    public String getMaterialDescriptionFieldName() {
        return materialDescriptionFieldName;
    }

    @Override
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

    @Override
    public void setDescriptionBase(String descriptionBase) {
        this.descriptionBase = descriptionBase;
        this.symModeHeader = descriptionBase + EncryptionConstants.HELPER_CONSTANT_SYM_MODE;
        this.signingAlgorithmHeader = descriptionBase + EncryptionConstants.HELPER_CONSTANT_SIGNING_ALG;
    }

}
