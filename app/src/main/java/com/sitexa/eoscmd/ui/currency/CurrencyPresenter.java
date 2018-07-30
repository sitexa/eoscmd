package com.sitexa.eoscmd.ui.currency;

import com.sitexa.eoscmd.data.EoscDataManager;
import com.sitexa.eoscmd.ui.base.BasePresenter;
import com.sitexa.eoscmd.ui.base.RxCallbackWrapper;

import javax.inject.Inject;

/**
 * Created by swapnibble on 2018-04-16.
 */
public class CurrencyPresenter extends BasePresenter<CurrencyMvpView> {
    @Inject
    EoscDataManager mDataManager;

    @Inject
    public CurrencyPresenter() {
    }

    public void onGetBalance(String contract, String account, String symbol) {
        getMvpView().showLoading(true);

        addDisposable(
                mDataManager.getCurrencyBalance(contract, account, symbol)
                        .doOnNext(balanceResult -> mDataManager.addAccountHistory(contract, account))
                        .subscribeOn(getSchedulerProvider().io())
                        .observeOn(getSchedulerProvider().ui())
                        .subscribeWith(new RxCallbackWrapper<String>(this) {
                            @Override
                            public void onNext(String result) {

                                if (!isViewAttached()) return;

                                getMvpView().showLoading(false);

                                getMvpView().showResult(result, null);
                            }
                        })
        );
    }

    public void onGetStats(String contract, String symbol) {
        addDisposable(
                mDataManager.getCurrencyStats(contract, symbol)
                        .doOnNext(balanceResult -> mDataManager.addAccountHistory(contract))
                        .subscribeOn(getSchedulerProvider().io())
                        .observeOn(getSchedulerProvider().ui())
                        .subscribeWith(new RxCallbackWrapper<String>(this) {
                            @Override
                            public void onNext(String result) {

                                if (!isViewAttached()) return;

                                getMvpView().showLoading(false);

                                getMvpView().showResult(result, null);
                            }
                        })
        );
    }
}
