package com.econnect.barangaymanagementapp.util;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

public class DateFormatter {

    // Converts a ZonedDateTime to UTC.
    public static ZonedDateTime convertToUtc(ZonedDateTime dateTime) {
        return dateTime.withZoneSameInstant(ZoneOffset.UTC);
    }

    // Formats a ZonedDateTime to a readable date format like "January 1, 2024".
    public static String formatDateToLongStyle(ZonedDateTime inputDate) {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MMMM d, yyyy");
        return inputDate.format(dateFormat);
    }

    // Formats a ZonedDateTime to a readable time format like "5:00 PM".
    public static String formatTimeTo12HourStyle(ZonedDateTime inputDate) {
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("h:mm a");
        return inputDate.format(timeFormat);
    }

    // Formats a LocalDate to "MM/dd/yyyy" (standard U.S. format).
    public static String formatToUsShortDate(LocalDate inputDate) {
        try {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("M/d/yyyy");
            return inputDate.format(dateFormatter);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    // Converts date from "MM/dd/yyyy" to a long-form date like "January 1, 2024".
    public static String formatToLongDate(String inputDate) {
        try {
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("M/d/yyyy");
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
            LocalDate date = LocalDate.parse(inputDate, inputFormatter);
            return date.format(outputFormatter);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String formatShortToLongDate(String inputDate) {
        try {
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("M/d/yyyy");
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
            LocalDate date = LocalDate.parse(inputDate, inputFormatter);
            return date.format(outputFormatter);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    // Converts a LocalDate to "MM/dd/yyyy" string format.
    public static String formatLocalDateToUsShortDate(LocalDate inputDate) {
        try {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("M/d/yyyy");
            return inputDate.format(dateFormatter);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Calculates age based on a given birthdate.
    public static String calculateAgeFromBirthdate(String birthdate) {
        try {
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("M/d/yyyy");
            LocalDate birthDateLocal = LocalDate.parse(birthdate, inputFormatter);
            LocalDate currentDate = LocalDate.now();

            int age = currentDate.getYear() - birthDateLocal.getYear();
            if (birthDateLocal.getDayOfYear() > currentDate.getDayOfYear()) {
                age--;
            }
            return String.valueOf(age);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String calculateAgeFromBirthdate(LocalDate birthdate) {
        LocalDate currentDate = LocalDate.now();
        int age = currentDate.getYear() - birthdate.getYear();
        if (birthdate.getDayOfYear() > currentDate.getDayOfYear()) {
            age--;
        }
        return String.valueOf(age);
    }

    public static String toTimeStamp(ZonedDateTime zonedDateTime) {
        return zonedDateTime.toInstant().toEpochMilli() + "";
    }

    public static LocalDate convertToDatePickerLocalDate(String inputDate) {
        // Create a lenient formatter for single-digit months/days
        DateTimeFormatter inputFormatter = new DateTimeFormatterBuilder()
                .appendValue(ChronoField.MONTH_OF_YEAR) // Handle single or double-digit months
                .appendLiteral('/')
                .appendValue(ChronoField.DAY_OF_MONTH) // Handle single or double-digit days
                .appendLiteral('/')
                .appendValue(ChronoField.YEAR, 4) // Year must be 4 digits
                .toFormatter();

        // Parse the input string to a LocalDate
        return LocalDate.parse(inputDate, inputFormatter);
    }

    public static String formatToDateTime(ZonedDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(formatter);
    }


}
