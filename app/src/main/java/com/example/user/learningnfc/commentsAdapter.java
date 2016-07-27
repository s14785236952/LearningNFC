package com.example.user.learningnfc;

import android.support.v7.widget.PopupMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.content.Context;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import android.widget.Button;
import android.widget.TextView;

public class commentsAdapter extends RecyclerView.Adapter<commentsAdapter.CommentsViewHolder> {
    public static Context mCtx;
    public static List<Comments> comments;

    private PopupMenu menu;

    public static class CommentsViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public TextView student_name, message, teacher_score;
        public Button option_menu;

        public CommentsViewHolder(View view) {
            super(view);
            mView = view;

            student_name = (TextView) view.findViewById(R.id.text_student_user);
            message = (TextView) view.findViewById(R.id.text_message);
            teacher_score = (TextView) view.findViewById(R.id.text_score);
            option_menu = (Button) view.findViewById(R.id.option_button);
        }
    }

    public commentsAdapter(List<Comments> comments) {
        this.comments = comments;
    }


    @Override
    public CommentsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_comments, parent, false);

        return new CommentsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final CommentsViewHolder holder, int position) {
        Comments comment = comments.get(position);
        holder.student_name.setText(comment.getName());
        holder.message.setText(comment.getMessage());
        holder.teacher_score.setText(comment.getTeacher_score());

        holder.option_menu.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                menu = new PopupMenu(v.getContext(), holder.option_menu);
                menu.inflate(R.menu.menu_comment);
                menu.getMenu().findItem(R.id.action_modify).setVisible(true);
                menu.getMenu().findItem(R.id.action_delete).setVisible(true);

                menu.show();
            }

        });
    }

    @Override
    public int getItemCount() {
        return (comments != null ? comments.size() : 0);
    }
}


