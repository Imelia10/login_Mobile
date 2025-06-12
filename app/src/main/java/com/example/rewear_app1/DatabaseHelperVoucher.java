package com.example.rewear_app1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelperVoucher extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "UserVouchers.db";
    private static final int DATABASE_VERSION = 5;

    private static final String TABLE_CLAIMED_VOUCHERS = "claimed_vouchers";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USER_EMAIL = "user_email";
    private static final String COLUMN_VOUCHER_ID = "voucher_id";
    private static final String COLUMN_VOUCHER_CODE = "voucher_code";
    private static final String COLUMN_CLAIM_DATE = "claim_date";

    public DatabaseHelperVoucher(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_CLAIMED_VOUCHERS + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_USER_EMAIL + " TEXT, "
                + COLUMN_VOUCHER_ID + " INTEGER, "
                + COLUMN_VOUCHER_CODE + " TEXT, "
                + COLUMN_CLAIM_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                + ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLAIMED_VOUCHERS);
        onCreate(db);
    }

    // Tambah klaim voucher untuk user jika belum pernah diklaim
    public boolean addClaimedVoucher(String userEmail, int voucherId, String voucherCode) {
        SQLiteDatabase db = this.getWritableDatabase();

        if (isVoucherClaimed(userEmail, voucherId)) {
            return false; // Sudah diklaim, tidak perlu tambah lagi
        }

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_EMAIL, userEmail);
        values.put(COLUMN_VOUCHER_ID, voucherId);
        values.put(COLUMN_VOUCHER_CODE, voucherCode);

        long result = db.insert(TABLE_CLAIMED_VOUCHERS, null, values);
        db.close();
        return result != -1;
    }

    // Hapus voucher berdasarkan email dan voucher_id
    public void hapusVoucher(String email, int voucherId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int deleted = db.delete(TABLE_CLAIMED_VOUCHERS, COLUMN_USER_EMAIL + " = ? AND " + COLUMN_VOUCHER_ID + " = ?",
                new String[]{email, String.valueOf(voucherId)});
        db.close();
        Log.d("DatabaseHelperVoucher", "Voucher yang dihapus: " + deleted);
    }

    // Ambil jumlah voucher yang sudah diklaim user
    public int getJumlahVoucherYangDiklaim(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM claimed_vouchers WHERE user_email = ?", new String[]{email});
        int count = 0;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        return count;
    }


    // Ambil semua voucher_id yang diklaim user
    public List<Integer> getClaimedVoucherIds(String email) {
        List<Integer> claimedIds = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery(
                    "SELECT " + COLUMN_VOUCHER_ID + " FROM " + TABLE_CLAIMED_VOUCHERS + " WHERE " + COLUMN_USER_EMAIL + " = ?",
                    new String[]{email});

            if (cursor.moveToFirst()) {
                do {
                    claimedIds.add(cursor.getInt(0));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("DatabaseHelperVoucher", "Error mengambil claimed_vouchers: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
        }

        return claimedIds;
    }



    // Cek apakah voucher sudah diklaim user
    public boolean isVoucherClaimed(String userEmail, int voucherId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            String query = "SELECT 1 FROM " + TABLE_CLAIMED_VOUCHERS +
                    " WHERE " + COLUMN_USER_EMAIL + " = ? AND " + COLUMN_VOUCHER_ID + " = ?";
            cursor = db.rawQuery(query, new String[]{userEmail, String.valueOf(voucherId)});
            return cursor.moveToFirst();
        } finally {
            if (cursor != null) cursor.close();
        }
    }

}
