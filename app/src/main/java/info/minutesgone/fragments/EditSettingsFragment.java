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

package info.minutesgone.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import info.minutesgone.R;
import info.minutesgone.models.Preferences;
import info.minutesgone.shared.ActivityUtils;
import info.minutesgone.tasks.CallLogData;
import info.minutesgone.tasks.OnTaskEnd;
import info.minutesgone.tasks.ParseCallLogTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditSettingsFragment extends Fragment implements OnTaskEnd<CallLogData> {

    private EditText edDay;
    private EditText edMinutes;
    private EditText edAlertLevel;
    private SwitchCompat swCountLocalCalls;
    private SwitchCompat showNotificationInStatusBar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_edit_settings, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.action_settings));

        if (savedInstanceState == null) {
            final Button btnCancel = (Button) view.findViewById(R.id.btnCancel);
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().getSupportFragmentManager().popBackStackImmediate();
                }
            });
            btnCancel.setFocusable(true);
            btnCancel.setFocusableInTouchMode(true);
            btnCancel.requestFocus();

            edDay = (EditText) view.findViewById(R.id.edDay);
            edMinutes = (EditText) view.findViewById(R.id.edMinutes);
            edAlertLevel = (EditText) view.findViewById(R.id.edAlertLevel);
            swCountLocalCalls = (SwitchCompat) view.findViewById(R.id.swCountLocalCalls);
            showNotificationInStatusBar = (SwitchCompat) view.findViewById(R.id.showNotificationInStatusBar);

            Preferences preferences = ActivityUtils.readPreferences(getActivity());

            edDay.setText(Integer.toString(preferences.getDay()));
            edMinutes.setText(Integer.toString(preferences.getMinutes()));
            edAlertLevel.setText(Integer.toString(preferences.getAlertLevel()));
            swCountLocalCalls.setChecked(preferences.isCountLocalCalls());
            showNotificationInStatusBar.setChecked(preferences.isShowNotificationInStatusBar());

            final Button btnSave = (Button) view.findViewById(R.id.btnSave);
            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveSettings();
                }
            });

            setRetainInstance(true);


        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        ActivityUtils.handleSendStatusBarNotification(getContext());


    }

    private boolean validateInputs() {

        if (edDay.getText().toString().isEmpty()) {
            edDay.setError(getString(R.string.dayInMonth_required));
            edDay.requestFocus();
            return false;
        }

        if (edMinutes.getText().toString().isEmpty()) {
            edMinutes.setError(getString(R.string.minutes_required));
            edMinutes.requestFocus();
            return false;
        }

        if (edAlertLevel.getText().toString().isEmpty()) {
            edAlertLevel.setError(getString(R.string.alert_level_perc_error));
            edAlertLevel.requestFocus();
            return false;
        } else {
            int aValue = Integer.parseInt(edAlertLevel.getText().toString());
            if (!(0 <= aValue && aValue <= 100)) {
                edAlertLevel.setError(getString(R.string.alert_level_perc_error));
                edAlertLevel.requestFocus();
                return false;
            }
        }


        return true;
    }

    private void saveSettings() {

        if (!validateInputs()) return;

        ActivityUtils.saveSettings(getActivity(), Integer.parseInt(edDay.getText().toString()), Integer.parseInt(edMinutes.getText().toString()), Integer.parseInt(edAlertLevel.getText().toString()), swCountLocalCalls.isChecked(), showNotificationInStatusBar.isChecked());

        new ParseCallLogTask(this).execute(getActivity());



    }

    @Override
    public void onTaskEnd(CallLogData data) {
        if(isAdded()){

            if(data.isPermissionError()){
                Toast.makeText(getContext(), "Please enable call logs read access.", Toast.LENGTH_LONG).show();
                return;
            }

            ActivityUtils.checkIfToSendAlert(getContext(),data.getPlanaLimitPercent());

            Toast.makeText(getActivity(), R.string.settings_saved, Toast.LENGTH_SHORT).show();
        }

    }


}
