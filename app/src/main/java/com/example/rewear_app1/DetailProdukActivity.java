//package com.example.rewear_app1;
//
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.net.Uri;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AlertDialog;
//import androidx.appcompat.app.AppCompatActivity;
//
//public class DetailProdukActivity extends AppCompatActivity {
//
//    private static final String TAG = "DetailProdukActivity";
//    private static final int REQUEST_EDIT_PRODUK = 1;
//
//    private ImageView imageBarang, btnEdit, btnHapus, btnBeli, fotoPenjual, ivKembali;
//    private TextView namaBarang, hargaBarang, deskripsiBarang, namaPenjual;
//    private String currentUserEmail;
//    private Produk produk;
//    private DatabaseHelperProduk dbHelperProduk;
//    private DatabaseHelper dbHelperUser;
//    private String asalHalaman;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.detail_barang_kita);
//
//        initViews();
//        dbHelperProduk = new DatabaseHelperProduk(this);
//        dbHelperUser = new DatabaseHelper(this);
//        getIntentData();
//        getCurrentUserEmail();
//        loadProductData();
//        setupButtons();
//    }
//
//    private void initViews() {
//        imageBarang = findViewById(R.id.imageBarang);
//        fotoPenjual = findViewById(R.id.fotoPenjual);
//        namaBarang = findViewById(R.id.namaBarang);
//        hargaBarang = findViewById(R.id.hargaBarang);
//        deskripsiBarang = findViewById(R.id.deskripsiBarang);
//        namaPenjual = findViewById(R.id.namaPenjual);
//        btnEdit = findViewById(R.id.btnEdit);
//        btnHapus = findViewById(R.id.btnHapus);
//        btnBeli = findViewById(R.id.btnBeli);
//        ivKembali = findViewById(R.id.back_icon);
//    }
//
//    private void getIntentData() {
//        int produkId = getIntent().getIntExtra("produk_id", -1);
//        asalHalaman = getIntent().getStringExtra("asal");
//
//        if (produkId == -1) {
//            Toast.makeText(this, "Produk tidak ditemukan", Toast.LENGTH_SHORT).show();
//            finish();
//            return;
//        }
//
//        produk = dbHelperProduk.getProdukById(produkId);
//        if (produk == null) {
//            Toast.makeText(this, "Data produk tidak valid", Toast.LENGTH_SHORT).show();
//            finish();
//        }
//    }
//
//    private void getCurrentUserEmail() {
//        SharedPreferences preferences = getSharedPreferences("user_session", MODE_PRIVATE);
//        currentUserEmail = preferences.getString("email", "");
//        if (currentUserEmail.isEmpty()) {
//            Toast.makeText(this, "Silakan login kembali", Toast.LENGTH_SHORT).show();
//            finish();
//        }
//    }
//
//    private void loadProductData() {
//        if (produk == null) return;
//
//        namaBarang.setText(produk.getNama());
//        hargaBarang.setText("Rp " + produk.getHarga());
//        deskripsiBarang.setText(produk.getDeskripsi());
//
//        if (produk.getGambarUri() != null && !produk.getGambarUri().isEmpty()) {
//            try {
//                imageBarang.setImageURI(Uri.parse(produk.getGambarUri()));
//            } catch (Exception e) {
//                Log.e(TAG, "Error loading product image", e);
//                imageBarang.setImageResource(R.drawable.profil1);
//            }
//        } else {
//            imageBarang.setImageResource(R.drawable.profil1);
//        }
//
//        loadSellerInfo();
//    }
//
//    private void loadSellerInfo() {
//        if (produk == null || produk.getIdPenjual() == null) {
//            namaPenjual.setText("Penjual tidak diketahui");
//            fotoPenjual.setImageResource(R.drawable.profil1);
//            btnEdit.setVisibility(View.GONE);
//            btnHapus.setVisibility(View.GONE);
//            btnBeli.setVisibility(View.GONE);
//            return;
//        }
//
//        try {
//            int idPenjual = Integer.parseInt(produk.getIdPenjual());
//            User penjual = dbHelperUser.getUserById(idPenjual);
//
//            if (penjual == null) {
//                namaPenjual.setText("Penjual tidak ditemukan");
//                fotoPenjual.setImageResource(R.drawable.profil1);
//                btnEdit.setVisibility(View.GONE);
//                btnHapus.setVisibility(View.GONE);
//                btnBeli.setVisibility(View.VISIBLE);
//                return;
//            }
//
//            namaPenjual.setText(penjual.getFirstName() + " " + penjual.getLastName());
//
//            if (penjual.getPhotoUri() != null && !penjual.getPhotoUri().isEmpty()) {
//                try {
//                    fotoPenjual.setImageURI(Uri.parse(penjual.getPhotoUri()));
//                } catch (Exception e) {
//                    fotoPenjual.setImageResource(R.drawable.profil1);
//                }
//            } else {
//                fotoPenjual.setImageResource(R.drawable.profil1);
//            }
//
//            Log.d("OWNERSHIP_CHECK", "Current User: " + currentUserEmail);
//            Log.d("OWNERSHIP_CHECK", "Product Owner: " + penjual.getEmail());
//
//            boolean isMyProduct = currentUserEmail.equalsIgnoreCase(penjual.getEmail());
//
//            btnEdit.setVisibility(isMyProduct ? View.VISIBLE : View.GONE);
//            btnHapus.setVisibility(isMyProduct ? View.VISIBLE : View.GONE);
//            btnBeli.setVisibility(isMyProduct ? View.GONE : View.VISIBLE);
//
//        } catch (NumberFormatException e) {
//            Log.e(TAG, "Format ID Penjual tidak valid", e);
//            namaPenjual.setText("ID Penjual tidak valid");
//            fotoPenjual.setImageResource(R.drawable.profil1);
//            btnEdit.setVisibility(View.GONE);
//            btnHapus.setVisibility(View.GONE);
//            btnBeli.setVisibility(View.VISIBLE);
//        }
//    }
//
//    private void setupButtons() {
//        btnEdit.setOnClickListener(v -> {
//            Intent intent = new Intent(DetailProdukActivity.this, EditProdukActivity.class);
//            intent.putExtra("produk_id", produk.getId());
//            startActivityForResult(intent, REQUEST_EDIT_PRODUK);
//        });
//
//        btnHapus.setOnClickListener(v -> showDeleteConfirmationDialog());
//
//        btnBeli.setOnClickListener(v -> {
//            if (produk == null) {
//                Toast.makeText(this, "Produk tidak ditemukan", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            // Dapatkan email penjual dari data User
//            try {
//                int idPenjual = Integer.parseInt(produk.getIdPenjual());
//                User penjual = dbHelperUser.getUserById(idPenjual);
//
//                if (penjual == null) {
//                    Toast.makeText(this, "Data penjual tidak ditemukan", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                String emailPenjual = penjual.getEmail();
//
//                if (currentUserEmail.equals(emailPenjual)) {
//                    Toast.makeText(this, "Kamu tidak bisa membeli produk milik sendiri", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                Intent intent = new Intent(DetailProdukActivity.this, DetailPesanan.class);
//                intent.putExtra("produk_id", produk.getId());
//                intent.putExtra("produk_nama", produk.getNama());
//                intent.putExtra("produk_harga", produk.getHarga());
//                intent.putExtra("produk_gambar_uri", produk.getGambarUri());
//                intent.putExtra("penjual_id", produk.getIdPenjual());
//                startActivity(intent);
//
//            } catch (NumberFormatException e) {
//                Toast.makeText(this, "ID Penjual tidak valid", Toast.LENGTH_SHORT).show();
//                Log.e(TAG, "Error parsing seller ID", e);
//            }
//        });
//        ivKembali.setOnClickListener(v -> navigateBack());
//    }
//
//    private void showDeleteConfirmationDialog() {
//        new AlertDialog.Builder(this)
//                .setTitle("Hapus Produk")
//                .setMessage("Yakin ingin menghapus produk ini?")
//                .setPositiveButton("Ya", (dialog, which) -> {
//                    boolean sukses = dbHelperProduk.hapusProduk(produk.getId());
//                    if (sukses) {
//                        Toast.makeText(this, "Produk berhasil dihapus", Toast.LENGTH_SHORT).show();
//                        finish();
//                    } else {
//                        Toast.makeText(this, "Gagal menghapus produk", Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .setNegativeButton("Batal", null)
//                .show();
//    }
//
//    private void navigateBack() {
//        if ("cardupload".equals(asalHalaman)) {
//            startActivity(new Intent(this, CardUploadBarangActivity.class));
//        } else if ("transaksi".equals(asalHalaman)) {
//            startActivity(new Intent(this, TransaksiActivity.class));
//        }
//        finish();
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_EDIT_PRODUK && resultCode == RESULT_OK) {
//            produk = dbHelperProduk.getProdukById(produk.getId());
//            loadProductData();
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        if (dbHelperProduk != null) dbHelperProduk.close();
//        if (dbHelperUser != null) dbHelperUser.close();
//        super.onDestroy();
//    }
//}



package com.example.rewear_app1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class DetailProdukActivity extends AppCompatActivity {

    private static final String TAG = "DetailProdukActivity";
    private static final int REQUEST_EDIT_PRODUK = 1;

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

        initViews();
        dbHelperProduk = new DatabaseHelperProduk(this);
        dbHelperUser = new DatabaseHelper(this);
        getIntentData();
        getCurrentUserEmail();
        loadProductData();
        setupButtons();
    }

    private void initViews() {
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
    }

    private void getIntentData() {
        int produkId = getIntent().getIntExtra("produk_id", -1);
        asalHalaman = getIntent().getStringExtra("asal");

        if (produkId == -1) {
            Toast.makeText(this, "Produk tidak ditemukan", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        produk = dbHelperProduk.getProdukById(produkId);
        if (produk == null) {
            Toast.makeText(this, "Data produk tidak valid", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void getCurrentUserEmail() {
        SharedPreferences preferences = getSharedPreferences("user_session", MODE_PRIVATE);
        currentUserEmail = preferences.getString("email", "");
        if (currentUserEmail.isEmpty()) {
            Toast.makeText(this, "Silakan login kembali", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadProductData() {
        if (produk == null) return;

        namaBarang.setText(produk.getNama());
        hargaBarang.setText("Rp " + produk.getHarga());
        deskripsiBarang.setText(produk.getDeskripsi());

        if (produk.getGambarUri() != null && !produk.getGambarUri().isEmpty()) {
            try {
                imageBarang.setImageURI(Uri.parse(produk.getGambarUri()));
            } catch (Exception e) {
                Log.e(TAG, "Error loading product image", e);
                imageBarang.setImageResource(R.drawable.profil1);
            }
        } else {
            imageBarang.setImageResource(R.drawable.profil1);
        }

        loadSellerInfo();
    }

    private void loadSellerInfo() {
        if (produk == null || produk.getIdPenjual() == null) {
            namaPenjual.setText("Penjual tidak diketahui");
            fotoPenjual.setImageResource(R.drawable.profil1);
            btnEdit.setVisibility(View.GONE);
            btnHapus.setVisibility(View.GONE);
            btnBeli.setVisibility(View.GONE);
            return;
        }

        try {
            int idPenjual = Integer.parseInt(produk.getIdPenjual());
            User penjual = dbHelperUser.getUserById(idPenjual);

            if (penjual == null) {
                namaPenjual.setText("Penjual tidak ditemukan");
                fotoPenjual.setImageResource(R.drawable.profil1);
                btnEdit.setVisibility(View.GONE);
                btnHapus.setVisibility(View.GONE);
                btnBeli.setVisibility(View.VISIBLE);
                return;
            }

            namaPenjual.setText(penjual.getFirstName() + " " + penjual.getLastName());

            if (penjual.getPhotoUri() != null && !penjual.getPhotoUri().isEmpty()) {
                try {
                    fotoPenjual.setImageURI(Uri.parse(penjual.getPhotoUri()));
                } catch (Exception e) {
                    fotoPenjual.setImageResource(R.drawable.profil1);
                }
            } else {
                fotoPenjual.setImageResource(R.drawable.profil1);
            }

            boolean isMyProduct = currentUserEmail.equalsIgnoreCase(penjual.getEmail());

            btnEdit.setVisibility(isMyProduct ? View.VISIBLE : View.GONE);
            btnHapus.setVisibility(isMyProduct ? View.VISIBLE : View.GONE);
            btnBeli.setVisibility(isMyProduct ? View.GONE : View.VISIBLE);

        } catch (NumberFormatException e) {
            Log.e(TAG, "Format ID Penjual tidak valid", e);
            namaPenjual.setText("ID Penjual tidak valid");
            fotoPenjual.setImageResource(R.drawable.profil1);
            btnEdit.setVisibility(View.GONE);
            btnHapus.setVisibility(View.GONE);
            btnBeli.setVisibility(View.VISIBLE);
        }
    }

    private void setupButtons() {
        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(DetailProdukActivity.this, EditProdukActivity.class);
            intent.putExtra("produk_id", produk.getId());
            startActivityForResult(intent, REQUEST_EDIT_PRODUK);
        });

        btnHapus.setOnClickListener(v -> showDeleteConfirmationDialog());

        btnBeli.setOnClickListener(v -> {
            if (produk == null) {
                Toast.makeText(this, "Produk tidak ditemukan", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int idPenjual = Integer.parseInt(produk.getIdPenjual());
                User penjual = dbHelperUser.getUserById(idPenjual);

                if (penjual == null) {
                    Toast.makeText(this, "Data penjual tidak ditemukan", Toast.LENGTH_SHORT).show();
                    return;
                }

                String emailPenjual = penjual.getEmail();

                if (currentUserEmail.equals(emailPenjual)) {
                    Toast.makeText(this, "Kamu tidak bisa membeli produk milik sendiri", Toast.LENGTH_SHORT).show();
                    return;
                }

                // ➤ Pengecekan kategori produk: jika Tukar Tambah, buka AjukanBarangActivity
                if ("Tukar Tambah".equalsIgnoreCase(produk.getKategori())) {
                    Intent intent = new Intent(DetailProdukActivity.this, AjukanBarangActivity.class);
                    intent.putExtra("produk_id", produk.getId());
                    intent.putExtra("penjual_id", produk.getIdPenjual());
                    startActivity(intent);
                } else {
                    // Jika bukan Tukar Tambah, buka halaman DetailPesanan
                    Intent intent = new Intent(DetailProdukActivity.this, DetailPesanan.class);
                    intent.putExtra("produk_id", produk.getId());
                    intent.putExtra("produk_nama", produk.getNama());
                    intent.putExtra("produk_harga", produk.getHarga());
                    intent.putExtra("produk_gambar_uri", produk.getGambarUri());
                    intent.putExtra("penjual_id", produk.getIdPenjual());
                    startActivity(intent);
                }

            } catch (NumberFormatException e) {
                Toast.makeText(this, "ID Penjual tidak valid", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error parsing seller ID", e);
            }
        });

        ivKembali.setOnClickListener(v -> navigateBack());
    }

    private void showDeleteConfirmationDialog() {
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
    }

    private void navigateBack() {
        if ("cardupload".equals(asalHalaman)) {
            startActivity(new Intent(this, CardUploadBarangActivity.class));
        } else if ("transaksi".equals(asalHalaman)) {
            startActivity(new Intent(this, TransaksiActivity.class));
        }
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_EDIT_PRODUK && resultCode == RESULT_OK) {
            produk = dbHelperProduk.getProdukById(produk.getId());
            loadProductData();
        }
    }

    @Override
    protected void onDestroy() {
        if (dbHelperProduk != null) dbHelperProduk.close();
        if (dbHelperUser != null) dbHelperUser.close();
        super.onDestroy();
    }
}
