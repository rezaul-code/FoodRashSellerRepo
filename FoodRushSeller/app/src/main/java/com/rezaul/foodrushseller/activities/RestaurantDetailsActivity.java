package com.rezaul.foodrushseller.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rezaul.foodrushseller.R;
import com.rezaul.foodrushseller.adapters.MenuItemAdapter;

public class RestaurantDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_details);

        RecyclerView recycler = findViewById(R.id.recyclerMenu);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton fab = findViewById(R.id.fabAddMenu);
        fab.setOnClickListener(v ->
                startActivity(new Intent(this, AddMenuItemActivity.class)));
    }
}
