package com.android.camera.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.android.camera.beauty.activity.BeautyFilterActivity;
import com.android.camera.beauty.activity.BeautyImageActivity;
import com.android.camera2.R;


public class MainActivity extends AppCompatActivity {

    private final String[] PERMISSIONS = new String[]{Manifest.permission.CAMERA};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_cam);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(PERMISSIONS, 54321);
        }
        initView();
    }

    private void initView() {
        Button btnFace = findViewById(R.id.btn_face);
        btnFace.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, FaceBeautyActivity.class)));

        Button btnFilter = findViewById(R.id.btn_filter);
        btnFilter.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, BeautyFilterActivity.class)));

        Button btnPhoto = findViewById(R.id.btn_photo);
        btnPhoto.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, BeautyImageActivity.class)));
    }

}
