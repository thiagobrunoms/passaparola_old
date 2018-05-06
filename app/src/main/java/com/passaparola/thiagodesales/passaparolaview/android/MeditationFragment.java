package com.passaparola.thiagodesales.passaparolaview.android;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.passaparola.thiagodesales.passaparolaview.R;
import com.passaparola.thiagodesales.passaparolaview.model.RSSMeditationItem;

public class MeditationFragment extends Fragment {

    private TextView meditationView;
    private TextView dateTextView;
    private TextView parolaTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_meditation, container, false);

        dateTextView = view.findViewById(R.id.parola_date_text_view);
        parolaTextView= view.findViewById(R.id.parola_title_text_view);
        meditationView = view.findViewById(R.id.meditation_text_view);

        return view;
    }

    public void setMeditation(String date, RSSMeditationItem meditation) {
        dateTextView.setText(meditation.getPublishedDate());//TODO Pode chegar a resposta da requisição sem ter montado a interface. Talvez na hora que chegar, colocar primeiro no BD
        parolaTextView.setText(meditation.getParola("pt")); //TODO w.r.t local language
        meditationView.setText(meditation.getMeditation("pt"));//TODO w.r.t local language
    }
}
