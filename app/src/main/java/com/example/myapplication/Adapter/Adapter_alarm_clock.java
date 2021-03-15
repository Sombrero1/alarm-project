package com.example.myapplication.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.myapplication.R;

import java.util.ArrayList;

public class Adapter_alarm_clock extends BaseAdapter {
    private ArrayList<Alarm_clock_item> items;
    private Context context;



    public Adapter_alarm_clock(Context context, ArrayList<Alarm_clock_item> items){
        this.context = context;
        this.items = items;

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
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        Alarm_clock_item alarm_clock_item = ((Alarm_clock_item) getItem(position));
        if(view == null){
            view =LayoutInflater.from(context).inflate(
                    R.layout.item, parent, false);
        }
        Log.d("debug",""+view);

        ((TextView) view.findViewById(R.id.time_alarm)).setText(alarm_clock_item.getTime());
        ((TextView) view.findViewById(R.id.days)).setText(alarm_clock_item.getDays());
        CheckBox cbBuy = (CheckBox) view.findViewById(R.id.checkbox_alarm);
        cbBuy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                alarm_clock_item.setTime(alarm_clock_item.getTime());
                notifyDataSetChanged();
            }
        });



//        ((LinearLayout) view.findViewById(R.id.linearLayout0)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(context, MainActivity2.class);
//                context.startActivity(intent);
//            }
//        });

        return view;
    }

}

