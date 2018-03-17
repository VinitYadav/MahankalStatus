package com.mahankalstatus.android.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mahankalstatus.android.bean.AddStatus;

import java.util.ArrayList;

public class DatabaseHandler extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "newsManager";
    // Contacts table name
    private static final String TABLE_STATUS = "all_status";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_STATUS_ID = "status_id";
    private static final String KEY_STATUS = "status";
    private static final String KEY_SERVER_KEY = "server_key";
    private static final String KEY_IS_APPROVED = "is_approved";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_STATUS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_STATUS_ID + " TEXT,"
                + KEY_STATUS + " TEXT,"
                + KEY_SERVER_KEY + " TEXT,"
                + KEY_IS_APPROVED + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATUS);
        // Create tables again
        onCreate(db);
    }

    // Adding new contact
    public void addContact(AddStatus bean) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_STATUS_ID, bean.getId());
        values.put(KEY_STATUS, bean.getStatus());
        values.put(KEY_SERVER_KEY, bean.getServerKey());
        values.put(KEY_IS_APPROVED, bean.getIsApproved());

        // Inserting Row
        db.insert(TABLE_STATUS, null, values);
        db.close(); // Closing database connection
    }

    /**
     * Clear table
     */
    public void clearTable() {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("delete from " + TABLE_STATUS);
    }

    // Getting single contact
    public AddStatus getSingleNews(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_STATUS, new String[]{KEY_ID,
                        KEY_STATUS_ID,
                        KEY_STATUS,
                        KEY_SERVER_KEY,
                        KEY_IS_APPROVED}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        AddStatus bean = new AddStatus(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
        // return contact
        return bean;
    }

    // Getting All news
    public ArrayList<AddStatus> getAllNews() {
        ArrayList<AddStatus> list = new ArrayList<AddStatus>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_STATUS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        int size = cursor.getCount();
        if (cursor.moveToFirst()) {
            do {
                list.add(new AddStatus(cursor.getString(2), cursor.getString(1), cursor.getString(3), cursor.getString(4)));
            } while (cursor.moveToNext());
        }
        return list;
    }

    /**
     * Get total news count
     *
     * @return Total count
     */
    public int getNewsCount() {
        String selectQuery = "SELECT  * FROM " + TABLE_STATUS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor.getCount();
    }

    /*// Updating single contact
    public int updateContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, contact.getName());
        values.put(KEY_PH_NO, contact.getPhoneNumber());

        // updating row
        return db.update(TABLE_NEWS, values, KEY_ID + " = ?",
                new String[]{String.valueOf(contact.getID())});
    }*/

    /*// Deleting single contact
    public void deleteContact(AddNewsBean bean) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NEWS, KEY_ID + " = ?",
                new String[]{String.valueOf(contact.getID())});
        db.close();
    }*/

    public void deleteItem(String key) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_STATUS, KEY_SERVER_KEY + " = ?",
                new String[]{String.valueOf(key)});
        db.close();
    }


    /*// Getting contacts Count
    public int getContactsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_NEWS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }*/

}