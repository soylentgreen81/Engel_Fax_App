package software.oi.engelfax;

import android.app.ActionBar;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;


import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import software.oi.engelfax.jfiglet.FigletFont;
import software.oi.engelfax.util.FigletPrinter;
import software.oi.engelfax.util.TextUtils;

/**
 * Created by stefa_000 on 14.12.2015.
 */
public class EngelSelectStyle extends AppCompatActivity {
    private ViewFlipper mViewFlipper;
    private GestureDetector mGestureDetector;
    private Spinner styleChooser;
    private static final String TAG = EngelSelectStyle.class.getSimpleName();
    private LinkedHashMap<Integer, String> titles;
    private Map<Integer, String> codes;
    private FloatingActionButton fab;
    private BroadcastReceiver sentReceiver;
    private final String SMS_SENT = "SMS_SENT";
    private final String SMS_DELIVERED = "SMS_DELIVERED";
    private final static String ASCII_ART = "A";
    private final static String FIGLET = "F";
    private final static int WIDTH = 24;
    private String phoneNumber = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_engel_selectstyle);
        //Get widgets
        mViewFlipper = (ViewFlipper) findViewById(R.id.previews);
        styleChooser = (Spinner) findViewById(R.id.styleSpinner);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        CustomGestureDetector customGestureDetector = new CustomGestureDetector();
        mGestureDetector = new GestureDetector(this, customGestureDetector);
        // get Text
        final String text = getIntent().getStringExtra(EngelMessenger.TEXT_KEY);
        phoneNumber = getString(R.string.number);
        //Load texts async
        PreviewLoader loader = new PreviewLoader();
        loader.execute(text);
        sentReceiver = new SmsReceiver();
        styleChooser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mViewFlipper.setDisplayedChild(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSms(styleChooser.getSelectedItemPosition(), text);
            }
        });

    }
    private void sendSms(int position, String text){
        if (!text.trim().equals("")) {
            String prefix = codes.get(position);

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

    private TextView createTextView(){
        TextView newView = new TextView(this);
        newView.setTextColor(Color.WHITE);
        newView.setBackgroundColor(Color.BLACK);
        newView.setTypeface(Typeface.MONOSPACE);

        newView.setVerticalScrollBarEnabled(true);
        return newView;
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);

        return super.onTouchEvent(event);
    }
    class CustomGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            // Swipe left (next)
            if (e1.getX() > e2.getX()) {
                mViewFlipper.setInAnimation(EngelSelectStyle.this, R.anim.slide_in_right);
                mViewFlipper.setOutAnimation(EngelSelectStyle.this, R.anim.slide_out_left);
                mViewFlipper.showNext();
                styleChooser.setSelection(mViewFlipper.getDisplayedChild());
            }

            // Swipe right (previous)
            if (e1.getX() < e2.getX()) {
                mViewFlipper.setInAnimation(EngelSelectStyle.this, R.anim.slide_in_left);
                mViewFlipper.setOutAnimation(EngelSelectStyle.this, R.anim.slide_out_right);
                mViewFlipper.showPrevious();
                styleChooser.setSelection(mViewFlipper.getDisplayedChild());
            }

            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }
    private static class PreviewText{


        public PreviewText(int id, String code, String title, String text) {
            this.title = title;
            this.code = code;
            this.id = id;
            this.text = text;
        }
        public final String text;
        public final String title;
        public final String code;
        public final int id;


    }
    private class SmsReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = null;
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    message = getString(R.string.sent);
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
    private class PreviewLoader extends AsyncTask<String, Void, List<PreviewText>>{
        private AtomicInteger id = new AtomicInteger(1);
        private List<PreviewText> readCSV(String path, String csv, String prefix, String text) throws IOException{
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
                    previewTexts.add(new PreviewText(id.getAndIncrement(), "#" + prefix + items[0], items[1], finalText));
                }
            }
            return previewTexts;

        }
        @Override
        protected List<PreviewText> doInBackground(String... textArray) {
            List<PreviewText> texts = new ArrayList<>();
            String text = textArray[0];
            texts.add(new PreviewText(0, "", "Simple",TextUtils.wordWrap(text,24)));
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
            titles = new LinkedHashMap<>();
            codes = new HashMap<>();
            for (PreviewText text : texts){
                TextView newView = createTextView();
                newView.setText(text.text);
                titles.put(text.id, text.title);
                codes.put(text.id, text.code);
                mViewFlipper.addView(newView, text.id, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }
            LinkedHashMapAdapter<Integer, String> styleAdapter =  new LinkedHashMapAdapter<Integer, String>(EngelSelectStyle.this, android.R.layout.simple_spinner_item, titles);
            styleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            styleChooser.setAdapter(styleAdapter);
        }

    }


}
