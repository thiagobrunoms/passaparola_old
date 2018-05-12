package com.passaparola.thiagodesales.passaparolaview.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class Utils {

    public static String sortChiaraImage() {
        Random r = new Random();
        int nextChiara = r.nextInt(9);
        String str = "ch" + (nextChiara == 0 ? 1 : nextChiara);
        return str;
    }

    //firstDate and secondDate on dd/MM/yyyy format
    //TODO Testar se datas iguais está retornando verdadeiro.
    public static boolean isFirstAfterSecond(String firstDate, String secondDate) {
        Log.d("isFirstAfterSecond", "F = " + firstDate + " -  S = " + secondDate);
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date dateFirst = dateFormat.parse(firstDate);
            Date dateSecond = dateFormat.parse(secondDate);
            Log.d("isFirstAfterSecond R ", "R = " + dateFirst.before(dateSecond));
            return dateFirst.after(dateSecond);
        } catch (ParseException e) {
            return false;
        }
    }

}
