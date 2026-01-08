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
import android.widget.TextView;
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

public class EditMenuImageActivity extends AppCompatActivity {

    private ImageView ivMenuImage;
    private TextView tvMenuName, tvMenuPrice;
    private Button btnSelectImage, btnUploadImage, btnDeleteImage;
    private ProgressBar progressBar;
    private Long menuItemId;
    private String menuItemName;
    private Double menuItemPrice;
    private String currentImagePath;
    private File selectedImageFile;
    private static final String TAG = "EditMenuImageActivity";

    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ActivityResultLauncher<String> permissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_menu_image);

        Log.d(TAG, "EditMenuImageActivity created");

        // Get data from intent
        menuItemId = getIntent().getLongExtra("menu_item_id", -1L);
        menuItemName = getIntent().getStringExtra("menu_item_name");
        menuItemPrice = getIntent().getDoubleExtra("menu_item_price", 0.0);
        currentImagePath = getIntent().getStringExtra("image_path");

        Log.d(TAG, "Menu Item ID: " + menuItemId);
        Log.d(TAG, "Menu Item Name: " + menuItemName);
        Log.d(TAG, "Current Image Path: " + currentImagePath);

        if (menuItemId == -1L) {
            Toast.makeText(this, "Invalid menu item", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize views
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        ivMenuImage = findViewById(R.id.ivMenuImage);
        tvMenuName = findViewById(R.id.tvMenuName);
        tvMenuPrice = findViewById(R.id.tvMenuPrice);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnUploadImage = findViewById(R.id.btnUploadImage);
        btnDeleteImage = findViewById(R.id.btnDeleteImage);
        progressBar = findViewById(R.id.progressBar);

        // Setup toolbar
        toolbar.setTitle("Edit Menu Image");
        toolbar.setNavigationOnClickListener(v -> finish());

        // Display menu item info
        tvMenuName.setText(menuItemName);
        tvMenuPrice.setText("₹ " + menuItemPrice);

        // Load current image if exists
        loadCurrentImage();

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

        // Permission launcher
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

        // Button listeners
        btnSelectImage.setOnClickListener(v -> requestPermissionAndPickImage());
        btnUploadImage.setOnClickListener(v -> uploadImage());
        btnDeleteImage.setOnClickListener(v -> deleteImage());

        // Initially disable upload button
        btnUploadImage.setEnabled(false);

        // Show/hide delete button based on current image
        updateDeleteButtonVisibility();
    }

    private void loadCurrentImage() {
        if (currentImagePath != null && !currentImagePath.isEmpty()) {
            String imageUrl = Constants.BASE_URL + currentImagePath;
            Log.d(TAG, "Loading image from: " + imageUrl);

            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.color.colorImagePlaceholder)
                    .error(R.color.colorImagePlaceholder)
                    .into(ivMenuImage);
        } else {
            Log.d(TAG, "No image available");
            ivMenuImage.setImageResource(R.color.colorImagePlaceholder);
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
            ivMenuImage.setImageBitmap(bitmap);

            // Save to temp file
            File tempFile = new File(getCacheDir(), "menu_" + System.currentTimeMillis() + ".jpg");
            FileOutputStream fos = new FileOutputStream(tempFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
            fos.close();

            selectedImageFile = tempFile;
            btnUploadImage.setEnabled(true);

            Toast.makeText(this, "Image selected! Click Upload to save.", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Image selected: " + tempFile.getAbsolutePath());

        } catch (Exception e) {
            Log.e(TAG, "Error handling image", e);
            Toast.makeText(this, "Error loading image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImage() {
        if (selectedImageFile == null) {
            Toast.makeText(this, "Please select an image first", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Uploading image for menu item ID: " + menuItemId);
        showProgress(true);

        try {
            RequestBody imageBody = RequestBody.create(
                    MediaType.parse("image/jpeg"),
                    selectedImageFile
            );

            MultipartBody.Part imagePart = MultipartBody.Part.createFormData(
                    "image",
                    selectedImageFile.getName(),
                    imageBody
            );

            PreferenceManager pref = new PreferenceManager(this);
            ApiService apiService = ApiClient.getClient(pref).create(ApiService.class);

            Log.d(TAG, "Making API call to upload menu image");

            apiService.updateMenuImage(menuItemId, imagePart)
                    .enqueue(new Callback<ApiResponse>() {
                        @Override
                        public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                            Log.d(TAG, "Upload response received: " + response.code());
                            showProgress(false);

                            if (response.isSuccessful()) {
                                Log.d(TAG, "Image uploaded successfully");
                                Toast.makeText(EditMenuImageActivity.this,
                                        "Image uploaded successfully!",
                                        Toast.LENGTH_SHORT).show();

                                btnUploadImage.setEnabled(false);
                                selectedImageFile = null;
                                updateDeleteButtonVisibility();

                                setResult(RESULT_OK);
                                finish();
                            } else {
                                Log.e(TAG, "Upload failed with code: " + response.code());
                                String errorMsg = "Failed to upload image";
                                try {
                                    if (response.errorBody() != null) {
                                        errorMsg = response.errorBody().string();
                                        Log.e(TAG, "Error body: " + errorMsg);
                                    }
                                } catch (Exception e) {
                                    Log.e(TAG, "Error reading error body", e);
                                }
                                Toast.makeText(EditMenuImageActivity.this,
                                        errorMsg,
                                        Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse> call, Throwable t) {
                            Log.e(TAG, "Upload failed: " + t.getMessage());
                            t.printStackTrace();
                            showProgress(false);
                            Toast.makeText(EditMenuImageActivity.this,
                                    "Network Error: " + t.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

        } catch (Exception e) {
            Log.e(TAG, "Exception during upload", e);
            showProgress(false);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteImage() {
        Log.d(TAG, "Deleting image for menu item ID: " + menuItemId);
        showProgress(true);

        PreferenceManager pref = new PreferenceManager(this);
        ApiService apiService = ApiClient.getClient(pref).create(ApiService.class);

        apiService.deleteMenuImage(menuItemId)
                .enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        Log.d(TAG, "Delete response received: " + response.code());
                        showProgress(false);

                        if (response.isSuccessful()) {
                            Log.d(TAG, "Image deleted successfully");
                            Toast.makeText(EditMenuImageActivity.this,
                                    "Image deleted successfully!",
                                    Toast.LENGTH_SHORT).show();

                            ivMenuImage.setImageResource(R.color.colorImagePlaceholder);
                            currentImagePath = null;
                            updateDeleteButtonVisibility();

                            setResult(RESULT_OK);
                            finish();
                        } else {
                            Log.e(TAG, "Delete failed with code: " + response.code());
                            String errorMsg = "Failed to delete image";
                            try {
                                if (response.errorBody() != null) {
                                    errorMsg = response.errorBody().string();
                                    Log.e(TAG, "Error body: " + errorMsg);
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error reading error body", e);
                            }
                            Toast.makeText(EditMenuImageActivity.this,
                                    errorMsg,
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        Log.e(TAG, "Delete failed: " + t.getMessage());
                        t.printStackTrace();
                        showProgress(false);
                        Toast.makeText(EditMenuImageActivity.this,
                                "Network Error: " + t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void updateDeleteButtonVisibility() {
        boolean hasImage = currentImagePath != null && !currentImagePath.isEmpty();
        btnDeleteImage.setVisibility(hasImage ? View.VISIBLE : View.GONE);
        Log.d(TAG, "Delete button visibility: " + (hasImage ? "VISIBLE" : "GONE"));
    }

    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnSelectImage.setEnabled(!show);
        btnUploadImage.setEnabled(!show && selectedImageFile != null);
        btnDeleteImage.setEnabled(!show);
    }
}