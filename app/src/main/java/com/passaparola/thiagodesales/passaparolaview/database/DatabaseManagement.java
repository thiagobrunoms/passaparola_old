package com.passaparola.thiagodesales.passaparolaview.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseManagement extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "passaparola.db";

    public DatabaseManagement(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d("onCreate Database", "Criando e executando SQL...");
        sqLiteDatabase.execSQL(DatabaseDefinitions.Meditations.SQL_CREATE_ENTRIES);
        sqLiteDatabase.execSQL(DatabaseDefinitions.Parolas.SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.d("onUpgrade", "Adicionando tabela de parolas - newVersion " + newVersion);
    }
}
