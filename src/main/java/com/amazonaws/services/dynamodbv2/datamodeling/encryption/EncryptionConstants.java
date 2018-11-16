/*
 * Copyright 2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.amazonaws.services.dynamodbv2.datamodeling.encryption;

public class EncryptionConstants {
    /**
     * A helper value used to form the key of the signing algorithm header in combination with the description base.
     */
    public static final String HELPER_CONSTANT_SIGNING_ALG = "signingAlg";
    /**
     * A helper value used to form the symmetric mode header in combination with the description base.
     */
    public static final String HELPER_CONSTANT_SYM_MODE = "sym-mode";

    public static final String DEFAULT_METADATA_FIELD = "*amzn-ddb-map-desc*";
    /**
     * The name of the field that contains the signature of the record
     */
    public static final String DEFAULT_SIGNATURE_FIELD = "*amzn-ddb-map-sig*";
    /**
     * The prefix used in combination with the signing algorithm and symmetric mode header
     */
    public static final String DEFAULT_DESCRIPTION_BASE = "amzn-ddb-map-"; // Same as the Mapper
    /**
     * The default value for the signing algorithm header
     */
    public static final String DEFAULT_SIGNING_ALGORITHM_HEADER = DEFAULT_DESCRIPTION_BASE + HELPER_CONSTANT_SIGNING_ALG;
    /**
     * The default value for the symmetric mode header
     */
    public static final String DEFAULT_SYM_MODE_HEADER = DEFAULT_DESCRIPTION_BASE + HELPER_CONSTANT_SYM_MODE;
}
