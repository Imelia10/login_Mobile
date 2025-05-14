package com.example.rewear_app1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

public class DetailProdukActivity extends AppCompatActivity {

    private ImageView imageBarang, btnEdit, btnHapus, btnBeli, fotoPenjual, ivKembali;
    private TextView namaBarang, hargaBarang, deskripsiBarang, namaPenjual;
    private String currentUserEmail;
    private Produk produk;
    private DatabaseHelperProduk dbHelperProduk;
    private DatabaseHelper dbHelperUser;
    private String asalHalaman;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_barang_kita);

        // Inisialisasi komponen
        imageBarang = findViewById(R.id.imageBarang);
        fotoPenjual = findViewById(R.id.fotoPenjual);
        namaBarang = findViewById(R.id.namaBarang);
        hargaBarang = findViewById(R.id.hargaBarang);
        deskripsiBarang = findViewById(R.id.deskripsiBarang);
        namaPenjual = findViewById(R.id.namaPenjual);
        btnEdit = findViewById(R.id.btnEdit);
        btnHapus = findViewById(R.id.btnHapus);
        btnBeli = findViewById(R.id.btnBeli);
        ivKembali = findViewById(R.id.back_icon);

        dbHelperProduk = new DatabaseHelperProduk(this);
        dbHelperUser = new DatabaseHelper(this);

        // Ambil ID produk dan asal halaman
        int produkId = getIntent().getIntExtra("produk_id", -1);
        asalHalaman = getIntent().getStringExtra("asal");

        if (produkId == -1) {
            Toast.makeText(this, "Produk tidak ditemukan", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Ambil produk dari database
        produk = dbHelperProduk.getProdukById(produkId);
        if (produk == null) {
            Toast.makeText(this, "Data produk tidak valid", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Ambil email user login dari SharedPreferences
        SharedPreferences preferences = getSharedPreferences("user_session", MODE_PRIVATE);
        currentUserEmail = preferences.getString("email", "");

        // Tampilkan data produk
        namaBarang.setText(produk.getNama());
        hargaBarang.setText("Rp " + produk.getHarga());
        deskripsiBarang.setText(produk.getDeskripsi());

        if (produk.getGambarUri() != null && !produk.getGambarUri().isEmpty()) {
            imageBarang.setImageURI(Uri.parse(produk.getGambarUri()));
        } else {
            imageBarang.setImageResource(R.drawable.profil1);
        }

        // Tampilkan info penjual
        String idPenjual = produk.getIdPenjual();
        boolean isProdukSendiri = false;

        if (idPenjual != null && !idPenjual.isEmpty()) {
            try {
                int idPenjualInt = Integer.parseInt(idPenjual);
                User penjual = dbHelperUser.getUserById(idPenjualInt);

                if (penjual != null) {
                    namaPenjual.setText(penjual.getFirstName() + " " + penjual.getLastName());

                    if (penjual.getPhotoUri() != null && !penjual.getPhotoUri().isEmpty()) {
                        fotoPenjual.setImageURI(Uri.parse(penjual.getPhotoUri()));
                    } else {
                        fotoPenjual.setImageResource(R.drawable.profil1);
                    }

                    // Bandingkan email user login dengan email penjual
                    if (penjual.getEmail().equalsIgnoreCase(currentUserEmail)) {
                        isProdukSendiri = true;
                    }

                } else {
                    namaPenjual.setText("Penjual tidak ditemukan");
                    fotoPenjual.setImageResource(R.drawable.profil1);
                }

            } catch (NumberFormatException e) {
                namaPenjual.setText("ID Penjual tidak valid");
                fotoPenjual.setImageResource(R.drawable.profil1);
            }
        } else {
            namaPenjual.setText("ID Penjual tidak tersedia");
            fotoPenjual.setImageResource(R.drawable.profil1);
        }

        // Tampilkan atau sembunyikan tombol edit & hapus berdasarkan kepemilikan produk
        if (isProdukSendiri) {
            btnEdit.setVisibility(ImageView.VISIBLE);
            btnHapus.setVisibility(ImageView.VISIBLE);
        } else {
            btnEdit.setVisibility(ImageView.GONE);
            btnHapus.setVisibility(ImageView.GONE);
        }

        // Tombol edit
        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(DetailProdukActivity.this, EditProdukActivity.class);
            intent.putExtra("produk_id", produk.getId());
            startActivity(intent);
        });

        // Tombol hapus
        btnHapus.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Hapus Produk")
                    .setMessage("Yakin ingin menghapus produk ini?")
                    .setPositiveButton("Ya", (dialog, which) -> {
                        boolean sukses = dbHelperProduk.hapusProduk(produk.getId());
                        if (sukses) {
                            Toast.makeText(this, "Produk berhasil dihapus", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, "Gagal menghapus produk", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Batal", null)
                    .show();
        });


        // Tombol beli
        btnBeli.setOnClickListener(v ->
                Toast.makeText(this, "Silakan hubungi penjual untuk membeli produk ini", Toast.LENGTH_SHORT).show()
        );

        // Tombol kembali
        ivKembali.setOnClickListener(v -> {
            if ("cardupload".equals(asalHalaman)) {
                startActivity(new Intent(this, CardUploadBarangActivity.class));
            } else if ("transaksi".equals(asalHalaman)) {
                startActivity(new Intent(this, TransaksiActivity.class));
            } else {
                finish();
            }
            finish();
        });
    }
}
