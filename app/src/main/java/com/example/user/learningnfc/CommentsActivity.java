package com.example.user.learningnfc;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;

import java.util.List;
import java.util.ArrayList;

public class CommentsActivity extends Activity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private List<Comments> commentList = new ArrayList<>();


    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new commentsAdapter(commentList);
        mRecyclerView.setAdapter(mAdapter);

        Comments comment = new Comments("蔡聖安", "Allen", "1顆星");
        commentList.add(comment);

        comment = new Comments("劉家綾", "Ling", "5顆星");
        commentList.add(comment);

        mAdapter.notifyDataSetChanged();
    }
}

