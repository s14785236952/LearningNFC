package com.example.user.learningnfc;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StudentsWrongExamsActivity extends ListActivity {
    // Progress Dialog
    private ProgressDialog pDialog;

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    ArrayList<HashMap<String, String>> wrongExamsList;

    // url to get all products list
    private static String url_student_wrong_exams = "http://163.21.245.192/android_connect/get_students_wrong_exams.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";

    private  String[] results;
    private String wrongExamsScore;


    // products JSONArray
    JSONArray wrong_exams = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students_wrong_exams);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        // Hashmap for ListView
        wrongExamsList = new ArrayList<HashMap<String, String>>();

        // Loading products in Background Thread
        new LoadAllwrong().execute();

        // Get listview
        ListView lv = getListView();

        // on seleting single product
        // launching Edit Product Screen
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // getting values from selected ListItem
                String exam_id = ((TextView) view.findViewById(R.id.pid)).getText()
                        .toString();

                // Starting new intent
                Intent in = new Intent(getApplicationContext(),
                        WhatStudentsWrongActivity.class);
                // sending pid to next activity
                in.putExtra("exam_id", exam_id);

                // starting new activity and expecting some response back
                startActivityForResult(in, 101);
            }
        });

    }

    // Response from Edit Product Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if result code 100
        if (resultCode == 101) {
            // if result code 100 is received
            // means user edited/deleted product
            // reload this screen again
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }

    }

    /**
     * Background Async Task to Load all product by making HTTP Request
     * */
    class LoadAllwrong extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(StudentsWrongExamsActivity.this);
            pDialog.setMessage("請稍候...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting All products from url
         * */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_student_wrong_exams, "GET", params);

            // Check your log cat for JSON reponse
            Log.d("All wrongExams: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    wrong_exams = json.getJSONArray("exams");

                    // looping through All Products
                    for (int i = 0; i < wrong_exams.length(); i++) {
                        JSONObject c = wrong_exams.getJSONObject(i);

                        // Storing each json item in variable
                        String id = c.getString("exam_id");
                        String wrongExams = c.getString("wrongExams");

                        Log.d("wrongExams" , wrongExams);
                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        map.put("exam_id", id);


                        String[] exam_items = wrongExams.split(",");
                        // items.length 是所有項目的個數
                        results = new String[exam_items.length];
                        // 將結果放入 results，
                        // 並利用 Integer.parseInt 來將整數字串轉換為 int
                        for (int k = 0; k < exam_items.length; k++) {
                            results[k] = exam_items[k].trim();
                            Log.d("exam_items" , ""+results[k]);
                        }
                        map.put("wrongExams", results[0]+" : "+results[exam_items.length-1]+"分");
                        Log.d("result" , ""+results[0]);
                        // adding HashList to ArrayList
                        wrongExamsList.add(map);

                    }
                } else {
                    // no products found
                    // Launch Add New product Activity
                    Intent i = new Intent(getApplicationContext(),
                            NewProductActivity.class);
                    // Closing all previous activities
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                    ListAdapter adapter = new SimpleAdapter(
                            StudentsWrongExamsActivity.this, wrongExamsList,
                            R.layout.list_item, new String[] { "exam_id",
                            "wrongExams"},
                            new int[] { R.id.pid, R.id.name });
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

}

