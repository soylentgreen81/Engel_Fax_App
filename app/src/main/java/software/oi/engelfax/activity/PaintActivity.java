package software.oi.engelfax.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.soundcloud.android.crop.Crop;

import java.io.File;

import software.oi.engelfax.AsciiBitmap;
import software.oi.engelfax.R;
import software.oi.engelfax.util.BitSet;
import software.oi.engelfax.util.ImageUtils;
import software.oi.engelfax.util.TextUtils;

/**
 * Shows the Paintcanvas and a menu which allows importing Images
 * @author Stefan Beukmann
 */
public class PaintActivity extends AppCompatActivity {
    private TextView paintView;
    private ImageButton adjustButton;
    private static final String ASCII_IMAGE_KEY = "ASCII_IMAGE";
    private static final String  BRIGHTNESS_THRESHOLD ="BRIGHTNESS_THRESHOLD";
    private static final String SOURCE_IMAGE = "SOURCE_IMAGE";
    private final int WIDTH = 24;
    private final int HEIGHT = 18;
    private final String TAG = PaintActivity.class.getSimpleName();
    private AsciiBitmap asciiBitmap;
    private final int EDIT_MODE = 1;
    private final int ERASE_MODE = 2;
    private int MODE = EDIT_MODE;
    private int brightnessThreshold = 128;
    private Bitmap sourceImage;
    private int selectedModeId;
    private final char[] alphabet = new char[]{' ','.','+','#'};
    private int currentChar = 1;
    private char FREE = ' ';
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paint);
        if (savedInstanceState != null){
            asciiBitmap = (AsciiBitmap) savedInstanceState.getParcelable(ASCII_IMAGE_KEY);
            sourceImage = (Bitmap) savedInstanceState.getParcelable(SOURCE_IMAGE);
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

        selectedModeId = R.id.drawMode;
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
        Button drawButton = (Button) findViewById(R.id.drawMode);
        drawButton.setOnClickListener(modeSelectListener);
        drawButton.setText(alphabet[currentChar] + "");
        findViewById(R.id.eraseMode).setOnClickListener(modeSelectListener);
        findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
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
    }

    private boolean draw(View v, MotionEvent event) {
        if (event.getPointerCount() == 1) {
            for (int i = 0; i < event.getHistorySize(); i++) {
                int x = (int) (event.getHistoricalX(i) * WIDTH / v.getWidth());
                int y = (int) (event.getHistoricalY(i) * HEIGHT / v.getHeight());
                char draw = (MODE == EDIT_MODE) ? alphabet[currentChar] : FREE;
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
        findViewById(selectedModeId).setBackground(ContextCompat.getDrawable(PaintActivity.this, R.drawable.back_button));
        v.setBackground(ContextCompat.getDrawable(PaintActivity.this, R.drawable.back_button_selected));
        if (v.getId() == R.id.eraseMode){
            MODE = ERASE_MODE;
        } else if (v.getId() == R.id.drawMode){
            MODE = EDIT_MODE;
            if (selectedModeId == v.getId()){
                currentChar++;
                if (currentChar >= alphabet.length)
                    currentChar = 1;
                ((Button) v).setText(alphabet[currentChar] + "");
            }
        }
        selectedModeId = v.getId();
    }

    private void send(){
        String message = "#P" + asciiBitmap.getAlphabet() + asciiBitmap.toBase64();
        Intent intent = new Intent(PaintActivity.this, PreviewActivity.class);
        intent.putExtra(MessengerActivity.TEXT_KEY, message);
        startActivity(intent);
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
