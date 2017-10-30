package info.minutesgone.tasks;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import info.minutesgone.models.ExcludedPhoneNumbers;
import info.minutesgone.shared.CallLogsManager;
import info.minutesgone.shared.Logger;
import info.minutesgone.shared.LoggerUtils;

public class ParseCallLogTask extends AsyncTask<Context,CallLogData,CallLogData> {

    private static Logger logger = LoggerUtils.getLogger(ParseCallLogTask.class.getName());

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
