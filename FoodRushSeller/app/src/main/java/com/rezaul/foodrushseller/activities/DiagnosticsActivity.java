package com.rezaul.foodrushseller.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.rezaul.foodrushseller.R;
import com.rezaul.foodrushseller.utils.Constants;
import com.rezaul.foodrushseller.utils.PreferenceManager;

public class DiagnosticsActivity extends AppCompatActivity {

    private TextView tvDiagnostics;
    private static final String TAG = "DiagnosticsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnostic);

        tvDiagnostics = findViewById(R.id.tvDiagnostics);
        Button btnCheckPrefs = findViewById(R.id.btnCheckPrefs);
        Button btnClearPrefs = findViewById(R.id.btnClearPrefs);

        btnCheckPrefs.setOnClickListener(v -> checkPreferences());
        btnClearPrefs.setOnClickListener(v -> clearPreferences());

        // Auto check on load
        checkPreferences();
    }

    private void checkPreferences() {
        Log.d(TAG, "========== CHECKING SHARED PREFERENCES ==========");

        PreferenceManager pref = new PreferenceManager(this);
        SharedPreferences sharedPref = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);

        StringBuilder sb = new StringBuilder();
        sb.append("========== SHARED PREFERENCES DEBUG ==========\n\n");

        // Check all keys
        String token = pref.getToken();
        String role = pref.getRole();
        boolean isLoggedIn = pref.isLoggedIn();

        sb.append("KEY_TOKEN:\n");
        sb.append("  Value: ").append(token == null ? "NULL" : token.substring(0, Math.min(50, token.length())) + "...\n");
        sb.append("  Length: ").append(token == null ? "0" : token.length()).append("\n");
        sb.append("  Exists: ").append(token != null && !token.isEmpty() ? "YES" : "NO").append("\n\n");

        sb.append("KEY_ROLE:\n");
        sb.append("  Value: '").append(role).append("'\n");
        sb.append("  Type: ").append(role == null ? "NULL" : role.getClass().getSimpleName()).append("\n");
        sb.append("  Equals 'SELLER': ").append(role != null && role.equals("SELLER") ? "YES" : "NO").append("\n");
        sb.append("  Equals 'seller': ").append(role != null && role.equals("seller") ? "YES" : "NO").append("\n");
        sb.append("  Equals 'BUYER': ").append(role != null && role.equals("BUYER") ? "YES" : "NO").append("\n");
        sb.append("  Trim & Equals 'SELLER': ").append(role != null && role.trim().equals("SELLER") ? "YES" : "NO").append("\n\n");

        sb.append("KEY_LOGGED_IN:\n");
        sb.append("  Value: ").append(isLoggedIn ? "TRUE" : "FALSE").append("\n\n");

        // List all preferences
        sb.append("========== ALL PREFERENCES ==========\n");
        try {
            java.util.Map<String, ?> allPrefs = sharedPref.getAll();
            for (String key : allPrefs.keySet()) {
                Object value = allPrefs.get(key);
                sb.append(key).append(": ");
                if (value == null) {
                    sb.append("NULL");
                } else if (value instanceof String) {
                    String strVal = (String) value;
                    if (strVal.length() > 50) {
                        sb.append(strVal.substring(0, 50)).append("...");
                    } else {
                        sb.append("'").append(strVal).append("'");
                    }
                } else {
                    sb.append(value.toString());
                }
                sb.append("\n");
            }
        } catch (Exception e) {
            sb.append("Error reading preferences: ").append(e.getMessage()).append("\n");
            Log.e(TAG, "Error reading preferences", e);
        }

        sb.append("\n========== ANALYSIS ==========\n");
        if (token == null || token.isEmpty()) {
            sb.append("❌ Token is MISSING - User not logged in\n");
        } else {
            sb.append("✅ Token exists\n");
        }

        if (role == null) {
            sb.append("❌ Role is NULL\n");
        } else if (role.isEmpty()) {
            sb.append("❌ Role is EMPTY\n");
        } else if (role.equals("SELLER")) {
            sb.append("✅ Role is SELLER\n");
        } else if (role.equals("seller")) {
            sb.append("⚠️ Role is 'seller' (lowercase) - SHOULD BE 'SELLER' (uppercase)\n");
        } else {
            sb.append("❌ Role is '" + role + "' (NOT SELLER)\n");
        }

        if (!isLoggedIn) {
            sb.append("❌ Is Logged In: FALSE\n");
        } else {
            sb.append("✅ Is Logged In: TRUE\n");
        }

        String diagnostics = sb.toString();
        Log.d(TAG, diagnostics);

        tvDiagnostics.setText(diagnostics);

        Toast.makeText(this, "Diagnostics loaded. Check logs for details.", Toast.LENGTH_SHORT).show();
    }

    private void clearPreferences() {
        Log.d(TAG, "Clearing all preferences...");

        PreferenceManager pref = new PreferenceManager(this);
        pref.logout();

        Toast.makeText(this, "Preferences cleared!", Toast.LENGTH_SHORT).show();

        // Reload diagnostics
        checkPreferences();
    }
}