package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.myapplication.Adapter.Alarm_clock_item;
import com.example.myapplication.models.Alarm;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

public class AlarmActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {
    private ImageButton btn_cancel;
    private ImageButton btn_save;
    private View time_form;
    private TextView time;
    private Calendar dateAndTime = Calendar.getInstance();
    private Double []geo;
    private EditText fromLoc;
    private EditText toLoc;
    private int id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_save = findViewById(R.id.btn_save);
        time_form = findViewById(R.id.time_form);
        time = findViewById(R.id.time_2);
        fromLoc = findViewById(R.id.fromLoc);
        toLoc = findViewById(R.id.toLoc);

         TextView.OnEditorActionListener listener = new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                                actionId == EditorInfo.IME_ACTION_DONE ||
                        event != null &&
                                event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (event == null || !event.isShiftPressed()) {
                        // the user is done typing.
                        try {
                            if (v.getId() == R.id.fromLoc) markerA.setPosition(getLatLng(((EditText)v).getText().toString()));
                            if (v.getId() == R.id.toLoc) markerB.setPosition(getLatLng(((EditText)v).getText().toString()));
                        } catch (IOException e) {
                            Toast.makeText(AlarmActivity.this,"неправильный адрес", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                        return false; // consume.
                    }
                }
                return false; // pass on to other listeners.
            }
        };
        fromLoc.setOnEditorActionListener(listener);
        toLoc.setOnEditorActionListener(listener);



        btn_cancel.setOnClickListener(this);
        btn_save.setOnClickListener(this);
        time_form.setOnClickListener(this);

        time.setText(toShortTime(dateAndTime.getTimeInMillis())); //Установить настоящее время в случае нового будильника

        Alarm_clock_item alarm_clock_item = (Alarm_clock_item) getIntent().getSerializableExtra("clock_edit");

        if (alarm_clock_item != null) {
            //загрузка данных
            time.setText(toShortTime(alarm_clock_item.getTimeMil()));
            dateAndTime.setTimeInMillis(alarm_clock_item.getTimeMil());
            geo = alarm_clock_item.getGeo();
            checkBoxSet(alarm_clock_item.getCheckboxs());
            id = alarm_clock_item.getId();
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                finish();
                break;
            case R.id.btn_save:
                Double[] geo = {markerA.getPosition().latitude,markerA.getPosition().longitude, markerB.getPosition().latitude,markerB.getPosition().longitude};
                Alarm_clock_item alarm_clock_item = new Alarm_clock_item(
                        (String) time.getText(),
                        dateAndTime.getTimeInMillis(),
                        geo,
                        checkBoxSave(),
                        true);
                alarm_clock_item.setId(id);

                Intent intent = getIntent().putExtra(
                        "clock", alarm_clock_item
                        );

                getIntent().putExtra("position",
                        getIntent().getIntExtra("position",0)
                );
                setResult(RESULT_OK, intent);

                finish();
                break;

            case R.id.time_form:
                setTime();
                Log.d("my_debug", "btn ok click");
                break;
        }

    }

    public void setTime() {
        int hours, mins;
        hours = dateAndTime.get(Calendar.HOUR_OF_DAY);
        mins = dateAndTime.get(Calendar.MINUTE);

        new TimePickerDialog(this, R.style.TimePickerTheme, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                dateAndTime.set(Calendar.MINUTE, minute);
                time.setText(toShortTime(dateAndTime.getTimeInMillis()));
                Toast.makeText(AlarmActivity.this, "Будильник установлен на: " + hourOfDay + ":" + minute, Toast.LENGTH_LONG).show();
            }
        }, hours, mins, true).show();
    }

    public String toShortTime(long timeInMillis) {
        return DateUtils.formatDateTime(AlarmActivity.this, timeInMillis, DateUtils.FORMAT_SHOW_TIME);
    }

    public boolean[] checkBoxSave(){
        boolean[] states = new boolean[7];
        for (int i = 0; i <5 ; i++) {
            CheckBox checkBox = ((CheckBox)((TableRow)findViewById(R.id.tablerow1)).getChildAt(i));
            states[i] = checkBox.isChecked();
    }
        for (int i = 0; i <2 ; i++) {
            CheckBox checkBox = ((CheckBox)((TableRow)findViewById(R.id.tablerow2)).getChildAt(i));
            states[5+i] = checkBox.isChecked();
        }
        return states;
    }
    public void checkBoxSet(boolean states[]){
        for (int i = 0; i <5 ; i++) {
            CheckBox checkBox = ((CheckBox)((TableRow)findViewById(R.id.tablerow1)).getChildAt(i));
            checkBox.setChecked(states[i]);
        }
        for (int i = 0; i <2 ; i++) {
            CheckBox checkBox = ((CheckBox)((TableRow)findViewById(R.id.tablerow2)).getChildAt(i));
            checkBox.setChecked(states[5+i]);
        }
    }
    public boolean[] getCheckBoxs(){
        boolean[]days = new boolean[7];
        for (int i = 0; i <5 ; i++) {
            CheckBox checkBox = ((CheckBox)((TableRow)findViewById(R.id.tablerow1)).getChildAt(i));
            days[i] = checkBox.isChecked();
        }
        for (int i = 0; i <2 ; i++) {
            CheckBox checkBox = ((CheckBox)((TableRow)findViewById(R.id.tablerow2)).getChildAt(i));
            days[5+i] = checkBox.isChecked();
        }
        return days;
    }
    private LatLng getLatLng(String address) throws IOException {
        Geocoder geocoder = new Geocoder(this);
        List<Address> addresses;
        addresses = geocoder.getFromLocationName(address, 1);
        if(addresses.size() > 0) {
            double latitude= addresses.get(0).getLatitude();
            double longitude= addresses.get(0).getLongitude();
            return new LatLng(latitude,longitude);
        }
        throw new IOException();
    }

    Marker markerA;
    Marker markerB;
    @Override
    public void onMapReady(GoogleMap googleMap) {
        GoogleMap mMap = googleMap;
//        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        // Add a marker in Sydney and move the camera
        LocationManager locMan = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PackageManager.PERMISSION_GRANTED);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(this,"Нет доступа",Toast.LENGTH_SHORT);
            finish();
            return;
        }
        if(geo == null) {
            Location lastLoc = locMan.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            double lat = lastLoc.getLatitude();//МОЖЕТ КРАШНУТЬСЯ, прописать флаг
            double lng = lastLoc.getLongitude();
            LatLng gps_user = new LatLng(lat, lng);
            markerA = mMap.addMarker(new MarkerOptions()
                    .position(gps_user)
                    .title("Вы")
                    .draggable(true));

            markerB = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(lat + 0.005, lng + 0.005))
                    .title("Конечный пункт")
                    .draggable(true)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_marker)));
        }
        else{
            markerA = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(geo[0],geo[1]))
                    .title("Вы")
                    .draggable(true));

            markerB = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(geo[2],geo[3]))
                    .title("Конечный пункт")
                    .draggable(true)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_marker)));
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLng(markerA.getPosition()));


        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
            }

            @Override
            public void onMarkerDrag(Marker marker) {
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                if(marker.getTitle().equals("Вы")){
                    markerA.setPosition(marker.getPosition());
                }
                else markerB.setPosition(marker.getPosition());
            }
        });
    }
        
    }
