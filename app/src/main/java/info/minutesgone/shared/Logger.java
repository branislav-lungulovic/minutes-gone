package info.minutesgone.shared;


import android.util.Log;

import static info.minutesgone.BuildConfig.DEBUG;

public class Logger {

    private String tag;

    public Logger() {
    }

    public Logger(String tag) {
        this.tag = tag;
    }

    public void d(String message) {
        if (DEBUG) Log.d(tag, message);
    }

    public void d(Object... messageArgs) {
        StringBuilder message = new StringBuilder();
        if (DEBUG) {
            for (int i = 0; i < messageArgs.length; i++) {
                String messagePart = castToString(messageArgs[i]);
                message.append(messagePart);
            }
            Log.d(tag, message.toString());
        }
    }

    public void e(String message) {
        Log.e(tag, message);
    }

    public void e(Object... messageArgs) {
        StringBuilder message = new StringBuilder();
        for (int i = 0; i < messageArgs.length; i++) {
            String messagePart = castToString(messageArgs[i]);
            message.append(messagePart);
        }
        Log.e(tag, message.toString());
    }

    private String castToString(final Object obj) {
        return (obj != null) ? obj.toString() : "null";
    }

    private String castToString(final int intVal) {
        return Integer.toString(intVal);
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
}
