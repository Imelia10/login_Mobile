package com.example.rewear_app1;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private static final String TAG = "HistoryActivity";

    private DatabaseHelperTransaksi dbHelperTransaksi;
    private DatabaseHelperProduk dbHelperProduk;
    private LinearLayout historyContainer;
    private String currentUserEmail;
    private ImageView backBtn, fabCustom, fabCustom1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        initViews();
        initDatabase();
        getCurrentUserEmail();
        loadTransactionHistory();
        setupNavigation();
    }

    private void initViews() {
        historyContainer = findViewById(R.id.historyContainer);
        backBtn = findViewById(R.id.back);
        fabCustom = findViewById(R.id.fab_custom);
        fabCustom1 = findViewById(R.id.fab_custom1);
    }

    private void initDatabase() {
        dbHelperTransaksi = new DatabaseHelperTransaksi(this);
        dbHelperProduk = new DatabaseHelperProduk(this);
    }

    private void getCurrentUserEmail() {
        currentUserEmail = getIntent().getStringExtra("user_email");
        if (currentUserEmail == null || currentUserEmail.isEmpty()) {
            showEmptyView("User belum login");
        }
    }

    private void loadTransactionHistory() {
        if (currentUserEmail == null || currentUserEmail.isEmpty()) return;

        List<Transaksi> transactions = dbHelperTransaksi.getAllTransaksiUser(currentUserEmail);
        historyContainer.removeAllViews();

        if (transactions.isEmpty()) {
            showEmptyView("Tidak ada riwayat transaksi");
            return;
        }

        for (Transaksi transaction : transactions) {
            addTransactionToHistory(transaction);
        }
    }

    private void addTransactionToHistory(Transaksi transaction) {
        Produk product = dbHelperProduk.getProdukForHistory(transaction.getIdProduk());
        if (product == null) return;

        View cardView = LayoutInflater.from(this).inflate(R.layout.item_history_card, historyContainer, false);

        ImageView productImage = cardView.findViewById(R.id.productImage);
        TextView tvNamaBarang = cardView.findViewById(R.id.tvNamaBarang);
        TextView tvDate = cardView.findViewById(R.id.tvDate);
        TextView tvAmount = cardView.findViewById(R.id.tvAmount);

        // Set text data
        tvNamaBarang.setText(product.getNama());
        tvDate.setText(transaction.getTanggal());
        tvAmount.setText(formatRupiah(transaction.getTotal()));

        // Load product image with better handling
        loadProductImage(productImage, product.getGambarUri());

        historyContainer.addView(cardView);
    }

    private void loadProductImage(ImageView imageView, String imageUris) {
        if (imageUris == null || imageUris.isEmpty()) {
            imageView.setImageResource(R.drawable.dress);
            return;
        }

        try {
            // Take the first image if multiple exist
            String firstImageUri = imageUris.split("\n")[0].trim();
            Uri uri = Uri.parse(firstImageUri);

            // Handle different URI types
            if (uri.getScheme() != null && uri.getScheme().equals("content")) {
                // For content URIs from FileProvider
                imageView.setImageURI(uri);
            } else if (uri.getScheme() != null && uri.getScheme().equals("file")) {
                // For file URIs, convert to content URI
                imageView.setImageURI(uri);
            } else {
                // For other URIs (http, https, etc.)
                imageView.setImageURI(uri);
            }

            Log.d(TAG, "Successfully loaded image: " + uri.toString());
        } catch (Exception e) {
            Log.e(TAG, "Error loading product image", e);
            imageView.setImageResource(R.drawable.dress);
        }
    }

    private void showEmptyView(String message) {
        TextView emptyView = new TextView(this);
        emptyView.setText(message);
        emptyView.setTextSize(16);
        emptyView.setPadding(16, 16, 16, 16);
        historyContainer.removeAllViews();
        historyContainer.addView(emptyView);
    }

    private void setupNavigation() {
        backBtn.setOnClickListener(v -> navigateToHome());
        fabCustom.setOnClickListener(v -> navigateToHome());
        fabCustom1.setOnClickListener(v -> {
            Intent intent = new Intent(HistoryActivity.this, ProfilActivity.class);
            intent.putExtra("email", currentUserEmail);
            startActivity(intent);
        });
    }

    private void navigateToHome() {
        Intent intent = new Intent(HistoryActivity.this, HomeActivity.class);
        intent.putExtra("user_email", currentUserEmail);
        startActivity(intent);
        finish();
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