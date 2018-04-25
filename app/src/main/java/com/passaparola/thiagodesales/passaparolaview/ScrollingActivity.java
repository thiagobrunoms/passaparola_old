package com.passaparola.thiagodesales.passaparolaview;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import java.util.Random;

public class ScrollingActivity extends AppCompatActivity implements ConnectionResponseHandler {

    private RecyclerView meditationListRecyclerView;
    private ImageView chiara;
    private Random r;
    private MeditationListAdapter listAdapter;
    private ArrayList<RSSMeditationItem> meditationsList;
    private String language;
    private Connections connectionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        connectionManager = new Connections(this);
        r = new Random();
        chiara = findViewById(R.id.imageView);
        sortChiaraImage();

        meditationListRecyclerView = (RecyclerView) findViewById(R.id.meditationsRecyclerView);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                sortChiaraImage();

            }
        });

        language = Locale.getDefault().getLanguage().toString();
        Log.d("IDIOMA", language);

        setupMeditationList();
        requestParola();
        requestMeditations();
    }

    private void sortChiaraImage() {
        int nextChiara = r.nextInt(9);
        String str = "ch" + (nextChiara == 0 ? 1 : nextChiara);
//        Log.d("imagem sorteada", str);
        chiara.setImageResource(getResources().getIdentifier(str, "drawable", getPackageName()));
    }

    private void setupMeditationList() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        meditationListRecyclerView.setLayoutManager(gridLayoutManager);

        meditationsList = new ArrayList<>();
        listAdapter = new MeditationListAdapter(meditationsList);
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
}
