package com.example.user.learningnfc;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ResultForWrongExamActivity extends Activity {

    TextView txtName;
    TextView txtAnswer;
    TextView txtopa;
    TextView txtopb;
    TextView txtopc;
    TextView txtopd;
    TextView txtdesc;
    TextView txtsuggest;
    TextView inputName;
    TextView inputanswer;
    TextView inputopa;
    TextView inputopb;
    TextView inputopc;
    TextView inputopd;
    TextView inputdesc;
    TextView inputsuggest;

    String pid;

    // Progress Dialog
//    test
    private ProgressDialog pDialog;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();
    // single product url
    private static final String url_product_details = "http://163.21.245.192/android_connect/get_product_details.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCT = "product";
    private static final String TAG_PID = "pid";
    private static final String TAG_NAME = "name";
    private static final String TAG_Answer = "answer";
    private static final String TAG_OPTIONA = "optiona";
    private static final String TAG_DESC = "description";
    String exam_id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_for_wrong_exam);

        // getting product details from intent
        Intent i = getIntent();
        exam_id = i.getStringExtra("exam_id");
        // getting product id (pid) from intent
        pid = i.getStringExtra(TAG_PID);
        inputName = (TextView) findViewById(R.id.inputName);
        inputanswer = (TextView) findViewById(R.id.inputanswer);
        inputopa = (TextView) findViewById(R.id.inputopa);
        inputopb = (TextView) findViewById(R.id.inputopb);
        inputopc = (TextView) findViewById(R.id.inputopc);
        inputopd = (TextView) findViewById(R.id.inputopd);
        inputdesc = (TextView)findViewById(R.id.inputdesc);
        inputsuggest = (TextView)findViewById(R.id.inputsuggest);
        // Getting complete product details in background thread
        new GetProductDetails().execute();

    }

    /**
     * Background Async Task to Get complete product details
     * */
    class GetProductDetails extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ResultForWrongExamActivity.this);
            pDialog.setMessage("Loading product details. Please wait...");
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
                        params.add(new BasicNameValuePair("pid", pid));

                        // getting product details by making HTTP request
                        // Note that product details url will use GET request
                        JSONObject json = jsonParser.makeHttpRequest(
                                url_product_details, "GET", params);

                        // check your log for json response
                        Log.d("Single Product Details", json.toString());

                        // json success tag
                        success = json.getInt(TAG_SUCCESS);
                        if (success == 1) {
                            // successfully received product details
                            JSONArray productObj = json
                                    .getJSONArray(TAG_PRODUCT); // JSON Array

                            // get first product object from JSON Array
                            JSONObject product = productObj.getJSONObject(0);

                            // product with this pid found
                            // Edit Text
                            txtName = (TextView) findViewById(R.id.inputName);
                            txtAnswer = (TextView) findViewById(R.id.inputanswer);
                            txtopa = (TextView) findViewById(R.id.inputopa);
                            txtopb = (TextView)findViewById(R.id.inputopb);
                            txtopc = (TextView)findViewById(R.id.inputopc);
                            txtopd = (TextView)findViewById(R.id.inputopd);
                            txtdesc = (TextView)findViewById(R.id.inputdesc);
                            txtsuggest = (TextView)findViewById(R.id.inputsuggest);


                            // display product data in EditText
                            txtName.setText(product.getString(TAG_NAME));
                            txtAnswer.setText(product.getString(TAG_Answer));
                            txtopa.setText(product.getString(TAG_OPTIONA));
                            txtopb.setText(product.getString("optionb"));
                            txtopc.setText(product.getString("optionc"));
                            txtopd.setText(product.getString("optiond"));
                            txtdesc.setText(product.getString(TAG_DESC));
                            txtsuggest.setText(product.getString("suggest"));


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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Intent in = new Intent(getApplicationContext(),
                    WhatStudentsWrongActivity.class);
            in.putExtra("exam_id",exam_id);
            startActivity(in);
            this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }


}