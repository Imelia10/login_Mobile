package com.example.rewear_app1;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class AdminVoucherActivity extends AppCompatActivity {

    private LinearLayout voucherList;
    private ImageButton btnTambah;
    private ImageView back;
    private DatabaseHelper dbHelper;
    private boolean isAdmin = false;
    private static final String TABLE_VOUCHER = "voucher";
    private static final String COLUMN_VOUCHER_JUDUL = "judul";
    private static final String COLUMN_VOUCHER_SYARAT = "syarat";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_voucher);

        voucherList = findViewById(R.id.voucherList);
        btnTambah = findViewById(R.id.btnTambah);
        back = findViewById(R.id.back);
        dbHelper = new DatabaseHelper(this);

        // Cek role admin atau user (dari intent)
        isAdmin = getIntent().getBooleanExtra("IS_ADMIN", false);

        // Tombol kembali
        back.setOnClickListener(v -> finish());

        // Load voucher dari database
        loadVouchers();

        // Admin: tombol tambah diskon preset
        btnTambah.setOnClickListener(v -> {
            if (isAdmin) {
                showTambahDialog();
            } else {
                Toast.makeText(this, "Hanya admin yang bisa menambah voucher", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showTambahDialog() {
        final String[] preset = {
                "DISKON 5% s/d Rp 10RB",
                "DISKON 7% s/d Rp 15RB",
                "DISKON 10% s/d Rp 25RB",
                "DISKON 15% s/d Rp 35RB"
        };
        final String[] syarat = {
                "Minimal 2 transaksi",
                "Minimal 3 transaksi",
                "Minimal 6 transaksi",
                "Minimal 10 transaksi"
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pilih Voucher");
        builder.setItems(preset, (dialog, which) -> {
            // Generate kode voucher unik
            String kode = "VCH" + System.currentTimeMillis() % 10000;

            // Insert voucher dengan kode
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_VOUCHER_JUDUL, preset[which]);
            values.put(COLUMN_VOUCHER_SYARAT, syarat[which]);
            values.put("kode", kode);

            long inserted = db.insert(TABLE_VOUCHER, null, values);
            if (inserted != -1) {
                Toast.makeText(AdminVoucherActivity.this, "Voucher ditambahkan", Toast.LENGTH_SHORT).show();
                loadVouchers();
            } else {
                Toast.makeText(AdminVoucherActivity.this, "Gagal menambahkan voucher", Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }

    private void loadVouchers() {
        voucherList.removeAllViews();
        Cursor cursor = dbHelper.getAllVouchers();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String judul = cursor.getString(cursor.getColumnIndexOrThrow("judul"));
            String syarat = cursor.getString(cursor.getColumnIndexOrThrow("syarat"));
            tambahVoucher(id, judul, syarat);
        }
        cursor.close();
    }

    private void tambahVoucher(int id, String judul, String syarat) {
        // Buat CardView programmatically
        CardView cardView = new CardView(this);
        cardView.setRadius(25);
        cardView.setCardElevation(10);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 0, 0, 30);
        cardView.setLayoutParams(cardParams);

        // Isi CardView
        LinearLayout isi = new LinearLayout(this);
        isi.setOrientation(LinearLayout.VERTICAL);
        isi.setPadding(30, 30, 30, 30);

        TextView tvJudul = new TextView(this);
        tvJudul.setText(judul);
        tvJudul.setTextSize(18);
        tvJudul.setPadding(0, 0, 0, 10);

        TextView tvSyarat = new TextView(this);
        tvSyarat.setText(syarat);
        tvSyarat.setTextSize(14);
        tvSyarat.setPadding(0, 0, 0, 20);

        Button btnVoucher = new Button(this);
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        btnVoucher.setLayoutParams(btnParams);

        if (isAdmin) {
            btnVoucher.setText("Hapus");
            btnVoucher.setBackgroundTintList(getResources().getColorStateList(android.R.color.holo_red_dark));
            btnVoucher.setOnClickListener(v -> {
                dbHelper.deleteVoucher(id);
                loadVouchers();
            });
        } else {
            btnVoucher.setText("Klaim");
            btnVoucher.setBackgroundTintList(getResources().getColorStateList(R.color.red));
            btnVoucher.setOnClickListener(v -> {
                Toast.makeText(this, "Voucher diklaim!", Toast.LENGTH_SHORT).show();
                btnVoucher.setEnabled(false);
                btnVoucher.setText("Sudah Klaim");
                btnVoucher.setBackgroundTintList(getResources().getColorStateList(android.R.color.darker_gray));
            });
        }

        // Gabungkan semua komponen ke CardView
        isi.addView(tvJudul);
        isi.addView(tvSyarat);
        isi.addView(btnVoucher);
        cardView.addView(isi);
        voucherList.addView(cardView);
    }
}
