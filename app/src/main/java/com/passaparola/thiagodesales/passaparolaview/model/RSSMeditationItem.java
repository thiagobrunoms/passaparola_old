package com.passaparola.thiagodesales.passaparolaview.model;

import java.util.HashMap;

public class RSSMeditationItem {

    private String publishedDate;
    private HashMap<String, String> parolas;
    private HashMap<String, String> meditations;
    private String currentLanguageId;

    public RSSMeditationItem(String publishedDate, HashMap<String, String> parolas, HashMap<String, String> meditations) {
        this.publishedDate = publishedDate;
        this.parolas = parolas;
        this.meditations = meditations;
    }

    public RSSMeditationItem(String publishedDate) {
        this.publishedDate = publishedDate;
        this.parolas = new HashMap<>();
        this.meditations = new HashMap<>();
    }

    public RSSMeditationItem() {
        this.parolas = new HashMap<>();
        this.meditations = new HashMap<>();
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public HashMap<String, String> getMeditations() {
        return meditations;
    }

    public HashMap<String, String> getParolas() {
        return parolas;
    }

    public void setParola(String language, String parola) {
        this.parolas.put(language, parola);
    }

    public void setMeditation(String language, String meditation) {
        this.meditations.put(language, meditation);
    }

    public String getParola(String language) {
        return this.parolas.get(language);
    }

    public String getMeditation(String language) {
        return this.meditations.get(language);
    }

    public void setCurrentParolaLanguage(String languageId) {
        this.currentLanguageId = languageId;
    }

    public String getCurrentParolaLanguage() {
        return currentLanguageId;
    }

    @Override
    public String toString() {
        String thisItem = publishedDate + " - \n";

        String eachParola = "= Parolas =\n";
        for (String language : parolas.keySet()) {
            eachParola = eachParola + language + " -> " + parolas.get(language) + "\n";
        }

        String eachMeditation = "Meditations=\n";
        for(String language : meditations.keySet()) {
            eachMeditation = eachMeditation + language + " -> " + meditations.get(language) + "\n";
        }

        return thisItem + eachParola + eachMeditation;
    }
}
