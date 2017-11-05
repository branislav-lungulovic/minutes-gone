/*
 * minutes-gone
 * Copyright (C) 2017.  Author: Branislav Lungulovic
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package info.minutesgone.tasks;


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
