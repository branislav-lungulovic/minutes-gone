package info.talkalert.tasks;


import java.util.ArrayList;

public class LoadedContactsData {

    public ArrayList<String> phoneValueArr = new ArrayList<>();
    public ArrayList<String> nameValueArr = new ArrayList<>();
    public ArrayList<String> nameAndPhoneValueArr = new ArrayList<>();


    @Override
    public String toString() {
        return "LoadedContactsData{" +
                "phoneValueArr=" + phoneValueArr +
                ", nameValueArr=" + nameValueArr +
                ", nameAndPhoneValueArr=" + nameAndPhoneValueArr +
                '}';
    }
}
