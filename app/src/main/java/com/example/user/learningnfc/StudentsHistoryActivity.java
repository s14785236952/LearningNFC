package com.example.user.learningnfc;

import android.app.ListActivity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StudentsHistoryActivity extends AppCompatActivity {
Button btnViewWrongExams;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students_history);
        btnViewWrongExams = (Button)findViewById(R.id.btnViewWrongExams);
        btnViewWrongExams.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent i = new Intent();
                i.setClass(StudentsHistoryActivity.this , StudentsWrongExamsActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
}
