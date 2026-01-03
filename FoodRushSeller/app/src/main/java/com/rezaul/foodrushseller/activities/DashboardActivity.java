package com.rezaul.foodrushseller.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
    private static final String TAG = "DashboardActivity";
    private static final int ADD_RESTAURANT_REQUEST = 1;

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

        recycler = findViewById(R.id.recyclerRestaurants);
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

        if (requestCode == ADD_RESTAURANT_REQUEST && resultCode == RESULT_OK) {
            Log.d(TAG, "Restaurant added successfully, reloading...");
            loadRestaurants();
        }
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

                if (response.isSuccessful() && response.body() != null) {
                    List<Restaurant> restaurants = response.body();
                    Log.d(TAG, "Restaurants Count: " + restaurants.size());

                    if (!restaurants.isEmpty()) {
                        Log.d(TAG, "SUCCESS: Loaded " + restaurants.size() + " restaurants");
                        recycler.setAdapter(new RestaurantAdapter(
                                DashboardActivity.this,
                                restaurants,
                                restaurant -> {
                                    Log.d(TAG, "Restaurant clicked: " + restaurant.getName());
                                    Intent i = new Intent(
                                            DashboardActivity.this,
                                            RestaurantDetailsActivity.class);
                                    i.putExtra("restaurant_id", restaurant.getId());
                                    i.putExtra("restaurant_name", restaurant.getName());
                                    startActivity(i);
                                }));
                    } else {
                        Log.d(TAG, "No restaurants found");
                        Toast.makeText(DashboardActivity.this,
                                "No restaurants yet. Add one!",
                                Toast.LENGTH_SHORT).show();
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

                Toast.makeText(DashboardActivity.this,
                        "Network Error: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}