package info.minutesgone.models;


import org.joda.time.LocalDate;

public class Preferences {

    private int day;

    private int minutes;

    private int alertLevel;

    private boolean countLocalCalls;

    private LocalDate lastNotificationSentDate;

    private boolean showNotificationInStatusBar;

    private int lastCalculatedPercent;

    public Preferences(int day, int minutes, int alertLevel, boolean countLocalCalls, LocalDate lastNotificationSentDate, boolean showNotificationInStatusBar, int lastCalculatedPercent) {
        this.day = day;
        this.minutes = minutes;
        this.alertLevel = alertLevel;
        this.countLocalCalls = countLocalCalls;
        this.lastNotificationSentDate = lastNotificationSentDate;
        this.showNotificationInStatusBar = showNotificationInStatusBar;
        this.lastCalculatedPercent = lastCalculatedPercent;
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

    public boolean isCountLocalCalls() {
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

    public boolean isShowNotificationInStatusBar() {
        return showNotificationInStatusBar;
    }

    public void setShowNotificationInStatusBar(boolean showNotificationInStatusBar) {
        this.showNotificationInStatusBar = showNotificationInStatusBar;
    }

    public int getLastCalculatedPercent() {
        return lastCalculatedPercent;
    }

    public void setLastCalculatedPercent(int lastCalculatedPercent) {
        this.lastCalculatedPercent = lastCalculatedPercent;
    }
}