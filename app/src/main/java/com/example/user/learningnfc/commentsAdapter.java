package com.example.user.learningnfc;

import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.content.Context;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import android.widget.TextView;

public class commentsAdapter extends RecyclerView.Adapter<commentsAdapter.CommentsViewHolder> {
    public static Context mCtx;
    public static List<Comments> comments;

    public static class CommentsViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public TextView student_name, message, teacher_score;

        public CommentsViewHolder(View view) {
            super(view);
            mView = view;

            student_name = (TextView) view.findViewById(R.id.text_student_user);
            message = (TextView) view.findViewById(R.id.text_message);
            teacher_score = (TextView) view.findViewById(R.id.text_score);
        }
    }

    public commentsAdapter(List<Comments> comments) {
        this.comments = comments;
    }


    // Create new views (invoked by the layout manager)
    @Override
    public CommentsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_comments, parent, false);
        // set the view's size, margins, paddings and layout parameters

        return new CommentsViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(CommentsViewHolder holder, int position) {
        Comments comment = comments.get(position);
        holder.student_name.setText(comment.getName());
        holder.message.setText(comment.getMessage());
        holder.teacher_score.setText(comment.getTeacher_score());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return (comments != null ? comments.size() : 0);
    }
}


