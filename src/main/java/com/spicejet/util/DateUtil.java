package com.spicejet.util;

import com.spicejet.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Utility for Date that will be used in application.
 */
@Component
public class DateUtil {

    private static Environment env;

    @Autowired
    public DateUtil(Environment env){
        DateUtil.env = env;
    }

    /**
     * Method formats Calendar to Boarding Pass Displayable Date Format. For Example : 12MAR16(12/March/2016)
     * @param calendar - Calendar Instance from where we need to fetch Date.
     * @return - Formatted Date in String format.
     */
    public static String formatDateForBoardingPass(Calendar calendar){
        DateFormatSymbols dateFormatSymbols = new DateFormatSymbols();
        dateFormatSymbols.setShortMonths(Constants.CUSTOM_MONTHS);
        SimpleDateFormat bpDateFormater = new SimpleDateFormat("ddMMMyy");
        return bpDateFormater.format(calendar.getTime());
    }

    /**
     * Method formats Calendar to Boarding Pass Displayable Time Format. For Example : 2312(23:12)
     * @param calendar - Calendar Instance from where we need to fetch Date.
     * @return - Formatted Time in String format.
     */
    public static String formatTimeForBoardingPass(Calendar calendar){
        SimpleDateFormat bpDateFormater = new SimpleDateFormat("HHmm");
        return bpDateFormater.format(calendar.getTime());
    }
}
