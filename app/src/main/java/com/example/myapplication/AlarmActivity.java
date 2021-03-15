package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.myapplication.Adapter.Alarm_clock_item;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Calendar;

public class AlarmActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {
    ImageButton btn_cancel;
    ImageButton btn_save;
    View time_form;
    TextView time;
    Calendar dateAndTime = Calendar.getInstance();
    int id = 0;


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
        if (alarm_clock_item != null) {
            time.setText(toShortTime(alarm_clock_item.getTimeMil()));
            dateAndTime.setTimeInMillis(alarm_clock_item.getTimeMil());
            id = alarm_clock_item.getId();
            Log.d("my_debug", "id:" + id);
        }
        Log.d("http", "id:" + id);

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
                new HttpRequestAlarm().execute(geo);
                Intent intent = getIntent().putExtra("clock", new Alarm_clock_item(id, (String) time.getText(), dateAndTime.getTimeInMillis(), "days", false));
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
        Alarm_clock_item alarm_clock_item = (Alarm_clock_item) getIntent().getSerializableExtra("clock_edit");
        int hours, mins;
        if (alarm_clock_item == null) {
            hours = dateAndTime.get(Calendar.HOUR_OF_DAY);
            mins = dateAndTime.get(Calendar.MINUTE);
        } else {
            hours = dateAndTime.get(Calendar.HOUR_OF_DAY);
            mins = dateAndTime.get(Calendar.MINUTE);
            Log.d("my_debug", "alarm!=null,hours:" + hours);
        }

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

    private GoogleMap mMap;
    private  Marker markerA;
    private  Marker markerB;
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
//            Log.d("gps",""+"а хрен тебе");
            //return;
        }
        Location lastLoc = locMan.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        double lat = lastLoc.getLatitude();
        double lng = lastLoc.getLongitude();
        Log.d("gps",""+lat + " "+lng);

        LatLng sydney = new LatLng(lat, lng);
        markerA = mMap.addMarker(new MarkerOptions().position(sydney).title("A").draggable(true));
        markerB = mMap.addMarker(new MarkerOptions().position(new LatLng(lat+1, lng+1)).title("B").draggable(true).icon(BitmapDescriptorFactory.fromResource( R.drawable.blue_marker)));

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
            }

            @Override
            public void onMarkerDrag(Marker marker) {
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                if(marker.getTitle().equals("A")){
                    markerA.setPosition(marker.getPosition());
                }
                else markerB.setPosition(marker.getPosition());
            }
        });
    }
        
    }
