package com.amazonaws.services.dynamodbv2.datamodeling.encryption.internal;

import com.amazonaws.services.dynamodbv2.datamodeling.encryption.materials.DecryptionMaterials;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.materials.EncryptionMaterials;

public interface InternalEncryptionMaterialsProvider<T> {
    /**
     * Retrieves encryption materials matching the specified description from some source.
     *
     * @param context
     *      Information to assist in selecting a the proper return value. The implementation
     *      is free to determine the minimum necessary for successful processing.
     *
     * @return
     *      The encryption materials that match the description, or null if no matching encryption materials found.
     */
    DecryptionMaterials getDecryptionMaterials(T context);

    /**
     * Returns EncryptionMaterials which the caller can use for encryption.
     * Each implementation of EncryptionMaterialsProvider can choose its own
     * strategy for loading encryption material.  For example, an
     * implementation might load encryption material from an existing key
     * management system, or load new encryption material when keys are
     * rotated.
     *
     * @param context
     *      Information to assist in selecting a the proper return value. The implementation
     *      is free to determine the minimum necessary for successful processing.
     *
     * @return EncryptionMaterials which the caller can use to encrypt or
     * decrypt data.
     */
    EncryptionMaterials getEncryptionMaterials(T context);

    /**
     * Forces this encryption materials provider to refresh its encryption
     * material.  For many implementations of encryption materials provider,
     * this may simply be a no-op, such as any encryption materials provider
     * implementation that vends static/non-changing encryption material.
     * For other implementations that vend different encryption material
     * throughout their lifetime, this method should force the encryption
     * materials provider to refresh its encryption material.
     */
    void refresh();
}
