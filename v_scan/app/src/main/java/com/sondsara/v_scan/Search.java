package com.sondsara.v_scan;

import android.app.Activity;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.SearchView;

import java.util.ArrayList;

public class Search extends ListActivity {
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.abc_search_view);

        handleIntent(getIntent());

    }

    @Override
    protected void onNewIntent(Intent intent){
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent){
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Database db = new Database(this);

            Cursor c = db.getIngredients();
            ArrayList<animalIngredient> results = new ArrayList<animalIngredient>();
            ArrayList<String> names = new ArrayList<String>();

            //Look into red black tree or balanced binary search tree.
            //Search for our query.
            while (c.moveToNext()) {
                String name = c.getString(c.getColumnIndexOrThrow("NAME"));

                if (name.toLowerCase().contains(query.toLowerCase())) {
                    String derivation = c.getString(c.getColumnIndexOrThrow("DERIVATION"));
                    String status = c.getString(c.getColumnIndexOrThrow("STATUS"));
                    results.add(new animalIngredient(name, derivation, status));
                    names.add(name);
                }
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, names);
            setListAdapter(adapter);

        }
    }

}
