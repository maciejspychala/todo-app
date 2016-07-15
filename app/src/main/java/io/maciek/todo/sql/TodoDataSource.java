package io.maciek.todo.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import io.maciek.todo.rest.models.Note;

/**
 * Created by maciej on 14.07.16.
 */
public class TodoDataSource {
    private SQLiteDatabase database;
    private TodoSqlHelper dbHelper;
    private String[] allColumns = new String[]{TodoSqlHelper.COLUMN_ID,
            TodoSqlHelper.COLUMN_USER_ID, TodoSqlHelper.COLUMN_TITLE, TodoSqlHelper.COLUMN_CHANGED, TodoSqlHelper.COLUMN_CHECKED};

    public TodoDataSource(Context context) {
        dbHelper = new TodoSqlHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void add(Note note) {
        ContentValues values = new ContentValues();
        values.put(TodoSqlHelper.COLUMN_ID, note.getId());
        values.put(TodoSqlHelper.COLUMN_TITLE, note.getTitle());
        values.put(TodoSqlHelper.COLUMN_CHECKED, note.isCompleted() ? 1 : 0);
        values.put(TodoSqlHelper.COLUMN_CHANGED, note.isChanged() ? 1 : 0);
        values.put(TodoSqlHelper.COLUMN_USER_ID, note.getUserId());
        database.insert(TodoSqlHelper.TABLE_TODO, null, values);
    }

    public void add(List<Note> notes) {
        for (Note note : notes) {
            add(note);
        }
    }

    public void deleteNote(Note note) {
        long id = note.getId();
        database.delete(TodoSqlHelper.TABLE_TODO, TodoSqlHelper.COLUMN_ID
                + " = " + id, null);
    }

    public void deleteAll() {
        database.delete(TodoSqlHelper.TABLE_TODO, "1 = 1", null);
    }

    public List<Note> getAllNotes() {
        List<Note> notes = new ArrayList<>();

        Cursor cursor = database.query(TodoSqlHelper.TABLE_TODO,
                allColumns, "", null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Note note = cursorToNote(cursor);
            notes.add(note);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return notes;
    }

    public void updateNote(Note note) {
        ContentValues values = new ContentValues();
        values.put(TodoSqlHelper.COLUMN_ID, note.getId());
        values.put(TodoSqlHelper.COLUMN_TITLE, note.getTitle());
        values.put(TodoSqlHelper.COLUMN_CHECKED, note.isCompleted());
        values.put(TodoSqlHelper.COLUMN_CHANGED, note.isChanged());
        values.put(TodoSqlHelper.COLUMN_USER_ID, note.getUserId());
        database.update(TodoSqlHelper.TABLE_TODO, values, TodoSqlHelper.COLUMN_ID + " = " + Integer.toString(note.getId()), null);
    }

    private Note cursorToNote(Cursor cursor) {
        Note note = new Note();
        note.setId(cursor.getInt(0));
        note.setUserId(cursor.getInt(1));
        note.setTitle(cursor.getString(2));
        note.setChanged(cursor.getInt(3) > 0);
        note.setCompleted(cursor.getInt(4) > 0);
        return note;
    }
}
