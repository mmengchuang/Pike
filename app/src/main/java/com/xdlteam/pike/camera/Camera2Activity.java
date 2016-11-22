package com.xdlteam.pike.camera;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.xdlteam.pike.R;

public class Camera2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, new CaptureDemoFragment()).commit();
        }
    }
}