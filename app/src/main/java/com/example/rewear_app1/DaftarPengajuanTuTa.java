package com.example.rewear_app1;

import androidx.core.content.ContextCompat;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.graphics.Typeface;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class DaftarPengajuanTuTa extends AppCompatActivity {

    private SearchView cari;
    private TextView tvBelumAdaBarang;
    private GridLayout gridProduk;
    private ImageView backIcon;

    private ArrayList<PengajuanTuta> listPengajuan = new ArrayList<>();
    private ArrayList<PengajuanTuta> listFiltered = new ArrayList<>();

    private DatabaseHelperPengajuanTuta dbHelper;
    private DatabaseHelperProduk dbProdukHelper;

    private String emailUserLogin;

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_pengajuan_tuta);

        cari = findViewById(R.id.cari);
        tvBelumAdaBarang = findViewById(R.id.tvBelumAdaBarang);
        gridProduk = findViewById(R.id.gridProduk);
        backIcon = findViewById(R.id.back_icon);

        dbHelper = new DatabaseHelperPengajuanTuta(this);
        dbProdukHelper = new DatabaseHelperProduk(this);

        backIcon.setOnClickListener(v -> {
            Intent intent = new Intent(DaftarPengajuanTuTa.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });

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
        emailUserLogin = sp.getString("email_login", "");

        if (emailUserLogin.isEmpty()) {
            Toast.makeText(this, "Silakan login kembali", Toast.LENGTH_SHORT).show();
            return;
        }

        listPengajuan = dbHelper.getPengajuanByUserEmail(emailUserLogin);

        if (listPengajuan.isEmpty()) {
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

        try {
            if (imageUri.startsWith("content://")) {
                imageView.setImageURI(Uri.parse(imageUri));
            } else {
                File imgFile = imageUri.startsWith("/") ? new File(imageUri) : new File(getFilesDir(), imageUri);
                if (imgFile.exists()) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 2;
                    Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath(), options);
                    imageView.setImageBitmap(bitmap != null ? bitmap : BitmapFactory.decodeResource(getResources(), R.drawable.dress));
                } else {
                    imageView.setImageResource(R.drawable.dress);
                }
            }
        } catch (Exception e) {
            Log.e("ImageLoad", "Error loading image: " + e.getMessage());
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

        int columnCount = 2;
        gridProduk.setColumnCount(columnCount);

        for (PengajuanTuta p : listFiltered) {
            CardView cardView = new CardView(this);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.setMargins(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8));
            cardView.setLayoutParams(params);

            cardView.setRadius(dpToPx(16));
            cardView.setCardElevation(dpToPx(6));

            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(dpToPx(12), dpToPx(12), dpToPx(12), dpToPx(12));

            ImageView iv = new ImageView(this);
            LinearLayout.LayoutParams ivParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(120));
            iv.setLayoutParams(ivParams);
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);

            loadAndDisplayImage(iv, (emailUserLogin.equals(p.getEmailPemilik()) && !emailUserLogin.equals(p.getEmailPengaju())) ? p.getGambarUri() : p.getGambar_tuta());

            TextView tvNamaProduk = new TextView(this);
            tvNamaProduk.setText((emailUserLogin.equals(p.getEmailPemilik()) && !emailUserLogin.equals(p.getEmailPengaju())) ? p.getNamaBarangTukar() : p.getNamaProduk());
            tvNamaProduk.setTextSize(16);
            tvNamaProduk.setTypeface(null, Typeface.BOLD);

            TextView tvNamaBarangTukar = new TextView(this);
            tvNamaBarangTukar.setText("Tukar: " + (emailUserLogin.equals(p.getEmailPemilik()) && !emailUserLogin.equals(p.getEmailPengaju()) ? p.getNamaProduk() : p.getNamaBarangTukar()));
            tvNamaBarangTukar.setTextSize(14);

            TextView tvHargaTukar = new TextView(this);
            String hargaText = "Harga Tukar: " + (p.getHargaTukar() != null ? p.getHargaTukar() : "Tidak tersedia");
            tvHargaTukar.setText(hargaText);
            tvHargaTukar.setTextSize(14);

            TextView tvStatus = new TextView(this);
            tvStatus.setText("Status: " + p.getStatus());
            tvStatus.setTextSize(14);
            switch(p.getStatus().toLowerCase(Locale.ROOT)) {
                case "diterima":
                    tvStatus.setTextColor(ContextCompat.getColor(this, R.color.green));
                    break;
                case "ditolak":
                    tvStatus.setTextColor(ContextCompat.getColor(this, R.color.red));
                    break;
                default:
                    tvStatus.setTextColor(ContextCompat.getColor(this, R.color.orange));
                    break;
            }

            TextView tvTanggal = new TextView(this);
            tvTanggal.setText(p.getTanggal() != null ? p.getTanggal() : "");
            tvTanggal.setTextSize(12);
            tvTanggal.setTypeface(null, Typeface.ITALIC);

            layout.addView(iv);
            layout.addView(tvNamaProduk);
            layout.addView(tvNamaBarangTukar);
            layout.addView(tvHargaTukar);
            layout.addView(tvStatus);
            layout.addView(tvTanggal);

            // Jika yang login adalah pemilik dan status belum diproses
            if (emailUserLogin.equals(p.getEmailPemilik()) && !p.getStatus().equalsIgnoreCase("diterima")
                    && !p.getStatus().equalsIgnoreCase("ditolak")) {

                LinearLayout buttonLayout = new LinearLayout(this);
                buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
                buttonLayout.setPadding(0, dpToPx(8), 0, 0);

                Button btnSetuju = new Button(this);
                btnSetuju.setText("Setujui");
                LinearLayout.LayoutParams setujuParams = new LinearLayout.LayoutParams(0, dpToPx(36), 1);
                btnSetuju.setLayoutParams(setujuParams);
                btnSetuju.setBackgroundColor(ContextCompat.getColor(this, R.color.green));
                btnSetuju.setTextColor(ContextCompat.getColor(this, android.R.color.white));
                btnSetuju.setTextSize(12); // Ukuran teks diperkecil
                btnSetuju.setOnClickListener(v -> {
                    dbHelper.updateStatus(p.getId(), "Diterima");

                    if (emailUserLogin.equals(p.getEmailPemilik())) {
                        dbProdukHelper.deleteProdukById(p.getProdukId());
                    }

                    loadDataPengajuan();
                    Toast.makeText(this, "Pengajuan telah disetujui", Toast.LENGTH_SHORT).show();
                });

                Button btnTolak = new Button(this);
                btnTolak.setText("Tolak");
                LinearLayout.LayoutParams tolakParams = new LinearLayout.LayoutParams(0, dpToPx(36), 1);
                btnTolak.setLayoutParams(tolakParams);
                btnTolak.setBackgroundColor(ContextCompat.getColor(this, R.color.red));
                btnTolak.setTextColor(ContextCompat.getColor(this, android.R.color.white));
                btnTolak.setTextSize(12); // Ukuran teks diperkecil
                btnTolak.setOnClickListener(v -> {
                    dbHelper.updateStatus(p.getId(), "Ditolak");
                    loadDataPengajuan();
                    Toast.makeText(this, "Pengajuan telah ditolak", Toast.LENGTH_SHORT).show();

            });

                buttonLayout.addView(btnSetuju);
                buttonLayout.addView(btnTolak);
                layout.addView(buttonLayout);
            }

            cardView.addView(layout);
            gridProduk.addView(cardView);
        }
    }

    @Override
    protected void onDestroy() {
        if (dbHelper != null) dbHelper.close();
        if (dbProdukHelper != null) dbProdukHelper.close();
        super.onDestroy();
    }
}
