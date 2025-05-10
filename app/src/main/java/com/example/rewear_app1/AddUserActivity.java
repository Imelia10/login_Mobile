package com.example.rewear_app1;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
public class AddUserActivity extends AppCompatActivity {

    private EditText editFirstName, editLastName, editPhone, editEmail, editPassword, editAlamat, editTtl;
    private ImageView imageViewProfile;
    private Button btnSelectPhoto, btnSave;

    private Uri selectedPhotoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        editFirstName = findViewById(R.id.editFirstName);
        editLastName = findViewById(R.id.editLastName);
        editPhone = findViewById(R.id.editPhone);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        editAlamat = findViewById(R.id.editAlamat);
        editTtl = findViewById(R.id.editTtl);
        imageViewProfile = findViewById(R.id.imageViewProfile);
        btnSelectPhoto = findViewById(R.id.btnSelectPhoto);
        btnSave = findViewById(R.id.btnSave);

        btnSelectPhoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 1);
        });

        // Tambahkan DatePicker untuk TTL
        editTtl.setOnClickListener(v -> showDatePicker());

        btnSave.setOnClickListener(v -> {
            // Menonaktifkan tombol Save sementara
            btnSave.setEnabled(false);
            saveUser();
        });
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                AddUserActivity.this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Format tanggal ke DD/MM/YYYY
                    String formattedDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear);
                    editTtl.setText(formattedDate);
                },
                year, month, day);
        datePickerDialog.show();
    }

    private void saveUser() {
        String firstName = editFirstName.getText().toString();
        String lastName = editLastName.getText().toString();
        String phone = editPhone.getText().toString();
        String email = editEmail.getText().toString();
        String password = editPassword.getText().toString();
        String alamat = editAlamat.getText().toString();
        String ttl = editTtl.getText().toString();

        if (firstName.isEmpty() || lastName.isEmpty() || phone.isEmpty() || email.isEmpty() ||
                password.isEmpty() || alamat.isEmpty() || ttl.isEmpty()) {
            Toast.makeText(this, "Harap lengkapi semua data", Toast.LENGTH_SHORT).show();
            btnSave.setEnabled(true);  // Mengaktifkan kembali tombol jika ada yang kosong
            return;
        }

        String photoUri = selectedPhotoUri != null ? selectedPhotoUri.toString() : "";

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        boolean isInserted = dbHelper.addUser(firstName, lastName, phone, email, password, alamat, ttl, photoUri);

        if (isInserted) {
            Toast.makeText(this, "User berhasil ditambahkan", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Gagal menambahkan user", Toast.LENGTH_SHORT).show();
        }
        btnSave.setEnabled(true);  // Mengaktifkan tombol setelah proses selesai
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            selectedPhotoUri = data.getData();
            imageViewProfile.setImageURI(selectedPhotoUri);
        }
    }
}
