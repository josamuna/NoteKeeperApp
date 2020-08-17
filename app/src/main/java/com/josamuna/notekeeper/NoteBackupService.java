package com.josamuna.notekeeper;

import android.app.IntentService;
import android.content.Intent;

import java.util.Objects;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 */
public class NoteBackupService extends IntentService {
    public static final String EXTRA_COURSE_ID = "com.josamuna.notekeeper.extra.COURSE_ID";

    public NoteBackupService() {
        super("NoteBackupService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String backupCourseId = intent.getStringExtra(EXTRA_COURSE_ID);
            NoteBackup.doBackup(this, Objects.requireNonNull(backupCourseId));
        }
    }
}
