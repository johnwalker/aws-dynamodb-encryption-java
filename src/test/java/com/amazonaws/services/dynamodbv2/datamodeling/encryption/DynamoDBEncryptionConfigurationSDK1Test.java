package com.amazonaws.services.dynamodbv2.datamodeling.encryption;

import com.amazonaws.services.dynamodbv2.datamodeling.encryption.materials.DecryptionMaterials;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.materials.EncryptionMaterials;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.providers.EncryptionMaterialsProvider;
import org.junit.Test;

import java.util.function.UnaryOperator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class DynamoDBEncryptionConfigurationSDK1Test {

    EncryptionMaterialsProvider encryptionMaterialsProviderStub = new EncryptionMaterialsProvider() {
        @Override
        public DecryptionMaterials getDecryptionMaterials(EncryptionContext context) {
            return null;
        }

        @Override
        public EncryptionMaterials getEncryptionMaterials(EncryptionContext context) {
            return null;
        }

        @Override
        public void refresh() {

        }
    };

    UnaryOperator<EncryptionContext> encryptionContextTransformerStub = (EncryptionContext t) -> t;

    @Test
    public void testDefaultConfiguration() {
        DynamoDBEncryptionConfigurationSDK1 configuration = DynamoDBEncryptionConfigurationSDK1.builder().build();
        assertEquals("*amzn-ddb-map-desc*", configuration.getMaterialDescriptionFieldName());
        assertEquals("amzn-ddb-map-", configuration.getDescriptionBase());
        assertEquals("amzn-ddb-map-sym-mode", configuration.getSymModeHeader());
        assertEquals("amzn-ddb-map-signingAlg", configuration.getSigningAlgorithmHeader());
        assertNull(configuration.getEncryptionContextTransformer());
        assertNull(configuration.getEncryptionMaterialsProvider());

    }

    @Test
    public void testDescriptionBase() {
        DynamoDBEncryptionConfigurationSDK1 configuration = DynamoDBEncryptionConfigurationSDK1
                .builder()
                .withDescriptionBase("prefix-")
                .build();

        assertEquals("prefix-", configuration.getDescriptionBase());
        assertEquals("prefix-sym-mode", configuration.getSymModeHeader());
        assertEquals("prefix-signingAlg", configuration.getSigningAlgorithmHeader());
        assertEquals("*amzn-ddb-map-desc*", configuration.getMaterialDescriptionFieldName());
    }

    @Test
    public void testSignatureFieldName() {
        String overriddenfieldname = "overriddenfieldname";
        DynamoDBEncryptionConfigurationSDK1 configuration = DynamoDBEncryptionConfigurationSDK1.builder().withSignatureFieldName(overriddenfieldname).build();

        assertEquals(overriddenfieldname, configuration.getSignatureFieldName());
    }

    @Test
    public void testAllConfigurationBuilderOptions() {
        DynamoDBEncryptionConfigurationSDK1 configuration = DynamoDBEncryptionConfigurationSDK1.builder()
                .withDescriptionBase("prefix-")
                .withMaterialDescriptionFieldName("bobbydroptables")
                .withSignatureFieldName("signaturename")
                .withEncryptionMaterialsProvider(encryptionMaterialsProviderStub)
                .withEncryptionContextTransformer(encryptionContextTransformerStub)
                .build();


        assertEquals("prefix-", configuration.getDescriptionBase());
        assertEquals("bobbydroptables", configuration.getMaterialDescriptionFieldName());
        assertEquals("signaturename", configuration.getSignatureFieldName());
        assertEquals(encryptionMaterialsProviderStub, configuration.getEncryptionMaterialsProvider());
        assertEquals(encryptionContextTransformerStub, configuration.getEncryptionContextTransformer());

    }

    @Test
    public void testToBuilderCopiesFieldsFromOriginalConfigurationFile() {
        // Setup the original configuration
        DynamoDBEncryptionConfigurationSDK1 configuration = DynamoDBEncryptionConfigurationSDK1.builder()
                .withDescriptionBase("prefix-")
                .withMaterialDescriptionFieldName("bobbydroptables")
                .withSignatureFieldName("signaturename")
                .withEncryptionMaterialsProvider(encryptionMaterialsProviderStub)
                .withEncryptionContextTransformer(encryptionContextTransformerStub)
                .build();

        DynamoDBEncryptionConfigurationSDK1 newlybuilt = configuration.toBuilder().build();

        assertEquals("prefix-", newlybuilt.getDescriptionBase());
        assertEquals("bobbydroptables", newlybuilt.getMaterialDescriptionFieldName());
        assertEquals("signaturename", newlybuilt.getSignatureFieldName());
        assertEquals(encryptionMaterialsProviderStub, newlybuilt.getEncryptionMaterialsProvider());
        assertEquals(encryptionContextTransformerStub, newlybuilt.getEncryptionContextTransformer());
    }

}
