package com.amazonaws.services.dynamodbv2.datamodeling.encryption;

import com.amazonaws.services.dynamodbv2.datamodeling.encryption.internal.InternalEncryptionMaterialsProvider;

public interface DynamoDBEncryptionConfiguration<T, U extends InternalEncryptionMaterialsProvider<T>> {
    /**
     * Get the name of the DynamoDB field used to store the signature.
     * Defaults to {@value EncryptionConstants#DEFAULT_SIGNATURE_FIELD}.
     *
     * @return the name of the DynamoDB field used to store the signature
     */
    String getSignatureFieldName();

    /**
     * Set the name of the DynamoDB field used to store the signature.
     * Defaults to {@value EncryptionConstants#DEFAULT_SIGNING_ALGORITHM_HEADER}
     *
     * @param signatureFieldName
     */

    /**
     * Get the name of the DynamoDB field used to store metadata used by the
     * DynamoDBEncryptedMapper. Defaults to {@value EncryptionConstants#DEFAULT_METADATA_FIELD}.
     *
     * @return the name of the DynamoDB field used to store metadata used by the
     *         DynamoDBEncryptedMapper
     */
    String getMaterialDescriptionFieldName();

    /**
     * Set the name of the DynamoDB field used to store metadata used by the
     * DynamoDBEncryptedMapper
     *
     * @param materialDescriptionFieldName
     */

    String getSigningAlgorithmHeader();
    String getSymModeHeader();

    /**
     * Get the name of the DynamoDB field used to store the signature.
     * Defaults to {@value EncryptionConstants#DEFAULT_SIGNATURE_FIELD}.
     *
     * @return the name of the DynamoDB field used to store the signature
     */
    String getDescriptionBase();

    /**
     * Set the name of the DynamoDB field used to store the signature.
     * Defaults to {@value EncryptionConstants#DEFAULT_SIGNING_ALGORITHM_HEADER}
     *
     * @param signatureFieldName
     */


    Transformer<T> getEncryptionContextTransformer();


    U getEncryptionMaterialsProvider();
}
