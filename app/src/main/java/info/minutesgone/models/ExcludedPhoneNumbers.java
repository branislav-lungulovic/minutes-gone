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

package info.minutesgone.models;

import android.provider.BaseColumns;

import java.io.Serializable;

public class ExcludedPhoneNumbers implements Serializable {

    private int id;
    private String name;
    private String phone;

    public ExcludedPhoneNumbers() {
        this.id = -1;
        this.phone="";
        this.name= "";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "ExcludedPhoneNumbers{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }

    public static class ExcludedPhoneNumbersDbDef implements BaseColumns {

        public static final String TABLE_EXCLUDED_PHONE_NUMBERS = "excluded_phone_numbers";

        //Create constants for column names of excluded_phone_numbersTable
        public final static String COLUMN_ID = "_id";
        public final static String COLUMN_NAME = "name";
        public final static String COLUMN_PHONE = "phone";

    }
}
