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
import com.rezaul.foodrushseller.adapters.MenuItemAdapter;
import com.rezaul.foodrushseller.models.MenuItem;
import com.rezaul.foodrushseller.models.Restaurant;
import com.rezaul.foodrushseller.network.ApiClient;
import com.rezaul.foodrushseller.network.ApiService;
import com.rezaul.foodrushseller.utils.Constants;
import com.rezaul.foodrushseller.utils.PreferenceManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantDetailsActivity extends AppCompatActivity {

    private RecyclerView recyclerMenu;
    private ProgressBar progressBar;
    private TextView tvEmptyMessage;
    private Long restaurantId;
    private String restaurantName;
    private static final String TAG = "RestaurantDetailsActivity";
    private static final int ADD_MENU_ITEM_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_details);

        Log.d(TAG, "========== ACTIVITY CREATED ==========");

        // Get restaurant ID from intent
        restaurantId = getIntent().getLongExtra("restaurant_id", -1L);
        restaurantName = getIntent().getStringExtra("restaurant_name");

        Log.d(TAG, "Restaurant ID: " + restaurantId);
        Log.d(TAG, "Restaurant Name: " + restaurantName);

        if (restaurantId == -1L) {
            Toast.makeText(this, "Invalid restaurant", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Setup toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle(restaurantName != null ? restaurantName : "Menu Items");
            toolbar.setNavigationOnClickListener(v -> finish());
            Log.d(TAG, "Toolbar setup completed");
        }

        // Setup RecyclerView
        recyclerMenu = findViewById(R.id.recyclerMenu);
        if (recyclerMenu != null) {
            recyclerMenu.setLayoutManager(new LinearLayoutManager(this));
            Log.d(TAG, "RecyclerView setup completed");
        }

        // Setup Progress Bar and Empty Message
        progressBar = findViewById(R.id.progressBar);
        tvEmptyMessage = findViewById(R.id.tvEmptyMessage);

        // Setup FAB
        FloatingActionButton fab = findViewById(R.id.fabAddMenu);
        if (fab != null) {
            fab.setOnClickListener(v -> {
                Intent intent = new Intent(this, AddMenuItemActivity.class);
                intent.putExtra(Constants.EXTRA_RESTAURANT_ID, restaurantId);
                intent.putExtra(Constants.EXTRA_RESTAURANT_NAME, restaurantName);
                startActivityForResult(intent, ADD_MENU_ITEM_REQUEST);
            });
        }

        // Load menu items
        loadMenuItems();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult - Request: " + requestCode + ", Result: " + resultCode);

        if (requestCode == ADD_MENU_ITEM_REQUEST && resultCode == RESULT_OK) {
            Log.d(TAG, "Menu item added successfully, reloading...");
            loadMenuItems();
        }
    }

    private void loadMenuItems() {
        Log.d(TAG, "========== LOADING MENU ITEMS ==========");
        Log.d(TAG, "Restaurant ID: " + restaurantId);

        PreferenceManager pref = new PreferenceManager(this);
        String role = pref.getRole();
        String token = pref.getToken();

        Log.d(TAG, "Current User Role: " + role);
        Log.d(TAG, "Token exists: " + (token != null && !token.isEmpty()));

        // Log role details for debugging
        if (role != null) {
            Log.d(TAG, "Role length: " + role.length());
            Log.d(TAG, "Role uppercase: " + role.toUpperCase());
            Log.d(TAG, "Role matches SELLER: " + role.equalsIgnoreCase("SELLER"));
            Log.d(TAG, "Role matches Seller: " + role.equalsIgnoreCase("Seller"));
            Log.d(TAG, "Role matches seller: " + role.equalsIgnoreCase("seller"));
        }

        // Show progress bar
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
        if (tvEmptyMessage != null) {
            tvEmptyMessage.setVisibility(View.GONE);
        }

        try {
            ApiService api = ApiClient.getClient(pref).create(ApiService.class);

            Call<Restaurant> call = api.getRestaurantById(restaurantId);
            Log.d(TAG, "API Request URL: " + call.request().url());
            Log.d(TAG, "Request Method: " + call.request().method());

            call.enqueue(new Callback<Restaurant>() {
                @Override
                public void onResponse(Call<Restaurant> call, Response<Restaurant> response) {
                    Log.d(TAG, "========== API RESPONSE RECEIVED ==========");
                    Log.d(TAG, "Status Code: " + response.code());
                    Log.d(TAG, "Message: " + response.message());
                    Log.d(TAG, "Is Successful: " + response.isSuccessful());

                    // Hide progress bar
                    if (progressBar != null) {
                        progressBar.setVisibility(View.GONE);
                    }

                    // Handle 403 Forbidden
                    if (response.code() == 403) {
                        Log.e(TAG, "========== ERROR 403 FORBIDDEN ==========");
                        Log.e(TAG, "You don't have permission to access this restaurant");
                        Log.e(TAG, "Possible causes:");
                        Log.e(TAG, "1. Restaurant doesn't belong to you");
                        Log.e(TAG, "2. Your user ID doesn't match restaurant owner");
                        Log.e(TAG, "3. Backend permission check is failing");

                        String errorBody = "";
                        try {
                            if (response.errorBody() != null) {
                                errorBody = response.errorBody().string();
                                Log.e(TAG, "Error details: " + errorBody);
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error reading error body", e);
                        }

                        Toast.makeText(RestaurantDetailsActivity.this,
                                "Permission Denied: You cannot access this restaurant",
                                Toast.LENGTH_LONG).show();

                        if (tvEmptyMessage != null) {
                            tvEmptyMessage.setVisibility(View.VISIBLE);
                            tvEmptyMessage.setText("Permission Denied:\n\nThis restaurant doesn't belong to your account.\n\n" +
                                    "Make sure you're logged in with the correct seller account that created this restaurant.");
                        }

                        return;
                    }

                    // Handle successful response
                    if (response.isSuccessful() && response.body() != null) {
                        Restaurant restaurant = response.body();
                        List<MenuItem> menuItems = restaurant.getMenuItems();

                        Log.d(TAG, "Restaurant Name: " + restaurant.getName());
                        Log.d(TAG, "Menu Items Count: " + (menuItems != null ? menuItems.size() : 0));

                        if (menuItems != null && !menuItems.isEmpty()) {
                            Log.d(TAG, "SUCCESS: Loaded " + menuItems.size() + " menu items");
                            recyclerMenu.setAdapter(new MenuItemAdapter(
                                    RestaurantDetailsActivity.this,
                                    menuItems,
                                    item -> {
                                        Log.d(TAG, "Item clicked: " + item.getName());
                                        Toast.makeText(RestaurantDetailsActivity.this,
                                                "Clicked: " + item.getName(),
                                                Toast.LENGTH_SHORT).show();
                                    }
                            ));
                            if (tvEmptyMessage != null) {
                                tvEmptyMessage.setVisibility(View.GONE);
                            }
                        } else {
                            Log.d(TAG, "No menu items found");
                            Toast.makeText(RestaurantDetailsActivity.this,
                                    "No menu items yet. Add some!",
                                    Toast.LENGTH_SHORT).show();
                            recyclerMenu.setAdapter(null);
                            if (tvEmptyMessage != null) {
                                tvEmptyMessage.setVisibility(View.VISIBLE);
                                tvEmptyMessage.setText("No menu items yet. Add some!");
                            }
                        }
                    } else {
                        // Handle other error responses
                        Log.e(TAG, "========== ERROR RESPONSE ==========");
                        Log.e(TAG, "Error Code: " + response.code());

                        String errorMsg = "Error " + response.code() + ": " + response.message();

                        try {
                            if (response.errorBody() != null) {
                                String errorBody = response.errorBody().string();
                                Log.e(TAG, "Error Body: " + errorBody);
                                errorMsg = errorBody;
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing response", e);
                        }

                        Toast.makeText(RestaurantDetailsActivity.this,
                                errorMsg,
                                Toast.LENGTH_LONG).show();

                        if (tvEmptyMessage != null) {
                            tvEmptyMessage.setVisibility(View.VISIBLE);
                            tvEmptyMessage.setText("Error: " + errorMsg);
                        }
                    }
                }

                @Override
                public void onFailure(Call<Restaurant> call, Throwable t) {
                    Log.e(TAG, "========== API CALL FAILED ==========");
                    Log.e(TAG, "Error Message: " + t.getMessage());
                    Log.e(TAG, "Error Type: " + t.getClass().getName());
                    t.printStackTrace();

                    // Hide progress bar
                    if (progressBar != null) {
                        progressBar.setVisibility(View.GONE);
                    }

                    String errorMessage = t.getMessage() != null ? t.getMessage() : "Unknown error";
                    Toast.makeText(RestaurantDetailsActivity.this,
                            "Network Error: " + errorMessage,
                            Toast.LENGTH_LONG).show();

                    if (tvEmptyMessage != null) {
                        tvEmptyMessage.setVisibility(View.VISIBLE);
                        tvEmptyMessage.setText("Network Error: " + errorMessage);
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Exception in loadMenuItems", e);
            e.printStackTrace();

            if (progressBar != null) {
                progressBar.setVisibility(View.GONE);
            }

            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();

            if (tvEmptyMessage != null) {
                tvEmptyMessage.setVisibility(View.VISIBLE);
                tvEmptyMessage.setText("Error: " + e.getMessage());
            }
        }
    }
}