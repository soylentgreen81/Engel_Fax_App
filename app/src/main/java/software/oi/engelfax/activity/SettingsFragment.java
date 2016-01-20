package software.oi.engelfax.activity;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import software.oi.engelfax.R;

/**
 * Created by stefa_000 on 20.01.2016.
 */
public  class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
}