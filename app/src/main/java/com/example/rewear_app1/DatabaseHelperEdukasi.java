package com.example.rewear_app1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseHelperEdukasi extends SQLiteOpenHelper {

    private static final String DB_NAME = "edukasi.db";
    private static final int DB_VERSION = 2; // Versi meningkat karena penambahan kolom

    public static final String TABLE_NAME = "edukasi";
    public static final String COL_ID = "id";
    public static final String COL_JUDUL = "judul";
    public static final String COL_DESKRIPSI = "deskripsi";
    public static final String COL_TIPE = "tipe"; // artikel / video
    public static final String COL_VIDEO_URI = "videoUri"; // kolom baru

    public DatabaseHelperEdukasi(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_JUDUL + " TEXT, " +
                COL_DESKRIPSI + " TEXT, " +
                COL_TIPE + " TEXT, " +
                COL_VIDEO_URI + " TEXT)";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Perbarui database dengan menambahkan kolom 'videoUri'
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_VIDEO_URI + " TEXT;");
        }
    }


    // Insert data edukasi
    public long insertEdukasi(String judul, String deskripsi, String tipe, String videoUri) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_JUDUL, judul);
        values.put(COL_DESKRIPSI, deskripsi);
        values.put(COL_TIPE, tipe);
        values.put(COL_VIDEO_URI, videoUri);
        long id = db.insert(TABLE_NAME, null, values);
        db.close();
        return id;
    }


    // Update data edukasi berdasarkan ID, termasuk tipe dan videoUri
    public void updateEdukasi(int id, String judul, String deskripsi, String tipe, String videoUri) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_JUDUL, judul);
        values.put(COL_DESKRIPSI, deskripsi);
        values.put(COL_TIPE, tipe);

        // Menambahkan videoUri hanya jika tipe adalah "video"
        if (tipe.equals("video")) {
            values.put(COL_VIDEO_URI, videoUri);
        }

        db.update(TABLE_NAME, values, COL_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }


    // Hapus data edukasi berdasarkan ID
    public void deleteEdukasi(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME, COL_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    // Ambil semua data edukasi
    public List<Map<String, String>> getAllEdukasi() {
        List<Map<String, String>> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Map<String, String> data = new HashMap<>();
                    data.put(COL_ID, String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID))));
                    data.put(COL_JUDUL, cursor.getString(cursor.getColumnIndexOrThrow(COL_JUDUL)));
                    data.put(COL_DESKRIPSI, cursor.getString(cursor.getColumnIndexOrThrow(COL_DESKRIPSI)));
                    data.put(COL_TIPE, cursor.getString(cursor.getColumnIndexOrThrow(COL_TIPE)));
                    data.put(COL_VIDEO_URI, cursor.getString(cursor.getColumnIndexOrThrow(COL_VIDEO_URI)));
                    list.add(data);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return list;
    }

    // Ambil ID terakhir yang dimasukkan
    public int getLastInsertedId() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        int id = -1;
        try {
            cursor = db.rawQuery("SELECT last_insert_rowid()", null);
            if (cursor != null && cursor.moveToFirst()) {
                id = cursor.getInt(0);
            }
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return id;
    }
}
