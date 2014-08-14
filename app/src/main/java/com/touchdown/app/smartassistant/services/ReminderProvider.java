package com.touchdown.app.smartassistant.services;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import com.touchdown.app.smartassistant.data.DbContract;
import com.touchdown.app.smartassistant.data.DbHelper;

/**
 * Created by Pete on 14.8.2014.
 */
public class ReminderProvider extends ContentProvider {
    private static final int REMINDER = 100;
    private static final int REMINDER_ID = 101;

    private static final int LOCATION = 300;
    private static final int LOCATION_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private SQLiteOpenHelper dbHelper;

    private static UriMatcher buildUriMatcher(){
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DbContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, DbContract.PATH_REMINDER, REMINDER);
        matcher.addURI(authority, DbContract.PATH_REMINDER +  "/#", REMINDER_ID);

        matcher.addURI(authority, DbContract.PATH_LOCATION, LOCATION);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch(match){
            case REMINDER_ID:
                return DbContract.ReminderEntry.CONTENT_ITEM_TYPE;
            case REMINDER:
                return DbContract.ReminderEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uir: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
