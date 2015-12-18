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
    private EditText nachrichtEditText;
    private FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_engel_messenger);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        nachrichtEditText = (EditText)  findViewById(R.id.nachrichtEditText);
        fab = (FloatingActionButton) findViewById(R.id.fab);




        fab.setOnClickListener(new View.OnClickListener() {
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

    private String getText(){
        return nachrichtEditText.getText().toString();
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        nachrichtEditText.setText(savedInstanceState.getString(TEXT_KEY));

    }
  /*  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_engel_messenger, menu);
        return true;
    }*/
    private void startPreview(){
        Intent intent = new Intent(this, EngelSelectStyle.class);
        intent.putExtra(TEXT_KEY, getText());
        startActivity(intent);
    }

    /*@Override
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
*/
}
