package com.sitexa.eoscmd.di.component;

import android.app.Application;
import android.content.Context;

import com.sitexa.eoscmd.app.EosCommanderApp;
import com.sitexa.eoscmd.data.EoscDataManager;
import com.sitexa.eoscmd.data.remote.HostInterceptor;
import com.sitexa.eoscmd.di.ApplicationContext;
import com.sitexa.eoscmd.di.module.AppModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {

    void inject(EosCommanderApp eosCommanderApp);

    @ApplicationContext
    Context context();

    Application application();

    EoscDataManager dataManager();

    HostInterceptor hostInterceptor();
}
