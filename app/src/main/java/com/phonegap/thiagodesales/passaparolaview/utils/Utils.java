package com.passaparola.thiagodesales.passaparolaview.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

public class Utils {

    public static String sortBackgroundForSharing() {
//        Random r = new Random();
//        int nextChiara = r.nextInt(14);
//        return "background_parola_" + nextChiara;
        return "background_parola_default";
    }

    //firstDate and secondDate on dd/MM/yyyy format
    public static boolean isFirstAfterSecond(String firstDate, String secondDate) {
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date dateFirst = dateFormat.parse(firstDate);
            Date dateSecond = dateFormat.parse(secondDate);
            return dateFirst.after(dateSecond);
        } catch (ParseException e) {
            return false;
        }
    }

    public static String getBrazilsLocalDate() {
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        df.setTimeZone(TimeZone.getTimeZone( "GMT-03:00" )); //Brazil :)
        return df.format(Calendar.getInstance().getTime());
    }

    public static String toBrazilsLocalDate(Date dateFrom) {
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        df.setTimeZone(TimeZone.getTimeZone( "GMT-03:00" )); //Brazil :)
        return df.format(dateFrom);
    }

    public static String isoDateToStandardFormat(String isoDate) {
        String dateParts[] = isoDate.split("T")[0].split("-");
        return dateParts[2] + "/" + dateParts[1] + "/" + dateParts[0];
    }

    public static int getCurrentMonth() {
        return Calendar.getInstance().get(Calendar.MONTH);
    }

}
