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


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.util.Pair;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import java.util.List;

import info.minutesgone.data.PersistenceService;
import info.minutesgone.data.sqllite.ExcludedPhoneNumbersPersistenceService;
import info.minutesgone.models.ExcludedPhoneNumbers;
import info.minutesgone.models.Preferences;
import info.minutesgone.tasks.CallLogData;
import info.minutesgone.tasks.ParseCallLogTask;
import info.androidminiloggr.Logger;

public class CallLogsManager {

    private static final Logger logger = Logger.getLogger(ParseCallLogTask.class.getName());

    private final PersistenceService<ExcludedPhoneNumbers> persistanceService;

    private Pair<LocalDate, LocalDateTime> dates = null;

    private String countryCode="";

    public CallLogsManager(Context context) {
        persistanceService = ExcludedPhoneNumbersPersistenceService.getInstance(context);

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




            return new CallLogData(true);
        }

        logger.d("readCallLOgs loaded logs");
        Cursor cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, columns, mSelectionClause, mSelectionArgs, null);

        CallLogData logData = handleLoadedCallLogs(cursor, excludedPhoneNumbers,preferences);

        if(cursor != null)cursor.close();

        return logData;
    }

    private CallLogData handleLoadedCallLogs(Cursor managedCursor, List<ExcludedPhoneNumbers> excludedPhoneNumbers, Preferences preferences) {
        int numberIndex = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int typeIndex = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
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
        //return new CallLogData(preferences.getMinutes(), 56, 56, 2, dates.first, dates.second);
    }

    private boolean checkIsNumberDomestic(Preferences preferences, String pNumber, PhoneNumberUtil pnu){

        logger.d("Phone device country code: ", pnu.getCountryCodeForRegion(countryCode));

        if (!preferences.isCountLocalCalls()) return true;

        Phonenumber.PhoneNumber pn;
        try {
            pn = pnu.parse(pNumber,countryCode);
            logger.d("Phone numbers country code: ", pn.getCountryCode());
        } catch (NumberParseException e) {
            logger.e("Error parsing phone number: ", pNumber,"exception: ", e);
            return true;
        }
        if ( pn.getCountryCode() == pnu.getCountryCodeForRegion(countryCode)) {
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

    private Pair<LocalDate, LocalDateTime> createDateRange(Preferences preferences) {
        LocalDate now = new LocalDate();
        int lastDayInMonth = now.dayOfMonth().getMaximumValue();
        int begginingDate = preferences.getDay();
        if(preferences.getDay() >= lastDayInMonth){
            begginingDate = lastDayInMonth -1;
        }
        LocalDate monthBegin = new LocalDate().withDayOfMonth(begginingDate);
        LocalDateTime monthEnd = monthBegin.plusMonths(1).plusDays(1).toLocalDateTime(LocalTime.MIDNIGHT);

        logger.d("dates: " + monthBegin + ", " + monthEnd);

        return new Pair(monthBegin, monthEnd);
    }

}
