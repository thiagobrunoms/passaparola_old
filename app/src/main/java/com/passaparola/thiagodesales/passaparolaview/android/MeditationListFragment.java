package com.passaparola.thiagodesales.passaparolaview.android;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.passaparola.thiagodesales.passaparolaview.R;
import com.passaparola.thiagodesales.passaparolaview.adapters.MeditationListAdapter;
import com.passaparola.thiagodesales.passaparolaview.connection.ConnectionResponseHandler;
import com.passaparola.thiagodesales.passaparolaview.connection.Connections;
import com.passaparola.thiagodesales.passaparolaview.model.RSSMeditationItem;

import java.util.ArrayList;

public class MeditationListFragment extends Fragment implements ConnectionResponseHandler, View.OnClickListener {

    private RecyclerView meditationListRecyclerView;
    private ArrayList<RSSMeditationItem> meditationsList;
    private MeditationListAdapter listAdapter;
    private Context context;

    public MeditationListFragment() {}

    public MeditationListFragment(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_scrolling, container, false);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2);

        meditationListRecyclerView = (RecyclerView) view.findViewById(R.id.meditationsRecyclerView);
        meditationListRecyclerView.setLayoutManager(gridLayoutManager);

        meditationsList = new ArrayList<>();

        listAdapter = new MeditationListAdapter(meditationsList,this);
        meditationListRecyclerView.setAdapter(listAdapter);

        requestMeditations();

        meditationListRecyclerView.addItemDecoration(
                new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));

        return view;
    }

    protected void requestMeditations() {
        Connections connectionManager = new Connections(this);
        connectionManager.setRequestType(Connections.REQUEST_TYPES.MEDITATION);
        connectionManager.execute();
    }

    @Override
    public void fireResponse(Object response) {
        Log.d("fireResponse", "Chegou resposta assíncrona da lista");
        meditationsList.addAll((ArrayList<RSSMeditationItem>) response);
        Log.d("fireResponse", "Meditações doanloadadas: " + meditationsList.size());
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        Log.d("Evento novo", "Clicando numa opcao...");
        //        int id = view.getId();
//
//        switch (id) {
//            case R.id.meditationsRecyclerView:
//                break;
//
//                case R.id
//        }

//        int meditationSelectedPosition = meditationListRecyclerView.getChildAdapterPosition(view);
//        RSSMeditationItem selectedMeditation = meditationsList.get(meditationSelectedPosition);
//
//
//        Intent intent = new Intent(getApplicationContext(), MeditationActivity.class);
//
//        //TODO parola and meditation may by in different languages
//        intent.putExtra(Constants.PAROLA.getConstantName(), selectedMeditation.getParolaPt()); //TODO define constant list
//        intent.putExtra(Constants.MEDITATION.getConstantName(), selectedMeditation.getMeditationPt());
//        intent.putExtra(Constants.PUBLISED_DATE.getConstantName(), selectedMeditation.getPublishedDate());
//        startActivity(intent);
//        Toast.makeText(this, selectedMeditation.getParolaPt(), Toast.LENGTH_LONG).show();
    }
}
