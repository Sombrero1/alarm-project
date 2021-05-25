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

import com.example.myapplication.Database;
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


        ((TextView) view.findViewById(R.id.time_alarm)).setText(alarm_clock_item.getTime());
        ((TextView) view.findViewById(R.id.days)).setText(getDaysString(alarm_clock_item.getCheckboxs()));

        CheckBox cbBuy = (CheckBox) view.findViewById(R.id.checkbox_alarm);
        cbBuy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                alarm_clock_item.setSelected(isChecked);
                alarm_clock_item.setTime(alarm_clock_item.getTime());//не факт что нужно
                notifyDataSetChanged();
                new Database.HttpRequestAlarmUpdate().execute(Database.toAlarmFromAlarmItem(alarm_clock_item));
            }
        });
        cbBuy.setChecked(alarm_clock_item.isSelected());


//        ((LinearLayout) view.findViewById(R.id.linearLayout0)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(context, MainActivity2.class);
//                context.startActivity(intent);
//            }
//        });

        return view;
    }

    public String getDaysString(boolean[] states){
        String []days = {"ПН","ВТ", "СР", "ЧТ", "ПТ", "СБ", "ВС"};
        String answer = "";

        for (int i = 0; i < 7 ; i++) {
            if (states[i]) answer +=" "+ days[i];
        }
//        if (answer.equals("")) answer = "-";

        return answer;
    }

}

