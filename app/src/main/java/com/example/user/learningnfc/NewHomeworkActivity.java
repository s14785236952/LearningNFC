package com.example.user.learningnfc;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
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
import android.widget.Toast;

public class NewHomeworkActivity extends Activity {

    // Progress Dialog
    private ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();
    EditText inputName_hw;
    EditText inputdesc_hw;
    TextView actionText;
    private Spinner spinner;
    private ArrayAdapter<String> actionList;
    private Context mContext;
    private String[] action  = {"0","1","2","3","4","5","6","7","8","9","10"};
    public static String action_needed;


    private static String schedule;

    private Button dateButton;
    private Calendar calendar;
    private int mYear, mMonth, mDay;
    private TextView dateText;
    private DatePickerDialog datePickerDialog;

    // url to create new product
    private static String url_create_homework = "http://163.21.245.192/android_connect/create_homework.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_homework);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        // Edit Text
        inputName_hw = (EditText) findViewById(R.id.inputName_hw);
        inputdesc_hw = (EditText) findViewById(R.id.inputdesc_hw);
        dateText = (TextView)findViewById(R.id.dateText);
        actionText = (TextView)findViewById(R.id.actionText);

        //spinner
        mContext = this.getApplicationContext();
        spinner = (Spinner)findViewById(R.id.inputaction_hw);
        actionList = new ArrayAdapter<>(NewHomeworkActivity.this, android.R.layout.simple_spinner_item, action);
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



        //select date
        calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);

        dateButton = (Button)findViewById(R.id.btn_date);
        //select date's Button
        dateButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                showDialog(0);
                datePickerDialog.updateDate(mYear, mMonth, mDay);
            }

        });


        // Create button
        Button btnCreateHomework = (Button) findViewById(R.id.btnCreateHomework);

        // button click event
        btnCreateHomework.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String name = inputName_hw.getText().toString();
                String desc = inputdesc_hw.getText().toString();
                String date = schedule;
                // creating new product in background thread
                new CreateNewProduct().execute(name,desc,date);
            }
        });
    }

    /**
     * Background Async Task to Create new product
     * */
    class CreateNewProduct extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(NewHomeworkActivity.this);
            pDialog.setMessage("Creating Exam..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
        @Override
        protected void onProgressUpdate(String... values) {
            Toast.makeText(getApplicationContext(), values[0], Toast.LENGTH_SHORT).show();
        }
        /**
         * Creating product
         * */
        protected String doInBackground(String... args) {
            String name = args[0],
                    desc = args[1],
                    date = args[2];

            // Building Parameters
            List<NameValuePair> params1 = new ArrayList<NameValuePair>();
            params1.add(new BasicNameValuePair("name", name));
            params1.add(new BasicNameValuePair("description", desc));
            params1.add(new BasicNameValuePair("schedule",date ));
            params1.add(new BasicNameValuePair("action_needed" , action_needed));
            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_create_homework,
                    "POST", params1);
            // check log cat fro response

            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully created product
                    Intent i = new Intent(getApplicationContext(), AllHomeworksActivity.class);
                    startActivity(i);

                    // closing this screen
                    finish();
                } else {
                    // failed to create product
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
            // dismiss the dialog once done
            pDialog.dismiss();
        }

    }
    @Override
    protected void onDestroy() {
        // pDialog.dismiss();
        super.onDestroy();
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


