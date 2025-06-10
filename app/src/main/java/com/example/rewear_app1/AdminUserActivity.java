package com.example.rewear_app1;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.List;

public class AdminUserActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 101;
    private TableLayout tableLayout;
    private DatabaseHelper dbHelper;
    private EditText editUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user);

        // Perizinan
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);
        }

        // Inisialisasi komponen
        tableLayout = findViewById(R.id.tableLayoutUsers);
        dbHelper = new DatabaseHelper(this);
        editUserId = findViewById(R.id.editUserId); // EditText untuk memasukkan ID user

        // Menangani klik tombol Edit, Tambah, Hapus
        findViewById(R.id.btnEdit).setOnClickListener(v -> handleEditUser());
        findViewById(R.id.btnAdd).setOnClickListener(v -> handleAddUser());
        findViewById(R.id.btnDelete).setOnClickListener(v -> handleDeleteUser());

        loadUserDataToTable(); // Memuat data pengguna ke tabel

        // Klik logoImage --> Balik ke HomeActivity
        ImageView logoImage = findViewById(R.id.back);
        logoImage.setOnClickListener(view -> {
            Intent intent = new Intent(AdminUserActivity.this, AdminActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void loadUserDataToTable() {
        List<User> userList = dbHelper.getAllUsers();

        if (userList.size() == 0) {
            Toast.makeText(this, "Tidak ada data pengguna", Toast.LENGTH_SHORT).show();
        }

        for (User user : userList) {
            TableRow row = new TableRow(this);
            row.setPadding(8, 8, 8, 8);

            TextView id = createCell(String.valueOf(user.getId()));
            TextView fname = createCell(user.getFirstName());
            TextView lname = createCell(user.getLastName());
            TextView phone = createCell(user.getPhone());
            TextView email = createCell(user.getEmail());
            TextView password = createCell("******");
            TextView alamat = createCell(user.getAlamat());
            TextView ttl = createCell(user.getTtl());

            ImageView photo = new ImageView(this);
            photo.setLayoutParams(new TableRow.LayoutParams(100, 100));
            photo.setPadding(8, 8, 8, 8);
            if (user.getPhotoUri() != null && !user.getPhotoUri().isEmpty()) {
                photo.setImageURI(Uri.parse(user.getPhotoUri()));
            } else {
                photo.setImageResource(R.drawable.profil1);
            }

            row.addView(id);
            row.addView(fname);
            row.addView(lname);
            row.addView(phone);
            row.addView(email);
            row.addView(password);
            row.addView(alamat);
            row.addView(ttl);
            row.addView(photo);

            tableLayout.addView(row);
        }
    }

    private TextView createCell(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setPadding(16, 8, 16, 8);
        return tv;
    }

    private void handleEditUser() {
        String userId = editUserId.getText().toString().trim();
        if (!userId.isEmpty()) {
            int id = Integer.parseInt(userId);
            // Logika untuk membuka halaman edit berdasarkan ID user
            Intent intent = new Intent(AdminUserActivity.this, EditUserActivity.class);
            intent.putExtra("USER_ID", id);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Masukkan ID User terlebih dahulu", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleAddUser() {
        // Logika untuk membuka halaman tambah user
        Intent intent = new Intent(AdminUserActivity.this, AddUserActivity.class);
        startActivity(intent);
    }

    private void handleDeleteUser() {
        String userId = editUserId.getText().toString().trim();
        if (!userId.isEmpty()) {
            int id = Integer.parseInt(userId);
            boolean isDeleted = dbHelper.deleteUser(id);
            if (isDeleted) {
                Toast.makeText(this, "User berhasil dihapus", Toast.LENGTH_SHORT).show();
                refreshTable();
            } else {
                Toast.makeText(this, "Gagal menghapus user", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Masukkan ID User terlebih dahulu", Toast.LENGTH_SHORT).show();
        }
    }

    private void refreshTable() {
        tableLayout.removeAllViews();
        loadUserDataToTable(); // Memuat ulang data setelah penghapusan
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Izin diberikan", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Izin ditolak", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
