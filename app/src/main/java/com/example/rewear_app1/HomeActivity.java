package com.example.rewear_app1;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rewear_app1.presentation.login.view.LoginActivity;

public class HomeActivity extends AppCompatActivity {

    TextView hiTextView;
    ImageView profilImageView, logoutImageView;
    DatabaseHelper dbHelper;
    String currentUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        hiTextView = findViewById(R.id.hi);
        profilImageView = findViewById(R.id.profil1);
        logoutImageView = findViewById(R.id.ivChat);
        dbHelper = new DatabaseHelper(this);

        // Ambil data user dari SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);
        currentUserEmail = sharedPreferences.getString("email", "");
        String userId = sharedPreferences.getString("user_id", "");

        // Tampilkan nama depan user
        if (!currentUserEmail.isEmpty()) {
            User user = dbHelper.getUserByEmail(currentUserEmail);
            if (user != null) {
                hiTextView.setText("Hi, " + user.getFirstName());
            }
        }

        logoutImageView.setOnClickListener(v -> {
            new AlertDialog.Builder(HomeActivity.this)
                    .setTitle("Konfirmasi Logout")
                    .setMessage("Apakah Anda yakin ingin logout?")
                    .setPositiveButton("Ya", (dialog, which) -> {
                        // Hapus semua data di SharedPreferences (user_session)
                        SharedPreferences preferences = getSharedPreferences("user_session", MODE_PRIVATE);
                        preferences.edit().clear().apply();

                        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish(); // Tutup DetailActivity
                    })
                    .setNegativeButton("Batal", null) // Tidak lakukan apa-apa jika dibatalkan
                    .show();
        });

        // Klik ke ProfilActivity
        profilImageView.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ProfilActivity.class);
            intent.putExtra("email", currentUserEmail);
            startActivity(intent);
        });

        // Klik ke DompetActivity (ditambahkan)
//        ImageView dompetIcon = findViewById(R.id.icon_dompet); // Pastikan ada di XML
//        dompetIcon.setOnClickListener(v -> {
//            Intent intent = new Intent(HomeActivity.this, DompetActivity.class);
//            intent.putExtra("user_id", userId); // pastikan variabel userId terisi dengan benar
//            startActivity(intent);
//
//        });

        // Klik ke VoucherActivity (ditambahkan)
        ImageView voucherIcon = findViewById(R.id.voucher); // Pastikan id voucher ada di XML
        voucherIcon.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, VoucherActivity.class);
            intent.putExtra("user_email", currentUserEmail); // pastikan currentUserEmail sudah benar
            startActivity(intent);

        });


        // Klik ke Card Transaksi
        LinearLayout cardPusatTransaksi = findViewById(R.id.cardPusatTransaksi);
        cardPusatTransaksi.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, TransaksiActivity.class);
            startActivity(intent);
        });

        // Klik ke Card Edukasi
        LinearLayout cardEdukasi = findViewById(R.id.cardEdukasi);
        cardEdukasi.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, EdukasiUserActivity.class);
            startActivity(intent);
        });

        // Klik ke Card Upload Barang
        LinearLayout cardUploadBarang = findViewById(R.id.cardUploadBarang);
        cardUploadBarang.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, CardUploadBarangActivity.class);
            startActivity(intent);
        });

        // Klik ke Card History
        ImageView cardHistory = findViewById(R.id.cardHistory);
        cardHistory.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, HistoryActivity.class);
            intent.putExtra("user_email", currentUserEmail);
            startActivity(intent);
        });

        LinearLayout cardPengajuanTuta = findViewById(R.id.cardPengajuanTuta);
        cardPengajuanTuta.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, DaftarPengajuanTuTa.class);
            intent.putExtra("user_email", currentUserEmail); // kirim email user
            startActivity(intent);
        });
    }
}