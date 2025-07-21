package com.example.final_project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.final_project.model.AuthRequest;
import com.example.final_project.model.AuthResponse;
import com.example.final_project.network.ApiService;
import com.example.final_project.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final String SHARED_PREFS = "app_prefs";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_FULL_NAME = "full_name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_USER_TYPE = "user_type";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    private EditText edtEmail, edtPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Check if user is already logged in
        if (isLoggedIn()) {
            goToMainActivity();
            return;
        }

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);

        // Set default values for testing
        edtEmail.setText("levanc@example.com");
        edtPassword.setText("pass123");
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void login() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (email.isEmpty()) {
            edtEmail.setError("Email không được để trống");
            edtEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            edtPassword.setError("Password không được để trống");
            edtPassword.requestFocus();
            return;
        }

        btnLogin.setEnabled(false);
        btnLogin.setText("Đang đăng nhập...");

        AuthRequest request = new AuthRequest(email, password);
        ApiService apiService = RetrofitClient.getInstance();

        Log.d(TAG, "Attempting login with email: " + email);

        apiService.login(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                btnLogin.setEnabled(true);
                btnLogin.setText("Đăng nhập");

                Log.d(TAG, "Login response code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse authResponse = response.body();

                    Log.d(TAG, "Login successful - UserID: " + authResponse.getUserId() +
                            ", Name: " + authResponse.getFullName() +
                            ", Type: " + authResponse.getUserType());

                    // Save user info to SharedPreferences
                    saveUserInfo(authResponse);

                    Toast.makeText(LoginActivity.this,
                            "Đăng nhập thành công!\nChào mừng " + authResponse.getFullName(),
                            Toast.LENGTH_LONG).show();

                    // Go to main activity
                    goToMainActivity();

                } else {
                    Log.e(TAG, "Login failed: " + response.code());
                    if (response.errorBody() != null) {
                        try {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "Error body: " + errorBody);
                        } catch (Exception e) {
                            Log.e(TAG, "Error reading error body", e);
                        }
                    }
                    Toast.makeText(LoginActivity.this,
                            "Đăng nhập thất bại: " + response.code(),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                btnLogin.setEnabled(true);
                btnLogin.setText("Đăng nhập");

                Log.e(TAG, "Login network error: " + t.getMessage(), t);
                Toast.makeText(LoginActivity.this,
                        "Lỗi kết nối: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void saveUserInfo(AuthResponse authResponse) {
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putInt(KEY_USER_ID, authResponse.getUserId());
        editor.putString(KEY_FULL_NAME, authResponse.getFullName());
        editor.putString(KEY_EMAIL, authResponse.getEmail());
        editor.putString(KEY_USER_TYPE, authResponse.getUserType());
        editor.putBoolean(KEY_IS_LOGGED_IN, true);

        editor.apply();

        Log.d(TAG, "User info saved to SharedPreferences");
    }

    private boolean isLoggedIn() {
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    private void goToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    // Static utility methods for other activities to use
    public static int getCurrentUserId(android.content.Context context) {
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        return prefs.getInt(KEY_USER_ID, -1);
    }

    public static String getCurrentUserName(android.content.Context context) {
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        return prefs.getString(KEY_FULL_NAME, "");
    }

    public static String getCurrentUserType(android.content.Context context) {
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        return prefs.getString(KEY_USER_TYPE, "");
    }

    public static String getCurrentUserEmail(android.content.Context context) {
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        return prefs.getString(KEY_EMAIL, "");
    }

    public static void logout(android.content.Context context) {
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();

        Log.d(TAG, "User logged out");
    }

    public static boolean isUserLoggedIn(android.content.Context context) {
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }
}
