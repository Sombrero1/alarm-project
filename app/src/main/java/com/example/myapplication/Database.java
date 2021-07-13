package com.example.myapplication;
//
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;
//import android.util.Log;
//
//import androidx.annotation.Nullable;
//
//import com.example.myapplication.Adapter.Alarm_clock_item;
//
//import java.util.ArrayList;
//
//public class Database extends SQLiteOpenHelper {
//    final String table = "mytable";
//
//    public Database(@Nullable Context context) {
//        super(context, "myDB", null, 2);
//    }
//
//    @Override
//    public void onCreate(SQLiteDatabase db) {
//        db.execSQL("create table mytable (id integer primary key autoincrement, time text,timeMil long,latgeoA real, lgtgeoA real, latgeoB real, lgtgeoB real, days text, switch integer)");
//    }
//
//    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//
//    }
//
//    public ArrayList<Alarm_clock_item> getClocks(){
//       SQLiteDatabase db = getWritableDatabase();
//       ArrayList<Alarm_clock_item> items = new ArrayList<>();
//      Cursor c = db.query(table,null,null,null,null,null,null);
//      if(c.moveToFirst()){
//          int idColumn = c.getColumnIndex("id");
//          int nameColumn = c.getColumnIndex("time");
//          int timeMilColumn = c.getColumnIndex("timeMil");
//          int daysColumn = c.getColumnIndex("days");
//          int switchColumn = c.getColumnIndex("switch");//1,0
//          int latgeoA = c.getColumnIndex("latgeoA");
//          int lgtgeoA = c.getColumnIndex("lgtgeoA");
//          int latgeoB = c.getColumnIndex("latgeoB");
//          int lgtgeoB = c.getColumnIndex("lgtgeoB");
//          do{
//              Log.d("my_debug",""+c.getLong(timeMilColumn));
//              Double [] geo = {c.getDouble(latgeoA), c.getDouble(lgtgeoA), c.getDouble(latgeoB),c.getDouble(lgtgeoB)};
//              items.add(new Alarm_clock_item(c.getInt(idColumn),c.getString(nameColumn),c.getLong(timeMilColumn),geo, c.getString(daysColumn), c.getInt(switchColumn)!=0));
//          }while (c.moveToNext());
//
//      }
//      db.close();
//
//      return items;
//    }
//
//    public void insertClock(Alarm_clock_item alarm_clock_item){
//        SQLiteDatabase db = getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put("time",alarm_clock_item.getTime());
//        contentValues.put("timeMil",alarm_clock_item.getTimeMil());
//        contentValues.put("days",alarm_clock_item.getDays());
//        contentValues.put("switch",alarm_clock_item.isCheckbox());
//        contentValues.put("latgeoA",alarm_clock_item.getGeo()[0]);
//        contentValues.put("lgtgeoA",alarm_clock_item.getGeo()[1]);
//        contentValues.put("latgeoB",alarm_clock_item.getGeo()[2]);
//        contentValues.put("lgtgeoB",alarm_clock_item.getGeo()[3]);
//        db.insert(table,null,contentValues);
//        db.close();
//    }
//    public void updateClock(Alarm_clock_item alarm_clock_item){
//        SQLiteDatabase db = getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put("time",alarm_clock_item.getTime());
//        contentValues.put("timeMil",alarm_clock_item.getTimeMil());
//        contentValues.put("days",alarm_clock_item.getDays());
//        contentValues.put("switch",alarm_clock_item.isCheckbox());
//        contentValues.put("latgeoA",alarm_clock_item.getGeo()[0]);
//        contentValues.put("lgtgeoA",alarm_clock_item.getGeo()[1]);
//        contentValues.put("latgeoB",alarm_clock_item.getGeo()[2]);
//        contentValues.put("lgtgeoB",alarm_clock_item.getGeo()[3]);
//        Log.d("my_debug","sql update:" + String.valueOf(alarm_clock_item.getId()));
//        int upd = db.update(table,contentValues,"id = ?",new String[]{""+alarm_clock_item.getId()});
//        Log.d("my_debug","upd:" + upd);
//        db.close();
//    }
//
//    public void deleteClock(int id){
//        SQLiteDatabase db = getWritableDatabase();
//        db.delete(table,"id  = "+id,null);
//        db.close();
//    }
//
//}


