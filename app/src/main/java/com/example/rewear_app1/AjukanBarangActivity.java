package com.example.rewear_app1;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class AjukanBarangActivity extends AppCompatActivity {
    private static final String TAG = "AjukanBarangActivity";
    private static final int MAX_IMAGES = 5;

    private ImageView backIcon, imageBarang, uploadIcon, submitButton;
    private TextView productName, productPrice, sellerName, uploadText;
    private EditText input_namabarang, input_keterangan, input_harga;
    private LinearLayout previewContainer;

    private Produk produk;
    private User penjual;
    private DatabaseHelperProduk dbHelperProduk;
    private DatabaseHelper dbHelperUser;

    private ActivityResultLauncher<String[]> imagePickerLauncher;
    private ArrayList<Uri> selectedImages = new ArrayList<>();

    private boolean isFormatting = false;

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
        setupImagePicker();
        setupButtons();
        setupPriceValidation();
        setupUploadClick();
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
    }

    private void setupPriceValidation() {
        input_harga.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

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
        int produkId = getIntent().getIntExtra("produk_id", -1);
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

        try {
            int sellerId = Integer.parseInt(penjualId);
            penjual = dbHelperUser.getUserById(sellerId);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "ID Penjual tidak valid", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadProductData() {
        productName.setText(produk.getNama());
        productPrice.setText("Rp " + produk.getHarga());

        if (produk.getGambarUri() != null && !produk.getGambarUri().isEmpty()) {
            try {
                imageBarang.setImageURI(Uri.parse(produk.getGambarUri()));
            } catch (Exception e) {
                Log.e(TAG, "Gagal memuat gambar produk", e);
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

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.OpenMultipleDocuments(),
                uris -> {
                    if (uris == null || uris.isEmpty()) return;

                    int spaceLeft = MAX_IMAGES - selectedImages.size();
                    int countToAdd = Math.min(spaceLeft, uris.size());

                    for (int i = 0; i < countToAdd; i++) {
                        Uri uri = uris.get(i);
                        selectedImages.add(uri);
                        addImagePreview(uri);
                    }

                    if (uris.size() > countToAdd) {
                        Toast.makeText(this, "Maksimal 5 gambar", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void setupUploadClick() {
        View.OnClickListener openImagePicker = v -> {
            // Buka pemilih gambar, dengan tipe gambar
            imagePickerLauncher.launch(new String[] {"image/*"});
        };
        uploadIcon.setOnClickListener(openImagePicker);
        uploadText.setOnClickListener(openImagePicker);
        imageBarang.setOnClickListener(openImagePicker); // opsional, klik gambar juga bisa upload ulang
    }


    private void setupButtons() {
        submitButton.setOnClickListener(v -> {
            String namaTukar = input_namabarang.getText().toString().trim();
            String keterangan = input_keterangan.getText().toString().trim();
            String hargaTukar = input_harga.getText().toString().trim();

            // Validasi input
            if (namaTukar.isEmpty() || keterangan.isEmpty() || hargaTukar.isEmpty()) {
                Toast.makeText(this, "Harap lengkapi semua field", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kirim data ke DetailTukarTambahActivity
            Intent intent = new Intent(this, DetailTukarTambahActivity.class);
            intent.putExtra("produk_id", produk.getId());
            intent.putExtra("nama_barang_tukar", namaTukar);
            intent.putExtra("harga_tukar", hargaTukar.replaceAll("[Rp.\\s]", ""));
            intent.putExtra("keterangan_tukar", keterangan);

            // Convert ArrayList<Uri> to ArrayList<String> lalu ke Parcelable
            ArrayList<String> uriStrings = new ArrayList<>();
            for (Uri uri : selectedImages) {
                uriStrings.add(uri.toString());
            }
            intent.putStringArrayListExtra("gambar_terpilih", uriStrings);

            startActivity(intent);
            finish(); // Tutup activity ini
        });
    }

    private void addImagePreview(Uri uri) {
        ImageView imageView = new ImageView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(200, 200);
        layoutParams.setMargins(8, 8, 8, 8);
        imageView.setLayoutParams(layoutParams);
        imageView.setImageURI(uri);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        previewContainer.addView(imageView);
    }

    @Override
    protected void onDestroy() {
        if (dbHelperProduk != null) dbHelperProduk.close();
        if (dbHelperUser != null) dbHelperUser.close();
        super.onDestroy();
    }
}
