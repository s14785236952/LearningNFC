package com.example.user.learningnfc;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.facebook.Profile;

public class ResultForHomeworkActivity extends Activity {

    TextView txtName_hw;
    TextView txtdesc_hw;
    TextView inputName_hw;
    TextView inputdesc_hw;
    TextView actionText;
    TextView action_completed;
    private Spinner spinner;
    private ArrayAdapter<String> actionList;
    private Context mContext;
    private String[] action  = {"0","1","2","3","4","5","6","7","8","9","10"};
    public static String action_needed;

    Button btnSave;
    Button btnDelete;

    private Button dateButton;
    private Calendar calendar;
    private int mYear, mMonth, mDay;
    private TextView dateText;
    private DatePickerDialog datePickerDialog;
    private static String schedule;
    int pidInHomeworks;
    String pid;

    // Progress Dialog
    private ProgressDialog pDialog;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();
    JSONArray students = null;
    // single product url
    private static final String url_homework_details = "http://163.21.245.192/android_connect/get_homework_details.php";
    private static String url_all_students = "http://163.21.245.192/android_connect/get_all_students.php";
    private static String url_create_student = "http://163.21.245.192/android_connect/create_student.php";
    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_HOMEWORK = "homework";
    private static final String TAG_PID = "pid";
    private static final String TAG_NAME = "name";
    private static final String TAG_SCHEDULE = "schedule";
    private static final String TAG_DESC = "description";
    private static final String TAG_ACTION = "action_needed";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_for_homework);

        calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);

        // save button
        btnSave = (Button) findViewById(R.id.btnSave_hw);
        btnDelete = (Button) findViewById(R.id.btnDelete_hw);

        dateText = (TextView)findViewById(R.id.dateText);
        dateButton = (Button)findViewById(R.id.dateButton);
        actionText = (TextView)findViewById(R.id.actionText);
        //spinner
        mContext = this.getApplicationContext();




        // getting product details from intent
        Intent i = getIntent();

        // getting product id (pid) from intent
        pid = i.getStringExtra(TAG_PID);
        inputName_hw = (TextView) findViewById(R.id.inputName_hw);
        inputdesc_hw = (TextView)findViewById(R.id.inputdesc_hw);

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
            pDialog = new ProgressDialog(ResultForHomeworkActivity.this);
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
                        params.add(new BasicNameValuePair("pid", pid));

                        // getting product details by making HTTP request
                        // Note that product details url will use GET request
                        JSONObject json = jsonParser.makeHttpRequest(
                                url_homework_details, "GET", params);

                        // Building Parameters
                        List<NameValuePair> params2 = new ArrayList<NameValuePair>();
                        // getting JSON string from URL
                        JSONObject json2 = jsonParser.makeHttpRequest(url_all_students, "GET", params2);

                        // check your log for json response
                        Log.d("Single student Details", json.toString());

                        // json success tag
                        success = json.getInt(TAG_SUCCESS);
                        if (success == 1) {
                            // successfully received product details
                            JSONArray productObj = json
                                    .getJSONArray(TAG_HOMEWORK); // JSON Array

                            // get first product object from JSON Array
                            JSONObject product = productObj.getJSONObject(0);

                            // product with this pid found
                            // Edit Text
                            txtName_hw = (TextView) findViewById(R.id.inputName_hw);
                            txtdesc_hw = (TextView)findViewById(R.id.inputdesc_hw);
                            dateText = (TextView)findViewById(R.id.dateText);
                            actionText = (TextView)findViewById(R.id.actionText);


                            // display product data in EditText
                            txtName_hw.setText(product.getString(TAG_NAME));
                            txtdesc_hw.setText(product.getString(TAG_DESC));
                            dateText.setText(product.getString(TAG_SCHEDULE));
                            actionText.setText(product.getString(TAG_ACTION));
                            pidInHomeworks =product.getInt("pid");

                            students = json2.getJSONArray("students");

                            for (int i = 0; i < students.length(); i++) {
                                JSONObject c = students.getJSONObject(i);

                                // Storing each json item in variable
                                int pidInStudents = c.getInt("homework_pid");
                                String student = c.getString("student");
                                Log.d("hello",pidInHomeworks+"   "+pidInStudents+student+"   "+Profile.getCurrentProfile().getLastName()
                                        +Profile.getCurrentProfile().getFirstName());

                                if (pidInHomeworks == pidInStudents && student.equals(Profile.getCurrentProfile().getLastName()
                                        +Profile.getCurrentProfile().getFirstName())){
                                    action_completed = (TextView)findViewById(R.id.actionText_completed);
                                    action_completed.setText(c.getString("action_completed"));
                                    break;
                                }else if (students.length() == i+1) {
                                    List<NameValuePair> params1 = new ArrayList<NameValuePair>();
                                    params1.add(new BasicNameValuePair("homework_pid", String.valueOf(pidInHomeworks)));
                                    params1.add(new BasicNameValuePair("student", Profile.getCurrentProfile().getLastName()
                                            +Profile.getCurrentProfile().getFirstName()));
                                    params1.add(new BasicNameValuePair("action_completed",String.valueOf(0) ));
                                    jsonParser.makeHttpRequest(url_create_student, "POST", params1);
                                    action_completed = (TextView)findViewById(R.id.actionText_completed);
                                    action_completed.setText(String.valueOf(0));
                                    break;
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
        }
    }

}