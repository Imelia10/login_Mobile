package com.example.rewear_app1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelperPengajuanTuta extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "db_rewear.db";
    private static final int DATABASE_VERSION = 5;

    private static final String TABLE_PENGAJUAN = "pengajuan_tuta";

    // Kolom
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_PRODUK_ID = "produkId";
    private static final String COLUMN_NAMA_PRODUK = "namaProduk";
    private static final String COLUMN_NAMA_BARANG_TUKAR = "namaBarangTukar";
    private static final String COLUMN_HARGA_TUKAR = "hargaTukar";
    private static final String COLUMN_METODE_PEMBAYARAN = "metodePembayaran";
    private static final String COLUMN_TANGGAL = "tanggal";
    private static final String COLUMN_GAMBAR_URI = "gambarUri";
    private static final String COLUMN_EMAIL_PENGAJU = "email_pengaju";
    private static final String COLUMN_EMAIL_PEMILIK = "email_pemilik";

    public DatabaseHelperPengajuanTuta(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_PENGAJUAN + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_PRODUK_ID + " INTEGER," +
                COLUMN_NAMA_PRODUK + " TEXT," +
                COLUMN_NAMA_BARANG_TUKAR + " TEXT," +
                COLUMN_HARGA_TUKAR + " TEXT," +
                COLUMN_METODE_PEMBAYARAN + " TEXT," +
                COLUMN_TANGGAL + " TEXT," +
                COLUMN_GAMBAR_URI + " TEXT," +
                COLUMN_EMAIL_PENGAJU + " TEXT," +
                COLUMN_EMAIL_PEMILIK + " TEXT" +
                ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PENGAJUAN);
        onCreate(db);
    }

    public boolean tambahPengajuan(int produkId, String namaProduk, String namaBarangTukar,
                                   String hargaTukar, String metodePembayaran, String tanggal,
                                   String gambarUri, String emailPengaju, String emailPemilik) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUK_ID, produkId);
        values.put(COLUMN_NAMA_PRODUK, namaProduk);
        values.put(COLUMN_NAMA_BARANG_TUKAR, namaBarangTukar);
        values.put(COLUMN_HARGA_TUKAR, hargaTukar);
        values.put(COLUMN_METODE_PEMBAYARAN, metodePembayaran);
        values.put(COLUMN_TANGGAL, tanggal);
        values.put(COLUMN_GAMBAR_URI, gambarUri);
        values.put(COLUMN_EMAIL_PENGAJU, emailPengaju);
        values.put(COLUMN_EMAIL_PEMILIK, emailPemilik);

        long result = db.insert(TABLE_PENGAJUAN, null, values);
        db.close();
        return result != -1;
    }

    public ArrayList<PengajuanTuta> getPengajuanByUserEmail(String email) {
        ArrayList<PengajuanTuta> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Use LIKE for case-insensitive comparison and trim whitespace
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_PENGAJUAN +
                        " WHERE LOWER(TRIM(" + COLUMN_EMAIL_PENGAJU + ")) = LOWER(TRIM(?))",
                new String[]{email.trim()}
        );
        if (cursor.moveToFirst()) {
            do {
                PengajuanTuta pengajuan = new PengajuanTuta();
                pengajuan.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                pengajuan.setProdukId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUK_ID)));
                pengajuan.setNamaProduk(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAMA_PRODUK)));
                pengajuan.setNamaBarangTukar(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAMA_BARANG_TUKAR)));
                pengajuan.setHargaTukar(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HARGA_TUKAR)));
                pengajuan.setMetodePembayaran(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_METODE_PEMBAYARAN)));
                pengajuan.setTanggal(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TANGGAL)));
                pengajuan.setGambarUri(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GAMBAR_URI)));
                pengajuan.setEmailPengaju(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL_PENGAJU)));
                pengajuan.setEmailPemilik(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL_PEMILIK)));

                list.add(pengajuan);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return list;
    }



    public ArrayList<PengajuanTuta> getAllPengajuan() {
        ArrayList<PengajuanTuta> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PENGAJUAN + " ORDER BY " + COLUMN_ID + " DESC", null);

        if (cursor.moveToFirst()) {
            do {
                PengajuanTuta pengajuan = new PengajuanTuta();
                pengajuan.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                pengajuan.setProdukId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUK_ID)));
                pengajuan.setNamaProduk(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAMA_PRODUK)));
                pengajuan.setNamaBarangTukar(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAMA_BARANG_TUKAR)));
                pengajuan.setHargaTukar(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HARGA_TUKAR)));
                pengajuan.setMetodePembayaran(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_METODE_PEMBAYARAN)));
                pengajuan.setTanggal(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TANGGAL)));
                pengajuan.setGambarUri(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GAMBAR_URI)));
                pengajuan.setEmailPengaju(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL_PENGAJU)));
                pengajuan.setEmailPemilik(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL_PEMILIK)));

                list.add(pengajuan);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return list;
    }
}
