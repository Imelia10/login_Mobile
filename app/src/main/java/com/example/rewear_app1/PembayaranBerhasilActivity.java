package com.example.rewear_app1;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class PembayaranBerhasilActivity extends AppCompatActivity {

    private DatabaseHelperTransaksi dbHelperTransaksi;
    private DatabaseHelperProduk dbHelperProduk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pembayaran_berhasil);

        // Initialize database helpers
        dbHelperTransaksi = new DatabaseHelperTransaksi(this);
        dbHelperProduk = new DatabaseHelperProduk(this);

        // Get transaction ID from intent
        int transaksiId = getIntent().getIntExtra("transaksi_id", -1);
        if (transaksiId == -1) {
            finish();
            return;
        }

        // Get transaction and product data
        Transaksi transaksi = dbHelperTransaksi.getTransaksiById(transaksiId);
        Produk produk = dbHelperProduk.getProdukById(transaksi.getIdProduk());

        if (transaksi == null || produk == null) {
            finish();
            return;
        }

        // Initialize views
        TextView etBarang = findViewById(R.id.etBarang);
        TextView etHarga = findViewById(R.id.etHarga);
        TextView etAlamat = findViewById(R.id.etAlamat);
        TextView etMetode = findViewById(R.id.etMetode);
        TextView tvTanggalPesanan = findViewById(R.id.tvTanggalPesanan);
        TextView tvTotal = findViewById(R.id.tvTotal);
        TextView tvDeliveryCharge = findViewById(R.id.tvDeliveryCharge);
        TextView tvDiscount = findViewById(R.id.tvDiscount);
        TextView totalHarga = findViewById(R.id.totalHarga);

        // Populate data
        etBarang.setText(produk.getNama());
        etHarga.setText(formatRupiah(Double.parseDouble(produk.getHarga())));
        etAlamat.setText(transaksi.getAlamat());
        etMetode.setText(transaksi.getMetodePembayaran());
        tvTanggalPesanan.setText(transaksi.getTanggal());
        tvTotal.setText(formatRupiah(Double.parseDouble(produk.getHarga())));
        tvDeliveryCharge.setText(formatRupiah(transaksi.getOngkir()));
        tvDiscount.setText(formatRupiah(transaksi.getDiskon()));
        totalHarga.setText(formatRupiah(transaksi.getTotal()));

        // Set up button
        ImageView btnSelesai = findViewById(R.id.btnSelesai);
        btnSelesai.setOnClickListener(v -> finish());
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