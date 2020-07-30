package com.josamuna.notekeeper;

import android.net.Uri;
import android.provider.BaseColumns;

public final class NoteKeeperProviderContract {
    public NoteKeeperProviderContract() {
    }

    static final String AUTHORITY = "com.josamuna.notekeeper.provider";
    private static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

    protected interface CoursesIdColumns {
        static final String COLUMN_COURSE_ID = "course_id";
    }

    protected interface CoursesColumns {
        static final String COLUMN_COURSE_TITLE = "course_title";
    }

    protected interface NotesColumns {
        static final String COLUMN_NOTE_TITLE = "note_title";
        static final String COLUMN_NOTE_TEXT = "note_text";
    }

    static final class Courses implements BaseColumns, CoursesColumns, CoursesIdColumns {
        static final String PATH = "courses";
        // content://com.josamuna.notekeeper.provider/courses
        static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, PATH);
    }

    static final class Notes implements BaseColumns, NotesColumns, CoursesIdColumns, CoursesColumns {
        static final String PATH = "notes";
        // content://com.josamuna.notekeeper.notes
        static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, PATH);
        static final String PATH_EXPANDED = "notes_expanded";
        static final Uri CONTENT_EXPANDED_URI = Uri.withAppendedPath(AUTHORITY_URI, PATH_EXPANDED);
    }
}
