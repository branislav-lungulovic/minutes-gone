package info.minutesgone.tasks;

import android.app.Activity;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;

import info.androidminiloggr.Logger;

public class LoadContactsTask extends AsyncTask<Activity,LoadedContactsData,LoadedContactsData> {

    private static final Logger logger = Logger.getLogger(LoadContactsTask.class.getName());

    private final OnTaskEnd<LoadedContactsData> onTaskEnd;

    public LoadContactsTask(OnTaskEnd<LoadedContactsData> onTaskEnd) {
        this.onTaskEnd = onTaskEnd;
    }

    @Override
    protected LoadedContactsData doInBackground(Activity...activity) {
        return readContactData(activity[0]);
    }

    @Override
    protected void onPostExecute(LoadedContactsData loadedContactsData) {
        super.onPostExecute(loadedContactsData);

        onTaskEnd.onTaskEnd(loadedContactsData);
    }

    private LoadedContactsData readContactData(Activity activity) {

        LoadedContactsData data = new LoadedContactsData();

        try {

            String phoneNumber = "";


            //Query to get contact name

            Cursor cur =
                    activity.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
                            null,
                            null,
                            null,
                            null);

            // If data data found in contacts
            if (cur.getCount() > 0) {

                int k = 0;
                String name = "";

                while (cur.moveToNext()) {

                    String id = cur
                            .getString(cur
                                    .getColumnIndex(ContactsContract.Contacts._ID));
                    name = cur
                            .getString(cur
                                    .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                    //Check contact have phone number
                    if (Integer
                            .parseInt(cur
                                    .getString(cur
                                            .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {

                        //Create query to get phone number by contact id
                        Cursor pCur = activity.getContentResolver()
                                .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                        null,
                                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                                + " = ?",
                                        new String[]{id},
                                        null);
                        int j = 0;

                        while (pCur
                                .moveToNext()) {
                            // Sometimes get multiple data
                            if (j == 0) {
                                // Get Phone number
                                phoneNumber = "" + pCur.getString(pCur
                                        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                                String nameAndNumber = name + ": " + phoneNumber;

                                // Add contacts names to adapter
                                logger.d("readContactData added: ", phoneNumber);


                                // Add ArrayList names to adapter
                                data.nameAndPhoneValueArr.add(nameAndNumber);

                                j++;
                                k++;
                            }
                        }  // End while loop
                        pCur.close();
                    } // End if

                }  // End while loop

            } // End Cursor value check
            cur.close();


        } catch (Exception e) {
            logger.e("AutocompleteContacts", "Exception : " + e);
        }

        return data;
    }
}
