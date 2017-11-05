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

package info.minutesgone.data.sqllite;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import info.androidminiloggr.Logger;
import info.minutesgone.tasks.ParseCallLogTask;

import static info.minutesgone.models.ExcludedPhoneNumbers.ExcludedPhoneNumbersDbDef.*;

class DatabaseHelper extends SQLiteOpenHelper {
    private static Logger logger = Logger.getLogger(DatabaseHelper.class.getSimpleName());
    private final static int DATABASE_VERSION= 1;
    private static final String DATABASE_NAME = "talkalert.db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_TABLE_EXCLUDED_PHONE_NUMBERS);
        } catch (SQLException e) {
            logger.d(" Error create database " + e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion != oldVersion){
            logger.d(" onUpgrade executed ");
        }

    }

    //String to create a customer table
    private static final String CREATE_TABLE_EXCLUDED_PHONE_NUMBERS =
            "CREATE TABLE " + TABLE_EXCLUDED_PHONE_NUMBERS + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_NAME + " TEXT, "
                    + COLUMN_PHONE + " TEXT NOT NULL) ";

}
