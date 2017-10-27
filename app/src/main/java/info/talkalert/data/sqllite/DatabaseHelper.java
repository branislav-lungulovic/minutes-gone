package info.talkalert.data.sqllite;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static info.talkalert.models.ExcludedPhoneNumbers.ExcludedPhoneNumbersDbDef.*;

public class DatabaseHelper extends SQLiteOpenHelper {
    private final static String LOG_TAG = DatabaseHelper.class.getSimpleName();
    private final static int DATABASE_VERSION= 1;
    public static final String DATABASE_NAME = "talkalert.db";

    private Context mContext;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_TABLE_EXCLUDED_PHONE_NUMBERS);
        } catch (SQLException e) {
            Log.d(LOG_TAG, " Error create database " + e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion != oldVersion){
            Log.d(LOG_TAG, " onUpgrade executed ");
        }

    }

    //String to create a customer table
    private static final String CREATE_TABLE_EXCLUDED_PHONE_NUMBERS =
            "CREATE TABLE " + TABLE_EXCLUDED_PHONE_NUMBERS + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_NAME + " TEXT, "
                    + COLUMN_PHONE + " TEXT NOT NULL) ";

}
