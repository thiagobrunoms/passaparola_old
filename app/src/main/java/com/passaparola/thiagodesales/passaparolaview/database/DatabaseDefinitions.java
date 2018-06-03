package com.passaparola.thiagodesales.passaparolaview.database;

import android.provider.BaseColumns;

final class DatabaseDefinitions {

    private DatabaseDefinitions() {}


    public static class Meditations implements BaseColumns {
        public static String TABLE_NAME = "Meditations";
        public static String DATE = "Date";
        public static String PAROLA = "Parola";
        public static String LANGUAGE = "language";
        public static String MEDITATION = "Meditation";

        public static String SQL_CREATE_ENTRIES = "CREATE TABLE " + TABLE_NAME + "(" + DATE + " text, "
                + PAROLA + " text, " + LANGUAGE + " text, " + MEDITATION + " text, PRIMARY KEY (" + DATE + ", " + LANGUAGE + ") )";
    }

    public static class Parolas implements BaseColumns {
        public static String TABLE_NAME = "Parolas";
        public static String DATE = "Date";
        public static String PAROLA = "Parola";
        public static String LANGUAGE = "language";

        public static String SQL_CREATE_ENTRIES = "CREATE TABLE " + TABLE_NAME + "(" + DATE + " text, "
                + PAROLA + " text, " + LANGUAGE + " text, PRIMARY KEY (" + DATE + ", " + LANGUAGE + ") )";
    }

}
