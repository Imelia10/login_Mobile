package com.example.rewear_app1;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EditUserActivity extends AppCompatActivity {

    private EditText editFirstName, editLastName, editPhone, editTtl;
    private ImageView imageViewProfile;
    private Button btnSelectPhoto, btnSave;
    private int id;
    private Uri selectedPhotoUri; // Variabel untuk menyimpan URI foto yang dipilih

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user); // Pastikan layout XML ini ada

        // Inisialisasi komponen
        editFirstName = findViewById(R.id.editFirstName);
        editLastName = findViewById(R.id.editLastName);
        editPhone = findViewById(R.id.editPhone);
        editTtl = findViewById(R.id.editTtl);
        imageViewProfile = findViewById(R.id.imageViewProfile);
        btnSelectPhoto = findViewById(R.id.btnSelectPhoto);
        btnSave = findViewById(R.id.btnSave);

        // Ambil data dari Intent
        Intent intent = getIntent();
        id = intent.getIntExtra("USER_ID", -1); // Ambil ID pengguna
        editFirstName.setText(intent.getStringExtra("firstName"));
        editLastName.setText(intent.getStringExtra("lastName"));
        editPhone.setText(intent.getStringExtra("phone"));
        editTtl.setText(intent.getStringExtra("ttl"));
        String photoUri = intent.getStringExtra("photoUri");
        if (photoUri != null && !photoUri.isEmpty()) {
            imageViewProfile.setImageURI(Uri.parse(photoUri));
        }

        // Pilih foto
        btnSelectPhoto.setOnClickListener(v -> {
            // Intent untuk memilih gambar dari galeri
            Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickPhoto, 1); // 1 adalah request code untuk memilih foto
        });

        // Simpan perubahan
        btnSave.setOnClickListener(v -> {
            // Ambil data baru dari EditText
            String firstName = editFirstName.getText().toString();
            String lastName = editLastName.getText().toString();
            String phone = editPhone.getText().toString();
            String ttl = editTtl.getText().toString();

            // Gunakan URI foto yang dipilih, atau URI lama jika tidak ada foto baru
            String photoUriToUpdate = selectedPhotoUri != null ? selectedPhotoUri.toString() : intent.getStringExtra("photoUri");

            // Update data ke database
            DatabaseHelper dbHelper = new DatabaseHelper(EditUserActivity.this);
            boolean isUpdated = dbHelper.updateUser(id, firstName, lastName, phone, ttl, photoUriToUpdate);

            if (isUpdated) {
                Toast.makeText(EditUserActivity.this, "Data berhasil diperbarui", Toast.LENGTH_SHORT).show();
                finish(); // Kembali ke halaman AdminUserActivity
            } else {
                Toast.makeText(EditUserActivity.this, "Gagal memperbarui data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Menangani hasil foto yang dipilih
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            selectedPhotoUri = data.getData(); // Ambil URI foto yang dipilih
            imageViewProfile.setImageURI(selectedPhotoUri); // Tampilkan foto yang dipilih di ImageView
        }
    }
}
