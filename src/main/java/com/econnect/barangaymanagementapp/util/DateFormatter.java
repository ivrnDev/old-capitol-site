package com.econnect.barangaymanagementapp.util;

import java.time.LocalDate;
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

    public static String toStandardFormat(LocalDate inputDate) {
        try {
            DateTimeFormatter birthdateFormat = DateTimeFormatter.ofPattern("M/d/yyyy");
            return inputDate.format(birthdateFormat);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static LocalDate toLocalDateFormat(String inputDate) {
        try {
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("M/d/yyyy");
            return LocalDate.parse(inputDate, inputFormatter);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String toStringLocaleDateFormat(LocalDate inputDate) {
        try {
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("M/d/yyyy");
            return inputDate.format(inputFormatter);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String calculateAge(LocalDate birthdate) {
        LocalDate currentDate = LocalDate.now();
        int age = currentDate.getYear() - birthdate.getYear();
        if (birthdate.getDayOfYear() > currentDate.getDayOfYear()) {
            age--;
        }
        return String.valueOf(age);
    }
}
