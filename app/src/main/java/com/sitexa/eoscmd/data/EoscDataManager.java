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
package com.sitexa.eoscmd.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.sitexa.eoscmd.crypto.ec.EosPrivateKey;
import com.sitexa.eoscmd.crypto.ec.EosPublicKey;
import com.sitexa.eoscmd.data.local.repository.EosAccountRepository;
import com.sitexa.eoscmd.data.prefs.PreferencesHelper;
import com.sitexa.eoscmd.data.remote.NodeosApi;
import com.sitexa.eoscmd.data.remote.model.abi.EosAbiMain;
import com.sitexa.eoscmd.data.remote.model.api.AccountInfoRequest;
import com.sitexa.eoscmd.data.remote.model.api.EosChainInfo;
import com.sitexa.eoscmd.data.remote.model.api.GetBalanceRequest;
import com.sitexa.eoscmd.data.remote.model.api.GetCodeRequest;
import com.sitexa.eoscmd.data.remote.model.api.GetRequestForCurrency;
import com.sitexa.eoscmd.data.remote.model.api.GetRequiredKeys;
import com.sitexa.eoscmd.data.remote.model.api.GetTableRequest;
import com.sitexa.eoscmd.data.remote.model.api.JsonToBinRequest;
import com.sitexa.eoscmd.data.remote.model.api.PushTxnResponse;
import com.sitexa.eoscmd.data.remote.model.chain.Action;
import com.sitexa.eoscmd.data.remote.model.chain.PackedTransaction;
import com.sitexa.eoscmd.data.remote.model.chain.SignedTransaction;
import com.sitexa.eoscmd.data.remote.model.types.EosNewAccount;
import com.sitexa.eoscmd.data.remote.model.types.EosTransfer;
import com.sitexa.eoscmd.data.remote.model.types.TypeAccountName;
import com.sitexa.eoscmd.data.remote.model.types.TypeAsset;
import com.sitexa.eoscmd.data.remote.model.types.TypeChainId;
import com.sitexa.eoscmd.data.remote.model.types.TypePublicKey;
import com.sitexa.eoscmd.data.wallet.EosWalletManager;
import com.sitexa.eoscmd.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;

import static com.sitexa.eoscmd.util.Consts.EOSIO_SYSTEM_ACCOUNT;
import static com.sitexa.eoscmd.util.Consts.EOSIO_TOKEN_CONTRACT;
import static com.sitexa.eoscmd.util.Consts.TX_EXPIRATION_IN_MILSEC;

@Singleton
public class EoscDataManager {

    private final NodeosApi mNodeosApi;
    private final PreferencesHelper mPrefHelper;
    private final EosWalletManager mWalletMgr;
    private final EosAccountRepository mAccountRepository;
    EosChainInfo currentBlockInfo;
    private HashMap<String, EosAbiMain> mAbiObjHouse;

    @Inject
    public EoscDataManager(NodeosApi nodeosApi, EosWalletManager walletManager, EosAccountRepository accountRepository, PreferencesHelper prefHelper) {
        mNodeosApi = nodeosApi;
        mWalletMgr = walletManager;
        mAccountRepository = accountRepository;
        mPrefHelper = prefHelper;

        mWalletMgr.setDir(mPrefHelper.getWalletDirFile());
        mWalletMgr.openExistingsInDir();

        mAbiObjHouse = new HashMap<>();
    }

    public EosWalletManager getWalletManager() {
        return mWalletMgr;
    }

    public PreferencesHelper getPreferenceHelper() {
        return mPrefHelper;
    }

    public void addAccountHistory(String... accountNames) {
        mAccountRepository.addAll(accountNames);
    }

    public void addAccountHistory(List<String> accountNames) {
        mAccountRepository.addAll(accountNames);
    }

    public void deleteAccountHistory(String accountName) {
        mAccountRepository.delete(accountName);
    }

    public List<String> searchAccount(String nameStarts) {
        return mAccountRepository.searchName(nameStarts);
    }

    public void pushAbiObject(String key, EosAbiMain abi) {
        mAbiObjHouse.put(key, abi);
    }

    public EosAbiMain popAbiObject(String key) {
        return mAbiObjHouse.remove(key);
    }

    public void clearAbiObjects() {
        mAbiObjHouse.clear();
    }

    public Observable<EosChainInfo> getChainInfo() {

        return mNodeosApi.readInfo("get_info");
    }

