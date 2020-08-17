package com.josamuna.notekeeper;

import android.annotation.SuppressLint;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.net.Uri;
import android.os.AsyncTask;

public class NoteUploaderJobService extends JobService {
    public static final String EXTRA_DATA_URI = "com.josamuna.notekeeper.extras.DATA_URI";
    private NoteUploader mNoteUploader;

    public NoteUploaderJobService() {
    }

    @SuppressWarnings("deprecation")
    @Override
    @SuppressLint("StaticFieldLeak")
    public boolean onStartJob(JobParameters jobParameters) {
        AsyncTask<JobParameters, Void, Void> task = new AsyncTask<JobParameters, Void, Void>() {
            @Override
            protected Void doInBackground(JobParameters... backgroundParameters) {
                JobParameters jobParams = backgroundParameters[0];
                String stringDataUri = jobParams.getExtras().getString(EXTRA_DATA_URI);
                Uri dataUri = Uri.parse(stringDataUri);
                mNoteUploader.doUpload(dataUri);

                if(!mNoteUploader.isCanceled())
                    jobFinished(jobParams, false);// true indicate the jobScheduler will schedule the task to run it later and false indicate job is done and not need to be scheduler

                return null;
            }
        };

        mNoteUploader = new NoteUploader(this);
        task.execute(jobParameters);

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        mNoteUploader.cancel();
        return true;
    }
}
