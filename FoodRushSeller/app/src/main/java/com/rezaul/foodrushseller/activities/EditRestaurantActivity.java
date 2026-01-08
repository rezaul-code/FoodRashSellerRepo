package com.rezaul.foodrushseller.activities;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.rezaul.foodrushseller.R;
import com.rezaul.foodrushseller.models.ApiResponse;
import com.rezaul.foodrushseller.network.ApiClient;
import com.rezaul.foodrushseller.network.ApiService;
import com.rezaul.foodrushseller.utils.Constants;
import com.rezaul.foodrushseller.utils.PreferenceManager;

import java.io.File;
import java.io.FileOutputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditRestaurantActivity extends AppCompatActivity {

    private ImageView ivBanner;
    private Button btnSelectImage, btnUploadImage, btnDeleteImage;
    private ProgressBar progressBar;

    private Long restaurantId;
    private String restaurantName;
    private String currentBannerUrl;

    private File selectedImageFile;

    private static final String TAG = "EditRestaurantActivity";

    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ActivityResultLauncher<String> permissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_restaurant);

        Log.d(TAG, "EditRestaurantActivity created");

        // Intent data
        restaurantId = getIntent().getLongExtra("restaurant_id", -1L);
        restaurantName = getIntent().getStringExtra("restaurant_name");
        currentBannerUrl = getIntent().getStringExtra("banner_url");

        if (restaurantId == -1L) {
            Toast.makeText(this, "Invalid restaurant", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Views
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        ivBanner = findViewById(R.id.ivBanner);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnUploadImage = findViewById(R.id.btnUploadImage);
        btnDeleteImage = findViewById(R.id.btnDeleteImage);
        progressBar = findViewById(R.id.progressBar);

        // Toolbar
        toolbar.setTitle(restaurantName != null ? restaurantName : "Edit Restaurant");
        toolbar.setNavigationOnClickListener(v -> finish());

        // Load existing banner
        loadCurrentBanner();

        // Image picker
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

        // Permission handler
        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        openImagePicker();
                    } else {
                        Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Clicks
        btnSelectImage.setOnClickListener(v -> requestPermissionAndPickImage());
        btnUploadImage.setOnClickListener(v -> uploadBanner());
        btnDeleteImage.setOnClickListener(v -> deleteBanner());

        btnUploadImage.setEnabled(false);
        updateDeleteButtonVisibility();
    }

    private void loadCurrentBanner() {
        if (currentBannerUrl != null && !currentBannerUrl.isEmpty()) {
            String imageUrl = Constants.BASE_URL + currentBannerUrl;

            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.color.colorImagePlaceholder)
                    .error(R.color.colorImagePlaceholder)
                    .into(ivBanner);
        } else {
            ivBanner.setImageResource(R.color.colorImagePlaceholder);
        }
    }

    private void requestPermissionAndPickImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
        } else {
            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void handleImageSelection(Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            ivBanner.setImageBitmap(bitmap);

            File tempFile = new File(getCacheDir(),
                    "banner_" + System.currentTimeMillis() + ".jpg");

            FileOutputStream fos = new FileOutputStream(tempFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
            fos.close();

            selectedImageFile = tempFile;
            btnUploadImage.setEnabled(true);

            Toast.makeText(this, "Image selected. Click upload.", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Image selected: " + tempFile.getAbsolutePath());

        } catch (Exception e) {
            Log.e(TAG, "Image error", e);
            Toast.makeText(this, "Image error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadBanner() {
        if (selectedImageFile == null) {
            Toast.makeText(this, "Please select an image first", Toast.LENGTH_SHORT).show();
            return;
        }

        showProgress(true);

        RequestBody imageBody = RequestBody.create(
                MediaType.parse("image/jpeg"),
                selectedImageFile
        );

        // TRY BOTH FIELD NAMES - START WITH "image"
        // If this fails, try changing to "banner"
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData(
                "image",  // ← CHANGE THIS TO "banner" IF IT FAILS
                selectedImageFile.getName(),
                imageBody
        );

        PreferenceManager pref = new PreferenceManager(this);
        ApiService apiService = ApiClient.getClient(pref).create(ApiService.class);

        Log.d(TAG, "=== UPLOADING BANNER ===");
        Log.d(TAG, "Restaurant ID: " + restaurantId);
        Log.d(TAG, "File: " + selectedImageFile.getName());

        apiService.uploadRestaurantBanner(restaurantId, imagePart)
                .enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        showProgress(false);

                        Log.d(TAG, "Response Code: " + response.code());
                        Log.d(TAG, "Is Successful: " + response.isSuccessful());

                        if (response.isSuccessful()) {
                            Toast.makeText(EditRestaurantActivity.this,
                                    "Banner uploaded successfully",
                                    Toast.LENGTH_SHORT).show();

                            // ✅ Get the banner URL from response if available
                            if (response.body() != null && response.body().getMessage() != null) {
                                // If server returns the banner URL in the response
                                Log.d(TAG, "Server Response Message: " + response.body().getMessage());
                            }

                            // Update state and refresh
                            currentBannerUrl = "updated";
                            selectedImageFile = null;
                            btnUploadImage.setEnabled(false);
                            updateDeleteButtonVisibility();

                            Log.d(TAG, "Setting RESULT_OK and finishing...");
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            try {
                                String errorBody = response.errorBody() != null ?
                                        response.errorBody().string() : "No error body";
                                Log.e(TAG, "Error: " + errorBody);

                                Toast.makeText(EditRestaurantActivity.this,
                                        "Error " + response.code() + ": " + errorBody,
                                        Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                Toast.makeText(EditRestaurantActivity.this,
                                        "Upload failed: " + response.code(),
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        showProgress(false);
                        Log.e(TAG, "Network error: " + t.getMessage(), t);
                        Toast.makeText(EditRestaurantActivity.this,
                                "Network error: " + t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void deleteBanner() {
        showProgress(true);

        PreferenceManager pref = new PreferenceManager(this);
        ApiService apiService = ApiClient.getClient(pref).create(ApiService.class);

        apiService.deleteRestaurantBanner(restaurantId)
                .enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        showProgress(false);

                        if (response.isSuccessful()) {
                            ivBanner.setImageResource(R.color.colorImagePlaceholder);
                            currentBannerUrl = null;
                            updateDeleteButtonVisibility();

                            Toast.makeText(EditRestaurantActivity.this,
                                    "Banner deleted successfully",
                                    Toast.LENGTH_SHORT).show();

                            setResult(RESULT_OK);
                            finish();
                        } else {
                            Toast.makeText(EditRestaurantActivity.this,
                                    "Delete failed: " + response.code(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        showProgress(false);
                        Toast.makeText(EditRestaurantActivity.this,
                                "Network error: " + t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void updateDeleteButtonVisibility() {
        boolean hasBanner = currentBannerUrl != null && !currentBannerUrl.isEmpty();
        btnDeleteImage.setVisibility(hasBanner ? View.VISIBLE : View.GONE);
    }

    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnSelectImage.setEnabled(!show);
        btnUploadImage.setEnabled(!show && selectedImageFile != null);
        btnDeleteImage.setEnabled(!show);
    }
}