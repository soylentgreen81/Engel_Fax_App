package software.oi.engelfax.util;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;

import java.util.ArrayList;

import software.oi.engelfax.R;

/**
 * Created by Stefan Beukmann on 31.01.2016.
 */
public abstract class GsmUtils {
    public static final String SMS_SENT = "SMS_SENT";
    public static final String SMS_DELIVERED = "SMS_DELIVERED";

    public static void sendSms(final Context context, String message) throws PhoneNumberException{
        String phoneNumber = Preferences.getNumber(context);
        if (PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber)) {

            SmsManager smsManager = SmsManager.getDefault();
            ArrayList<String>  parts = smsManager.divideMessage(message);
            ArrayList<PendingIntent> sentIntents = new ArrayList<>(parts.size());
            ArrayList<PendingIntent> deliveredIntents = new ArrayList<>(parts.size());
            for (int i=0;i<parts.size();i++){
                sentIntents.add(PendingIntent.getBroadcast(context, 0, new Intent(SMS_SENT), 0));
                deliveredIntents.add(PendingIntent.getBroadcast(context, 0, new Intent(SMS_DELIVERED), 0));
            }
            if (parts.size() == 1) {
                smsManager.sendTextMessage(phoneNumber, null, parts.get(0), sentIntents.get(0), deliveredIntents.get(0));
            } else {
                smsManager.sendMultipartTextMessage(phoneNumber, null, parts, sentIntents, deliveredIntents);

            }
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
