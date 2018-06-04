package com.passaparola.thiagodesales.passaparolaview.activities;

import android.app.Activity;
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
import com.passaparola.thiagodesales.passaparolaview.facade.Facade;
import com.passaparola.thiagodesales.passaparolaview.listeners.MeditationListener;
import com.passaparola.thiagodesales.passaparolaview.model.RSSMeditationItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

// ./adb devices
//./adb -s DEVICE shell
//run-as com.passaparola.thiagodesales.passaparolaview
//cd /data/data/com.passaparola.thiagodesales.passaparolaview/databases/
//sqlite3 passaparola.db

//TODO Generate notification when new meditation available

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
        chiara.setImageResource(R.drawable.ch2);

        String localLanguage = Locale.getDefault().getLanguage();
        List<String> supportedParolaLanguages = Arrays.asList(getResources().getStringArray(R.array.language_id_list));
        List<String> supportedMeditationLanguages = Arrays.asList(getResources().getStringArray(R.array.supported_meditations));
        boolean supportsParolaLanguage = supportedParolaLanguages.contains(localLanguage);
        boolean supportsMeditationLanguage = supportedMeditationLanguages.contains(localLanguage);

        MyFragmentPagerAdapter pagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());

        parolaFragment = new ParolaFragment(getApplicationContext());
        pagerAdapter.addFragment(parolaFragment, getString(R.string.parola));
        parolaFragment.setCurrentParolaLanguage(supportsParolaLanguage ? localLanguage : getString(R.string.default_language));

        meditationFragment = new MeditationFragment();
        pagerAdapter.addFragment(meditationFragment, getString(R.string.title_activity_meditation));
        meditationFragment.setCurrentParolaLanguage(supportsMeditationLanguage ? localLanguage : getString(R.string.default_language));

        meditationListFragment = new MeditationListFragment(getApplicationContext(), supportsMeditationLanguage ? localLanguage : getString(R.string.default_language), this);
        pagerAdapter.addFragment(meditationListFragment, getString(R.string.experiences));

        tabLayout = (TabLayout) findViewById(R.id.menuTab);
        final ViewPager pager = (ViewPager) findViewById(R.id.viewPager);
        pager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(pager);

        String supported[] = getResources().getStringArray(R.array.supported_meditations);
        for (int i = 0; i < supported.length; i++) {
            Log.d("supported", supported[i]);
        }

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

    @Override
    public void onClick(View view) {
        idiomaList.show();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        String language = (String) listView.getItemAtPosition(position);
        languageId = hashLangId.get(language);

        if (parolaFragment.isAdded()) {
            parolaFragment.setCurrentParolaLanguage(languageId);
            parolaFragment.requestParola();
        }

        if (meditationListFragment.isAdded())
            meditationListFragment.setCurrentParolaLanguage(languageId);


        if (meditationFragment.isAdded()) {
            meditationFragment.setCurrentParolaLanguage(languageId);
            meditationFragment.requestMeditations();
        }

        idiomaList.hide();
    }

    @Override
    public void onNewMeditation(ArrayList<RSSMeditationItem> meditations) {
        meditationFragment.setMeditation(meditations.get(0));
        tabLayout.getTabAt(1).select();
    }
}
