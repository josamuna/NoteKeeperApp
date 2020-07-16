package com.josamuna.notekeeper;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NoteRecyclerAdapter extends RecyclerView.Adapter<NoteRecyclerAdapter.ViewHolder> {

    private final Context mContext;
    private final List<NoteInfo> mNote;
    private final LayoutInflater mLayoutInflater;

    NoteRecyclerAdapter(Context context, List<NoteInfo> note) {
        mContext = context;
        mNote = note;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.item_note_list, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NoteInfo noteInfo = mNote.get(position);
        holder.mTextCourse.setText(noteInfo.getCourse().getTitle());
        holder.mTextTitle.setText(noteInfo.getTitle());
        holder.mCurrentPosition = position;
    }

    @Override
    public int getItemCount() {
        return mNote.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        final TextView mTextCourse;
        final TextView mTextTitle;
        int mCurrentPosition;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            mTextCourse = itemView.findViewById(R.id.text_course_card);
            mTextTitle = itemView.findViewById(R.id.text_title_card);

            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, NoteActivity.class);
                intent.putExtra(NoteActivity.NOTE_POSITION, mCurrentPosition);
                mContext.startActivity(intent);
            });
        }
    }
}
