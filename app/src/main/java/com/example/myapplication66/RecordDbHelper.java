package com.example.myapplication66;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * SQLite数据库帮助类
 * 用于持久化存储消费记录数据
 */
public class RecordDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "expense_record.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "records";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_AMOUNT = "amount";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_NOTE = "note";
    public static final String COLUMN_DATE = "date";

    public RecordDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableSql = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_AMOUNT + " REAL, " +
                COLUMN_CATEGORY + " TEXT, " +
                COLUMN_NOTE + " TEXT, " +
                COLUMN_DATE + " TEXT)";
        db.execSQL(createTableSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    /**
     * 插入一条消费记录
     */
    public long insertTransaction(Transaction transaction) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_AMOUNT, transaction.getAmount());
        values.put(COLUMN_CATEGORY, transaction.getCategory());
        values.put(COLUMN_NOTE, transaction.getNote());
        values.put(COLUMN_DATE, transaction.getDate());
        long result = db.insert(TABLE_NAME, null, values);
        db.close();
        return result;
    }

    /**
     * 查询所有消费记录
     */
    public List<Transaction> getAllTransactions() {
        List<Transaction> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, COLUMN_ID + " DESC");

        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID));
            double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT));
            String category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY));
            String note = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTE));
            String date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE));

            Transaction transaction = new Transaction(amount, category, note, date);
            transaction.setId(id);
            list.add(transaction);
        }
        cursor.close();
        db.close();
        return list;
    }

    /**
     * 清空所有记录
     */
    public void deleteAllTransactions() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
    }

    /**
     * 获取记录总数
     */
    public int getCount() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }
}
