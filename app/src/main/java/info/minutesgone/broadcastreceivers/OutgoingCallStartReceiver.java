package info.minutesgone.broadcastreceivers;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import info.minutesgone.R;
import info.minutesgone.shared.ActivityUtils;
import info.minutesgone.shared.Logger;
import info.minutesgone.shared.LoggerUtils;
import info.minutesgone.tasks.CallLogData;
import info.minutesgone.tasks.OnTaskEnd;
import info.minutesgone.tasks.ParseCallLogTask;

public class OutgoingCallStartReceiver extends BroadcastReceiver implements OnTaskEnd<CallLogData>{

    private static Logger logger = LoggerUtils.getLogger(OutgoingCallStartReceiver.class.getName());

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
    public void onTaskEnd(CallLogData data) {

        if(data.isPermissionError()){
            Toast.makeText(context, "Please enable call logs read access.", Toast.LENGTH_LONG).show();
            return;
        }

        ActivityUtils.checkIfToSendAlert(context,data.getPlanaLimitPercent());

        pendingResult.finish();
    }


}
