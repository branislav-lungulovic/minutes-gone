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


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import info.minutesgone.R;
import info.minutesgone.activities.MainActivity;
import info.minutesgone.adapters.AutoCompleteContacts;
import info.minutesgone.tasks.persistence.PersistenceServiceAsyncTask;
import info.minutesgone.models.ExcludedPhoneNumbers;
import info.androidminiloggr.Logger;
import info.minutesgone.tasks.persistence.OnSaveTaskEnd;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditPhoneFragment extends DialogFragment implements OnSaveTaskEnd{

    private static Logger logger = Logger.getLogger(EditPhoneFragment.class.getName());

    private final String LOG_TAG = getClass().getName();

    private DialogInterface.OnDismissListener onDismissListener;

    private boolean mInEditMode = false;

    private ExcludedPhoneNumbers excludedPhoneNumbers;

    private AutoCompleteTextView nameEditText;
    private AutoCompleteTextView phoneEditText;

    private AutoCompleteContacts phoneAutoC;

    private String idVal = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            excludedPhoneNumbers = (ExcludedPhoneNumbers)getArguments().getSerializable("excludedPhoneNumber");
        }

        if(excludedPhoneNumbers == null){
            excludedPhoneNumbers = new ExcludedPhoneNumbers();
        }

        mInEditMode = excludedPhoneNumbers.getId() > 0;

        setRetainInstance(true);

    }

    private PersistenceServiceAsyncTask<ExcludedPhoneNumbers> createPersistanceTask(){
        return new PersistenceServiceAsyncTask<>(((MainActivity)getActivity()).getPersistenceService(),this);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialogFragment = new AlertDialog.Builder(getActivity());
        if (savedInstanceState == null){
            LayoutInflater inflater = getActivity().getLayoutInflater();

            View rootView = View.inflate(getContext(),R.layout.fragment_edit_phone, null);
            dialogFragment.setView(rootView);

            View titleView = View.inflate(getContext(),R.layout.dialog_title, null);
            TextView titleText = (TextView)titleView.findViewById(R.id.text_view_dialog_title);
            titleText.setText(mInEditMode ? getString(R.string.change_number) : getString(R.string.add_number));
            dialogFragment.setCustomTitle(titleView);

            dialogFragment.setPositiveButton(mInEditMode ? getString(R.string.change) : getString(R.string.add), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            dialogFragment.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            nameEditText = (AutoCompleteTextView)rootView.findViewById(R.id.contact_name);
            phoneEditText   = (AutoCompleteTextView)rootView.findViewById(R.id.phone);

            populateForm(excludedPhoneNumbers);

            AutoCompleteContacts.build(getActivity()).setPhone(phoneEditText).setContactName(nameEditText).load();

        }

        return dialogFragment.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        AlertDialog d = (AlertDialog)getDialog();

        if (d != null){
            Button negativeButton = d.getButton(Dialog.BUTTON_NEGATIVE);
            negativeButton.setFocusable(true);
            negativeButton.setFocusableInTouchMode(true);
            negativeButton.requestFocus();

            Button positiveButton = d.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    boolean readyToCloseDialog = false;

                    if (validateInputs()) {
                        saveData();
                        readyToCloseDialog = true;
                    }
                    if (readyToCloseDialog)
                        dismiss();
                }
            });
        }
    }

    private void saveData() {

        populateBean();

        createPersistanceTask().save(excludedPhoneNumbers);

    }

    private void populateForm(ExcludedPhoneNumbers excludedPhoneNumbers){

        nameEditText.setText(excludedPhoneNumbers.getName());
        phoneEditText.setText(excludedPhoneNumbers.getPhone());
    }

    private void populateBean(){
        excludedPhoneNumbers.setName(nameEditText.getText().toString());
        excludedPhoneNumbers.setPhone(phoneEditText.getText().toString());
    }

    private void displayMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    private boolean validateInputs() {


        if (phoneEditText.getText().toString().isEmpty())
        {
            phoneEditText.setError(getString(R.string.phone_is_required));
            phoneEditText.requestFocus();
            return false;
        }

        return true;
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (onDismissListener != null) {
            onDismissListener.onDismiss(dialog);
        }
    }

    @Override
    public void onSaveTaskEnd() {
        if(isAdded()){
            displayMessage(getString(R.string.contact_saved));
        }

    }
}
