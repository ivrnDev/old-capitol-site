package com.econnect.barangaymanagementapp.util;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateFormatter {

    public static ZonedDateTime getFormattedZonedDateTime(ZonedDateTime dateTime) {
        return dateTime.withZoneSameInstant(ZoneOffset.UTC);
    }

    public static String extractDateAndFormat(ZonedDateTime inputDate) {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MMMM d, yyyy");
        return inputDate.format(dateFormat);
    }

    public static String extractTimeAndFormat(ZonedDateTime inputDate) {
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("h:mm a");
        return inputDate.format(timeFormat);
    }
}
