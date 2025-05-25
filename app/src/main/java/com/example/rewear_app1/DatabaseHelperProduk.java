package com.example.rewear_app1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelperProduk extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ReWearDB";
    private static final int DATABASE_VERSION = 8;

    private static final String TABLE_PRODUK = "produk";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAMA = "nama";
    private static final String COLUMN_KATEGORI = "kategori";
    private static final String COLUMN_KETERANGAN = "deskripsi";
    private static final String COLUMN_HARGA = "harga";
    private static final String COLUMN_GAMBAR_URI = "gambarUri";
    private static final String COLUMN_EMAIL_PEMILIK = "emailPemilik";
    private static final String COLUMN_NAMA_DEPAN = "namaDepan";
    private static final String COLUMN_NAMA_BELAKANG = "namaBelakang";
    private static final String COLUMN_ID_PENJUAL = "idPenjual";
    private static final String COLUMN_STATUS = "status";

    public DatabaseHelperProduk(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Membuat tabel produk dengan kolom yang lengkap dan default status 'Tersedia'
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_PRODUK + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAMA + " TEXT, " +
                COLUMN_KATEGORI + " TEXT, " +
                COLUMN_KETERANGAN + " TEXT, " +
                COLUMN_HARGA + " TEXT, " +
                COLUMN_GAMBAR_URI + " TEXT, " +
                COLUMN_EMAIL_PEMILIK + " TEXT, " +
                COLUMN_NAMA_DEPAN + " TEXT, " +
                COLUMN_NAMA_BELAKANG + " TEXT, " +
                COLUMN_ID_PENJUAL + " TEXT, " +
                COLUMN_STATUS + " TEXT DEFAULT 'Tersedia'" +
                ");";
        db.execSQL(CREATE_TABLE);
    }

    // Upgrade database dengan cara drop dan create ulang tabel
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUK);
        onCreate(db);
    }

    // Menambah produk baru ke database
    public long tambahProduk(String namaBarang, String kategori, String keterangan, String harga, String imageUriString, String userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAMA, namaBarang);
        values.put(COLUMN_KATEGORI, kategori);
        values.put(COLUMN_KETERANGAN, keterangan);
        values.put(COLUMN_HARGA, harga);
        values.put(COLUMN_GAMBAR_URI, imageUriString);
        values.put(COLUMN_ID_PENJUAL, userId);
        values.put(COLUMN_STATUS, "Tersedia"); // Default status

        long result = db.insert(TABLE_PRODUK, null, values);
        db.close();
        return result;
    }

    // Mendapatkan daftar produk berdasarkan kategori, keyword pencarian, dan userId penjual, hanya yang statusnya Tersedia
    public List<Produk> getProdukByKategoriAndKeywordAndUser(String kategori, String keyword, String userId) {
        List<Produk> produkList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selection = COLUMN_KATEGORI + " = ? AND " +
                COLUMN_ID_PENJUAL + " = ? AND (" +
                COLUMN_NAMA + " LIKE ? OR " +
                COLUMN_KETERANGAN + " LIKE ?) AND " +
                COLUMN_STATUS + " = ?";
        String[] selectionArgs = new String[]{kategori, userId, "%" + keyword + "%", "%" + keyword + "%", "Tersedia"};

        Cursor cursor = db.query(TABLE_PRODUK, null, selection, selectionArgs, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                Produk produk = getProdukFromCursor(cursor);
                produkList.add(produk);
            }
            cursor.close();
        }

        db.close();
        return produkList;
    }

    // Mendapatkan daftar produk berdasarkan kategori dan keyword, hanya produk dengan status Tersedia
    public List<Produk> getProdukByKategoriAndKeyword(String kategori, String keyword) {
        List<Produk> produkList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selection = COLUMN_KATEGORI + " = ? AND " + COLUMN_NAMA + " LIKE ? AND " + COLUMN_STATUS + " = ?";
        String[] selectionArgs = {kategori, "%" + keyword + "%", "Tersedia"};

        Cursor cursor = db.query(TABLE_PRODUK, null, selection, selectionArgs, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                Produk produk = getProdukFromCursor(cursor);
                produkList.add(produk);
            }
            cursor.close();
        }

        db.close();
        return produkList;
    }

    // Mendapatkan produk berdasarkan ID, hanya yang status Tersedia
    public Produk getProdukById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selection = COLUMN_ID + " = ? AND " + COLUMN_STATUS + " = ?";
        String[] selectionArgs = {String.valueOf(id), "Tersedia"};

        Cursor cursor = db.query(TABLE_PRODUK, null, selection, selectionArgs, null, null, null);
        Produk produk = null;

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                produk = getProdukFromCursor(cursor);
            }
            cursor.close();
        }

        db.close();
        return produk;
    }

    // Mendapatkan produk untuk histori (termasuk yang dihapus)
    public Produk getProdukForHistory(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_PRODUK, null, COLUMN_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        Produk produk = null;

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                produk = getProdukFromCursor(cursor);
            }
            cursor.close();
        }

        db.close();
        return produk;
    }

    // Update status produk, misal menjadi "Dihapus"
    public boolean updateStatusProduk(int id, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STATUS, status);

        int rows = db.update(TABLE_PRODUK, values, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return rows > 0;
    }

    // Update data produk secara lengkap (kecuali emailPemilik, namaDepan, namaBelakang tidak diupdate di sini)
    public boolean updateProduk(int id, String nama, String gambarUri, String harga, String kategori, String deskripsi) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAMA, nama);
        values.put(COLUMN_GAMBAR_URI, gambarUri);
        values.put(COLUMN_HARGA, harga);
        values.put(COLUMN_KATEGORI, kategori);
        values.put(COLUMN_KETERANGAN, deskripsi);

        int rows = db.update(TABLE_PRODUK, values, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return rows > 0;
    }

    // Hapus produk dengan mengganti status menjadi "Dihapus"
    public boolean hapusProduk(int id) {
        return updateStatusProduk(id, "Dihapus");
    }

    // Mengambil string gambarUri dari produk berdasarkan id
    public String getGambarUrisById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_GAMBAR_URI + " FROM " + TABLE_PRODUK + " WHERE " + COLUMN_ID + " = ?", new String[]{String.valueOf(id)});

        String uris = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                uris = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GAMBAR_URI));
            }
            cursor.close();
        }
        db.close();
        return uris;
    }

    // Debug: Print semua user dari tabel users (asumsi ada tabel users)
    public void printAllUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT email, firstName, lastName, photoUri FROM users", null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String email = cursor.getString(0);
                String first = cursor.getString(1);
                String last = cursor.getString(2);
                String photo = cursor.getString(3);
                Log.d("CEK_USERS", "Email: " + email + ", Nama: " + first + " " + last + ", Foto: " + photo);
            }
            cursor.close();
        }

        db.close();
    }

    // Helper method untuk konversi Cursor ke objek Produk
    private Produk getProdukFromCursor(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
        String nama = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAMA));
        String kategori = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KATEGORI));
        String deskripsi = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KETERANGAN));
        String harga = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HARGA));
        String gambarUri = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GAMBAR_URI));
        String idPenjual = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID_PENJUAL));
        String status = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS));
        return new Produk(id, nama, gambarUri, harga, kategori, deskripsi, idPenjual, status);
    }
}
