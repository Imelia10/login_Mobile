package com.example.rewear_app1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    TextView hiTextView;
    ImageView profilImageView;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        hiTextView = findViewById(R.id.hi);
        profilImageView = findViewById(R.id.profil1);


        dbHelper = new DatabaseHelper(this);

        // Ambil email dari SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "");

        // Ambil data user dari SQLite berdasarkan email
        if (!email.isEmpty()) {
            User user = dbHelper.getUserByEmail(email); // Ambil data user dari SQLite
            if (user != null) {
                // Tampilkan nama di TextView
                hiTextView.setText("Hi, " + user.getFirstName());

                // TIDAK PERLU ubah foto profil di sini.
                // Biar gambar profil tetap icon default yang ada di layout.

                // Klik icon profil --> masuk ke ProfilActivity
                profilImageView.setOnClickListener(view -> {
                    Intent intent = new Intent(HomeActivity.this, ProfilActivity.class);
                    intent.putExtra("email", email);  // Kirim email ke ProfilActivity
                    startActivity(intent);
                });


            }
        }
    }
}
