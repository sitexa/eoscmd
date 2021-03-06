package com.sitexa.eoscmd.data.remote.model.api;

import com.google.gson.annotations.Expose;
import com.sitexa.eoscmd.data.remote.model.types.TypeAccountName;

public class GetBalanceRequest extends GetRequestForCurrency {

    @Expose
    private TypeAccountName account;

    public GetBalanceRequest(String tokenContract, String account, String symbol) {
        super(tokenContract, symbol);
        this.account = new TypeAccountName(account);
    }
}
