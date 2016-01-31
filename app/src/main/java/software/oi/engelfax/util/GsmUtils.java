package software.oi.engelfax.util;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;

import software.oi.engelfax.R;

/**
 * Created by Stefan Beukmann on 31.01.2016.
 */
public abstract class GsmUtils {
    public static final String SMS_SENT = "SMS_SENT";

    public static void sendSms(final Context context, String message) throws PhoneNumberException{
        String phoneNumber = Preferences.getNumber(context);
        if (PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber)) {
            PendingIntent sentPendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(SMS_SENT), 0);
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null,  message, sentPendingIntent, null);
        }
        else {
            String error;
            if (phoneNumber == null || "".equals(phoneNumber)) {
                error = context.getString(R.string.error_no_phone_no);
            }
            else {
                error = String.format(context.getString(R.string.error_phone_no), phoneNumber);
            }
            throw new PhoneNumberException(error);
        }
    }
}
