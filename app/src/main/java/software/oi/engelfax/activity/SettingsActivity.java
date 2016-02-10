package software.oi.engelfax.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import software.oi.engelfax.R;

/**
 * Created by Stefan Beukmann on 20.01.2016.
 */
public class SettingsActivity extends AppCompatActivity{
    public static final String PHONE_NO = "phone_no";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
        android.support.v7.app.ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(getString(R.string.settings));
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setHomeButtonEnabled(true);
        };
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       if (item.getItemId() == android.R.id.home){
           finish();
           return true;
       } else {
           return super.onOptionsItemSelected(item);
       }
    }
}