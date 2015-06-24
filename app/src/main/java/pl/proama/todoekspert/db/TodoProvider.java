package pl.proama.todoekspert.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class TodoProvider extends ContentProvider {


	private static final String TODOS_COLECTION = "todos";

	private static final int TODOS = 1;
	private static final int TODOS_ID = 2;

	private static final String AUTHORITY = "pl.proama.todoekspert.Provider";

	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" +
			TODOS_COLECTION);
	
	public static final String MIME_TYPE_DIR = "vnd.todoekspert.cursor.dir/todo";
	public static final String MIME_TYPE_ITEM = "vnd.todoekspert.cursor.item/todo";
	
	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static
    {
        sURIMatcher.addURI(AUTHORITY, TODOS_COLECTION, TODOS);
        sURIMatcher.addURI(AUTHORITY, TODOS_COLECTION + "/#", TODOS_ID);
     
    }

	private DbHelper dbHelper;
    
    @Override
	public String getType(Uri uri) {
		int match = sURIMatcher.match(uri);
		if(match == TODOS_ID) {
			return MIME_TYPE_ITEM;
		} else {
			return MIME_TYPE_DIR;
		} 
		
	}

	@Override
	public boolean onCreate() {
		dbHelper = new DbHelper(getContext());
		return dbHelper != null;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteDatabase readableDatabase = dbHelper.getReadableDatabase();
		Cursor cursor = readableDatabase.query(TodoDao.TABLE_NAME, projection, selection, selectionArgs,
				null, null, sortOrder);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		
		return cursor;
	}

	

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase writableDatabase = dbHelper.getWritableDatabase();
		long id = writableDatabase.insertWithOnConflict(TodoDao.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
		
		getContext().getContentResolver().notifyChange(uri, null);
		
		
		return ContentUris.withAppendedId(uri, id);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase writableDatabase = dbHelper.getWritableDatabase();
		int count = writableDatabase.delete(TodoDao.TABLE_NAME, selection, selectionArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		SQLiteDatabase writableDatabase = dbHelper.getWritableDatabase();
		int count = writableDatabase.update(TodoDao.TABLE_NAME, values, selection, selectionArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		
		return count;
	}

}