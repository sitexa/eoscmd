package com.sitexa.eoscmd.di.component;

import com.sitexa.eoscmd.di.PerActivity;
import com.sitexa.eoscmd.di.module.ActivityModule;
import com.sitexa.eoscmd.ui.MainActivity;
import com.sitexa.eoscmd.ui.account.AccountMainFragment;
import com.sitexa.eoscmd.ui.account.create.CreateEosAccountDialog;
import com.sitexa.eoscmd.ui.account.info.InputAccountDialog;
import com.sitexa.eoscmd.ui.currency.CurrencyFragment;
import com.sitexa.eoscmd.ui.gettable.GetTableFragment;
import com.sitexa.eoscmd.ui.push.PushFragment;
import com.sitexa.eoscmd.ui.push.abiview.MsgInputActivity;
import com.sitexa.eoscmd.ui.settings.SettingsActivity;
import com.sitexa.eoscmd.ui.transfer.TransferFragment;
import com.sitexa.eoscmd.ui.wallet.WalletFragment;
import com.sitexa.eoscmd.ui.wallet.dlg.CreateWalletDialog;
import com.sitexa.eoscmd.ui.wallet.dlg.InputDataDialog;

import dagger.Component;

@PerActivity
@Component(dependencies = AppComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(MainActivity activity);

    void inject(SettingsActivity activity);

    void inject(AccountMainFragment fragment);

    void inject(CreateEosAccountDialog dialog);

    void inject(WalletFragment fragment);

    void inject(PushFragment fragment);

    void inject(GetTableFragment fragment);

    void inject(CurrencyFragment fragment);

    void inject(TransferFragment fragment);

    void inject(InputDataDialog dialog);

    void inject(CreateWalletDialog dialog);

    void inject(InputAccountDialog dialog);

    void inject(MsgInputActivity activity);
}
