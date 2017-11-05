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

package info.minutesgone.broadcastreceivers;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import info.minutesgone.shared.ActivityUtils;
import info.androidminiloggr.Logger;
import info.minutesgone.tasks.CallLogData;
import info.minutesgone.tasks.OnTaskEnd;
import info.minutesgone.tasks.ParseCallLogTask;

public class OutgoingCallStartReceiver extends BroadcastReceiver implements OnTaskEnd<CallLogData>{

    private static Logger logger = Logger.getLogger(OutgoingCallStartReceiver.class.getName());

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
