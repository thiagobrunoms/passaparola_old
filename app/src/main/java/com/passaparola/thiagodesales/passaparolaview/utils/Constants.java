package com.passaparola.thiagodesales.passaparolaview.utils;

public enum Constants {

    PAROLA ("Parola"), MEDITATION ("Meditation"), PUBLISED_DATE("Published Date");

    private String constant;

    private Constants(String constant) {
        this.constant = constant;
    }

    public String getConstantName() {
        return this.constant;
    }

}
