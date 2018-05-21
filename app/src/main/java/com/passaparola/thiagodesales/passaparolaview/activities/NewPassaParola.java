package com.passaparola.thiagodesales.passaparolaview.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.passaparola.thiagodesales.passaparolaview.R;
import com.passaparola.thiagodesales.passaparolaview.android.MeditationFragment;
import com.passaparola.thiagodesales.passaparolaview.android.MeditationListFragment;
import com.passaparola.thiagodesales.passaparolaview.adapters.MyFragmentPagerAdapter;
import com.passaparola.thiagodesales.passaparolaview.android.ParolaFragment;
import com.passaparola.thiagodesales.passaparolaview.database.DatabaseDataManagement;
import com.passaparola.thiagodesales.passaparolaview.listeners.MeditationListener;
import com.passaparola.thiagodesales.passaparolaview.model.RSSMeditationItem;
import com.passaparola.thiagodesales.passaparolaview.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


//TODO Ao selecionar um idioma que não tem tradução da meditação, a lista está ficando incompleta na aba meditação. Só acontece em algumas situações...:/ Ir pra aba parola, selecionar um idioma, e voltar pra aba experiencias
//TODO Adicionar o nome do autor do texto.

public class NewPassaParola extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, MeditationListener {

    private ArrayList<RSSMeditationItem> meditationsList;
    private MeditationListFragment meditationListFragment;
    private MeditationFragment meditationFragment;
    private ImageView chiara;
    private ListView listView;
    private AlertDialog idiomaList;
    private HashMap<String, String> hashLangId;
    private ParolaFragment parolaFragment;
    private String languageId;
    private TabLayout tabLayout;

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

        MyFragmentPagerAdapter pagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());

        parolaFragment = new ParolaFragment(getApplicationContext());
        pagerAdapter.addFragment(parolaFragment, "Parola"); //TODO Internationalitions
        parolaFragment.setCurrentParolaLanguage("pt");

        meditationFragment = new MeditationFragment();
        pagerAdapter.addFragment(meditationFragment, "Meditação"); //TODO Internationalitions
        meditationFragment.setCurrentParolaLanguage("pt");

        meditationListFragment = new MeditationListFragment(getApplicationContext(), "pt", this);
        pagerAdapter.addFragment(meditationListFragment, "Experiências");//TODO Internationalitions

        tabLayout = (TabLayout) findViewById(R.id.menuTab);
        final ViewPager pager = (ViewPager) findViewById(R.id.viewPager);
        pager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(pager);

//        Intent intent = new Intent(getApplicationContext(), TesteAct.class);
//        startActivity(intent);

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

    @Override
    public void onClick(View view) {
        idiomaList.show();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        String language = (String) listView.getItemAtPosition(position);
        languageId = hashLangId.get(language);
        Log.d("Selecionado", language + " -> " + languageId);

        parolaFragment.setCurrentParolaLanguage(languageId);
        parolaFragment.requestParola();

        if (meditationListFragment.isAdded())
            meditationListFragment.setCurrentParolaLanguage(languageId);
        else Log.d("onItemClick", "meditationListFragment not added!");

        if (meditationFragment.isAdded()) {
            meditationFragment.setCurrentParolaLanguage(languageId);
            meditationFragment.requestMeditations();
        } else Log.d("onItemClick", "meditationFragment not added!");

        idiomaList.hide();
    }

    @Override
    public void onNewMeditation(ArrayList<RSSMeditationItem> meditations) {
        meditationFragment.setMeditation(meditations.get(0));
        tabLayout.getTabAt(1).select();
    }
}
