package com.example.rewear_app1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelperTransaksi extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ReWearTransaksiDB";
    private static final int DATABASE_VERSION = 5;

    // Nama tabel dan kolom
    private static final String TABLE_TRANSAKSI = "transaksi";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_EMAIL_PEMBELI = "email_pembeli";
    private static final String COLUMN_ID_PEMBELI = "id_pembeli";       // kolom tambahan
    private static final String COLUMN_ID_PRODUK = "id_produk";
    private static final String COLUMN_NAMA_BARANG = "nama_barang";    // kolom tambahan
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
        String CREATE_TRANSAKSI_TABLE = "CREATE TABLE " + TABLE_TRANSAKSI + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_EMAIL_PEMBELI + " TEXT, "
                + COLUMN_ID_PEMBELI + " INTEGER, "
                + COLUMN_ID_PRODUK + " INTEGER, "
                + COLUMN_NAMA_BARANG + " TEXT, "
                + COLUMN_TANGGAL + " TEXT, "
                + COLUMN_ALAMAT + " TEXT, "
                + COLUMN_METODE_PEMBAYARAN + " TEXT, "
                + COLUMN_ONGKIR + " REAL, "
                + COLUMN_DISKON + " REAL, "
                + COLUMN_TOTAL + " REAL, "
                + COLUMN_STATUS + " TEXT DEFAULT 'pending'"
                + ")";
        db.execSQL(CREATE_TRANSAKSI_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSAKSI);
        onCreate(db);
    }

    // Menambahkan transaksi baru
    public long addTransaksi(Transaksi transaksi) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID_PEMBELI, transaksi.getIdPembeli());
        values.put(COLUMN_ID_PRODUK, transaksi.getIdProduk());
        values.put(COLUMN_EMAIL_PEMBELI, transaksi.getEmailPembeli());
        values.put(COLUMN_NAMA_BARANG, transaksi.getNamaBarang());
        values.put(COLUMN_TANGGAL, transaksi.getTanggal());
        values.put(COLUMN_ALAMAT, transaksi.getAlamat());
        values.put(COLUMN_METODE_PEMBAYARAN, transaksi.getMetodePembayaran());
        values.put(COLUMN_ONGKIR, transaksi.getOngkir());
        values.put(COLUMN_DISKON, transaksi.getDiskon());
        values.put(COLUMN_TOTAL, transaksi.getTotal());
        values.put(COLUMN_STATUS, transaksi.getStatus() != null ? transaksi.getStatus() : "pending");

        long id = db.insert(TABLE_TRANSAKSI, null, values);
        db.close();
        return id;
    }

    public int getJumlahTransaksiDenganDiskon(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM transaksi WHERE email_pembeli = ? AND diskon > 0", new String[]{email});
        int count = 0;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        return count;
    }

    public List<Transaksi> getAllTransaksi() {
        List<Transaksi> transaksiList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TRANSAKSI, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Transaksi transaksi = new Transaksi();
                transaksi.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                transaksi.setIdProduk(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID_PRODUK)));
                transaksi.setEmailPembeli(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL_PEMBELI)));
                transaksi.setIdPembeli(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID_PEMBELI)));
                transaksi.setNamaBarang(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAMA_BARANG)));
                transaksi.setTanggal(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TANGGAL)));
                transaksi.setAlamat(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ALAMAT)));
                transaksi.setMetodePembayaran(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_METODE_PEMBAYARAN)));
                transaksi.setOngkir(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_ONGKIR)));
                transaksi.setDiskon(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_DISKON)));
                transaksi.setTotal(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_TOTAL)));
                transaksi.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS)));
                transaksiList.add(transaksi);
            } while (cursor.moveToNext());

            cursor.close();
        }
        db.close();
        return transaksiList;
    }

    public int getJumlahTransaksiUser(String emailPembeli) {
        int jumlah = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_TRANSAKSI + " WHERE " + COLUMN_EMAIL_PEMBELI + " = ?", new String[]{emailPembeli});
        if (cursor.moveToFirst()) {
            jumlah = cursor.getInt(0);
        }
        cursor.close();
        return jumlah;
    }


    public double getTotalBelanjaUser(String userEmail) {
        double total = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(" + COLUMN_TOTAL + ") as total FROM " + TABLE_TRANSAKSI +
                " WHERE " + COLUMN_EMAIL_PEMBELI + " = ? AND " + COLUMN_STATUS + " = 'completed'";
        Cursor cursor = db.rawQuery(query, new String[]{userEmail});
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"));
        }
        cursor.close();
        return total;
    }


    // Mendapatkan transaksi berdasarkan id
    public Transaksi getTransaksiById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Transaksi transaksi = null;

        Cursor cursor = db.query(
                TABLE_TRANSAKSI,
                null,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)},
                null, null, null
        );

        if (cursor != null && cursor.moveToFirst()) {
            transaksi = new Transaksi();
            transaksi.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
            transaksi.setEmailPembeli(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL_PEMBELI)));
            transaksi.setIdPembeli(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID_PEMBELI)));
            transaksi.setIdProduk(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID_PRODUK)));
            transaksi.setNamaBarang(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAMA_BARANG)));
            transaksi.setTanggal(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TANGGAL)));
            transaksi.setAlamat(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ALAMAT)));
            transaksi.setMetodePembayaran(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_METODE_PEMBAYARAN)));
            transaksi.setOngkir(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_ONGKIR)));
            transaksi.setDiskon(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_DISKON)));
            transaksi.setTotal(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_TOTAL)));
            transaksi.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS)));
            cursor.close();
        }

        db.close();
        return transaksi;
    }

    // Mendapatkan transaksi terakhir berdasarkan email pembeli
    public List<Transaksi> getTransactionsByEmail(String email) {
        List<Transaksi> transaksiList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM transaksi WHERE email_pembeli = ?", new String[]{email});

        if (cursor.moveToFirst()) {
            do {
                Transaksi transaksi = new Transaksi();
                transaksi.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                transaksi.setIdProduk(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID_PRODUK)));
                transaksi.setTanggal(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TANGGAL)));
                transaksi.setTotal(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_TOTAL)));
                transaksiList.add(transaksi);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return transaksiList;
    }


    // Di DatabaseHelperTransaksi.java
    public boolean pindahkanKeHistory(int transaksiId) {
        SQLiteDatabase db = this.getWritableDatabase();

        // 1. Ambil data transaksi
        Transaksi transaksi = getTransaksiById(transaksiId);
        if (transaksi == null) return false;

        // 2. Simpan ke tabel history (atau update status)
        ContentValues values = new ContentValues();
        values.put("status", "completed"); // Tambahkan kolom status

        int rowsAffected = db.update(TABLE_TRANSAKSI, values,
                COLUMN_ID + "=?", new String[]{String.valueOf(transaksiId)});

        db.close();
        return rowsAffected > 0;
    }

    public Transaksi getTransaksiTerakhir(String emailPembeli) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_TRANSAKSI +
                " WHERE " + COLUMN_EMAIL_PEMBELI + " = ?" +
                " ORDER BY " + COLUMN_ID + " DESC LIMIT 1";

        Cursor cursor = db.rawQuery(query, new String[]{emailPembeli});

        Transaksi transaksi = null;
        if (cursor.moveToFirst()) {
            transaksi = new Transaksi();
            transaksi.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
            transaksi.setEmailPembeli(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL_PEMBELI)));
            transaksi.setIdPembeli(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID_PEMBELI)));
            transaksi.setIdProduk(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID_PRODUK)));
            transaksi.setNamaBarang(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAMA_BARANG)));
            transaksi.setTanggal(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TANGGAL)));
            transaksi.setAlamat(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ALAMAT)));
            transaksi.setMetodePembayaran(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_METODE_PEMBAYARAN)));
            transaksi.setOngkir(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_ONGKIR)));
            transaksi.setDiskon(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_DISKON)));
            transaksi.setTotal(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_TOTAL)));
            transaksi.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS)));
        }

        cursor.close();
        db.close();
        return transaksi;
    }

    // Mendapatkan semua transaksi tanpa filter
    public List<Transaksi> getAllTransactions() {
        List<Transaksi> transaksiList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TRANSAKSI,
                null,
                null,
                null,
                null, null,
                COLUMN_ID + " DESC");

        if (cursor.moveToFirst()) {
            do {
                Transaksi transaksi = new Transaksi();
                transaksi.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                transaksi.setEmailPembeli(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL_PEMBELI)));
                transaksi.setIdPembeli(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID_PEMBELI)));
                transaksi.setIdProduk(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID_PRODUK)));
                transaksi.setNamaBarang(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAMA_BARANG)));
                transaksi.setTanggal(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TANGGAL)));
                transaksi.setAlamat(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ALAMAT)));
                transaksi.setMetodePembayaran(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_METODE_PEMBAYARAN)));
                transaksi.setOngkir(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_ONGKIR)));
                transaksi.setDiskon(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_DISKON)));
                transaksi.setTotal(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_TOTAL)));
                transaksi.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS)));
                transaksiList.add(transaksi);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return transaksiList;
    }

    // Mendapatkan semua transaksi user tertentu berdasarkan email pembeli
    public List<Transaksi> getAllTransaksiUser(String emailPembeli) {
        List<Transaksi> transaksiList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_TRANSAKSI,
                null,
                COLUMN_EMAIL_PEMBELI + " = ?",
                new String[]{emailPembeli},
                null, null,
                COLUMN_ID + " DESC");

        if (cursor.moveToFirst()) {
            do {
                Transaksi transaksi = new Transaksi();
                transaksi.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                transaksi.setEmailPembeli(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL_PEMBELI)));
                transaksi.setIdPembeli(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID_PEMBELI)));
                transaksi.setIdProduk(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID_PRODUK)));
                transaksi.setNamaBarang(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAMA_BARANG)));
                transaksi.setTanggal(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TANGGAL)));
                transaksi.setAlamat(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ALAMAT)));
                transaksi.setMetodePembayaran(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_METODE_PEMBAYARAN)));
                transaksi.setOngkir(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_ONGKIR)));
                transaksi.setDiskon(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_DISKON)));
                transaksi.setTotal(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_TOTAL)));
                transaksi.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS)));
                transaksiList.add(transaksi);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return transaksiList;
    }


    // Update status transaksi berdasarkan id
    public boolean updateStatusTransaksi(int transaksiId, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STATUS, status);

        int rowsAffected = db.update(TABLE_TRANSAKSI, values,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(transaksiId)});
        db.close();
        return rowsAffected > 0;
    }



    // Hapus transaksi berdasarkan id
    public void deleteTransaksi(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TRANSAKSI, COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
    }
}
