package software.oi.engelfax.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.soundcloud.android.crop.Crop;

import java.io.File;

import mbanje.kurt.fabbutton.FabButton;
import software.oi.engelfax.AsciiBitmap;
import software.oi.engelfax.R;
import software.oi.engelfax.components.SmsBroadcastReceiver;
import software.oi.engelfax.util.GsmUtils;
import software.oi.engelfax.util.ImageUtils;
import software.oi.engelfax.util.PhoneNumberException;

/**
 * Shows the Paintcanvas and a menu which allows importing Images
 * @author Stefan Beukmann
 */
public final class PaintActivity extends AppCompatActivity implements SmsBroadcastReceiver.SmsSentCallbacks {
    private TextView paintView;
    private FabButton sendButton;
    private ImageButton adjustButton;
    private FloatingActionButton drawButtonCharacter1;
    private FloatingActionButton drawButtonCharacter2;
    private FloatingActionButton drawButtonCharacter3;
    private FloatingActionButton drawButtonCharacter4;
    private static final String ASCII_IMAGE_KEY = "ASCII_IMAGE";
    private static final String  BRIGHTNESS_THRESHOLD ="BRIGHTNESS_THRESHOLD";
    private static final String SOURCE_IMAGE = "SOURCE_IMAGE";
    private final int WIDTH = 24;
    private final int HEIGHT = 18;
    private final String TAG = PaintActivity.class.getSimpleName();
    private AsciiBitmap asciiBitmap;
    private int brightnessThreshold = 128;
    private Bitmap sourceImage;
    private int selectedModeId;
    private final char[] alphabet = new char[]{' ','.','+','#'};
    private int currentChar = 1;
    private SmsBroadcastReceiver sentReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_paint);
        if (savedInstanceState != null){
            asciiBitmap =  savedInstanceState.getParcelable(ASCII_IMAGE_KEY);
            sourceImage =  savedInstanceState.getParcelable(SOURCE_IMAGE);
            brightnessThreshold = savedInstanceState.getInt(BRIGHTNESS_THRESHOLD);
        }
        else {
            asciiBitmap = new AsciiBitmap.Builder()
                                         .setHeight(HEIGHT)
                                         .setWidth(WIDTH)
                                         .setBitDepth(2)
                                         .setAlphabet(alphabet)
                                         .build();
        }

      //  selectedModeId = R.id.drawMode;
        paintView = (TextView) findViewById(R.id.paintArea);
        paintView.setTypeface(Typeface.MONOSPACE);
        showPreview();

        paintView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return draw(v, event);
            }
        });
        View.OnClickListener modeSelectListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              modeSwitch(v);
            }
        };
        selectedModeId = R.id.drawButtonCharacter1;
        drawButtonCharacter1 = (FloatingActionButton) findViewById(R.id.drawButtonCharacter1);
        drawButtonCharacter1.setOnClickListener(modeSelectListener);
        drawButtonCharacter2 = (FloatingActionButton) findViewById(R.id.drawButtonCharacter2);
        drawButtonCharacter2.setOnClickListener(modeSelectListener);
        drawButtonCharacter3 = (FloatingActionButton) findViewById(R.id.drawButtonCharacter3);
        drawButtonCharacter3.setOnClickListener(modeSelectListener);
        drawButtonCharacter4 = (FloatingActionButton) findViewById(R.id.drawButtonCharacter4);
        drawButtonCharacter4.setOnClickListener(modeSelectListener);


        sendButton = (FabButton) findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send();
            }
        });
        adjustButton  = (ImageButton) findViewById(R.id.adjustImage);
        adjustButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAdjustDialog();
            }
        });
        adjustButton.setVisibility(View.INVISIBLE);
        sentReceiver = new SmsBroadcastReceiver(this);
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
    private boolean draw(View v, MotionEvent event) {
        if (event.getPointerCount() == 1) {
            for (int i = 0; i < event.getHistorySize(); i++) {
                int x = (int) (event.getHistoricalX(i) * WIDTH / v.getWidth());
                int y = (int) (event.getHistoricalY(i) * HEIGHT / v.getHeight());
                char draw = alphabet[currentChar];
                int pos = y * WIDTH + y + x;
                if (y < HEIGHT && y >= 0 && x < WIDTH && x >= 0 && paintView.getText().charAt(pos) != draw) {
                    asciiBitmap.drawChar(x, y, draw);
                    paintView.setText(asciiBitmap.toString());
                }
            }
            return true;
        } else
            return false;
    }

    private void modeSwitch(View v) {
        ((FloatingActionButton) findViewById(selectedModeId)).setColorNormal(getResources().getColor(R.color.colorAccent));
        ((FloatingActionButton) v).setColorNormal(getResources().getColor(R.color.colorSelected));
        if (v.getId() == R.id.drawButtonCharacter1)
            currentChar = 0;
        else if (v.getId() == R.id.drawButtonCharacter2)
            currentChar = 1;
        else if (v.getId() == R.id.drawButtonCharacter3)
            currentChar = 2;
        else if (v.getId() == R.id.drawButtonCharacter4)
            currentChar = 3;
        selectedModeId = v.getId();
    }



    private void showAdjustDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View customView = getLayoutInflater().inflate(R.layout.dialog_image_slider, null);
        AlertDialog dialog = builder.setView(customView)
                .setCancelable(true)
                .setPositiveButton("OK",null)
                .setTitle(getString(R.string.brightness_threshold))
                .create();

        SeekBar bar = (SeekBar) customView.findViewById(R.id.brightnessThresholdBar);
        bar.setProgress(brightnessThreshold);
        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                brightnessThreshold = progress;
                previewBitmap();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        dialog.show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_paint_activity, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.clearImage:
                asciiBitmap.clear();
                showPreview();
                return true;
            case R.id.loadImage:
                Crop.pickImage(this);
                return true;
            case R.id.invertImage:
                asciiBitmap.invert();
                showPreview();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
            beginCrop(result.getData());
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, result);
        }
    }
    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, destination).withAspect(2, 3).start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            LoadImage loadImage = new LoadImage();
            loadImage.execute(Crop.getOutput(result));
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private void showPreview(){
        paintView.setText(asciiBitmap.toString(), TextView.BufferType.EDITABLE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(ASCII_IMAGE_KEY, asciiBitmap);
        outState.putInt(BRIGHTNESS_THRESHOLD, brightnessThreshold);
        outState.putParcelable(SOURCE_IMAGE, sourceImage);
    }
    private void previewBitmap(){
        if (sourceImage != null) {
            asciiBitmap.loadBitmap(sourceImage, brightnessThreshold);
            showPreview();
        }
    }
    private void send(){
        sendButton.setEnabled(false);
        sendButton.showProgress(true);
        String message = "#P" + asciiBitmap.getAlphabet() + asciiBitmap.toBase64();
        try {
            GsmUtils.sendSms(this, message);
        }
        catch (PhoneNumberException ex){
            Snackbar.make(sendButton, ex.getMessage(), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.settings), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(PaintActivity.this, SettingsActivity.class);
                            startActivity(intent);
                        }
                    }).show();
            sendButton.setEnabled(true);
            sendButton.showProgress(false);
        }


    }
    @Override
    public void onSmsSentSuccess() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onSmsSentError(String message) {
        Snackbar.make(sendButton, message, Snackbar.LENGTH_LONG).show();
        sendButton.setEnabled(true);
        sendButton.showProgress(false);
    }

    private class LoadImage extends AsyncTask<Uri, Void, Bitmap>{
        @Override
        protected Bitmap doInBackground(Uri ... uri) {
            return ImageUtils.scaleMonochrome(PaintActivity.this, uri[0], WIDTH, HEIGHT);
        }
        @Override
        protected void onPostExecute(Bitmap result) {
            sourceImage = result;
            previewBitmap();
            if (sourceImage != null)
                adjustButton.setVisibility(View.VISIBLE);
        }
    }
}
