package com.passaparola.thiagodesales.passaparolaview.facade;

import android.content.Context;
import android.util.Log;

import com.passaparola.thiagodesales.passaparolaview.connection.ConnectionResponseHandler;
import com.passaparola.thiagodesales.passaparolaview.connection.Connections;
import com.passaparola.thiagodesales.passaparolaview.database.DatabaseDataManagement;
import com.passaparola.thiagodesales.passaparolaview.files.FileManager;
import com.passaparola.thiagodesales.passaparolaview.listeners.MeditationListener;
import com.passaparola.thiagodesales.passaparolaview.listeners.ParolaListener;
import com.passaparola.thiagodesales.passaparolaview.model.Parola;
import com.passaparola.thiagodesales.passaparolaview.model.RSSMeditationItem;

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

    private Facade(Context context) {
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
        fileManager.drawPictureForFileSharing(meditationItem);
    }
}
