package info.talkalert.shared;


public class StringUtils {

    public static String padRight(String s, int n) {
        return String.format("%1$-" + n + "s", s);
    }

    public static String padLeft(String s, int n) {
        return String.format("%1$" + n + "s", s);
    }

    public static String padRight(String s, int n, String padValue) {
        return String.format("%1$-" + n + "s", s).replace(" ", padValue);
    }

    public static String padLeft(String s, int n, String padValue) {
        return String.format("%1$" + n + "s", s).replace(" ", padValue);
    }
}
