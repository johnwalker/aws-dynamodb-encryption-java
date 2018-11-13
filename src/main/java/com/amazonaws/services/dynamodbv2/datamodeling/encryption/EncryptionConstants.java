package com.amazonaws.services.dynamodbv2.datamodeling.encryption;

public class EncryptionConstants {
    public static final String HELPER_CONSTANT_SIGNING_ALG = "signingAlg";
    public static final String HELPER_CONSTANT_SYM_MODE = "sym-mode";

    public static final String DEFAULT_METADATA_FIELD = "*amzn-ddb-map-desc*";
    public static final String DEFAULT_SIGNATURE_FIELD = "*amzn-ddb-map-sig*";
    public static final String DEFAULT_DESCRIPTION_BASE = "amzn-ddb-map-"; // Same as the Mapper
    public static final String DEFAULT_SIGNING_ALGORITHM_HEADER = DEFAULT_DESCRIPTION_BASE + HELPER_CONSTANT_SIGNING_ALG;
    public static final String DEFAULT_SYM_MODE_HEADER = DEFAULT_DESCRIPTION_BASE + HELPER_CONSTANT_SYM_MODE;

}
