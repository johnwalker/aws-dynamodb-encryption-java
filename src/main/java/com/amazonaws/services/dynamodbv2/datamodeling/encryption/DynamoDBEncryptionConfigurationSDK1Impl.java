package com.amazonaws.services.dynamodbv2.datamodeling.encryption;

import com.amazonaws.services.dynamodbv2.datamodeling.encryption.providers.EncryptionMaterialsProvider;

class DynamoDBEncryptionConfigurationSDK1Impl implements DynamoDBEncryptionConfigurationSDK1 {
    // Not set directly, but modified by changing descriptionBase
    private String symModeHeader;
    private String signingAlgorithmHeader;

    private String signatureFieldName;
    private String materialDescriptionFieldName;
    private String descriptionBase;
    private Transformer<EncryptionContext> encryptionContextTransformer;
    private EncryptionMaterialsProvider encryptionMaterialsProviderSdk2;

    DynamoDBEncryptionConfigurationSDK1Impl() {
        this.signatureFieldName = EncryptionConstants.DEFAULT_SIGNATURE_FIELD;
        this.materialDescriptionFieldName = EncryptionConstants.DEFAULT_METADATA_FIELD;
        this.signingAlgorithmHeader = EncryptionConstants.DEFAULT_SIGNING_ALGORITHM_HEADER;
        this.symModeHeader = EncryptionConstants.DEFAULT_SYM_MODE_HEADER;
        this.descriptionBase = EncryptionConstants.DEFAULT_DESCRIPTION_BASE;
        this.encryptionContextTransformer = null;
        this.encryptionMaterialsProviderSdk2 = null;
    }

    DynamoDBEncryptionConfigurationSDK1Impl(DynamoDBEncryptionConfigurationSDK1ImplBuilder builder) {
        this.signatureFieldName = builder.signatureFieldName;
        this.materialDescriptionFieldName = builder.materialDescriptionFieldName;
        this.signingAlgorithmHeader = builder.descriptionBase + EncryptionConstants.HELPER_CONSTANT_SIGNING_ALG;
        this.symModeHeader = builder.descriptionBase + EncryptionConstants.HELPER_CONSTANT_SYM_MODE;
        this.descriptionBase = builder.descriptionBase;
        this.encryptionContextTransformer = builder.encryptionContextTransformer;
        this.encryptionMaterialsProviderSdk2 = builder.encryptionMaterialsProvider;
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

    public void setEncryptionContextTransformer(Transformer<EncryptionContext> encryptionContextTransformer) {
        this.encryptionContextTransformer = encryptionContextTransformer;

    }

    @Override
    public Transformer<EncryptionContext> getEncryptionContextTransformer() {
        return encryptionContextTransformer;
    }

    public void setEncryptionMaterialsProvider(EncryptionMaterialsProvider encryptionMaterialsProvider) {
        this.encryptionMaterialsProviderSdk2 = encryptionMaterialsProvider;
    }

    @Override
    public EncryptionMaterialsProvider getEncryptionMaterialsProvider() {
        return encryptionMaterialsProviderSdk2;
    }

    static final class DynamoDBEncryptionConfigurationSDK1ImplBuilder implements DynamoDBEncryptionConfigurationSDK1.Builder {

        private EncryptionMaterialsProvider encryptionMaterialsProvider;
        private Transformer<EncryptionContext> encryptionContextTransformer;
        private String materialDescriptionFieldName;
        private String descriptionBase;
        private String signatureFieldName;

        public DynamoDBEncryptionConfigurationSDK1ImplBuilder() {
            this(new DynamoDBEncryptionConfigurationSDK1Impl());
        }

        public DynamoDBEncryptionConfigurationSDK1ImplBuilder(DynamoDBEncryptionConfigurationSDK1 configuration) {
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
        public Builder withMaterialDescriptionFieldName(String descriptionFieldName) {
            this.materialDescriptionFieldName = descriptionFieldName;
            return this;
        }

        @Override
        public Builder withDescriptionBase(String descriptionBase) {
            this.descriptionBase = descriptionBase;
            return this;
        }

        @Override
        public Builder withEncryptionContextTransformer(Transformer<EncryptionContext> transformer) {
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
