package com.example.user.learningnfc;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class AllNFCtoEditNFCActivity extends AppCompatActivity {
    String pid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_nfcto_edit_nfc);
        Button btnEditImage = (Button)findViewById(R.id.btnEditImage);
        Button btnEditNFC =(Button)findViewById(R.id.btnEditNFC);

        Intent i = getIntent();

        pid = i.getStringExtra("learn_id");

        btnEditImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Starting new intent
                Intent in = new Intent(getApplicationContext(),
                        NewImageActivity.class);
                // sending pid to next activity
                in.putExtra("learn_id", pid);
                Log.d("learn_id" ,pid);
                startActivityForResult(in, 100);
            }
        });

        btnEditNFC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Starting new intent
                Intent in = new Intent(getApplicationContext(),
                        EditNFCActivity.class);
                // sending pid to next activity
                in.putExtra("learn_id", pid);
                startActivityForResult(in, 100);
            }
        });

    }

    // Response from Edit Product Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if result code 100
        if (resultCode == 100) {
            // if result code 100 is received
            // means user edited/deleted product
            // reload this screen again
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
    }
}
