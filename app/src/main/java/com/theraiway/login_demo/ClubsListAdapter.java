package com.theraiway.login_demo;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Custom adapter for lists
 */
public class ClubsListAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final String[] values;
    private final int[] drawableImages = {R.drawable.apc,R.drawable.music,R.drawable.drama,R.drawable.saasc,
    R.drawable.pdc,R.drawable.rotaract,R.drawable.english};

    boolean flag = true;

    public ClubsListAdapter(Context context , String[] values){
        super(context,-1,values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.list_item_club,parent,false);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.imageView);
        TextView textView = (TextView) rowView.findViewById(R.id.list_item_club_textview);

        textView.setText(values[position]);
        if(flag){
            textView.setTextColor(Color.RED);
            flag = false;
        }else{
            textView.setTextColor(Color.BLUE);
            flag = true;
        }

        imageView.setImageResource(drawableImages[position]);

        return rowView;
    }
}