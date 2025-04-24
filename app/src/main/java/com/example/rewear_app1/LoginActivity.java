package com.example.rewear_app1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText email, password;
    Button btnLogin;
    TextView daftar;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btnLogin = findViewById(R.id.btnLogin);
        daftar = findViewById(R.id.daftar);

        dbHelper = new DatabaseHelper(this);

        btnLogin.setOnClickListener(view -> {
            String emailInput = email.getText().toString().trim();
            String passwordInput = password.getText().toString().trim();

            if (emailInput.isEmpty()) {
                Toast.makeText(this, "Email tidak boleh kosong!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (passwordInput.isEmpty()) {
                Toast.makeText(this, "Password tidak boleh kosong!", Toast.LENGTH_SHORT).show();
                return;
            }

            User user = dbHelper.getUserByEmailAndPassword(emailInput, passwordInput);
            if (user != null) {
                // Simpan nama pengguna di SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("firstName", user.getFirstName());
                editor.apply();

                // Tampilkan pesan berhasil login
                Toast.makeText(this, "Login Berhasil", Toast.LENGTH_SHORT).show();

                // Arahkan ke HomeActivity setelah login berhasil
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();  // Menutup LoginActivity agar tidak bisa kembali ke halaman login
            } else {
                // Jika email atau password salah
                Toast.makeText(this, "Email atau Password salah!", Toast.LENGTH_SHORT).show();
            }
        });

        daftar.setOnClickListener(view -> {
            // Arahkan ke RegisterActivity untuk pendaftaran pengguna baru
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}
