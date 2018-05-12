package com.passaparola.thiagodesales.passaparolaview.listeners;

import com.passaparola.thiagodesales.passaparolaview.model.RSSMeditationItem;

public interface MyOnOptionsClickListener {

    public void onReadMeditation(RSSMeditationItem meditationItem);

    public void onReadExperiences(RSSMeditationItem meditationItem);

    public void onWriteExperiences(RSSMeditationItem meditationItem);

    public void onShareMeditation(RSSMeditationItem meditationItem);


}
