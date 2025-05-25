package com.example.rewear_app1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.*;

public class DetailTukarTambahActivity extends AppCompatActivity {

    private ImageView backIcon, imageBarang, lanjut;
    private TextView namaProduk, hargaProduk, tanggalPesanan, hargaRincian, hargaTambah, hargaProdukJual, ongkir, discount, totalHarga;
    private EditText namaBarangTukar, metodePembayaran;

    private DatabaseHelperProduk dbHelperProduk;
    private int produkId = -1;
    private Produk produk;

    private String namaBarangTukarStr, hargaTukarStr, keteranganTukar;
    private ArrayList<Uri> gambarTerpilih;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_tuta);

        dbHelperProduk = new DatabaseHelperProduk(this);

        initViews();
        getIntentData();

        if (produkId == -1) {
            Toast.makeText(this, "Produk tidak ditemukan", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadProductData();
        setupButtons();
    }

    private void initViews() {
        backIcon = findViewById(R.id.back_icon);
        imageBarang = findViewById(R.id.imageBarang);
        namaProduk = findViewById(R.id.nama_produk);
        hargaProduk = findViewById(R.id.harga_produk);
        namaBarangTukar = findViewById(R.id.nama_barang_tukar);
        metodePembayaran = findViewById(R.id.metode_pembayaran);
        tanggalPesanan = findViewById(R.id.tanggal_pesanan);
        hargaRincian = findViewById(R.id.harga_produk_rincian);
        hargaTambah = findViewById(R.id.harga_tambah);
        hargaProdukJual = findViewById(R.id.harga_produk_jual);
        ongkir = findViewById(R.id.ongkir);
        discount = findViewById(R.id.discount);
        totalHarga = findViewById(R.id.total_harga);
        lanjut = findViewById(R.id.lanjut);
    }

    private void getIntentData() {
        Intent intent = getIntent();
        produkId = intent.getIntExtra("produk_id", -1);
        namaBarangTukarStr = intent.getStringExtra("nama_barang_tukar");
        hargaTukarStr = intent.getStringExtra("harga_tukar");
        keteranganTukar = intent.getStringExtra("keterangan_tukar");

        // Handle gambar yang dikirim sebagai ArrayList<String>
        ArrayList<String> uriStrings = intent.getStringArrayListExtra("gambar_terpilih");
        gambarTerpilih = new ArrayList<>();
        if (uriStrings != null) {
            for (String uriString : uriStrings) {
                gambarTerpilih.add(Uri.parse(uriString));
            }
        }
    }

    private void loadProductData() {
        produk = dbHelperProduk.getProdukById(produkId);
        if (produk == null) {
            Toast.makeText(this, "Data produk tidak ditemukan", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Tampilkan nama produk yang ditukar di EditText (namabarangtukar)
        namaBarangTukar.setText(produk.getNama());

        // Tampilkan nama produk pengganti di TextView namaProduk
        if (namaBarangTukarStr != null && !namaBarangTukarStr.isEmpty()) {
            namaProduk.setText(namaBarangTukarStr);
        } else {
            namaProduk.setText("-");
        }

        // Harga produk asli
        hargaProduk.setText(formatRupiah(produk.getHarga()));

        // Gambar produk
        try {
            if (gambarTerpilih != null && !gambarTerpilih.isEmpty()) {
                imageBarang.setImageURI(gambarTerpilih.get(0));
            } else if (produk.getGambarUri() != null && !produk.getGambarUri().isEmpty()) {
                imageBarang.setImageURI(Uri.parse(produk.getGambarUri()));
            } else {
                imageBarang.setImageResource(R.drawable.dress);
            }
        } catch (Exception e) {
            imageBarang.setImageResource(R.drawable.dress);
        }

        // Metode pembayaran default
        metodePembayaran.setText("COD (Cash On Delivery)");

        // Tanggal hari ini
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", new Locale("id", "ID"));
        tanggalPesanan.setText(sdf.format(new Date()));

        calculatePrices();
    }

    private void calculatePrices() {
        try {
            int hargaProdukAsli = parseRupiahToInt(produk.getHarga());
            int hargaTukar = hargaTukarStr != null ? parseRupiahToInt(hargaTukarStr) : 0;
            int biayaKirim = 15000;
            int diskon = 0;

            int selisihHarga = hargaProdukAsli - hargaTukar;
            int total = Math.max(selisihHarga, 0) + biayaKirim - diskon;

            hargaRincian.setText(formatRupiah(String.valueOf(hargaTukar)));
            hargaTambah.setText(formatRupiah(String.valueOf(hargaProdukAsli)));
            hargaProdukJual.setText(formatRupiah(String.valueOf(Math.max(selisihHarga, 0))));
            ongkir.setText(formatRupiah(String.valueOf(biayaKirim)));
            discount.setText("-" + formatRupiah(String.valueOf(diskon)));
            totalHarga.setText(formatRupiah(String.valueOf(total)));

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Error dalam perhitungan harga", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private int parseRupiahToInt(String rupiah) {
        if (rupiah == null) return 0;
        return Integer.parseInt(rupiah.replaceAll("[^0-9]", ""));
    }

    private String formatRupiah(String value) {
        try {
            int number = parseRupiahToInt(value);
            return "Rp " + String.format(Locale.US, "%,d", number).replace(",", ".");
        } catch (NumberFormatException e) {
            return "Rp 0";
        }
    }

    private void setupButtons() {
        backIcon.setOnClickListener(v -> finish());

        lanjut.setOnClickListener(v -> kirimPengajuan());
    }


    private void kirimPengajuan() {
        String namaBarangTukarFinal = namaBarangTukar.getText().toString().trim();
        String namaProdukFinal = namaProduk.getText().toString().trim();
        String metodeBayar = metodePembayaran.getText().toString().trim();
        String tanggal = tanggalPesanan.getText().toString().trim();
        String hargaTukarFinal = hargaTukarStr != null ? hargaTukarStr : "0";

        // Ambil URI gambar dari database
        String gambarUriStr = saveImagesToStorage(gambarTerpilih);


        SharedPreferences sp = getSharedPreferences("user_session", MODE_PRIVATE);
        String emailPengaju = sp.getString("email_login", "");
        String emailPemilik = produk != null ? produk.getIdPenjual() : "";

        if (emailPengaju.isEmpty() || emailPemilik.isEmpty()) {
            Toast.makeText(this, "Data email tidak valid", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseHelperPengajuanTuta dbHelperPengajuan = new DatabaseHelperPengajuanTuta(this);
        boolean sukses = dbHelperPengajuan.tambahPengajuan(
                produkId,
                namaProdukFinal,
                namaBarangTukarFinal,
                hargaTukarFinal,
                metodeBayar,
                tanggal,
                gambarUriStr,
                emailPengaju,
                emailPemilik
        );

        Log.d("PengajuanTuta", "Insert result: " + sukses +
                ", EmailPengaju: " + emailPengaju +
                ", EmailPemilik: " + emailPemilik);

        if (sukses) {
            Toast.makeText(this, "Pengajuan berhasil dikirim", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, DaftarPengajuanTuTa.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Gagal mengirim pengajuan", Toast.LENGTH_SHORT).show();
        }
    }

    private String saveImagesToStorage(ArrayList<Uri> imageUris) {
        StringBuilder savedPaths = new StringBuilder();

        for (Uri uri : imageUris) {
            try {
                // Create a unique filename
                String fileName = "img_" + System.currentTimeMillis() + ".jpg";
                File destFile = new File(getFilesDir(), fileName);

                // Copy file
                InputStream in = getContentResolver().openInputStream(uri);
                OutputStream out = new FileOutputStream(destFile);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();

                // Log the saved file path
                Log.d("ImageSave", "Saved image to: " + destFile.getAbsolutePath());

                if (savedPaths.length() > 0) savedPaths.append(";");
                savedPaths.append(fileName); // Store just the filename
            } catch (Exception e) {
                Log.e("ImageSave", "Error saving image", e);
            }
        }

        Log.d("ImageSave", "Final saved paths: " + savedPaths.toString());
        return savedPaths.toString();
    }

}



//package com.example.rewear_app1;
//
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.content.SharedPreferences;
//import android.widget.*;
//import androidx.appcompat.app.AppCompatActivity;
//
//import java.text.SimpleDateFormat;
//import java.util.*;
//
//public class DetailTukarTambahActivity extends AppCompatActivity {
//
//    private ImageView backIcon, imageBarang, lanjut;
//    private TextView namaProduk, hargaProduk, tanggalPesanan, hargaRincian, hargaTambah, hargaProdukJual, ongkir, discount, totalHarga;
//    private EditText namaBarangTukar, metodePembayaran;
//
//    private DatabaseHelperProduk dbHelperProduk;
//    private int produkId;
//    private Produk produk;
//
//    private String namaBarangTukarStr, hargaTukarStr, keteranganTukar;
//    private ArrayList<Uri> gambarTerpilih;
//    private String emailPengaju, emailPemilik; // Add these fields
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_detail_tuta);
//
//        dbHelperProduk = new DatabaseHelperProduk(this);
//
//        initViews();
//        getIntentData();
//
//        if (produkId == -1) {
//            Toast.makeText(this, "Produk tidak ditemukan", Toast.LENGTH_SHORT).show();
//            finish();
//            return;
//        }
//
//        loadProductData();
//        setupButtons();
//    }
//
//    private void initViews() {
//        backIcon = findViewById(R.id.back_icon);
//        imageBarang = findViewById(R.id.imageBarang);
//        namaProduk = findViewById(R.id.nama_produk);
//        hargaProduk = findViewById(R.id.harga_produk);
//        namaBarangTukar = findViewById(R.id.nama_barang_tukar);
//        metodePembayaran = findViewById(R.id.metode_pembayaran);
//        tanggalPesanan = findViewById(R.id.tanggal_pesanan);
//        hargaRincian = findViewById(R.id.harga_produk_rincian);
//        hargaTambah = findViewById(R.id.harga_tambah);
//        hargaProdukJual = findViewById(R.id.harga_produk_jual);
//        ongkir = findViewById(R.id.ongkir);
//        discount = findViewById(R.id.discount);
//        totalHarga = findViewById(R.id.total_harga);
//        lanjut = findViewById(R.id.lanjut);
//    }
//
//    private void getIntentData() {
//        Intent intent = getIntent();
//        produkId = intent.getIntExtra("produk_id", -1);
//        namaBarangTukarStr = intent.getStringExtra("nama_barang_tukar");
//        hargaTukarStr = intent.getStringExtra("harga_tukar");
//        keteranganTukar = intent.getStringExtra("keterangan_tukar");
//        gambarTerpilih = intent.getParcelableArrayListExtra("gambar_terpilih");
//        emailPengaju = intent.getStringExtra("email_pengaju"); // Get from intent
//        emailPemilik = intent.getStringExtra("email_pemilik"); // Get from intent
//    }
//
//    private void loadProductData() {
//        produk = dbHelperProduk.getProdukById(produkId);
//        if (produk == null) {
//            Toast.makeText(this, "Data produk tidak ditemukan", Toast.LENGTH_SHORT).show();
//            finish();
//            return;
//        }
//
//        // Set nama barang tukar (produk asli)
//        namaBarangTukar.setText(produk.getNama());
//
//        // Set nama produk (barang tukar yang ditukar)
//        if (namaBarangTukarStr != null && !namaBarangTukarStr.isEmpty()) {
//            namaProduk.setText(namaBarangTukarStr);
//        } else {
//            namaProduk.setText("-");
//        }
//
//        // Set harga produk asli
//        hargaProduk.setText(formatRupiah(produk.getHarga()));
//
//        // Set gambar barang
//        try {
//            if (gambarTerpilih != null && !gambarTerpilih.isEmpty()) {
//                imageBarang.setImageURI(gambarTerpilih.get(0));
//            } else if (produk.getGambarUri() != null && !produk.getGambarUri().isEmpty()) {
//                imageBarang.setImageURI(Uri.parse(produk.getGambarUri()));
//            } else {
//                imageBarang.setImageResource(R.drawable.dress);
//            }
//        } catch (Exception e) {
//            imageBarang.setImageResource(R.drawable.dress);
//        }
//
//        // Set metode pembayaran default
//        metodePembayaran.setText("COD (Cash On Delivery)");
//
//        // Set tanggal pesanan sekarang dengan format Indonesia
//        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", new Locale("id", "ID"));
//        tanggalPesanan.setText(sdf.format(new Date()));
//
//        calculatePrices();
//    }
//
//    private void calculatePrices() {
//        try {
//            int hargaProdukAsli = parseRupiahToInt(produk.getHarga());
//            int hargaTukar = hargaTukarStr != null ? parseRupiahToInt(hargaTukarStr) : 0;
//            int biayaKirim = 15000;
//            int diskon = 0;
//
//            int selisihHarga = hargaProdukAsli - hargaTukar;
//            int total;
//
//            if (selisihHarga > 0) {
//                total = selisihHarga + biayaKirim - diskon;
//            } else {
//                total = biayaKirim - diskon;
//            }
//
//            hargaRincian.setText(formatRupiah(String.valueOf(hargaTukar)));
//            hargaTambah.setText(formatRupiah(String.valueOf(hargaProdukAsli)));
//            hargaProdukJual.setText(formatRupiah(String.valueOf(Math.max(selisihHarga, 0))));
//            ongkir.setText(formatRupiah(String.valueOf(biayaKirim)));
//            discount.setText("-" + formatRupiah(String.valueOf(diskon)));
//            totalHarga.setText(formatRupiah(String.valueOf(total)));
//
//        } catch (NumberFormatException e) {
//            Toast.makeText(this, "Error dalam perhitungan harga", Toast.LENGTH_SHORT).show();
//            e.printStackTrace();
//        }
//    }
//
//    private int parseRupiahToInt(String rupiah) throws NumberFormatException {
//        if (rupiah == null) return 0;
//        return Integer.parseInt(rupiah.replaceAll("[^0-9]", ""));
//    }
//
//    private String formatRupiah(String value) {
//        try {
//            int number = parseRupiahToInt(value);
//            return "Rp " + String.format(Locale.US, "%,d", number).replace(",", ".");
//        } catch (NumberFormatException e) {
//            return "Rp 0";
//        }
//    }
//
//    private void setupButtons() {
//        backIcon.setOnClickListener(v -> finish());
//
//        lanjut.setOnClickListener(v -> kirimPengajuan());
//    }
//
//    private void kirimPengajuan() {
//        String namaBarangTukarFinal = namaBarangTukar.getText().toString().trim();
//        String namaProdukFinal = namaProduk.getText().toString().trim();
//        String metodeBayar = metodePembayaran.getText().toString().trim();
//        String tanggal = tanggalPesanan.getText().toString().trim();
//        String hargaTukarFinal = hargaTukarStr != null ? hargaTukarStr : "0";
//
//        // Ambil URI gambar pertama jika ada
//        String gambarUriStr = "";
//        if (gambarTerpilih != null && !gambarTerpilih.isEmpty()) {
//            gambarUriStr = gambarTerpilih.get(0).toString();
//        }
//
//        // Ambil email pengaju dari SharedPreferences
//        SharedPreferences sp = getSharedPreferences("user_login", MODE_PRIVATE);
//        String emailPengaju = sp.getString("email_login", "");
//
//        // Pastikan email pemilik produk diambil dari produk yang ditukar
//        Produk produk = dbHelperProduk.getProdukById(produkId);
//        String emailPemilik = produk != null ? produk.getIdPenjual() : "";
//
//        DatabaseHelperPengajuanTuta dbHelperPengajuan = new DatabaseHelperPengajuanTuta(this);
//        boolean sukses = dbHelperPengajuan.tambahPengajuan(
//                produkId,
//                namaProdukFinal,
//                namaBarangTukarFinal,
//                hargaTukarFinal,
//                metodeBayar,
//                tanggal,
//                gambarUriStr,
//                emailPengaju,
//                emailPemilik
//        );
//
//        if (sukses) {
//            Toast.makeText(this, "Pengajuan berhasil dikirim", Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(this, DaftarPengajuanTuTa.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//            finish();
//        } else {
//            Toast.makeText(this, "Gagal mengirim pengajuan", Toast.LENGTH_SHORT).show();
//        }
//    }}