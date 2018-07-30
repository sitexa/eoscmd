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
package com.sitexa.eoscmd.ui.settings;

import com.sitexa.eoscmd.R;
import com.sitexa.eoscmd.data.EoscDataManager;
import com.sitexa.eoscmd.data.remote.HostInterceptor;
import com.sitexa.eoscmd.data.remote.model.api.EosChainInfo;
import com.sitexa.eoscmd.ui.base.BasePresenter;
import com.sitexa.eoscmd.ui.base.RxCallbackWrapper;
import com.sitexa.eoscmd.util.RefValue;
import com.sitexa.eoscmd.util.StringUtils;

import javax.inject.Inject;

public class SettingsPresenter extends BasePresenter<SettingsMvpView> {

    private final EoscDataManager mDataManager;
    private final HostInterceptor mHostInterceptor;

    private boolean mConnected;

    @Inject
    public SettingsPresenter(EoscDataManager dataManager, HostInterceptor hostInterceptor) {
        mDataManager = dataManager;
        mHostInterceptor = hostInterceptor;

        mConnected = false;
    }

    @Override
    public void attachView(SettingsMvpView mvpView) {
        super.attachView(mvpView);

        RefValue<Integer> portRef = new RefValue<>(0);

        // fetch connection info from preference
        String host = mDataManager.getPreferenceHelper().getNodeosConnInfo(portRef);
        String port = String.valueOf(portRef.data);
        boolean connInfoOk = false;
        if (validateConnInfo(host, port) > 0) {
            getMvpView().showConnInfo(host, portRef.data);
            connInfoOk = true;
        }

        // get skip signing option
        boolean skipSigning = mDataManager.getPreferenceHelper().shouldSkipSigning();
        getMvpView().showCheckOptions(skipSigning);

        // connect
        if (connInfoOk) {
            tryConnectNodeos(host, port);
        }
    }


    private int validateConnInfo(CharSequence host, CharSequence port) {

        if (StringUtils.isEmpty(host) || StringUtils.isEmpty(port)) {
            return 0;
        }

        int parsedPort;
        try {
            parsedPort = Integer.valueOf(port.toString());
            if ((parsedPort <= 0) || (parsedPort >= 65536)) {
                return -1;
            }

            return parsedPort;
        } catch (NumberFormatException e) {
            //e.printStackTrace();
            return -2;
        }
    }

    private void processConnResult(EosChainInfo info, String host, int port) {

        try {
            if (info != null) {
                mConnected = true;
                mDataManager.getPreferenceHelper().putNodeosConnInfo(host, port);
                getMvpView().showConnStatus(true, info.getBrief());
            } else {
                mConnected = false;
                getMvpView().showConnStatus(false, "");
                getMvpView().onError(R.string.conn_failed);
            }
        } catch (MvpViewNotAttachedException e) {
            e.printStackTrace();
        }
    }

    public void tryConnectNodeos(CharSequence host, CharSequence port) {
        String hostAddress = host.toString();
        int nodeosPort = validateConnInfo(hostAddress, port);
        if (nodeosPort <= 0) {
            getMvpView().onError(R.string.invalid_connection_info);
            getMvpView().showConnStatus(false, null);
            return;
        }

        getMvpView().hideKeyboard();

        // change host info
        mHostInterceptor.setInterceptor("http", hostAddress, nodeosPort);

        getMvpView().showLoading(true);

        addDisposable(
                mDataManager.getChainInfo()
                        .subscribeOn(getSchedulerProvider().io())
                        .observeOn(getSchedulerProvider().ui())
                        .subscribeWith(new RxCallbackWrapper<EosChainInfo>(this) {
                                           @Override
                                           public void onNext(EosChainInfo info) {
                                               if (!isViewAttached()) return;

                                               getMvpView().showLoading(false);
                                               processConnResult(info, hostAddress, nodeosPort);
                                           }
                                       }
                        )
        );
    }

    public void onBackButton() {
        getMvpView().exitWithResult(mConnected);
    }

    public void onChangeIgnoreSignature(boolean skipSigning) {
        mDataManager.getPreferenceHelper().putSkipSigning(skipSigning);
    }
}
