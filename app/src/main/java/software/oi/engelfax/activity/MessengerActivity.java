package software.oi.engelfax.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import software.oi.engelfax.R;
import software.oi.engelfax.util.Preferences;

public class MessengerActivity extends AppCompatActivity {
    private static final String TAG = MessengerActivity.class.getSimpleName();
    private EditText nachrichtEditText;
    private ImageButton sendButton;
    public static final String MAGIC_WORD = "ENGELPOWER";
    private static final int REQUEST_SMS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_engel_messenger);
        android.support.v7.app.ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(getString(R.string.title));
            ab.setSubtitle(getString(R.string.sub_title));
        }
        nachrichtEditText = (EditText)  findViewById(R.id.nachrichtEditText);
        sendButton = (ImageButton) findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPreview();
            }
        });

    }



    public static final String TEXT_KEY = "TEXT";

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(TEXT_KEY, getText());
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        nachrichtEditText.setText(savedInstanceState.getString(TEXT_KEY));

    }
    private String getText(){
        return nachrichtEditText.getText().toString();
    }


    private void startPreview(){
        Intent intent = new Intent(this, PreviewActivity.class);
        intent.putExtra(TEXT_KEY, getText());
        startActivityForResult(intent, REQUEST_SMS);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SMS && resultCode == Activity.RESULT_OK){
            Snackbar.make(nachrichtEditText, getResources().getString(R.string.sent), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

        } else if (requestCode == IntentIntegrator.REQUEST_CODE){
            saveNumber(requestCode, resultCode, data);
        }
    }
    private void saveNumber(int requestCode, int resultCode, Intent data){
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Snackbar.make(nachrichtEditText, getResources().getString(R.string.canceled), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            } else {
                String phoneNumber = result.getContents();

                if (phoneNumber.startsWith(MAGIC_WORD) && Preferences.saveNumber(this, phoneNumber.substring(MAGIC_WORD.length()))){
                    Snackbar.make(nachrichtEditText, getResources().getString(R.string.number_saved), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                else {
                    Snackbar.make(nachrichtEditText, getResources().getString(R.string.error_number_save), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        }
    }
 @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_engel_messenger, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_paint: {
                Intent intent = new Intent(this, PaintActivity.class);
                startActivityForResult(intent, REQUEST_SMS);
                return true;
            }
            case R.id.action_settings: {
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            }
            case R.id.action_scan_qr: {
                IntentIntegrator intentIntegrator = new IntentIntegrator(this);
                intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                intentIntegrator.setPrompt(getString(R.string.scan_qr));
                intentIntegrator.initiateScan();
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
