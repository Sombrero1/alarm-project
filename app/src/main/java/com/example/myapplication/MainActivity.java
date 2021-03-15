package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.myapplication.Adapter.Adapter_alarm_clock;
import com.example.myapplication.Adapter.Alarm_clock_item;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    ListView clocks;
    ImageButton btn_add;
    Database database;
    Adapter_alarm_clock adapter_alarm_clock;
    ArrayList<Alarm_clock_item> array;
    final int MENU_DELETE = 1;
    final int MENU_CANCEL = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = new Database(this);
        array = database.getClocks();
        adapter_alarm_clock = new Adapter_alarm_clock(this,array);

        clocks = findViewById(R.id.clocks);
        clocks.setOnItemClickListener(this);
        clocks.setAdapter(adapter_alarm_clock);
        clocks.setOnItemClickListener(this);


        btn_add = findViewById(R.id.btn_save);
        btn_add.setOnClickListener(this);

        registerForContextMenu(clocks);

    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btn_save){
            Toast.makeText(this,"Уд",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, AlarmActivity.class);
            startActivityForResult(intent,1);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) return;
        switch (requestCode){
            case 1:
                Alarm_clock_item alarm_clock_item = (Alarm_clock_item) data.getSerializableExtra("clock");
                Log.d("my_debug","message: "+alarm_clock_item.getTime());
                database.insertClock(alarm_clock_item);
                arrayUpdate();
                adapter_alarm_clock.notifyDataSetChanged();
                break;
            case 2:
                Alarm_clock_item alarm_clock_item_edit = (Alarm_clock_item) data.getSerializableExtra("clock");
                database.updateClock(alarm_clock_item_edit);
                arrayUpdate();
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
                Log.d("my_debug","delete");
                AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                Toast.makeText(this,"Удалён" + array.get(acmi.position).getId(),Toast.LENGTH_SHORT).show();

               // database.deleteClock();
                database.deleteClock(array.get(acmi.position).getId());
                arrayUpdate();
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
        intent.putExtra("clock_edit",array.get(position));
        startActivityForResult(intent,2);
    }

    public void arrayUpdate(){
        ArrayList<Alarm_clock_item> items = database.getClocks();
        array.clear();
        for (int i = 0; i < items.size() ; i++) {
            array.add(items.get(i));
        }
    }
}