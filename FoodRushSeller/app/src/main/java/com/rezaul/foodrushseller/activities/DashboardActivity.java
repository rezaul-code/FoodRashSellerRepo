package com.rezaul.foodrushseller.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rezaul.foodrushseller.R;
import com.rezaul.foodrushseller.adapters.RestaurantAdapter;
import com.rezaul.foodrushseller.models.Restaurant;
import com.rezaul.foodrushseller.network.ApiClient;
import com.rezaul.foodrushseller.network.ApiService;
import com.rezaul.foodrushseller.utils.PreferenceManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardActivity extends AppCompatActivity {

    private RecyclerView recycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        recycler = findViewById(R.id.recyclerRestaurants);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton fab = findViewById(R.id.fabAddRestaurant);
        fab.setOnClickListener(v ->
                startActivity(new Intent(this, AddRestaurantActivity.class)));

        loadRestaurants();
    }

    private void loadRestaurants() {
        ApiService api = ApiClient
                .getClient(new PreferenceManager(this))
                .create(ApiService.class);

        api.getRestaurants().enqueue(new Callback<List<Restaurant>>() {
            @Override
            public void onResponse(Call<List<Restaurant>> call,
                                   Response<List<Restaurant>> response) {
                if (response.isSuccessful()) {
                    recycler.setAdapter(new RestaurantAdapter(
                            DashboardActivity.this,
                            response.body(),
                            restaurant -> {
                                Intent i = new Intent(
                                        DashboardActivity.this,
                                        RestaurantDetailsActivity.class);
                                i.putExtra("restaurant_id", restaurant.getId());
                                startActivity(i);
                            }));
                }
            }

            @Override
            public void onFailure(Call<List<Restaurant>> call, Throwable t) { }
        });
    }
}