    public Observable<String> getTable(String accountName, String code, String table,
                                       String tableKey, String lowerBound, String upperBound, int limit) {
        return mNodeosApi.getTable(
                new GetTableRequest(accountName, code, table, tableKey, lowerBound, upperBound, limit))
                .map(tableResult -> Utils.prettyPrintJson(tableResult));
    }

    public Observable<EosPrivateKey[]> createKey(int count) {
        return Observable.fromCallable(() -> {
            EosPrivateKey[] retKeys = new EosPrivateKey[count];
            for (int i = 0; i < count; i++) {
                retKeys[i] = new EosPrivateKey();
            }

            return retKeys;
        });
    }

    private SignedTransaction createTransaction(String contract, String actionName, String dataAsHex,
                                                String[] permissions, EosChainInfo chainInfo) {
        currentBlockInfo = chainInfo;
        Action action = new Action(contract, actionName);
        action.setAuthorization(permissions);
        action.setData(dataAsHex);

        SignedTransaction txn = new SignedTransaction();
        txn.addAction(action);
        txn.putSignatures(new ArrayList<>());


        if (null != chainInfo) {
            txn.setReferenceBlock(chainInfo.getHeadBlockId());
            txn.setExpiration(chainInfo.getTimeAfterHeadBlockTime(TX_EXPIRATION_IN_MILSEC));
        }

        return txn;
    }

    private SignedTransaction createTransaction(List<Action> actions, EosChainInfo chainInfo) {
        currentBlockInfo = chainInfo;
        SignedTransaction txn = new SignedTransaction();
        txn.setActions(actions);
        txn.putSignatures(new ArrayList<>());


        if (null != chainInfo) {
            txn.setReferenceBlock(chainInfo.getHeadBlockId());
            txn.setExpiration(chainInfo.getTimeAfterHeadBlockTime(TX_EXPIRATION_IN_MILSEC));
        }

        return txn;
    }

    private Observable<PackedTransaction> signAndPackTransaction(SignedTransaction txnBeforeSign) {

        return mNodeosApi.getRequiredKeys(new GetRequiredKeys(txnBeforeSign, mWalletMgr.listPubKeys()))
                .map(keys -> {
                    final SignedTransaction stxn;
                    if (mPrefHelper.shouldSkipSigning()) {
                        stxn = txnBeforeSign;
                    } else {
                        stxn = mWalletMgr.signTransaction(txnBeforeSign, keys.getKeys(), new TypeChainId(currentBlockInfo.getChain_id()));
                    }

                    return new PackedTransaction(stxn);
                });
    }

    private String[] getActivePermission(String accountName) {
        return new String[]{accountName + "@active"};
    }

    public Observable<JsonObject> readAccountInfo(String accountName) {
        return mNodeosApi.getAccountInfo(new AccountInfoRequest(accountName));
    }

    public Observable<JsonObject> getTransactions(String accountName) {

        JsonObject gsonObject = new JsonObject();
        gsonObject.addProperty(NodeosApi.GET_TRANSACTIONS_KEY, accountName);

        return mNodeosApi.getAccountHistory(NodeosApi.ACCOUNT_HISTORY_GET_TRANSACTIONS, gsonObject);
    }

    public Observable<JsonObject> getServants(String accountName) {

        JsonObject gsonObject = new JsonObject();
        gsonObject.addProperty(NodeosApi.GET_SERVANTS_KEY, accountName);

        return mNodeosApi.getAccountHistory(NodeosApi.ACCOUNT_HISTORY_GET_SERVANTS, gsonObject);
    }

    void setInfo(EosChainInfo info) {
        currentBlockInfo = info;
    }

    public Observable<PushTxnResponse> createAccount(EosNewAccount newAccountData) {
        return getChainInfo()
                .map(info -> createTransaction(EOSIO_SYSTEM_ACCOUNT, newAccountData.getActionName(), newAccountData.getAsHex()
                        , getActivePermission(newAccountData.getCreatorName()), info))
                .flatMap(txn -> signAndPackTransaction(txn))
                .flatMap(packedTxn -> mNodeosApi.pushTransaction(packedTxn));
    }

    public Observable<Action> createAccountAction(String creator, String newAccount, EosPublicKey ownerKey, EosPublicKey activeKey) {
        EosNewAccount newAccountData = new EosNewAccount(creator, newAccount
                , TypePublicKey.from(ownerKey), TypePublicKey.from(activeKey));


        Action action = new Action(EOSIO_SYSTEM_ACCOUNT, newAccountData.getActionName());
        action.setAuthorization(getActivePermission(newAccountData.getCreatorName()));
        action.setData(newAccountData.getAsHex());

        return Observable.just(action);
    }


