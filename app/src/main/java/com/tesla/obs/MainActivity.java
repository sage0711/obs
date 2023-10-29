package com.tesla.obs;

import com.tesla.obs.StayService;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toast.makeText(getApplicationContext(), "ABC", Toast.LENGTH_SHORT).show();
        startService(new Intent(this, StayService.class));
    }
}