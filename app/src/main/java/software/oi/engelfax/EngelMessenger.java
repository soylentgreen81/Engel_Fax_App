package software.oi.engelfax;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.LinkedHashMap;
import java.util.Map;

public class EngelMessenger extends AppCompatActivity {
    private static final String TAG = EngelMessenger.class.getSimpleName();
    final String SMS_SENT = "SMS_SENT";
    final String SMS_DELIVERED = "SMS_DELIVERED";
    private String phoneNumber = "";
    private EditText nachrichtEditText;
    private Spinner styleSpinner;
    private final LinkedHashMap<String, String> styleMap = new LinkedHashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        phoneNumber = getString(R.string.action_settings);
        styleMap.put("", "Ohne");
        styleMap.put("#AB ", "Batman");
        styleMap.put("#CC ", "Kuh");
        styleMap.put("#CB ", "Kuh mit Bong");
        styleMap.put("#CR ", "Ren");
        styleMap.put("#CS ", "Stimpy");
        styleMap.put("#FC ", "Font 1");
        styleMap.put("#FS ", "Font 2");
        styleMap.put("#F3 ", "Font 3");
        styleMap.put("#FI ", "Font 3D");
        setContentView(R.layout.activity_engel_messenger);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        nachrichtEditText = (EditText)  findViewById(R.id.nachrichtEditText);
        styleSpinner = (Spinner) findViewById(R.id.styleSpinner);
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        final FloatingActionButton previewButton = (FloatingActionButton) findViewById(R.id.preview);

        LinkedHashMapAdapter<String, String> styleAdapter =  new LinkedHashMapAdapter<String, String>(this, android.R.layout.simple_spinner_item, styleMap);
        styleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        styleSpinner.setAdapter(styleAdapter);
        sentReceiver = new BroadcastReceiver() {
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
                fab.setEnabled(true);
                nachrichtEditText.setText("", TextView.BufferType.EDITABLE);
                nachrichtEditText.setEnabled(true);

            }
        };
        deliveredReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String message = null;
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        message = getString(R.string.delivered);
                        break;
                    case Activity.RESULT_CANCELED:
                        message = getString(R.string.error_not_delivered);
                        break;
                }
                if (message != null)
                    Snackbar.make(fab, message, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
            }
            };

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String text =  getText();
                if (!text.trim().equals("")) {
                    String prefix = getStyle();

                    nachrichtEditText.setEnabled(false);
                    fab.setEnabled(false);
                    PendingIntent sentPendingIntent = PendingIntent.getBroadcast(EngelMessenger.this, 0, new Intent(SMS_SENT), 0);
                    PendingIntent deliveredPendingIntent = PendingIntent.getBroadcast(EngelMessenger.this, 0, new Intent(SMS_DELIVERED), 0);
                    SmsManager smsManager = SmsManager.getDefault();
                    //Log.d(TAG, "Text: " + prefix + text);
                    smsManager.sendTextMessage(phoneNumber, null, prefix + text, sentPendingIntent, deliveredPendingIntent);
                }
                else{
                    Snackbar.make(fab, getString(R.string.error_no_message), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });
        previewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPreview();

            }
        });
    }
    private BroadcastReceiver sentReceiver;
    private BroadcastReceiver deliveredReceiver;

    public void onStart(){
        super.onStart();
        registerReceiver(sentReceiver, new IntentFilter(SMS_SENT));
        registerReceiver(deliveredReceiver, new IntentFilter(SMS_DELIVERED));


    }
    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(sentReceiver);
        unregisterReceiver(deliveredReceiver);
    }

    public static final String STYLE_KEY = "STYLE";
    public static final String TEXT_KEY = "TEXT";

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STYLE_KEY, getStyle());
        outState.putString(TEXT_KEY, getText());
    }
    private String getStyle(){
        return ((Map.Entry<String, String>) styleSpinner.getSelectedItem()).getKey();
    }
    private String getText(){
        return nachrichtEditText.getText().toString();
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        nachrichtEditText.setText(savedInstanceState.getString(TEXT_KEY));
        String style = savedInstanceState.getString(STYLE_KEY);
        int pos = 0;
        for (String key : styleMap.keySet()) {
            if (key.equals(style)) {
                styleSpinner.setSelection(pos);
                break;
            }
            pos++;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_engel_messenger, menu);
        return true;
    }
    private void startPreview(){
        Intent intent = new Intent(this, EngelSelectStyle.class);
        intent.putExtra(TEXT_KEY, getText());
        intent.putExtra(STYLE_KEY, getStyle());
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_settings:
                startPreview();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
