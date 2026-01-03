package com.rezaul.foodrushseller.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.rezaul.foodrushseller.R;
import com.rezaul.foodrushseller.models.LoginRequest;
import com.rezaul.foodrushseller.models.LoginResponse;
import com.rezaul.foodrushseller.network.ApiClient;
import com.rezaul.foodrushseller.network.ApiService;
import com.rezaul.foodrushseller.utils.PreferenceManager;
import com.rezaul.foodrushseller.utils.ValidationUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etPhone, etPassword;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView tvRegister = findViewById(R.id.tvRegister);

        preferenceManager = new PreferenceManager(this);

        btnLogin.setOnClickListener(v -> login());
        tvRegister.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
    }

    private void login() {

        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (!ValidationUtils.isValidPhone(phone)) {
            Toast.makeText(this, "Enter valid phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!ValidationUtils.isValidPassword(password)) {
            Toast.makeText(this, "Enter valid password", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = ApiClient
                .getClient(preferenceManager)
                .create(ApiService.class);

        LoginRequest request = new LoginRequest(phone, password);

        apiService.login(request)
                .enqueue(new Callback<LoginResponse>() {

                    @Override
                    public void onResponse(Call<LoginResponse> call,
                                           Response<LoginResponse> response) {

                        if (response.isSuccessful() && response.body() != null) {

                            preferenceManager.saveLogin(
                                    response.body().getToken(),
                                    response.body().getRole()
                            );

                            Toast.makeText(LoginActivity.this,
                                    "Login successful",
                                    Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(
                                    LoginActivity.this,
                                    DashboardActivity.class
                            ));
                            finish();

                        } else {
                            Toast.makeText(LoginActivity.this,
                                    "Invalid phone or password",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        Toast.makeText(LoginActivity.this,
                                "Server not reachable",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
