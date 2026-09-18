package com.campusflow.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Represents a scheduled date and time window for a resource.
 */
public class TimeSlot implements Serializable, Comparable<TimeSlot> {
    private static final long serialVersionUID = 1L;

    public static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    private final LocalDate date;
    private final LocalTime startTime;
    private final LocalTime endTime;

    public TimeSlot(LocalDate date, LocalTime startTime, LocalTime endTime) {
        if (date == null || startTime == null || endTime == null) {
            throw new IllegalArgumentException("Date, start time, and end time cannot be null.");
        }
        if (!endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("End time (" + endTime + ") must be after start time (" + startTime + ").");
        }
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public static TimeSlot of(String dateStr, String startStr, String endStr) {
        LocalDate d = LocalDate.parse(dateStr, DATE_FMT);
        LocalTime s = LocalTime.parse(startStr, TIME_FMT);
        LocalTime e = LocalTime.parse(endStr, TIME_FMT);
        return new TimeSlot(d, s, e);
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    /**
     * Checks if this timeslot conflicts or overlaps with another timeslot.
     * Two timeslots conflict if they are on the same date and their time ranges intersect.
     */
    public boolean overlapsWith(TimeSlot other) {
        if (other == null || !this.date.equals(other.date)) {
            return false;
        }
        // Conflict occurs if (start < other.end) && (end > other.start)
        return this.startTime.isBefore(other.endTime) && this.endTime.isAfter(other.startTime);
    }

    public long getDurationMinutes() {
        return java.time.Duration.between(startTime, endTime).toMinutes();
    }

    @Override
    public int compareTo(TimeSlot other) {
        int dateCmp = this.date.compareTo(other.date);
        if (dateCmp != 0) return dateCmp;
        return this.startTime.compareTo(other.startTime);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeSlot timeSlot = (TimeSlot) o;
        return Objects.equals(date, timeSlot.date) &&
               Objects.equals(startTime, timeSlot.startTime) &&
               Objects.equals(endTime, timeSlot.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, startTime, endTime);
    }

    @Override
    public String toString() {
        return String.format("%s [%s - %s]", date.format(DATE_FMT), startTime.format(TIME_FMT), endTime.format(TIME_FMT));
    }
}
