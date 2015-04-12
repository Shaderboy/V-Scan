package com.example.chris.veganizer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
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
import java.util.HashMap;


public class MainActivity2 extends Activity {

    String upc = "";
    protected Factual factual = new Factual("wDhW6eCPSv2BwdJvvQP63Pbpat7cWAkTnxSazIRM", "9ZIQc30A8DM0sX4qBtZoXE8DtZzf9X5sOQRR3bWf", true);
    private animalIngredient[] badSeeds;
    private ArrayList<animalIngredient> animalProducts;
    private ListView list;
    private Context context;
    private ImageView pic;
    private ImageView background;
    private TextView resultText = null;
    private ProgressBar progress;
    private ImageButton reset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity2);

        Intent intent = this.getIntent();
        animalProducts = intent.getParcelableArrayListExtra("key");
        upc = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        resultText = (TextView) findViewById(R.id.resultText);
        list = (ListView) findViewById(R.id.listView);
        context = this;
        pic = (ImageView) findViewById(R.id.imageView);
        background = (ImageView) findViewById(R.id.background);
        progress = (ProgressBar) findViewById(R.id.progressBar);
        reset = (ImageButton) findViewById(R.id.restartButton);

        background.setScaleType(ImageView.ScaleType.FIT_XY);

        FactualRetrievalTask task = new FactualRetrievalTask();
        task.execute();

        listenRestart();
    }

    void listenRestart() {
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View vw) {
                restart();
            }
        });
    }

    void restart(){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.startActivity(intent);
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

    protected class FactualRetrievalTask extends AsyncTask<Query, Integer, ReadResponse> {

        @Override
        protected ReadResponse doInBackground (Query... params){
            try {
                Query query = new Query().field("upc").isEqual(upc);
                ReadResponse resp = factual.fetch("products-cpg-nutrition", query);
                return resp;
            }catch (Exception e){
                StringBuffer sb = new StringBuffer();
                sb.append("I can't connect to the database.... Sorry!");
                resultText.setText(sb.toString());
                resultText.setVisibility(View.VISIBLE);
                return null;
            }
        }

        @Override
        protected void onPostExecute(ReadResponse resp) {

            StringBuffer sb = new StringBuffer();
            sb.append("");
            int isVegan = 1;
            String contains = "";

            if (resp != null) {

                try {
                    JSONArray ingredientsJSON = (JSONArray) resp.getData().get(0).get("ingredients");
                    String[] ingredients = new String[ingredientsJSON.length()];
                    badSeeds = new animalIngredient[ingredientsJSON.length()];

                    for (int i = 0; i < ingredientsJSON.length(); i++) {

                        if (!ingredientsJSON.getString(i).contains("Contains"))
                            ingredients[i] = ingredientsJSON.getString(i);
                        else
                            contains = ingredientsJSON.getString(i);

                        for (int j = 0; j < animalProducts.size(); j++) {
                            if (ingredients[i].contains(animalProducts.get(j).name)) {
                                if (animalProducts.get(j).name.equals(ingredients[i]))
                                    badSeeds[i] = animalProducts.get(j);
                                else {
                                    String bsName = ingredients[i];
                                    String bsDerivation = animalProducts.get(j).derivation;
                                    String bsStatus = animalProducts.get(j).status;
                                    animalIngredient temp = new animalIngredient(bsName, bsDerivation, bsStatus);
                                    badSeeds[i] = temp;
                                }
                                if (animalProducts.get(j).status.equals("Not Vegan"))
                                    isVegan = -1;
                                else if (animalProducts.get(j).status.equals("Sometimes Vegan"))
                                    isVegan = 0;
                            } else if (contains.contains(animalProducts.get(j).name))
                                if (animalProducts.get(j).status.equals("Not Vegan"))
                                    isVegan = -1;
                                else if (animalProducts.get(j).status.equals("Sometimes Vegan"))
                                    isVegan = 0;
                        }
                    }

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

                    ArrayList<HashMap<String, animalIngredient>> gris = new ArrayList<HashMap<String, animalIngredient>>();
                    HashMap<String, animalIngredient> map;
                    for (int q = 0; q < ingredients.length - 1; q+=2) {
                        map = new HashMap<String, animalIngredient>();
                        if (badSeeds[q] != null)
                            map.put("one", badSeeds[q]);
                        else {
                            animalIngredient temp = new animalIngredient(ingredients[q], "", "");
                            map.put("one", temp);
                        }

                        if (badSeeds[q + 1] != null)
                            map.put("two", badSeeds[q + 1]);
                        else {
                            animalIngredient temp = new animalIngredient(ingredients[q + 1], "", "");
                            map.put("two", temp);
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
                    sb.append("Sorry, I couldn't find that in the database. You might have to go this one alone.");
                    resultText.setText(sb.toString());
                    resultText.setVisibility(View.VISIBLE);
                    e.printStackTrace();
                }
            }else{
                sb.append("Sorry, it looks like the database isn't working right now... Try again later.");
                resultText.setText(sb.toString());
                resultText.setVisibility(View.VISIBLE);
            }
        }

    }
}
