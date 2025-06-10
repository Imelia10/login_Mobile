package com.example.rewear_app1;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class EditUserActivity extends AppCompatActivity {

    private EditText editFirstName, editLastName, editPhone, editTtl;
    private ImageView imageViewProfile;
    private Button btnSelectPhoto, btnSave;
    private int id;
    private Uri selectedPhotoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

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
        id = intent.getIntExtra("USER_ID", -1);
        editFirstName.setText(intent.getStringExtra("firstName"));
        editLastName.setText(intent.getStringExtra("lastName"));
        editPhone.setText(intent.getStringExtra("phone"));
        editTtl.setText(intent.getStringExtra("ttl"));
        String photoUri = intent.getStringExtra("photoUri");
        if (photoUri != null && !photoUri.isEmpty()) {
            imageViewProfile.setImageURI(Uri.parse(photoUri));
        }

        // Pilih foto dari galeri
        btnSelectPhoto.setOnClickListener(v -> {
            Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickPhoto, 1);
        });

        // Tampilkan DatePicker saat klik TTL
        editTtl.setOnClickListener(v -> showDatePicker());

        // Simpan perubahan
        btnSave.setOnClickListener(v -> {
            String firstName = editFirstName.getText().toString();
            String lastName = editLastName.getText().toString();
            String phone = editPhone.getText().toString();
            String ttl = editTtl.getText().toString();
            String photoUriToUpdate = selectedPhotoUri != null ? selectedPhotoUri.toString() : intent.getStringExtra("photoUri");

            DatabaseHelper dbHelper = new DatabaseHelper(EditUserActivity.this);
            boolean isUpdated = dbHelper.updateUser(id, firstName, lastName, phone, ttl, photoUriToUpdate);

            if (isUpdated) {
                Toast.makeText(EditUserActivity.this, "Data berhasil diperbarui", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(EditUserActivity.this, "Gagal memperbarui data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Fungsi menampilkan DatePickerDialog
    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(EditUserActivity.this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String date = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear);
                    editTtl.setText(date);
                }, year, month, day);

        datePickerDialog.show();
    }

    // Menangani hasil foto yang dipilih
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            selectedPhotoUri = data.getData();
            imageViewProfile.setImageURI(selectedPhotoUri);
        }
    }
}
