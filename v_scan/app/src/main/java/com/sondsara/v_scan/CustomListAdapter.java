package com.sondsara.v_scan;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

//This is our custom display class to show the user the ingredients from the scan.
public class CustomListAdapter extends BaseAdapter {

    private ArrayList<animalIngredient[]> items;
    private Context cont;
    private TextView text, text2;
    private View view;
    private final static int r = 255;
    private final static int g = 153;
    private final static int b = 51;
    Typeface font;

    //When it gets constructed, set the constructor arguments to their corresponding global variables.
    public CustomListAdapter(Context context, ArrayList<animalIngredient[]> data){
        super();
        //act = activity;
        cont = context;
        items = data;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        final ViewHolder holder;

        //Make our new layout.
        LayoutInflater inflater = (LayoutInflater) cont.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        view = v;

        if (view == null) {
            view = inflater.inflate(R.layout.custom_list, null);
            holder = new ViewHolder();

            //Get reference to the text views within the layout.
            holder.one = (TextView) view.findViewById(R.id.textView);
            holder.two = (TextView) view.findViewById(R.id.textView2);
            view.setTag(holder);
        }else{
            holder = (ViewHolder)view.getTag();
        }

        //Get references to each ingredient we passed in. They may not necessarily be animal ingredients, but we're just using that class.
        final animalIngredient one = items.get(position)[0];
        final animalIngredient two = items.get(position)[1];

        //Get the info from each so we can determine how to display them.
        String status1 = one.status;
        String status2 = two.status;

        holder.one.setText(one.name);

        //Set the text color and whether or not we can click on it based on if it's guaranteed vegan or not.
        if (status1.equals("")) {
            holder.one.setTextColor(Color.BLACK);
            holder.one.setClickable(false);
        } else if (status1.equals("Sometimes Vegan")) {
            holder.one.setTextColor(Color.rgb(r, g, b));
            holder.one.setClickable(true);
        } else if (status1.equals("Not Vegan")) {
            holder.one.setTextColor(Color.RED);
            holder.one.setClickable(true);
        }

        if (holder.one.isClickable()) {
            holder.one.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View vw) {
                    ShowInfo(one);
                }
            });
        }

        //All the same as for the first ingredient.
        holder.two.setText(two.name);
        if (status2.equals("")) {
            holder.two.setTextColor(Color.BLACK);
            holder.two.setClickable(false);
        } else if (status2.equals("Sometimes Vegan")){
            holder.two.setTextColor(Color.rgb(r, g, b));
            holder.two.setClickable(true);
        }else if (status2.equals("Not Vegan")) {
            holder.two.setTextColor(Color.RED);
            holder.two.setClickable(true);
        }

        if (holder.two.isClickable()) {
            holder.two.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View vw) {
                    ShowInfo(two);
                }
            });
        }

        return view;
    }

    //This pops up the information box if an ingredient is tapped.
    private void ShowInfo(animalIngredient clicked){

        View v = ((Activity)cont).getWindow().getDecorView().findViewById(android.R.id.content);

        //Display our info text in the proper color.
        final TextView info = (TextView) v.findViewById(R.id.popupText);
        info.setText("Derived from " + clicked.derivation);
        if (clicked.status.equals("Not Vegan"))
            info.setTextColor(Color.RED);
        else
            info.setTextColor(Color.rgb(r, g, b));
        info.setVisibility(View.VISIBLE);

        //Display the ingredient's name.
        final TextView header = (TextView) v.findViewById(R.id.popupHeader);
        header.setText(clicked.name);
        if (clicked.status.equals("Not Vegan"))
            header.setTextColor(Color.RED);
        else
            header.setTextColor(Color.rgb(r, g, b));
        header.setVisibility(View.VISIBLE);

        //Set the popup window itself to be visible. TODO: Get a better graphic for this window.
        final ImageView window = (ImageView) v.findViewById(R.id.popupWindow);
        window.setVisibility(View.VISIBLE);

        //If the user clicks on the background outside of the info box, close the info box and bring them back to the results page.
        final ImageView background = (ImageView) v.findViewById(R.id.haze);
        background.setVisibility(View.VISIBLE);
        background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vw) {
                info.setVisibility(View.INVISIBLE);
                header.setVisibility(View.INVISIBLE);
                window.setVisibility(View.INVISIBLE);
                background.setClickable(false);
                background.setVisibility(View.INVISIBLE);
            }
        });
    }

    private class ViewHolder{
        private TextView one;
        private TextView two;
    }

}
