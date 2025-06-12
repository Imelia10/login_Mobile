package com.example.rewear_app1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ReWear.db";
    private static final int DATABASE_VERSION = 19; // Incremented from 11

    // Users table constants
    private static final String TABLE_NAME = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_PHONE = "phone";
    private static final String COLUMN_FIRST_NAME = "first_name";
    private static final String COLUMN_LAST_NAME = "last_name";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_ALAMAT = "alamat";
    private static final String COLUMN_TTL = "ttl";
    private static final String COLUMN_PHOTO_URI = "photoUri";
    private static final String COLUMN_SALDO = "saldo";

    // Voucher table constants
    private static final String TABLE_VOUCHER = "voucher";
    private static final String COLUMN_VOUCHER_ID = "id";
    private static final String COLUMN_VOUCHER_JUDUL = "judul";
    private static final String COLUMN_VOUCHER_SYARAT = "syarat";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create users table
        String createUserTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_PHONE + " TEXT UNIQUE, " +
                COLUMN_FIRST_NAME + " TEXT, " +
                COLUMN_LAST_NAME + " TEXT, " +
                COLUMN_EMAIL + " TEXT, " +
                COLUMN_PASSWORD + " TEXT, " +
                COLUMN_ALAMAT + " TEXT, " +
                COLUMN_TTL + " TEXT, " +
                COLUMN_PHOTO_URI + " TEXT, " +
                COLUMN_SALDO + " DOUBLE DEFAULT 0" +
                ")";
        db.execSQL(createUserTable);

        // Create voucher table
        String createVoucherTable = "CREATE TABLE " + TABLE_VOUCHER + " (" +
                COLUMN_VOUCHER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_VOUCHER_JUDUL + " TEXT, " +
                COLUMN_VOUCHER_SYARAT + " TEXT, " +
                "kode TEXT" + // Tambahkan ini
                ")";
        db.execSQL(createVoucherTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VOUCHER);
        onCreate(db);
    }



    private int getNextAvailableId(SQLiteDatabase db) {
        int id = 1;
        Cursor cursor = db.rawQuery("SELECT id FROM " + TABLE_NAME + " ORDER BY id", null);
        while (cursor.moveToNext()) {
            if (cursor.getInt(0) != id) {
                break;
            }
            id++;
        }
        cursor.close();
        return id;
    }

    public int getSaldoUser(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT saldo FROM users WHERE id = ?", new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            int saldo = cursor.getInt(0);
            cursor.close();
            return saldo;
        }
        cursor.close();
        return 0;
    }

    public int getIdPenjualDariProduk(int produkId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT idPenjual FROM produk WHERE id = ?", new String[]{String.valueOf(produkId)});
        if (cursor.moveToFirst()) {
            int idPenjual = cursor.getInt(0);
            cursor.close();
            return idPenjual;
        }
        cursor.close();
        return -1; // Tidak ditemukan
    }

    public boolean tambahSaldoUser(int userId, int jumlahSaldo) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT saldo FROM users WHERE id = ?", new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            int saldoSekarang = cursor.getInt(0);
            int saldoBaru = saldoSekarang + jumlahSaldo;

            ContentValues values = new ContentValues();
            values.put("saldo", saldoBaru);
            int result = db.update("users", values, "id = ?", new String[]{String.valueOf(userId)});
            cursor.close();
            return result > 0;
        }
        cursor.close();
        return false;
    }

    public boolean deleteVoucher(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_VOUCHER, COLUMN_VOUCHER_ID + " = ?", new String[]{String.valueOf(id)});
        return rowsAffected > 0;
    }

    public boolean registerUser(String phone, String firstName, String lastName, String email, String password, String alamat, String ttl, String photoUri) {
        SQLiteDatabase db = this.getWritableDatabase();
        int newId = getNextAvailableId(db);

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ID, newId);
        contentValues.put(COLUMN_PHONE, phone);
        contentValues.put(COLUMN_FIRST_NAME, firstName);
        contentValues.put(COLUMN_LAST_NAME, lastName);
        contentValues.put(COLUMN_EMAIL, email);
        contentValues.put(COLUMN_PASSWORD, password);
        contentValues.put(COLUMN_ALAMAT, alamat);
        contentValues.put(COLUMN_TTL, ttl);
        contentValues.put(COLUMN_PHOTO_URI, photoUri);

        long result = db.insert(TABLE_NAME, null, contentValues);
        db.close();
        return result != -1;
    }

    public boolean addUser(String firstName, String lastName, String phone, String email, String password, String alamat, String ttl, String photoUri) {
        SQLiteDatabase db = this.getWritableDatabase();
        int newId = getNextAvailableId(db);

        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, newId);
        values.put(COLUMN_FIRST_NAME, firstName);
        values.put(COLUMN_LAST_NAME, lastName);
        values.put(COLUMN_PHONE, phone);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_ALAMAT, alamat);
        values.put(COLUMN_TTL, ttl);
        values.put(COLUMN_PHOTO_URI, photoUri);

        long result = db.insert(TABLE_NAME, null, values);
        db.close();
        return result != -1;
    }

    public boolean updateUser(int userId, String firstName, String lastName, String phone, String ttl, String photoUri) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_FIRST_NAME, firstName);
        contentValues.put(COLUMN_LAST_NAME, lastName);
        contentValues.put(COLUMN_PHONE, phone);
        contentValues.put(COLUMN_TTL, ttl);
        contentValues.put(COLUMN_PHOTO_URI, photoUri);

        int rowsAffected = db.update(TABLE_NAME, contentValues, COLUMN_ID + "=?", new String[]{String.valueOf(userId)});
        db.close();
        return rowsAffected > 0;
    }

    public boolean deleteUser(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_NAME, COLUMN_ID + "=?", new String[]{String.valueOf(userId)});
        db.close();
        return rowsAffected > 0;
    }

    public boolean isEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, COLUMN_EMAIL + "=?", new String[]{email}, null, null, null);
        boolean exists = (cursor != null && cursor.moveToFirst());
        if (cursor != null) cursor.close();
        return exists;
    }

    public int getUserCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        db.close();
        return count;
    }

    public User getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, COLUMN_ID + "=?", new String[]{String.valueOf(userId)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            String firstName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FIRST_NAME));
            String lastName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LAST_NAME));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE));
            String email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL));
            String password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD));
            String alamat = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ALAMAT));
            String ttl = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TTL));
            String photoUri = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHOTO_URI));
            double saldo = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_SALDO));

            cursor.close();
            return new User(userId, firstName, lastName, phone, email, password, alamat, ttl, photoUri, saldo);
        }

        if (cursor != null) cursor.close();
        return null;
    }

    public User getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, COLUMN_EMAIL + "=?", new String[]{email}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
            String firstName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FIRST_NAME));
            String lastName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LAST_NAME));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE));
            String password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD));
            String alamat = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ALAMAT));
            String ttl = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TTL));
            String photoUri = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHOTO_URI));
            double saldo = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_SALDO));

            cursor.close();
            return new User(id, firstName, lastName, phone, email, password, alamat, ttl, photoUri, saldo);
        }

        if (cursor != null) cursor.close();
        return null;
    }

    public User getUserByEmailAndPassword(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE email = ? AND password = ?", new String[]{email, password});

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
            String firstName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FIRST_NAME));
            String lastName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LAST_NAME));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE));
            String alamat = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ALAMAT));
            String ttl = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TTL));
            String photoUri = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHOTO_URI));
            double saldo = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_SALDO));

            cursor.close();
            return new User(id, firstName, lastName, phone, email, password, alamat, ttl, photoUri, saldo);
        }

        if (cursor != null) cursor.close();
        return null;
    }

    public double getSaldo(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_SALDO + " FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?", new String[]{String.valueOf(userId)});
        double saldo = 0;
        if (cursor.moveToFirst()) {
            saldo = cursor.getDouble(0);
        }
        cursor.close();
        db.close();
        return saldo;
    }

    public boolean insertVoucher(String judul, String syarat) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_VOUCHER_JUDUL, judul);
        values.put(COLUMN_VOUCHER_SYARAT, syarat);
        long result = db.insert(TABLE_VOUCHER, null, values);
        return result != -1;
    }

    public Cursor getAllVouchers() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_VOUCHER, null);
    }

    public boolean tambahSaldo(int userId, double jumlah) {
        double saldoSekarang = getSaldo(userId);
        double saldoBaru = saldoSekarang + jumlah;

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SALDO, saldoBaru);
        int rows = db.update(TABLE_NAME, values, COLUMN_ID + "=?", new String[]{String.valueOf(userId)});
        db.close();
        return rows > 0;
    }

    public int getUserIdByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        int userId = -1;
        Cursor cursor = db.rawQuery("SELECT id FROM users WHERE email = ?", new String[]{email});
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
        }
        cursor.close();
        db.close();
        return userId;
    }

    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String firstName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FIRST_NAME));
                String lastName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LAST_NAME));
                String phone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE));
                String email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL));
                String password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD));
                String alamat = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ALAMAT));
                String ttl = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TTL));
                String photoUri = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHOTO_URI));
                double saldo = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_SALDO));

                userList.add(new User(id, firstName, lastName, phone, email, password, alamat, ttl, photoUri, saldo));
            } while (cursor.moveToNext());
            cursor.close();
        }

        db.close();
        return userList;
    }
}