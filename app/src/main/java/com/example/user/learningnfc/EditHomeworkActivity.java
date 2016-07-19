package com.example.user.learningnfc;

import java.util.ArrayList;
import java.util.Calendar;
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

public class EditHomeworkActivity extends Activity {

    EditText txtName_hw;
    EditText txtdesc_hw;
    EditText inputName_hw;
    EditText inputdesc_hw;
    TextView actionText;
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

    String pid;

    // Progress Dialog
    private ProgressDialog pDialog;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();
    // single product url
    private static final String url_homework_details = "http://163.21.245.192/android_connect/get_homework_details.php";

    // url to update product
    private static final String url_update_homework = "http://163.21.245.192/android_connect/update_homework.php";

    // url to delete product
    private static final String url_delete_homework = "http://163.21.245.192/android_connect/delete_homework.php";

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
        setContentView(R.layout.activity_edit_homework);

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
        spinner = (Spinner)findViewById(R.id.inputaction_hw);
        actionList = new ArrayAdapter<>(EditHomeworkActivity.this, android.R.layout.simple_spinner_item, action);
        spinner.setAdapter(actionList);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,int position, long arg3) {
                action_needed = action[position];
                actionText.setText("需要的動作總共有"+action_needed+"項");
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });



        // getting product details from intent
        Intent i = getIntent();

        // getting product id (pid) from intent
        pid = i.getStringExtra(TAG_PID);
        inputName_hw = (EditText) findViewById(R.id.inputName_hw);
        inputdesc_hw = (EditText)findViewById(R.id.inputdesc_hw);

        // Getting complete product details in background thread
        new GetProductDetails().execute();

        // save button click event
        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                String name = inputName_hw.getText().toString();
                String desc = inputdesc_hw.getText().toString();
                String schedule_hw = schedule;
                String action_hw = action_needed;
                // starting background task to update product
                new SaveProductDetails().execute(name,desc,schedule_hw,action_hw);
            }
        });

        // Delete button click event
        btnDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // deleting product in background thread
                new DeleteProduct().execute();
            }
        });

        dateButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                showDialog(0);
                datePickerDialog.updateDate(mYear, mMonth, mDay);
            }

        });

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
            pDialog = new ProgressDialog(EditHomeworkActivity.this);
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

                        // check your log for json response
                        Log.d("Single Homework Details", json.toString());

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
                            txtName_hw = (EditText) findViewById(R.id.inputName_hw);
                            txtdesc_hw = (EditText)findViewById(R.id.inputdesc_hw);
                            dateText = (TextView)findViewById(R.id.dateText);
                            actionText = (TextView)findViewById(R.id.actionText);


                            // display product data in EditText
                            txtName_hw.setText(product.getString(TAG_NAME));
                            txtdesc_hw.setText(product.getString(TAG_DESC));
                            dateText.setText(product.getString(TAG_SCHEDULE));
                            actionText.setText(product.getString(TAG_ACTION));



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

    /**
     * Background Async Task to  Save product Details
     * */
    class SaveProductDetails extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EditHomeworkActivity.this);
            pDialog.setMessage("請稍候...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Saving product
         * */
        protected String doInBackground(String... args) {

            // getting updated data from EditTexts
            String name = args[0],
                    desc = args[1],
                    schedule_hw = args[2],
                    action = args[3];

            Log.d("name desc",name+" and "+ desc+" and " +schedule_hw+" and "+action);

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(TAG_PID, pid));
            params.add(new BasicNameValuePair(TAG_NAME, name));
            params.add(new BasicNameValuePair(TAG_DESC, desc));
            params.add(new BasicNameValuePair(TAG_SCHEDULE, schedule_hw));
            params.add(new BasicNameValuePair(TAG_ACTION, action));

            // sending modified data through http request
            // Notice that update product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_update_homework,
                    "POST", params);
            Log.d("Update Homework", json.toString());
            // check json success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully updated
                    Intent i = getIntent();
                    // send result code 100 to notify about product update
                    setResult(101, i);
                    finish();
                } else {
                    // failed to update product
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
            // dismiss the dialog once product uupdated
            pDialog.dismiss();
        }
    }

    /*****************************************************************
     * Background Async Task to Delete Product
     * */
    class DeleteProduct extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EditHomeworkActivity.this);
            pDialog.setMessage("請稍候...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Deleting product
         * */
        protected String doInBackground(String... args) {

            // Check for success tag
            int success;
            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("pid", pid));

                // getting product details by making HTTP request
                JSONObject json = jsonParser.makeHttpRequest(
                        url_delete_homework, "POST", params);

                // check your log for json response
                Log.d("Delete Product", json.toString());

                // json success tag
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    // product successfully deleted
                    // notify previous activity by sending code 100
                    Intent i = getIntent();
                    // send result code 100 to notify about product deletion
                    setResult(101, i);
                    finish();
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
            // dismiss the dialog once product deleted
            pDialog.dismiss();

        }

    }


    @Override
    protected Dialog onCreateDialog(int id) {
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month,
                                  int day) {
                mYear = year;
                mMonth = month;
                mDay = day;
                dateText.setText("您設定的截止日期為"+setDateFormat(year,month,day));
                schedule = Integer.toString(year)+"/"+Integer.toString(month+1)+"/"+Integer.toString(day);
                Log.d("schedule",schedule);
            }

        }, mYear,mMonth, mDay);

        return datePickerDialog;
    }

    private String setDateFormat(int year,int monthOfYear,int dayOfMonth){
        return String.valueOf(year) + " / "
                + String.valueOf(monthOfYear + 1) + " / "
                + String.valueOf(dayOfMonth);
    }

}