package com.passaparola.thiagodesales.passaparolaview.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.passaparola.thiagodesales.passaparolaview.R;
import com.passaparola.thiagodesales.passaparolaview.android.MeditationFragment;
import com.passaparola.thiagodesales.passaparolaview.android.MeditationListFragment;
import com.passaparola.thiagodesales.passaparolaview.android.MyFragmentPagerAdapter;
import com.passaparola.thiagodesales.passaparolaview.android.ParolaFragment;
import com.passaparola.thiagodesales.passaparolaview.connection.ConnectionResponseHandler;
import com.passaparola.thiagodesales.passaparolaview.connection.Connections;
import com.passaparola.thiagodesales.passaparolaview.database.DatabaseDataManagement;
import com.passaparola.thiagodesales.passaparolaview.model.RSSMeditationItem;
import com.passaparola.thiagodesales.passaparolaview.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;

public class NewPassaParola extends AppCompatActivity implements ConnectionResponseHandler, View.OnClickListener, AdapterView.OnItemClickListener {

    private ArrayList<RSSMeditationItem> meditationsList;
    private MeditationListFragment meditationListFragment;
    private MeditationFragment meditationFragment;
    private ImageView chiara;
    private ListView listView;
    private AlertDialog idiomaList;
    private HashMap<String, String> hashLangId;
    private ParolaFragment parolaFragment;
    private DatabaseDataManagement db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_passa_parola);
        Toolbar toolbar = (Toolbar) findViewById(R.id.htab_toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);

        idiomaList = buildAlert();

        chiara = findViewById(R.id.htab_header);
        setTopImage();
    }


    @Override
    protected void onStart() {
        super.onStart();

        MyFragmentPagerAdapter pagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());

        parolaFragment = new ParolaFragment(getApplicationContext());
        pagerAdapter.addFragment(parolaFragment, "Parola"); //TODO Internationalitions
        parolaFragment.setRequestParolaLanguage("pt");

        meditationFragment = new MeditationFragment();
        pagerAdapter.addFragment(meditationFragment, "Meditação"); //TODO Internationalitions

        meditationListFragment = new MeditationListFragment(getApplicationContext());
        pagerAdapter.addFragment(meditationListFragment, "Experiências");//TODO Internationalitions

        requestMeditations();

        TabLayout tabLayout = (TabLayout) findViewById(R.id.menuTab);
        final ViewPager pager = (ViewPager) findViewById(R.id.viewPager);
        pager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(pager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                pager.setCurrentItem(tab.getPosition());
                Log.d("selecionando tab", "onTabSelected: pos: " + tab.getPosition());

                switch (tab.getPosition()) {
                    case 0:
                        // TODO: 31/03/17
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private AlertDialog buildAlert() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setCancelable(true);
        dialog.setTitle(R.string.language);
        View v = getLayoutInflater().inflate(R.layout.language_list_layout, null);
        dialog.setView(v);
        listView = (ListView) v.findViewById(R.id.language_list_id);

        String langs[] = (String[]) getResources().getStringArray(R.array.languages_list);
        String langsId[] = (String[]) getResources().getStringArray(R.array.language_id_list);

        hashLangId = new HashMap<>();
        for (int i = 0; i < langs.length; i++)
            hashLangId.put(langs[i], langsId[i]);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, langs);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(this);
        return dialog.create();
    }

    private void setTopImage() {
        String chiaraId = Utils.sortChiaraImage();
        chiara.setImageResource(getResources().getIdentifier("ch2", "drawable", getPackageName()));
    }

    protected void requestMeditations() {
        meditationsList = new ArrayList<>();
        Connections connectionManager = new Connections(this);
        connectionManager.setRequestType(Connections.REQUEST_TYPES.MEDITATION);
        connectionManager.execute();
    }
//
    @Override
    public void fireResponse(Object response) {
        Log.d("fireResponse", "Chegou resposta assíncrona no NewPassaParola");
        meditationsList.addAll((ArrayList<RSSMeditationItem>) response);
        meditationFragment.setMeditation("", meditationsList.get(0));
        meditationListFragment.setMeditationList(meditationsList);
    }

    @Override
    public void onClick(View view) {
//        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show();

//        popupMenu.show();

        idiomaList.show();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        String language = (String) listView.getItemAtPosition(position);
        String languageId = hashLangId.get(language);
        Log.d("Selecionado", language + " -> " + languageId);

        parolaFragment.setRequestParolaLanguage(languageId);
        parolaFragment.requestParola();
        idiomaList.hide();

    }
}
