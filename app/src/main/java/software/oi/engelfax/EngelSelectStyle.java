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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

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
    private LinkedHashMap<String, String> arts = new LinkedHashMap<>();
    private LinkedHashMap<String, String> fonts = new LinkedHashMap<>();
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
        try {
            readCSV("asciiart/art.csv", arts);
            readCSV("fonts/fonts.csv", fonts);

        }
        catch (Exception ex){

        }
        int id = 0;
        for (String asciiArt: arts.keySet()) {
            TextView newView = createTextView();
            try {
                InputStream is = getAssets().open("asciiart/" + asciiArt);
                String s = IOUtils.toString(is);
                IOUtils.closeQuietly(is);
                newView.setText(text + "\n" + s);
            }
            catch (Exception e){
                newView.setText(e.getMessage());
            }
            data.put(id, asciiArt);
            mViewFlipper.addView(newView, id, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            id++;
        }

        for (String font : fonts.keySet()) {
            TextView newView = createTextView();
            try {
                InputStream is = getAssets().open("fonts/" + font + ".flf");
                String figletText = TextUtils.wordWrap(text, 24, new FigletPrinter(new FigletFont(is)));
                newView.setText(figletText);
                IOUtils.closeQuietly(is);
            }
            catch(Exception e){
                newView.setText(e.getMessage());
            }
            data.put(id, font);
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


    private void readCSV(String path, Map<String, String> theMap) throws IOException{
        InputStream in = getAssets().open(path);
        TextUtils.readCSV(IOUtils.toString(in), theMap);
        IOUtils.closeQuietly(in);
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
}
