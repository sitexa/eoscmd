package com.sitexa.eoscmd.data.remote.model.chain;

import com.google.gson.annotations.Expose;
import com.sitexa.eoscmd.data.remote.model.types.TypeAccountName;
import com.sitexa.eoscmd.data.remote.model.types.TypeScopeName;


public class DataAccessInfo {
    //public enum Type { read, write };

    @Expose
    private String type; // access type

    @Expose
    private TypeAccountName code;

    @Expose
    private TypeScopeName scope;

    @Expose
    private long sequence; // uint64_t
}
