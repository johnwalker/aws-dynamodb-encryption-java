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
    public static final String HELPER_CONSTANT_SIGNING_ALG = "signingAlg";
    public static final String HELPER_CONSTANT_SYM_MODE = "sym-mode";

    public static final String DEFAULT_METADATA_FIELD = "*amzn-ddb-map-desc*";
    public static final String DEFAULT_SIGNATURE_FIELD = "*amzn-ddb-map-sig*";
    public static final String DEFAULT_DESCRIPTION_BASE = "amzn-ddb-map-"; // Same as the Mapper
    public static final String DEFAULT_SIGNING_ALGORITHM_HEADER = DEFAULT_DESCRIPTION_BASE + HELPER_CONSTANT_SIGNING_ALG;
    public static final String DEFAULT_SYM_MODE_HEADER = DEFAULT_DESCRIPTION_BASE + HELPER_CONSTANT_SYM_MODE;

}
