package com.sondsara.v_scan;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.factual.driver.Factual;
import com.factual.driver.Query;
import com.factual.driver.ReadResponse;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

//This is where all the actual logic and display happens.
public class Analyze extends Activity {

    String upc = "";
    protected Factual factual = new Factual("wDhW6eCPSv2BwdJvvQP63Pbpat7cWAkTnxSazIRM", "9ZIQc30A8DM0sX4qBtZoXE8DtZzf9X5sOQRR3bWf", true);
    private final Set<String> excludes = Collections.unmodifiableSet(new HashSet<String>() {
        {
            add("almond");
            add("soy");
            add("cashew");
            add("peanut");
            add("hemp");
            add("rice");
            add("cocoa");
            add("coconut");
            add("sunflower");
            add("vegetable");
        }
    });

    private animalIngredient[] badSeeds;
    private HashMap<String, animalIngredient> animalProducts;
    private HashMap<String, animalIngredient> spacedAnimalProducts;
    private HashMap<String, veganProduct> veganProducts;
    private ListView list;
    private Context context;
    private ImageView pic;
    private ImageView background;
    private TextView resultText = null;
    private ProgressBar progress;
    private ImageButton reset;
    private Button lookupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze);

        Intent intent = this.getIntent();
        context = this;

        //Set our local array lists and upc from the first activity's.
        //animalProducts = intent.getParcelableArrayListExtra("key");
        animalProducts = MainActivity.animalProducts;
        spacedAnimalProducts = MainActivity.spacedAnimalProducts;
        veganProducts = MainActivity.veganProducts;
        upc = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        //Get references to all the gui elements.
        resultText = (TextView) findViewById(R.id.resultText);
        list = (ListView) findViewById(R.id.listView);
        pic = (ImageView) findViewById(R.id.imageView);
        background = (ImageView) findViewById(R.id.lookupBG);
        progress = (ProgressBar) findViewById(R.id.progressBar);
        reset = (ImageButton) findViewById(R.id.restartButton);
        lookupButton = (Button) findViewById(R.id.lookupButton);

        //This makes the whole layout stretch to fit the screen.
        background.setScaleType(ImageView.ScaleType.FIT_XY);

        //FactualRetrievalTask is what interacts with our online product database.
        FactualRetrievalTask task = new FactualRetrievalTask();
        task.execute();

    }

    void listenLookup(){
        lookupButton.setVisibility(View.VISIBLE);
        lookupButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View vw){
                //Launch our ingredient search class.
                Intent intent = new Intent(Analyze.this, Lookup.class);
                startActivity(intent);
            }
        });
    }

    void listenRestart() {
        //If they click the "Scan Again" button on the bottom, go to our restart function.
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View vw) {
                restart();
            }
        });
    }

    void restart(){

        //Start our main activity again. TODO: Don't re-instantiate the main class.
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        //Tell the activity to keep track of this one, instead of making a new one every time it launches this.
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.startActivity(intent);

        //Set everything to be invisible again.
        list.setVisibility(View.INVISIBLE);
        pic.setVisibility(View.INVISIBLE);
        reset.setVisibility(View.INVISIBLE);
        progress.setVisibility(View.VISIBLE);
        resultText.setVisibility(View.INVISIBLE);

        ImageView window = (ImageView) findViewById(R.id.popupWindow);
        TextView info = (TextView) findViewById(R.id.popupText);
        TextView header = (TextView) findViewById(R.id.popupHeader);
        if (window.getVisibility() == View.VISIBLE){
            window.setVisibility(View.INVISIBLE);
            info.setVisibility(View.INVISIBLE);
            header.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //This is an asynchronous task, meaning it runs in the background, allowing us to do other stuff while it's loading.
    protected class FactualRetrievalTask extends AsyncTask<Query, Integer, ReadResponse> {

        //Use an int instead of a boolean so we can include Maybe Vegan. TODO: Possibly switch this to an enumerator for more readable code.
        int isVegan = 1;

        @Override
        protected ReadResponse doInBackground (Query... params){
            try {
                //Factual API stuff: tell it to look up our upc code and to get the nutrition data for that product.
                Query query = new Query().field("upc").isEqual(upc);
                ReadResponse resp = factual.fetch("products-cpg-nutrition", query);
                return resp;
            }catch (Exception e){
                //If it can't connect to the database, print out our error message for the user to see.
                StringBuffer sb = new StringBuffer();
                sb.append("I can't connect to the database.... Sorry!");
                resultText.setText(sb.toString());
                resultText.setVisibility(View.VISIBLE);
                progress.setVisibility(View.INVISIBLE);
                listenLookup();
                //Set up the listener to rescan.
                listenRestart();
                return null;
            }
        }

        //This will automatically be called when ReadResponse has finished, and will take the value it returned.
        @Override
        protected void onPostExecute(ReadResponse resp) {

            StringBuffer sb = new StringBuffer();
            sb.append("");
            String contains = "";
            Boolean safe = false;

            if (resp != null) {

                String product;
                String company;

                try{
                    product = (String) resp.getData().get(0).get("product_name");
                    product = product.toLowerCase();
                    company = (String) resp.getData().get(0).get("brand");
                    company = company.toLowerCase();

                    //First we check to see if our product is made by a company that's on our good list.
                    if (veganProducts.containsKey(company)){
                        String vegProduct = veganProducts.get(company).name;
                        //If it is, and that company is 100% vegan, skip the rest and go straight to the result.
                        if (vegProduct.equals("Any")){
                            safe = true;
                        //Also do the same if the company only has a couple of things that aren't vegan and our product isn't one of them.
                        }else if (vegProduct.contains("Any but")){
                            if (!vegProduct.contains(product)){
                                safe = true;
                            }
                        //Do the same if the company isn't 100% vegan, but our product is listed as good. It's important that this is the last thing to check.
                        }else if (product.contains(vegProduct)){
                            safe = true;
                        }
                    }
                }catch(Exception e){
                    e.printStackTrace();
                    sb.append ("Couldn't find that product.");
                    resultText.setText(sb.toString());
                    resultText.setVisibility(View.VISIBLE);
                    progress.setVisibility(View.INVISIBLE);
                    listenLookup();
                    //Set up the listener to rescan.
                    listenRestart();
                    return;
                }

                //Now we parse the ingredients via a JSON array that the factual database returns and put them into an array of strings.
                JSONArray ingredientsJSON = new JSONArray();
                try {
                    ingredientsJSON = (JSONArray) resp.getData().get(0).get("ingredients");
                }catch(Exception e){
                    sb.append ("Couldn't find that product.");
                    resultText.setText(sb.toString());
                    resultText.setVisibility(View.VISIBLE);
                    progress.setVisibility(View.INVISIBLE);
                    listenLookup();
                    //Set up the listener to rescan.
                    listenRestart();
                    return;
                }
                String[] ingredients = new String[ingredientsJSON.length()];
                badSeeds = new animalIngredient[ingredientsJSON.length()];

                try {
                    //If we didn't already declare it safe via our known-vegan list, we can check the ingredients.
                    if (!safe) {
                        //For each ingredient:
                        for (int i = 0; i < ingredientsJSON.length(); i++) {

                            //TODO:Improve "contains less than x%" logic
                            if (ingredientsJSON.getString(i).toLowerCase().contains("contains less") ||
                                    !ingredientsJSON.getString(i).toLowerCase().contains("contains"))
                                ingredients[i] = ingredientsJSON.getString(i).toLowerCase();
                            else
                                //Keep track of statements like "Contains:", but don't include it as one of the ingredients.
                                contains = ingredientsJSON.getString(i).toLowerCase();

                            String[] ingWords = ingredients[i].split(" ");

                            //If our ingredient has that animal product in it's name and ISN'T almond or soy, etc...
                            //For example, since milk is an animal product, whole milk and skim milk will get flagged, but almond milk won't.
                            boolean good = false;
                            for (int w = 0; w < ingWords.length; w++) {
                                if (excludes.contains(ingWords[w])) {
                                    good = true;
                                }
                            }

                            if (!good){

                                //If it's directly equal, add it to our shit list.
                                for (int w = 0; w < ingWords.length; w++){
                                    if (animalProducts.containsKey(ingWords[w].toLowerCase())){
                                        animalIngredient ap = animalProducts.get(ingWords[w].toLowerCase());
                                        if (ap.name.equals(ingredients[i]))
                                            badSeeds[i] = ap;
                                            //If it's not directly equal but contains an animal ingredient on our list, make a new item for it using the info from the item that flagged it.
                                        else {
                                            animalIngredient temp = new animalIngredient(ingredients[i], ap.derivation, ap.status);
                                            badSeeds[i] = temp;
                                        }

                                        //If we've come across an ingredient that's not vegan or sometimes vegan, we already know that the whole product has that same status.
                                        CheckStatus(ap);
                                        break;

                                    //Some products are tricky. They won't list animal products in the ingredients, so let's also check the Contains: statement that we saved earlier.
                                    }
                                }

                                if (badSeeds[i] == null) {
                                    if (spacedAnimalProducts.containsKey(ingredients[i])) {
                                        badSeeds[i] = animalProducts.get(ingredients[i]);

                                        //If we've come across an ingredient that's not vegan or sometimes vegan, we already know that the whole product has that same status.
                                        CheckStatus(animalProducts.get(ingredients[i]));
                                    }
                                }
                            }
                        }
                        //Some products are tricky. They won't list animal products in the ingredients, so let's also check the Contains: statement that we saved earlier.
			            String[] containsWords = contains.split(" ");
                        for (int i = 0; i < containsWords.length; i++) {
                            if (animalProducts.containsKey(containsWords[i]));
                                CheckStatus(animalProducts.get(containsWords));
                        }
                    } else {
                        //If we're already safe, just log the ingredients to show the user.
                        for (int i = 0; i < ingredientsJSON.length(); i++) {
                            if (!ingredientsJSON.getString(i).toLowerCase().contains("contains"))
                                ingredients[i] = ingredientsJSON.getString(i);
                        }
                    }
                }catch (Exception e) {
                    sb.append("Couldn't parse the ingredients.");
                    resultText.setText(sb.toString());
                    resultText.setVisibility(View.VISIBLE);
                    progress.setVisibility(View.INVISIBLE);
                    listenLookup();
                    //Set up the listener to rescan.
                    listenRestart();
                    return;
                }
                try {
                    //Set the picture and background based on our result.
                    if (isVegan == 1) {
                        pic.setImageResource(R.drawable.vegan);
                        background.setImageResource(R.drawable.green_background);
                    } else if (isVegan == -1) {
                        pic.setImageResource(R.drawable.not_vegan);
                        background.setImageResource(R.drawable.red_background);
                    } else if (isVegan == 0) {
                        pic.setImageResource(R.drawable.maybe_vegan);
                        background.setImageResource(R.drawable.orange_background);
                    }

                    //Create a new Array list of animalIngredient pairs to send to our display class.
                    ArrayList<animalIngredient[]> gris = new ArrayList<animalIngredient[]>();
                    animalIngredient[] map;

                    //Add all the ingredients two at a time.
                    for (int q = 0; q < ingredients.length; q+=2) {
                        map = new animalIngredient[2];
                        //If our badSeeds array has something at the same index as our ingredient, pass through all its information so we can mark it.
                        if (badSeeds[q] != null)
                            map[0] = badSeeds[q];
                            //If not, pass in the name of the ingredient and some dummy information (since we don't have a record of that ingredient).
                        else {
                            animalIngredient temp = new animalIngredient(ingredients[q], "", "");
                            map[0] = temp;
                        }

                        if (q < ingredients.length - 1) {
                            //Do the same for another ingredient at the same time, so we can display in two columns. Check the CustomListAdapter class to see how this works.
                            if (badSeeds[q + 1] != null)
                                map[0] = badSeeds[q + 1];
                            else {
                                animalIngredient temp = new animalIngredient(ingredients[q + 1], "", "");
                                map[1] = temp;
                            }
                        }else{
                            animalIngredient fake = new animalIngredient("", "", "");
                            map[1] = fake;
                        }
                        gris.add(map);
                    }

                    CustomListAdapter adapt = new CustomListAdapter(context, gris);
                    list.setAdapter(adapt);

                    list.setVisibility(View.VISIBLE);
                    pic.setVisibility(View.VISIBLE);
                    reset.setVisibility(View.VISIBLE);
                    progress.setVisibility(View.GONE);

                } catch (Exception e) {
                    sb.append("Couldn't set adapter.");
                    resultText.setText(sb.toString());
                    resultText.setVisibility(View.VISIBLE);
                    progress.setVisibility(View.INVISIBLE);
                    reset.setVisibility(View.VISIBLE);
                    e.printStackTrace();
                }
            }else{
                sb.append("Sorry, it looks like the database isn't working right now... Try again later.");
                resultText.setText(sb.toString());
                resultText.setVisibility(View.VISIBLE);
                progress.setVisibility(View.INVISIBLE);
                reset.setVisibility(View.VISIBLE);
                listenLookup();
                //Set up the listener to rescan.
                listenRestart();
                return;
            }
            //Set up the listener to rescan.
            listenRestart();
        }

        void CheckStatus (animalIngredient product){
            if (product.status.equals("Not Vegan"))
                isVegan = -1;
            else if (product.status.equals("Sometimes Vegan") && isVegan != -1)
                isVegan = 0;
        }

    }
}
