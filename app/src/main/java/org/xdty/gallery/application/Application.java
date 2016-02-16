package org.xdty.gallery.application;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.xdty.gallery.R;

import cat.ereza.customactivityoncrash.CustomActivityOnCrash;

public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isCatch = pref.getBoolean(getString(R.string.catch_crash_key), false);
        if (isCatch) {
            CustomActivityOnCrash.install(this);
        }
    }

}

