package com.example.gocar.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.HashMap;

public class SQLiteHandler extends SQLiteOpenHelper {

    //This class takes care of storing the user data in SQLite database. Whenever we need to get the logged in user information, we fetch from SQLite instead of making request to server.

    private static final String TAG = SQLiteHandler.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "android_api";

    // Login table name
    private static final String TABLE_USER = "user";
    private static final String TABLE_REVIEWS = "review";

    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_UID = "uid";
    private static final String KEY_CREATED_AT = "created_at";
    private static final String KEY_NATIONALITY = "nationality";
    private static final String KEY_AGE = "age";
    private static final String KEY_PHONE_NO = "phone_no";

    private static final String KEY_REVIEW_ID = "id";
    private static final String KEY_UID_REVIEW = "uid";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_CAR_ID = "car_id";
    private static final String KEY_REVIEW = "review";

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_REVIEW_TABLE = "CREATE TABLE " + TABLE_REVIEWS + "("
                + KEY_REVIEW_ID + " INTEGER PRIMARY KEY ," + KEY_UID_REVIEW + " TEXT," + KEY_USER_ID + " INTEGER,"
                + KEY_CAR_ID + " INTEGER," + KEY_REVIEW + " TEXT" + ")";

        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_UID + " TEXT," + KEY_NAME + " TEXT,"
                + KEY_EMAIL + " TEXT UNIQUE," + KEY_AGE + " TEXT," + KEY_NATIONALITY + " TEXT,"
                + KEY_PHONE_NO + " TEXT," + KEY_CREATED_AT + " TEXT" + ")";

        db.execSQL(CREATE_LOGIN_TABLE);
        db.execSQL(CREATE_REVIEW_TABLE);

        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REVIEWS);

        // Create tables again
        onCreate(db);
    }

    /**
     * Storing user details in database
     * */
    public void addUser(String id, String name, String email, String uid, String created_at, String age, String nationality, String phone_no) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, id);
        values.put(KEY_UID, uid);
        values.put(KEY_NAME, name);
        values.put(KEY_EMAIL, email);
        values.put(KEY_AGE, age);
        values.put(KEY_NATIONALITY, nationality);
        values.put(KEY_PHONE_NO, phone_no);
        values.put(KEY_CREATED_AT, created_at);


        // Inserting Row
        db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite: " + id);
    }

    public void addReview(String uid , String user_id , String car_id , String review) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_UID_REVIEW , uid);
        values.put(KEY_USER_ID , user_id);
        values.put(KEY_CAR_ID , car_id);
        values.put(KEY_REVIEW , review);

        // Inserting Row
        long id = db.insert(TABLE_REVIEWS, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New review has been inserted into sqlite: " + id);
    }

    /**
     * Getting user data from database
     * */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("id", cursor.getString(0));
            user.put("uid", cursor.getString(1));
            user.put("name", cursor.getString(2));
            user.put("email", cursor.getString(3));
            user.put("age", cursor.getString(4));
            user.put("nationality", cursor.getString(5));
            user.put("phone_no", cursor.getString(6));
            user.put("created_at", cursor.getString(7));

        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());


        return user;
    }

    /**
     * Re crate database Delete all tables and create them again
     * */
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_USER, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }

}
