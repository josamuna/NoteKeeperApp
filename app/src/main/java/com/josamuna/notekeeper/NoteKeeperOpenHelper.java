package com.josamuna.notekeeper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.josamuna.notekeeper.NoteKeeperDatabaseContract.CourseInfoEntry;
import com.josamuna.notekeeper.NoteKeeperDatabaseContract.NoteInfoEntry;

public class NoteKeeperOpenHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "NoteKeeper.db";
    // Version 1 contains tables and version 2 contains tables and indexes
    public static final int DATABASE_VERSION = 2;

    // factory used to customize our database interaction, we don't really need to do that and replace it to null value
    public NoteKeeperOpenHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CourseInfoEntry.SQL_CREATE_TABLE);
        db.execSQL(NoteInfoEntry.SQL_CREATE_TABLE);

        //CREATE INDEXES
        db.execSQL(CourseInfoEntry.SQL_CREATE_INDEX1);
        db.execSQL(NoteInfoEntry.SQL_CREATE_INDEX1);

        // Initialize first data
        DatabaseDataWorker worker = new DatabaseDataWorker(db);
        worker.insertCourses();
        worker.insertSampleNotes();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion < 2) {
            db.execSQL(CourseInfoEntry.SQL_CREATE_INDEX1);
            db.execSQL(NoteInfoEntry.SQL_CREATE_INDEX1 );
        }
    }
}
