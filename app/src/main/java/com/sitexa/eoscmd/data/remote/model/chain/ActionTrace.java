package com.sitexa.eoscmd.data.remote.model.chain;

import com.google.gson.annotations.Expose;

import java.util.List;


public class ActionTrace {
    @Expose
    public String receiver;

    @Expose
    public boolean context_free;

    @Expose
    public long cpu_usage;

    @Expose
    public Action act;

    @Expose
    public String console;

    @Expose
    public List<DataAccessInfo> data_access;

    @Expose
    public long auths_used; // uint32_t
}
