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
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class AjukanBarangActivity extends AppCompatActivity {
    private static final String TAG = "AjukanBarangActivity";
    private static final int MAX_IMAGES = 5;
    private static final int PICK_IMAGES = 1;
    private static final int REQUEST_PERMISSION_CODE = 100;

    private ImageView backIcon, imageBarang, uploadIcon, submitButton;
    private TextView productName, productPrice, sellerName, uploadText;
    private EditText input_namabarang, input_keterangan, input_harga;
    private LinearLayout previewContainer;

    private Produk produk;
    private User penjual;
    private DatabaseHelperProduk dbHelperProduk;
    private DatabaseHelper dbHelperUser;

    private ArrayList<Uri> selectedImages = new ArrayList<>();

    private boolean isFormatting = false;

    // Variabel tambahan
    private int produkId;
    private String namaProduk;
    private String namaBarangTukar;
    private String hargaTukar;
    private String metodePembayaran = "Tukar Tambah";
    private String tanggal = "";
    private String gambarUri = "";
    private String gambarJual = "";
    private String emailPemilik = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajukan_barang);

        dbHelperProduk = new DatabaseHelperProduk(this);
        dbHelperUser = new DatabaseHelper(this);

        initViews();
        getIntentData();
        loadProductData();
        loadSellerData();
        setupButtons();
        setupPriceValidation();
        setupUploadClick();

        // Cek permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_CODE);
        }
    }

    private void initViews() {
        backIcon = findViewById(R.id.back_icon);
        imageBarang = findViewById(R.id.imageBarang);
        productName = findViewById(R.id.nama_produk);
        productPrice = findViewById(R.id.harga_produk);
        sellerName = findViewById(R.id.namaPenyelenggara);
        uploadIcon = findViewById(R.id.upload_icon);
        uploadText = findViewById(R.id.upload_text);
        submitButton = findViewById(R.id.btnDaftar);
        previewContainer = findViewById(R.id.preview_container);

        input_namabarang = findViewById(R.id.input_namabarang);
        input_keterangan = findViewById(R.id.input_keterangan);
        input_harga = findViewById(R.id.input_harga);

        backIcon.setOnClickListener(v -> finish());
    }

    private void setupPriceValidation() {
        input_harga.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if (isFormatting) return;

                isFormatting = true;
                try {
                    String cleanString = s.toString().replaceAll("[Rp.,\\s]", "");
                    if (!cleanString.isEmpty()) {
                        int enteredValue = Integer.parseInt(cleanString);
                        int hargaProduk = Integer.parseInt(produk.getHarga().replaceAll("[^0-9]", ""));

                        if (enteredValue > hargaProduk) {
                            input_harga.setError("Harga penawaran tidak boleh melebihi harga produk");
                        } else {
                            input_harga.setError(null);
                        }

                        String formatted = NumberFormat.getCurrencyInstance(new Locale("in", "ID"))
                                .format((double) enteredValue)
                                .replace("Rp", "Rp ")
                                .replace(",00", "");
                        input_harga.setText(formatted);
                        input_harga.setSelection(formatted.length());
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Format harga error", e);
                } finally {
                    isFormatting = false;
                }
            }
        });
    }

    private void getIntentData() {
        produkId = getIntent().getIntExtra("produk_id", -1);
        String penjualId = getIntent().getStringExtra("penjual_id");

        if (produkId == -1 || penjualId == null) {
            Toast.makeText(this, "Data produk tidak valid", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        produk = dbHelperProduk.getProdukById(produkId);
        if (produk == null) {
            Toast.makeText(this, "Produk tidak ditemukan", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        namaProduk = produk.getNama();

        try {
            int sellerId = Integer.parseInt(penjualId);
            penjual = dbHelperUser.getUserById(sellerId);

            if (penjual != null) {
                emailPemilik = penjual.getEmail();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "ID Penjual tidak valid", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadProductData() {
        productName.setText(produk.getNama());
        productPrice.setText("Rp " + produk.getHarga());

        String gambarUri = produk.getGambarUri();
        if (gambarUri != null && !gambarUri.isEmpty()) {
            String[] gambarArray = gambarUri.split("\n");
            if (gambarArray.length > 0 && !gambarArray[0].isEmpty()) {
                gambarJual = gambarArray[0];
                imageBarang.setImageURI(Uri.parse(gambarArray[0]));
            } else {
                imageBarang.setImageResource(R.drawable.profil1);
            }
        } else {
            imageBarang.setImageResource(R.drawable.profil1);
        }
    }

    private void loadSellerData() {
        if (penjual != null) {
            sellerName.setText(penjual.getFirstName() + " " + penjual.getLastName());
        } else {
            sellerName.setText("Penjual tidak diketahui");
        }
    }

    private void setupUploadClick() {
        View.OnClickListener openImagePicker = v -> {
            if (selectedImages.size() >= MAX_IMAGES) {
                Toast.makeText(this, "Maksimal 5 gambar", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Pilih Gambar"), PICK_IMAGES);
        };

        uploadIcon.setOnClickListener(openImagePicker);
        uploadText.setOnClickListener(openImagePicker);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGES && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count && selectedImages.size() < MAX_IMAGES; i++) {
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        selectedImages.add(imageUri);
                    }
                } else if (data.getData() != null && selectedImages.size() < MAX_IMAGES) {
                    selectedImages.add(data.getData());
                }
                Toast.makeText(this, selectedImages.size() + " gambar dipilih", Toast.LENGTH_SHORT).show();
                showImagePreviews();
            }
        }
    }

    private void showImagePreviews() {
        previewContainer.removeAllViews();

        for (Uri uri : selectedImages) {
            ImageView imageView = new ImageView(this);
            imageView.setImageURI(uri);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(200, 200);
            layoutParams.setMargins(8, 0, 8, 0);
            imageView.setLayoutParams(layoutParams);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            previewContainer.addView(imageView);
        }
    }

    private void setupButtons() {
        submitButton.setOnClickListener(v -> {
            namaBarangTukar = input_namabarang.getText().toString().trim();
            String keterangan = input_keterangan.getText().toString().trim();
            hargaTukar = input_harga.getText().toString().trim();

            if (namaBarangTukar.isEmpty() || keterangan.isEmpty() || hargaTukar.isEmpty()) {
                Toast.makeText(this, "Harap lengkapi semua field", Toast.LENGTH_SHORT).show();
                return;
            }

            String hargaTawarBersih = hargaTukar.replaceAll("[Rp.\\s]", "");

            if (!selectedImages.isEmpty()) {
                gambarUri = selectedImages.get(0).toString();
            } else {
                gambarUri = "";
            }

            SharedPreferences sp = getSharedPreferences("user_session", MODE_PRIVATE);
            String emailPengaju = sp.getString("email_login", "");

            boolean success = true;

            if (success) {
                Toast.makeText(this, "Detail Pengajuan Anda", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(this, DetailTukarTambahActivity.class);
                intent.putExtra("produk_id", produk.getId());
                intent.putExtra("nama_barang_tukar", namaBarangTukar);
                intent.putExtra("harga_tukar", hargaTawarBersih);
                intent.putExtra("keterangan_tukar", keterangan);
                intent.putExtra("gambarJual", gambarJual);
                intent.putExtra("gambarTuta", gambarUri);

                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Gagal menyimpan pengajuan", Toast.LENGTH_SHORT).show();
            }
        });
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

    @Override
    protected void onDestroy() {
        if (dbHelperProduk != null) dbHelperProduk.close();
        if (dbHelperUser != null) dbHelperUser.close();
        super.onDestroy();
    }
}