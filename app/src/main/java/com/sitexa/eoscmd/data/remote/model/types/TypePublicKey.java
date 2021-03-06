package com.sitexa.eoscmd.data.remote.model.types;

import com.sitexa.eoscmd.crypto.ec.EosPublicKey;

public class TypePublicKey implements EosType.Packer {
    private static final byte PACK_VAL_CURVE_PARAM_TYPE_K1 = 0;
    private static final byte PACK_VAL_CURVE_PARAM_TYPE_R1 = 1;

    private final EosPublicKey mPubKey;

    public TypePublicKey(EosPublicKey publicKey) {
        mPubKey = publicKey;
    }

    public static TypePublicKey from(EosPublicKey publicKey) {
        return new TypePublicKey(publicKey);
    }

    @Override
    public void pack(EosType.Writer writer) {
        writer.putVariableUInt(mPubKey.isCurveParamK1() ? PACK_VAL_CURVE_PARAM_TYPE_K1 : PACK_VAL_CURVE_PARAM_TYPE_R1);

        writer.putBytes(mPubKey.getBytes());
    }
}
