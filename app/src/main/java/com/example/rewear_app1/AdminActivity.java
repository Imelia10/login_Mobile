package com.example.rewear_app1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class AdminActivity extends AppCompatActivity {

    private TextView userCountTextView;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        userCountTextView = findViewById(R.id.userCount);
        dbHelper = new DatabaseHelper(this);

        int userCount = dbHelper.getUserCount();
        userCountTextView.setText("Jumlah Pengguna: " + userCount);

        CardView cardUser = findViewById(R.id.cardUser);
        cardUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("AdminActivity", "CardView clicked!");
                Intent intent = new Intent(AdminActivity.this, AdminUserActivity.class);
                startActivity(intent);
            }
        });

        CardView cardVoucher = findViewById(R.id.cardVoucher);
        cardVoucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // startActivity(new Intent(AdminActivity.this, VoucherActivity.class));
            }
        });

        // Membuat card edukasi bisa diklik
        CardView cardEdukasi = findViewById(R.id.cardEdukasi);
        cardEdukasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Membuka halaman KelolaEdukasiActivity saat card diklik
                Intent intent = new Intent(AdminActivity.this, KelolaEdukasiActivity.class);
                startActivity(intent);
            }
        });
    }
}
