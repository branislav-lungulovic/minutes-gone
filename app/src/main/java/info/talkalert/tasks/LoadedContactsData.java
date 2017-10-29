package info.talkalert.tasks;


import java.util.ArrayList;

public class LoadedContactsData {

    private final ArrayList<String> phoneValueArr = new ArrayList<>();
    private final ArrayList<String> nameValueArr = new ArrayList<>();
    public final ArrayList<String> nameAndPhoneValueArr = new ArrayList<>();


    @Override
    public String toString() {
        return "LoadedContactsData{" +
                "phoneValueArr=" + phoneValueArr +
                ", nameValueArr=" + nameValueArr +
                ", nameAndPhoneValueArr=" + nameAndPhoneValueArr +
                '}';
    }
}
