package com.passaparola.thiagodesales.passaparolaview.android;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.passaparola.thiagodesales.passaparolaview.R;
import com.passaparola.thiagodesales.passaparolaview.connection.ConnectionResponseHandler;
import com.passaparola.thiagodesales.passaparolaview.connection.Connections;
import com.passaparola.thiagodesales.passaparolaview.database.DatabaseDataManagement;
import com.passaparola.thiagodesales.passaparolaview.model.Parola;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ParolaFragment extends Fragment implements ConnectionResponseHandler {

    private TextView parolaPraseTextView;
    private TextView parolaDateTextView;
    private ImageView flagImageView;
    private View view;
    private DatabaseDataManagement db;
    private String currentLanguageId;
    private Connections connectionManager;
    private Context context;

    public ParolaFragment(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.d("onCreateView da Parola", "Criando view da parola");
        view = inflater.inflate(R.layout.parola_main_phrase, container, false);

        parolaPraseTextView = view.findViewById(R.id.parola_phrase_id);
        parolaDateTextView = view.findViewById(R.id.passa_parola_date_textview_id);

//        parolaPraseTextView.setText("Thiago de Sales Testando Passa Parolad de Hoje. Ok? Blz!! Grande demais já!!");
        connectionManager = new Connections(this);

        return view;

    }

    @Override
    public void onStart() {
        super.onStart();

        requestParola();
    }

    public void setRequestParolaLanguage(String languageId) {
        currentLanguageId = languageId;
    }

    public void requestParola() {
        db = DatabaseDataManagement.getInstance(this.context);
        HashMap<String, Parola> lastParolas = db.readLastParolas();

        Log.d("requestParola", "Verificando parola armazenada");
        if (lastParolas.size() == 0 || lastParolas.get(currentLanguageId) == null) {
            Log.d("requestParola", "existe uma nova!!");
            connectionManager = new Connections(this);
            connectionManager.setRequestType(Connections.REQUEST_TYPES.PAROLA);
            HashMap<Object, Object> params = new HashMap<>();
            params.put("language", currentLanguageId);
            connectionManager.setParameters(params);
            connectionManager.execute();
        } else {
            Log.d("requestParola", "pode pegar do DB!!");
            feedUI(lastParolas.get(currentLanguageId));
        }

        if (view != null) {
            flagImageView = view.findViewById(R.id.flag);
            flagImageView.setImageResource(getResources().getIdentifier(currentLanguageId, "drawable", getContext().getPackageName()));
        } else
            Log.d("ParolaFrag", "Por quê é nulo?");
    }

    private void feedUI(Parola parola) {
        if(parola != null) {
            if (parolaDateTextView == null) {
                Log.d("parolaDateTextView", "EH NULO?!?!");
            } else {
                parolaDateTextView.setText(parola.getDate());
                parolaPraseTextView.setText(parola.getParola());
            }
        } else {
            Log.d("feedUI", "Parola é null!");
        }
    }

    @Override
    public void fireResponse(Object object) {
        Parola parola = (Parola) object;
        feedUI(parola);
        db.insertParola(parola);
    }
}
