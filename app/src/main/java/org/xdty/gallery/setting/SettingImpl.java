package org.xdty.gallery.setting;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import org.xdty.gallery.R;

import java.util.HashSet;
import java.util.Set;

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

    @Override
    public Set<String> getServers() {
        return new HashSet<>(mPref.getStringSet("server_list", new HashSet<String>()));
    }

    @Override
    public void addServer(String server) {
        Set<String> servers = getServers();
        servers.add(server);
        mPref.edit().putStringSet("server_list", servers).apply();
    }

    @Override
    public String getLocalPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    @NonNull
    private String getString(int id) {
        return mContext.getString(id);
    }
}
