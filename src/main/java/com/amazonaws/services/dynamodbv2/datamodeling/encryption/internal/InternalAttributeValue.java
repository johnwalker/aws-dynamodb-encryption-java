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
package com.amazonaws.services.dynamodbv2.datamodeling.encryption.internal;

import java.nio.ByteBuffer;
import java.util.*;

public class InternalAttributeValue {
    private String s;
    private String n;
    private ByteBuffer b;
    private List<String> sS;
    private List<String> nS;
    private List<ByteBuffer> bS;
    private Map<String, InternalAttributeValue> m;
    private List<InternalAttributeValue> l;
    private Boolean nULLValue;
    private Boolean bOOL;

    public InternalAttributeValue() {
    }

    public void setS(String s) {
        this.s = s;
    }

    public InternalAttributeValue withS(String s) {
        this.setS(s);
        return this;
    }

    public String getS() {
        return this.s;
    }

    public void setN(String n) {
        this.n = n;
    }

    public String getN() {
        return this.n;
    }

    public void setB(ByteBuffer b) {
        this.b = b;
    }

    public ByteBuffer getB() {
        return this.b;
    }

    public List<String> getSS() {
        return this.sS;
    }

    public void setSS(Collection<String> sS) {
        if (sS == null) {
            this.sS = null;
        } else {
            this.sS = new ArrayList(sS);
        }
    }

    public List<String> getNS() {
        return this.nS;
    }

    public void setNS(Collection<String> nS) {
        if (nS == null) {
            this.nS = null;
        } else {
            this.nS = new ArrayList(nS);
        }
    }

    public List<ByteBuffer> getBS() {
        return this.bS;
    }

    public void setBS(Collection<ByteBuffer> bS) {
        if (bS == null) {
            this.bS = null;
        } else {
            this.bS = new ArrayList(bS);
        }
    }

    public Map<String, InternalAttributeValue> getM() {
        return this.m;
    }

    public void setM(Map<String, InternalAttributeValue> m) {
        this.m = m;
    }

    public List<InternalAttributeValue> getL() {
        return this.l;
    }

    public void setL(Collection<InternalAttributeValue> l) {
        if (l == null) {
            this.l = null;
        } else {
            this.l = new ArrayList(l);
        }
    }

    public void setNULL(Boolean nULLValue) {
        this.nULLValue = nULLValue;
    }

    public Boolean getNULL() {
        return this.nULLValue;
    }

    public Boolean isNULL() {
        return this.nULLValue;
    }

    public void setBOOL(Boolean bOOL) {
        this.bOOL = bOOL;
    }

    public Boolean getBOOL() {
        return this.bOOL;
    }

    public Boolean isBOOL() {
        return this.bOOL;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getS() != null) {
            sb.append("S: ").append(this.getS()).append(",");
        }

        if (this.getN() != null) {
            sb.append("N: ").append(this.getN()).append(",");
        }

        if (this.getB() != null) {
            sb.append("B: ").append(this.getB()).append(",");
        }

        if (this.getSS() != null) {
            sb.append("SS: ").append(this.getSS()).append(",");
        }

        if (this.getNS() != null) {
            sb.append("NS: ").append(this.getNS()).append(",");
        }

        if (this.getBS() != null) {
            sb.append("BS: ").append(this.getBS()).append(",");
        }

        if (this.getM() != null) {
            sb.append("M: ").append(this.getM()).append(",");
        }

        if (this.getL() != null) {
            sb.append("L: ").append(this.getL()).append(",");
        }

        if (this.getNULL() != null) {
            sb.append("NULL: ").append(this.getNULL()).append(",");
        }

        if (this.getBOOL() != null) {
            sb.append("BOOL: ").append(this.getBOOL());
        }

        sb.append("}");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InternalAttributeValue that = (InternalAttributeValue) o;
        return Objects.equals(s, that.s) &&
                Objects.equals(n, that.n) &&
                Objects.equals(b, that.b) &&
                Objects.equals(sS, that.sS) &&
                Objects.equals(nS, that.nS) &&
                Objects.equals(bS, that.bS) &&
                Objects.equals(m, that.m) &&
                Objects.equals(l, that.l) &&
                Objects.equals(nULLValue, that.nULLValue) &&
                Objects.equals(bOOL, that.bOOL);
    }

    @Override
    public int hashCode() {
        return Objects.hash(s, n, b, sS, nS, bS, m, l, nULLValue, bOOL);
    }
}
