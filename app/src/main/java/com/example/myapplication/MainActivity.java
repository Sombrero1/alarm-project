package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.myapplication.Adapter.Adapter_alarm_clock;
import com.example.myapplication.Adapter.Alarm_clock_item;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    ListView clocks;
    ImageButton btn_add;
    ImageButton btn_settings;
    ImageButton btn_sinch;
    Database database;
    Adapter_alarm_clock adapter_alarm_clock;
    final int MENU_DELETE = 1;
    final int MENU_CANCEL = 2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        database = new Database(getSharedPreferences("data", Context.MODE_PRIVATE));



        adapter_alarm_clock = new Adapter_alarm_clock(this, database.getClocks());
        adapter_alarm_clock.notifyDataSetChanged();

        clocks = findViewById(R.id.clocks);
        clocks.setOnItemClickListener(this);
        clocks.setAdapter(adapter_alarm_clock);
        clocks.setOnItemClickListener(this);

        btn_add = findViewById(R.id.btn_save);
        btn_add.setOnClickListener(this);

        btn_add = findViewById(R.id.settings);
        btn_add.setOnClickListener(this);

        btn_sinch = findViewById(R.id.sinch);
        btn_sinch.setOnClickListener(this);

        registerForContextMenu(clocks);

        new Database.HttpRequestAlarmGet().execute(database);
        adapter_alarm_clock.notifyDataSetChanged();

    }

    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.btn_save){
            Intent intent = new Intent(MainActivity.this, AlarmActivity.class);
            startActivityForResult(intent,1);
        }
        if(v.getId()==R.id.settings){
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        }
        if(v.getId()==R.id.sinch){
            new Database.HttpRequestAlarmGet().execute(database);
            adapter_alarm_clock.notifyDataSetChanged();
        }
};

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) return;
        switch (requestCode){
            case 1:
                Alarm_clock_item alarm_clock_item = (Alarm_clock_item) data.getSerializableExtra("clock");
                database.insertClock(alarm_clock_item);
                adapter_alarm_clock.notifyDataSetChanged();
                break;
            case 2:
                Alarm_clock_item alarm_clock_item_edit = (Alarm_clock_item) data.getSerializableExtra("clock");
                database.updateClock(data.getIntExtra("position",0),alarm_clock_item_edit);
                adapter_alarm_clock.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0,MENU_DELETE, 0,"Удалить");
        menu.add(0,MENU_CANCEL, 1,"Отмена");
        Toast.makeText(this,"Удалён",Toast.LENGTH_SHORT);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case MENU_DELETE:
                AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

                database.deleteClock(acmi.position);
                adapter_alarm_clock.notifyDataSetChanged();
                break;
            case MENU_CANCEL:
                Log.d("my_debug","cancel");
                break;
        }

        return super.onContextItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(this,"Редактирование",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this, AlarmActivity.class);
        intent.putExtra("clock_edit",database.getClocks().get(position));
        intent.putExtra("position",position);
        startActivityForResult(intent,2);
    }

    @Override
    protected void onPause() {
        super.onPause();
        database.save();
    }
}