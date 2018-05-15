package com.passaparola.thiagodesales.passaparolaview.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.passaparola.thiagodesales.passaparolaview.R;
import com.passaparola.thiagodesales.passaparolaview.exceptions.DataInsertionException;
import com.passaparola.thiagodesales.passaparolaview.model.Parola;
import com.passaparola.thiagodesales.passaparolaview.model.RSSMeditationItem;
import com.passaparola.thiagodesales.passaparolaview.utils.Utils;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

public class DatabaseDataManagement {

    private static DatabaseDataManagement instance;
    private DatabaseManagement databaseManagement;
    private SQLiteDatabase sqlWriteable;
    private SQLiteDatabase sqlReadable;
    private Context context;
    private String supportedLanguages[];
    private enum OrderDirection {ASC, DESC}


    private DatabaseDataManagement(Context context) {
        this.context = context;
        databaseManagement = new DatabaseManagement(this.context);
        sqlWriteable = databaseManagement.getWritableDatabase();
        sqlReadable = databaseManagement.getReadableDatabase();
        supportedLanguages = context.getResources().getStringArray(R.array.supported_meditations);
    }

    public static DatabaseDataManagement getInstance(Context context) {
        if (instance == null)
            instance = new DatabaseDataManagement(context);

        return instance;
    }

    //TODO Estou apenas adicionando a lista de meditações. Entretanto, não estou removendo as antigas quando atualizo a lista inteira.
    public boolean insertMeditation(RSSMeditationItem meditation) {
        Log.d("insertMeditation", meditation.toString());
        ContentValues values = new ContentValues();

        HashMap<String, String> parolas = meditation.getParolas();
        HashMap<String, String> meditations = meditation.getMeditations();

        String date = meditation.getPublishedDate();
        long status;
        for (int i = 0; i < supportedLanguages.length; i++) {
            values.put(DatabaseDefinitions.Meditations.DATE, date); //TODO Is it necessary?
            values.put(DatabaseDefinitions.Meditations.LANGUAGE, supportedLanguages[i]);
            values.put(DatabaseDefinitions.Meditations.PAROLA, parolas.get(supportedLanguages[i]));
            values.put(DatabaseDefinitions.Meditations.MEDITATION, meditations.get(supportedLanguages[i]));

            //TODO throw exception in case of status = -1?
            status = sqlWriteable.insert(DatabaseDefinitions.Meditations.TABLE_NAME, null, values);

            values.clear();
        }

        return true; //false if exceotion occurs
    }

    private void feedData(RSSMeditationItem meditation, Cursor cursor) {
        meditation.setParola(cursor.getString(cursor.getColumnIndex(DatabaseDefinitions.Meditations.LANGUAGE)),
                cursor.getString(cursor.getColumnIndex(DatabaseDefinitions.Meditations.PAROLA)));
        meditation.setMeditation(cursor.getString(cursor.getColumnIndex(DatabaseDefinitions.Meditations.LANGUAGE)),
                cursor.getString(cursor.getColumnIndex(DatabaseDefinitions.Meditations.MEDITATION)));
    }

    public ArrayList<RSSMeditationItem> readAllMeditations() {
        Log.d("readAllMeditations", "Lendo todas as meditações");
        Cursor cursor = sqlReadable.query(DatabaseDefinitions.Meditations.TABLE_NAME, null, null, null, null, null, "date(" + DatabaseDefinitions.Meditations.DATE + ")" +
                OrderDirection.ASC.name());

        ArrayList<RSSMeditationItem> meditations = new ArrayList<>();
        Log.d("readAllMeditations", "getCount = " + cursor.getCount());
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            RSSMeditationItem meditation = new RSSMeditationItem();
            String previousDate = cursor.getString(cursor.getColumnIndex(DatabaseDefinitions.Meditations.DATE));
            meditation.setPublishedDate(previousDate);
            feedData(meditation, cursor);

            meditations.add(meditation);
            do {
                String currentDate = cursor.getString(cursor.getColumnIndex(DatabaseDefinitions.Meditations.DATE));
                if (currentDate.equals(previousDate)) {
                    feedData(meditation, cursor);
                } else {
                    previousDate = currentDate;
                    meditation = new RSSMeditationItem();
                    meditation.setPublishedDate(previousDate);
                    feedData(meditation, cursor);
                    meditations.add(meditation);
                }
            } while(cursor.moveToNext());
        } else Log.d("reaadAllMeditations", "Não ha meditações salvas!");

