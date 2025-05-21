//package com.example.rewear_app1;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.widget.ImageView;
//import android.widget.TextView;
//import androidx.appcompat.app.AppCompatActivity;
//
//public class PembayaranBerhasilActivity extends AppCompatActivity {
//
//    private DatabaseHelperTransaksi dbHelperTransaksi;
//    private DatabaseHelperProduk dbHelperProduk;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_pembayaran_berhasil);
//
//        // Inisialisasi database helper
//        dbHelperTransaksi = new DatabaseHelperTransaksi(this);
//        dbHelperProduk = new DatabaseHelperProduk(this);
//
//        // Ambil transaksi_id dari intent
//        int transaksiId = getIntent().getIntExtra("transaksi_id", -1);
//        if (transaksiId == -1) {
//            finish();
//            return;
//        }
//
//        // Ambil data transaksi dan produk
//        Transaksi transaksi = dbHelperTransaksi.getTransaksiById(transaksiId);
//        Produk produk = dbHelperProduk.getProdukById(transaksi.getIdProduk());
//
//        if (transaksi == null || produk == null) {
//            finish();
//            return;
//        }
//
//        // Inisialisasi view
//        TextView etBarang = findViewById(R.id.etBarang);
//        TextView etHarga = findViewById(R.id.etHarga);
//        TextView etAlamat = findViewById(R.id.etAlamat);
//        TextView etMetode = findViewById(R.id.etMetode);
//        TextView tvTanggalPesanan = findViewById(R.id.tvTanggalPesanan);
//        TextView tvTotal = findViewById(R.id.tvTotal);
//        TextView tvDeliveryCharge = findViewById(R.id.tvDeliveryCharge);
//        TextView tvDiscount = findViewById(R.id.tvDiscount);
//        TextView totalHarga = findViewById(R.id.totalHarga);
//
//        // Tampilkan data
//        etBarang.setText(produk.getNama());
//        etHarga.setText(formatRupiah(Double.parseDouble(produk.getHarga())));
//        etAlamat.setText(transaksi.getAlamat());
//        etMetode.setText(transaksi.getMetodePembayaran());
//        tvTanggalPesanan.setText(transaksi.getTanggal());
//        tvTotal.setText(formatRupiah(Double.parseDouble(produk.getHarga())));
//        tvDeliveryCharge.setText(formatRupiah(transaksi.getOngkir()));
//        tvDiscount.setText(formatRupiah(transaksi.getDiskon()));
//        totalHarga.setText(formatRupiah(transaksi.getTotal()));
//
//        // Update status produk menjadi "Dihapus" (soft delete)
//        dbHelperProduk.hapusProduk(produk.getId());
//
//        // Update status transaksi menjadi "completed"
//        dbHelperTransaksi.updateStatusTransaksi(transaksiId, "completed");
//
//        // Tombol selesai
//        ImageView btnSelesai = findViewById(R.id.btnSelesai);
//        btnSelesai.setOnClickListener(v -> {
//            Intent intent = new Intent(PembayaranBerhasilActivity.this, TransaksiActivity.class);
//            intent.putExtra("kategori_terpilih", produk.getKategori());
//            startActivity(intent);
//            finish();
//        });
//    }
//
//    private String formatRupiah(double nilai) {
//        return "Rp " + String.format("%,.0f", nilai).replace(",", ".");
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (dbHelperTransaksi != null) dbHelperTransaksi.close();
//        if (dbHelperProduk != null) dbHelperProduk.close();
//    }
//}
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
    private DatabaseHelperDompet dbHelperDompet;
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

        // Ambil data penting untuk insert ke dompet
        int penjualId = -1;
        try {
            penjualId = Integer.parseInt(produk.getIdPenjual());
        } catch (NumberFormatException e) {
            Log.e(TAG, "Invalid penjualId format", e);
            Toast.makeText(this, "Data penjual tidak valid", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        int pembeliId = getCurrentUserId(); // ganti dengan ambil ID user yang sedang login
        String tanggal = transaksi.getTanggal();

        // Ambil harga produk, pastikan format angka
        int harga = 0;
        try {
            String hargaStr = produk.getHarga().replaceAll("[^\\d]", "");
            harga = Integer.parseInt(hargaStr);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Invalid harga format", e);
            Toast.makeText(this, "Data harga produk tidak valid", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Insert transaksi ke dompet
        boolean insertSuccess = dbHelperDompet.insertTransaction(penjualId, pembeliId, harga, tanggal);
        if (!insertSuccess) {
            Log.e(TAG, "Gagal insert transaksi ke dompet");
            Toast.makeText(this, "Gagal menyimpan transaksi dompet", Toast.LENGTH_SHORT).show();
        }

        // Display transaction details
        displayTransactionDetails(transaksi, produk);

        // Update product and transaction status
        updateStatuses(transaksi, produk);

        // Process seller payment
        processSellerPayment(produk);

        // Setup finish button
        setupFinishButton(produk);
    }

    private void initializeDatabaseHelpers() {
        dbHelperTransaksi = new DatabaseHelperTransaksi(this);
        dbHelperProduk = new DatabaseHelperProduk(this);
        dbHelperDompet = new DatabaseHelperDompet(this);
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
        // Soft delete product
        if (!dbHelperProduk.hapusProduk(produk.getId())) {
            Log.w(TAG, "Failed to soft delete product");
        }

        // Update transaction status
        if (!dbHelperTransaksi.updateStatusTransaksi(transaksi.getId(), "completed")) {
            Log.w(TAG, "Failed to update transaction status");
        }
    }

    private void processSellerPayment(Produk produk) {
        try {
            int idPenjual = Integer.parseInt(produk.getIdPenjual());
            double hargaProduk = Double.parseDouble(produk.getHarga());

            // Gunakan DatabaseHelperDompet untuk update saldo
            boolean success = dbHelperDompet.tambahSaldo(idPenjual, hargaProduk);

            if (success) {
                Log.d(TAG, "Saldo penjual berhasil ditambah");
                // Catat transaksi
                dbHelperDompet.insertTransaction(idPenjual, getCurrentUserId(), hargaProduk, getCurrentDate());
            } else {
                Log.e(TAG, "Gagal menambah saldo penjual");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error processSellerPayment: " + e.getMessage());
        }
    }

    void setupFinishButton(Produk produk) {
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

    // Contoh dummy, harus diganti dengan session user yang sesungguhnya
    private int getCurrentUserId() {
        return 1;
    }

    // Contoh dummy tanggal sekarang
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
            if (dbHelperDompet != null) dbHelperDompet.close();
            if (dbHelperUser != null) dbHelperUser.close();
        } catch (Exception e) {
            Log.e(TAG, "Error closing database connections", e);
        }
    }
}


//    private void processSellerPayment(Produk produk) {
//        try {
//            String idPenjualStr = produk.getIdPenjual();
//            if (idPenjualStr == null || idPenjualStr.isEmpty()) {
//                Toast.makeText(this, "Seller ID is missing", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            // Ganti bagian parsing idPenjual dengan pengecekan lebih ketat
//            int idPenjual;
//            try {
//                idPenjual = Integer.parseInt(idPenjualStr.trim());
//                if (idPenjual <= 0) {
//                    throw new NumberFormatException("Invalid seller ID");
//                }
//            } catch (NumberFormatException e) {
//                Log.e(TAG, "Invalid seller ID: " + idPenjualStr, e);
//                Toast.makeText(this, "Invalid seller ID", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            // Ganti bagian parsing harga dengan penanganan error lebih baik
//            double hargaProduk;
//            try {
//                String hargaStr = produk.getHarga().replaceAll("[^\\d.]", ""); // Hapus karakter non-digit
//                hargaProduk = Double.parseDouble(hargaStr);
//                if (hargaProduk <= 0) {
//                    throw new NumberFormatException("Invalid price");
//                }
//            } catch (NumberFormatException e) {
//                Log.e(TAG, "Invalid product price: " + produk.getHarga(), e);
//                Toast.makeText(this, "Invalid product price", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            boolean success = dbHelperDompet.addIncomeFromSale(
//                    idPenjual,
//                    hargaProduk,
//                    produk.getId(),
//                    produk.getNama()
//            );
//
//            if (success) {
//                Toast.makeText(this, "Successfully added Rp" + hargaProduk + " to seller's balance",
//                        Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(this, "Failed to add balance to seller",
//                        Toast.LENGTH_SHORT).show();
//            }
//        } catch (Exception e) {
//            Log.e(TAG, "Error processing seller payment", e);
//            Toast.makeText(this, "Error processing payment", Toast.LENGTH_SHORT).show();
//        }
//    }


    // Di PembayaranBerhasilActivity, bagian processSellerPayment:
