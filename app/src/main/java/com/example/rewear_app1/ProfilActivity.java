package com.example.rewear_app1;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ProfilActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private ImageView fotoProfil, logoImage, back;
    private TextView etFirstName, etLastName, etPhone, etEmail, etAlamat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profil);

        dbHelper = new DatabaseHelper(this);

        // Inisialisasi semua view
        fotoProfil = findViewById(R.id.foto);
        logoImage = findViewById(R.id.fab_custom);
        back = findViewById(R.id.back);
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        etAlamat = findViewById(R.id.etAlamat);

        // Ambil email dari intent
        String email = getIntent().getStringExtra("email");

        if (email != null) {
            // Ambil data user dari database berdasarkan email
            User user = dbHelper.getUserByEmail(email);

            if (user != null) {
                // Set data ke TextView
                etFirstName.setText(user.getFirstName());
                etLastName.setText(user.getLastName());
                etPhone.setText(user.getPhone());
                etEmail.setText(user.getEmail());
                etAlamat.setText(user.getAlamat());

                // Set foto profil jika ada URI
                String photoUriString = user.getPhotoUri();

                if (photoUriString != null && !photoUriString.isEmpty()) {
                    Uri photoUri = Uri.parse(photoUriString);
                    fotoProfil.setImageURI(photoUri);
                }
            } else {
                Toast.makeText(this, "User tidak ditemukan", Toast.LENGTH_SHORT).show();
            }
        }

        // Klik logoImage --> Balik ke HomeActivity
        logoImage.setOnClickListener(view -> {
            Intent intent = new Intent(ProfilActivity.this, HomeActivity.class);
            startActivity(intent);
            finish(); // supaya ProfilActivity ditutup, user langsung di Home
        });
        // Klik back --> Balik ke HomeActivity
        back.setOnClickListener(view -> {
            Intent intent = new Intent(ProfilActivity.this, HomeActivity.class);
            startActivity(intent);
            finish(); // supaya ProfilActivity ditutup, user langsung di Home
        });
    }
}
