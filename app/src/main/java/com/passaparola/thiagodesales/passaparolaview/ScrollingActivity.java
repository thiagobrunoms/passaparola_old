package com.passaparola.thiagodesales.passaparolaview;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class ScrollingActivity extends AppCompatActivity implements ConnectionResponseHandler, View.OnClickListener {

    private RecyclerView meditationListRecyclerView;
    private ImageView chiara;
    private MeditationListAdapter listAdapter;
    private ArrayList<RSSMeditationItem> meditationsList;
    private String language;
    private Connections connectionManager;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        connectionManager = new Connections(this);
        chiara = findViewById(R.id.imageView);
        setTopImage();

        meditationListRecyclerView = (RecyclerView) findViewById(R.id.meditationsRecyclerView);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                setTopImage();
            }
        });

//        LocaleList list = LocaleList.getDefault();
        dialog = buildAlert();
        language = Locale.getDefault().getLanguage().toString();
        Log.d("IDIOMA", language);

        setupMeditationList();
        requestParola();
        requestMeditations();
    }

    private AlertDialog buildAlert() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setCancelable(true);
        dialog.setTitle("Passa Parola");
        dialog.setView(getLayoutInflater().inflate(R.layout.layout_meditation_item, null));
        return dialog.create();
    }

    private void setTopImage() {
        String chiaraId = Utils.sortChiaraImage();
        chiara.setImageResource(getResources().getIdentifier(chiaraId, "drawable", getPackageName()));
    }

    private void setupMeditationList() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        meditationListRecyclerView.setLayoutManager(gridLayoutManager);

        meditationsList = new ArrayList<>();
        listAdapter = new MeditationListAdapter(meditationsList,this);
        meditationListRecyclerView.setAdapter(listAdapter);

        meditationListRecyclerView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void requestParola() {
        Connections connectionManager = new Connections(this);
        connectionManager.setRequestType(Connections.REQUEST_TYPES.PAROLA);
        HashMap<Object, Object> params = new HashMap<>();
        params.put("language", "pt");
        connectionManager.setParameters(params);
        connectionManager.execute();
    }

    protected void requestMeditations() {
        Connections connectionManager = new Connections(this);
        connectionManager.setRequestType(Connections.REQUEST_TYPES.MEDITATION);
        connectionManager.execute();
    }

    @Override
    public void fireResponse(Object response) {
        Log.d("fireResponse", "Chegou resposta assíncrona");
        if (response instanceof String) {
            Log.d("PAROLA NA VIEW", (String)response);
            getSupportActionBar().setTitle("My title");
        } else if (response instanceof  ArrayList) {
            meditationsList.addAll((ArrayList<RSSMeditationItem>) response);
            Log.d("fireResponse", "Meditações doanloadadas: " + meditationsList.size());
            listAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View view) {
        int meditationSelectedPosition = meditationListRecyclerView.getChildAdapterPosition(view);
        RSSMeditationItem selectedMeditation = meditationsList.get(meditationSelectedPosition);


        Intent intent = new Intent(getApplicationContext(), MeditationActivity.class);

        //TODO parola and meditation may by in different languages
        intent.putExtra(Constants.PAROLA.getConstantName(), selectedMeditation.getParolaPt()); //TODO define constant list
        intent.putExtra(Constants.MEDITATION.getConstantName(), selectedMeditation.getMeditationPt());
        intent.putExtra(Constants.PUBLISED_DATE.getConstantName(), selectedMeditation.getPublishedDate());
        startActivity(intent);
//        Toast.makeText(this, selectedMeditation.getParolaPt(), Toast.LENGTH_LONG).show();
    }
}