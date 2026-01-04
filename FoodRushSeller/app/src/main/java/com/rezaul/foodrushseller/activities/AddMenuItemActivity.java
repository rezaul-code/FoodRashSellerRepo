package com.rezaul.foodrushseller.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.rezaul.foodrushseller.R;
import com.rezaul.foodrushseller.models.ApiResponse;
import com.rezaul.foodrushseller.network.ApiClient;
import com.rezaul.foodrushseller.network.ApiService;
import com.rezaul.foodrushseller.utils.Constants;
import com.rezaul.foodrushseller.utils.PreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddMenuItemActivity extends AppCompatActivity {

    private EditText etItemName, etPrice, etDescription;
    private Button btnAddItem, btnSelectImage, btnVeg, btnNonVeg;
    private ImageView ivMenuImage;
    private ProgressBar progressBar;
    private Long restaurantId;
    private File selectedImageFile;
    private boolean isVeg = true; // Default to veg
    private static final String TAG = "AddMenuItemActivity";

    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ActivityResultLauncher<String> cameraPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_menu_item_with_image);

        Log.d(TAG, "========== ACTIVITY CREATED ==========");

        // Get restaurant ID from intent
        restaurantId = getIntent().getLongExtra(Constants.EXTRA_RESTAURANT_ID, -1L);

        Log.d(TAG, "Restaurant ID received: " + restaurantId);

        if (restaurantId == -1L) {
            Toast.makeText(this, "Invalid restaurant", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize views
        etItemName = findViewById(R.id.etItemName);
        etPrice = findViewById(R.id.etPrice);
        etDescription = findViewById(R.id.etDescription);
        btnAddItem = findViewById(R.id.btnAddMenuItem);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        ivMenuImage = findViewById(R.id.ivMenuImage);
        progressBar = findViewById(R.id.progressBar);
        btnVeg = findViewById(R.id.btnVeg);
        btnNonVeg = findViewById(R.id.btnNonVeg);

        // Image picker launcher
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            handleImageSelection(imageUri);
                        }
                    }
                }
        );

        // Camera permission launcher
        cameraPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        openImagePicker();
                    } else {
                        Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        btnSelectImage.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                cameraPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
            } else {
                cameraPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        });

        btnAddItem.setOnClickListener(v -> addMenuItem());

        // Veg/Non-Veg toggle buttons
        btnVeg.setOnClickListener(v -> selectVeg(true));
        btnNonVeg.setOnClickListener(v -> selectVeg(false));

        // Set initial state - Veg selected
        selectVeg(true);
    }

    private void selectVeg(boolean veg) {
        isVeg = veg;

        if (veg) {
            // Veg selected - Green
            btnVeg.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
            btnVeg.setTextColor(ContextCompat.getColor(this, android.R.color.white));

            btnNonVeg.setBackgroundColor(ContextCompat.getColor(this, android.R.color.darker_gray));
            btnNonVeg.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));

            Log.d(TAG, "Veg selected");
        } else {
            // Non-Veg selected - Green
            btnVeg.setBackgroundColor(ContextCompat.getColor(this, android.R.color.darker_gray));
            btnVeg.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));

            btnNonVeg.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
            btnNonVeg.setTextColor(ContextCompat.getColor(this, android.R.color.white));

            Log.d(TAG, "Non-Veg selected");
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void handleImageSelection(Uri imageUri) {
        try {
            // Get bitmap from URI
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);

            // Display preview
            ivMenuImage.setImageBitmap(bitmap);

            // Save to temp file
            File tempFile = new File(getCacheDir(), "menu_image_" + System.currentTimeMillis() + ".jpg");
            FileOutputStream fos = new FileOutputStream(tempFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
            fos.close();

            selectedImageFile = tempFile;
            Log.d(TAG, "Image selected and saved: " + tempFile.getAbsolutePath());
            Toast.makeText(this, "Image selected!", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Log.e(TAG, "Error handling image", e);
            Toast.makeText(this, "Error loading image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void addMenuItem() {
        String name = etItemName.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        Log.d(TAG, "Add Menu Item clicked");
        Log.d(TAG, "Name: " + name);
        Log.d(TAG, "Price: " + priceStr);
        Log.d(TAG, "Description: " + description);
        Log.d(TAG, "IsVeg: " + isVeg);
        Log.d(TAG, "Image selected: " + (selectedImageFile != null));

        // Validation
        if (name.isEmpty()) {
            etItemName.setError("Item name is required");
            etItemName.requestFocus();
            return;
        }
        if (priceStr.isEmpty()) {
            etPrice.setError("Price is required");
            etPrice.requestFocus();
            return;
        }
        if (description.isEmpty()) {
            etDescription.setError("Description is required");
            etDescription.requestFocus();
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
            if (price <= 0) {
                etPrice.setError("Price must be greater than 0");
                etPrice.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            etPrice.setError("Invalid price format");
            etPrice.requestFocus();
            return;
        }

        // Check if image is selected
        if (selectedImageFile == null) {
            Toast.makeText(this, "Please select a menu item image", Toast.LENGTH_SHORT).show();
            return;
        }

        showProgress(true);
        submitMenuItemWithImage(name, price, description);
    }

    private void submitMenuItemWithImage(String name, double price, String description) {
        try {
            // Create JSON data with veg field - use selected value
            JSONObject jsonData = new JSONObject();
            jsonData.put("name", name);
            jsonData.put("price", price);
            jsonData.put("description", description);
            jsonData.put("veg", isVeg);  // Use the selected veg/non-veg value
            jsonData.put("available", true);

            Log.d(TAG, "Request JSON: " + jsonData.toString());

            // Create request body for JSON
            RequestBody dataBody = RequestBody.create(
                    MediaType.parse("application/json"),
                    jsonData.toString()
            );

            // Create multipart image body
            RequestBody imageBody = RequestBody.create(
                    MediaType.parse("image/jpeg"),
                    selectedImageFile
            );

            MultipartBody.Part imagePart = MultipartBody.Part.createFormData(
                    "image",
                    selectedImageFile.getName(),
                    imageBody
            );

            // API Call
            PreferenceManager pref = new PreferenceManager(this);
            ApiService apiService = ApiClient.getClient(pref).create(ApiService.class);

            Log.d(TAG, "Making API call with image - Restaurant ID: " + restaurantId);
            Log.d(TAG, "Image file size: " + selectedImageFile.length() + " bytes");

            apiService.addMenuItem(restaurantId, dataBody, imagePart)
                    .enqueue(new Callback<ApiResponse>() {
                        @Override
                        public void onResponse(Call<ApiResponse> call,
                                               Response<ApiResponse> response) {
                            Log.d(TAG, "========== API RESPONSE RECEIVED ==========");
                            Log.d(TAG, "Status Code: " + response.code());
                            Log.d(TAG, "Message: " + response.message());
                            Log.d(TAG, "Is Successful: " + response.isSuccessful());

                            showProgress(false);

                            if (response.isSuccessful()) {
                                ApiResponse apiResponse = response.body();
                                if (apiResponse != null) {
                                    Log.d(TAG, "API Success: " + apiResponse.isSuccess());
                                    Log.d(TAG, "API Message: " + apiResponse.getMessage());

                                    Toast.makeText(AddMenuItemActivity.this,
                                            "Menu item added successfully!",
                                            Toast.LENGTH_SHORT).show();
                                    setResult(RESULT_OK);
                                    finish();
                                } else {
                                    Log.d(TAG, "Response body is null but response successful");
                                    Toast.makeText(AddMenuItemActivity.this,
                                            "Menu item added successfully!",
                                            Toast.LENGTH_SHORT).show();
                                    setResult(RESULT_OK);
                                    finish();
                                }
                            } else {
                                Log.e(TAG, "========== ERROR RESPONSE ==========");
                                Log.e(TAG, "Error Code: " + response.code());

                                String errorMsg = "Failed to add menu item (Error: " + response.code() + ")";
                                try {
                                    if (response.errorBody() != null) {
                                        String errorBody = response.errorBody().string();
                                        Log.e(TAG, "Error Body: " + errorBody);
                                        errorMsg = errorBody;
                                    }
                                } catch (Exception e) {
                                    Log.e(TAG, "Error reading error body", e);
                                }

                                Toast.makeText(AddMenuItemActivity.this,
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

                            showProgress(false);

                            Toast.makeText(AddMenuItemActivity.this,
                                    "Network Error: " + t.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

        } catch (JSONException e) {
            Log.e(TAG, "JSON Error: " + e.getMessage());
            showProgress(false);
            e.printStackTrace();
            Toast.makeText(this, "Error creating request: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void showProgress(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? android.view.View.VISIBLE : android.view.View.GONE);
        }
        btnAddItem.setEnabled(!show);
        btnSelectImage.setEnabled(!show);
        btnVeg.setEnabled(!show);
        btnNonVeg.setEnabled(!show);
    }
}