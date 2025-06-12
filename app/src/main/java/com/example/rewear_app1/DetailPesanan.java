package com.example.rewear_app1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import java.io.File;

public class DetailPesanan extends AppCompatActivity {

    private static final String TAG = "DetailPesanan";

    private ImageView backIcon, btnBatal, btnBayar, gambarProduk;
    private TextView namaProduk, hargaProduk, alamatPengiriman, metodePembayaran,
            tanggalPesanan, ongkir, discount, totalHarga, hargaProdukRincian;

    private String currentUserEmail;
    private Produk produk;
    private Transaksi transaksi;

    private DatabaseHelperProduk dbHelperProduk;
    private DatabaseHelperTransaksi dbHelperTransaksi;
    private DatabaseHelper dbHelperUser;
    private DatabaseHelperVoucher dbHelperVoucher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_pesanan);

        initViews();
        initDatabaseHelpers();
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

    private void initDatabaseHelpers() {
        dbHelperVoucher = new DatabaseHelperVoucher(this);
        dbHelperProduk = new DatabaseHelperProduk(this);
        dbHelperTransaksi = new DatabaseHelperTransaksi(this);
        dbHelperUser = new DatabaseHelper(this);
    }

    private void getCurrentUserEmail() {
        SharedPreferences preferences = getSharedPreferences("user_session", MODE_PRIVATE);
        currentUserEmail = preferences.getString("email", "");
        if (currentUserEmail.isEmpty()) {
            Toast.makeText(this, "Silakan login kembali", Toast.LENGTH_SHORT).show();
            finish();
        }
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

            loadProductImage(gambarUri != null ? gambarUri : produk.getGambarUri());

            transaksi = new Transaksi();
            transaksi.setIdProduk(produkId);
            transaksi.setEmailPembeli(currentUserEmail);
            transaksi.setTanggal(getCurrentDate());
            transaksi.setAlamat(currentUser.getAlamat());
            transaksi.setMetodePembayaran("COD");
            transaksi.setOngkir(15000);
            transaksi.setDiskon(0); // default, akan diperbarui di displayData()
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

            loadProductImage(produk.getGambarUri());
        }

        displayData();
    }

    private void loadProductImage(String imageUris) {
        if (imageUris == null || imageUris.isEmpty()) {
            gambarProduk.setImageResource(R.drawable.profil1);
            return;
        }

        try {
            String firstImageUri = imageUris.split("\n")[0].trim();
            Uri uri = Uri.parse(firstImageUri);

            if ("content".equals(uri.getScheme())) {
                gambarProduk.setImageURI(uri);
            } else if ("file".equals(uri.getScheme())) {
                File file = new File(uri.getPath());
                Uri fileUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", file);
                gambarProduk.setImageURI(fileUri);
            } else {
                gambarProduk.setImageURI(uri);
            }

            Log.d(TAG, "Successfully loaded image: " + uri.toString());
        } catch (Exception e) {
            Log.e(TAG, "Error loading product image", e);
            gambarProduk.setImageResource(R.drawable.profil1);
        }
    }

    private void displayData() {
        namaProduk.setText(produk.getNama());
        double hargaProdukDouble = Double.parseDouble(produk.getHarga());
        hargaProduk.setText(formatRupiah(hargaProdukDouble));
        hargaProdukRincian.setText(formatRupiah(hargaProdukDouble));

        alamatPengiriman.setText(transaksi.getAlamat());
        metodePembayaran.setText(transaksi.getMetodePembayaran());
        tanggalPesanan.setText(transaksi.getTanggal());

        double ongkirDouble = transaksi.getOngkir();
        double diskon = cekDanTerapkanDiskon(hargaProdukDouble);
        transaksi.setDiskon(diskon);

        ongkir.setText(formatRupiah(ongkirDouble));
        discount.setText(formatRupiah(diskon));

        double total = hargaProdukDouble + ongkirDouble - diskon;
        transaksi.setTotal(total);
        totalHarga.setText(formatRupiah(total));
    }

    private double cekDanTerapkanDiskon(double hargaProduk) {
        int totalVoucherDiklaim = dbHelperVoucher.getJumlahVoucherYangDiklaim(currentUserEmail);
        int transaksiDiskonSebelumnya = dbHelperTransaksi.getJumlahTransaksiDenganDiskon(currentUserEmail);

        if (totalVoucherDiklaim <= transaksiDiskonSebelumnya) {
            return 0;
        }

        int jumlahTransaksi = dbHelperTransaksi.getAllTransaksiUser(currentUserEmail).size();
        double persen = 0;
        double maxDiskon = 0;

        if (jumlahTransaksi >= 10) {
            persen = 0.15;
            maxDiskon = 35000;
        } else if (jumlahTransaksi >= 6) {
            persen = 0.10;
            maxDiskon = 25000;
        } else if (jumlahTransaksi >= 3) {
            persen = 0.07;
            maxDiskon = 15000;
        } else if (jumlahTransaksi >= 2) {
            persen = 0.05;
            maxDiskon = 10000;
        }

        double potongan = hargaProduk * persen;
        return Math.min(potongan, maxDiskon);
    }




    private String formatRupiah(double nilai) {
        return "Rp " + String.format("%,.0f", nilai).replace(",", ".");
    }

    private String getCurrentDate() {
        return new java.text.SimpleDateFormat("dd MMMM yyyy", java.util.Locale.getDefault())
                .format(new java.util.Date());
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
                    showPaymentSuccess();
                } else {
                    Toast.makeText(this, "Gagal memproses pembayaran", Toast.LENGTH_SHORT).show();
                }
            } else {
                showPaymentSuccess();
            }
        });
    }

    private void showPaymentSuccess() {
        Toast.makeText(this, "Pembayaran berhasil", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, PembayaranBerhasilActivity.class);
        intent.putExtra("transaksi_id", transaksi.getId());
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelperProduk != null) dbHelperProduk.close();
        if (dbHelperTransaksi != null) dbHelperTransaksi.close();
        if (dbHelperUser != null) dbHelperUser.close();
    }
}