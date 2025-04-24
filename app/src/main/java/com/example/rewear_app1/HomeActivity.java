package com.example.rewear_app1;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    TextView hiTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        hiTextView = findViewById(R.id.hi); // Menggunakan ID yang sesuai dengan layout kamu

        // Mengambil data dari SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String firstName = sharedPreferences.getString("firstName", "User");

        // Mengubah teks sesuai dengan nama pengguna
        hiTextView.setText("Hi, " + firstName); // Menampilkan nama pengguna di TextView dengan ID hi
    }
}
