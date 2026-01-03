package com.rezaul.foodrushseller.utils;

import android.util.Log;

/**
 * Helper class to test and verify API configuration
 * Use this to diagnose API connection issues
 */
public class ApiTestHelper {

    private static final String TAG = "ApiTestHelper";

    /**
     * Call this from onCreate() to verify API setup
     * ApiTestHelper.testApiConfiguration(new PreferenceManager(this));
     */
    public static void testApiConfiguration(PreferenceManager pref) {
        Log.d(TAG, "========== API CONFIGURATION TEST ==========");

        // Check base URL
        String baseUrl = Constants.BASE_URL;
        Log.d(TAG, "Base URL: " + baseUrl);
        Log.d(TAG, "Base URL valid: " + (baseUrl != null && !baseUrl.isEmpty()));

        // Check token
        String token = pref.getToken();
        Log.d(TAG, "Token exists: " + (token != null && !token.isEmpty()));
        if (token != null) {
            Log.d(TAG, "Token length: " + token.length());
            Log.d(TAG, "Token starts with 'Bearer': " + token.startsWith("Bearer"));
            int previewLength = Math.min(30, token.length());
            Log.d(TAG, "Token preview: " + token.substring(0, previewLength) + "...");
        } else {
            Log.e(TAG, "ERROR: Token is NULL - User might not be logged in!");
        }

        // Check login status
        boolean loggedIn = pref.isLoggedIn();
        Log.d(TAG, "Is Logged In: " + loggedIn);

        // Check role
        String role = pref.getRole();
        Log.d(TAG, "User Role: " + role);

        Log.d(TAG, "========== TEST COMPLETE ==========");
    }

    /**
     * Verify restaurant ID before making API call
     */
    public static boolean isValidRestaurantId(Long restaurantId) {
        boolean valid = restaurantId != null && restaurantId > 0 && restaurantId != -1L;
        Log.d(TAG, "Restaurant ID: " + restaurantId + ", Valid: " + valid);
        if (!valid) {
            Log.e(TAG, "ERROR: Invalid restaurant ID provided!");
        }
        return valid;
    }

    /**
     * Build expected API URL
     */
    public static String buildRestaurantUrl(Long restaurantId) {
        String url = Constants.BASE_URL + "/api/seller/restaurant/" + restaurantId;
        Log.d(TAG, "Expected URL: " + url);
        return url;
    }

    /**
     * Log complete request details for debugging
     */
    public static void logRequestDetails(Long restaurantId, PreferenceManager pref) {
        Log.d(TAG, "========== REQUEST DETAILS ==========");
        Log.d(TAG, "Endpoint: /api/seller/restaurant/" + restaurantId);
        Log.d(TAG, "Full URL: " + buildRestaurantUrl(restaurantId));
        Log.d(TAG, "Method: GET");

        String token = pref.getToken();
        if (token != null) {
            Log.d(TAG, "Authorization Header: Bearer [TOKEN_" + token.length() + "_CHARS]");
        } else {
            Log.e(TAG, "ERROR: No token found for authorization!");
        }

        Log.d(TAG, "========== END REQUEST DETAILS ==========");
    }
}