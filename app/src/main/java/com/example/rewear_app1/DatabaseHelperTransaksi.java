package com.example.rewear_app1;

import android.content.ContentValues;
import android.content.Context;
import java.util.List;
import java.util.ArrayList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelperTransaksi extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ReWearTransaksiDB";
    private static final int DATABASE_VERSION = 1;

    // Nama tabel dan kolom
    private static final String TABLE_TRANSAKSI = "transaksi";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_EMAIL_PEMBELI = "email_pembeli";
    private static final String COLUMN_ID_PRODUK = "id_produk";
    private static final String COLUMN_TANGGAL = "tanggal";
    private static final String COLUMN_ALAMAT = "alamat";
    private static final String COLUMN_METODE_PEMBAYARAN = "metode_pembayaran";
    private static final String COLUMN_ONGKIR = "ongkir";
    private static final String COLUMN_DISKON = "diskon";
    private static final String COLUMN_TOTAL = "total";
    private static final String COLUMN_STATUS = "status";

    public DatabaseHelperTransaksi(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TRANSAKSI_TABLE = "CREATE TABLE " + TABLE_TRANSAKSI + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_EMAIL_PEMBELI + " TEXT,"
                + COLUMN_ID_PRODUK + " INTEGER,"
                + COLUMN_TANGGAL + " TEXT,"
                + COLUMN_ALAMAT + " TEXT,"
                + COLUMN_METODE_PEMBAYARAN + " TEXT,"
                + COLUMN_ONGKIR + " REAL,"
                + COLUMN_DISKON + " REAL,"
                + COLUMN_TOTAL + " REAL,"
                + COLUMN_STATUS + " TEXT DEFAULT 'pending'"
                + ")";
        db.execSQL(CREATE_TRANSAKSI_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSAKSI);
        onCreate(db);
    }

    // Method untuk menambahkan transaksi baru
    public long addTransaksi(Transaksi transaksi) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL_PEMBELI, transaksi.getEmailPembeli());
        values.put(COLUMN_ID_PRODUK, transaksi.getIdProduk());
        values.put(COLUMN_TANGGAL, transaksi.getTanggal());
        values.put(COLUMN_ALAMAT, transaksi.getAlamat());
        values.put(COLUMN_METODE_PEMBAYARAN, transaksi.getMetodePembayaran());
        values.put(COLUMN_ONGKIR, transaksi.getOngkir());
        values.put(COLUMN_DISKON, transaksi.getDiskon());
        values.put(COLUMN_TOTAL, transaksi.getTotal());
        values.put(COLUMN_STATUS, transaksi.getStatus());

        long id = db.insert(TABLE_TRANSAKSI, null, values);
        db.close();
        return id;
    }

    // Method untuk mendapatkan transaksi berdasarkan ID
    public Transaksi getTransaksi(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_TRANSAKSI,
                new String[]{COLUMN_ID, COLUMN_EMAIL_PEMBELI, COLUMN_ID_PRODUK, COLUMN_TANGGAL,
                        COLUMN_ALAMAT, COLUMN_METODE_PEMBAYARAN, COLUMN_ONGKIR,
                        COLUMN_DISKON, COLUMN_TOTAL, COLUMN_STATUS},
                COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        Transaksi transaksi = new Transaksi(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getInt(2),
                cursor.getString(3),
                cursor.getString(4),
                cursor.getString(5),
                cursor.getDouble(6),
                cursor.getDouble(7),
                cursor.getDouble(8),
                cursor.getString(9)
        );

        cursor.close();
        return transaksi;
    }

    // Method untuk mendapatkan transaksi terakhir berdasarkan email pembeli
    public Transaksi getTransaksiTerakhir(String emailPembeli) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_TRANSAKSI +
                " WHERE " + COLUMN_EMAIL_PEMBELI + " = ?" +
                " ORDER BY " + COLUMN_ID + " DESC LIMIT 1";

        Cursor cursor = db.rawQuery(query, new String[]{emailPembeli});

        if (cursor.moveToFirst()) {
            Transaksi transaksi = new Transaksi(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getInt(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getDouble(6),
                    cursor.getDouble(7),
                    cursor.getDouble(8),
                    cursor.getString(9)
            );
            cursor.close();
            return transaksi;
        }

        cursor.close();
        return null;
    }

    // Method untuk mendapatkan semua transaksi user
    public List<Transaksi> getAllTransaksiUser(String emailPembeli) {
        List<Transaksi> transaksiList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TRANSAKSI,
                new String[]{COLUMN_ID, COLUMN_EMAIL_PEMBELI, COLUMN_ID_PRODUK, COLUMN_TANGGAL,
                        COLUMN_ALAMAT, COLUMN_METODE_PEMBAYARAN, COLUMN_ONGKIR,
                        COLUMN_DISKON, COLUMN_TOTAL, COLUMN_STATUS},
                COLUMN_EMAIL_PEMBELI + "=?",
                new String[]{emailPembeli}, null, null, COLUMN_ID + " DESC", null);

        if (cursor.moveToFirst()) {
            do {
                Transaksi transaksi = new Transaksi(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getInt(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getDouble(6),
                        cursor.getDouble(7),
                        cursor.getDouble(8),
                        cursor.getString(9)
                );
                transaksiList.add(transaksi);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return transaksiList;
    }

    // Method untuk update status transaksi
    public int updateStatusTransaksi(int id, String status) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_STATUS, status);

        return db.update(TABLE_TRANSAKSI, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    // Method untuk menghapus transaksi
    public void deleteTransaksi(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TRANSAKSI, COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
    }
}