    private Observable<Action> getActionAfterBindArgs(String contract, String permissionAccount, String actionName, String args) {
        return mNodeosApi.jsonToBin(new JsonToBinRequest(contract, actionName, args))
                .map(binResp -> {
                    Action action = new Action(contract, actionName);
                    action.setAuthorization(getActivePermission(permissionAccount));
                    action.setData(binResp.getBinargs());

                    return action;
                });
    }

    public Observable<Action> buyRamInAssetAction(String payer, String receiver, String assetQuantity) {
        JsonObject object = new JsonObject();
        object.addProperty("payer", new TypeAccountName(payer).toString());
        object.addProperty("receiver", new TypeAccountName(receiver).toString());
        object.addProperty("quant", new TypeAsset(assetQuantity).toString());

        return getActionAfterBindArgs(EOSIO_SYSTEM_ACCOUNT, payer, "buyram", new Gson().toJson(object));
    }


    public Observable<Action> delegateAction(String from, String receiver, String networkAsset, String cpuAsset, boolean transfer) {
        JsonObject object = new JsonObject();
        object.addProperty("from", new TypeAccountName(from).toString());
        object.addProperty("receiver", new TypeAccountName(receiver).toString());
        object.addProperty("stake_net_quantity", new TypeAsset(networkAsset).toString());
        object.addProperty("stake_cpu_quantity", new TypeAsset(cpuAsset).toString());
        object.addProperty("transfer", transfer);

        return getActionAfterBindArgs(EOSIO_SYSTEM_ACCOUNT, from, "delegatebw", new Gson().toJson(object));
    }

    public Observable<JsonObject> transfer(String from, String to, long amount, String memo) {

        EosTransfer transfer = new EosTransfer(from, to, amount, memo);

        return pushActionRetJson(EOSIO_TOKEN_CONTRACT, transfer.getActionName(), Utils.prettyPrintJson(transfer), getActivePermission(from)); //transfer.getAsHex()
    }

    public Observable<JsonObject> pushActionRetJson(String contract, String action, String data, String[] permissions) {
        return mNodeosApi.jsonToBin(new JsonToBinRequest(contract, action, data))
                .flatMap(jsonToBinResp -> getChainInfo()
                        .map(info -> createTransaction(contract, action, jsonToBinResp.getBinargs(), permissions, info)))
                .flatMap(this::signAndPackTransaction)
                .flatMap(mNodeosApi::pushTransactionRetJson);
    }

    public Observable<PushTxnResponse> pushAction(String contract, String action, String data, String[] permissions) {
        return mNodeosApi.jsonToBin(new JsonToBinRequest(contract, action, data))
                .flatMap(jsonToBinResp -> getChainInfo()
                        .map(info -> createTransaction(contract, action, jsonToBinResp.getBinargs(), permissions, info)))
                .flatMap(this::signAndPackTransaction)
                .flatMap(mNodeosApi::pushTransaction);
    }


    public Observable<PushTxnResponse> pushActions(List<Action> actions) {
        return getChainInfo()
                .map(info -> createTransaction(actions, info))
                .flatMap(this::signAndPackTransaction)
                .flatMap(mNodeosApi::pushTransaction);
    }

    public Observable<EosAbiMain> getCodeAbi(String contract) {
        return mNodeosApi.getCode(new GetCodeRequest(contract))
                .filter(codeResp -> codeResp.isValidCode())
                .map(result -> new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
                        .create().fromJson(result.getAbi(), EosAbiMain.class));
    }

    public Observable<EosAbiMain> getAbiMainFromJson(String jsonStr) {
        return Observable.just(new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
                .create().fromJson(jsonStr, EosAbiMain.class));
    }

    public Observable<String> getCurrencyBalance(String contract, String account, String symbol) {
        return mNodeosApi.getCurrencyBalance(new GetBalanceRequest(contract, account, symbol))
                .map(result -> Utils.prettyPrintJson(result));
    }

    public Observable<String> getCurrencyStats(String contract, String symbol) {
        return mNodeosApi.getCurrencyStats(new GetRequestForCurrency(contract, symbol))
                .map(result -> Utils.prettyPrintJson(result));
    }


}
