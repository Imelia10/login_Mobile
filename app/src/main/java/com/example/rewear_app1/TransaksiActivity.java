package com.example.rewear_app1;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.cardview.widget.CardView;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class TransaksiActivity extends AppCompatActivity {

    private Button btnBeli, btnSewa, btnTukarTambah;
    private SearchView searchView;
    private ImageView ivTambah;
    private TextView tvBelumAdaBarang;
    private GridLayout gridProduk;

    private DatabaseHelperProduk dbHelper;
    private String kategoriAktif = "Beli";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaksi);

        // Inisialisasi UI components
        btnBeli = findViewById(R.id.btnbeli);
        btnSewa = findViewById(R.id.btnsewa);
        btnTukarTambah = findViewById(R.id.btntukartambah);
        searchView = findViewById(R.id.cari);
        ivTambah = findViewById(R.id.ivtambah);
        tvBelumAdaBarang = findViewById(R.id.tvBelumAdaBarang);
        gridProduk = findViewById(R.id.gridProduk);

        dbHelper = new DatabaseHelperProduk(this);

        // Terima kategori dari intent
        String kategoriTerpilih = getIntent().getStringExtra("kategori_terpilih");
        if (kategoriTerpilih != null) {
            kategoriAktif = kategoriTerpilih;
        }

        // Tombol kategori listener
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
            Intent intent = new Intent(TransaksiActivity.this, TambahProdukActivity.class);
            startActivity(intent);
        });

        // SearchView listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                tampilkanProduk(query, kategoriAktif);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                tampilkanProduk(newText, kategoriAktif);
                return false;
            }
        });

        // Set tampilan awal
        switch (kategoriAktif) {
            case "Beli":
                setSelectedButton(btnBeli);
                break;
            case "Sewa":
                setSelectedButton(btnSewa);
                break;
            case "Tukar Tambah":
                setSelectedButton(btnTukarTambah);
                break;
        }
        tampilkanProduk("", kategoriAktif);
    }

    private void setSelectedButton(Button selectedButton) {
        btnBeli.setBackgroundTintList(getResources().getColorStateList(R.color.kategori_normal));
        btnSewa.setBackgroundTintList(getResources().getColorStateList(R.color.kategori_normal));
        btnTukarTambah.setBackgroundTintList(getResources().getColorStateList(R.color.kategori_normal));

        selectedButton.setBackgroundTintList(getResources().getColorStateList(R.color.kategori_selected));
    }

    private void tampilkanProduk(String keyword, String kategori) {
        List<Produk> produkList = dbHelper.getProdukByKategoriAndKeyword(kategori, keyword);

        gridProduk.removeAllViews();

        if (produkList.isEmpty()) {
            tvBelumAdaBarang.setVisibility(View.VISIBLE);
            return;
        } else {
            tvBelumAdaBarang.setVisibility(View.GONE);
        }

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

            ImageView iv = new ImageView(this);
            iv.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(100)));
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);

            String uriStr = produk.getGambarUri();
            if (uriStr != null && !uriStr.isEmpty()) {
                iv.setImageURI(Uri.parse(uriStr));
            } else {
                iv.setImageResource(R.drawable.profil1); // ganti dengan gambar default kamu
            }

            TextView nama = new TextView(this);
            nama.setText(produk.getNama());
            nama.setTextSize(16);
            nama.setTypeface(null, Typeface.BOLD);

            TextView harga = new TextView(this);
            harga.setText("Rp " + produk.getHarga());
            harga.setTextSize(14);

            layout.addView(iv);
            layout.addView(nama);
            layout.addView(harga);
            cardView.addView(layout);
            gridProduk.addView(cardView);
        }
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
