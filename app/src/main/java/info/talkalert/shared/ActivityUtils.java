package info.talkalert.shared;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.widget.TextView;

import org.joda.time.LocalDate;

import info.talkalert.R;
import info.talkalert.activities.MainActivity;
import info.talkalert.models.Preferences;

public class ActivityUtils {

    public static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    public static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 2;

    public static final int STATUS_BAR_NOTIFICATION_ID = 1;

    public static Preferences readPreferences(Context context){

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        int defaultDay = context.getResources().getInteger(R.integer.default_day);
        int day = sharedPref.getInt(context.getString(R.string.key_day), defaultDay);

        int defaultMinutes = context.getResources().getInteger(R.integer.default_minutes);
        int minutes = sharedPref.getInt(context.getString(R.string.key_minutes), defaultMinutes);

        int defaultAlertLevel = context.getResources().getInteger(R.integer.default_alert_level);
        int alertLevel = sharedPref.getInt(context.getString(R.string.key_alert_level), defaultAlertLevel);

        boolean defaultCountLocalCalls = context.getResources().getBoolean(R.bool.default_count_local_calls);
        boolean countLocalCalls = sharedPref.getBoolean(context.getString(R.string.key_count_local_calls), defaultCountLocalCalls);

        boolean defaultShowNotificationInStatusBar = context.getResources().getBoolean(R.bool.default_showNotificationInStatusBar);
        boolean showNotificationInStatusBar = sharedPref.getBoolean(context.getString(R.string.key_showNotificationInStatusBar), defaultShowNotificationInStatusBar);

        String lastNotificationSentDateStr = sharedPref.getString(context.getString(R.string.key_lastNotificationSentDate), "");
        LocalDate lastNotificationSentDate = (lastNotificationSentDateStr.isEmpty())?null:new LocalDate(lastNotificationSentDateStr);

        return new Preferences(day,minutes,alertLevel,countLocalCalls,lastNotificationSentDate,showNotificationInStatusBar);
    }

    public static String readStringPreference(Context context, String keyName){

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getString(keyName, "");

    }

    public static int readIntPreference(Context context, String keyName){

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getInt(keyName, -1);

    }

    public static void saveStringPreference(Context context, String value, String keyName) {


        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(keyName, value);
        editor.apply();


    }

    public static void saveSettings(Context context, int day, int minutes, int alertLevel, boolean countLocalCalls, boolean showNotificationInStatusBar) {


        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(context.getString(R.string.key_day), day);
        editor.putInt(context.getString(R.string.key_minutes), minutes);
        editor.putInt(context.getString(R.string.key_alert_level), alertLevel);
        editor.putBoolean(context.getString(R.string.key_count_local_calls), countLocalCalls);
        editor.putBoolean(context.getString(R.string.key_showNotificationInStatusBar), showNotificationInStatusBar);
        editor.apply();


    }

    public static void setHtmlText(TextView tv, String data){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            tv.setText(Html.fromHtml(data,Html.FROM_HTML_MODE_LEGACY));
        } else {
            tv.setText(Html.fromHtml(data));
        }
    }



    static String getContryCodeFromPhone(Context context){

        TelephonyManager telephonyMngr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        String contryCodeFromPhone = telephonyMngr.getSimCountryIso();

        return contryCodeFromPhone != null?contryCodeFromPhone:"";

    }

    public static void sendStatusBarNotification(Context context,String message){

        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Resources res = context.getResources();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.hg_transp))
                .setContentTitle(context.getString(R.string.app_name))
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message))
                .setContentText(message)
                .setAutoCancel(true)
                .setOngoing(true)
                .setProgress(100,33,false)
        ;
        nm.notify(STATUS_BAR_NOTIFICATION_ID, builder.build());

        ActivityUtils.saveStringPreference(context,new LocalDate().toString(),context.getString(R.string.key_lastNotificationSentDate));

    }

    public static void cancelStatusBarNotification(Context context){



        NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        nm.cancel(STATUS_BAR_NOTIFICATION_ID);



    }

    public static void handleStatusBarNotification(Context context,boolean isChecked){
        if(isChecked){
            ActivityUtils.sendStatusBarNotification(context,"Message message");
        }else{
            ActivityUtils.cancelStatusBarNotification(context);
        }
    }



}
