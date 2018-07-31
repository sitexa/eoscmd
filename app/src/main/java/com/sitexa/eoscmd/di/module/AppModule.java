package com.sitexa.eoscmd.di.module;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sitexa.eoscmd.data.local.db.AppDatabase;
import com.sitexa.eoscmd.data.local.repository.EosAccountRepository;
import com.sitexa.eoscmd.data.local.repository.EosAccountRepositoryImpl;
import com.sitexa.eoscmd.data.prefs.PreferencesHelper;
import com.sitexa.eoscmd.data.remote.HostInterceptor;
import com.sitexa.eoscmd.data.remote.NodeosApi;
import com.sitexa.eoscmd.data.util.GsonEosTypeAdapterFactory;
import com.sitexa.eoscmd.data.wallet.EosWalletManager;
import com.sitexa.eoscmd.di.ApplicationContext;
import com.sitexa.eoscmd.util.RefValue;
import com.sitexa.eoscmd.util.StringUtils;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class AppModule {

    private static final String ENDPOINT = "http://testnet1.eos.io";
    private final Application mApp;

    public AppModule(Application application) {
        mApp = application;
    }

    @Provides
    Application provideApp() {
        return mApp;
    }

    @Provides
    @ApplicationContext
    Context provideAppContext() {
        return mApp;
    }

    @Provides
    @Singleton
    HostInterceptor providesHostInterceptor() {
        return new HostInterceptor();
    }

    @Provides
    @Singleton
    OkHttpClient providesOkHttpClient(HostInterceptor interceptor) {

        return new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();
    }

    @Provides
    @Singleton
    Gson providesGson() {
        return new GsonBuilder()
                .registerTypeAdapterFactory(new GsonEosTypeAdapterFactory())
                .serializeNulls()
                .excludeFieldsWithoutExposeAnnotation().create();
    }

    @Provides
    @Singleton
    NodeosApi providesEosService(Gson gson, OkHttpClient okHttpClient, PreferencesHelper preferencesHelper) {
        RefValue<Integer> portRef = new RefValue<>(0);
        String addr = preferencesHelper.getNodeosConnInfo(portRef);

        String url = StringUtils.isEmpty(addr) ? ENDPOINT : ("http://" + addr + ":" + portRef.data);


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // retrofit 용 rxjava2 adapter
                .client(okHttpClient)
                .build();

        return retrofit.create(NodeosApi.class);
    }

    @Provides
    @Singleton
    EosWalletManager providesWalletManager() {
        return new EosWalletManager();
    }

    @Provides
    @Singleton
    AppDatabase provideAppDatabase(@ApplicationContext Context context) {
        return Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "eosc.db")
                .build();
    }

    @Provides
    @Singleton
    EosAccountRepository provideAccountRepository(AppDatabase database) {
        return new EosAccountRepositoryImpl(database);
    }
}
