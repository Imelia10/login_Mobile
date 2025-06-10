package com.example.rewear_app1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class PembayaranBerhasilActivity extends AppCompatActivity {

    private static final String TAG = "PembayaranBerhasil";
    private DatabaseHelperTransaksi dbHelperTransaksi;
    private DatabaseHelperProduk dbHelperProduk;
    private DatabaseHelper dbHelperUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pembayaran_berhasil);

        // Initialize database helpers
        initializeDatabaseHelpers();

        // Get transaction data
        Transaksi transaksi = getTransactionData();
        if (transaksi == null) {
            finish();
            return;
        }

        // Get product data
        Produk produk = getProductData(transaksi);
        if (produk == null) {
            finish();
            return;
        }

        // Ambil data penting
        int penjualId;
        try {
            penjualId = Integer.parseInt(produk.getIdPenjual());
        } catch (NumberFormatException e) {
            Log.e(TAG, "Invalid penjualId format", e);
            Toast.makeText(this, "Data penjual tidak valid", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        int pembeliId = getCurrentUserId();
        String tanggal = transaksi.getTanggal();

        int harga;
        try {
            String hargaStr = produk.getHarga().replaceAll("[^\\d]", "");
            harga = Integer.parseInt(hargaStr);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Invalid harga format", e);
            Toast.makeText(this, "Data harga produk tidak valid", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        // Display transaction details
        displayTransactionDetails(transaksi, produk);

        // Update product and transaction status
        updateStatuses(transaksi, produk);

        // Setup finish button
        setupFinishButton(produk);
    }

    private void initializeDatabaseHelpers() {
        dbHelperTransaksi = new DatabaseHelperTransaksi(this);
        dbHelperProduk = new DatabaseHelperProduk(this);
        dbHelperUser = new DatabaseHelper(this);
    }

    private Transaksi getTransactionData() {
        int transaksiId = getIntent().getIntExtra("transaksi_id", -1);
        if (transaksiId == -1) {
            Toast.makeText(this, "Transaction ID not found", Toast.LENGTH_SHORT).show();
            return null;
        }

        Transaksi transaksi = dbHelperTransaksi.getTransaksiById(transaksiId);
        if (transaksi == null) {
            Toast.makeText(this, "Transaction data not found", Toast.LENGTH_SHORT).show();
        }
        return transaksi;
    }

    private Produk getProductData(Transaksi transaksi) {
        Produk produk = dbHelperProduk.getProdukById(transaksi.getIdProduk());
        if (produk == null) {
            Toast.makeText(this, "Product data not found", Toast.LENGTH_SHORT).show();
        }
        return produk;
    }

    private void displayTransactionDetails(Transaksi transaksi, Produk produk) {
        try {
            TextView etBarang = findViewById(R.id.etBarang);
            TextView etHarga = findViewById(R.id.etHarga);
            TextView etAlamat = findViewById(R.id.etAlamat);
            TextView etMetode = findViewById(R.id.etMetode);
            TextView tvTanggalPesanan = findViewById(R.id.tvTanggalPesanan);
            TextView tvTotal = findViewById(R.id.tvTotal);
            TextView tvDeliveryCharge = findViewById(R.id.tvDeliveryCharge);
            TextView tvDiscount = findViewById(R.id.tvDiscount);
            TextView totalHarga = findViewById(R.id.totalHarga);

            etBarang.setText(produk.getNama());
            etHarga.setText(formatRupiah(Double.parseDouble(produk.getHarga())));
            etAlamat.setText(transaksi.getAlamat());
            etMetode.setText(transaksi.getMetodePembayaran());
            tvTanggalPesanan.setText(transaksi.getTanggal());
            tvTotal.setText(formatRupiah(Double.parseDouble(produk.getHarga())));
            tvDeliveryCharge.setText(formatRupiah(transaksi.getOngkir()));
            tvDiscount.setText(formatRupiah(transaksi.getDiskon()));
            totalHarga.setText(formatRupiah(transaksi.getTotal()));
        } catch (NumberFormatException e) {
            Log.e(TAG, "Error formatting price values", e);
            Toast.makeText(this, "Error displaying transaction details", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateStatuses(Transaksi transaksi, Produk produk) {
        if (!dbHelperProduk.hapusProduk(produk.getId())) {
            Log.w(TAG, "Failed to soft delete product");
        }

        if (!dbHelperTransaksi.updateStatusTransaksi(transaksi.getId(), "completed")) {
            Log.w(TAG, "Failed to update transaction status");
        }
    }

    private void setupFinishButton(Produk produk) {
        ImageView btnSelesai = findViewById(R.id.btnSelesai);
        btnSelesai.setOnClickListener(v -> {
            Intent intent = new Intent(PembayaranBerhasilActivity.this, TransaksiActivity.class);
            intent.putExtra("kategori_terpilih", produk.getKategori());
            startActivity(intent);
            finish();
        });
    }

    private String formatRupiah(double nilai) {
        try {
            return "Rp " + String.format("%,.0f", nilai).replace(",", ".");
        } catch (Exception e) {
            Log.e(TAG, "Error formatting currency", e);
            return "Rp 0";
        }
    }

    // Ganti dengan ID user yang sedang login
    private int getCurrentUserId() {
        return 1;
    }

    private String getCurrentDate() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new java.util.Date());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (dbHelperTransaksi != null) dbHelperTransaksi.close();
            if (dbHelperProduk != null) dbHelperProduk.close();
            if (dbHelperUser != null) dbHelperUser.close();
        } catch (Exception e) {
            Log.e(TAG, "Error closing database connections", e);
        }
    }
}

