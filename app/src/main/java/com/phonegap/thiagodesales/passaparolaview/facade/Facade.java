package com.passaparola.thiagodesales.passaparolaview.facade;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.passaparola.thiagodesales.passaparolaview.R;
import com.passaparola.thiagodesales.passaparolaview.connection.ConnectionResponseHandler;
import com.passaparola.thiagodesales.passaparolaview.connection.Connections;
import com.passaparola.thiagodesales.passaparolaview.database.DatabaseDataManagement;
import com.passaparola.thiagodesales.passaparolaview.files.FileManager;
import com.passaparola.thiagodesales.passaparolaview.listeners.MeditationListener;
import com.passaparola.thiagodesales.passaparolaview.listeners.ParolaListener;
import com.passaparola.thiagodesales.passaparolaview.model.Parola;
import com.passaparola.thiagodesales.passaparolaview.model.RSSMeditationItem;
import com.passaparola.thiagodesales.passaparolaview.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;


public class Facade implements ConnectionResponseHandler {
    private DatabaseDataManagement db;
    private FileManager fileManager;
    private static Facade instance;
    private ArrayList<ParolaListener> parolaListeners;
    private ArrayList<MeditationListener> meditationListeners;
    private Context context;

    private Facade(Context context) {
        this.context = context;
        db = DatabaseDataManagement.getInstance(context);
        fileManager = FileManager.getInstance(context);
        parolaListeners = new ArrayList<>();
        meditationListeners = new ArrayList<>();
    }

    public synchronized static Facade getInstance(Context context) {
        if (instance == null)
            instance = new Facade(context);

        Log.d("Facade.getInstance", "facade = " + instance);
        return instance;
    }

    public void addParolaListerner(ParolaListener parolaListener) {
        parolaListeners.add(parolaListener);
    }

    public void addMeditationListeners(MeditationListener meditationListener) {
        meditationListeners.add(meditationListener);
    }

    public HashMap<String, Parola> readParolas() {
        return db.readLastParolas();
    }

    public RSSMeditationItem readTodaysMeditation() {
        Log.d("Facade.readTodayMed", "Solicitando as de hoje!");
        return db.readMeditationFromDate(Calendar.getInstance().getTime());
    }

//    public RSSMeditationItem readMeditationFromDate(Date date) {
//        return db.readMeditationFromDate(date);
//    }

    public ArrayList<RSSMeditationItem> getAllMeditations() {
        Log.d("getAllMeditations", "Solicitando TODAS");
        return db.readAllMeditations();
    }

    public void downloadParola(String currentLanguageId) {
        Connections connectionManager = new Connections(this);
        connectionManager.setRequestType(Connections.REQUEST_TYPES.PAROLA);
        HashMap<Object, Object> params = new HashMap<>();
        params.put("language", currentLanguageId);
        connectionManager.setParameters(params);
        connectionManager.execute();
    }

    public void downloadMeditations() {
        Log.d("downloadMeditations", "Fazendo download...");
        Connections connectionManager = new Connections(this);
        connectionManager.setRequestType(Connections.REQUEST_TYPES.MEDITATION);
        connectionManager.execute();
    }

    @Override
    public void fireResponse(Object object) {
        if (object instanceof Parola) {
            Parola parola = (Parola) object;
            db.insertParola(parola);
            notifyParolaListeners(parola);
        } else if (object instanceof ArrayList) {
            ArrayList<RSSMeditationItem> meditationList = (ArrayList<RSSMeditationItem>) object;

            for (RSSMeditationItem meditation : meditationList) {
                db.insertMeditation(meditation);
            }

            notifyMeditationListeners(meditationList);
        }
    }

    private void notifyParolaListeners(Parola parola) {
        for (ParolaListener listener : parolaListeners) {
            listener.onNewParola(parola);
        }
    }

    private void notifyMeditationListeners(ArrayList<RSSMeditationItem> meditations) {
        Log.d("notifyMeditati", "Notificando meditations listeners");
        for (MeditationListener listener : meditationListeners) {
            listener.onNewMeditation(meditations);
        }
    }

    public void buildImageForSharing(RSSMeditationItem meditationItem) {
        Log.d("facade", "buildImageForSharing");
        Uri uri = fileManager.drawPictureForFileSharing(meditationItem);
        meditationItem.setLocalUri(uri);
    }

    public void shareParola(RSSMeditationItem meditationItem) {
        Log.d("facade", "shareParola para URI " + meditationItem.getLocalUri());

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);

        Uri uri = meditationItem.getLocalUri();
        if (uri != null) {
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.setType("image/*");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            String language = meditationItem.getCurrentParolaLanguage();
            String finalText = context.getResources().getText(R.string.app_name) + " - " + Utils.isoDateToStandardFormat(meditationItem.getPublishedDate()) + "\n" + meditationItem.getParola(language) + "\n" + meditationItem.getMeditation(language) + "\nApolônio de Carvalo. ";
            shareIntent.putExtra(Intent.EXTRA_TEXT, finalText);
            shareIntent.setType("text/plain");
        }

        context.startActivity(Intent.createChooser(shareIntent, "Share Passa parola")); //TODO Internationalization...
    }


}
