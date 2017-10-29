package info.talkalert.tasks;


import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

public class CallLogData {


    private Integer planaLimit;
    private Integer planaLimitPercent;
    private Integer includedDuration;
    private Integer excludedDuration;
    private LocalDate dateFrom;
    private LocalDateTime dateTo;

    private boolean permissionError=false;

    public CallLogData() {
    }

    public CallLogData(Integer planaLimit, Integer planaLimitPercent, Integer includedDuration, Integer excludedDuration, LocalDate dateFrom, LocalDateTime dateTo) {
        this.planaLimit = planaLimit;
        this.planaLimitPercent = planaLimitPercent;
        this.includedDuration = includedDuration;
        this.excludedDuration = excludedDuration;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
    }

    public CallLogData(boolean permissionError) {
        this.permissionError = permissionError;
    }

    public Integer getPlanaLimit() {
        return planaLimit;
    }

    public void setPlanaLimit(Integer planaLimit) {
        this.planaLimit = planaLimit;
    }

    public Integer getPlanaLimitPercent() {
        return planaLimitPercent;
    }

    public void setPlanaLimitPercent(Integer planaLimitPercent) {
        this.planaLimitPercent = planaLimitPercent;
    }

    public Integer getIncludedDuration() {
        return includedDuration;
    }

    public void setIncludedDuration(Integer includedDuration) {
        this.includedDuration = includedDuration;
    }

    public Integer getExcludedDuration() {
        return excludedDuration;
    }

    public void setExcludedDuration(Integer excludedDuration) {
        this.excludedDuration = excludedDuration;
    }

    public LocalDate getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(LocalDate dateFrom) {
        this.dateFrom = dateFrom;
    }

    public LocalDateTime getDateTo() {
        return dateTo;
    }

    public void setDateTo(LocalDateTime dateTo) {
        this.dateTo = dateTo;
    }

    public boolean isPermissionError() {
        return permissionError;
    }

    public void setPermissionError(boolean permissionError) {
        this.permissionError = permissionError;
    }

    @Override
    public String toString() {
        return "CallLogData{" +
                "planaLimit=" + planaLimit +
                ", planaLimitPercent=" + planaLimitPercent +
                ", includedDuration=" + includedDuration +
                ", excludedDuration=" + excludedDuration +
                ", dateFrom=" + dateFrom +
                ", dateTo=" + dateTo +
                '}';
    }
}
