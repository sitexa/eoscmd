/*
 * Copyright (c) 2018 SITEXA.
 *
 * The MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.sitexa.eoscmd.data.remote.model.types;


import com.sitexa.eoscmd.crypto.digest.Sha256;

public class TypeChainId {
    private final Sha256 mId;

    public TypeChainId() {
        mId = Sha256.ZERO_HASH;
    }

    public TypeChainId(String str) {
        mId = new Sha256(getSha256FromHexStr(str));
    }

    byte[] getSha256FromHexStr(String str) {
        int len = str.length();
        byte[] bytes = new byte[32];
        for (int i = 0; i < len; i += 2) {
            String strIte = str.substring(i, i + 2);
            Integer n = Integer.parseInt(strIte, 16) & 0xFF;
            ;
            bytes[i / 2] = n.byteValue();
        }
        return bytes;
    }

    public byte[] getBytes() {
        return mId.getBytes();
    }
}
