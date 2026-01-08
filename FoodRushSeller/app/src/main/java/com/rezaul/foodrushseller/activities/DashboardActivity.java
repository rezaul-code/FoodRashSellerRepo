package com.rezaul.foodrushseller.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
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
    private ProgressBar progressBar;
    private TextView tvEmptyMessage;
    private static final String TAG = "DashboardActivity";
    private static final int ADD_RESTAURANT_REQUEST = 1;
    private static final int EDIT_RESTAURANT_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Log.d(TAG, "========== ACTIVITY CREATED ==========");

        // Setup Toolbar with Logout
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("My Restaurants");
            toolbar.inflateMenu(R.menu.menu_dashboard);
            toolbar.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.action_logout) {
                    logout();
                    return true;
                }
                return false;
            });
            Log.d(TAG, "Toolbar setup completed");
        }

        // Initialize views
        recycler = findViewById(R.id.recyclerRestaurants);
        progressBar = findViewById(R.id.progressBar);
        tvEmptyMessage = findViewById(R.id.tvEmptyMessage);

        if (recycler != null) {
            recycler.setLayoutManager(new LinearLayoutManager(this));
            Log.d(TAG, "RecyclerView setup completed");
        }

        FloatingActionButton fab = findViewById(R.id.fabAddRestaurant);
        if (fab != null) {
            fab.setOnClickListener(v -> {
                Log.d(TAG, "FAB clicked - Starting AddRestaurantActivity");
                startActivityForResult(
                        new Intent(this, AddRestaurantActivity.class),
                        ADD_RESTAURANT_REQUEST
                );
            });
            Log.d(TAG, "FAB setup completed");
        }

        loadRestaurants();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult - Request: " + requestCode + ", Result: " + resultCode);

        if (resultCode == RESULT_OK) {
            if (requestCode == ADD_RESTAURANT_REQUEST) {
                Log.d(TAG, "Restaurant added successfully, reloading...");
                // Add small delay to ensure server has updated
                loadRestaurantsWithDelay(500);
            } else if (requestCode == EDIT_RESTAURANT_REQUEST) {
                Log.d(TAG, "Restaurant edited successfully, reloading...");
                // Add delay to ensure server has updated
                loadRestaurantsWithDelay(500);
            }
        }
    }

    private void loadRestaurantsWithDelay(long delayMs) {
        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(
                this::loadRestaurants,
                delayMs
        );
    }

    private void logout() {
        Log.d(TAG, "Logging out...");

        PreferenceManager pref = new PreferenceManager(this);
        pref.logout();

        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void loadRestaurants() {
        Log.d(TAG, "========== LOADING RESTAURANTS ==========");

        // Show progress bar, hide others
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
        if (tvEmptyMessage != null) {
            tvEmptyMessage.setVisibility(View.GONE);
        }
        if (recycler != null) {
            recycler.setVisibility(View.GONE);
        }

        PreferenceManager pref = new PreferenceManager(this);
        ApiService api = ApiClient.getClient(pref).create(ApiService.class);

        api.getRestaurants().enqueue(new Callback<List<Restaurant>>() {
            @Override
            public void onResponse(Call<List<Restaurant>> call,
                                   Response<List<Restaurant>> response) {
                Log.d(TAG, "========== API RESPONSE RECEIVED ==========");
                Log.d(TAG, "Status Code: " + response.code());
                Log.d(TAG, "Message: " + response.message());
                Log.d(TAG, "Is Successful: " + response.isSuccessful());

                // Hide progress bar
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }

                if (response.isSuccessful() && response.body() != null) {
                    List<Restaurant> restaurants = response.body();
                    Log.d(TAG, "Restaurants Count: " + restaurants.size());

                    if (!restaurants.isEmpty()) {
                        Log.d(TAG, "SUCCESS: Loaded " + restaurants.size() + " restaurants");

                        // Show RecyclerView, hide empty message
                        recycler.setVisibility(View.VISIBLE);
                        tvEmptyMessage.setVisibility(View.GONE);

                        // Log restaurant details with banner URLs
                        for (Restaurant restaurant : restaurants) {
                            Log.d(TAG, "Restaurant: " + restaurant.getName() +
                                    " | ID: " + restaurant.getId() +
                                    " | Banner URL: " + restaurant.getBannerImageUrl());
                        }

                        // Setup adapter with BOTH click listeners
                        recycler.setAdapter(new RestaurantAdapter(
                                DashboardActivity.this,
                                restaurants,
                                new RestaurantAdapter.OnRestaurantClickListener() {
                                    @Override
                                    public void onClick(Restaurant restaurant) {
                                        // Open restaurant details (menu items)
                                        Log.d(TAG, "Restaurant clicked: " + restaurant.getName());
                                        Intent i = new Intent(
                                                DashboardActivity.this,
                                                RestaurantDetailsActivity.class);
                                        i.putExtra("restaurant_id", restaurant.getId());
                                        i.putExtra("restaurant_name", restaurant.getName());
                                        startActivity(i);
                                    }

                                    @Override
                                    public void onEditClick(Restaurant restaurant) {
                                        // Open edit restaurant activity
                                        Log.d(TAG, "Edit clicked for: " + restaurant.getName());
                                        Log.d(TAG, "Current banner URL: " + restaurant.getBannerImageUrl());

                                        Intent intent = new Intent(
                                                DashboardActivity.this,
                                                EditRestaurantActivity.class);
                                        intent.putExtra("restaurant_id", restaurant.getId());
                                        intent.putExtra("restaurant_name", restaurant.getName());
                                        intent.putExtra("banner_url", restaurant.getBannerImageUrl());
                                        startActivityForResult(intent, EDIT_RESTAURANT_REQUEST);
                                    }
                                }));
                    } else {
                        Log.d(TAG, "No restaurants found");

                        // Hide RecyclerView, show empty message
                        recycler.setVisibility(View.GONE);
                        tvEmptyMessage.setVisibility(View.VISIBLE);
                        tvEmptyMessage.setText("No restaurants yet.\nTap + to add your first restaurant!");

                        recycler.setAdapter(null);
                    }
                } else {
                    // Handle error response
                    Log.e(TAG, "========== ERROR RESPONSE ==========");
                    Log.e(TAG, "Error Code: " + response.code());

                    String errorMsg = "Failed to load restaurants (Error: " + response.code() + ")";
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "Error Body: " + errorBody);
                            errorMsg = errorBody;
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading error body", e);
                    }

                    // Show error in empty message
                    recycler.setVisibility(View.GONE);
                    tvEmptyMessage.setVisibility(View.VISIBLE);
                    tvEmptyMessage.setText("Error: " + errorMsg);

                    Toast.makeText(DashboardActivity.this,
                            errorMsg,
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Restaurant>> call, Throwable t) {
                Log.e(TAG, "========== API CALL FAILED ==========");
                Log.e(TAG, "Error Message: " + t.getMessage());
                Log.e(TAG, "Error Type: " + t.getClass().getName());
                t.printStackTrace();

                // Hide progress bar
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }

                String errorMessage = t.getMessage() != null ? t.getMessage() : "Unknown error";

                // Show error in empty message
                recycler.setVisibility(View.GONE);
                tvEmptyMessage.setVisibility(View.VISIBLE);
                tvEmptyMessage.setText("Network Error:\n" + errorMessage);

                Toast.makeText(DashboardActivity.this,
                        "Network Error: " + errorMessage,
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}