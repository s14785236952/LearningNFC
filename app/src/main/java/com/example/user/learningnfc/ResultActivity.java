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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ResultActivity extends ListActivity {

    // Progress Dialog
    private ProgressDialog pDialog;

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    ArrayList<HashMap<String, String>> productsList;

    TextView inputscore;
    private static String url_all_products = "http://163.21.245.192/android_connect/get_all_products.php";

    private static final String url_product_details = "http://163.21.245.192/android_connect/get_product_details.php";
    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCT = "product";
    private static final String TAG_PID = "pid";
    private static final String TAG_NAME = "name";

    // products JSONArray
    JSONArray products = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        inputscore = (TextView)findViewById(R.id.inputScore);
        inputscore.setText(Integer.toString(ExamGoActivity.exam_score));

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        productsList = new ArrayList<HashMap<String, String>>();

        new LoadStudentWrongExam().execute();

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
                        ResultForAnswerActivity.class);
                // sending pid to next activity
                in.putExtra(TAG_PID, pid);
                startActivity(in);
                finish();
            }
        });

    }

    /**
     * Background Async Task to Load all product by making HTTP Request
     * */
    class LoadStudentWrongExam extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ResultActivity.this);
            pDialog.setMessage("Loading Wrong Exams. Please wait...");
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
            Log.d("wrong_number", Integer.toString(ExamGoActivity.wrong_exams_number));
            if(ExamGoActivity.wrong_exams_number != 0 ){
                for (int i = 2 ; i <= ExamGoActivity.wrong_exams_number*2 + 1 ; i+=2){
                    params.add(new BasicNameValuePair("pid", ExamGoActivity.mylist.get(i)));
                    Log.d("mylist" , ExamGoActivity.mylist.get(i));
                    JSONObject json = jParser.makeHttpRequest(url_product_details, "GET", params);
                    // Check your log cat for JSON reponse
                    Log.d("錯的題目： ", json.toString());

                    try {
                        // Checking for SUCCESS TAG
                        int success = json.getInt(TAG_SUCCESS);
                        if (success == 1) {
                            // products found
                            // Getting Array of Products
                            products = json.getJSONArray(TAG_PRODUCT);

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
                        } else {
                            // no products found
                            // Launch Add New product Activity
                            Intent intent = new Intent(getApplicationContext(),
                                    ExamActivity.class);
                            // Closing all previous activities
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
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
                            ResultActivity.this, productsList,
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
            myIntent = new Intent(ResultActivity.this, ExamActivity.class);
            startActivity(myIntent);
            this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
