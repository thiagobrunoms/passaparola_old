package com.passaparola.thiagodesales.passaparolaview.model;

import java.util.HashMap;

public class RSSMeditationItem {

    private String publishedDate;
    private String parolaPt;
    private String parolaIt;
    private String meditationPt;
    private String meditationIt;
//    private HashMap<>

    public RSSMeditationItem(String publishedDate, String parolaPt, String parolaIt, String meditationPt, String meditationIt) {
        this.publishedDate = publishedDate;
        this.parolaPt = parolaPt;
        this.parolaIt = parolaIt;
        this.meditationPt = meditationPt;
        this.meditationIt = meditationIt;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String getParolaPt() {
        return parolaPt;
    }

    public void setParolaPt(String parolaPt) {
        this.parolaPt = parolaPt;
    }

    public String getParolaIt() {
        return parolaIt;
    }

    public void setParolaIt(String parolaIt) {
        this.parolaIt = parolaIt;
    }

    public String getMeditationPt() {
        return meditationPt;
    }

    public void setMeditationPt(String meditationPt) {
        this.meditationPt = meditationPt;
    }

    public String getMeditationIt() {
        return meditationIt;
    }

    public void setMeditationIt(String meditationIt) {
        this.meditationIt = meditationIt;
    }

    @Override
    public String toString() {
        return "RSSMeditationItem{" +
                "publishedDate='" + publishedDate + '\'' +
                ", parolaPt='" + parolaPt + '\'' +
                ", parolaIt='" + parolaIt + '\'' +
                ", meditationPt='" + meditationPt + '\'' +
                ", meditationIt='" + meditationIt + '\'' +
                '}';
    }
}
