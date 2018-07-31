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
package com.sitexa.eoscmd.ui.account;

import com.google.gson.JsonObject;
import com.sitexa.eoscmd.R;
import com.sitexa.eoscmd.data.EoscDataManager;
import com.sitexa.eoscmd.ui.account.info.AccountInfoType;
import com.sitexa.eoscmd.ui.base.BasePresenter;
import com.sitexa.eoscmd.ui.base.RxCallbackWrapper;
import com.sitexa.eoscmd.util.StringUtils;
import com.sitexa.eoscmd.util.Utils;

import javax.inject.Inject;

import io.reactivex.Completable;

public class AccountMainPresenter extends BasePresenter<AccountMainMvpView> {

    @Inject
    EoscDataManager mDataManager;

    @Inject
    public AccountMainPresenter() {
    }

    public void onClickCreateAccount() {
        // check any opened wallet
        if (mDataManager.getWalletManager().listWallets(false).size() <= 0) {
            getMvpView().showToast(R.string.no_wallet_unlocked_or_selected);
            return;
        }

        getMvpView().openCreateAccountDialog();
    }

    public void loadAccountInfo(String account, AccountInfoType infoType) {
        if (StringUtils.isEmpty(account)) {
            return;
        }

        switch (infoType) {
            case REGISTRATION:
                getRegistrationInfo(account);
                break;

            case TRANSACTIONS:
                getTransactions(account);
                break;

            case SERVANTS:
                getServents(account);
                break;
        }
    }

    private void showResult(int titleId, String account, String result) {
        addDisposable(
                Completable.fromAction(() -> mDataManager.addAccountHistory(account))
                        .subscribeOn(getSchedulerProvider().io())
                        .observeOn(getSchedulerProvider().ui())
                        .doOnComplete(() -> getMvpView().showAccountInfo(titleId, account, result, null))
                        .subscribe()
        );
    }

    private void getRegistrationInfo(String account) {
        getMvpView().showLoading(true);

        addDisposable(mDataManager
                .readAccountInfo(account)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribeWith(new RxCallbackWrapper<JsonObject>(this) {
                    @Override
                    public void onNext(JsonObject result) {
                        if (!isViewAttached()) return;

                        getMvpView().showLoading(false);

                        showResult(AccountInfoType.REGISTRATION.getTitleId(), account, Utils.prettyPrintJson(result));
                        //getMvpView().showAccountInfo( AccountInfoType.REGISTRATION.getTitleId(), account,  Utils.prettyPrintJson( result) );
                    }

                })
        );
    }

    private void getTransactions(String account) {
        getMvpView().showLoading(true);

        addDisposable(mDataManager
                .getTransactions(account)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribeWith(new RxCallbackWrapper<JsonObject>(this) {
                    @Override
                    public void onNext(JsonObject result) {
                        if (!isViewAttached()) return;

                        getMvpView().showLoading(false);

                        showResult(AccountInfoType.TRANSACTIONS.getTitleId(), account, Utils.prettyPrintJson(result));
                        //getMvpView().showAccountInfo( AccountInfoType.TRANSACTIONS.getTitleId(), account, Utils.prettyPrintJson(result ) );
                    }

                })
        );
    }

    private void getServents(String account) {
        getMvpView().showLoading(true);

        addDisposable(mDataManager
                .getServants(account)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribeWith(new RxCallbackWrapper<JsonObject>(this) {
                    @Override
                    public void onNext(JsonObject result) {
                        if (!isViewAttached()) return;

                        getMvpView().showLoading(false);

                        showResult(AccountInfoType.SERVANTS.getTitleId(), account, Utils.prettyPrintJson(result));
                        //getMvpView().showAccountInfo( AccountInfoType.SERVANTS.getTitleId(), account, Utils.prettyPrintJson(result ) );
                    }

                })
        );
    }
}
