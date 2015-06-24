package pl.proama.todoekspert.db;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import javax.inject.Inject;
import javax.inject.Singleton;

import pl.proama.todoekspert.Todo;

@Singleton
public class TodoDao {

    /**
     * Nazwy kolumn w DB.
     */
    public static final String C_ID = "_id";
    public static final String C_CONTENT = "content";
    public static final String C_DONE = "done";
    public static final String C_USER_ID = "user_id";
    public static final String C_CREATED_AT = "created_at";
    public static final String C_UPDATED_AT = "updated_at";

    public static final String TABLE_NAME = "todos";
    private DbHelper dbHelper;

    @Inject
    public TodoDao(DbHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    /**
     * Metoda zapisująca obiekt {@link Todo} do bazy. W przypadku konfliktu nadpisuje starą wartość.
     * @param todo
     */
    public void insertOrUpdate(Todo todo) {

        //pobranie bazy danych do zapisu
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        //utworzenie słownika nazwa kolumny -> wartość
        ContentValues values = new ContentValues();
        values.put(C_ID, todo.getObjectId());
        values.put(C_CONTENT, todo.getContent());
        values.put(C_DONE, todo.isDone());
        values.put(C_CREATED_AT, todo.getCreatedAt().getTime());
        values.put(C_UPDATED_AT, todo.getUpdatedAt().getTime());
        values.put(C_USER_ID, todo.getUser().getObjectId());

        db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);

    }

    public Cursor query(String userId, boolean sortAsc) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        //SELECT * FROM todos WHERE user_id=? ORDER BY ASC/DESC
        return db.query(TABLE_NAME, null, String.format("%s=?", C_USER_ID),
                new String[]{userId}, null, null,
                String.format("%s %s", C_UPDATED_AT, sortAsc ? "ASC" : "DESC"));
    }

}
