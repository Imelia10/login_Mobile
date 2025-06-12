package com.example.rewear_app1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.File;
import java.io.InputStream;

import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.*;

public class DetailTukarTambahActivity extends AppCompatActivity {

    private ImageView backIcon, imageBarang, lanjut, batal;
    private TextView namaProduk, hargaProduk, tanggalPesanan, hargaRincian, hargaTambah, hargaProdukJual, ongkir, totalHarga;
    private EditText namaBarangTukar, metodePembayaran;

    private DatabaseHelperProduk dbHelperProduk;
    private DatabaseHelperTransaksi dbHelperTransaksi;

    private DatabaseHelper dbHelperPenjual;
    private int produkId = -1;
    private Produk produk;

    private String namaBarangTukarStr, hargaTukarStr, keteranganTukar, gambarUriTukar, gambarUriJual ;
//    private ArrayList<Uri> gambarTerpilih;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_tuta);

        dbHelperProduk = new DatabaseHelperProduk(this);

        dbHelperPenjual = new DatabaseHelper(this);
        dbHelperTransaksi = new DatabaseHelperTransaksi(this);


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
        batal = findViewById(R.id.batal);
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
        totalHarga = findViewById(R.id.total_harga);
        lanjut = findViewById(R.id.lanjut);
    }

    private void getIntentData() {
        Intent intent = getIntent();
        produkId = intent.getIntExtra("produk_id", -1);
        namaBarangTukarStr = intent.getStringExtra("nama_barang_tukar");
        hargaTukarStr = intent.getStringExtra("harga_tukar");
        keteranganTukar = intent.getStringExtra("keterangan_tukar");
        gambarUriTukar = intent.getStringExtra("gambarTuta");

        gambarUriJual = intent.getStringExtra("gambarJual");

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
//        hargaProduk.setText(formatRupiah(produk.getHarga()));
        hargaProduk.setText(formatRupiah(hargaTukarStr));

        // Gambar produk
        try {
            if (gambarUriTukar != null && !gambarUriTukar.isEmpty()) {
                imageBarang.setImageURI(Uri.parse(gambarUriTukar));
            } else {
                imageBarang.setImageResource(R.drawable.dress);
            }
            /*
            if (gambarTerpilih != null && !gambarTerpilih.isEmpty()) {
                imageBarang.setImageURI(gambarTerpilih.get(0));
            } else if (produk.getGambarUri() != null && !produk.getGambarUri().isEmpty()) {
                imageBarang.setImageURI(Uri.parse(produk.getGambarUri()));
            } else {
                imageBarang.setImageResource(R.drawable.dress);
            }
            */
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
            SharedPreferences sp = getSharedPreferences("user_session", MODE_PRIVATE);
            String emailUser = sp.getString("email_login", "");

            int hargaProdukAsli = parseRupiahToInt(produk.getHarga());
            int hargaTukar = hargaTukarStr != null ? parseRupiahToInt(hargaTukarStr) : 0;
            int biayaKirim = 15000;

            int selisihHarga = hargaProdukAsli - hargaTukar;
            int total = Math.max(selisihHarga, 0) + biayaKirim;

            hargaRincian.setText(formatRupiah(String.valueOf(hargaTukar)));
            hargaTambah.setText(formatRupiah(String.valueOf(hargaProdukAsli)));
            hargaProdukJual.setText(formatRupiah(String.valueOf(Math.max(selisihHarga, 0))));
            ongkir.setText(formatRupiah(String.valueOf(biayaKirim)));
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
        batal.setOnClickListener(v -> finish());
        lanjut.setOnClickListener(v -> kirimPengajuan());
    }


    private void kirimPengajuan() {
        String namaBarangTukarFinal = namaBarangTukar.getText().toString().trim();
        String namaProdukFinal = namaProduk.getText().toString().trim();
        String metodeBayar = metodePembayaran.getText().toString().trim();
        String tanggal = tanggalPesanan.getText().toString().trim();
        String hargaTukarFinal = hargaTukarStr != null ? hargaTukarStr : "0";

        // Ambil URI gambar dari database
//        String gambarTuta = gambarUriTukar;
//        String gambarUriTerpilih = saveImagesToStorage(gambarTerpilih);

        ArrayList<Uri> uriListJual = new ArrayList<>();
        uriListJual.add(Uri.parse(gambarUriJual.trim()));
        String outGambarJual = saveImagesToStorage(uriListJual);

        ArrayList<Uri> uriListTukar = new ArrayList<>();
        uriListTukar.add(Uri.parse(gambarUriTukar.trim()));
        String outGambarTukar = saveImagesToStorage(uriListTukar);


        SharedPreferences sp = getSharedPreferences("user_session", MODE_PRIVATE);
        String emailPengaju = sp.getString("email_login", "");
        String idPenjual = produk != null ? produk.getIdPenjual() : "";

        // get data penjual
        var userPenjual = dbHelperPenjual.getUserById(Integer.parseInt(idPenjual));

        if (emailPengaju.isEmpty() || userPenjual == null) {
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
                outGambarJual,
                emailPengaju,
                userPenjual.getEmail(),
                outGambarTukar

        );

        Log.d("PengajuanTuta", "Insert result: " + sukses +
                ", EmailPengaju: " + emailPengaju +
                ", EmailPemilik: " + userPenjual.getEmail());

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

