//package com.example.rewear_app1;
//
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//public class LoginActivity extends AppCompatActivity {
//
//    EditText email, password;
//    Button btnLogin;
//    TextView daftar;
//    DatabaseHelper dbHelper;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login);
//
//        email = findViewById(R.id.email);
//        password = findViewById(R.id.password);
//        btnLogin = findViewById(R.id.btnLogin);
//        daftar = findViewById(R.id.daftar);
//
//        dbHelper = new DatabaseHelper(this);
//
//        btnLogin.setOnClickListener(view -> {
//            String emailInput = email.getText().toString().trim();
//            String passwordInput = password.getText().toString().trim();
//
//            if (emailInput.isEmpty()) {
//                Toast.makeText(this, "Email tidak boleh kosong!", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            if (passwordInput.isEmpty()) {
//                Toast.makeText(this, "Password tidak boleh kosong!", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            // Tambahan: Cek apakah login sebagai admin
//            if (emailInput.equals("admin@gmail.com") && passwordInput.equals("123")) {
//                Toast.makeText(this, "Login Admin Berhasil", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
//                startActivity(intent);
//                finish(); // Menutup LoginActivity
//                return;
//            }
//
//            // Login biasa (user biasa)
//            User user = dbHelper.getUserByEmail(emailInput);
//            if (user != null) {
//                // Simpan nama pengguna dan email di SharedPreferences
//                SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.putString("firstName", user.getFirstName()); // Gunakan getFirstName()
//                editor.putString("email", user.getEmail()); // Gunakan getEmail()
//                editor.apply();
//
//                // Tampilkan pesan berhasil login
//                Toast.makeText(this, "Login Berhasil", Toast.LENGTH_SHORT).show();
//
//                // Arahkan ke HomeActivity setelah login berhasil
//                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
//                startActivity(intent);
//                finish(); // Menutup LoginActivity agar tidak bisa kembali ke halaman login
//            } else {
//                // Jika email atau password salah
//                Toast.makeText(this, "Email atau Password salah!", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        daftar.setOnClickListener(view -> {
//            // Arahkan ke RegisterActivity untuk pendaftaran pengguna baru
//            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
//            startActivity(intent);
//        });
//    }
//}










//package com.example.rewear_app1;
//
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//public class LoginActivity extends AppCompatActivity {
//
//    EditText email, password;
//    Button btnLogin;
//    TextView daftar;
//    DatabaseHelper dbHelper;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login);
//
//        email = findViewById(R.id.email);
//        password = findViewById(R.id.password);
//        btnLogin = findViewById(R.id.btnLogin);
//        daftar = findViewById(R.id.daftar);
//
//        dbHelper = new DatabaseHelper(this);
//
//        btnLogin.setOnClickListener(view -> {
//            String emailInput = email.getText().toString().trim();
//            String passwordInput = password.getText().toString().trim();
//
//            if (emailInput.isEmpty()) {
//                Toast.makeText(this, "Email tidak boleh kosong!", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            if (passwordInput.isEmpty()) {
//                Toast.makeText(this, "Password tidak boleh kosong!", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            // Cek jika login sebagai admin
//            if (emailInput.equals("admin@gmail.com") && passwordInput.equals("123")) {
//                Toast.makeText(this, "Login Admin Berhasil", Toast.LENGTH_SHORT).show();
//                startActivity(new Intent(LoginActivity.this, AdminActivity.class));
//                finish();
//                return;
//            }
//
//            // Login user biasa
//            User user = dbHelper.getUserByEmailAndPassword(emailInput, passwordInput); // pastikan gunakan validasi password juga
//            if (user != null) {
//                // Simpan data user ke SharedPreferences
//                SharedPreferences sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.putString("user_id", String.valueOf(user.getId())); // Simpan ID pengguna
//                editor.putString("firstName", user.getFirstName());
//                editor.putString("lastName", user.getLastName());
//                editor.putString("email", user.getEmail());
//                editor.apply();
//
//                Toast.makeText(this, "Login Berhasil", Toast.LENGTH_SHORT).show();
//
//                // Masuk ke HomeActivity
//                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
//                finish();
//            } else {
//                Toast.makeText(this, "Email atau Password salah!", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        daftar.setOnClickListener(view -> {
//            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
//        });
//    }
//}



// INI YANG GABUNGAN KATA CGP
package com.example.rewear_app1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText email, password;
    Button btnLogin;
    TextView daftar;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btnLogin = findViewById(R.id.btnLogin);
        daftar = findViewById(R.id.daftar);

        dbHelper = new DatabaseHelper(this);

        btnLogin.setOnClickListener(view -> {
            String emailInput = email.getText().toString().trim();
            String passwordInput = password.getText().toString().trim();

            if (emailInput.isEmpty()) {
                Toast.makeText(this, "Email tidak boleh kosong!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (passwordInput.isEmpty()) {
                Toast.makeText(this, "Password tidak boleh kosong!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Login Admin
            if (emailInput.equals("admin@gmail.com") && passwordInput.equals("123")) {
                Toast.makeText(this, "Login Admin Berhasil", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this, AdminActivity.class));
                finish();
                return;
            }

            // Login User Biasa
            User user = dbHelper.getUserByEmailAndPassword(emailInput, passwordInput);
            if (user != null) {
                // Simpan data user ke SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("user_id", String.valueOf(user.getId()));
                editor.putString("firstName", user.getFirstName());
                editor.putString("lastName", user.getLastName());
                editor.putString("email", user.getEmail());
                editor.apply();

                Toast.makeText(this, "Login Berhasil", Toast.LENGTH_SHORT).show();

                // Arahkan ke HomeActivity
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                intent.putExtra("user_email", user.getEmail());
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Email atau Password salah!", Toast.LENGTH_SHORT).show();
            }
        });

        daftar.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }
}






//
//
//package com.example.rewear_app1;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//public class LoginActivity extends AppCompatActivity {
//
//    EditText email, password;
//    Button btnLogin;
//    TextView daftar;
//    DatabaseHelper dbHelper;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login);
//
//        email = findViewById(R.id.email);
//        password = findViewById(R.id.password);
//        btnLogin = findViewById(R.id.btnLogin);
//        daftar = findViewById(R.id.daftar);
//
//        dbHelper = new DatabaseHelper(this);
//
//        btnLogin.setOnClickListener(view -> {
//            String emailInput = email.getText().toString().trim();
//            String passwordInput = password.getText().toString().trim();
//
//            if (emailInput.isEmpty()) {
//                Toast.makeText(this, "Email tidak boleh kosong!", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            if (passwordInput.isEmpty()) {
//                Toast.makeText(this, "Password tidak boleh kosong!", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            // Login admin
//            if (emailInput.equals("admin@gmail.com") && passwordInput.equals("123")) {
//                Toast.makeText(this, "Login Admin Berhasil", Toast.LENGTH_SHORT).show();
//                startActivity(new Intent(LoginActivity.this, AdminActivity.class));
//                finish();
//                return;
//            }
//
//            // Login user biasa
//            User user = dbHelper.getUserByEmailAndPassword(emailInput, passwordInput);
//            if (user != null) {
//                Toast.makeText(this, "Login Berhasil", Toast.LENGTH_SHORT).show();
//
//                // Kirim email user ke HomeActivity lewat Intent
//                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
//                intent.putExtra("user_email", user.getEmail());
//                startActivity(intent);
//                finish();
//            } else {
//                Toast.makeText(this, "Email atau Password salah!", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        daftar.setOnClickListener(view -> {
//            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
//        });
//    }
//}
