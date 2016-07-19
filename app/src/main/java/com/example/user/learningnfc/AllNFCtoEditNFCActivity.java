package com.example.user.learningnfc;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AllNFCtoEditNFCActivity extends AppCompatActivity {
    TextView learn_tag,learn_name,learn_desc;
    ImageView img;
    private String image;
    private ProgressDialog pDialog;
    String pid;

    JSONParser jsonParser = new JSONParser();

    private static final String url_learning_details = "http://163.21.245.192/android_connect/get_learning_details.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_nfcto_edit_nfc);
        Button btnEditImage = (Button) findViewById(R.id.btnEditImage);
        Button btnEditNFC = (Button) findViewById(R.id.btnEditNFC);
        Button btnCommentsBoard = (Button) findViewById(R.id.comments_btn);

        new GetLearnDetails().execute();

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

        btnCommentsBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getApplicationContext(),
                        CommentsActivity.class);
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

    class GetLearnDetails extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AllNFCtoEditNFCActivity.this);
            pDialog.setMessage("請稍候...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Getting product details in background thread
         * */
        protected String doInBackground(String... params) {
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    // Check for success tag
                    int success;
                    try {
                        // Building Parameters
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("learn_id", pid));

                        // getting product details by making HTTP request
                        // Note that product details url will use GET request
                        JSONObject json = jsonParser.makeHttpRequest(
                                url_learning_details, "GET", params);

                        // check your log for json response
                        Log.d("Single Leanring Details", json.toString());

                        // json success tag
                        success = json.getInt("success");
                        if (success == 1) {
                            // successfully received product details
                            JSONArray productObj = json
                                    .getJSONArray("learning"); // JSON Array

                            // get first product object from JSON Array
                            JSONObject product = productObj.getJSONObject(0);
                            learn_name = (TextView)findViewById(R.id.learn_name3);
                            learn_tag = (TextView)findViewById(R.id.learn_tag3);
                            learn_desc = (TextView)findViewById(R.id.learn_desc3);
                            learn_tag.setText(product.getString("tag"));
                            learn_name.setText(product.getString("name"));
                            learn_desc.setText(product.getString("description"));

                            image = product.getString("image");
                            img = (ImageView)findViewById(R.id.img_learn3);
                            img.setImageBitmap(ImgUtils.getBitmapFromURL("http://163.21.245.192/android_connect/uploadedimages/"+image));

                        }else{
                            // product with pid not found
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once got all details
            pDialog.dismiss();
        }
    }
}
