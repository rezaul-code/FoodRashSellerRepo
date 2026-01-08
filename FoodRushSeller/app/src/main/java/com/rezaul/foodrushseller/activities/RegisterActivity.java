package com.rezaul.foodrushseller.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.rezaul.foodrushseller.R;
import com.rezaul.foodrushseller.models.ApiResponse;
import com.rezaul.foodrushseller.models.RegisterRequest;
import com.rezaul.foodrushseller.network.ApiClient;
import com.rezaul.foodrushseller.network.ApiService;
import com.rezaul.foodrushseller.utils.PreferenceManager;
import com.rezaul.foodrushseller.utils.ValidationUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText etName, etPhone, etPassword, etEmail, etBusiness;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        etEmail = findViewById(R.id.etEmail);
        etBusiness = findViewById(R.id.etBusinessName);

        Button btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(v -> register());
    }

    private void register() {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String business = etBusiness.getText().toString().trim();

        if (!ValidationUtils.isValidPhone(phone) || !ValidationUtils.isValidPassword(password)) {
            Toast.makeText(this, "Invalid phone or password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (name.isEmpty() || email.isEmpty() || business.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = ApiClient.getClient(new PreferenceManager(this)).create(ApiService.class);
        RegisterRequest request = new RegisterRequest(name, phone, password, business, email);

        apiService.registerSeller(request).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, "Failed: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}