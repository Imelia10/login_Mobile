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

    public static final String DATABASE_NAME = "ReWearDB";
    public static final int DATABASE_VERSION = 6;

    public static final String TABLE_PRODUK = "produk";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAMA = "nama";
    public static final String COLUMN_KATEGORI = "kategori";
    public static final String COLUMN_KETERANGAN = "deskripsi";
    public static final String COLUMN_HARGA = "harga";
    public static final String COLUMN_GAMBAR_URI = "gambarUri";
    public static final String COLUMN_EMAIL_PEMILIK = "emailPemilik";
    public static final String COLUMN_NAMA_DEPAN = "namaDepan";
    public static final String COLUMN_NAMA_BELAKANG = "namaBelakang";
    public static final String COLUMN_ID_PENJUAL = "idPenjual";

    public DatabaseHelperProduk(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_PRODUK + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_NAMA + " TEXT," +
                COLUMN_KATEGORI + " TEXT," +
                COLUMN_KETERANGAN + " TEXT," +
                COLUMN_HARGA + " TEXT," +
                COLUMN_GAMBAR_URI + " TEXT," +
                COLUMN_EMAIL_PEMILIK + " TEXT," +
                COLUMN_NAMA_DEPAN + " TEXT," +
                COLUMN_NAMA_BELAKANG + " TEXT," +
                COLUMN_ID_PENJUAL + " TEXT" +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUK);
        onCreate(db);
    }

    public long tambahProduk(String namaBarang, String kategori, String keterangan, String harga, String imageUriString, String userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nama", namaBarang);
        values.put("kategori", kategori);
        values.put("deskripsi", keterangan);
        values.put("harga", harga);
        values.put("gambarUri", imageUriString);
        values.put("idPenjual", userId); // ini untuk tahu siapa pemilik produk

        return db.insert("produk", null, values);
    }


    public List<Produk> getProdukByKategoriAndKeywordAndUser(String kategori, String keyword, String userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Produk> produkList = new ArrayList<>();
        String selection = "kategori = ? AND idPenjual = ? AND (nama LIKE ? OR deskripsi LIKE ?)";
        String[] selectionArgs = new String[]{kategori, userId, "%" + keyword + "%", "%" + keyword + "%"};
        Cursor cursor = db.query(TABLE_PRODUK, null, selection, selectionArgs, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String nama = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAMA));
                String harga = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HARGA));
                String gambarUri = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GAMBAR_URI));
                String kategoriProduk = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KATEGORI));
                String deskripsi = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KETERANGAN));
                String idPenjualStr = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID_PENJUAL));

                // Pastikan idPenjual bertipe String
                Produk produk = new Produk(id, nama, gambarUri, harga, kategoriProduk, deskripsi, idPenjualStr);
                produkList.add(produk);
            }
            cursor.close();
        }
        return produkList;
    }


    public List<Produk> getProdukByKategoriAndKeyword(String kategori, String keyword) {
        List<Produk> produkList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selection = COLUMN_KATEGORI + " = ? AND " + COLUMN_NAMA + " LIKE ?";
        String[] selectionArgs = {kategori, "%" + keyword + "%"};

        Cursor cursor = db.query(TABLE_PRODUK, null, selection, selectionArgs, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String nama = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAMA));
                String harga = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HARGA));
                String kategoriDb = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KATEGORI));
                String gambarUri = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GAMBAR_URI));
                String emailPemilik = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL_PEMILIK));
                String deskripsi = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KETERANGAN));
                String idPenjualStr = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID_PENJUAL));

                // Menggunakan konstruktor yang benar
                Produk produk = new Produk(id, nama, gambarUri, harga, kategoriDb, deskripsi, idPenjualStr);
                produkList.add(produk);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return produkList;
    }


    public Produk getProdukById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};

        Cursor cursor = db.query(TABLE_PRODUK, null, selection, selectionArgs, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            String nama = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAMA));
            String harga = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HARGA));
            String kategori = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KATEGORI));
            String gambarUri = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GAMBAR_URI));
            String emailPemilik = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL_PEMILIK));
            String deskripsi = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KETERANGAN));
            String idPenjualStr = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID_PENJUAL));

            // Membuat objek Produk menggunakan konstruktor yang sesuai
            Produk produk = new Produk(id, nama, gambarUri, harga, kategori, deskripsi, idPenjualStr);
            cursor.close();
            return produk;
        }

        return null;
    }


    public boolean hapusProduk(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int hasil = db.delete("produk", "id=?", new String[]{String.valueOf(id)});
        db.close();
        return hasil > 0;
    }



    public void printAllUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT email, firstName, lastName, photoUri FROM users", null);

        while (cursor.moveToNext()) {
            String email = cursor.getString(0);
            String first = cursor.getString(1);
            String last = cursor.getString(2);
            String photo = cursor.getString(3);
            Log.d("CEK_USERS", "Email: " + email + ", Nama: " + first + " " + last + ", Foto: " + photo);
        }

        cursor.close();
    }
}
