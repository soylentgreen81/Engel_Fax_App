package software.oi.engelfax.activity;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by Stefan Beukmann on 20.01.2016.
 */
public class SettingsActivity extends Activity {
    public static final String PHONE_NO = "phone_no";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}