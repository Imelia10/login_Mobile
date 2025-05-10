package com.example.rewear_app1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;
public class DatabaseHelperProduk extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "ReWearDB";
    public static final int DATABASE_VERSION = 2;

    public static final String TABLE_PRODUK = "produk";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAMA = "nama";
    public static final String COLUMN_KATEGORI = "kategori";
    public static final String COLUMN_KETERANGAN = "keterangan";
    public static final String COLUMN_HARGA = "harga";
    public static final String COLUMN_GAMBAR_URI = "gambarUri";  // Kolom untuk gambar URI

    public DatabaseHelperProduk(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_PRODUK + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAMA + " TEXT, " +
                COLUMN_KATEGORI + " TEXT, " +
                COLUMN_KETERANGAN + " TEXT, " +
                COLUMN_HARGA + " TEXT, " +
                COLUMN_GAMBAR_URI + " TEXT)";  // Menyimpan satu gambar URI
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUK);
        onCreate(db);
    }

    public long tambahProduk(String nama, String kategori, String keterangan, String harga, String gambarUri) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_NAMA, nama);
        values.put(COLUMN_KATEGORI, kategori);
        values.put(COLUMN_KETERANGAN, keterangan);
        values.put(COLUMN_HARGA, harga);
        values.put(COLUMN_GAMBAR_URI, gambarUri);  // Menyimpan satu gambar URI

        return db.insert(TABLE_PRODUK, null, values);
    }

    public List<Produk> getProdukByKategoriAndKeyword(String kategori, String keyword) {
        List<Produk> produkList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selection = COLUMN_KATEGORI + " = ? AND " + COLUMN_NAMA + " LIKE ?";
        String[] selectionArgs = { kategori, "%" + keyword + "%" };

        Cursor cursor = db.query(TABLE_PRODUK, null, selection, selectionArgs, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String nama = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAMA));
                String harga = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HARGA));
                String kategoriDb = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KATEGORI));
                String gambarUri = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GAMBAR_URI));  // Ambil gambar URI

                Produk produk = new Produk(id, nama, gambarUri, harga, kategoriDb);
                produkList.add(produk);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return produkList;
    }
}
