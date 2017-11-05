
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

package info.minutesgone.tasks;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import info.minutesgone.models.ExcludedPhoneNumbers;
import info.minutesgone.shared.CallLogsManager;
import info.androidminiloggr.Logger;

public class ParseCallLogTask extends AsyncTask<Context,CallLogData,CallLogData> {

    private static Logger logger = Logger.getLogger(ParseCallLogTask.class.getName());

    private final OnTaskEnd<CallLogData> onTaskEnd;

    public ParseCallLogTask(OnTaskEnd<CallLogData> onTaskEnd) {
        this.onTaskEnd = onTaskEnd;
    }

    @Override
    protected CallLogData doInBackground(Context ...context) {

        CallLogsManager callLogsManager = new CallLogsManager(context[0]);

        List<ExcludedPhoneNumbers> excludedNumbers = callLogsManager.loadExcludedPhonesData();

        return callLogsManager.readCallLogs(excludedNumbers,context[0]);
    }

    @Override
    protected void onPostExecute(CallLogData result) {
        super.onPostExecute(result);

        onTaskEnd.onTaskEnd(result);

    }


}
