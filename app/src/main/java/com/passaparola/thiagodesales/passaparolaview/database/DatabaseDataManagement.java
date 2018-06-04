package com.passaparola.thiagodesales.passaparolaview.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.passaparola.thiagodesales.passaparolaview.R;
import com.passaparola.thiagodesales.passaparolaview.model.Parola;
import com.passaparola.thiagodesales.passaparolaview.model.RSSMeditationItem;
import com.passaparola.thiagodesales.passaparolaview.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

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
        ContentValues values = new ContentValues();

        HashMap<String, String> parolas = meditation.getParolas();
        HashMap<String, String> meditations = meditation.getMeditations();

        String date = meditation.getPublishedDate();
        long status;
        for (int i = 0; i < supportedLanguages.length; i++) {
            values.put(DatabaseDefinitions.Meditations.DATE, date);
            values.put(DatabaseDefinitions.Meditations.LANGUAGE, supportedLanguages[i]);
            values.put(DatabaseDefinitions.Meditations.PAROLA, parolas.get(supportedLanguages[i]));
            values.put(DatabaseDefinitions.Meditations.MEDITATION, meditations.get(supportedLanguages[i]));

            try {
                status = sqlWriteable.insert(DatabaseDefinitions.Meditations.TABLE_NAME, null, values);
            } catch (SQLiteConstraintException e) {
                //Nothing to do if we try to insert an existed meditation.
            }

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
        Cursor cursor = sqlReadable.query(DatabaseDefinitions.Meditations.TABLE_NAME, null, null, null, null, null, "date(" + DatabaseDefinitions.Meditations.DATE + ")" +
                OrderDirection.DESC.name());

        ArrayList<RSSMeditationItem> meditations = new ArrayList<>();
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
        }

        return meditations;
    }

    public RSSMeditationItem readMeditationFromDate(Date dateFrom) {
//        String date = Utils.toBrazilsLocalDate(dateFrom);

//        Cursor cursor = sqlReadable.query(DatabaseDefinitions.Meditations.TABLE_NAME, null,
//                DatabaseDefinitions.Meditations.DATE + " = ?", new String[]{dateFrom}, null, null,
//                null, null);
//        String query = "SELECT * from " + DatabaseDefinitions.Meditations.TABLE_NAME + " where date like ?";

        String query = "SELECT * from " + DatabaseDefinitions.Meditations.TABLE_NAME + " where date like ?";
        Cursor cursor = sqlReadable.rawQuery(query,
                new String[]{new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime())+"%"} );

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
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseDefinitions.Parolas.DATE, parola.getDate());
        contentValues.put(DatabaseDefinitions.Parolas.LANGUAGE, parola.getLanguage());
        contentValues.put(DatabaseDefinitions.Parolas.PAROLA, parola.getParola());

        try {
            long returnedId = sqlWriteable.insert(DatabaseDefinitions.Parolas.TABLE_NAME, null, contentValues);
        } catch (SQLiteConstraintException e) {
            Log.d("insertParola", "Parola " + parola.getParola()  + " already stored!");
        }


    }

    public HashMap<String, Parola> readLastParolas() {
        String today = Utils.getBrazilsLocalDate();

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

            parolas.put(languageId, parola);
        }


        return parolas;
    }


}
