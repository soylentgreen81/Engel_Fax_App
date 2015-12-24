package software.oi.engelfax.activity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import software.oi.engelfax.PreviewText;
import software.oi.engelfax.R;
import software.oi.engelfax.jfiglet.FigletFont;
import software.oi.engelfax.util.FigletPrinter;
import software.oi.engelfax.util.TextUtils;

public class EngelPreview extends AppCompatActivity implements  LoaderFragment.TaskCallbacks {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    public final static String ASCII_ART = "A";
    public final static String FIGLET = "F";
    public final static String COWSAY = "C";
    public final static int WIDTH = 24;
    private Spinner styleChooser;
    private FloatingActionButton fab;
    private String phoneNumber;
    private final String SMS_SENT = "SMS_SENT";
    private final String SMS_DELIVERED = "SMS_DELIVERED";
    private final String TAG = EngelPreview.class.getSimpleName();
    private ArrayList<PreviewText> previews;
    private BroadcastReceiver sentReceiver;
    private static final String TAG_LOADER_FRAGMENT = "loader_fragment";

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_engel_preview);
        styleChooser = (Spinner) findViewById(R.id.styleSpinner);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // get Text
        final String text = getIntent().getStringExtra(EngelMessenger.TEXT_KEY);
        phoneNumber = getString(R.string.number);
        sentReceiver = new SmsReceiver();
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        //If screen is rotated, restore previewImages and jump to current position
        // otherwise load Preview Texts asynchronously
        if (savedInstanceState != null && savedInstanceState.containsKey(PREVIEWS)) {
            previews = savedInstanceState.getParcelableArrayList(PREVIEWS);
            int position = savedInstanceState.getInt(PREVIEW_POSITION);
            showPreviews();
            mViewPager.setCurrentItem(position);
            styleChooser.setSelection(position);

        } else {
            android.app.FragmentManager fm = getFragmentManager();
            LoaderFragment loaderFragment = (LoaderFragment) fm.findFragmentByTag(TAG_LOADER_FRAGMENT);
            // If the Fragment is non-null, then it is currently being
            // retained across a configuration change.
            if (loaderFragment == null) {
                loaderFragment = new LoaderFragment().newInstance(text);
                fm.beginTransaction().add(loaderFragment, TAG_LOADER_FRAGMENT).commit();
            }
        }


        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                styleChooser.setSelection(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendSms(text);
            }
        });
        styleChooser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mViewPager.setCurrentItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }
    public void onStart(){
        super.onStart();
        registerReceiver(sentReceiver, new IntentFilter(SMS_SENT));

    }
    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(sentReceiver);
    }
    private void showPreviews(){
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), previews);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        ArrayAdapter<PreviewText> styleAdapter = new ArrayAdapter<PreviewText>(EngelPreview.this,android.R.layout.simple_spinner_dropdown_item, previews);
        styleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        styleChooser.setAdapter(styleAdapter);
    }
    public static final String PREVIEW_POSITION= "PREVIEW_POSITION";
    public static final String PREVIEWS = "PREVIEWS";
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (previews != null) {
            outState.putInt(PREVIEW_POSITION, mViewPager.getCurrentItem());
            outState.putParcelableArrayList(PREVIEWS, previews);
        }
    }

    @Override
    public void onPrexecute() {

    }

    @Override
    public void onPostExecute(ArrayList<PreviewText> texts) {
        this.previews = texts;
        showPreviews();
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private List<PreviewText> texts;
        public SectionsPagerAdapter(FragmentManager fm, List<PreviewText> texts) {
            super(fm);
            this.texts = texts;
        }

        @Override
        public Fragment getItem(int position) {
            PreviewText text = texts.get(position);
            return PreviewFragment.newInstance(text);
        }

        @Override
        public int getCount() {
            return texts.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position >= 0 && position <texts.size())
                return texts.get(position).title;
            else
                return null;
        }
    }


    private void sendSms(String text){
        if (!text.trim().equals("")) {

            String prefix = ((PreviewText) styleChooser.getSelectedItem()).code;
            fab.setEnabled(false);
            fab.setVisibility(View.INVISIBLE);
            PendingIntent sentPendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(SMS_SENT), 0);
            PendingIntent deliveredPendintIntent= PendingIntent.getBroadcast(this, 0, new Intent(SMS_DELIVERED),0);
            SmsManager smsManager = SmsManager.getDefault();
            Log.d(TAG, "Text: " + prefix + text + ", Number " + phoneNumber);
            smsManager.sendTextMessage(phoneNumber, null, prefix + text, sentPendingIntent, deliveredPendintIntent);
        }
        else{
            Snackbar.make(fab, getString(R.string.error_no_message), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }


    private class SmsReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = null;
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    EngelPreview.this.setResult(Activity.RESULT_OK);
                    EngelPreview.this.finish();
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    message =  getString(R.string.error_generic);
                    break;
                case SmsManager.RESULT_ERROR_NO_SERVICE:
                    message = getString(R.string.error_no_service);
                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU:
                    message =  getString(R.string.error_null_pdu);
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                    message =  getString(R.string.error_radio_off);
                    break;
            }
            if (message != null)

                Snackbar.make(fab, message, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            fab.setEnabled(true);
            fab.setVisibility(View.VISIBLE);

        }
    };

}
