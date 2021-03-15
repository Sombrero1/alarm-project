package com.example.myapplication;

import android.os.AsyncTask;
import android.util.Log;

import com.example.myapplication.models.Alarm;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class HttpRequestAlarm extends AsyncTask<Double,Void, Alarm> {
    @Override
    protected Alarm doInBackground(Double... voids) {
        try {
            final String url = "http://192.168.31.104:8888/put_clock";
            Log.d("http_alarm","1");
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            Alarm greeting = restTemplate.postForObject(url,new Alarm("name",21321,voids),Alarm.class);
            Log.d("http_alarm","name:" + greeting.getName());
            return greeting;
        } catch (Exception e) {
            Log.d("http_alarm","error");
            Log.e("MainActivity", e.getMessage(), e);
        }

        return null;
    }
}
