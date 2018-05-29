package com.passaparola.thiagodesales.passaparolaview.utils;

public enum ConstantNames {

    PAROLA ("Parola"), MEDITATION ("Meditation"), PUBLISED_DATE("Published Date");

    private String constant;

    private ConstantNames(String constant) {
        this.constant = constant;
    }

    public String getConstantName() {
        return this.constant;
    }

}