        return meditations;
    }

    public RSSMeditationItem readMeditationFromDate(Date dateFrom) {
        String date = Utils.toBrazilsLocalDate(dateFrom);

        Log.d("Date a ser buscada", date);
        Cursor cursor = sqlReadable.query(DatabaseDefinitions.Meditations.TABLE_NAME, null,
                DatabaseDefinitions.Meditations.DATE + " = ?", new String[]{date}, null, null,
                null, null);

        RSSMeditationItem meditationItem = null;
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            meditationItem = new RSSMeditationItem();
            meditationItem.setPublishedDate(cursor.getString(cursor.getColumnIndex(DatabaseDefinitions.Meditations.DATE)));

            do {
                String languageId = cursor.getString(cursor.getColumnIndex(DatabaseDefinitions.Meditations.LANGUAGE));
                meditationItem.setParola(languageId, cursor.getString(cursor.getColumnIndex(DatabaseDefinitions.Meditations.PAROLA)));
                meditationItem.setMeditation(languageId, cursor.getString(cursor.getColumnIndex(DatabaseDefinitions.Meditations.MEDITATION)));
            } while(cursor.moveToNext());
        }

        return meditationItem;
    }

    public void insertParola(Parola parola) {
        Log.d("insertParola", parola.toString());

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseDefinitions.Parolas.DATE, parola.getDate());
        contentValues.put(DatabaseDefinitions.Parolas.LANGUAGE, parola.getLanguage());
        contentValues.put(DatabaseDefinitions.Parolas.PAROLA, parola.getParola());

        long returnedId = sqlWriteable.insert(DatabaseDefinitions.Parolas.TABLE_NAME, null, contentValues);
        Log.d("insertParola", "Id da parola " + returnedId);
    }

    public HashMap<String, Parola> readLastParolas() {
        String today = Utils.getBrazilsLocalDate();

        Log.d("readLastParolas",  "buscando parolas da data " + today);

        Cursor cursor = sqlReadable.query(DatabaseDefinitions.Parolas.TABLE_NAME, null,
                DatabaseDefinitions.Parolas.DATE + " = ?", new String[]{today}, null, null, null);

        HashMap<String, Parola> parolas = new HashMap<>();
        Parola parola;
        while(cursor.moveToNext()) {
            parola = new Parola();
            parola.setDate(cursor.getString(cursor.getColumnIndex(DatabaseDefinitions.Parolas.DATE)));

            String languageId = cursor.getString(cursor.getColumnIndex(DatabaseDefinitions.Parolas.LANGUAGE));
            parola.setLanguage(languageId);
            parola.setParola(cursor.getString(cursor.getColumnIndex(DatabaseDefinitions.Parolas.PAROLA)));

            Log.d("readLastParolas", "Ultima parola armazenada " + parola.toString());

            parolas.put(languageId, parola);
        }


        return parolas;
    }
//
//    public ArrayList<Red> readRedFilterByDateInterval(String dateInit, String dateEnd, OrderDirection orderDirection) {
//        Log.d("filtering oranges", "i: " + dateInit + ", e: " + dateEnd);
//        Cursor cursor = dbReadable.query(DatabaseDefinitions.Red.TABLE_NAME, null, DatabaseDefinitions.Red.DATE +
//                " BETWEEN ? AND ?", new String[]{dateInit, dateEnd}, null, null, getOrderByDate(orderDirection), null);
//        return readRedFromCursor(cursor);
//    }

//    private boolean exists(String dateUS, Aspects aspect) {
//        Cursor cursor = dbReadable.query(DatabaseDefinitions.getTableName(aspect), new String[]{DatabaseDefinitions.DefaultColumns.DATE},
//                DatabaseDefinitions.DefaultColumns.DATE + " = ?", new String[]{dateUS}, null, null, null);
//        int count = cursor.getCount();
//        cursor.close();
//        return (count > 0);
//    }

}
