package software.oi.engelfax.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import software.oi.engelfax.PreviewText;
import software.oi.engelfax.R;
import software.oi.engelfax.components.SmsBroadcastReceiver;
import software.oi.engelfax.util.GsmUtils;
import software.oi.engelfax.util.PhoneNumberException;

public final class PreviewActivity extends AppCompatActivity implements  PreviewLoaderFragment.TaskCallbacks, SmsBroadcastReceiver.SmsSentCallbacks {

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
    private mbanje.kurt.fabbutton.FabButton fab;
    private final String TAG = PreviewActivity.class.getSimpleName();
    private ArrayList<PreviewText> previews;
    private BroadcastReceiver sentReceiver;
    private static final String TAG_LOADER_FRAGMENT = "loader_fragment";
    private static final String PREVIEW_POSITION= "PREVIEW_POSITION";
    private static final String PREVIEWS = "PREVIEWS";
    private static final String TEXT_KEY = "TEXT";

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_engel_preview);
        styleChooser = (Spinner) findViewById(R.id.styleSpinner);

        fab = (mbanje.kurt.fabbutton.FabButton ) findViewById(R.id.fab);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // get Text
        final String text = getIntent().getStringExtra(TEXT_KEY);

        sentReceiver = new SmsBroadcastReceiver(this);
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
            PreviewLoaderFragment previewLoaderFragment = (PreviewLoaderFragment) fm.findFragmentByTag(TAG_LOADER_FRAGMENT);
            // If the Fragment is non-null, then it is currently being
            // retained across a configuration change.
            if (previewLoaderFragment == null) {
                previewLoaderFragment = PreviewLoaderFragment.newInstance(text);
                fm.beginTransaction().add(previewLoaderFragment, TAG_LOADER_FRAGMENT).commit();
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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onStart(){
        super.onStart();
        registerReceiver(sentReceiver, new IntentFilter(GsmUtils.SMS_SENT));
    }
    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(sentReceiver);
    }
    private void showPreviews(){
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), previews);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        ArrayAdapter<PreviewText> styleAdapter = new ArrayAdapter<>(PreviewActivity.this,android.R.layout.simple_spinner_dropdown_item, previews);
        styleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        styleChooser.setAdapter(styleAdapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (previews != null) {
            outState.putInt(PREVIEW_POSITION, mViewPager.getCurrentItem());
            outState.putParcelableArrayList(PREVIEWS, previews);
        }
    }

    @Override
    public void onPreExecute() {
        //TODO show Loading indicator
    }

    @Override
    public void onPostExecute(ArrayList<PreviewText> texts) {
        this.previews = texts;
        showPreviews();
    }

    @Override
    public void onSmsSentSuccess() {
        setResult(Activity.RESULT_OK);
        finish();
    }

    @Override
    public void onSmsSentError(String message) {
        if (message != null)

            Snackbar.make(fab, message, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        fab.setEnabled(true);
        fab.showProgress(false);

    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private final List<PreviewText> texts;
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
        text = text == null ? "" : text.trim();
        String prefix = ((PreviewText) styleChooser.getSelectedItem()).code;
        text = prefix + text;
        if (!"".equals(text)){
            fab.setEnabled(false);
            fab.showProgress(true);
            try {
                GsmUtils.sendSms(this, text);
            }
            catch (PhoneNumberException ex){
                Snackbar.make(fab, ex.getMessage(), Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.settings), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(PreviewActivity.this, SettingsActivity.class);
                                startActivity(intent);
                            }
                        }).show();
                fab.setEnabled(true);
                fab.showProgress(false);
            }
        }else {
            Snackbar.make(fab, getString(R.string.error_no_message), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    public static Intent makeIntent(Context context, String text){
        Intent intent = new Intent(context, PreviewActivity.class);
        intent.putExtra(TEXT_KEY, text);
        return intent;
    }



}
