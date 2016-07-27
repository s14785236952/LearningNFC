package com.example.user.learningnfc;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.view.KeyEvent;
import android.widget.TextView;

public class WhatStudentsWrongActivity extends ListActivity {
    // Progress Dialog
    private ProgressDialog pDialog;
    private  String[] results;
    // products JSONArray
    JSONArray wrong_exams = null;
    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    ArrayList<HashMap<String, String>> wrongExamsList;

    // url to get all products list
    private static String url_get_exam_details = "http://163.21.245.192/android_connect/get_wrongExam_details.php";
    private static final String url_product_details = "http://163.21.245.192/android_connect/get_product_details.php";

    private static final String TAG_PRODUCT = "product";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PID = "pid";
    private static final String TAG_NAME = "name";
    ArrayList<HashMap<String, String>> productsList;
    String exam_id;
    // products JSONArray
    JSONArray products = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_what_students_wrong);
        Intent in = getIntent();
        exam_id = in.getStringExtra("exam_id");
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        new GetwrongExam().execute();
        productsList = new ArrayList<HashMap<String, String>>();



        // Get listview
        ListView lv_result = getListView();

        // on seleting single product
        // launching Edit Product Screen
        lv_result.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // getting values from selected ListItem
                String pid = ((TextView) view.findViewById(R.id.pid_result)).getText()
                        .toString();

                // Starting new intent
                Intent in = new Intent(getApplicationContext(),
                        ResultForWrongExamActivity.class);
                // sending pid to next activity
                in.putExtra(TAG_PID, pid);
                in.putExtra("exam_id",exam_id);
                startActivity(in);
                finish();
            }
        });
    }


    class GetwrongExam extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(WhatStudentsWrongActivity.this);
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
                        params.add(new BasicNameValuePair("exam_id", exam_id));
                        // getting product details by making HTTP request
                        // Note that product details url will use GET request
                        JSONObject json = jParser.makeHttpRequest(
                                url_get_exam_details, "GET", params);

                        // check your log for json response

                        // json success tag
                        success = json.getInt(TAG_SUCCESS);
                        if (success == 1) {
                            // successfully received product details
                            JSONArray productObj = json
                                    .getJSONArray("exam"); // JSON Array

                            // get first product object from JSON Array
                            JSONObject product = productObj.getJSONObject(0);

                            String wrongExams = product.getString("wrongExams");

                            String[] exam_items = wrongExams.split(",");
                            // items.length 是所有項目的個數
                            results = new String[exam_items.length];
                            // 將結果放入 results，
                            // 並利用 Integer.parseInt 來將整數字串轉換為 int
                            for (int k = 0; k < exam_items.length; k++) {
                                results[k] = exam_items[k].trim();
                                Log.d("exam_items" , ""+results[k]);
                            }


                            List<NameValuePair> params2 = new ArrayList<NameValuePair>();
                            if(results[results.length-1] != results[1]){
                                for (int i = 1 ; i < Integer.parseInt(results[results.length-2]) ; i++){
                                    params2.add(new BasicNameValuePair("pid", results[i]));
                                    JSONObject json2 = jParser.makeHttpRequest(url_product_details, "GET", params2);
                                        if (success == 1) {
                                            // products found
                                            // Getting Array of Products
                                            products = json2.getJSONArray(TAG_PRODUCT);

                                            // looping through All Products
                                            for (int k = 0; k < products.length(); k++) {
                                                JSONObject c = products.getJSONObject(k);

                                                // Storing each json item in variable
                                                String id = c.getString(TAG_PID);
                                                String name = c.getString(TAG_NAME);

                                                // creating new HashMap
                                                HashMap<String, String> map = new HashMap<String, String>();

                                                // adding each child node to HashMap key => value
                                                map.put(TAG_PID, id);
                                                map.put(TAG_NAME, name);

                                                // adding HashList to ArrayList
                                                productsList.add(map);

                                            }
                                        }
                                    }
                            }


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

            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                    ListAdapter adapter = new SimpleAdapter(
                            WhatStudentsWrongActivity.this, productsList,
                            R.layout.list_item_result, new String[] { TAG_PID,
                            TAG_NAME},
                            new int[] { R.id.pid_result, R.id.name_result });
                    // updating listview
                    setListAdapter(adapter);
                }
            });
        }
    }



    @Override
    protected void onDestroy() {
        pDialog.dismiss();
        super.onDestroy();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Intent myIntent = new Intent();
            myIntent = new Intent(WhatStudentsWrongActivity.this, StudentsWrongExamsActivity.class);
            startActivity(myIntent);
            this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }



}
