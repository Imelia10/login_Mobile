package com.example.rewear_app1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelperVoucher extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "UserVouchers.db";
    private static final int DATABASE_VERSION = 2;

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
        String CREATE_TABLE = "CREATE TABLE " + TABLE_CLAIMED_VOUCHERS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USER_EMAIL + " TEXT,"
                + COLUMN_VOUCHER_ID + " INTEGER,"
                + COLUMN_VOUCHER_CODE + " TEXT,"
                + COLUMN_CLAIM_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLAIMED_VOUCHERS);
        onCreate(db);
    }

    public boolean addClaimedVoucher(String userEmail, int voucherId, String voucherCode) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_EMAIL, userEmail);
        values.put(COLUMN_VOUCHER_ID, voucherId);
        values.put(COLUMN_VOUCHER_CODE, voucherCode);

        long result = db.insert(TABLE_CLAIMED_VOUCHERS, null, values);
        return result != -1;
    }

    public boolean isVoucherClaimed(String userEmail, int voucherId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_CLAIMED_VOUCHERS +
                " WHERE " + COLUMN_USER_EMAIL + " = ? AND " + COLUMN_VOUCHER_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{userEmail, String.valueOf(voucherId)});

        boolean isClaimed = cursor.getCount() > 0;
        cursor.close();
        return isClaimed;
    }
}