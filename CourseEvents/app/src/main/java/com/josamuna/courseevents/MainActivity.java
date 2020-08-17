package com.josamuna.courseevents;

import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CourseEventsDisplayCallbacks {

    private List<String> mCourseEvents;
    private ArrayAdapter<String> mCoursesEventsAdapter;
    private CourseEventsReceiver mCourseEventsReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = findViewById(R.id.list_course_events);
        mCourseEvents = new ArrayList<>();
        mCoursesEventsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mCourseEvents);
        listView.setAdapter(mCoursesEventsAdapter);

        setupCourseEventReceiver();
    }

    private void setupCourseEventReceiver() {
        mCourseEventsReceiver = new CourseEventsReceiver();
        mCourseEventsReceiver.setCourseEventsDisplayCallbacks(this);

        IntentFilter intentFilter = new IntentFilter(CourseEventsReceiver.ACTION_COURSE_EVENT);
        registerReceiver(mCourseEventsReceiver, intentFilter);
    }

    @Override
    public void onEventReceived(String courseId, String courseMessage) {
        String displayText = String.format("%s : %s", courseId, courseMessage);
        mCourseEvents.add(displayText);
        mCoursesEventsAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mCourseEventsReceiver);
        super.onDestroy();
    }
}