package com.example.rewear_app1.presentation.login.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.rewear_app1.AdminActivity;
import com.example.rewear_app1.DatabaseHelper;
import com.example.rewear_app1.HomeActivity;
import com.example.rewear_app1.RegisterActivity;
import com.example.rewear_app1.databinding.ActivityLoginBinding;
import com.example.rewear_app1.presentation.login.viewmodel.LoginViewModel;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private LoginViewModel loginViewModel;


    private void listenObserver(){

        loginViewModel.getLoginStatus().observe(this, status -> {
            if ("admin".equals(status)) {
                Toast.makeText(this, "Login Admin Berhasil", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, AdminActivity.class));
                finish();
            } else if ("user".equals(status)) {
                Toast.makeText(this, "Login User Berhasil", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, status, Toast.LENGTH_SHORT).show();
            }
        });

        loginViewModel.getLoggedInUser().observe(this, user -> {
            //generate session
            long expiresAt = System.currentTimeMillis() + 60 * 60 * 1000;

            // Simpan ke SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("email_login", user.getEmail());
            editor.putString("user_id", String.valueOf(user.getId()));
            editor.putString("firstName", user.getFirstName());
            editor.putString("lastName", user.getLastName());
            editor.putString("email", user.getEmail());
            editor.putLong("session_expires_at", expiresAt);
            editor.apply();

            Toast.makeText(this, "Login Berhasil", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, HomeActivity.class);
            intent.putExtra("user_email", user.getEmail());
            startActivity(intent);
            finish();
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        loginViewModel.setDatabaseHelper(new DatabaseHelper(this)); // inject dbHelper

        listenObserver();       // listen viewModel

        binding.btnLogin.setOnClickListener(view -> {
            String email = binding.etEmail.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();
            loginViewModel.login(email, password);
        });

        // Ke register
        binding.tvDaftar.setOnClickListener(view -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });

        binding.tvDaftar.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }
}