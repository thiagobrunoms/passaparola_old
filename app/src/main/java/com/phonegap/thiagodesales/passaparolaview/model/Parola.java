package com.passaparola.thiagodesales.passaparolaview.model;

public class Parola {

    private String date;
    private String parola;
    private String language;

    public Parola() {}

    public Parola(String date, String parola, String language) {
        this.date = date;
        this.parola = parola;
        this.language = language;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getParola() {
        return parola;
    }

    public void setParola(String parola) {
        this.parola = parola;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public String toString() {
        return "Date: " + date + " - Language: " + language + " - Parola; " + parola;
    }
}
