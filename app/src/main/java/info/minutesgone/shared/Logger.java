package info.minutesgone.shared;


import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.Log;

public class Logger {

    private String tag;

    public static boolean isDebuggable = false;


    public static void init(Context appContext){
        isDebuggable =  ( 0 != ( appContext.getApplicationContext().getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE ) );
    }

    public static Logger getLogger(String tag){

        return new Logger(tag);
    }

    private Logger() {
    }

    private Logger(String tag) {
        this.tag = tag;
    }

    public void v(String msg) {
        Log.v(tag, msg);
    }

    public void v(String tag, String msg, Throwable tr) {
        Log.v(tag, msg, tr);
    }

    public void v(Object... messageArgs) {

            String message = createMessage(messageArgs);
            Log.v(tag, message);
    }

    public void d(String message) {
        if (isDebuggable) Log.d(tag, message);
    }

    public void d(Object... messageArgs) {

        if (isDebuggable) {
            String message = createMessage(messageArgs);
            Log.d(tag, message);
        }
    }

    public void i(String msg) {
        Log.i(tag, msg);
    }

    public void i(String tag, String msg, Throwable tr) {
        Log.i(tag, msg, tr);
    }

    public void i(Object... messageArgs) {

        String message = createMessage(messageArgs);
        Log.i(tag, message);
    }

    public void w(String msg) {
        Log.w(tag, msg);
    }

    public void w(String tag, String msg, Throwable tr) {
        Log.w(tag, msg, tr);
    }

    public void w(String tag, Throwable tr) {
        Log.w(tag, tr);
    }

    public void w(Object... messageArgs) {

        String message = createMessage(messageArgs);
        Log.w(tag, message);
    }

    public void e(String message) {
        Log.e(tag, message);
    }

    public void e(String message, Throwable tr) {
        Log.e(tag, message,tr);
    }

    public void e(Object... messageArgs) {
        String message = createMessage(messageArgs);
        Log.e(tag, message);
    }

    public void wtf(String msg) {
        Log.wtf(tag, msg);
    }

    public void wtf(String tag, String msg, Throwable tr) {
        Log.wtf(tag, msg, tr);
    }

    public void wtf(String tag, Throwable tr) {
        Log.wtf(tag, tr);
    }

    public void wtf(Object... messageArgs) {

        String message = createMessage(messageArgs);
        Log.wtf(tag, message);
    }

    private String createMessage(Object[] messageArgs) {
        StringBuilder message = new StringBuilder();
        for (int i = 0; i < messageArgs.length; i++) {
            String messagePart = castToString(messageArgs[i]);
            message.append(messagePart);
        }
        return message.toString();
    }

    private String castToString(final Object obj) {
        return (obj != null) ? obj.toString() : "null";
    }

    private String castToString(final int intVal) {
        return Integer.toString(intVal);
    }

    private String castToString(final short val) {
        return Integer.toString(val);
    }

    private String castToString(final float val) {
        return Float.toString(val);
    }

    private String castToString(final double val) {
        return Double.toString(val);
    }

    private String castToString(final boolean val) {
        return Boolean.toString(val);
    }

    private String castToString(final char val) {
        return String.valueOf(val);
    }

    private String castToString(final char[] val) {
        return String.valueOf(val);
    }

}
