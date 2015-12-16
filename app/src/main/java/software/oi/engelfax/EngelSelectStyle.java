package software.oi.engelfax;

import android.app.ActionBar;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.ViewFlipper;


import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;

import software.oi.engelfax.jfiglet.FigletFont;
import software.oi.engelfax.util.TextUtils;

/**
 * Created by stefa_000 on 14.12.2015.
 */
public class EngelSelectStyle extends AppCompatActivity {
    private ViewFlipper mViewFlipper;
    private GestureDetector mGestureDetector;
    private Spinner styleChooser;
    private static final String TAG = EngelSelectStyle.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_engel_selectstyle);
        mViewFlipper = (ViewFlipper) findViewById(R.id.previews);
        CustomGestureDetector customGestureDetector = new CustomGestureDetector();
        mGestureDetector = new GestureDetector(this, customGestureDetector);
        LinkedHashMap<Integer, String> data = new LinkedHashMap<>();

        // Set in/out flipping animations
        final String text = TextUtils.wordWrap(getIntent().getStringExtra(EngelMessenger.TEXT_KEY), 24);
        int[] resources = {R.raw.batman, R.raw.bolizei, R.raw.faschistan1, R.raw.cat, R.raw.einhorn, R.raw.einhorn2, R.raw.eule, R.raw.face, R.raw.gammler, R.raw.party, R.raw.partybot, R.raw.roflkopter};
        int[] figlets = {R.raw.banner3d, R.raw.contessa, R.raw.cybermedium, R.raw.isometric4, R.raw.larry3d, R.raw.mini, R.raw.shortf, R.raw.straight};

        int black = Color.parseColor("#000000");
        int white = Color.parseColor("#ffffff");
        int id = 0;
        for (int res : resources) {
            TextView newView = new TextView(this);
            newView.setTextColor(white);
            newView.setBackgroundColor(black);
            newView.setTypeface(Typeface.MONOSPACE);
            try {
                InputStream is = getResources().openRawResource(res);
                String s = IOUtils.toString(is);
                IOUtils.closeQuietly(is);
                newView.setText(text + "\n" + s);
            }
            catch (Exception ex){
                newView.setText(ex.getMessage());
            }
            data.put(id, "ID: "+ id);
            mViewFlipper.addView(newView, id, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            id++;
        }

        for (int figlet : figlets) {

            TextView newView = new TextView(this);
            newView.setTextColor(white);
            newView.setBackgroundColor(black);
            newView.setTypeface(Typeface.MONOSPACE);
            try {
                InputStream fontIn = getResources().openRawResource(figlet);
                newView.setText(FigletFont.convertOneLine(fontIn, text, 24));
                fontIn.close();
            }
            catch(Exception e){
                newView.setText(e.getMessage());
                e.printStackTrace();
            }
            newView.setVerticalScrollBarEnabled(true);
            data.put(id, "ID: "+ id);
            mViewFlipper.addView(newView, id, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            id++;
        }
        styleChooser = (Spinner) findViewById(R.id.styleSpinner);
        LinkedHashMapAdapter<Integer, String> styleAdapter =  new LinkedHashMapAdapter<Integer, String>(this, android.R.layout.simple_spinner_item, data);
        styleChooser.setAdapter(styleAdapter);
        styleChooser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mViewFlipper.setDisplayedChild(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
}
