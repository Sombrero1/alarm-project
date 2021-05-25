package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class SettingsActivity extends AppCompatActivity  implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ImageButton imageButton = findViewById(R.id.save_set);
        ImageButton imageButton2 = findViewById(R.id.cancel);
        imageButton.setOnClickListener(this);
        imageButton2.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if( v.getId() == R.id.cancel) finish();

        if(v.getId() == R.id.save_set){
            EditText ssid = findViewById(R.id.ssid);
            EditText password =findViewById(R.id.password);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        //Replace below IP with the IP of that device in which server socket open.
                        //If you change port then change the port number in the server side code also.
                        Socket s = new Socket("192.168.5.1", 1001);
                        Thread.sleep(1000);
                        OutputStream out = s.getOutputStream();

                        PrintWriter output = new PrintWriter(out);
                        String send = String.format("{\"ssid\": \"%s\", \"password\": \"%s\"}",ssid.getText().toString(),password.getText().toString());
                        System.out.println(send);
                        output.println(send);
                        Thread.sleep(1000);
                        output.flush();
                        output.close();
                        out.close();
                        s.close();
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            finish();
        }

    }
}