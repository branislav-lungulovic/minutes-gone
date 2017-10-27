package info.talkalert.data.sqllite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import info.talkalert.data.PersistenceService;
import info.talkalert.models.ExcludedPhoneNumbers;

import static info.talkalert.models.ExcludedPhoneNumbers.ExcludedPhoneNumbersDbDef.*;


public class ExcludedPhoneNumbersPersistenceService implements PersistenceService<ExcludedPhoneNumbers> {

    private static ExcludedPhoneNumbersPersistenceService instance = new ExcludedPhoneNumbersPersistenceService();

    private static final String LOG_TAG = ExcludedPhoneNumbersPersistenceService.class.getName();

    private DatabaseHelper databaseHelper;
    private SQLiteDatabase wDatabase;

    public void init(Context context){
        if(databaseHelper == null){
            databaseHelper = new DatabaseHelper(context);
            wDatabase = databaseHelper.getWritableDatabase();
        }
    }

    public static ExcludedPhoneNumbersPersistenceService getInstance(Context context){
        instance.init(context);
        return instance;
    }

    @Override
    public void create(ExcludedPhoneNumbers entity) {

        if (wDatabase != null){
            //prepare the transaction information that will be saved to the database
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME, entity.getName());
            values.put(COLUMN_PHONE, entity.getPhone());
            try {
                wDatabase.insertOrThrow(TABLE_EXCLUDED_PHONE_NUMBERS, null, values);
                Log.d(LOG_TAG, "Phone Number Added");

            } catch (SQLException e) {
                Log.d(LOG_TAG, "Error " + e.getCause() + " " + e.getMessage());
            }
        }
    }

    @Override
    public void update(ExcludedPhoneNumbers entity) {

        if (wDatabase != null){
            // New value for one column
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME, entity.getName());
            values.put(COLUMN_PHONE, entity.getPhone());

            String selection = COLUMN_ID + " = ?";
            String[] selectionArgs = { Integer.toString(entity.getId()) };

            int count = wDatabase.update(
                    TABLE_EXCLUDED_PHONE_NUMBERS,
                    values,
                    selection,
                    selectionArgs);
        }
    }

    @Override
    public void delete(ExcludedPhoneNumbers entity) {

        if(wDatabase != null){
            String selection = COLUMN_ID + " = ?";
            String[] selectionArgs = { Integer.toString(entity.getId()) };
            wDatabase.delete(TABLE_EXCLUDED_PHONE_NUMBERS, selection, selectionArgs);
        }
    }

    @Override
    public void save(ExcludedPhoneNumbers entity) {
        if(entity.getId() > -1){
            update(entity);
        }else{
            create(entity);
        }
    }

    @Override
    public List<ExcludedPhoneNumbers> getAll() {

        Log.d(LOG_TAG,"Entered getAll");

        //initialize an empty list of customers
        List<ExcludedPhoneNumbers> ephones = new ArrayList<>();

        //sql command to select all Customers;
        String selectQuery = "SELECT * FROM " +TABLE_EXCLUDED_PHONE_NUMBERS;

        //make sure the database is not empty
        if (wDatabase != null) {

            //get a cursor for all customers in the database
            Cursor cursor = wDatabase.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    //get each customer in the cursor
                    ExcludedPhoneNumbers epn = new ExcludedPhoneNumbers();
                    epn.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                    epn.setPhone(cursor.getString(cursor.getColumnIndex(COLUMN_PHONE)));
                    epn.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));

                    ephones.add(epn);
                    cursor.moveToNext();
                }
            }
            cursor.close();
        }

        return ephones;
    }

    protected ExcludedPhoneNumbersPersistenceService() {
    }

    @Override
    public void close() {
        wDatabase.close();
    }
}
