package com.passaparola.thiagodesales.passaparolaview.listeners;

import com.passaparola.thiagodesales.passaparolaview.model.RSSMeditationItem;

import java.util.ArrayList;

public interface MeditationListener {

    public void onNewMeditation(ArrayList<RSSMeditationItem> meditations);

}
