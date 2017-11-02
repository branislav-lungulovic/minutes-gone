package info.minutesgone;

import android.app.Application;

import info.androidminiloggr.Logger;


public class MinutesgoneApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //Logger.init(BuildConfig.DEBUG);
    }
}
