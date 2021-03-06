package com.sitexa.eoscmd.data.remote.model.abi;

import com.google.gson.annotations.Expose;

public class EosAbiAction {
    @Expose
    public String name;

    @Expose
    public String type;

    @Override
    public String toString() {
        return "EosAction: " + name + ", type: " + type;
    }
}
