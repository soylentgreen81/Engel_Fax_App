package software.oi.engelfax;

import android.app.ActionBar;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import org.apache.commons.io.IOUtils;

import java.io.InputStream;

/**
 * Created by stefa_000 on 14.12.2015.
 */
public class EngelSelectStyle extends AppCompatActivity {
    private ViewFlipper mViewFlipper;
    private GestureDetector mGestureDetector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_engel_selectstyle);
        mViewFlipper = (ViewFlipper ) findViewById(R.id.previews);
        CustomGestureDetector customGestureDetector = new CustomGestureDetector();
        mGestureDetector = new GestureDetector(this, customGestureDetector);
        // Set in/out flipping animations

        int[] ressources = {R.raw.batman, R.raw.bolizei, R.raw.faschistan1, R.raw.cat, R.raw.einhorn, R.raw.einhorn2, R.raw.eule, R.raw.face, R.raw.gammler, R.raw.party, R.raw.partybot, R.raw.roflkopter};
        int black = Color.parseColor("#000000");
        int white = Color.parseColor("#ffffff");
        try {
            for (int i=0;i<ressources.length;i++) {
                InputStream is = getResources().openRawResource(ressources[i]);
                String s = IOUtils.toString(is);
                IOUtils.closeQuietly(is);
                TextView newView = new TextView(this);
                newView.setTextColor(white);
                newView.setBackgroundColor(black);
                newView.setTypeface(Typeface.MONOSPACE);
                newView.setText(s);
                mViewFlipper.addView(newView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
            }
        } catch(Exception ex){

         }
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
            }

            // Swipe right (previous)
            if (e1.getX() < e2.getX()) {
                mViewFlipper.setInAnimation(EngelSelectStyle.this, R.anim.slide_in_left);
                mViewFlipper.setOutAnimation(EngelSelectStyle.this, R.anim.slide_out_right);
                mViewFlipper.showPrevious();
            }

            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }
}
