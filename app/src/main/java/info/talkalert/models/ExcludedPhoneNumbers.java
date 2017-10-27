package info.talkalert.models;

import android.provider.BaseColumns;

import java.io.Serializable;
import java.util.Date;

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
