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
    private TextView tvDetailName, tvDetailAddress, tvDetailLocation, tvDetailDelivery;
    private Long restaurantId;
    private String restaurantName;
    private static final String TAG = "RestaurantDetailsActivity";
    private static final int ADD_MENU_ITEM_REQUEST = 1;
    private static final int EDIT_MENU_IMAGE_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_details);

        restaurantId = getIntent().getLongExtra("restaurant_id", -1L);
        restaurantName = getIntent().getStringExtra("restaurant_name");

        if (restaurantId == -1L) {
            Toast.makeText(this, "Invalid restaurant", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize Views
        tvDetailName = findViewById(R.id.tvDetailName);
        tvDetailAddress = findViewById(R.id.tvDetailAddress);
        tvDetailLocation = findViewById(R.id.tvDetailLocation);
        tvDetailDelivery = findViewById(R.id.tvDetailDelivery);
        recyclerMenu = findViewById(R.id.recyclerMenu);
        progressBar = findViewById(R.id.progressBar);
        tvEmptyMessage = findViewById(R.id.tvEmptyMessage);

        // Setup toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle(restaurantName != null ? restaurantName : "Menu Items");
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        recyclerMenu.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton fab = findViewById(R.id.fabAddMenu);
        if (fab != null) {
            fab.setOnClickListener(v -> {
                Intent intent = new Intent(this, AddMenuItemActivity.class);
                intent.putExtra(Constants.EXTRA_RESTAURANT_ID, restaurantId);
                intent.putExtra(Constants.EXTRA_RESTAURANT_NAME, restaurantName);
                startActivityForResult(intent, ADD_MENU_ITEM_REQUEST);
            });
        }

        loadMenuItems();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            loadMenuItems();
        }
    }

    private void loadMenuItems() {
        PreferenceManager pref = new PreferenceManager(this);
        progressBar.setVisibility(View.VISIBLE);
        tvEmptyMessage.setVisibility(View.GONE);

        ApiService api = ApiClient.getClient(pref).create(ApiService.class);
        api.getRestaurants().enqueue(new Callback<List<Restaurant>>() {
            @Override
            public void onResponse(Call<List<Restaurant>> call, Response<List<Restaurant>> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    Restaurant targetRestaurant = null;
                    for (Restaurant r : response.body()) {
                        if (r.getId().equals(restaurantId)) {
                            targetRestaurant = r;
                            break;
                        }
                    }

                    if (targetRestaurant != null) {
                        tvDetailName.setText(targetRestaurant.getName());
                        tvDetailAddress.setText(targetRestaurant.getAddress());

                        // FIXED: Convert Enum to String using .name()
                        String locationText = "Location: " + (targetRestaurant.getLocationArea() != null
                                ? targetRestaurant.getLocationArea().name() : "N/A");
                        tvDetailLocation.setText(locationText);

                        // FIXED: Convert Enum to String using .name()
                        String deliveryText = (targetRestaurant.getDeliveryType() != null
                                ? targetRestaurant.getDeliveryType().name() : "STANDARD");
                        tvDetailDelivery.setText(deliveryText);

                        // Populate Menu Items
                        List<MenuItem> items = targetRestaurant.getMenuItems();
                        if (items != null && !items.isEmpty()) {
                            recyclerMenu.setAdapter(new MenuItemAdapter(RestaurantDetailsActivity.this, items, new MenuItemAdapter.OnMenuItemClickListener() {
                                @Override
                                public void onItemClick(MenuItem item) { /* Handle click */ }

                                @Override
                                public void onEditImageClick(MenuItem item) {
                                    Intent intent = new Intent(RestaurantDetailsActivity.this, EditMenuImageActivity.class);
                                    intent.putExtra("menu_item_id", item.getId());
                                    intent.putExtra("menu_item_name", item.getName());
                                    intent.putExtra("menu_item_price", item.getPrice());
                                    intent.putExtra("image_path", item.getImagePath());
                                    startActivityForResult(intent, EDIT_MENU_IMAGE_REQUEST);
                                }
                            }));
                            tvEmptyMessage.setVisibility(View.GONE);
                        } else {
                            tvEmptyMessage.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Restaurant>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(RestaurantDetailsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}