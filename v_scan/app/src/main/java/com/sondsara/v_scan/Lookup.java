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

public class Lookup extends ActionBarActivity {

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

        //Set our local array lists and upc from the first activity's.
        //animalProducts = intent.getParcelableArrayListExtra("key");
        animalProducts = new ArrayList<animalIngredient> (MainActivity.animalProducts.values());
        animalProducts.addAll(MainActivity.spacedAnimalProducts.values());
        //veganProducts = intent.getParcelableArrayListExtra("key2");
        //String[] options = new String[animalProducts.size()];

        list = (ListView) findViewById(R.id.productsList);
        background = (ImageView) findViewById(R.id.lookupBG);
        //autoList = (ListView) findViewById(R.id.autoList);
        //searchBar = (AutoCompleteTextView) findViewById(R.id.search);
        //This makes the whole layout stretch to fit the screen.
        background.setScaleType(ImageView.ScaleType.FIT_XY);

        //Create a new Array list of string-animalIngredient pairs to send to our display class.
        ArrayList<HashMap<String, animalIngredient>> gris = new ArrayList<HashMap<String, animalIngredient>>();
        HashMap<String, animalIngredient> map;

        //Add all the ingredients two at a time.
        for (int q = 0; q < animalProducts.size(); q+=2) {
            map = new HashMap<String, animalIngredient>();
            //If our badSeeds array has something at the same index as our ingredient, pass through all its information so we can mark it.
            map.put("one", animalProducts.get(q));
            //options[q] = animalProducts.get(q).name;

            if (q < animalProducts.size() - 1) {
                //Do the same for another ingredient at the same time, so we can display in two columns. Check the CustomListAdapter class to see how this works.
                map.put("two", animalProducts.get(q + 1));
                //options[q + 1] = animalProducts.get(q + 1).name;
            }else{
                animalIngredient fake = new animalIngredient("", "", "");
                map.put("two", fake);
            }
            gris.add(map);
        }

        CustomListAdapter adapt = new CustomListAdapter(context, gris);
        list.setAdapter(adapt);

        list.setVisibility(View.VISIBLE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        //super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate (R.menu.options_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.search_bar);

        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setSubmitButtonEnabled(true);
        //searchView.setOnQueryTextListener(this);

        return super.onCreateOptionsMenu(menu);
    }

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

}
