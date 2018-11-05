package com.amazonaws.services.dynamodbv2.datamodeling.encryption;

public class DefaultDynamoDBEncryptionConfiguration implements DynamoDBEncryptionConfiguration {

    private String signatureFieldName;
    private String materialDescriptionFieldName;
    private String signingAlgorithmHeader;

    public DefaultDynamoDBEncryptionConfiguration() {
        this.signatureFieldName = EncryptionConstants.DEFAULT_SIGNATURE_FIELD;
        this.materialDescriptionFieldName = EncryptionConstants.DEFAULT_METADATA_FIELD;
        this.signingAlgorithmHeader = EncryptionConstants.DEFAULT_SIGNING_ALGORITHM_HEADER;

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
}
