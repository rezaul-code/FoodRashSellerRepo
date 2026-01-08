package com.rezaul.foodrushseller.network;

import com.rezaul.foodrushseller.utils.PreferenceManager;

import android.util.Log;
import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    private PreferenceManager preferenceManager;
    private static final String TAG = "AuthInterceptor";

    public AuthInterceptor(PreferenceManager preferenceManager) {
        this.preferenceManager = preferenceManager;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request original = chain.request();
        Request.Builder requestBuilder = original.newBuilder();

        String token = preferenceManager.getToken();

        Log.d(TAG, "=== REQUEST ===");
        Log.d(TAG, "URL: " + original.url());
        Log.d(TAG, "Token exists: " + (token != null && !token.isEmpty()));

        if (token != null && !token.isEmpty()) {
            Log.d(TAG, "Token first 50 chars: " + token.substring(0, Math.min(50, token.length())));
            Log.d(TAG, "Token length: " + token.length());

            // Don't add "Bearer " if token already starts with it
            String authValue = token.startsWith("Bearer ") ? token : "Bearer " + token;
            requestBuilder.addHeader("Authorization", authValue);

            Log.d(TAG, "Authorization header: " + authValue.substring(0, Math.min(50, authValue.length())) + "...");
        } else {
            Log.e(TAG, "ERROR: Token is NULL or empty!");
        }

        Request request = requestBuilder.build();
        Log.d(TAG, "Headers: " + request.headers());

        Response response = chain.proceed(request);

        Log.d(TAG, "Response Code: " + response.code());
        Log.d(TAG, "=== END REQUEST ===");

        return response;
    }
}