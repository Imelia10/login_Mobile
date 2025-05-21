package com.example.rewear_app1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelperDompet extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "dompet.db";
    private static final int DATABASE_VERSION = 4;

    // Nama tabel dan kolom saldo
    private static final String TABLE_SALDO = "saldo";
    private static final String COL_USER_ID = "user_id";
    private static final String COL_JUMLAH_SALDO = "jumlah";

    // Nama tabel dan kolom untuk transaksi dan produk (contoh, sesuaikan dengan skema kamu)
    private static final String TABLE_TRANSAKSI = "transaksi";
    private static final String COL_TRANSAKSI_JUMLAH_BAYAR = "jumlah_bayar";
    private static final String COL_TRANSAKSI_ID_PRODUK = "id_produk";
    private static final String COL_TRANSAKSI_TANGGAL = "tanggal";

    private static final String TABLE_PRODUK = "produk";
    private static final String COL_PRODUK_ID = "id";
    private static final String COL_PRODUK_ID_PENJUAL = "id_penjual";

    public DatabaseHelperDompet(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Buat tabel saldo
        String createTableSaldo = "CREATE TABLE " + TABLE_SALDO + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USER_ID + " INTEGER UNIQUE, " +
                COL_JUMLAH_SALDO + " REAL DEFAULT 0)";
        db.execSQL(createTableSaldo);

        // Kalau tabel transaksi dan produk belum dibuat di sini, buat juga atau pastikan sudah ada di database lain
        // Contoh dummy:
        /*
        String createTableProduk = "CREATE TABLE " + TABLE_PRODUK + " (" +
                COL_PRODUK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_PRODUK_ID_PENJUAL + " INTEGER)";
        db.execSQL(createTableProduk);

        String createTableTransaksi = "CREATE TABLE " + TABLE_TRANSAKSI + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_TRANSAKSI_ID_PRODUK + " INTEGER, " +
                COL_TRANSAKSI_JUMLAH_BAYAR + " INTEGER, " +
                COL_TRANSAKSI_TANGGAL + " TEXT)";
        db.execSQL(createTableTransaksi);
        */
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SALDO);
        // Jika ingin drop tabel lain juga, tambahkan di sini
        onCreate(db);
    }

    // Method untuk menambah saldo user
    public boolean tambahSaldo(int userId, double jumlah) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            Cursor cursor = db.rawQuery("SELECT " + COL_JUMLAH_SALDO + " FROM " + TABLE_SALDO +
                    " WHERE " + COL_USER_ID + " = ?", new String[]{String.valueOf(userId)});

            if (cursor.moveToFirst()) {
                double saldoSekarang = cursor.getDouble(0);
                double saldoBaru = saldoSekarang + jumlah;

                ContentValues values = new ContentValues();
                values.put(COL_JUMLAH_SALDO, saldoBaru);

                int rowsAffected = db.update(TABLE_SALDO, values, COL_USER_ID + " = ?",
                        new String[]{String.valueOf(userId)});
                cursor.close();
                return rowsAffected > 0;
            } else {
                cursor.close();
                ContentValues values = new ContentValues();
                values.put(COL_USER_ID, userId);
                values.put(COL_JUMLAH_SALDO, jumlah);

                long result = db.insert(TABLE_SALDO, null, values);
                return result != -1;
            }
        } finally {
            db.close();
        }
    }
    public boolean insertTransaction(int penjualId, int pembeliId, double jumlah, String tanggal) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("penjualId", penjualId);
        values.put("pembeliId", pembeliId);
        values.put("jumlah", jumlah); // ContentValues bisa simpan double
        values.put("tanggal", tanggal);

        long result = db.insert("dompet", null, values);
        return result != -1;
    }


    // Method untuk mendapatkan saldo user
    public double getSaldo(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COL_JUMLAH_SALDO + " FROM " + TABLE_SALDO +
                " WHERE " + COL_USER_ID + " = ?", new String[]{String.valueOf(userId)});

        double saldo = 0;
        if (cursor.moveToFirst()) {
            saldo = cursor.getDouble(0);
        }
        cursor.close();
        return saldo;
    }
    public int getSaldoByUserId(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT saldo FROM users WHERE id = ?", new String[]{String.valueOf(userId)});
        int saldo = 0;
        if (cursor.moveToFirst()) {
            saldo = cursor.getInt(0);
        }
        cursor.close();
        return saldo;
    }


    // Fungsi untuk hitung total pendapatan user berdasar transaksi produk user
    public int getTotalPendapatanByUserId(int userId) {
        int totalPendapatan = 0;
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT SUM(t." + COL_TRANSAKSI_JUMLAH_BAYAR + ") as total " +
                "FROM " + TABLE_TRANSAKSI + " t " +
                "INNER JOIN " + TABLE_PRODUK + " p ON t." + COL_TRANSAKSI_ID_PRODUK + " = p." + COL_PRODUK_ID + " " +
                "WHERE p." + COL_PRODUK_ID_PENJUAL + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            totalPendapatan = cursor.getInt(cursor.getColumnIndexOrThrow("total"));
        }
        cursor.close();
        return totalPendapatan;
    }

    // Fungsi ambil detail transaksi (jumlah dan tanggal) untuk produk user
    public Cursor getTransaksiByUserId(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT t." + COL_TRANSAKSI_JUMLAH_BAYAR + ", t." + COL_TRANSAKSI_TANGGAL +
                " FROM " + TABLE_TRANSAKSI + " t " +
                " INNER JOIN " + TABLE_PRODUK + " p ON t." + COL_TRANSAKSI_ID_PRODUK + " = p." + COL_PRODUK_ID +
                " WHERE p." + COL_PRODUK_ID_PENJUAL + " = ? " +
                " ORDER BY t." + COL_TRANSAKSI_TANGGAL + " DESC";

        return db.rawQuery(query, new String[]{String.valueOf(userId)});
    }
}

















