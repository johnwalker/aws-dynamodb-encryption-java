package com.amazonaws.services.dynamodbv2.datamodeling.encryption.configuration;

/**
 * AttributeEncryptionAction is a class used to decide what to do with each attribute
 */
public enum AttributeEncryptionAction {
    /**
     * Encrypt and sign the attribute
     */
    ENCRYPT_AND_SIGN,
    /**
     * Encrypt, but don't sign the attribute
     */
    ENCRYPT,
    /**
     * Sign, but don't encrypt the attribute
     */
    SIGN,
    /**
     * Don't encrypt or sign the attribute
     */
    DO_NOTHING
}
