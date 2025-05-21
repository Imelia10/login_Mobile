package com.example.rewear_app1;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class DompetActivity extends AppCompatActivity {

    private TextView tvSaldo;
    private DatabaseHelperDompet dbHelper;
    private String userId; // Akan di-set di onCreate

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dompet);

        // Ambil data userId dari intent
        String userIdString = getIntent().getStringExtra("user_id");
        int userId = -1;
        if (userIdString != null && !userIdString.isEmpty()) {
            userId = Integer.parseInt(userIdString);
        }

        // Inisialisasi
        tvSaldo = findViewById(R.id.saldo);
        dbHelper = new DatabaseHelperDompet(this);

        // Ambil saldo user dari database
        double saldo = dbHelper.getSaldo(userId);

        // Format saldo dan tampilkan
        String saldoFormatted = "Rp " + String.format("%,.0f", saldo).replace(',', '.');
        tvSaldo.setText(saldoFormatted);
    }
}



//package com.example.rewear_app1;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//public class DompetActivity extends AppCompatActivity {
//
//    private DatabaseHelperDompet dbHelperDompet;
//    private DatabaseHelper dbHelperUser;
//
//    private TextView tvTotalSaldo;
//    private ImageView backBtn;
//    private String currentUserEmail;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_dompet);
//
//        // Inisialisasi view
//        tvTotalSaldo = findViewById(R.id.saldo);
//        backBtn = findViewById(R.id.back);
//
//        // Inisialisasi database helpers
//        dbHelperDompet = new DatabaseHelperDompet(this);
//        dbHelperUser = new DatabaseHelper(this);
//
//        // Dapatkan email user dari intent
//        currentUserEmail = getIntent().getStringExtra("user_email");
//        if (currentUserEmail == null) {
//            Log.e("DompetActivity", "Email user tidak ditemukan");
//            finish();
//            return;
//        }
//
//        // Hitung dan tampilkan saldo
//        updateSaldoDisplay();
//
//        // Tombol kembali ke Home
//        backBtn.setOnClickListener(v -> {
//            Intent intent = new Intent(DompetActivity.this, HomeActivity.class);
//            intent.putExtra("user_email", currentUserEmail);
//            startActivity(intent);
//            finish();
//        });
//    }
//
//    private void updateSaldoDisplay() {
//        // Dapatkan ID user dari email
//        int userId = dbHelperUser.getUserIdByEmail(currentUserEmail);
//        if (userId == -1) {
//            Log.e("DompetActivity", "User ID tidak ditemukan untuk email: " + currentUserEmail);
//            tvTotalSaldo.setText("Rp 0");
//            return;
//        }
//
//        // Dapatkan saldo dari dompet
//        double saldo = dbHelperDompet.getSaldo(userId);
//        tvTotalSaldo.setText(formatRupiah(saldo));
//
//        Log.d("DompetActivity", "Saldo untuk user " + userId + ": " + saldo);
//    }
//
//    private String formatRupiah(double nilai) {
//        return "Rp " + String.format("%,.0f", nilai).replace(",", ".");
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        // Tutup koneksi database
//        if (dbHelperDompet != null) {
//            dbHelperDompet.close();
//        }
//        if (dbHelperUser != null) {
//            dbHelperUser.close();
//        }
//    }
//}






//
//
//package com.example.rewear_app1;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.ImageView;
//import android.widget.TextView;
//import androidx.appcompat.app.AppCompatActivity;
//
//public class DompetActivity extends AppCompatActivity {
//
//    private DatabaseHelperDompet dbHelperDompet;
//    private DatabaseHelper dbHelperUser;
//    private TextView tvTotalSaldo;
//    private ImageView backBtn;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_dompet);
//
//        // Initialize views
//        tvTotalSaldo = findViewById(R.id.saldo);
//        backBtn = findViewById(R.id.back);
//
//        // Initialize database helpers
//        dbHelperDompet = new DatabaseHelperDompet(this);
//        dbHelperUser = new DatabaseHelper(this);
//
//        // Get current user email from intent
//        String userEmail = getIntent().getStringExtra("user_email");
//        if (userEmail == null) {
//            Log.e("DompetActivity", "No user email found in intent");
//            finish();
//            return;
//        }
//
//        // Get user ID from email
//        int userId = dbHelperUser.getUserIdByEmail(userEmail);
//        if (userId == -1) {
//            Log.e("DompetActivity", "User not found for email: " + userEmail);
//            tvTotalSaldo.setText("Rp 0");
//        } else {
//            // Get and display balance
//            double balance = dbHelperDompet.getSaldo(userId);
//            tvTotalSaldo.setText(formatRupiah(balance));
//            Log.d("DompetActivity", "Balance for user " + userId + ": " + balance);
//        }
//
//        // Set up back button
//        backBtn.setOnClickListener(v -> {
//            Intent intent = new Intent(DompetActivity.this, HomeActivity.class);
//            intent.putExtra("user_email", userEmail);
//            startActivity(intent);
//            finish();
//        });
//    }
//
//    private String formatRupiah(double value) {
//        return "Rp " + String.format("%,.0f", value).replace(",", ".");
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        // Close database connections
//        if (dbHelperDompet != null) {
//            dbHelperDompet.close();
//        }
//        if (dbHelperUser != null) {
//            dbHelperUser.close();
//        }
//    }
//}