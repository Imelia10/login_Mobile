package com.example.rewear_app1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class DetailPesanan extends AppCompatActivity {

    private ImageView backIcon, btnBatal, btnBayar, gambarProduk;
    private TextView namaProduk, hargaProduk, alamatPengiriman, metodePembayaran,
            tanggalPesanan, ongkir, discount, totalHarga, hargaProdukRincian;
    private String currentUserEmail;
    private Produk produk;
    private Transaksi transaksi;
    private DatabaseHelperProduk dbHelperProduk;
    private DatabaseHelperTransaksi dbHelperTransaksi;
    private DatabaseHelper dbHelperUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_pesanan);

        initViews();
        dbHelperProduk = new DatabaseHelperProduk(this);
        dbHelperTransaksi = new DatabaseHelperTransaksi(this);
        dbHelperUser = new DatabaseHelper(this);
        getCurrentUserEmail();
        loadData();
        setupButtons();
    }

    private void initViews() {
        backIcon = findViewById(R.id.back_icon);
        btnBatal = findViewById(R.id.batal);
        btnBayar = findViewById(R.id.bayar);
        gambarProduk = findViewById(R.id.gambar_produk);

        namaProduk = findViewById(R.id.nama_produk);
        hargaProduk = findViewById(R.id.harga_produk);
        alamatPengiriman = findViewById(R.id.alamat_pengiriman);
        metodePembayaran = findViewById(R.id.metode_pembayaran);
        tanggalPesanan = findViewById(R.id.tanggal_pesanan);
        ongkir = findViewById(R.id.ongkir);
        discount = findViewById(R.id.discount);
        totalHarga = findViewById(R.id.total_harga);
        hargaProdukRincian = findViewById(R.id.harga_produk_rincian);
    }

    private void getCurrentUserEmail() {
        SharedPreferences preferences = getSharedPreferences("user_session", MODE_PRIVATE);
        currentUserEmail = preferences.getString("email", "");
    }

    private void loadData() {
        User currentUser = dbHelperUser.getUserByEmail(currentUserEmail);
        if (currentUser == null || currentUser.getAlamat() == null) {
            Toast.makeText(this, "Alamat pengiriman tidak ditemukan", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        int produkId = getIntent().getIntExtra("produk_id", -1);
        String gambarUri = getIntent().getStringExtra("produk_gambar_uri");

        if (produkId != -1) {
            produk = dbHelperProduk.getProdukById(produkId);
            if (produk == null) {
                Toast.makeText(this, "Produk tidak ditemukan", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            if (gambarUri != null && !gambarUri.isEmpty()) {
                try {
                    gambarProduk.setImageURI(Uri.parse(gambarUri));
                } catch (Exception e) {
                    gambarProduk.setImageResource(R.drawable.profil1);
                }
            } else {
                gambarProduk.setImageResource(R.drawable.profil1);
            }

            // Set transaksi dengan diskon 0
            transaksi = new Transaksi();
            transaksi.setIdProduk(produkId);
            transaksi.setEmailPembeli(currentUserEmail);
            transaksi.setTanggal(getCurrentDate());
            transaksi.setAlamat(currentUser.getAlamat());
            transaksi.setMetodePembayaran("COD");
            transaksi.setOngkir(15000);
            transaksi.setDiskon(0); // Diskon di-set 0
            transaksi.setTotal(Double.parseDouble(produk.getHarga()) + transaksi.getOngkir());
        } else {
            transaksi = dbHelperTransaksi.getTransaksiTerakhir(currentUserEmail);
            if (transaksi == null) {
                Toast.makeText(this, "Tidak ada data transaksi", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            produk = dbHelperProduk.getProdukById(transaksi.getIdProduk());
            if (produk == null) {
                Toast.makeText(this, "Data produk tidak valid", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            if (produk.getGambarUri() != null && !produk.getGambarUri().isEmpty()) {
                try {
                    gambarProduk.setImageURI(Uri.parse(produk.getGambarUri()));
                } catch (Exception e) {
                    gambarProduk.setImageResource(R.drawable.profil1);
                }
            } else {
                gambarProduk.setImageResource(R.drawable.profil1);
            }
        }

        displayData();
    }

    private void displayData() {
        // Tampilkan data produk
        namaProduk.setText(produk.getNama());
        hargaProduk.setText(formatRupiah(Double.parseDouble(produk.getHarga())));
        hargaProdukRincian.setText(formatRupiah(Double.parseDouble(produk.getHarga())));

        // Tampilkan info pengiriman
        alamatPengiriman.setText(transaksi.getAlamat());
        metodePembayaran.setText(transaksi.getMetodePembayaran());
        tanggalPesanan.setText(transaksi.getTanggal());

        // Tampilkan ongkir dan diskon (0)
        ongkir.setText(formatRupiah(transaksi.getOngkir()));
        discount.setText(formatRupiah(0)); // Selalu tampilkan Rp 0

        // Hitung total: harga produk + ongkir
        double total = Double.parseDouble(produk.getHarga()) + transaksi.getOngkir();
        totalHarga.setText(formatRupiah(total));
    }

    private String formatRupiah(double nilai) {
        return "Rp " + String.format("%,.0f", nilai).replace(",", ".");
    }

    private String getCurrentDate() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd MMMM yyyy", java.util.Locale.getDefault());
        return sdf.format(new java.util.Date());
    }

    private void setupButtons() {
        backIcon.setOnClickListener(v -> finish());

        btnBatal.setOnClickListener(v -> {
            if (transaksi.getId() > 0) {
                dbHelperTransaksi.deleteTransaksi(transaksi.getId());
                Toast.makeText(this, "Pesanan dibatalkan", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Transaksi belum diproses", Toast.LENGTH_SHORT).show();
            }
        });

        btnBayar.setOnClickListener(v -> {
            if (transaksi.getId() == 0) {
                long id = dbHelperTransaksi.addTransaksi(transaksi);
                if (id != -1) {
                    transaksi.setId((int) id);
                    Toast.makeText(this, "Pembayaran berhasil", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(this, PembayaranBerhasilActivity.class);
                    intent.putExtra("transaksi_id", transaksi.getId());
                    startActivity(intent);
                    finish();
                }
            } else {
                Toast.makeText(this, "Pembayaran berhasil", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, PembayaranBerhasilActivity.class);
                intent.putExtra("transaksi_id", transaksi.getId());
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelperProduk != null) dbHelperProduk.close();
        if (dbHelperTransaksi != null) dbHelperTransaksi.close();
        if (dbHelperUser != null) dbHelperUser.close();
    }
}