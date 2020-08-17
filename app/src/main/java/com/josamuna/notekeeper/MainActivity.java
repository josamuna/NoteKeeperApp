package com.josamuna.notekeeper;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.josamuna.notekeeper.NoteKeeperDatabaseContract.NoteInfoEntry;

import java.util.List;

import static com.josamuna.notekeeper.NoteKeeperProviderContract.Notes;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final int NOTE_UPLOADER_JOB_ID = 1;
    private NoteRecyclerAdapter mNoteRecyclerAdapter;
    private RecyclerView mRecyclerItems;
    private LinearLayoutManager mNoteLayoutManager;
    private CourseRecyclerAdapter mCourseRecyclerAdapter;
    private GridLayoutManager mGridLayoutManager;
    private NoteKeeperOpenHelper mDbOpenHelper;
    private int LOADER_NOTES = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get instance of NoteKeeperOpenHelper Class
        mDbOpenHelper = new NoteKeeperOpenHelper(this);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener((view) -> startActivity(new Intent(MainActivity.this, NoteActivity.class)));

        // Setting up the preferences values to the default values
        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false); // false indicate do not override every time pref values

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //noinspection deprecation
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        // Allow NavigationView to handle action on click
        navigationView.setNavigationItemSelectedListener(this);
        initializeDisplayContent();
    }

    @Override
    protected void onDestroy() {
        mDbOpenHelper.close();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mNoteRecyclerAdapter.notifyDataSetChanged();
//        loadNotes();
//        getSupportLoaderManager().restartLoader(LOADER_NOTES, null, this);
        getLoaderManager().restartLoader(LOADER_NOTES, null, this);
        updateNavHeader();
        openDrawer();
    }

    private void openDrawer() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> { // Use Runnable Interface with Functional Interface using Lambda Expression
            DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
            drawerLayout.openDrawer(GravityCompat.START);
        }, 1000);
    }

    @SuppressWarnings("unused")
    private void loadNotes() {
        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
        final String[] noteColumns = {
                NoteInfoEntry.COLUMN_NOTE_TITLE,
                NoteInfoEntry.COLUMN_COURSE_ID,
                NoteInfoEntry._ID};
        final String noteOrderBy = NoteInfoEntry.COLUMN_COURSE_ID + "," + NoteInfoEntry.COLUMN_NOTE_TITLE;
        final Cursor noteCursor = db.query(NoteInfoEntry.TABLE_NAME, noteColumns,
                null, null, null, null, noteOrderBy);
        // Associate Adapter (RecyclerAdapter) to the Cursor
        mNoteRecyclerAdapter.changeCursor(noteCursor);
    }

    private void updateNavHeader() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView textUserName = headerView.findViewById(R.id.text_user_name);
        TextView textEmailAddress = headerView.findViewById(R.id.text_email_address);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String userName = pref.getString("user_display_name", "");
        String emailAddress = pref.getString("user_email_address", "");

        textUserName.setText(userName);
        textEmailAddress.setText(emailAddress);
    }

    private void initializeDisplayContent() {
        // Read Data from database
        DataManager.loadFromDatabase(mDbOpenHelper);

        mRecyclerItems = findViewById(R.id.list_items);
        mNoteLayoutManager = new LinearLayoutManager(this);
        mGridLayoutManager = new GridLayoutManager(this, getResources().getInteger(R.integer.course_grid_span));

        mNoteRecyclerAdapter = new NoteRecyclerAdapter(this, null);

        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        mCourseRecyclerAdapter = new CourseRecyclerAdapter(this, courses);
        displayNote();
    }

    private void displayCourse() {
        mRecyclerItems.setLayoutManager(mGridLayoutManager);
        mRecyclerItems.setAdapter(mCourseRecyclerAdapter);
        selectNavigationMenuItem(R.id.nav_courses);
    }

    private void displayNote() {
        mRecyclerItems.setLayoutManager(mNoteLayoutManager);
        mRecyclerItems.setAdapter(mNoteRecyclerAdapter);
        selectNavigationMenuItem(R.id.nav_notes);
    }

    private void selectNavigationMenuItem(int id) {
        // Set selection menu item by default
        NavigationView navigationView = findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        menu.findItem(id).setChecked(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch(id) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.action_backup_notes:
                backupNotes();
                return true;
            case R.id.action_upload_notes:
                scheduleNoteUpload();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void scheduleNoteUpload() {
        PersistableBundle extras = new PersistableBundle();
        extras.putString(NoteUploaderJobService.EXTRA_DATA_URI, Notes.CONTENT_URI.toString());

        ComponentName componentName = new ComponentName(this, NoteUploaderJobService.class);
        JobInfo jobInfo = new JobInfo.Builder(NOTE_UPLOADER_JOB_ID, componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY) // Disabled that line to test without Network
                .setExtras(extras)
                .build();
        
        JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(jobInfo);
    }

    private void backupNotes() {
//        NoteBackup.doBackup(MainActivity.this, NoteBackup.ALL_COURSES); // Avoid to call doBackup() method directly, we would use Service instead of that.
        Intent intent = new Intent(this, NoteBackupService.class);
        intent.putExtra(NoteBackupService.EXTRA_COURSE_ID, NoteBackup.ALL_COURSES);
        startService(intent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_notes) {
            displayNote();
        } else if (id == R.id.nav_courses) {
            displayCourse();
        } else if (id == R.id.nav_share) {
//            handleSelection(R.string.nav_share_message);
            handleShare();
        } else if (id == R.id.nav_send) {
            handleSelection();
        }

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void handleShare() {
        View view = findViewById(R.id.list_items);
        Snackbar.make(view, "Shared To - " +
                PreferenceManager.getDefaultSharedPreferences(this).getString("user_favorite_social", ""),
                Snackbar.LENGTH_LONG).show();
    }

    private void handleSelection() {
        View view = findViewById(R.id.list_items);
        Snackbar.make(view, R.string.nav_send_message, Snackbar.LENGTH_LONG).show();
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader loader = null;
        if(id == LOADER_NOTES) {
            final String[] noteColumns = {
                    Notes._ID,
                    Notes.COLUMN_NOTE_TITLE,
                    Notes.COLUMN_COURSE_TITLE };
            final String noteOrderBy = Notes.COLUMN_COURSE_TITLE +
                    "," + Notes.COLUMN_NOTE_TITLE;

            loader = new CursorLoader(this, Notes.CONTENT_EXPANDED_URI, noteColumns,
                    null, null, noteOrderBy);
//            loader = new CursorLoader(this) {
//                @Override
//                public Cursor loadInBackground() {
//                    SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
//                    final String[] noteColumns = {
//                            NoteInfoEntry.getQName(NoteInfoEntry._ID),
//                            NoteInfoEntry.COLUMN_NOTE_TITLE,
//                            CourseInfoEntry.COLUMN_COURSE_TITLE};
//                    final String noteOrderBy = CourseInfoEntry.COLUMN_COURSE_TITLE +
//                            "," + NoteInfoEntry.COLUMN_NOTE_TITLE;

       //             // note_info JOIN course_info ON note_info.course_id = course_info.course_id
//                    String tablesWithJoin = NoteInfoEntry.TABLE_NAME + " JOIN " +
//                            CourseInfoEntry.TABLE_NAME + " ON " +
//                            NoteInfoEntry.getQName(NoteInfoEntry.COLUMN_COURSE_ID) + " = " +
//                            CourseInfoEntry.getQName(CourseInfoEntry.COLUMN_COURSE_ID);
//
//                    return db.query(tablesWithJoin, noteColumns,
//                            null, null, null, null, noteOrderBy);
//                }
//            };
        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(loader.getId() == LOADER_NOTES)  {
            mNoteRecyclerAdapter.changeCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if(loader.getId() == LOADER_NOTES)  {
            mNoteRecyclerAdapter.changeCursor(null);
        }
    }
}
