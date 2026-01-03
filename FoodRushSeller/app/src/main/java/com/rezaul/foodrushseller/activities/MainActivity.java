package com.rezaul.foodrushseller.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.rezaul.foodrushseller.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // No UI logic here — just redirect
        startActivity(new Intent(this, SplashActivity.class));
        finish();
    }
}
