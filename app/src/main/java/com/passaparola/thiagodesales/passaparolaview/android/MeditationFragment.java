package com.passaparola.thiagodesales.passaparolaview.android;

import android.content.Context;
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
import com.passaparola.thiagodesales.passaparolaview.utils.Utils;

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
    private String currentSupportedLanguageId;
    private Context context;

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        facade = Facade.getInstance(getActivity());
        facade.addMeditationListeners(this);
    }

    @Override
    public void onStart() {
        super.onStart();

        dateTextView.setText(new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime()));

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
        Log.d("MedFrag.requestMeditat", "Solicitando Meditations");
        RSSMeditationItem meditation = facade.readTodaysMeditation();
        if (meditation != null) {
            Log.d("MedFrag.requestMeditat", "vai feedUI " + meditation);
            setMeditation(meditation);
        } else Log.d("Med.Request", "meditation é null!");

        //This fragment can't request for meditations, since MeditationListFragment already does.
        //It receives the last one through OnNewMeditation callback, if download is needed.
//        else {
//            facade.downloadMeditations();
//        }
    }

    public void feedUI() {
        dateTextView.setText(Utils.isoDateToStandardFormat(meditation.getPublishedDate()));

        if (this.isAdded() && supportedLanguageList.contains(languageId)) {

            String parolaToSet = meditation.getParola(languageId);
            String meditationToSet = meditation.getMeditation(languageId);

            parolaTextView.setText(parolaToSet != null ? parolaToSet : getString(R.string.parola_unavailable));
            meditationView.setText( (meditationToSet != null ? meditationToSet : getString(R.string.meditation_unavailable)) + "\nApolônio Carvalho Nascimento");
            currentSupportedLanguageId = languageId;
        } else {
            parolaTextView.setText(meditation.getParola(currentSupportedLanguageId));
            meditationView.setText(meditation.getMeditation(currentSupportedLanguageId));
        }
    }

    @Override
    public void onNewMeditation(ArrayList<RSSMeditationItem> meditations) {
        setMeditation(meditations.get(0));//This fragment shows current (the last one) meditation
    }
}
