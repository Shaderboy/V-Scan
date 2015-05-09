package com.sondsara.v_scan;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class SearchAdapter extends ArrayAdapter<animalIngredient>{

    private Context context;
    private ArrayList <animalIngredient> data;
    private View view;
    private TextView nameView, statusView;
    private final static int r = 255;
    private final static int g = 153;
    private final static int b = 51;

    public SearchAdapter(Context context, ArrayList<animalIngredient> data){
        super(context, R.layout.search_list, data);
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView (int position, View v, ViewGroup parent){
        //Make our new layout.
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        view = v;

        if (view == null) {
            view = inflater.inflate(R.layout.search_list, parent, false);
            //Get reference to the text views within the layout.
            nameView = (TextView) view.findViewById(R.id.nameView);
            statusView = (TextView) view.findViewById(R.id.statusView);
        }

        final animalIngredient ap = data.get(position);

        nameView.setText(ap.name);
        statusView.setText(ap.status);

        nameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vw) {
                ShowInfo(ap);
            }
        });
        statusView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vw) {
                ShowInfo(ap);
            }
        });

        return view;
    }

    private void ShowInfo(animalIngredient clicked){
        View v = ((Activity)context).getWindow().getDecorView().findViewById(android.R.id.content);
        v.setClickable(false);

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

}
