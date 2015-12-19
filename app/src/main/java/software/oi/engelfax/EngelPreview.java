package software.oi.engelfax;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.view.Menu;
import android.view.MenuItem;
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

import software.oi.engelfax.jfiglet.FigletFont;
import software.oi.engelfax.util.FigletPrinter;
import software.oi.engelfax.util.TextUtils;

public class EngelPreview extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private final static String ASCII_ART = "A";
    private final static String FIGLET = "F";
    private final static int WIDTH = 24;
    private Spinner styleChooser;
    private FloatingActionButton fab;
    private String phoneNumber;
    private final String SMS_SENT = "SMS_SENT";
    private final String SMS_DELIVERED = "SMS_DELIVERED";
    private final String TAG = EngelPreview.class.getSimpleName();

    private BroadcastReceiver sentReceiver;

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
        //Load Preview Texts asynchronously
        PreviewLoader loader = new PreviewLoader();
        loader.execute(text);

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
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            PreviewText text = texts.get(position);
            return PlaceholderFragment.newInstance(text);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static final String PREVIEW_TEXT = "previewText";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(PreviewText previewText) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putString(PREVIEW_TEXT, previewText.text);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_engel_preview, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getArguments().getString(PREVIEW_TEXT));
            textView.setMovementMethod(new ScrollingMovementMethod());
            return rootView;
        }
    }
    private void sendSms(String text){
        if (!text.trim().equals("")) {

            String prefix = ((PreviewText) styleChooser.getSelectedItem()).code;
            fab.setEnabled(false);
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
    public void onStart(){
        super.onStart();
        registerReceiver(sentReceiver, new IntentFilter(SMS_SENT));

    }
    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(sentReceiver);
    }
    private static class PreviewText{


        public PreviewText(String code, String title, String text) {
            this.title = title;
            this.code = code;
            this.text = text;
        }
        public final String text;
        public final String title;
        public final String code;

        @Override
        public String toString() {
            return title;
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

        }
    };
    private class PreviewLoader extends AsyncTask<String, Void, List<PreviewText>> {
        private final String emptyLine = "\n" + String.format("%"+WIDTH+"s", "");
        private List<PreviewText> readCSV(String path, String csv, String prefix, String text) throws IOException {
            List<PreviewText> previewTexts = new ArrayList<>();
            InputStream in = getAssets().open(path + "/" + csv);

            String input = IOUtils.toString(in);
            String wrappedText = TextUtils.wordWrap(text, WIDTH);
            IOUtils.closeQuietly(in);
            String[] rawCodes = input.split("\n");
            for (String line : rawCodes){
                String[] items = line.split(";");

                if (items.length == 2) {
                    String finalText = "";
                    switch (prefix){
                        case ASCII_ART:
                            try {
                                InputStream is = getAssets().open(path + "/" + items[1]);
                                String asciiArt = IOUtils.toString(is);
                                IOUtils.closeQuietly(is);
                                finalText = wrappedText + "\n" + asciiArt;
                            }
                            catch (Exception ex){
                                finalText = ex.getMessage();
                            }
                            break;
                        case FIGLET:
                            try {
                                InputStream is = getAssets().open(path + "/" + items[1] + ".flf");
                                finalText = TextUtils.wordWrap(text, 24, new FigletPrinter(new FigletFont(is)));
                                IOUtils.closeQuietly(is);
                            }
                            catch (Exception ex){
                                finalText = ex.getMessage();
                            }
                            break;
                    }
                    finalText+=emptyLine;
                    previewTexts.add(new PreviewText("#" + prefix + items[0], items[1], finalText));
                }
            }
            return previewTexts;

        }
        @Override
        protected List<PreviewText> doInBackground(String... textArray) {
            List<PreviewText> texts = new ArrayList<>();
            String text = textArray[0];
            texts.add(new PreviewText("", "Simple", TextUtils.wordWrap(text, 24) + emptyLine));
            try {
                texts.addAll(readCSV("asciiart","art.csv", "A", text));
                texts.addAll(readCSV("fonts","fonts.csv", "F", text));
            }
            catch (Exception ex){

            }
            return texts;

        }

        @Override
        protected void onPostExecute(List<PreviewText> texts) {
            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), texts);
            mViewPager.setAdapter(mSectionsPagerAdapter);
            ArrayAdapter<PreviewText> styleAdapter = new ArrayAdapter<PreviewText>(EngelPreview.this,android.R.layout.simple_spinner_dropdown_item, texts);
            styleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            styleChooser.setAdapter(styleAdapter);

        }

    }
}
