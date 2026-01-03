package com.rezaul.foodrushseller.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.rezaul.foodrushseller.R;
import com.rezaul.foodrushseller.models.ApiResponse;
import com.rezaul.foodrushseller.models.Restaurant;
import com.rezaul.foodrushseller.network.ApiClient;
import com.rezaul.foodrushseller.network.ApiService;
import com.rezaul.foodrushseller.utils.PreferenceManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddRestaurantActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_restaurant);

        EditText etName = findViewById(R.id.etRestaurantName);
        EditText etDesc = findViewById(R.id.etRestaurantDesc);
        EditText etAddr = findViewById(R.id.etRestaurantAddress);
        Button btnSave = findViewById(R.id.btnSaveRestaurant);

        btnSave.setOnClickListener(v -> {

            Restaurant restaurant = new Restaurant();
            // backend accepts JSON fields directly

            ApiService apiService = ApiClient
                    .getClient(new PreferenceManager(this))
                    .create(ApiService.class);

            apiService.addRestaurant(restaurant)
                    .enqueue(new Callback<ApiResponse>() {

                        @Override
                        public void onResponse(Call<ApiResponse> call,
                                               Response<ApiResponse> response) {
                            Toast.makeText(AddRestaurantActivity.this,
                                    "Restaurant added",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        }

                        @Override
                        public void onFailure(Call<ApiResponse> call, Throwable t) {
                            Toast.makeText(AddRestaurantActivity.this,
                                    "Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}
