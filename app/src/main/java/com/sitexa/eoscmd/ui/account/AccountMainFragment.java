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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sitexa.eoscmd.R;
import com.sitexa.eoscmd.di.component.ActivityComponent;
import com.sitexa.eoscmd.ui.account.create.CreateEosAccountDialog;
import com.sitexa.eoscmd.ui.account.info.AccountInfoType;
import com.sitexa.eoscmd.ui.account.info.InputAccountDialog;
import com.sitexa.eoscmd.ui.base.BaseFragment;
import com.sitexa.eoscmd.ui.result.ShowResultDialog;

import javax.inject.Inject;

public class AccountMainFragment extends BaseFragment
        implements AccountMainMvpView {

    @Inject
    AccountMainPresenter mPresenter;


    public static AccountMainFragment newInstance() {
        Bundle args = new Bundle();
        AccountMainFragment fragment = new AccountMainFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        ActivityComponent component = getActivityComponent();
        if (component != null) {
            component.inject(this);

            mPresenter.attachView(this);
        }

        return view;
    }


    @Override
    protected void setUpView(View view) {
        //  create account
        view.findViewById(R.id.btn_command_create_account).setOnClickListener(v -> mPresenter.onClickCreateAccount());

        // eosc get account <account>
        view.findViewById(R.id.btn_get_account).setOnClickListener(v -> openInputAccountDialog(AccountInfoType.REGISTRATION));

        // eosc get transaction <account>
        view.findViewById(R.id.btn_get_transaction).setOnClickListener(v -> openInputAccountDialog(AccountInfoType.TRANSACTIONS));

        // eosc get servants <account>
        view.findViewById(R.id.btn_get_servants).setOnClickListener(v -> openInputAccountDialog(AccountInfoType.SERVANTS));
    }

    @Override
    public void onDestroyView() {
        mPresenter.detachView();
        super.onDestroyView();
    }

    @Override
    public void openCreateAccountDialog() {
        CreateEosAccountDialog.newInstance()
                .show(getChildFragmentManager());
    }


    private void openInputAccountDialog(AccountInfoType infoType) {
        InputAccountDialog.newInstance(infoType)
                .setCallback((account, retInfoType) -> mPresenter.loadAccountInfo(account, retInfoType))
                .show(getChildFragmentManager());
    }


    @Override
    public void showAccountInfo(int titleRscId, String account, String info, String statusInfo) {
        String title = String.format(getString(R.string.account_info_title_fmt), getString(titleRscId), account);

        ShowResultDialog.newInstance(title, info, statusInfo).show(getChildFragmentManager());
    }
}