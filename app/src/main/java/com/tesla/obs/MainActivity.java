package com.tesla.obs;

import androidx.appcompat.app.AppCompatActivity;
import com.tesla.obs.StayService;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toast.makeText(getApplicationContext(), "Here service goes", Toast.LENGTH_LONG).show();
        //        startService(new Intent(String.valueOf(StayService.class)));
    }
}