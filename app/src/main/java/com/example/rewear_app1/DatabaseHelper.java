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
    private static final int DATABASE_VERSION = 7;

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

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_PHONE + " TEXT UNIQUE, " +
                COLUMN_FIRST_NAME + " TEXT, " +
                COLUMN_LAST_NAME + " TEXT, " +
                COLUMN_EMAIL + " TEXT, " +
                COLUMN_PASSWORD + " TEXT, " +
                COLUMN_ALAMAT + " TEXT, " +
                COLUMN_TTL + " TEXT, " +
                COLUMN_PHOTO_URI + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
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

            cursor.close();
            return new User(userId, firstName, lastName, phone, email, password, alamat, ttl, photoUri);
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

            cursor.close();
            return new User(id, firstName, lastName, phone, email, password, alamat, ttl, photoUri);
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

            cursor.close();
            return new User(id, firstName, lastName, phone, email, password, alamat, ttl, photoUri);
        }

        if (cursor != null) cursor.close();
        return null;
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

                userList.add(new User(id, firstName, lastName, phone, email, password, alamat, ttl, photoUri));
            } while (cursor.moveToNext());
            cursor.close();
        }

        db.close();
        return userList;
    }


}