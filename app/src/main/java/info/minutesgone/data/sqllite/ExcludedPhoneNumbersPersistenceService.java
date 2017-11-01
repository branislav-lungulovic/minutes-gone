package info.minutesgone.data.sqllite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import info.minutesgone.data.PersistenceService;
import info.minutesgone.models.ExcludedPhoneNumbers;
import info.minutesgone.shared.Logger;

import static info.minutesgone.models.ExcludedPhoneNumbers.ExcludedPhoneNumbersDbDef.COLUMN_ID;
import static info.minutesgone.models.ExcludedPhoneNumbers.ExcludedPhoneNumbersDbDef.COLUMN_NAME;
import static info.minutesgone.models.ExcludedPhoneNumbers.ExcludedPhoneNumbersDbDef.COLUMN_PHONE;
import static info.minutesgone.models.ExcludedPhoneNumbers.ExcludedPhoneNumbersDbDef.TABLE_EXCLUDED_PHONE_NUMBERS;


public class ExcludedPhoneNumbersPersistenceService implements PersistenceService<ExcludedPhoneNumbers> {

    private static final ExcludedPhoneNumbersPersistenceService instance = new ExcludedPhoneNumbersPersistenceService();

    private static Logger logger = Logger.getLogger(ExcludedPhoneNumbersPersistenceService.class.getName());

    private DatabaseHelper databaseHelper;
    private SQLiteDatabase wDatabase;

    private void init(Context context){
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
                logger.d("Phone Number Added");

            } catch (SQLException e) {
                logger.d("Error " + e.getCause() + " " + e.getMessage());
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

            wDatabase.update(
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

        logger.d("Entered getAll");

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

    private ExcludedPhoneNumbersPersistenceService() {
    }

    @Override
    public void close() {
        wDatabase.close();
    }
}
