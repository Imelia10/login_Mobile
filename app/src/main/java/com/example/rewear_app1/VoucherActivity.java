package com.example.rewear_app1;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class VoucherActivity extends AppCompatActivity {

    private LinearLayout voucherList;
    private ImageView  back;

    private DatabaseHelper dbHelper;
    private DatabaseHelperTransaksi dbHelperTransaksi;
    private DatabaseHelperVoucher dbHelperVoucher;
    private String currentUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher);

        back = findViewById(R.id.back); // inisialisasi tombol back

        // Set klik listener untuk kembali ke HomeActivity
        back.setOnClickListener(view -> {
            Intent intent = new Intent(VoucherActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });

        initViews();
        initDatabase();
        getCurrentUserEmail();
        showFirstTimeDialog();
        loadVouchers();
    }


    private void initViews() {
        voucherList = findViewById(R.id.voucherList);
    }

    private void initDatabase() {
        dbHelper = new DatabaseHelper(this);
        dbHelperTransaksi = new DatabaseHelperTransaksi(this);
        dbHelperVoucher = new DatabaseHelperVoucher(this);
    }

    private void getCurrentUserEmail() {
        currentUserEmail = getIntent().getStringExtra("user_email");
        if (currentUserEmail == null || currentUserEmail.isEmpty()) {
            Toast.makeText(this, "Silakan login terlebih dahulu", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void showFirstTimeDialog() {
        SharedPreferences prefs = getSharedPreferences("voucher_prefs", MODE_PRIVATE);
        int visitCount = prefs.getInt("visit_count", 0) + 1;

        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("visit_count", visitCount);
        editor.apply();

        if (visitCount % 3 == 0) {
            new AlertDialog.Builder(this)
                    .setTitle("Perhatian")
                    .setMessage("Voucher tidak dapat digunakan pada transaksi Tukar Tambah.\n\nVoucher yang sudah diklaim tidak bisa diklaim ulang.")
                    .setPositiveButton("Mengerti", null)
                    .setCancelable(false)
                    .show();
        }
    }

    private void loadVouchers() {
        voucherList.removeAllViews();
        Cursor cursor = dbHelper.getAllVouchers();

        if (cursor.getCount() == 0) {
            showEmptyView("Tidak ada voucher tersedia");
            return;
        }

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String judul = cursor.getString(cursor.getColumnIndexOrThrow("judul"));
            String syarat = cursor.getString(cursor.getColumnIndexOrThrow("syarat"));
            String kode = cursor.getString(cursor.getColumnIndexOrThrow("kode"));

            tambahVoucher(id, judul, syarat, kode);
        }
        cursor.close();
    }

    private void showEmptyView(String message) {
        TextView emptyView = new TextView(this);
        emptyView.setText(message);
        emptyView.setTextSize(16);
        emptyView.setPadding(16, 16, 16, 16);
        voucherList.addView(emptyView);
    }

    private void tambahVoucher(int id, String judul, String syarat, String kode) {
        CardView cardView = new CardView(this);
        cardView.setRadius(25);
        cardView.setCardElevation(10);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 0, 0, 30);
        cardView.setLayoutParams(cardParams);

        LinearLayout isi = new LinearLayout(this);
        isi.setOrientation(LinearLayout.VERTICAL);
        isi.setPadding(30, 30, 30, 30);

        TextView tvJudul = new TextView(this);
        tvJudul.setText(judul);
        tvJudul.setTextSize(18);
        tvJudul.setPadding(0, 0, 0, 10);

        TextView tvSyarat = new TextView(this);
        tvSyarat.setText(syarat);
        tvSyarat.setTextSize(14);
        tvSyarat.setPadding(0, 0, 0, 20);

        TextView tvKode = new TextView(this);
        tvKode.setText("Kode: " + kode);
        tvKode.setTextSize(12);
        tvKode.setPadding(0, 0, 0, 20);

        Button btnVoucher = new Button(this);
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        btnVoucher.setLayoutParams(btnParams);

        boolean alreadyClaimed = dbHelperVoucher.isVoucherClaimed(currentUserEmail, id);
        boolean meetsRequirements = checkVoucherRequirements(syarat);

        if (alreadyClaimed) {
            btnVoucher.setText("Sudah Diklaim");
            btnVoucher.setEnabled(false);
            btnVoucher.setBackgroundTintList(getResources().getColorStateList(android.R.color.darker_gray));
        } else if (!meetsRequirements) {
            btnVoucher.setText("Tidak Memenuhi Syarat");
            btnVoucher.setEnabled(false);
            btnVoucher.setBackgroundTintList(getResources().getColorStateList(android.R.color.darker_gray));
        } else {
            btnVoucher.setText("Klaim");
            btnVoucher.setEnabled(true);
            btnVoucher.setBackgroundTintList(getResources().getColorStateList(R.color.red));
            btnVoucher.setOnClickListener(v -> {
                boolean success = dbHelperVoucher.addClaimedVoucher(currentUserEmail, id, kode);
                if (success) {
                    Toast.makeText(this, "Voucher berhasil diklaim!", Toast.LENGTH_SHORT).show();
                    loadVouchers(); // refresh tampilan
                } else {
                    Toast.makeText(this, "Gagal mengklaim voucher", Toast.LENGTH_SHORT).show();
                }
            });
        }

        isi.addView(tvJudul);
        isi.addView(tvSyarat);
        isi.addView(tvKode);
        isi.addView(btnVoucher);
        cardView.addView(isi);
        voucherList.addView(cardView);
    }
    private boolean checkVoucherRequirements(String syarat) {
        if (currentUserEmail == null || currentUserEmail.isEmpty()) return false;

        if (syarat.contains("Minimal belanja")) {
            try {
                String amountStr = syarat.replaceAll("[^0-9]", "");
                double minAmount = Double.parseDouble(amountStr);
                double userTotal = dbHelperTransaksi.getTotalBelanjaUser(currentUserEmail);
                return userTotal >= minAmount;
            } catch (NumberFormatException e) {
                return false;
            }
        } else if (syarat.contains("Minimal") && syarat.contains("transaksi")) {
            try {
                String countStr = syarat.replaceAll("[^0-9]", "");
                int minCount = Integer.parseInt(countStr);
                int userCount = dbHelperTransaksi.getJumlahTransaksiUser(currentUserEmail);
                return userCount >= minCount;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        return false;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) dbHelper.close();
        if (dbHelperTransaksi != null) dbHelperTransaksi.close();
        if (dbHelperVoucher != null) dbHelperVoucher.close();
    }
}
