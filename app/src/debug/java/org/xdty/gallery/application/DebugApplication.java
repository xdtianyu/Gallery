package org.xdty.gallery.application;

import android.os.StrictMode;

import com.facebook.stetho.Stetho;

import org.xdty.gallery.application.Application;

public class DebugApplication extends Application {
    public final static String TAG = DebugApplication.class.getSimpleName();

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

        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                        .build());
        super.onCreate();
    }
}
