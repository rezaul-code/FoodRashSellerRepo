package com.rezaul.foodrushseller.activities;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.rezaul.foodrushseller.R;
import com.rezaul.foodrushseller.utils.PreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

public class TokenDecoderActivity extends AppCompatActivity {

    private TextView tvTokenInfo;
    private static final String TAG = "TokenDecoderActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_token_decoder);

        tvTokenInfo = findViewById(R.id.tvTokenInfo);
        Button btnDecodeToken = findViewById(R.id.btnDecodeToken);
        Button btnCopyToken = findViewById(R.id.btnCopyToken);

        btnDecodeToken.setOnClickListener(v -> decodeToken());
        btnCopyToken.setOnClickListener(v -> copyTokenToClipboard());

        // Auto decode on load
        decodeToken();
    }

    private void decodeToken() {
        Log.d(TAG, "========== DECODING JWT TOKEN ==========");

        PreferenceManager pref = new PreferenceManager(this);
        String token = pref.getToken();

        StringBuilder sb = new StringBuilder();
        sb.append("========== JWT TOKEN DECODER ==========\n\n");

        if (token == null || token.isEmpty()) {
            sb.append("❌ NO TOKEN FOUND\n");
            sb.append("User not logged in!\n");
            tvTokenInfo.setText(sb.toString());
            Toast.makeText(this, "No token found. Please login first.", Toast.LENGTH_SHORT).show();
            return;
        }

        sb.append("✅ TOKEN FOUND\n\n");
        sb.append("Token Length: ").append(token.length()).append(" characters\n");
        sb.append("Token Preview: ").append(token.substring(0, Math.min(50, token.length()))).append("...\n\n");

        // Split token into parts
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            sb.append("❌ INVALID TOKEN FORMAT\n");
            sb.append("Token should have 3 parts separated by dots\n");
            sb.append("Found: ").append(parts.length).append(" parts\n");
            tvTokenInfo.setText(sb.toString());
            return;
        }

        sb.append("✅ TOKEN HAS 3 PARTS (valid JWT format)\n\n");

        try {
            // Decode header
            sb.append("========== HEADER ==========\n");
            String header = decodeBase64(parts[0]);
            sb.append(formatJson(header)).append("\n\n");

            // Decode payload (this is what we care about!)
            sb.append("========== PAYLOAD (IMPORTANT) ==========\n");
            String payload = decodeBase64(parts[1]);
            sb.append(formatJson(payload)).append("\n\n");

            // Parse payload to check for user ID
            sb.append("========== USER ID FIELDS ==========\n");
            JSONObject payloadJson = new JSONObject(payload);

            // Check for various user ID field names
            String[] userIdFields = {"userId", "user_id", "id", "sub", "sellerId", "seller_id"};
            boolean foundUserId = false;

            for (String field : userIdFields) {
                if (payloadJson.has(field)) {
                    Object value = payloadJson.get(field);
                    sb.append("✅ Found: '").append(field).append("': ").append(value).append("\n");
                    foundUserId = true;
                }
            }

            if (!foundUserId) {
                sb.append("❌ NO USER ID FOUND IN TOKEN!\n");
                sb.append("The token doesn't contain any user identifier.\n");
                sb.append("This is why you're getting 403 Forbidden.\n");
                sb.append("Backend can't identify which user made the request.\n");
            }

            // Check for other important fields
            sb.append("\n========== OTHER FIELDS ==========\n");
            String[] otherFields = {"email", "username", "role", "iat", "exp"};
            for (String field : otherFields) {
                if (payloadJson.has(field)) {
                    Object value = payloadJson.get(field);
                    String displayValue = value.toString();
                    if (field.equals("exp") || field.equals("iat")) {
                        // Convert Unix timestamp
                        long timestamp = Long.parseLong(displayValue);
                        displayValue = timestamp + " (Unix timestamp)";
                    }
                    sb.append(field).append(": ").append(displayValue).append("\n");
                }
            }

            sb.append("\n========== DIAGNOSIS ==========\n");
            if (foundUserId) {
                sb.append("✅ Token contains user ID\n");
                sb.append("✅ Backend should be able to identify you\n");
                sb.append("⚠️ If still getting 403, issue is in backend logic\n");
            } else {
                sb.append("❌ TOKEN IS MISSING USER ID!\n");
                sb.append("❌ Backend can't identify which user you are\n");
                sb.append("❌ This is causing the 403 Forbidden error\n");
                sb.append("\nSOLUTION:\n");
                sb.append("Backend's login endpoint needs to include user_id in JWT token\n");
            }

            // Show signature
            sb.append("\n========== SIGNATURE ==========\n");
            sb.append(parts[2].substring(0, Math.min(50, parts[2].length()))).append("...\n");
            sb.append("(Digital signature to verify token authenticity)\n");

            Log.d(TAG, sb.toString());

        } catch (Exception e) {
            Log.e(TAG, "Error decoding token", e);
            sb.append("❌ ERROR DECODING TOKEN\n");
            sb.append("Error: ").append(e.getMessage()).append("\n");
            sb.append("Make sure the token is valid\n");
        }

        tvTokenInfo.setText(sb.toString());
    }

    private void copyTokenToClipboard() {
        PreferenceManager pref = new PreferenceManager(this);
        String token = pref.getToken();

        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "No token to copy", Toast.LENGTH_SHORT).show();
            return;
        }

        android.content.ClipboardManager clipboard =
                (android.content.ClipboardManager) getSystemService(android.content.Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("JWT Token", token);
        clipboard.setPrimaryClip(clip);

        Toast.makeText(this, "Token copied to clipboard!\n\nPaste at: https://jwt.io", Toast.LENGTH_LONG).show();
    }

    private String decodeBase64(String encoded) throws Exception {
        // Add padding if needed
        int padding = encoded.length() % 4;
        if (padding > 0) {
            encoded += "=".repeat(4 - padding);
        }
        byte[] decoded = Base64.decode(encoded, Base64.URL_SAFE);
        return new String(decoded, "UTF-8");
    }

    private String formatJson(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            return obj.toString(2); // Pretty print with 2-space indent
        } catch (JSONException e) {
            return json; // Return as-is if not valid JSON
        }
    }
}