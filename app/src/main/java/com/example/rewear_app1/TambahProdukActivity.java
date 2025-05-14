package com.example.rewear_app1;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class TambahProdukActivity extends AppCompatActivity {

    private static final int PICK_IMAGES = 1;
    private static final int REQUEST_PERMISSION_CODE = 100;
    private String namaDepan, namaBelakang;

    private AutoCompleteTextView inputKategori;
    private EditText inputNamaBarang, inputKeterangan, inputHarga;
    private EditText imageUploadButton;
    private ImageView tombolSimpan;

    private ArrayList<Uri> imageUris = new ArrayList<>();
    private DatabaseHelperProduk dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        // Inisialisasi view
        inputKategori = findViewById(R.id.input_kategori);
        inputNamaBarang = findViewById(R.id.input_namabarang);
        inputKeterangan = findViewById(R.id.input_keterangan);
        inputHarga = findViewById(R.id.input_harga);
        imageUploadButton = findViewById(R.id.input_upgambar);
        tombolSimpan = findViewById(R.id.upload_button);

        dbHelper = new DatabaseHelperProduk(this);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE);
        }

        String[] kategoriList = {"Beli", "Sewa", "Tukar Tambah"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, kategoriList);
        inputKategori.setAdapter(adapter);
        inputKategori.setOnClickListener(v -> inputKategori.showDropDown());

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
                    e.printStackTrace();
                }

                isEditing = false;
            }
        });

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

        tombolSimpan.setOnClickListener(v -> simpanKeDatabase());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGES && resultCode == Activity.RESULT_OK) {
            if (data != null) {
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
            }
        }
    }

    private void simpanKeDatabase() {
        String kategori = inputKategori.getText().toString().trim();
        String namaBarang = inputNamaBarang.getText().toString().trim();
        String keterangan = inputKeterangan.getText().toString().trim();
        String harga = inputHarga.getText().toString().trim().replace("Rp", "").replaceAll("[.,\\s]", "");

        if (kategori.isEmpty() || namaBarang.isEmpty() || harga.isEmpty()) {
            Toast.makeText(this, "Kategori, Nama, Harga wajib diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cek apakah ada gambar yang dipilih
        if (imageUris.isEmpty()) {
            Toast.makeText(this, "Silakan pilih gambar", Toast.LENGTH_SHORT).show();
            return;
        }

        // Ambil gambar URI pertama
//String gambarUri = imageUris.get(0).toString(); // Menyimpan satu gambar URI



        SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
        String userId = prefs.getString("user_id", "");
        String namaDepan = prefs.getString("nama_depan", "");
        String namaBelakang = prefs.getString("nama_belakang", "");

        if (userId.isEmpty()) {
            Toast.makeText(this, "User tidak dikenali. Silakan login ulang.", Toast.LENGTH_SHORT).show();
            return;
        }


        String imageUriString = imageUris.get(0).toString();

        // Menyimpan data produk ke database
        long result = dbHelper.tambahProduk(namaBarang, kategori, keterangan, harga, imageUriString, userId);
        if (result != -1) {
            Toast.makeText(this, "Produk berhasil disimpan", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(TambahProdukActivity.this, TransaksiActivity.class);
            intent.putExtra("kategori_terpilih", kategori);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Gagal menyimpan produk", Toast.LENGTH_SHORT).show();
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

