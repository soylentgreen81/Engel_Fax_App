package software.oi.engelfax.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import software.oi.engelfax.R;
import software.oi.engelfax.util.GsmUtils;
import software.oi.engelfax.util.Preferences;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.TestCase.assertEquals;

/**
 * Tests for the PreviewActivity
 * Created by Stefan Beukmann on 04.02.2016.
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public abstract class PreviewActivityTest {
    protected  abstract String getMessage();
    private static final String TAG = PreviewActivityTest.class.getSimpleName();
    public ActivityTestRule<PreviewActivity> mActivityRule = new ActivityTestRule<PreviewActivity>(PreviewActivity.class, true, false);
    private InterceptSmsReceiver mReceiver;
    private String mSmsText;
    private Activity mActivity;
    @Before
    public void setUp(){
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext();
        Preferences.saveNumber(appContext, "4711");
        Intent intent = PreviewActivity.makeIntent(appContext, getMessage());
        mReceiver = new InterceptSmsReceiver();
        mActivity = mActivityRule.launchActivity(intent);
        mActivity.registerReceiver(mReceiver, new IntentFilter(GsmUtils.SMS_SENT));
    }

    @After
    public void tearDown(){
       try {
           mActivity.unregisterReceiver(mReceiver);
       }
       catch (Exception ex ){  }
    }
    @Test
    public void testSimpleSMS() throws InterruptedException {
        onView(withId(R.id.fab)).perform(click());
        Thread.sleep(1000);
        assertEquals(getMessage(), mSmsText);
    }
    @Test
    public void testPrefixSMS() throws InterruptedException {
        onView(withId(R.id.container)).perform(swipeLeft());
        onView(withId(R.id.fab)).perform(click());
        Thread.sleep(1000);
        assertEquals("#AA " + getMessage(), mSmsText);
    }


    /**
     * Broadcast receiver which intercepts the sending of the sms
     * and sets it's body to the mText field
     */
    public class InterceptSmsReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();

                String sms = (String) bundle.get("uri");
                Uri smsUri = Uri.parse(sms);
                Cursor cursor = mActivity.getContentResolver().query(smsUri, null,
                        null, null, null);

                if (cursor.moveToNext()) {
                    for (int i = 0; i < cursor.getColumnCount(); i++) {
                        String columnName = cursor.getColumnName(i);
                        if ("body".equals(columnName)) {
                            mSmsText = cursor.getString(i);
                        }

                    }
                }

            setResultCode(android.app.Activity.RESULT_OK);
        }
    }
}
