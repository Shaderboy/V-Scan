package com.sondsara.v_scan;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class Database extends SQLiteAssetHelper {

    private static String DATABASE_NAME = "test.db";
    //Make sure this number gets incremented each time the database gets updated.
    private static final int DATABASE_VERSION = 27;

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        setForcedUpgrade(DATABASE_VERSION);
    }

    public Cursor getIngredients() {

        SQLiteDatabase db = getReadableDatabase();
        db.setMaximumSize(500000);
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        //These are the column names from the animal_products table.
        String[] sqlSelect = {"0 _id", "NAME", "DERIVATION", "STATUS"};
        String sqlTables = "animal_products";

        qb.setTables(sqlTables);
        //This creates a handle to the query to return to our main class.
        Cursor c = qb.query(db, sqlSelect, null, null, null, null, null);

        c.moveToFirst();
        return c;

    }

    //Same thing as above but for known vegan products
    public Cursor getVeggies(){
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[] sqlSelect = {"0 _id", "NAME", "COMPANY"};
        String sqlTables = "vegan_products";

        qb.setTables(sqlTables);
        Cursor c = qb.query(db, sqlSelect, null, null, null, null, null);

        c.moveToFirst();
        return c;
    }

}