import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;


import com.example.myapplication.Adapter.Alarm_clock_item;
import com.example.myapplication.models.Alarm;
import com.google.gson.Gson;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Database{
    SharedPreferences.Editor editor;
    SharedPreferences sharedPreferences;
    ArrayList<Alarm_clock_item> items;

    public Database(SharedPreferences sharedPreferences){
        this.editor = sharedPreferences.edit();
        this.sharedPreferences = sharedPreferences;
        items = new ArrayList<Alarm_clock_item>();

        Gson gson = new Gson();
        if (gson.fromJson(sharedPreferences.getString("alarms",""),ArrayList.class)!= null){
            items = new ArrayList<>(
                    Arrays.asList(
                            gson.fromJson(sharedPreferences.getString("alarms",""),
                                    Alarm_clock_item[].class)
                    )
            );

    }};


    static public Alarm_clock_item toAlarmItemFromAlarm(Alarm alarm){
        return new Alarm_clock_item(alarm.getTime(),0,alarm.getGeo(), alarm.getDays(), alarm.isSelected());

    }

    public ArrayList<Alarm_clock_item> getClocks(){
        return items;
    }

    public void insertClock(Alarm_clock_item alarm_clock_item){
            new HttpRequestAlarmPost().execute(alarm_clock_item);
            items.add(alarm_clock_item);
    }
    public void insertClockWithCheck(Alarm_clock_item alarm_clock_item){
        for (int i = 0; i < items.size() ; i++) {
            if(items.get(i).getId() == alarm_clock_item.getId()) {
                updateClockLocal(i, alarm_clock_item);
                return;
            }
        }
        items.add(alarm_clock_item);
    }

    public void updateClock(int id,Alarm_clock_item alarm_clock_item){
        items.set(id, alarm_clock_item);
       new HttpRequestAlarmUpdate().execute(alarm_clock_item);
    }
    public void updateClockLocal(int i, Alarm_clock_item alarm_clock_item){
            items.set(i,alarm_clock_item);
    }

    public void deleteClock(int id){
        new HttpRequestAlarmDelete().execute(items.get(id).getId());
        items.remove(id);
    }

    public void save(){
        Gson gson = new Gson();
        String json = gson.toJson(items);
        editor.putString("alarms", json);
        editor.apply();
    }

    private static String HOST = "http://62.77.153.231:8086";

    static public class HttpRequestAlarmGet extends AsyncTask<Database, Void, Integer> {
        @Override
        protected Integer doInBackground(Database... database) {
            try {
                final String url = String.format("%s/clocks",HOST);
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                Alarm_clock_item[]  alarms = restTemplate.getForObject(url, Alarm_clock_item[].class);
                Log.d("http get",url);
                for (int i = 0; i < alarms.length; i++) {
                    Log.d("http get3",String.valueOf(alarms[i].getTime()));
                    database[0].insertClockWithCheck(alarms[i]);
                }


            } catch (Exception e) {
                //не отправился
            }

            return 1;
        }

    }


    static public class HttpRequestAlarmPost extends AsyncTask<Alarm_clock_item,Void, Void> {
        @Override
        protected Void doInBackground(Alarm_clock_item... alarm) {
            try {
                final String url = String.format("%s/clocks",HOST);
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                int id = restTemplate.postForObject(url,alarm[0],Integer.class);
                alarm[0].setId(id);
                Log.d("http post",url);

            } catch (Exception e) {
                //не отправился
            }
            return null;
        }
    }

    static public class HttpRequestAlarmUpdate extends AsyncTask<Alarm_clock_item,Void, Void> {
        @Override
        protected Void doInBackground(Alarm_clock_item... alarms) {
            try {
                final String url = String.format("%s/clocks/",HOST) + alarms[0].getId();
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                restTemplate.put(url,alarms[0]);
                Log.d("http update",url);
            } catch (Exception e) {
                //не отправился
            }
            return null;
        }
    }


    static public class HttpRequestAlarmDelete extends AsyncTask<Integer,Void, Void> {
        @Override
        protected Void doInBackground(Integer... id) {
            try {
                final String url = String.format("%s/clocks/",HOST) + id[0];
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.delete(url);
                Log.d("http delete",url);
            } catch (Exception e) {
                //не отправился
            }
            return null;
        }
    }



}