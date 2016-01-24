package software.oi.engelfax.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.soundcloud.android.crop.Crop;

import java.io.File;

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
    private final int WIDTH = 24;
    private final int HEIGHT = 24;
    private final String TAG = PaintActivity.class.getSimpleName();
    private BitSet bits;
    private final int EDIT_MODE = 1;
    private final int VIEW_MODE = 2;
    private final int ERASE_MODE = 3;
    private int MODE = VIEW_MODE;
    private float textSize = 1.0f;
    private float scale = 1.0f;
    private int scrollX = 0;
    private int scrollY = 0;
    private int brightnessThreshold = 128;
    private Bitmap sourceImage;
    private int selectedModeId;
    private final String BLOCK = "#";
    private final String FREE = " ";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paint);
        if (savedInstanceState != null){
            bits = BitSet.valueOf(savedInstanceState.getByteArray(BIT_KEY));
            sourceImage = (Bitmap) savedInstanceState.getParcelable(SOURCE_IMAGE);
            brightnessThreshold = savedInstanceState.getInt(BRIGHTNESS_THRESHOLD);
        }
        else {
            bits = new BitSet(WIDTH * HEIGHT);
        }

        selectedModeId = R.id.viewMode;
        paintView = (TextView) findViewById(R.id.paintArea);
        paintView.setTypeface(Typeface.MONOSPACE);
        textSize = paintView.getTextSize()/2;
        showPreview();
        final GestureDetector gd = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                scrollX += distanceX;
                scrollY += distanceY;
                return true;
            }
        });
        final ScaleGestureDetector sgd = new ScaleGestureDetector(this, new ScaleGestureDetector.SimpleOnScaleGestureListener(){
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                    scale*= detector.getScaleFactor();
                    scale = Math.max(0.1f, Math.min(scale, 1.5f));
                    paintView.setScaleX(scale);
                    paintView.setScaleY(scale);
                    return true;

            }

        });
        paintView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MODE == VIEW_MODE) {
                    gd.onTouchEvent(event);
                    sgd.onTouchEvent(event);
                    return true;
                } else {
                    if (event.getPointerCount() == 1) {
                        for (int i = 0; i < event.getHistorySize(); i++) {
                            int zeile = (int) (event.getHistoricalY(i) * HEIGHT / v.getHeight());
                            int spalte = (int) (event.getHistoricalX(i) * WIDTH / v.getWidth());
                            boolean draw = MODE == EDIT_MODE;
                            int pos = zeile* WIDTH + spalte;
                            if (zeile < HEIGHT && zeile >= 0 && spalte < WIDTH && spalte >= 0) {
                                bits.set(pos, draw);
                                int textPos = pos+zeile;
                                Editable editText = (Editable) paintView.getText();
                                editText.replace(textPos, textPos+1,  draw ? BLOCK : FREE);
                            }
                        }
                        return true;
                    } else
                        return false;
                }
            }
        });
        View.OnClickListener modeSelectListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                findViewById(selectedModeId).setBackground(ContextCompat.getDrawable(PaintActivity.this, R.drawable.back_button));
                v.setBackground(ContextCompat.getDrawable(PaintActivity.this, R.drawable.back_button_selected));
                selectedModeId = v.getId();
                if (v.getId() == R.id.viewMode){
                    MODE = VIEW_MODE;
                } else if (v.getId() == R.id.eraseMode){
                    MODE = ERASE_MODE;
                } else if (v.getId() == R.id.drawMode){
                    MODE = EDIT_MODE;
                }
            }
        };
        findViewById(R.id.viewMode).setOnClickListener(modeSelectListener);
        findViewById(R.id.eraseMode).setOnClickListener(modeSelectListener);
        findViewById(R.id.drawMode).setOnClickListener(modeSelectListener);
        findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send();
            }
        });
        findViewById(R.id.adjustImage).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                showAdjustDialog();
            }
        });
    }
    private void send(){
        String message = "#B" + ImageUtils.toString(bits);
        Intent intent = new Intent(PaintActivity.this, PreviewActivity.class);
        intent.putExtra(MessengerActivity.TEXT_KEY, message);
        startActivity(intent);
    }

    private void showAdjustDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View customView = getLayoutInflater().inflate(R.layout.dialog_image_slider, null);
        AlertDialog dialog = builder.setView(customView)
                .setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        previewBitmap();
                    }
                })
                .setTitle(getString(R.string.brightness_threshold))
                .create();

        SeekBar bar = (SeekBar) customView.findViewById(R.id.brightnessThresholdBar);
        bar.setProgress(brightnessThreshold);
        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    brightnessThreshold = progress;
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
                bits.clear();
                scale = 1.0f;
                paintView.setScaleX(scale);
                paintView.setScaleY(scale);
                showPreview();
                return true;
            case R.id.loadImage:
                Crop.pickImage(this);
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
        paintView.setText(TextUtils.renderBitSet(bits, WIDTH, HEIGHT, BLOCK, FREE), TextView.BufferType.EDITABLE);
    }
    private static final String BIT_KEY = "BIT_KEY";
    private static final String  BRIGHTNESS_THRESHOLD ="BRIGHTNESS_THRESHOLD";
    private static final String SOURCE_IMAGE = "SOURCE_IMAGE";
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putByteArray(BIT_KEY, bits.toByteArray());
        outState.putInt(BRIGHTNESS_THRESHOLD, brightnessThreshold);
        outState.putParcelable(SOURCE_IMAGE, sourceImage);
    }
    private void previewBitmap(){
        for (int y=0;y<HEIGHT;y++){
            for (int x=0;x<WIDTH;x++) {
                int pixel = sourceImage.getPixel(x, y);
                int r = Color.green(pixel);
                int g = Color.red(pixel);
                int b = Color.blue(pixel);
                int V = Math.max(b, Math.max(r, g));
                int pos = y* WIDTH + x;
                bits.set(pos, V < brightnessThreshold);
            }
        }
        showPreview();
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
        }
    }
}
