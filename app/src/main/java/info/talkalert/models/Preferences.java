package info.talkalert.models;


import org.joda.time.LocalDate;

public class Preferences {

    private int day;

    private int minutes;

    private int alertLevel;

    private boolean countLocalCalls;

    private LocalDate lastNotificationSentDate;

    public Preferences(int day, int minutes, int alertLevel, boolean countLocalCalls, LocalDate lastNotificationSentDate) {
        this.day = day;
        this.minutes = minutes;
        this.alertLevel = alertLevel;
        this.countLocalCalls = countLocalCalls;
        this.lastNotificationSentDate=lastNotificationSentDate;

    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public int getAlertLevel() {
        return alertLevel;
    }

    public void setAlertLevel(int alertLevel) {
        this.alertLevel = alertLevel;
    }

    public boolean countLocalCalls() {
        return countLocalCalls;
    }

    public void setCountLocalCalls(boolean countLocalCalls) {
        this.countLocalCalls = countLocalCalls;
    }

    public LocalDate getLastNotificationSentDate() {
        return lastNotificationSentDate;
    }

    public void setLastNotificationSentDate(LocalDate lastNotificationSentDate) {
        this.lastNotificationSentDate = lastNotificationSentDate;
    }
}