//package com.example.rewear_app1;
//
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;
//import android.util.Log;
//
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.Locale;
//
//public class DatabaseHelperDompet extends SQLiteOpenHelper {
//
//    private static final String TAG = "DatabaseHelperDompet";
//    private static final String DATABASE_NAME = "RewearDB";
//    private static final int DATABASE_VERSION = 6;
//
//    // Table names
//    private static final String TABLE_USERS = "users";
//    private static final String TABLE_DOMPET = "dompet";
//
//    // users table columns
//    private static final String KEY_USER_ID = "id";
//    private static final String KEY_EMAIL = "email";
//    private static final String KEY_SALDO = "saldo";
//
//    // dompet table columns
//    private static final String KEY_DOMPET_ID = "id_transaksi";
//    private static final String KEY_DOMPET_USER_ID = "id_user";
//    private static final String KEY_DOMPET_PRODUCT_ID = "id_produk";
//    private static final String KEY_DOMPET_NAMA_PRODUK = "nama_produk";
//    private static final String KEY_DOMPET_JUMLAH = "jumlah";
//    private static final String KEY_DOMPET_TANGGAL = "tanggal";
//    private static final String KEY_DOMPET_JENIS = "jenis";
//    private static final String KEY_DOMPET_KETERANGAN = "keterangan";
//
//    public DatabaseHelperDompet(Context context) {
//        super(context, DATABASE_NAME, null, DATABASE_VERSION);
//    }
//
//    @Override
//    public void onCreate(SQLiteDatabase db) {
//        // Create users table
//        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
//                + KEY_USER_ID + " INTEGER PRIMARY KEY,"
//                + KEY_EMAIL + " TEXT UNIQUE,"
//                + KEY_SALDO + " DOUBLE DEFAULT 0"
//                + ")";
//        db.execSQL(CREATE_USERS_TABLE);
//
//        // Create dompet (transactions) table
//        String CREATE_DOMPET_TABLE = "CREATE TABLE " + TABLE_DOMPET + "("
//                + KEY_DOMPET_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
//                + KEY_DOMPET_USER_ID + " INTEGER,"
//                + KEY_DOMPET_PRODUCT_ID + " INTEGER,"
//                + KEY_DOMPET_NAMA_PRODUK + " TEXT,"
//                + KEY_DOMPET_JUMLAH + " DOUBLE NOT NULL,"
//                + KEY_DOMPET_TANGGAL + " TEXT NOT NULL,"
//                + KEY_DOMPET_JENIS + " TEXT NOT NULL,"
//                + KEY_DOMPET_KETERANGAN + " TEXT,"
//                + "FOREIGN KEY(" + KEY_DOMPET_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + KEY_USER_ID + ")"
//                + ")";
//        db.execSQL(CREATE_DOMPET_TABLE);
//    }
//
//    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        // Upgrade strategy: drop tables and recreate (simple)
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOMPET);
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
//        onCreate(db);
//    }
//
//    // Inisialisasi wallet user (dipanggil saat registrasi)
//    public boolean initializeUserWallet(int userId, String email) {
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//        values.put(KEY_USER_ID, userId);
//        values.put(KEY_EMAIL, email);
//        values.put(KEY_SALDO, 0);
//
//        long result = db.insertWithOnConflict(TABLE_USERS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
//        db.close();
//
//        return result != -1;
//    }
//    public boolean addIncomeFromSale(int userId, double amount, int productId, String productName) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.beginTransaction();
//        try {
//            // 1. Pastikan user ada, jika tidak buat wallet baru
//            if (!userExists(userId)) {
//                initializeUserWallet(userId, ""); // Email kosong karena tidak critical
//            }
//
//            // 2. Update saldo
//            double currentSaldo = getSaldo(userId);
//            ContentValues userValues = new ContentValues();
//            userValues.put(KEY_SALDO, currentSaldo + amount);
//
//            int updatedRows = db.update(TABLE_USERS, userValues,
//                    KEY_USER_ID + "=?", new String[]{String.valueOf(userId)});
//
//            if (updatedRows == 0) {
//                Log.e(TAG, "Gagal update saldo user");
//                return false;
//            }
//
//            // 3. Catat transaksi
//            ContentValues transValues = new ContentValues();
//            transValues.put(KEY_DOMPET_USER_ID, userId);
//            transValues.put(KEY_DOMPET_PRODUCT_ID, productId);
//            transValues.put(KEY_DOMPET_NAMA_PRODUK, productName);
//            transValues.put(KEY_DOMPET_JUMLAH, amount);
//            transValues.put(KEY_DOMPET_TANGGAL, getCurrentDateTime());
//            transValues.put(KEY_DOMPET_JENIS, "PENJUALAN");
//            transValues.put(KEY_DOMPET_KETERANGAN, "Pendapatan dari penjualan " + productName);
//
//            long result = db.insert(TABLE_DOMPET, null, transValues);
//
//            if (result == -1) {
//                Log.e(TAG, "Gagal mencatat transaksi");
//                return false;
//            }
//
//            db.setTransactionSuccessful();
//            return true;
//        } catch (Exception e) {
//            Log.e(TAG, "Error in addIncomeFromSale", e);
//            return false;
//        } finally {
//            db.endTransaction();
//        }
//    }
//
//    // Cek apakah user ada
//    public boolean userExists(int userId) {
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.query(TABLE_USERS,
//                new String[]{KEY_USER_ID},
//                KEY_USER_ID + " = ?",
//                new String[]{String.valueOf(userId)},
//                null, null, null);
//
//        boolean exists = (cursor.getCount() > 0);
//        cursor.close();
//        db.close();
//        return exists;
//    }
//
//    // Ambil saldo user saat ini
//    public double getSaldo(int userId) {
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.query("users",
//                new String[]{"saldo"},
//                "id=?",
//                new String[]{String.valueOf(userId)},
//                null, null, null);
//
//        if (cursor != null && cursor.moveToFirst()) {
//            double saldo = cursor.getDouble(0);
//            cursor.close();
//            return saldo;
//        }
//        return 0;
//    }
//
//    // Update saldo user
//    public boolean updateSaldo(int userId, double newSaldo) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put(KEY_SALDO, newSaldo);
//
//        int rows = db.update(TABLE_USERS, values, KEY_USER_ID + " = ?", new String[]{String.valueOf(userId)});
//        db.close();
//
//        return rows > 0;
//    }
//
//    // Tambah transaksi dompet (hasil penjualan)
//    public boolean addTransaksiDompet(int userId, int productId, String namaProduk, double jumlah, String jenis, String keterangan) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.beginTransaction();
//        try {
//            // Pastikan user ada
//            if (!userExists(userId)) {
//                initializeUserWallet(userId, "");
//            }
//
//            // Update saldo
//            double currentSaldo = getSaldo(userId);
//            double updatedSaldo = currentSaldo + jumlah;
//            boolean saldoUpdated = updateSaldo(userId, updatedSaldo);
//            if (!saldoUpdated) {
//                Log.e(TAG, "Gagal update saldo user.");
//                return false;
//            }
//
//            // Simpan transaksi
//            ContentValues values = new ContentValues();
//            values.put(KEY_DOMPET_USER_ID, userId);
//            values.put(KEY_DOMPET_PRODUCT_ID, productId);
//            values.put(KEY_DOMPET_NAMA_PRODUK, namaProduk);
//            values.put(KEY_DOMPET_JUMLAH, jumlah);
//            values.put(KEY_DOMPET_TANGGAL, getCurrentDateTime());
//            values.put(KEY_DOMPET_JENIS, jenis);
//            values.put(KEY_DOMPET_KETERANGAN, keterangan);
//
//            long insertId = db.insert(TABLE_DOMPET, null, values);
//            if (insertId == -1) {
//                Log.e(TAG, "Gagal simpan transaksi dompet.");
//                return false;
//            }
//
//            db.setTransactionSuccessful();
//            return true;
//        } catch (Exception e) {
//            Log.e(TAG, "Error addTransaksiDompet: ", e);
//            return false;
//        } finally {
//            db.endTransaction();
//            db.close();
//        }
//    }
//
//    // Ambil semua transaksi hasil penjualan user (jenis PENJUALAN)
//    public Cursor getTransaksiPenjualanByUser(int userId) {
//        SQLiteDatabase db = this.getReadableDatabase();
//        String selection = KEY_DOMPET_USER_ID + " = ? AND " + KEY_DOMPET_JENIS + " = ?";
//        String[] selectionArgs = {String.valueOf(userId), "PENJUALAN"};
//
//        return db.query(TABLE_DOMPET,
//                new String[]{KEY_DOMPET_ID, KEY_DOMPET_PRODUCT_ID, KEY_DOMPET_NAMA_PRODUK, KEY_DOMPET_JUMLAH, KEY_DOMPET_TANGGAL, KEY_DOMPET_KETERANGAN},
//                selection,
//                selectionArgs,
//                null,
//                null,
//                KEY_DOMPET_TANGGAL + " DESC");
//    }
//
//    // Ambil total pendapatan user dari transaksi PENJUALAN
//    public double getTotalPendapatan(int userId) {
//        double total = 0;
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.rawQuery(
//                "SELECT SUM(" + KEY_DOMPET_JUMLAH + ") FROM " + TABLE_DOMPET +
//                        " WHERE " + KEY_DOMPET_USER_ID + " = ? AND " + KEY_DOMPET_JENIS + " = ?",
//                new String[]{String.valueOf(userId), "PENJUALAN"});
//
//        if (cursor.moveToFirst()) {
//            total = cursor.getDouble(0);
//        }
//        cursor.close();
//        db.close();
//        return total;
//    }
//
//    private String getCurrentDateTime() {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
//        return sdf.format(new Date());
//    }
//
//}
