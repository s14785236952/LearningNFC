package com.example.user.learningnfc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.facebook.Profile;

public class FinishedworkActivity extends ListActivity {

    // Progress Dialog
    private ProgressDialog pDialog;

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    ArrayList<HashMap<String, String>> homeworksList;


    private static String url_all_students = "http://163.21.245.192/android_connect/get_all_students.php";
    private static String url_all_homeworks = "http://163.21.245.192/android_connect/get_all_homeworks.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_HOMEWORKS = "homeworks";
    private static final String TAG_PID = "pid";
    private static final String TAG_NAME = "name";
    private static final String TAG_DESC = "description";
    private static final String TAG_SCHEDULE = "schedule";


    // products JSONArray
    JSONArray homeworks,students = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_homeworks);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        // Hashmap for ListView
        homeworksList = new ArrayList<HashMap<String, String>>();

        // Loading products in Background Thread
        new LoadAllHomeworks().execute();

        // Get listview
        ListView lv = getListView();

        // on seleting single product
        // launching Edit Product Screen
        lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // getting values from selected ListItem
                String pid = ((TextView) view.findViewById(R.id.pid)).getText()
                        .toString();

                // Starting new intent
                Intent in = new Intent(getApplicationContext(),
                        ResultForHomeworkActivity.class);
                // sending pid to next activity
                in.putExtra(TAG_PID, pid);

                // starting new activity and expecting some response back
                startActivityForResult(in, 101);
            }
        });

    }


    /**
     * Background Async Task to Load all product by making HTTP Request
     * */
    class LoadAllHomeworks extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(FinishedworkActivity.this);
            pDialog.setMessage("Loading exams. Please wait...");
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
            JSONObject json = jParser.makeHttpRequest(url_all_homeworks, "GET", params);

            // Building Parameters
            List<NameValuePair> params_student = new ArrayList<NameValuePair>();
            // getting JSON string from URL
            JSONObject json_student = jParser.makeHttpRequest(url_all_students, "GET", params_student);

            // Check your log cat for JSON reponse
            Log.d("All Homeworks: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    homeworks = json.getJSONArray(TAG_HOMEWORKS);

                    // looping through All Products
                    for (int i = 0; i < homeworks.length(); i++) {
                        JSONObject c = homeworks.getJSONObject(i);

                        // Storing each json item in variable
                        String id = c.getString(TAG_PID);
                        String name = c.getString(TAG_NAME);
                        String action_needed = c.getString("action_needed");


                        students = json_student.getJSONArray("students");
                        for (int k = 0; k < students.length(); k++) {
                            JSONObject c2 = students.getJSONObject(k);
                            String homework_pid = c2.getString("homework_pid");
                            String action_completed = c2.getString("action_completed");
                            String student = c2.getString("student");
                            Log.d("id: ",id+" : "+homework_pid);
                            Log.d("student: ",student+" : "+Profile.getCurrentProfile().getLastName()
                                    + Profile.getCurrentProfile().getFirstName());
                            Log.d("count: ",action_completed+" : "+action_needed);
                            if (homework_pid.equals(id) && student.equals(Profile.getCurrentProfile().getLastName()
                                    + Profile.getCurrentProfile().getFirstName()) && action_completed.equals(action_needed)) {
                                // creating new HashMap
                                HashMap<String, String> map = new HashMap<String, String>();

                                // adding each child node to HashMap key => value
                                map.put(TAG_PID, id);
                                map.put(TAG_NAME, name);

                                // adding HashList to ArrayList
                                homeworksList.add(map);
                                break;
                            }else if (students.length() == k+1){
                                break;
                            }
                        }


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
                            FinishedworkActivity.this, homeworksList,
                            R.layout.list_item, new String[] { TAG_PID,
                            TAG_NAME},
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Intent myIntent = new Intent();
            myIntent = new Intent(FinishedworkActivity.this, HomeworkActivity.class);
            startActivity(myIntent);
            this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}