package com.example.rewear_app1;
import android.content.Intent;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_PERMISSION_CODE = 100;

    EditText phone, firstName, lastName, email, password, alamat, ttl;
    Button btnRegister;
    CheckBox agreeCheckbox;
    ImageView backIcon, profileImage;
    DatabaseHelper dbHelper;

    Uri selectedImageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        phone = findViewById(R.id.input_phone);
        firstName = findViewById(R.id.input_first_name);
        lastName = findViewById(R.id.input_last_name);
        email = findViewById(R.id.input_email);
        password = findViewById(R.id.input_pass);
        alamat = findViewById(R.id.input_alamat);
        ttl = findViewById(R.id.input_ttl);
        btnRegister = findViewById(R.id.register_button);
        agreeCheckbox = findViewById(R.id.checkbox_agree);
        backIcon = findViewById(R.id.back_icon);
        profileImage = findViewById(R.id.image_profile);

        dbHelper = new DatabaseHelper(this);

        // Periksa izin penyimpanan
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE);
        }

        profileImage.setOnClickListener(v -> openGallery());
        ttl.setOnClickListener(v -> showDatePicker());

        btnRegister.setOnClickListener(view -> {
            String phoneInput = phone.getText().toString().trim();
            String firstNameInput = firstName.getText().toString().trim();
            String lastNameInput = lastName.getText().toString().trim();
            String emailInput = email.getText().toString().trim();
            String passwordInput = password.getText().toString().trim();
            String alamatInput = alamat.getText().toString().trim();
            String ttlInput = ttl.getText().toString().trim();

            if (phoneInput.isEmpty() || firstNameInput.isEmpty() || lastNameInput.isEmpty() ||
                    emailInput.isEmpty() || passwordInput.isEmpty() || alamatInput.isEmpty() || ttlInput.isEmpty()) {
                Toast.makeText(this, "Semua field harus diisi!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!emailInput.contains("@")) {
                Toast.makeText(this, "Email tidak valid!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!agreeCheckbox.isChecked()) {
                Toast.makeText(this, "Kamu harus menyetujui Terms of Service!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (dbHelper.isEmailExists(emailInput)) {
                Toast.makeText(this, "Email sudah terdaftar!", Toast.LENGTH_SHORT).show();
                return;
            }

            String photoUriString = selectedImageUri != null ? selectedImageUri.toString() : "";

            boolean isRegistered = dbHelper.registerUser(phoneInput, firstNameInput, lastNameInput,
                    emailInput, passwordInput, alamatInput, ttlInput, photoUriString);

            if (isRegistered) {
                Toast.makeText(this, "Registrasi Berhasil", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Registrasi gagal!", Toast.LENGTH_SHORT).show();
            }
        });

        backIcon.setOnClickListener(v -> finish());
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            profileImage.setImageURI(selectedImageUri); // Set image ke ImageView
        }
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
            ttl.setText(date);
        }, year, month, day).show();
    }

    // Menghandle hasil permintaan izin
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Izin diperlukan untuk memilih foto", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
