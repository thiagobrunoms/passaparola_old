package com.passaparola.thiagodesales.passaparolaview.android;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.passaparola.thiagodesales.passaparolaview.R;
import com.passaparola.thiagodesales.passaparolaview.adapters.MeditationListAdapter;
import com.passaparola.thiagodesales.passaparolaview.facade.Facade;
import com.passaparola.thiagodesales.passaparolaview.listeners.MeditationListener;
import com.passaparola.thiagodesales.passaparolaview.listeners.MyOnOptionsClickListener;
import com.passaparola.thiagodesales.passaparolaview.model.RSSMeditationItem;
import com.passaparola.thiagodesales.passaparolaview.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MeditationListFragment extends Fragment implements MyOnOptionsClickListener, MeditationListener, View.OnClickListener {

    private RecyclerView meditationListRecyclerView;
    private ArrayList<RSSMeditationItem> meditationsList;
    private MeditationListAdapter listAdapter;
    private Context context;
    private Facade facade;
    private String languageId;
    private List<String> supportedLanguageList;
    private MeditationListener meditationListener;
    private AlertDialog.Builder alertBuilder;
    private AlertDialog shareDialog;
    private Button shareTextButton;
    private Button shareImageButton;
    private RSSMeditationItem meditationItemSelected;


    public MeditationListFragment(Context context, String languageId, MeditationListener meditationListener) {
        this.context = context;
        this.languageId = languageId;
        this.meditationListener = meditationListener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_scrolling, container, false);
        meditationListRecyclerView = (RecyclerView) view.findViewById(R.id.meditationsRecyclerView);

//        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2);
        meditationListRecyclerView.setLayoutManager(new LinearLayoutManager(context));

        meditationsList = new ArrayList<>();
        listAdapter = new MeditationListAdapter(meditationsList, this, meditationListRecyclerView, languageId, context);
        listAdapter.setCurrentParolaLanguage(languageId);
        meditationListRecyclerView.setAdapter(listAdapter);

        meditationListRecyclerView.addItemDecoration(
                new DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        supportedLanguageList = Arrays.asList(getResources().getStringArray(R.array.supported_meditations));
        facade = Facade.getInstance();
        facade.addMeditationListeners(this);
        requestMeditations();

        alertBuilder = new AlertDialog.Builder(getActivity());
        alertBuilder.setCancelable(true);
        alertBuilder.setView(getLayoutInflater().inflate(R.layout.share_options, null));
        alertBuilder.setTitle("Forma de Compartilhamento");
        shareDialog = alertBuilder.create();

    }

    public void setCurrentParolaLanguage(String languageId) {
        if (supportedLanguageList.contains(languageId)) {
            this.languageId = languageId;
            listAdapter.setCurrentParolaLanguage(languageId);
            meditationListRecyclerView.invalidate();
            meditationListRecyclerView.setAdapter(listAdapter);
            listAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(context, context.getString(R.string.not_available_for_language) + " " + context.getString(context.getResources().getIdentifier(languageId, "string", context.getPackageName())), Toast.LENGTH_LONG).show();
        }

    }

    public void requestMeditations() {
        ArrayList<RSSMeditationItem> localMeditationsList = facade.getAllMeditations();

        String today = Utils.getBrazilsLocalDate();
        if (localMeditationsList.size() > 0 && !Utils.isFirstAfterSecond(today, Utils.isoDateToStandardFormat(localMeditationsList.get(0).getPublishedDate()))) {
            feedUI(localMeditationsList);
        } else {
            facade.downloadMeditations();
        }
    }

    private void feedUI(ArrayList<RSSMeditationItem> meditationList) {
        meditationsList.addAll(meditationList);
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public void onNewMeditation(ArrayList<RSSMeditationItem> meditations) {
        feedUI(meditations);
    }

    @Override
    public void onReadMeditation(RSSMeditationItem meditationItem) {
        ArrayList<RSSMeditationItem> meditationList = new ArrayList<>();
        meditationList.add(meditationItem);
        meditationListener.onNewMeditation(meditationList);
    }

    @Override
    public void onReadExperiences(RSSMeditationItem meditationItem) {
//        Log.d("onRead", "Ler experiencias " + meditationItem.getPublishedDate());
        Toast.makeText(context, getString(R.string.future_features), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onWriteExperiences(RSSMeditationItem meditationItem) {
//        Log.d("onRead", "Escrever experiencias" + meditationItem.getPublishedDate());
        Toast.makeText(context, getString(R.string.future_features), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onShareMeditation(RSSMeditationItem meditationItem) {
        Log.d("onRead", "Compartilhar meditacao " + meditationItem.getPublishedDate());
        this.meditationItemSelected = meditationItem;

        shareDialog.show();
        shareTextButton = (Button) shareDialog.findViewById(R.id.button_share_text);
        shareTextButton.setOnClickListener(this);

        shareImageButton = (Button) shareDialog.findViewById(R.id.button_share_image);
        shareImageButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button_share_text) {
            if (meditationItemSelected.getParola(languageId) != null) {
                meditationItemSelected.setLocalUri(null);
                facade.shareParola(meditationItemSelected);
            } else
                Toast.makeText(context, getString(R.string.parola_unavailable), Toast.LENGTH_LONG).show();

        } else if (view.getId() == R.id.button_share_image) {
            if (meditationItemSelected.getMeditation(languageId) != null) { //if meditation existis for such language
                facade.buildImageForSharing(meditationItemSelected);

                if (meditationItemSelected.getLocalUri() != null)
                    facade.shareParola(meditationItemSelected);
                else {
                    Toast.makeText(context, getString(R.string.long_meditation_text_warnning), Toast.LENGTH_LONG).show();
                }
            } else
                Toast.makeText(context, getString(R.string.meditation_unavailable), Toast.LENGTH_LONG).show();
        }

        shareDialog.dismiss();
    }

}
