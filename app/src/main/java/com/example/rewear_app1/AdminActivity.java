package com.example.rewear_app1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class AdminActivity extends AppCompatActivity {

    private TextView userCountTextView;
    private int userId;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        userCountTextView = findViewById(R.id.userCount);
        userId = getIntent().getIntExtra("USER_ID", -1);
        dbHelper = new DatabaseHelper(this);

        int userCount = dbHelper.getUserCount();
        userCountTextView.setText("Jumlah Pengguna: " + userCount);

        // Card untuk data pengguna
        CardView cardUser = findViewById(R.id.cardUser);
        cardUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("AdminActivity", "CardView clicked!");
                Intent intent = new Intent(AdminActivity.this, AdminUserActivity.class);
                startActivity(intent);
            }
        });

        // Card untuk edukasi
        CardView cardEdukasi = findViewById(R.id.cardEdukasi);
        cardEdukasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, KelolaEdukasiActivity.class);
                startActivity(intent);
            }
        });

        // RelativeLayout untuk kelola voucher
        RelativeLayout voucherLayout = findViewById(R.id.voucher);
        voucherLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, AdminVoucherActivity.class);
                intent.putExtra("USER_ID", userId);
                intent.putExtra("IS_ADMIN", true); // tambahkan ini agar isAdmin jadi true
                startActivity(intent);
            }

    });
    }
}
