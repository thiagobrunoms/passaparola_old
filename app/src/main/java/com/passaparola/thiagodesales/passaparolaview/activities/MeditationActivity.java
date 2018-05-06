package com.passaparola.thiagodesales.passaparolaview.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.passaparola.thiagodesales.passaparolaview.R;
import com.passaparola.thiagodesales.passaparolaview.utils.Constants;
import com.passaparola.thiagodesales.passaparolaview.utils.Utils;

public class MeditationActivity extends AppCompatActivity {

    private TextView publishedDateTextView;
    private TextView parolaTextView;
    private TextView meditationTextView;
    private ImageView chiara;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meditation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        chiara = findViewById(R.id.imageView);
        String chiaraId = Utils.sortChiaraImage();
        chiara.setImageResource(getResources().getIdentifier(chiaraId, "drawable", getPackageName()));

        publishedDateTextView = (TextView) findViewById(R.id.parola_date_text_view);
        parolaTextView = (TextView) findViewById(R.id.parola_title_text_view);
        meditationTextView = (TextView) findViewById(R.id.meditation_text_view);

        Intent thisIntent = getIntent();
        fillContent(
                thisIntent.getStringExtra(Constants.PUBLISED_DATE.getConstantName()),
                thisIntent.getStringExtra(Constants.PAROLA.getConstantName()),
                thisIntent.getStringExtra(Constants.MEDITATION.getConstantName())
        );
    }

    private void fillContent(String publishedDate, String parola, String meditation) {
        publishedDateTextView.setText(publishedDate);
        parolaTextView.setText(parola);
        meditationTextView.setText(meditation);
    }
}
