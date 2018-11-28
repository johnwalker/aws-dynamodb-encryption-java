package com.amazonaws.services.dynamodbv2.datamodeling.encryption.configuration;

import com.amazonaws.services.dynamodbv2.datamodeling.encryption.EncryptionConstants;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.internal.InternalEncryptionMaterialsProvider;
import com.amazonaws.services.dynamodbv2.datamodeling.internal.Preconditions;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

abstract public class InternalDynamoDBEncryptionConfigurationImpl<T, M extends InternalEncryptionMaterialsProvider<T>,
        B extends InternalDynamoDBEncryptionConfigurationBuilder<T, M, ?, ?>,
        C extends InternalDynamoDBEncryptionConfiguration>
        implements InternalDynamoDBEncryptionConfiguration<T, M, B> {
    private String descriptionBase;
    private String signingAlgorithmHeader;
    private String symModeHeader;

    private String materialDescriptionFieldName;
    private String signatureFieldName;
    private Function<T, T> encryptionContextTransformer;
    private M encryptionMaterialsProvider;
    private T encryptionContext;

    private AttributeEncryptionAction defaultAttributeEncryptionAction;
    private Map<String, AttributeEncryptionAction> attributeEncryptionActionOverrides;

    protected InternalDynamoDBEncryptionConfigurationImpl() {
        this.descriptionBase = EncryptionConstants.DEFAULT_DESCRIPTION_BASE;
        this.signingAlgorithmHeader = EncryptionConstants.DEFAULT_SIGNING_ALGORITHM_HEADER;
        this.symModeHeader = EncryptionConstants.DEFAULT_SYM_MODE_HEADER;
        this.materialDescriptionFieldName = EncryptionConstants.DEFAULT_METADATA_FIELD;
        this.signatureFieldName = EncryptionConstants.DEFAULT_SIGNATURE_FIELD;
        this.defaultAttributeEncryptionAction = AttributeEncryptionAction.ENCRYPT_AND_SIGN;
    }

    protected InternalDynamoDBEncryptionConfigurationImpl(final InternalDynamoDBEncryptionConfigurationBuilderImpl<T, M, B, ?> builder) {
        this.descriptionBase = builder.descriptionBase;
        this.signingAlgorithmHeader = builder.descriptionBase + EncryptionConstants.HELPER_CONSTANT_SIGNING_ALG;
        this.symModeHeader = builder.descriptionBase + EncryptionConstants.HELPER_CONSTANT_SYM_MODE;

        this.materialDescriptionFieldName = builder.materialDescriptionFieldName;
        this.signatureFieldName = builder.signatureFieldName;
        this.encryptionMaterialsProvider = builder.encryptionMaterialsProvider;
        this.defaultAttributeEncryptionAction = builder.defaultAttributeEncryptionAction;

        // FIXME don't do a shallow copy
        this.encryptionContext = builder.encryptionContext;
        this.attributeEncryptionActionOverrides = Optional.ofNullable(builder.attributeEncryptionActionOverrides)
                .map(HashMap::new).orElse(null);
        this.encryptionContextTransformer = builder.encryptionContextOverrideOperator;
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
    public Function<T, T> getEncryptionContextOverrideOperator() {
        return encryptionContextTransformer;
    }

    @Override
    public T getEncryptionContext() {
        return encryptionContext;
    }

    @Override
    public Map<String, AttributeEncryptionAction> getAttributeEncryptionActionOverrides() {
        return attributeEncryptionActionOverrides;
    }

    @Override
    public AttributeEncryptionAction getDefaultAttributeEncryptionAction() {
        return defaultAttributeEncryptionAction;
    }

    public M getEncryptionMaterialsProvider() {
        return encryptionMaterialsProvider;
    }

    public static abstract class InternalDynamoDBEncryptionConfigurationBuilderImpl<T,
            M extends InternalEncryptionMaterialsProvider<T>,
            B extends InternalDynamoDBEncryptionConfigurationBuilder,
            C extends InternalDynamoDBEncryptionConfiguration<T, M, B>>
            implements InternalDynamoDBEncryptionConfigurationBuilder<T, M, B, C> {

        // FIXME double check defaults
        String descriptionBase;
        String signatureFieldName;
        String materialDescriptionFieldName;
        M encryptionMaterialsProvider;
        Function<T, T> encryptionContextOverrideOperator;
        T encryptionContext;
        Map<String, AttributeEncryptionAction> attributeEncryptionActionOverrides;
        AttributeEncryptionAction defaultAttributeEncryptionAction;

        public InternalDynamoDBEncryptionConfigurationBuilderImpl(C configuration) {
            this.descriptionBase = configuration.getDescriptionBase();
            this.signatureFieldName = configuration.getSignatureFieldName();
            this.materialDescriptionFieldName = configuration.getMaterialDescriptionFieldName();
            this.encryptionContextOverrideOperator = configuration.getEncryptionContextOverrideOperator();
            this.encryptionMaterialsProvider = configuration.getEncryptionMaterialsProvider();
            this.encryptionContext = configuration.getEncryptionContext();
            this.attributeEncryptionActionOverrides = Optional.ofNullable(
                    configuration.getAttributeEncryptionActionOverrides())
                    .map(HashMap::new)
                    .orElse(null);
            this.defaultAttributeEncryptionAction = configuration.getDefaultAttributeEncryptionAction();
        }

        @Override
        public B withSignatureFieldName(String signatureFieldName) {
            this.signatureFieldName = signatureFieldName;
            return (B) this;
        }

        @Override
        public B withMaterialDescriptionFieldName(String materialDescriptionFieldName) {
            this.materialDescriptionFieldName = materialDescriptionFieldName;
            return (B) this;
        }

        @Override
        public B withDescriptionBase(String descriptionBase) {
            this.descriptionBase = descriptionBase;
            return (B) this;
        }

        @Override
        public B withEncryptionContextOverrideOperator(Function<T, T> encryptionContextOverrideOperator) {
            this.encryptionContextOverrideOperator = encryptionContextOverrideOperator;
            return (B) this;
        }

        @Override
        public B withEncryptionMaterialsProvider(M encryptionMaterialsProvider) {
            this.encryptionMaterialsProvider = encryptionMaterialsProvider;
            return (B) this;
        }

        @Override
        public B withEncryptionContext(T encryptionContext) {
            this.encryptionContext = encryptionContext;
            return (B) this;
        }

        @Override
        public B withAttributeEncryptionActionOverrides(final Map<String, AttributeEncryptionAction> encryptionActionOverrides) {
            this.attributeEncryptionActionOverrides = new HashMap<>(encryptionActionOverrides);
            return (B) this;
        }

        @Override
        public B withDefaultAttributeEncryptionAction(final AttributeEncryptionAction defaultAttributeEncryptionAction) {
            Preconditions.assertNotNull(defaultAttributeEncryptionAction, "defaultAttributeEncryptionAction must be non-null");
            this.defaultAttributeEncryptionAction = defaultAttributeEncryptionAction;
            return (B) this;
        }

        // FIXME: either refactor the interface
        // certainly remove a null build
        @Override
        public C build() { return null; }
    }
}
