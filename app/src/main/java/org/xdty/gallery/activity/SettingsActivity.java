package org.xdty.gallery.activity;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import androidx.appcompat.app.AppCompatActivity;

import org.xdty.gallery.BuildConfig;
import org.xdty.gallery.R;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = SettingsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(android.R.id.content, new SettingsFragment())
                    .commit();
        }
    }

    public static class SettingsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);

            Preference version = findPreference(getString(R.string.version_key));
            String versionString = BuildConfig.VERSION_NAME;
            if (BuildConfig.DEBUG) {
                versionString += "." + BuildConfig.BUILD_TYPE;
            }
            version.setSummary(versionString);
        }
    }
}
