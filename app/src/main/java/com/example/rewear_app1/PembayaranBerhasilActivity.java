package com.example.rewear_app1;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PembayaranBerhasilActivity extends AppCompatActivity {

    EditText etBarang, etHarga, etAlamat, etMetode;
    TextView tvTanggalPesanan, tvTipePengantaran, tvAmount, tvDeliveryCharge, tvTax, tvDiscount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pembayaran_berhasil);

        // Inisialisasi view
        etBarang = findViewById(R.id.etBarang);
        etHarga = findViewById(R.id.etHarga);
        etAlamat = findViewById(R.id.etAlamat);
        etMetode = findViewById(R.id.etMetode);
        tvTanggalPesanan = findViewById(R.id.tvTanggalPesanan);
        tvTipePengantaran = findViewById(R.id.tvTipePengantaran);
        tvAmount = findViewById(R.id.tvTotal);
        tvDeliveryCharge = findViewById(R.id.tvDeliveryCharge);
        tvDiscount = findViewById(R.id.discount);

        // Contoh data, bisa diganti dari intent/database
        String namaBarang = "Dress";
        String hargaBarang = "Rp 50.000";
        String alamatPengiriman = "Rungkut Asri Timur, SBY";
        String metodePembayaran = "COD (Cash On Delivery)";
        String tanggalPesanan = "28 Juni 2025";
        String tipePengantaran = "Ekonomi";
        String amount = "Rp 50.000";
        String delivery = "Rp 15.000";
        String tax = "Rp 5.000";
        String discount = "Rp 0";

        // Set data ke view
        etBarang.setText(namaBarang);
        etHarga.setText(hargaBarang);
        etAlamat.setText(alamatPengiriman);
        etMetode.setText(metodePembayaran);
        tvTanggalPesanan.setText(tanggalPesanan);
        tvTipePengantaran.setText(tipePengantaran);
        tvAmount.setText(amount);
        tvDeliveryCharge.setText(delivery);
        tvTax.setText(tax);
        tvDiscount.setText(discount);
    }
}
