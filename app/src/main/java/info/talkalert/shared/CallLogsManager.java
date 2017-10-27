package info.talkalert.shared;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.util.Pair;
import android.widget.Toast;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import java.util.List;

import info.talkalert.data.PersistenceService;
import info.talkalert.data.sqllite.ExcludedPhoneNumbersPersistenceService;
import info.talkalert.models.ExcludedPhoneNumbers;
import info.talkalert.models.Preferences;
import info.talkalert.tasks.CallLogData;
import info.talkalert.tasks.ParseCallLogTask;

public class CallLogsManager {

    private static Logger logger = LoggerUtils.getLogger(ParseCallLogTask.class.getName());

    private PersistenceService<ExcludedPhoneNumbers> persistanceService;

    Pair<LocalDate, LocalDateTime> dates = null;

    private String countryCode="";

    public CallLogsManager(Context context) {
        persistanceService = ExcludedPhoneNumbersPersistenceService.getInstance(context);
        ;
    }

    public CallLogData readCallLogs(List<ExcludedPhoneNumbers> excludedPhoneNumbers, Context context) {

        countryCode=ActivityUtils.getContryCodeFromPhone(context);
        countryCode=countryCode.toUpperCase();

        Preferences preferences = ActivityUtils.readPreferences(context);

        String[] columns = new String[]{CallLog.Calls.DURATION, CallLog.Calls.NUMBER, CallLog.Calls.TYPE, CallLog.Calls.DATE};

        String mSelectionClause = CallLog.Calls.DATE + " >= ? and " + CallLog.Calls.DATE + " < ?";
        dates = createDateRange(preferences);
        String[] mSelectionArgs = {String.valueOf(dates.first.toDate().getTime()), String.valueOf(dates.second.toDate().getTime())};

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(context, "Error parsing call logs. Please enable call logs read access.", Toast.LENGTH_LONG).show();
            return null;
        }

        logger.d("readCallLOgs loaded logs");
        Cursor cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, columns, mSelectionClause, mSelectionArgs, null);

        CallLogData logData = handleLoadedCallLogs(cursor, excludedPhoneNumbers,preferences);

        cursor.close();

        return logData;
    }

    private CallLogData handleLoadedCallLogs(Cursor managedCursor, List<ExcludedPhoneNumbers> excludedPhoneNumbers, Preferences preferences) {
        int numberIndex = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int typeIndex = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int dateIndex = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int durationIndex = managedCursor.getColumnIndex(CallLog.Calls.DURATION);

        int count = 0;
        int durationSum = 0;

        int duration;
        int durationSumInMinutes;

        int excludedDuration;
        int excludedDurationSum = 0;

        int notDomesticDuration;
        int notDomesticSum = 0;


        int excludedDurationSumInMinutes;

        float durationPercents = 0f;
        int type;


        PhoneNumberUtil pnu = PhoneNumberUtil.getInstance();

        while (managedCursor.moveToNext()) {
            type = managedCursor.getInt(typeIndex);

            if (type == CallLog.Calls.OUTGOING_TYPE) {

                String pNumber = managedCursor.getString(numberIndex);


                if (checkIsNumberDomestic(preferences, pNumber, pnu)) {
                    if (!checkInExcludedList(pNumber, excludedPhoneNumbers, pnu)) {
                        count++;
                        duration = managedCursor.getInt(durationIndex);
                        durationSum += duration;
                    } else {
                        excludedDuration = managedCursor.getInt(durationIndex);
                        excludedDurationSum += excludedDuration;
                    }
                } else {
                    notDomesticDuration = managedCursor.getInt(durationIndex);
                    notDomesticSum += notDomesticDuration;
                }


            }


        }

        //durationSum = 56*60;
        durationSumInMinutes = Math.round(durationSum / 60);
        excludedDurationSum += notDomesticSum;

        logger.d("Durations in sec: durationSum: ", durationSum, " excludedDurationSum: ",excludedDurationSum, " notDomesticSum ", notDomesticSum);

        excludedDurationSumInMinutes = Math.round(excludedDurationSum / 60);
        if (preferences.getMinutes() > 0)
            durationPercents = ((float) durationSumInMinutes / preferences.getMinutes()) * 100;
        int finalDurationPercents = Math.min(100, Math.round(durationPercents));
        logger.d("preferences.getMinutes(): ", preferences.getMinutes(), ", count: ", count, ", durationSum: ", durationSum, ", durationSumInMinutes: ", durationSumInMinutes, ", durationPercents:", durationPercents, ", finalDurationPercents:", finalDurationPercents);

        managedCursor.close();

        return new CallLogData(preferences.getMinutes(), finalDurationPercents, durationSumInMinutes, excludedDurationSumInMinutes, dates.first, dates.second);
    }

    private boolean checkIsNumberDomestic(Preferences preferences, String pNumber, PhoneNumberUtil pnu){

        logger.d("Phone device country code: ", pnu.getCountryCodeForRegion(countryCode));

        if (!preferences.countLocalCalls()) return true;

        Phonenumber.PhoneNumber pn=null;
        try {
            pn = pnu.parse(pNumber,countryCode);
            logger.d("Phone numbers country code: ", pn.getCountryCode());
        } catch (NumberParseException e) {
            logger.e("Error parsing phone number: ", pNumber,"exception: ", e);
            return true;
        }
        if (pn != null && pn.getCountryCode() == pnu.getCountryCodeForRegion(countryCode)) {
            logger.d("Phone number: ", pNumber, " IS domestic call ");
            return true;
        }

        logger.d("Phone number: ", pNumber, " IS NOT domestic call ");

        return false;
    }

    private boolean checkInExcludedList(String checkNum, List<ExcludedPhoneNumbers> excludedPhoneNumbers, PhoneNumberUtil pnu) {

        PhoneNumberUtil.MatchType mt;

        for (ExcludedPhoneNumbers excludedPhoneNumber : excludedPhoneNumbers) {

            mt = pnu.isNumberMatch(excludedPhoneNumber.getPhone(), checkNum);
            if (mt == PhoneNumberUtil.MatchType.NSN_MATCH || mt == PhoneNumberUtil.MatchType.EXACT_MATCH || mt == PhoneNumberUtil.MatchType.SHORT_NSN_MATCH) {
                logger.d("Excluded phone number matched: ", excludedPhoneNumber.getPhone(), " and ", checkNum);
                return true;
            }

        }

        return false;
    }

    public List<ExcludedPhoneNumbers> loadExcludedPhonesData() {
        return persistanceService.getAll();
    }

    public Pair<LocalDate, LocalDateTime> createDateRange(Preferences preferences) {

        LocalDate monthBegin = new LocalDate().withDayOfMonth(preferences.getDay());
        LocalDateTime monthEnd = monthBegin.plusMonths(1).plusDays(1).toLocalDateTime(LocalTime.MIDNIGHT);

        logger.d("dates: " + monthBegin + ", " + monthEnd);

        return new Pair(monthBegin, monthEnd);
    }

}