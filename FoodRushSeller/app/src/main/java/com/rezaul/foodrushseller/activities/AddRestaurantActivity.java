package com.rezaul.foodrushseller.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.rezaul.foodrushseller.R;
import com.rezaul.foodrushseller.models.ApiResponse;
import com.rezaul.foodrushseller.models.DeliveryType;
import com.rezaul.foodrushseller.models.LocationArea;
import com.rezaul.foodrushseller.network.ApiClient;
import com.rezaul.foodrushseller.network.ApiService;
import com.rezaul.foodrushseller.utils.PreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddRestaurantActivity extends AppCompatActivity {

    private EditText etName, etDesc, etAddr;
    private AutoCompleteTextView spLocation, spDelivery;
    private Button btnSave;

    // Convert enums to String arrays for dropdown
    private String[] locationOptions;
    private String[] deliveryOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_restaurant);

        etName = findViewById(R.id.etRestaurantName);
        etDesc = findViewById(R.id.etRestaurantDesc);
        etAddr = findViewById(R.id.etRestaurantAddress);
        spLocation = findViewById(R.id.spLocationArea);
        spDelivery = findViewById(R.id.spDeliveryType);
        btnSave = findViewById(R.id.btnSaveRestaurant);

        // Initialize arrays from enums
        initializeDropdownOptions();

        // Setup Dropdowns for Material AutoCompleteTextView
        ArrayAdapter<String> locAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, locationOptions);
        spLocation.setAdapter(locAdapter);
        spLocation.setText(locationOptions[0], false);

        ArrayAdapter<String> delAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, deliveryOptions);
        spDelivery.setAdapter(delAdapter);
        spDelivery.setText(deliveryOptions[0], false);

        btnSave.setOnClickListener(v -> saveRestaurant());
    }

    private void initializeDropdownOptions() {
        // Convert LocationArea enum to String array
        LocationArea[] locations = LocationArea.values();
        locationOptions = new String[locations.length];
        for (int i = 0; i < locations.length; i++) {
            locationOptions[i] = locations[i].name();
        }

        // Convert DeliveryType enum to String array
        DeliveryType[] deliveries = DeliveryType.values();
        deliveryOptions = new String[deliveries.length];
        for (int i = 0; i < deliveries.length; i++) {
            deliveryOptions[i] = deliveries[i].name();
        }
    }

    private void saveRestaurant() {
        String name = etName.getText().toString().trim();
        String desc = etDesc.getText().toString().trim();
        String addr = etAddr.getText().toString().trim();
        String location = spLocation.getText().toString();
        String delivery = spDelivery.getText().toString();

        if (name.isEmpty() || desc.isEmpty() || addr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            JSONObject json = new JSONObject();
            json.put("name", name);
            json.put("description", desc);
            json.put("address", addr);
            json.put("locationArea", location);
            json.put("deliveryType", delivery);

            RequestBody body = RequestBody.create(
                    MediaType.parse("application/json"),
                    json.toString()
            );

            ApiService apiService = ApiClient.getClient(new PreferenceManager(this)).create(ApiService.class);

            apiService.addRestaurantJson(body).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(AddRestaurantActivity.this, "Restaurant added!", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Toast.makeText(AddRestaurantActivity.this, "Error: " + response.code(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    Toast.makeText(AddRestaurantActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}