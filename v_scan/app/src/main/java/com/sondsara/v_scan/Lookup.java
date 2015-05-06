package com.sondsara.v_scan;

import android.app.ActionBar;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Lookup extends ActionBarActivity implements SearchView.OnQueryTextListener {

    public static TreeMap<String, animalIngredient> sortedProducts = new TreeMap<String, animalIngredient>();

    private Context context;
    private ArrayList<animalIngredient> animalProducts;
    //private HashMap <String, animalIngredient> animalProducts;
    private ArrayList<veganProduct> veganProducts;
    private ListView list;
    private ImageView background;
    private AutoCompleteTextView searchBar;
    private ListView autoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lookup);

        Intent intent = this.getIntent();
        context = this;

        list = (ListView) findViewById(R.id.productsList);
        background = (ImageView) findViewById(R.id.lookupBG);
        //autoList = (ListView) findViewById(R.id.autoList);
        //searchBar = (AutoCompleteTextView) findViewById(R.id.search);
        //This makes the whole layout stretch to fit the screen.
        background.setScaleType(ImageView.ScaleType.FIT_XY);

        //Create a new Array list of animalIngredient pairs to send to our display class.
        sortedProducts.putAll(MainActivity.animalProducts);
        sortedProducts.putAll(MainActivity.spacedAnimalProducts);

        //Set our local array lists and upc from the first activity's.
        animalProducts = new ArrayList<animalIngredient> (sortedProducts.values());
        ArrayList<String> names = new ArrayList<String>();

        SearchAdapter adapter = new SearchAdapter(this, animalProducts);
        list.setAdapter(adapter);

        list.setVisibility(View.VISIBLE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        MenuInflater inflater = getMenuInflater();
        inflater.inflate (R.menu.options_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.search_bar);

        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setSubmitButtonEnabled(false);
        searchView.setOnQueryTextListener(this);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        animalProducts.clear();
        for (Map.Entry<String, animalIngredient> entry : Search.filterPrefix(sortedProducts, newText).entrySet()){
            animalProducts.add(entry.getValue());
        }

        SearchAdapter adapter = new SearchAdapter(this, animalProducts);
        list.setAdapter(adapter);

        return true;
    }

}
