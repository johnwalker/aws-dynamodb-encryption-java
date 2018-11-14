package com.amazonaws.services.dynamodbv2.datamodeling.encryption;

import com.amazonaws.services.dynamodbv2.datamodeling.encryption.providers.EncryptionMaterialsProvider;

import java.util.function.UnaryOperator;

public interface DynamoDBEncryptionConfigurationSDK1 extends DynamoDBEncryptionConfiguration<EncryptionContext, EncryptionMaterialsProvider> {

    static Builder builder() {
        return new DynamoDBEncryptionConfigurationSDK1Impl.DynamoDBEncryptionConfigurationSDK1ImplBuilder();
    }

    default Builder toBuilder() {
        return new DynamoDBEncryptionConfigurationSDK1Impl.DynamoDBEncryptionConfigurationSDK1ImplBuilder(this);
    }

    interface Builder {
        /**
         * Get the name of the DynamoDB field used to store the signature.
         * Defaults to {@value EncryptionConstants#DEFAULT_SIGNATURE_FIELD}.
         *
         * @return the name of the DynamoDB field used to store the signature
         */
        Builder withSignatureFieldName(String signatureFieldName);

        Builder withMaterialDescriptionFieldName(String descriptionFieldName);

        Builder withDescriptionBase(String descriptionBase);

        Builder withEncryptionContextTransformer(UnaryOperator<EncryptionContext> transformer);

        Builder withEncryptionMaterialsProvider(EncryptionMaterialsProvider encryptionMaterialsProvider);

        DynamoDBEncryptionConfigurationSDK1 build();
    }


}
