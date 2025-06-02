package com.example.rewear_app1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.util.List;

public class CardUploadBarangActivity extends AppCompatActivity {
    private Button btnBeli, btnSewa, btnTukarTambah;
    private SearchView searchView;
    private ImageView ivTambah, ivKembali;
    private TextView tvBelumAdaBarang;
    private GridLayout gridProduk;
    private DatabaseHelperProduk dbHelper;
    private String kategoriAktif = "Beli";
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_produk);

        btnBeli = findViewById(R.id.btnbeli);
        btnSewa = findViewById(R.id.btnsewa);
        btnTukarTambah = findViewById(R.id.btntukartambah);
        searchView = findViewById(R.id.cari);
        ivTambah = findViewById(R.id.ivtambah);
        ivKembali = findViewById(R.id.back_icon); // Menambahkan tombol kembali
        tvBelumAdaBarang = findViewById(R.id.tvBelumAdaBarang);
        gridProduk = findViewById(R.id.gridProduk);

        dbHelper = new DatabaseHelperProduk(this);

        SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
        currentUserId = prefs.getString("user_id", "");

        String kategoriTerpilih = getIntent().getStringExtra("kategori_terpilih");
        if (kategoriTerpilih != null) {
            kategoriAktif = kategoriTerpilih;
        }

        btnBeli.setOnClickListener(v -> {
            kategoriAktif = "Beli";
            setSelectedButton(btnBeli);
            tampilkanProduk(searchView.getQuery().toString(), kategoriAktif);
        });

        btnSewa.setOnClickListener(v -> {
            kategoriAktif = "Sewa";
            setSelectedButton(btnSewa);
            tampilkanProduk(searchView.getQuery().toString(), kategoriAktif);
        });

        btnTukarTambah.setOnClickListener(v -> {
            kategoriAktif = "Tukar Tambah";
            setSelectedButton(btnTukarTambah);
            tampilkanProduk(searchView.getQuery().toString(), kategoriAktif);
        });

        ivTambah.setOnClickListener(v -> {
            startActivity(new Intent(this, TambahProdukActivity.class));
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                tampilkanProduk(query, kategoriAktif);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                tampilkanProduk(newText, kategoriAktif);
                return true;
            }
        });

        switch (kategoriAktif) {
            case "Beli": setSelectedButton(btnBeli); break;
            case "Sewa": setSelectedButton(btnSewa); break;
            case "Tukar Tambah": setSelectedButton(btnTukarTambah); break;
        }

        tampilkanProduk("", kategoriAktif);

        ivKembali.setOnClickListener(v -> { // Listener untuk tombol kembali
            startActivity(new Intent(this, HomeActivity.class)); // Mengarahkan ke HomeActivity
            finish(); // Menutup aktivitas ini
        });
    }

    private void setSelectedButton(Button selectedButton) {
        btnBeli.setBackgroundTintList(getResources().getColorStateList(R.color.kategori_normal));
        btnSewa.setBackgroundTintList(getResources().getColorStateList(R.color.kategori_normal));
        btnTukarTambah.setBackgroundTintList(getResources().getColorStateList(R.color.kategori_normal));
        selectedButton.setBackgroundTintList(getResources().getColorStateList(R.color.kategori_selected));
    }

    private void tampilkanProduk(String keyword, String kategori) {
        gridProduk.removeAllViews();
        List<Produk> produkList = dbHelper.getProdukByKategoriAndKeywordAndUser(kategori, keyword, currentUserId);

        if (produkList.isEmpty()) {
            tvBelumAdaBarang.setVisibility(View.VISIBLE);
            return;
        }

        tvBelumAdaBarang.setVisibility(View.GONE);

        for (Produk produk : produkList) {
            CardView cardView = new CardView(this);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.setMargins(16, 16, 16, 16);
            cardView.setLayoutParams(params);
            cardView.setRadius(20);
            cardView.setCardElevation(8);

            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(16, 16, 16, 16);
            layout.setGravity(Gravity.CENTER_HORIZONTAL);

            ImageView iv = new ImageView(this);
            iv.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(120)));
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);

            // ambil hanya gambar awal saja
            String gambarUri = produk.getGambarUri();

            if (gambarUri != null && !gambarUri.isEmpty()) {
                // Split string berdasarkan \n untuk dapat list gambar
                String[] gambarArray = gambarUri.split("\n");
                if (gambarArray.length > 0 && !gambarArray[0].isEmpty()) {
                    iv.setImageURI(Uri.parse(gambarArray[0]));  // pakai gambar pertama
                } else {
                    iv.setImageResource(R.drawable.profil1);  // default jika kosong
                }
            } else {
                iv.setImageResource(R.drawable.profil1);  // default jika null atau kosong
            }

            iv.setOnClickListener(v -> {
                Intent intent = new Intent(this, DetailProdukActivity.class);
                intent.putExtra("produk_id", produk.getId());
                startActivity(intent);
            });

            TextView nama = new TextView(this);
            nama.setText(produk.getNama());
            nama.setTextSize(16);
            nama.setTypeface(null, Typeface.BOLD);
            nama.setPadding(0, 12, 0, 4);
            nama.setGravity(Gravity.CENTER);

            TextView harga = new TextView(this);
            harga.setText("Rp " + produk.getHarga());
            harga.setTextSize(14);
            harga.setGravity(Gravity.CENTER);

            layout.addView(iv);
            layout.addView(nama);
            layout.addView(harga);
            cardView.addView(layout);
            gridProduk.addView(cardView);
        }
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
    @Override
    protected void onResume() {
        super.onResume();
        tampilkanProduk(searchView.getQuery().toString(), kategoriAktif);
    }
}
