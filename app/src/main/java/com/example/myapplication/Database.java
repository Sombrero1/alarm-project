package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.myapplication.Adapter.Alarm_clock_item;

import java.util.ArrayList;

public class Database extends SQLiteOpenHelper {
    final String table = "mytable";

    public Database(@Nullable Context context) {
        super(context, "myDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table mytable (id integer primary key autoincrement, time text,timeMil long, days text, switch integer)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public ArrayList<Alarm_clock_item> getClocks(){
       SQLiteDatabase db = getWritableDatabase();
       ArrayList<Alarm_clock_item> items = new ArrayList<>();
      Cursor c = db.query(table,null,null,null,null,null,null);
      if(c.moveToFirst()){
          int idColumn = c.getColumnIndex("id");
          int nameColumn = c.getColumnIndex("time");
          int timeMilColumn = c.getColumnIndex("timeMil");
          int daysColumn = c.getColumnIndex("days");
          int switchColumn = c.getColumnIndex("switch");//1,0
          do{
              Log.d("my_debug",""+c.getLong(timeMilColumn));
              items.add(new Alarm_clock_item(c.getInt(idColumn),c.getString(nameColumn),c.getLong(timeMilColumn), c.getString(daysColumn), c.getInt(switchColumn)!=0));
          }while (c.moveToNext());

      }
      db.close();

      return items;
    }

    public void insertClock(Alarm_clock_item alarm_clock_item){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("time",alarm_clock_item.getTime());
        contentValues.put("timeMil",alarm_clock_item.getTimeMil());
        contentValues.put("days",alarm_clock_item.getDays());
        contentValues.put("switch",alarm_clock_item.isCheckbox());
        db.insert(table,null,contentValues);
        db.close();
    }
    public void updateClock(Alarm_clock_item alarm_clock_item){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("time",alarm_clock_item.getTime());
        contentValues.put("timeMil",alarm_clock_item.getTimeMil());
        contentValues.put("days",alarm_clock_item.getDays());
        contentValues.put("switch",alarm_clock_item.isCheckbox());
        Log.d("my_debug","sql update:" + String.valueOf(alarm_clock_item.getId()));
        int upd = db.update(table,contentValues,"id = ?",new String[]{""+alarm_clock_item.getId()});
        Log.d("my_debug","upd:" + upd);
        db.close();
    }

    public void deleteClock(int id){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(table,"id  = "+id,null);
        db.close();

    }

}
