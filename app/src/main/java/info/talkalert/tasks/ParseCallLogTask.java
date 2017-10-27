package info.talkalert.tasks;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import info.talkalert.models.ExcludedPhoneNumbers;
import info.talkalert.shared.CallLogsManager;
import info.talkalert.shared.Logger;
import info.talkalert.shared.LoggerUtils;

public class ParseCallLogTask extends AsyncTask<Context,CallLogData,CallLogData> {

    private static Logger logger = LoggerUtils.getLogger(ParseCallLogTask.class.getName());

    private OnTaskEnd<CallLogData> onTaskEnd;

    private CallLogsManager callLogsManager;

    public ParseCallLogTask(OnTaskEnd<CallLogData> onTaskEnd) {
        this.onTaskEnd = onTaskEnd;
    }

    @Override
    protected CallLogData doInBackground(Context ...context) {

        callLogsManager = new CallLogsManager(context[0]);

        List<ExcludedPhoneNumbers> excludedNumbers = callLogsManager.loadExcludedPhonesData();

        return callLogsManager.readCallLogs(excludedNumbers,context[0]);
    }

    @Override
    protected void onPostExecute(CallLogData result) {
        super.onPostExecute(result);

        onTaskEnd.onTaskEnd(result);

    }


}
