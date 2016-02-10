package software.oi.engelfax.components;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;

import java.lang.ref.WeakReference;

import software.oi.engelfax.R;

/**
 * General SMS BroadcastReciver with callback
 * Created by Stefan Beukmann on 31.01.2016.
 */
public final class SmsBroadcastReceiver extends BroadcastReceiver {
    public interface SmsSentCallbacks {
        void onSmsSentSuccess();
        void onSmsSentError(String message);

    }
    private final WeakReference<SmsSentCallbacks> callback;
    public SmsBroadcastReceiver(SmsSentCallbacks callback){
        this.callback = new WeakReference<>(callback);
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        if (getResultCode() == Activity.RESULT_OK) {
            callback.get().onSmsSentSuccess();
        }
        else {
            String message = null;
            switch (getResultCode()) {

                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    message = context.getString(R.string.error_generic);
                    break;
                case SmsManager.RESULT_ERROR_NO_SERVICE:
                    message = context.getString(R.string.error_no_service);
                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU:
                    message = context.getString(R.string.error_null_pdu);
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                    message = context.getString(R.string.error_radio_off);
                    break;

            }
            callback.get().onSmsSentError(message);
        }
    }
}
