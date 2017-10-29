package info.talkalert.fragments;


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

import info.talkalert.R;
import info.talkalert.activities.MainActivity;
import info.talkalert.adapters.AutoCompleteContacts;
import info.talkalert.tasks.persistence.PersistenceServiceAsyncTask;
import info.talkalert.models.ExcludedPhoneNumbers;
import info.talkalert.shared.Logger;
import info.talkalert.shared.LoggerUtils;
import info.talkalert.tasks.persistence.OnSaveTaskEnd;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditPhoneFragment extends DialogFragment implements OnSaveTaskEnd{

    private static Logger logger = LoggerUtils.getLogger(EditPhoneFragment.class.getName());

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
