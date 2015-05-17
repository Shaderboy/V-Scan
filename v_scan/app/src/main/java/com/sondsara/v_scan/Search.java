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
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

//TODO: Since we're auto-culling the display based on real time input, figure out what to do when they hit enter!
//TODO:(i.e. when this class gets called).
public class Search extends Activity {

    private ListView list;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lookup);

        list = (ListView) findViewById(R.id.productsList);

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

            TreeMap<String, animalIngredient> sortedProducts = Lookup.sortedProducts;
            ArrayList<animalIngredient> results = new ArrayList<animalIngredient>();
            ArrayList<String> names = new ArrayList<String>();

            //Search for our query.
            for (Map.Entry<String, animalIngredient> entry : filterPrefix(sortedProducts, query).entrySet()){
                results.add(entry.getValue());
            }

            boolean good = false;

            if (query.contains(" ") && (char) (query.charAt(query.length() - 1)) != ' '){
                String[] words = query.split(" ");
                //for (Map.Entry<String, animalIngredient> entry : Search.filterPrefix(sortedProducts, words[1]).entrySet()){
                //    results.add(entry.getValue());
                //}
                for (int w = 0; w < words.length; w++) {
                    if (Analyze.excludes.contains(words[w])) {
                        good = true;
                    }
                }
                if (!good) {

                    //If it's directly equal, add it to our shit list.
                    for (int w = 0; w < words.length; w++) {
                        if (MainActivity.animalProducts.containsKey(words[w])) {
                            animalIngredient ap = MainActivity.animalProducts.get(words[w]);
                            if (ap.name.equals(query))
                                results.add(ap);
                                //If it's not directly equal but contains an animal ingredient on our list, make a new item for it using the info from the item that flagged it.
                            else {
                                animalIngredient temp = new animalIngredient(query, ap.derivation, ap.status);
                                results.add(temp);
                            }
                        }
                    }
                }
            }

            SearchAdapter adapter = new SearchAdapter(this, results);
            list.setAdapter(adapter);

            list.setVisibility(View.VISIBLE);
        }
    }

    public static <V> SortedMap<String, V> filterPrefix (SortedMap<String, V> baseMap, String prefix){
        if (prefix.length() > 0){
            char nextLetter = (char) (prefix.charAt(prefix.length() - 1) + 1);
            String end = prefix.substring(0, prefix.length() - 1) + nextLetter;
            return baseMap.subMap(prefix, end);
        }
        return baseMap;
    }

}
