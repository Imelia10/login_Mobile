package com.example.rewear_app1;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private DatabaseHelperTransaksi dbHelperTransaksi;
    private DatabaseHelperProduk dbHelperProduk;
    private LinearLayout historyContainer;

    private String currentUserEmail;

    // ImageView navigasi
    private ImageView backBtn, fabCustom, fabCustom1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        historyContainer = findViewById(R.id.historyContainer);
        dbHelperTransaksi = new DatabaseHelperTransaksi(this);
        dbHelperProduk = new DatabaseHelperProduk(this);

        backBtn = findViewById(R.id.back);
        fabCustom = findViewById(R.id.fab_custom);
        fabCustom1 = findViewById(R.id.fab_custom1);

        // Ambil email user dari Intent
        currentUserEmail = getIntent().getStringExtra("user_email");

        // Load data history transaksi
        loadTransactionHistory();

        // Navigasi ke HomeActivity
        backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(HistoryActivity.this, HomeActivity.class);
            intent.putExtra("user_email", currentUserEmail);
            startActivity(intent);
            finish();
        });

        // Klik logo tengah ke HomeActivity
        fabCustom.setOnClickListener(v -> {
            Intent intent = new Intent(HistoryActivity.this, HomeActivity.class);
            intent.putExtra("user_email", currentUserEmail);
            startActivity(intent);
            finish();
        });

        // Klik profil ke ProfilActivity
        fabCustom1.setOnClickListener(v -> {
            Intent intent = new Intent(HistoryActivity.this, ProfilActivity.class);
            intent.putExtra("email", currentUserEmail);
            startActivity(intent);
        });
    }

    private void loadTransactionHistory() {
        if (currentUserEmail == null || currentUserEmail.isEmpty()) {
            TextView emptyView = new TextView(this);
            emptyView.setText("User belum login.");
            emptyView.setTextSize(16);
            emptyView.setPadding(16, 16, 16, 16);
            historyContainer.removeAllViews();
            historyContainer.addView(emptyView);
            return;
        }

        List<Transaksi> transactions = dbHelperTransaksi.getAllTransaksiUser(currentUserEmail);
        historyContainer.removeAllViews();

        if (transactions.isEmpty()) {
            TextView emptyView = new TextView(this);
            emptyView.setText("Tidak ada riwayat transaksi.");
            emptyView.setTextSize(16);
            emptyView.setPadding(16, 16, 16, 16);
            historyContainer.addView(emptyView);
            return;
        }

        for (Transaksi transaction : transactions) {
            addTransactionToHistory(transaction);
        }
    }

    private void addTransactionToHistory(Transaksi transaction) {
        // Use the new method that can retrieve deleted products
        Produk product = dbHelperProduk.getProdukForHistory(transaction.getIdProduk());
        if (product == null) return;

        LayoutInflater inflater = LayoutInflater.from(this);
        View cardView = inflater.inflate(R.layout.item_history_card, historyContainer, false);

        ImageView productImage = cardView.findViewById(R.id.productImage);
        TextView tvNamaBarang = cardView.findViewById(R.id.tvNamaBarang);
        TextView tvDate = cardView.findViewById(R.id.tvDate);
        TextView tvAmount = cardView.findViewById(R.id.tvAmount);

        // Set nama barang dan tanggal + total
        tvNamaBarang.setText(product.getNama());
        tvDate.setText(transaction.getTanggal());
        tvAmount.setText(formatRupiah(transaction.getTotal()));

        // Tampilkan gambar produk jika ada URI-nya
        String uriString = product.getGambarUri();
        if (uriString != null && !uriString.isEmpty()) {
            try {
                Uri imageUri = Uri.parse(uriString);
                productImage.setImageURI(imageUri);
            } catch (Exception e) {
                productImage.setImageResource(R.drawable.dress); // fallback
            }
        } else {
            productImage.setImageResource(R.drawable.dress); // fallback image
        }

        historyContainer.addView(cardView);
    }

    private String formatRupiah(double nilai) {
        return "Rp " + String.format("%,.0f", nilai).replace(",", ".");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelperTransaksi != null) dbHelperTransaksi.close();
        if (dbHelperProduk != null) dbHelperProduk.close();
    }
}