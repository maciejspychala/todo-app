package io.maciek.todo.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by maciej on 14.07.16.
 */
public class TodoSqlHelper extends SQLiteOpenHelper {

    public static final String TABLE_TODO = "todo";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_CHECKED = "checked";
    public static final String COLUMN_CHANGED = "changed";
    public static final String COLUMN_USER_ID = "userid";

    private static final String DATABASE_NAME = "todos.db";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE = "create table "
            + TABLE_TODO + "( "
            + COLUMN_ID + " integer primary key, "
            + COLUMN_TITLE + " text, "
            + COLUMN_CHECKED + " integer, "
            + COLUMN_CHANGED + " integer, "
            + COLUMN_USER_ID + " integer );";


    public TodoSqlHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TODO);
        onCreate(db);
    }
}
