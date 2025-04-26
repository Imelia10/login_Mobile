package com.example.rewear_app1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ReWear.db";
    private static final int DATABASE_VERSION = 3; // Naikkan versi untuk perubahan
    private static final String TABLE_NAME = "users";
    private static final String COLUMN_PHONE = "phone";
    private static final String COLUMN_FIRST_NAME = "first_name";
    private static final String COLUMN_LAST_NAME = "last_name";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_ALAMAT = "alamat";
    private static final String COLUMN_TTL = "ttl";
    private static final String COLUMN_PHOTO_URI = "photoUri"; // Kolom baru

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + "(" +
                COLUMN_PHONE + " TEXT PRIMARY KEY, " +
                COLUMN_FIRST_NAME + " TEXT, " +
                COLUMN_LAST_NAME + " TEXT, " +
                COLUMN_EMAIL + " TEXT, " +
                COLUMN_PASSWORD + " TEXT, " +
                COLUMN_ALAMAT + " TEXT, " +
                COLUMN_TTL + " TEXT, " +
                COLUMN_PHOTO_URI + " TEXT)";  // Menambahkan kolom photoUri
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Tambahkan kolom photoUri jika versi database lebih rendah dari 2
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COLUMN_PHOTO_URI + " TEXT");
        }
    }

    public boolean registerUser(String phone, String firstName, String lastName, String email, String password, String alamat, String ttl, String photoUri) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_PHONE, phone);
        contentValues.put(COLUMN_FIRST_NAME, firstName);
        contentValues.put(COLUMN_LAST_NAME, lastName);
        contentValues.put(COLUMN_EMAIL, email);
        contentValues.put(COLUMN_PASSWORD, password);
        contentValues.put(COLUMN_ALAMAT, alamat);
        contentValues.put(COLUMN_TTL, ttl);
        contentValues.put(COLUMN_PHOTO_URI, photoUri);  // Menyimpan URI foto profil

        long result = db.insert(TABLE_NAME, null, contentValues);
        db.close();
        return result != -1;
    }

    public boolean isEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, COLUMN_EMAIL + "=?", new String[]{email}, null, null, null);
        boolean exists = (cursor != null && cursor.moveToFirst());
        if (cursor != null) cursor.close();
        return exists;
    }

    public User getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, COLUMN_EMAIL + "=?", new String[]{email}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            String firstName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FIRST_NAME));
            String lastName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LAST_NAME));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE));
            String emailDb = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL));
            String passwordDb = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD));
            String alamat = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ALAMAT));
            String ttl = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TTL));
            String photoUri = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHOTO_URI));

            cursor.close();
            return new User(firstName, lastName, phone, emailDb, passwordDb, alamat, ttl, photoUri);
        }

        if (cursor != null) {
            cursor.close();
        }
        return null;
    }

    public User getUserByEmailAndPassword(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE email = ? AND password = ?", new String[]{email, password});

        if (cursor != null && cursor.moveToFirst()) {
            String firstName = cursor.getString(cursor.getColumnIndexOrThrow("first_name"));
            String lastName = cursor.getString(cursor.getColumnIndexOrThrow("last_name"));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"));
            String emailDb = cursor.getString(cursor.getColumnIndexOrThrow("email"));
            String passwordDb = cursor.getString(cursor.getColumnIndexOrThrow("password"));
            String alamat = cursor.getString(cursor.getColumnIndexOrThrow("alamat"));
            String ttl = cursor.getString(cursor.getColumnIndexOrThrow("ttl"));
            String photoUri = cursor.getString(cursor.getColumnIndexOrThrow("photoUri"));

            cursor.close();
            return new User(firstName, lastName, phone, emailDb, passwordDb, alamat, ttl, photoUri);
        }

        if (cursor != null) {
            cursor.close();
        }
        return null;
    }
}
