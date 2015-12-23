package software.oi.engelfax;

import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Base64;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import java.nio.Buffer;

import software.oi.engelfax.util.BitSet;
import software.oi.engelfax.util.TextUtils;

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
    private float scale = 1.0f;
    private int scrollX = 0;
    private int scrollY = 0;

    private int selectedModeId;
    private Switch modeSwitch;
    private final String BLOCK = "#";
    private final String FREE = " ";
    private boolean draw = true;
    private ImageButton sendButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paint);
        if (savedInstanceState != null){
            bits = BitSet.valueOf(savedInstanceState.getByteArray(BIT_KEY));
        }
        else {
            bits = new BitSet(WIDTH * HEIGHT);
        }
        selectedModeId = R.id.viewMode;
        paintView = (TextView) findViewById(R.id.paintArea);
        paintView.setTypeface(Typeface.MONOSPACE);
        previewImage();
        sendButton = (ImageButton) findViewById(R.id.sendButton);
        final GestureDetector gd = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                scrollX += distanceX;
                scrollY += distanceY;
              /*  if (scrollX < 0)
                    scrollX = 0;
                if (scrollY < 0)
                    scrollY = 0;*/

                paintView.scrollTo(scrollX, scrollY);
                return true;
            }
        });
        final ScaleGestureDetector sgd = new ScaleGestureDetector(this, new ScaleGestureDetector.SimpleOnScaleGestureListener(){
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                    scale *= detector.getScaleFactor();
                    scale = Math.max(0.1f, Math.min(scale, 2.0f));
                    paintView.setScaleX(scale);
                    paintView.setScaleY(scale);
                    Log.d(TAG, String.format("ScrollX/Y %d/%d", paintView.getScrollX(), paintView.getScrollY()));
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
                   // Log.d(TAG, String.format("View: w=%d, h=%d, x=%f, y=%f", v.getWidth(), v.getHeight(), event.getX(), event.getY()));
                    //Log.d(TAG, String.format("Scroll x=%d,y=%d", v.getScrollX(), v.getScrollY()));
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
                findViewById(selectedModeId).setBackground(getResources().getDrawable(R.drawable.back_button));
                v.setBackground(getResources().getDrawable(R.drawable.back_button_selected));
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
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               byte[] result = bits.toByteArray();
                byte[] reversed = result;
                for (int i=0;i<result.length;i++) {

                        reversed[i] = (byte)(Integer.reverse(result[i]) >>> (Integer.SIZE - Byte.SIZE));

                }
                String message = "#B" + new String(Base64.encode(reversed, Base64.NO_WRAP));
                Intent intent = new Intent(PaintActivity.this, EngelPreview.class);
                intent.putExtra(EngelMessenger.TEXT_KEY, message);
                startActivity(intent);


            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_paint_activity, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        float scale = paintView.getScaleX();
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.clearImage:
                bits.clear();
                previewImage();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void previewImage(){
        paintView.setText(TextUtils.renderBitSet(bits, WIDTH, HEIGHT, BLOCK, FREE), TextView.BufferType.EDITABLE);
    }
    private static final String BIT_KEY = "BIT_KEY";
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putByteArray(BIT_KEY, bits.toByteArray());
    }
}
