/*
 * minutes-gone
 * Copyright (C) 2017.  Author: Branislav Lungulovic
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package info.minutesgone.shared;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.widget.TextView;

import org.joda.time.LocalDate;

import info.minutesgone.R;
import info.minutesgone.activities.MainActivity;
import info.minutesgone.models.Preferences;

public class ActivityUtils {

    public static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    public static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 2;

    public static final int STATUS_BAR_NOTIFICATION_ID = 1;

    private static int baseStatusBarImage = R.drawable.si000;

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

        int planLimitPercent = sharedPref.getInt(context.getString(R.string.key_lastCalculatedPercent), 0);

        return new Preferences(day,minutes,alertLevel,countLocalCalls,lastNotificationSentDate,showNotificationInStatusBar,planLimitPercent);
    }

    public static String readStringPreference(Context context, String keyName){

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getString(keyName, "");

    }

    public static int readIntPreference(Context context, String keyName){

        return readIntPreference(context,keyName,-1);

    }

    public static int readIntPreference(Context context, String keyName, int defaultValue){

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getInt(keyName, defaultValue);

    }

    public static void saveStringPreference(Context context, String value, String keyName) {


        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(keyName, value);
        editor.apply();


    }

    public static void saveIntPreference(Context context, int value, String keyName) {


        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(keyName, value);
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

    public static void sendPermanentStatusBarNotification(Context context){

        int percentVal = ActivityUtils.readIntPreference(context,context.getString(R.string.key_lastCalculatedPercent,0));
        String message = String.format(context.getResources().getString(R.string.quota_notification),percentVal+"%");

        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Resources res = context.getResources();
        int smallIcon = calcIcon(context,percentVal);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentIntent(contentIntent)
                .setSmallIcon(baseStatusBarImage)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.hg_transp))
                .setContentTitle(context.getString(R.string.app_name))
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message))
                .setContentText(message)
                .setAutoCancel(true)
                .setOngoing(true)

        ;
        if(smallIcon != 0)builder.setSmallIcon(smallIcon);
        nm.notify(STATUS_BAR_NOTIFICATION_ID, builder.build());


    }

    private static int calcIcon(Context context, int percent){
        int newIcon = baseStatusBarImage + percent;
        int checkExistence = context.getResources().getIdentifier(String.valueOf(newIcon), "drawable", context.getPackageName());
        return checkExistence;
    }

    public static void cancelPermanentStatusBarNotification(Context context){



        NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        nm.cancel(STATUS_BAR_NOTIFICATION_ID);



    }

    public static void handleSendStatusBarNotification(Context context){
        Preferences pref = ActivityUtils.readPreferences(context);
        if(pref.isShowNotificationInStatusBar()){
            ActivityUtils.sendPermanentStatusBarNotification(context);
        }else{
            ActivityUtils.cancelPermanentStatusBarNotification(context);
        }
    }

    public static void sendAlertNotification(Context context,String message){

        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Resources res = context.getResources();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.ic_warning_black_24dp)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.ic_warning_black_24dp))
                .setContentTitle(context.getString(R.string.app_name))
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message))
                .setContentText(message)
                .setAutoCancel(true);
        nm.notify(0, builder.build());

        saveStringPreference(context,new LocalDate().toString(),context.getString(R.string.key_lastNotificationSentDate));

    }

    public static void checkIfToSendAlert(Context context, int planLimitPercent){

        saveIntPreference(context,planLimitPercent,context.getString(R.string.key_lastCalculatedPercent));

        Preferences preferences = readPreferences(context);

        if(preferences.getAlertLevel() > 0 && preferences.getAlertLevel() <= preferences.getLastCalculatedPercent() && notificationCanBeSend(preferences)){
            sendAlertNotification(context,String.format(context.getResources().getString(R.string.quota_alert),preferences.getLastCalculatedPercent()+"%"));
        }

        handleSendStatusBarNotification(context);
    }

    private static boolean notificationCanBeSend(Preferences preferences) {
        return !(preferences.getLastNotificationSentDate() != null && preferences.getLastNotificationSentDate().getMonthOfYear() == new LocalDate().getMonthOfYear());
    }


}
