package com.example.rewear_app1;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.graphics.Typeface;
import android.util.Log;
import java.util.Arrays;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.widget.GridLayout.LayoutParams;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class DaftarPengajuanTuTa extends AppCompatActivity {

    private SearchView cari;
    private TextView tvBelumAdaBarang;
    private GridLayout gridProduk;

    private ArrayList<PengajuanTuta> listPengajuan = new ArrayList<>();
    private ArrayList<PengajuanTuta> listFiltered = new ArrayList<>();

    private DatabaseHelperPengajuanTuta dbHelper;

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_pengajuan_tuta);

        // Initialize views
        cari = findViewById(R.id.cari);
        tvBelumAdaBarang = findViewById(R.id.tvBelumAdaBarang);
        gridProduk = findViewById(R.id.gridProduk);

        // Initialize database helper
        dbHelper = new DatabaseHelperPengajuanTuta(this);

        // Verify files directory exists
        File filesDir = getFilesDir();
        Log.d("FilesDir", "App files directory: " + filesDir.getAbsolutePath());
        Log.d("FilesDir", "Files in directory: " + Arrays.toString(filesDir.list()));

        setupSearch();
        loadDataPengajuan();
    }

    private void setupSearch() {
        cari.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterData(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterData(newText);
                return true;
            }
        });
    }

    private void loadDataPengajuan() {
        SharedPreferences sp = getSharedPreferences("user_session", MODE_PRIVATE);
        String emailLogin = sp.getString("email_login", "");

        Log.d("DaftarPengajuanTuTa", "Logged in email: " + emailLogin);

        if (emailLogin.isEmpty()) {
            Toast.makeText(this, "Silakan login kembali", Toast.LENGTH_SHORT).show();
            return;
        }

        listPengajuan = dbHelper.getPengajuanByUserEmail(emailLogin);

        // Log all pengajuan data
        for (PengajuanTuta p : listPengajuan) {
            Log.d("DaftarPengajuanTuTa", "Pengajuan: " +
                    "ID=" + p.getId() +
                    ", Produk=" + p.getNamaProduk() +
                    ", EmailPengaju=" + p.getEmailPengaju() +
                    ", EmailPemilik=" + p.getEmailPemilik() +
                    ", Image URI=" + p.getGambarUri());
        }

        if (listPengajuan.isEmpty()) {
            Log.d("DaftarPengajuanTuTa", "No pengajuan found for this user");
            tvBelumAdaBarang.setVisibility(View.VISIBLE);
        } else {
            tvBelumAdaBarang.setVisibility(View.GONE);
        }

        filterData("");
    }

    private void filterData(String query) {
        String lowerQuery = query != null ? query.toLowerCase(Locale.ROOT) : "";
        listFiltered.clear();

        for (PengajuanTuta p : listPengajuan) {
            boolean cocokSearch = lowerQuery.isEmpty() ||
                    (p.getNamaProduk() != null && p.getNamaProduk().toLowerCase(Locale.ROOT).contains(lowerQuery)) ||
                    (p.getNamaBarangTukar() != null && p.getNamaBarangTukar().toLowerCase(Locale.ROOT).contains(lowerQuery));

            if (cocokSearch) {
                listFiltered.add(p);
            }
        }
        tampilkanData();
    }

    private void loadAndDisplayImage(ImageView imageView, String imageUri) {
        if (imageUri == null || imageUri.isEmpty()) {
            imageView.setImageResource(R.drawable.dress);
            return;
        }

        String[] imagePaths = imageUri.split(";");
        if (imagePaths.length == 0) {
            imageView.setImageResource(R.drawable.dress);
            return;
        }

        String path = imagePaths[0];

        File imgFile;
        if (path.startsWith("/")) {
            imgFile = new File(path); // full path
        } else {
            imgFile = new File(getFilesDir(), path); // nama file saja
        }

        Log.d("ImageLoad", "Trying to load image from: " + imgFile.getAbsolutePath());

        if (imgFile.exists()) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath(), options);
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            } else {
                imageView.setImageResource(R.drawable.dress);
            }
        } else {
            Log.d("ImageLoad", "File not found: " + imgFile.getAbsolutePath());
            imageView.setImageResource(R.drawable.dress);
        }
    }


    private void tampilkanData() {
        gridProduk.removeAllViews();

        if (listFiltered.isEmpty()) {
            tvBelumAdaBarang.setVisibility(View.VISIBLE);
            return;
        } else {
            tvBelumAdaBarang.setVisibility(View.GONE);
        }

        for (PengajuanTuta p : listFiltered) {
            // Log pengajuan data including image URI
            Log.d("PengajuanData", "ID: " + p.getId() +
                    ", Nama Produk: " + p.getNamaProduk() +
                    ", Image URI: " + p.getGambarUri());

            CardView cardView = new CardView(this);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.setMargins(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));
            cardView.setLayoutParams(params);

            cardView.setRadius(dpToPx(20));
            cardView.setCardElevation(dpToPx(8));

            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));

            ImageView iv = new ImageView(this);
            iv.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(100)));
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);

            loadAndDisplayImage(iv, p.getGambarUri());

            TextView tvNamaProduk = new TextView(this);
            tvNamaProduk.setText(p.getNamaProduk() != null ? p.getNamaProduk() : "");
            tvNamaProduk.setTextSize(16);
            tvNamaProduk.setTypeface(null, Typeface.BOLD);

            TextView tvNamaBarangTukar = new TextView(this);
            tvNamaBarangTukar.setText("Tukar: " + (p.getNamaBarangTukar() != null ? p.getNamaBarangTukar() : ""));
            tvNamaBarangTukar.setTextSize(14);

            TextView tvHargaTukar = new TextView(this);
            tvHargaTukar.setText("Harga Tukar: " + (p.getHargaTukar() != null ? p.getHargaTukar() : ""));
            tvHargaTukar.setTextSize(14);

            TextView tvTanggal = new TextView(this);
            tvTanggal.setText(p.getTanggal() != null ? p.getTanggal() : "");
            tvTanggal.setTextSize(12);
            tvTanggal.setTypeface(null, Typeface.ITALIC);

            layout.addView(iv);
            layout.addView(tvNamaProduk);
            layout.addView(tvNamaBarangTukar);
            layout.addView(tvHargaTukar);
            layout.addView(tvTanggal);

            cardView.addView(layout);
            gridProduk.addView(cardView);
        }
    }

    @Override
    protected void onDestroy() {
        if (dbHelper != null) {
            dbHelper.close();
        }
        super.onDestroy();
    }
}