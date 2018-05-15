package com.passaparola.thiagodesales.passaparolaview.android;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.passaparola.thiagodesales.passaparolaview.R;
import com.passaparola.thiagodesales.passaparolaview.facade.Facade;
import com.passaparola.thiagodesales.passaparolaview.listeners.MeditationListener;
import com.passaparola.thiagodesales.passaparolaview.model.RSSMeditationItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class MeditationFragment extends Fragment implements MeditationListener{

    private TextView meditationView;
    private TextView dateTextView;
    private TextView parolaTextView;
    private Facade facade;
    private String languageId;
    private List<String> supportedLanguageList;
    private RSSMeditationItem meditation;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_meditation, container, false);

        dateTextView = view.findViewById(R.id.parola_date_text_view);
        parolaTextView= view.findViewById(R.id.parola_title_text_view);
        meditationView = view.findViewById(R.id.meditation_text_view);

        supportedLanguageList = Arrays.asList(getResources().getStringArray(R.array.supported_meditations));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        dateTextView.setText(new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime()));

        facade = Facade.getInstance(getContext());
        facade.addMeditationListeners(this);

        requestMeditations();
    }

    public void setCurrentParolaLanguage(String languageId) {
        this.languageId = languageId;
    }

    public void setMeditation(RSSMeditationItem meditation) {
        this.meditation = meditation;
        feedUI();
    }


    public void requestMeditations() {
        Log.d("requestMeditations", "Solicitando Meditations");
        RSSMeditationItem meditation = facade.readTodaysMeditation();
        if (meditation != null) {
            Log.d("requestMeditations", "vai feedUI " + meditation);
            setMeditation(meditation);
        }

        //This fragment can't request for meditations, since MeditationListFragment already does.
        //It receives the last one through OnNewMeditation callback, if download is needed.
//        else {
//            facade.downloadMeditations();
//        }
    }

    public void feedUI() {
        dateTextView.setText(meditation.getPublishedDate());//TODO Pode chegar a resposta da requisição sem ter montado a interface. Talvez na hora que chegar, colocar primeiro no BD

        if (supportedLanguageList.contains(languageId)) {
            parolaTextView.setText(meditation.getParola(languageId)); //TODO w.r.t local language
            meditationView.setText(meditation.getMeditation(languageId));//TODO w.r.t local language
        } else {
            //TODO else generate warnning!?
            parolaTextView.setText(meditation.getParola("pt")); //TODO w.r.t local language
            meditationView.setText(meditation.getMeditation("pt"));//TODO w.r.t local language
        }
    }

    @Override
    public void onNewMeditation(ArrayList<RSSMeditationItem> meditations) {
        setMeditation(meditations.get(0));//This fragment shows current (the last one) meditation
    }
}
