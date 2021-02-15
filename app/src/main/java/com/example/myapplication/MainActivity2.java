package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.myapplication.Adapter.Alarm_clock_item;

import java.util.Calendar;

public class MainActivity2 extends AppCompatActivity implements View.OnClickListener {
    ImageButton btn_cancel;
    ImageButton btn_save;
    View time_form;
    TextView time;
    Calendar dateAndTime=Calendar.getInstance();
    int id=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_save = findViewById(R.id.btn_save);
        time_form = findViewById(R.id.time_form);
        time = findViewById(R.id.time_2);

        btn_cancel.setOnClickListener(this);
        btn_save.setOnClickListener(this);
        time_form.setOnClickListener(this);

        Alarm_clock_item alarm_clock_item = (Alarm_clock_item) getIntent().getSerializableExtra("clock_edit");
        if(alarm_clock_item!=null) {
            time.setText(toShortTime(alarm_clock_item.getTimeMil()));
            dateAndTime.setTimeInMillis(alarm_clock_item.getTimeMil());
            id=alarm_clock_item.getId();
            Log.d("my_debug","id:"+id);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_cancel:
                finish();
                break;
            case R.id.btn_save:

                Intent intent = getIntent().putExtra("clock",new Alarm_clock_item(id,(String) time.getText(),dateAndTime.getTimeInMillis(),"days",false));
                setResult(RESULT_OK,intent);
                finish();
                break;
            case R.id.time_form:
                setTime();
                Log.d("my_debug","btn ok click");
                break;


        }

    }

    public void setTime(){
        Alarm_clock_item alarm_clock_item = (Alarm_clock_item) getIntent().getSerializableExtra("clock_edit");
        int hours,mins;
        if(alarm_clock_item==null){
            hours=dateAndTime.get(Calendar.HOUR_OF_DAY);
            mins = dateAndTime.get(Calendar.MINUTE);
        }
        else{
            hours = dateAndTime.get(Calendar.HOUR_OF_DAY);
            mins = dateAndTime.get(Calendar.MINUTE);
            Log.d("my_debug","alarm!=null,hours:"+hours);
        }

        new TimePickerDialog(this,R.style.TimePickerTheme ,new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                dateAndTime.set(Calendar.MINUTE, minute);
                time.setText(toShortTime(dateAndTime.getTimeInMillis()));
                Toast.makeText(MainActivity2.this,"Будильник установлен на: "+hourOfDay+":"+minute,Toast.LENGTH_LONG).show();
            }
        }, hours, mins, true).show();
    }

    public String toShortTime(long timeInMillis){
        return DateUtils.formatDateTime(MainActivity2.this,timeInMillis,DateUtils.FORMAT_SHOW_TIME);
    }

}