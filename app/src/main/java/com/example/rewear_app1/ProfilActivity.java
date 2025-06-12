package com.example.rewear_app1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import com.example.rewear_app1.presentation.login.view.LoginActivity;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ProfilActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private ImageView fotoProfil, history, back, voucher, home;
    private TextView etFirstName, etLastName, etPhone, etEmail, etAlamat;
    private String currentUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profil);

        dbHelper = new DatabaseHelper(this);

        // Inisialisasi semua view
        fotoProfil = findViewById(R.id.foto);
        history = findViewById(R.id.history);
        back = findViewById(R.id.back);
        home = findViewById(R.id.home);
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        etAlamat = findViewById(R.id.etAlamat);
        voucher = findViewById(R.id.voucher);

        // Ambil email user dari SharedPreferences, bukan intent
        SharedPreferences sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);
        currentUserEmail = sharedPreferences.getString("email", null);

        if (currentUserEmail != null) {
            // Ambil data user dari database berdasarkan email
            User user = dbHelper.getUserByEmail(currentUserEmail);

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
        } else {
            Toast.makeText(this, "User belum login, silakan login dulu", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ProfilActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        // Klik ke Card History
        ImageView cardHistory = findViewById(R.id.history);
        cardHistory.setOnClickListener(v -> {
            Intent intent = new Intent(ProfilActivity.this, HistoryActivity.class);
            intent.putExtra("user_email", currentUserEmail);
            startActivity(intent);
        });

        // Klik ke VoucherActivity (ditambahkan)
        ImageView voucherIcon = findViewById(R.id.voucher); // Pastikan id voucher ada di XML
        voucherIcon.setOnClickListener(v -> {
            Intent intent = new Intent(ProfilActivity.this, VoucherActivity.class);
            intent.putExtra("user_email", currentUserEmail); // pastikan currentUserEmail sudah benar
            startActivity(intent);

        });

        // Klik back --> ke HomeActivity
        back.setOnClickListener(view -> {
            Intent intent = new Intent(ProfilActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });
        // Klik home --> ke HomeActivity
        ImageView home = findViewById(R.id.home); // Pastikan id voucher ada di XML
        home.setOnClickListener(view -> {
            Intent intent = new Intent(ProfilActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });

    }
}
