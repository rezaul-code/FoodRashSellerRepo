package com.rezaul.foodrushseller.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.rezaul.foodrushseller.R;
import com.rezaul.foodrushseller.models.ApiResponse;
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
    private Button btnSave;
    private static final String TAG = "AddRestaurantActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_restaurant);

        Log.d(TAG, "========== ACTIVITY CREATED ==========");

        etName = findViewById(R.id.etRestaurantName);
        etDesc = findViewById(R.id.etRestaurantDesc);
        etAddr = findViewById(R.id.etRestaurantAddress);
        btnSave = findViewById(R.id.btnSaveRestaurant);

        btnSave.setOnClickListener(v -> saveRestaurant());
    }

    private void saveRestaurant() {
        String name = etName.getText().toString().trim();
        String desc = etDesc.getText().toString().trim();
        String addr = etAddr.getText().toString().trim();

        Log.d(TAG, "Save Restaurant clicked");
        Log.d(TAG, "Name: " + name);
        Log.d(TAG, "Description: " + desc);
        Log.d(TAG, "Address: " + addr);

        // Validation
        if (name.isEmpty()) {
            etName.setError("Restaurant name is required");
            etName.requestFocus();
            return;
        }
        if (desc.isEmpty()) {
            etDesc.setError("Description is required");
            etDesc.requestFocus();
            return;
        }
        if (addr.isEmpty()) {
            etAddr.setError("Address is required");
            etAddr.requestFocus();
            return;
        }

        // Create JSON body
        try {
            JSONObject json = new JSONObject();
            json.put("name", name);
            json.put("description", desc);
            json.put("address", addr);

            Log.d(TAG, "Request JSON: " + json.toString());

            RequestBody body = RequestBody.create(
                    MediaType.parse("application/json"),
                    json.toString()
            );

            // API Call
            PreferenceManager pref = new PreferenceManager(this);
            ApiService apiService = ApiClient.getClient(pref).create(ApiService.class);

            Log.d(TAG, "Making API call...");

            apiService.addRestaurantJson(body)
                    .enqueue(new Callback<ApiResponse>() {
                        @Override
                        public void onResponse(Call<ApiResponse> call,
                                               Response<ApiResponse> response) {
                            Log.d(TAG, "========== API RESPONSE RECEIVED ==========");
                            Log.d(TAG, "Status Code: " + response.code());
                            Log.d(TAG, "Message: " + response.message());
                            Log.d(TAG, "Is Successful: " + response.isSuccessful());
                            Log.d(TAG, "Request URL: " + call.request().url());

                            if (response.isSuccessful()) {
                                ApiResponse apiResponse = response.body();
                                if (apiResponse != null) {
                                    Log.d(TAG, "API Success: " + apiResponse.isSuccess());
                                    Log.d(TAG, "API Message: " + apiResponse.getMessage());

                                    Toast.makeText(AddRestaurantActivity.this,
                                            "Restaurant added successfully!",
                                            Toast.LENGTH_SHORT).show();
                                    setResult(RESULT_OK);
                                    finish();
                                } else {
                                    Log.d(TAG, "Response body is null but response successful");
                                    Toast.makeText(AddRestaurantActivity.this,
                                            "Restaurant added successfully!",
                                            Toast.LENGTH_SHORT).show();
                                    setResult(RESULT_OK);
                                    finish();
                                }
                            } else {
                                // Handle error response
                                Log.e(TAG, "========== ERROR RESPONSE ==========");
                                Log.e(TAG, "Error Code: " + response.code());

                                String errorMsg = "Failed to add restaurant (Error: " + response.code() + ")";
                                try {
                                    if (response.errorBody() != null) {
                                        String errorBody = response.errorBody().string();
                                        Log.e(TAG, "Error Body: " + errorBody);
                                        errorMsg = errorBody;
                                    }
                                } catch (Exception e) {
                                    Log.e(TAG, "Error reading error body", e);
                                }

                                Toast.makeText(AddRestaurantActivity.this,
                                        errorMsg,
                                        Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse> call, Throwable t) {
                            Log.e(TAG, "========== API CALL FAILED ==========");
                            Log.e(TAG, "Error Message: " + t.getMessage());
                            Log.e(TAG, "Error Type: " + t.getClass().getName());
                            t.printStackTrace();

                            Toast.makeText(AddRestaurantActivity.this,
                                    "Network Error: " + t.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

        } catch (JSONException e) {
            Log.e(TAG, "JSON Error: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "Error creating request: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }
}