package com.passaparola.thiagodesales.passaparolaview.facade;

import android.content.Context;

import com.passaparola.thiagodesales.passaparolaview.connection.ConnectionResponseHandler;
import com.passaparola.thiagodesales.passaparolaview.connection.Connections;
import com.passaparola.thiagodesales.passaparolaview.database.DatabaseDataManagement;
import com.passaparola.thiagodesales.passaparolaview.listeners.NewParolaListener;
import com.passaparola.thiagodesales.passaparolaview.model.Parola;

import java.util.ArrayList;
import java.util.HashMap;

public class Facade implements ConnectionResponseHandler {
    private DatabaseDataManagement db;
    private static Facade instance;
    private ArrayList<NewParolaListener> parolaListeners;

    private Facade(Context context) {
        db = DatabaseDataManagement.getInstance(context);
        parolaListeners = new ArrayList<>();
    }

    public synchronized static Facade getInstance(Context context) {
        if (instance == null)
            instance = new Facade(context);

        return instance;
    }

    public void addParolaListerner(NewParolaListener parolaListener) {
        parolaListeners.add(parolaListener);
    }

    public HashMap<String, Parola> readParolas() {
        HashMap<String, Parola> lastParolas = db.readLastParolas();
        return lastParolas;
    }

    public void downloadParola(String currentLanguageId) {
        Connections connectionManager = new Connections(this);
        connectionManager.setRequestType(Connections.REQUEST_TYPES.PAROLA);
        HashMap<Object, Object> params = new HashMap<>();
        params.put("language", currentLanguageId);
        connectionManager.setParameters(params);
        connectionManager.execute();
    }

    @Override
    public void fireResponse(Object object) {
        if (object instanceof Parola) {
            Parola parola = (Parola) object;
            db.insertParola(parola);

            for (NewParolaListener listener : parolaListeners) {
                listener.onNewParola(parola);
            }
        }
    }
}
