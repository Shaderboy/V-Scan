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

public class CustomListAdapter extends BaseAdapter {

    private ArrayList<HashMap<String, animalIngredient>> items;
    private Context cont;
    private TextView text, text2;
    private View view;
    Typeface font;

    public CustomListAdapter(Context context, ArrayList<HashMap<String, animalIngredient>> data){
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
        LayoutInflater inflater = (LayoutInflater) cont.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        view = v;

        if (view == null) {
            view = inflater.inflate(R.layout.custom_list, null);
            text = (TextView) view.findViewById(R.id.textView);
            text2 = (TextView) view.findViewById(R.id.textView2);
        }

        final animalIngredient one = items.get(position).get("one");
        final animalIngredient two = items.get(position).get("two");

        String status1 = one.status;
        String name1 = one.name;
        String status2 = two.status;
        String name2 = two.name;

        text.setText(name1);
        text.setTypeface(font);
        if (status1.equals("")) {
            text.setTextColor(Color.BLACK);
            text.setClickable(false);
        } else if (status1.equals("Sometimes Vegan")) {
            text.setTextColor(Color.YELLOW);
            text.setClickable(true);
        } else if (status1.equals("Not Vegan")) {
            text.setTextColor(Color.RED);
            text.setClickable(true);
        }

        if (text.isClickable()) {
            text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View vw) {
                    ShowInfo(one);
                }
            });
        }

        text2.setText(name2);
        text2.setTypeface(font);
        if (status2.equals("")) {
            text2.setTextColor(Color.BLACK);
            text2.setClickable(false);
        } else if (status2.equals("Sometimes Vegan")){
            text2.setTextColor(Color.YELLOW);
            text2.setClickable(true);
        }else if (status2.equals("Not Vegan")) {
            text2.setTextColor(Color.RED);
            text2.setClickable(true);
        }

        if (text2.isClickable()) {
            text2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View vw) {
                    ShowInfo(two);
                }
            });
        }

        return view;
    }

    private void ShowInfo(animalIngredient clicked){
        View v = ((Activity)cont).getWindow().getDecorView().findViewById(android.R.id.content);

        final TextView info = (TextView) v.findViewById(R.id.popupText);
        info.setText("Derived from " + clicked.derivation);
        if (clicked.status.equals("Not Vegan"))
            info.setTextColor(Color.RED);
        else
            info.setTextColor(Color.YELLOW);
        info.setVisibility(View.VISIBLE);

        final TextView header = (TextView) v.findViewById(R.id.popupHeader);
        header.setText(clicked.name);
        if (clicked.status.equals("Not Vegan"))
            header.setTextColor(Color.RED);
        else
            header.setTextColor(Color.YELLOW);
        header.setVisibility(View.VISIBLE);

        final ImageView window = (ImageView) v.findViewById(R.id.popupWindow);
        window.setVisibility(View.VISIBLE);

        final ImageView background = (ImageView) v.findViewById(R.id.background);
        background.setClickable(true);
        background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vw) {
                info.setVisibility(View.INVISIBLE);
                header.setVisibility(View.INVISIBLE);
                window.setVisibility(View.INVISIBLE);
                background.setClickable(false);
            }
        });
    }

}
