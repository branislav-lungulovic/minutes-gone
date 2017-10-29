package info.talkalert.broadcastreceivers;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import org.joda.time.LocalDate;

import info.talkalert.R;
import info.talkalert.activities.MainActivity;
import info.talkalert.models.Preferences;
import info.talkalert.shared.ActivityUtils;
import info.talkalert.shared.Logger;
import info.talkalert.shared.LoggerUtils;
import info.talkalert.tasks.CallLogData;
import info.talkalert.tasks.OnTaskEnd;
import info.talkalert.tasks.ParseCallLogTask;

public class CallEndReceiver extends BroadcastReceiver implements OnTaskEnd<CallLogData>{

    private static Logger logger = LoggerUtils.getLogger(CallEndReceiver.class.getName());

    private static final String TAG = "MyBroadcastReceiver";
    private PendingResult pendingResult;
    private Context context;
    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;

        pendingResult =  goAsync();

        new ParseCallLogTask(this).execute(context);



    }

    @Override
    public void onTaskEnd(CallLogData logData) {

        if(logData.isPermissionError()){
            Toast.makeText(context, "Please enable call logs read access.", Toast.LENGTH_LONG).show();
            return;
        }

        Preferences preferences = ActivityUtils.readPreferences(context);

        if(preferences.getAlertLevel() > 0 && preferences.getAlertLevel() <= logData.getPlanaLimitPercent() && notificationCanBeSend(preferences)){
            sendNotification(String.format(context.getResources().getString(R.string.quota_alert),logData.getPlanaLimitPercent()+"%"));
        }

        pendingResult.finish();
    }

    private boolean notificationCanBeSend(Preferences preferences) {
        return !(preferences.getLastNotificationSentDate() != null && preferences.getLastNotificationSentDate().getMonthOfYear() == new LocalDate().getMonthOfYear());
    }

    private void sendNotification(String message){

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

        ActivityUtils.saveStringPreference(context,new LocalDate().toString(),context.getString(R.string.key_lastNotificationSentDate));

    }


}
