package com.example.rewear_app1;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class EditProdukActivity extends AppCompatActivity {

    private static final int PICK_IMAGES = 1;
    private static final int REQUEST_PERMISSION_CODE = 100;

    private AutoCompleteTextView inputKategori;
    private EditText inputNamaBarang, inputKeterangan, inputHarga;
    private EditText imageUploadButton;
    private ImageView tombolSimpan;
    private LinearLayout previewContainer;

    private ArrayList<Uri> imageUris = new ArrayList<>();
    private DatabaseHelperProduk dbHelper;
    private int produkId;
    private Produk produk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        initViews();
        checkPermissions();
        setupKategoriDropdown();
        setupHargaFormatter();
        loadProdukData();
        setupClickListeners();
    }

    private void initViews() {
        inputKategori = findViewById(R.id.input_kategori);
        inputNamaBarang = findViewById(R.id.input_namabarang);
        inputKeterangan = findViewById(R.id.input_keterangan);
        inputHarga = findViewById(R.id.input_harga);
        imageUploadButton = findViewById(R.id.input_upgambar);
        tombolSimpan = findViewById(R.id.upload_button);
        previewContainer = findViewById(R.id.preview_container);
        dbHelper = new DatabaseHelperProduk(this);

        produkId = getIntent().getIntExtra("produk_id", -1);
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE);
        }
    }

    private void setupKategoriDropdown() {
        // Selalu set adapter dengan ketiga opsi kategori
        String[] kategoriList = {"Beli", "Sewa", "Tukar Tambah"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, kategoriList);
        inputKategori.setAdapter(adapter);

        // Set dropdown muncul saat klik
        inputKategori.setOnClickListener(v -> inputKategori.showDropDown());
    }

    private void setupHargaFormatter() {
        inputHarga.addTextChangedListener(new TextWatcher() {
            private boolean isEditing = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (isEditing) return;
                isEditing = true;

                String cleanString = s.toString().replaceAll("[Rp.,\\s]", "");
                try {
                    long parsed = Long.parseLong(cleanString);
                    String formatted = NumberFormat.getNumberInstance(new Locale("id", "ID")).format(parsed);
                    inputHarga.setText("Rp " + formatted);
                    inputHarga.setSelection(inputHarga.getText().length());
                } catch (NumberFormatException e) {
                    // Ignore error
                }

                isEditing = false;
            }
        });
    }

    private void loadProdukData() {
        if (produkId == -1) {
            Toast.makeText(this, "Produk tidak valid", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        produk = dbHelper.getProdukById(produkId);
        if (produk == null) {
            Toast.makeText(this, "Produk tidak ditemukan", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        inputKategori.setText(produk.getKategori(), false);
        inputKategori.post(() -> inputKategori.showDropDown());

        inputNamaBarang.setText(produk.getNama());
        inputKeterangan.setText(produk.getDeskripsi());

        try {
            String cleanHarga = produk.getHarga().replaceAll("[^0-9]", "");
            long hargaValue = Long.parseLong(cleanHarga);
            String formattedHarga = NumberFormat.getNumberInstance(new Locale("id", "ID")).format(hargaValue);
            inputHarga.setText("Rp " + formattedHarga);
        } catch (NumberFormatException e) {
            inputHarga.setText(produk.getHarga());
        }

        if (produk.getGambarUri() != null && !produk.getGambarUri().isEmpty()) {
            try {
                Uri uri = Uri.parse(produk.getGambarUri());
                imageUris.add(uri);
                tampilkanPreviewGambar();
                imageUploadButton.setText("1 gambar terpilih");
            } catch (Exception e) {
                Toast.makeText(this, "Gagal memuat gambar", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void setupClickListeners() {
        imageUploadButton.setOnClickListener(v -> {
            if (imageUris.size() >= 5) {
                Toast.makeText(this, "Maksimal 5 gambar", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Pilih Gambar"), PICK_IMAGES);
        });

        tombolSimpan.setOnClickListener(v -> updateProduk());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGES && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                imageUris.clear();

                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count && imageUris.size() < 5; i++) {
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        imageUris.add(imageUri);
                    }
                } else if (data.getData() != null && imageUris.size() < 5) {
                    imageUris.add(data.getData());
                }

                Toast.makeText(this, imageUris.size() + " gambar dipilih", Toast.LENGTH_SHORT).show();
                tampilkanPreviewGambar();
                imageUploadButton.setText(imageUris.size() + " gambar terpilih");
            }
        }
    }

    private void tampilkanPreviewGambar() {
        previewContainer.removeAllViews();

        for (Uri uri : imageUris) {
            ImageView imageView = new ImageView(this);
            imageView.setImageURI(uri);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(200, 200);
            layoutParams.setMargins(8, 0, 8, 0);
            imageView.setLayoutParams(layoutParams);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            previewContainer.addView(imageView);
        }
    }

    private void updateProduk() {
        String kategori = inputKategori.getText().toString().trim();
        String namaBarang = inputNamaBarang.getText().toString().trim();
        String keterangan = inputKeterangan.getText().toString().trim();
        String harga = inputHarga.getText().toString().trim().replace("Rp", "").replaceAll("[.,\\s]", "");

        if (kategori.isEmpty() || namaBarang.isEmpty() || harga.isEmpty()) {
            Toast.makeText(this, "Kategori, Nama, Harga wajib diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUris.isEmpty()) {
            Toast.makeText(this, "Silakan pilih gambar", Toast.LENGTH_SHORT).show();
            return;
        }

        String imageUriString = imageUris.get(0).toString();

        boolean result = dbHelper.updateProduk(produkId, namaBarang, imageUriString, harga, kategori, keterangan);
        if (result) {
            Toast.makeText(this, "Produk berhasil diperbarui", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Gagal memperbarui produk", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Izin diperlukan untuk memilih gambar", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
