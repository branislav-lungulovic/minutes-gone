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

package info.minutesgone.adapters;


import android.app.Activity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import java.util.ArrayList;

import info.androidminiloggr.Logger;
import info.minutesgone.tasks.LoadContactsTask;
import info.minutesgone.tasks.LoadedContactsData;
import info.minutesgone.tasks.OnTaskEnd;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class AutoCompleteContacts implements AdapterView.OnItemClickListener, OnTaskEnd<LoadedContactsData> {

    private static final Logger logger = Logger.getLogger(AutoCompleteContacts.class.getName());

    private EditText phoneEditText;
    private EditText contactNameEditText;

    private ArrayAdapter<String> adapter;

    private Activity activity;

    private static ArrayList<String> nameAndPhoneValueArr = new ArrayList<>();


    public static AutoCompleteContacts build(Activity activity) {

        AutoCompleteContacts newInstance = new AutoCompleteContacts();
        newInstance.activity = activity;
        return newInstance;

    }

    public AutoCompleteContacts setPhone(EditText editText) {

        this.phoneEditText = editText;
        return this;
    }

    public AutoCompleteContacts setContactName(EditText editText) {

        this.contactNameEditText = editText;
        return this;
    }

    public AutoCompleteContacts load() {

        if (nameAndPhoneValueArr.size() == 0) {
            new LoadContactsTask(this).execute(activity);
        } else {
            initializeAutocompleteTextFields();
        }
        return this;
    }

    private void setAutocompleteEditText(EditText editText) {

        if (editText instanceof AutoCompleteTextView) {
            AutoCompleteTextView editTextAuto = (AutoCompleteTextView) editText;
            if (adapter == null) {
                adapter = new ContainsArrayAdapter(activity, android.R.layout.simple_dropdown_item_1line, nameAndPhoneValueArr);
            }
            editTextAuto.setThreshold(1);
            editTextAuto.setAdapter(adapter);
            editTextAuto.setOnItemClickListener(this);
        }

    }

    protected AutoCompleteContacts() {
    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        // Get Array index value for selected name
        String contactName = "";
        String phone = "";


        String selected = (String) parent.getItemAtPosition(position);

        int pos = -1;
        for (int i = 0; i < nameAndPhoneValueArr.size(); i++) {
            if (nameAndPhoneValueArr.get(i).equals(selected)) pos = i;
        }
        if (pos > -1) {

            String nameAndPhone = nameAndPhoneValueArr.get(pos);

            String[] nameAndPhoneSplited = nameAndPhone.split(":");
            if (nameAndPhoneSplited.length > 1) {
                contactName = nameAndPhoneSplited[0].trim();
                phone = nameAndPhoneSplited[1].trim();
            } else if (nameAndPhoneSplited.length == 0) {
                phone = nameAndPhoneSplited[0].trim();
            }

            // Get Phone Number
            if (phoneEditText != null) phoneEditText.setText(phone);
            if (contactNameEditText != null) contactNameEditText.setText(contactName);
        }


        InputMethodManager imm = (InputMethodManager) activity.getSystemService(
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);


    }

    @Override
    public void onTaskEnd(LoadedContactsData data) {
        logger.d("onTaskEnd called with value: ", data);

        nameAndPhoneValueArr = data.nameAndPhoneValueArr;

        initializeAutocompleteTextFields();

    }

    private void initializeAutocompleteTextFields() {
        if (this.phoneEditText != null) setAutocompleteEditText(this.phoneEditText);
        if (this.contactNameEditText != null) setAutocompleteEditText(this.contactNameEditText);
    }
}
