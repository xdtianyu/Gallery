package org.xdty.gallery.application;

import android.os.StrictMode;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;

import org.xdty.gallery.utils.OkHttp;

import rx.Observable;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class DebugApplication extends Application {
    private final static String TAG = DebugApplication.class.getSimpleName();

    @Override
    public void onCreate() {

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                //.penaltyDeath()
                .build());

        Observable.<Void>just(null).observeOn(Schedulers.io()).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                Stetho.initialize(
                        Stetho.newInitializerBuilder(DebugApplication.this)
                                .enableDumpapp(
                                        Stetho.defaultDumperPluginsProvider(DebugApplication.this))
                                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(
                                        DebugApplication.this))
                                .build());
            }
        });

        OkHttp.getInstance().addNetworkInterceptor(new StethoInterceptor());

        super.onCreate();
    }
}
