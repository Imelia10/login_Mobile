package com.example.rewear_app1;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class VoucherActivity extends AppCompatActivity {

    private LinearLayout voucherList;
    private DatabaseHelper dbHelper;
    private DatabaseHelperTransaksi dbHelperTransaksi;
    private DatabaseHelperVoucher dbHelperVoucher;
    private String currentUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher);

        initViews();
        initDatabase();
        getCurrentUserEmail();
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

        boolean canClaim = checkVoucherRequirements(syarat) && !dbHelperVoucher.isVoucherClaimed(currentUserEmail, id);

        if (canClaim) {
            btnVoucher.setText("Klaim");
            btnVoucher.setBackgroundTintList(getResources().getColorStateList(R.color.red));
            btnVoucher.setOnClickListener(v -> claimVoucher(id, kode, btnVoucher));
        } else {
            btnVoucher.setText(dbHelperVoucher.isVoucherClaimed(currentUserEmail, id) ? "Sudah Diklaim" : "Tidak Memenuhi Syarat");
            btnVoucher.setEnabled(false);
            btnVoucher.setBackgroundTintList(getResources().getColorStateList(android.R.color.darker_gray));
        }

        isi.addView(tvJudul);
        isi.addView(tvSyarat);
        isi.addView(tvKode);
        isi.addView(btnVoucher);
        cardView.addView(isi);
        voucherList.addView(cardView);
    }

    private void claimVoucher(int voucherId, String kodeVoucher, Button btnVoucher) {
        boolean success = dbHelperVoucher.addClaimedVoucher(currentUserEmail, voucherId, kodeVoucher);

        if (success) {
            Toast.makeText(this, "Voucher berhasil diklaim!", Toast.LENGTH_SHORT).show();
            btnVoucher.setText("Sudah Diklaim");
            btnVoucher.setEnabled(false);
            btnVoucher.setBackgroundTintList(getResources().getColorStateList(android.R.color.darker_gray));
        } else {
            Toast.makeText(this, "Gagal mengklaim voucher", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkVoucherRequirements(String syarat) {
        if (currentUserEmail == null || currentUserEmail.isEmpty()) {
            return false;
        }

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