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

    private EditText etName, etPhone, etPassword, etBusiness;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        etBusiness = findViewById(R.id.etBusinessName);

        Button btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(v -> register());
    }

    private void register() {

        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (!ValidationUtils.isValidPhone(phone) ||
                !ValidationUtils.isValidPassword(password)) {
            Toast.makeText(this, "Invalid input", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = ApiClient
                .getClient(new PreferenceManager(this))
                .create(ApiService.class);

        RegisterRequest request = new RegisterRequest(
                etName.getText().toString().trim(),
                phone,
                password,
                etBusiness.getText().toString().trim()
        );

        apiService.registerSeller(request)
                .enqueue(new Callback<ApiResponse>() {

                    @Override
                    public void onResponse(Call<ApiResponse> call,
                                           Response<ApiResponse> response) {
                        Toast.makeText(RegisterActivity.this,
                                "Registration successful",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        Toast.makeText(RegisterActivity.this,
                                "Registration failed",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
