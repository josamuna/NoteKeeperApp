package com.josamuna.notekeeper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class CourseRecyclerAdapter extends RecyclerView.Adapter<CourseRecyclerAdapter.ViewHolder> {

    private final Context mContext;
    private final List<CourseInfo> mCourse;
    private final LayoutInflater mLayoutInflater;

    CourseRecyclerAdapter(Context context, List<CourseInfo> course) {
        mContext = context;
        mCourse = course;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.item_course_list, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CourseInfo courseInfo = mCourse.get(position);
        holder.mTextCourse.setText(courseInfo.getTitle());
        holder.mCurrentPosition = position;
    }

    @Override
    public int getItemCount() {
        return mCourse.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        final TextView mTextCourse;
        int mCurrentPosition;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            mTextCourse = itemView.findViewById(R.id.text_course_card);

            itemView.setOnClickListener(v -> Snackbar.make(v, mCourse.get(mCurrentPosition).getTitle(), Snackbar.LENGTH_LONG).show());
        }
    }
}
