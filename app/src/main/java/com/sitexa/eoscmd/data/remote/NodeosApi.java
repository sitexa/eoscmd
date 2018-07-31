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
package com.sitexa.eoscmd.data.remote;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sitexa.eoscmd.data.remote.model.api.AccountInfoRequest;
import com.sitexa.eoscmd.data.remote.model.api.EosChainInfo;
import com.sitexa.eoscmd.data.remote.model.api.GetBalanceRequest;
import com.sitexa.eoscmd.data.remote.model.api.GetCodeRequest;
import com.sitexa.eoscmd.data.remote.model.api.GetCodeResponse;
import com.sitexa.eoscmd.data.remote.model.api.GetRequestForCurrency;
import com.sitexa.eoscmd.data.remote.model.api.GetRequiredKeys;
import com.sitexa.eoscmd.data.remote.model.api.GetTableRequest;
import com.sitexa.eoscmd.data.remote.model.api.JsonToBinRequest;
import com.sitexa.eoscmd.data.remote.model.api.JsonToBinResponse;
import com.sitexa.eoscmd.data.remote.model.api.PushTxnResponse;
import com.sitexa.eoscmd.data.remote.model.api.RequiredKeysResponse;
import com.sitexa.eoscmd.data.remote.model.chain.PackedTransaction;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface NodeosApi {

    String ACCOUNT_HISTORY_GET_TRANSACTIONS = "get_transactions";
    String GET_TRANSACTIONS_KEY = "account_name";
    String ACCOUNT_HISTORY_GET_SERVANTS = "get_controlled_accounts";
    String GET_SERVANTS_KEY = "controlling_account";
    String ACCOUNT_HISTORY_GET_KEY_ACCOUNTS = "get_key_accounts";

    @POST("/v1/chain/{infoType}")
    Observable<EosChainInfo> readInfo(@Path("infoType") String infoType);

    @POST("/v1/chain/get_account")
    Observable<JsonObject> getAccountInfo(@Body AccountInfoRequest body);

    @POST("/v1/chain/get_table_rows")
    Observable<JsonObject> getTable(@Body GetTableRequest body);

    @POST("/v1/chain/push_transaction")
    Observable<PushTxnResponse> pushTransaction(@Body PackedTransaction body);

    @POST("/v1/chain/push_transaction")
    Observable<JsonObject> pushTransactionRetJson(@Body PackedTransaction body);

    @POST("/v1/chain/get_required_keys")
    Observable<RequiredKeysResponse> getRequiredKeys(@Body GetRequiredKeys body);

    @POST("/v1/chain/get_currency_balance")
    Observable<JsonArray> getCurrencyBalance(@Body GetBalanceRequest body);

    @POST("/v1/chain/get_currency_stats")
    Observable<JsonObject> getCurrencyStats(@Body GetRequestForCurrency body);

    @POST("/v1/chain/abi_json_to_bin")
    Observable<JsonToBinResponse> jsonToBin(@Body JsonToBinRequest body);

    @POST("/v1/chain/get_code")
    Observable<GetCodeResponse> getCode(@Body GetCodeRequest body);

    @POST("/v1/account_history/{history_path}")
    Observable<JsonObject> getAccountHistory(@Path("history_path") String historyPath, @Body JsonObject body);
}
