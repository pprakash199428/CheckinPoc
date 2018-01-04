package com.spicejet.util;

/**
 * Created by Pgupta on 14-06-2016.
 */
public class SpiceJetUtil {

    public static String rightPadSpacesIfRequired(String inputString, int fieldSize) {
        return String.format("%1$-" + fieldSize + "s", inputString);
    }

    public static String leftPadSpacesIfRequired(String inputString, int fieldSize) {
        return String.format("%1$" + fieldSize + "s", inputString);
    }

    public static String convertToJulian(String unformattedDate) {
    /*Unformatted Date: ddmmyyyy*/
        String resultJulian = "";
        if (unformattedDate.length() > 0) {
     /*Days of month*/
            int[] monthValues = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

            String dayS, monthS, yearS;
            dayS = unformattedDate.substring(0, 2);
            monthS = unformattedDate.substring(2, 4);
            yearS = unformattedDate.substring(4, unformattedDate.length());

     /*Convert to Integer*/
            int day = Integer.valueOf(dayS);
            int month = Integer.valueOf(monthS);
            int year = Integer.valueOf(yearS);

            //Leap year check
            if (year % 4 == 0) {
                monthValues[1] = 29;
            }
            //Start building Julian date
            String julianDate = "";
           /* //last two digit of year: 2012 ==> 12
            julianDate += yearS.substring(2, 4);*/

            int julianDays = 0;
            for (int i = 0; i < month - 1; i++) {
                julianDays += monthValues[i];
            }
            julianDays += day;

            julianDate += String.valueOf(julianDays);
            resultJulian = julianDate;
            if (String.valueOf(julianDays).length() == 1) {
                resultJulian = "00" + resultJulian;
            }
            if (String.valueOf(resultJulian).length() == 2) {
                resultJulian = "0" + resultJulian;
            }

        }
        return resultJulian;
    }
}
