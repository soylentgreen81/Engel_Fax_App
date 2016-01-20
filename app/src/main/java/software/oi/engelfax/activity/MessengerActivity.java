package software.oi.engelfax.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;

import software.oi.engelfax.R;

public class MessengerActivity extends AppCompatActivity {
    private static final String TAG = MessengerActivity.class.getSimpleName();
    private EditText nachrichtEditText;
    private ImageButton sendButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_engel_messenger);
        android.support.v7.app.ActionBar ab = getSupportActionBar();
        ab.setTitle(getString(R.string.title));
        ab.setSubtitle(getString(R.string.sub_title));

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

    static final int REQUEST_SMS = 1;

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
                startActivity(intent);
                return true;
            }
            case R.id.action_settings: {
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
