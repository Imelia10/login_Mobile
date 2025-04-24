package com.example.rewear_app1;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity {

    EditText phone, firstName, lastName, email, password, alamat, ttl;
    Button btnRegister;
    CheckBox agreeCheckbox;
    ImageView backIcon;
    DatabaseHelper dbHelper;

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

        dbHelper = new DatabaseHelper(this);

        // Tanggal Lahir dengan DatePicker
        ttl.setOnClickListener(v -> showDatePicker());

        // Tombol Daftar
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

            boolean isRegistered = dbHelper.registerUser(phoneInput, firstNameInput, lastNameInput, emailInput, passwordInput, alamatInput, ttlInput);
            if (isRegistered) {
                Toast.makeText(this, "Registrasi Berhasil", Toast.LENGTH_SHORT).show();
                finish(); // Kembali ke LoginActivity
            } else {
                Toast.makeText(this, "Registrasi Gagal", Toast.LENGTH_SHORT).show();
            }
        });

        // Tombol Panah Kembali ke Login
        backIcon.setOnClickListener(v -> finish());
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
}
