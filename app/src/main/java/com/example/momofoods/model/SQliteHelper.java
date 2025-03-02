package com.example.momofoods.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SQliteHelper extends SQLiteOpenHelper {
    public SQliteHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("PRAGMA foreign_keys = ON;");

        // User Table
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS user (" +
                "mobile TEXT NOT NULL PRIMARY KEY, " +
                "email TEXT NOT NULL, " +
                "name TEXT NOT NULL)");

        // Categories Table
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS categories (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL)");

        // Foods Table
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS foods (" +
                "id TEXT NOT NULL PRIMARY KEY, " +
                "name TEXT NOT NULL, " +
                "descrption TEXT NOT NULL, " +
                "price TEXT NOT NULL, " +
                "rating TEXT NOT NULL, " +
                "calories TEXT NOT NULL, " +
                "qty TEXT NOT NULL, " +
                "categories_id INTEGER NOT NULL, " +
                "FOREIGN KEY (categories_id) REFERENCES categories (id) ON DELETE NO ACTION ON UPDATE NO ACTION)");

        // Cart Table
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS cart (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "qty INTEGER NOT NULL, " +
                "foods_id TEXT NOT NULL, " +
                "user_mobile TEXT NOT NULL, " +
                "FOREIGN KEY (foods_id) REFERENCES foods (id) ON DELETE NO ACTION ON UPDATE NO ACTION, " +
                "FOREIGN KEY (user_mobile) REFERENCES user (mobile) ON DELETE NO ACTION ON UPDATE NO ACTION)");

        // Admin Type Table
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS admin_type (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "type TEXT NOT NULL)");

        // Admin Table
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS admin (" +
                "mobile TEXT NOT NULL PRIMARY KEY, " +
                "email TEXT NOT NULL, " +
                "name TEXT NOT NULL, " +
                "datetime TEXT NOT NULL, " +
                "admin_type_id INTEGER NOT NULL, " +
                "FOREIGN KEY (admin_type_id) REFERENCES admin_type (id) ON DELETE NO ACTION ON UPDATE NO ACTION)");

        // Menu Table
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS menu (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "foods_id TEXT NOT NULL, " +
                "FOREIGN KEY (foods_id) REFERENCES foods (id) ON DELETE NO ACTION ON UPDATE NO ACTION)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // Drop tables on upgrade
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS user");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS categories");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS foods");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS cart");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS admin_type");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS admin");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS menu");

        // Recreate tables
        onCreate(sqLiteDatabase);
    }
}
