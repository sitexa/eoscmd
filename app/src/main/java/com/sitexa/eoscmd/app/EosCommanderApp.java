package com.sitexa.eoscmd.app;

import android.app.Application;
import android.content.Context;

import com.sitexa.eoscmd.BuildConfig;
import com.sitexa.eoscmd.data.EoscDataManager;
import com.sitexa.eoscmd.di.component.AppComponent;
import com.sitexa.eoscmd.di.component.DaggerAppComponent;
import com.sitexa.eoscmd.di.module.AppModule;

import javax.inject.Inject;

import timber.log.Timber;

public class EosCommanderApp extends Application {
    @Inject
    EoscDataManager mDataManager;
    private AppComponent mAppComponent;

    public static EosCommanderApp get(Context context) {
        return (EosCommanderApp) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // https://android-developers.googleblog.com/2013/08/some-securerandom-thoughts.html
        PRNGFixes.apply();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        mAppComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();

        mAppComponent.inject(this);
    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }
}
