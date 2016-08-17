package org.xdty.gallery.model.setting;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import org.xdty.gallery.R;

public class SettingImpl implements Setting {

    private Context mContext;
    private SharedPreferences mPref;

    public SettingImpl(Context context) {
        mContext = context;
        mPref = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    @Override
    public boolean isCatchCrashEnable() {
        return mPref.getBoolean(getString(R.string.catch_crash_key), false);
    }

    @NonNull
    private String getString(int id) {
        return mContext.getString(id);
    }
}
