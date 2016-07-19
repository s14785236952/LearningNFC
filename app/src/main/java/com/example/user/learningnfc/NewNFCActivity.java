package com.example.user.learningnfc;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
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

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class NewNFCActivity extends Activity {

    // Progress Dialog
    private ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();
    EditText inputName_nfc;
    EditText inputdesc_nfc;
    EditText inputtag;


    // url to create new product
    private static String url_create_learning = "http://163.21.245.192/android_connect/create_learning.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_nfc);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        // Edit Text
        inputName_nfc = (EditText) findViewById(R.id.inputName_nfc);
        inputdesc_nfc = (EditText) findViewById(R.id.inputdesc_nfc);
        inputtag = (EditText)findViewById(R.id.inputtag);
        // Create button
        Button btnCreateNFC = (Button) findViewById(R.id.btnCreateNFC);

        // button click event
        btnCreateNFC.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String name = inputName_nfc.getText().toString();
                String desc = inputdesc_nfc.getText().toString();
                String tag = inputtag.getText().toString();
                // creating new product in background thread
                new CreateNewProduct().execute(name, desc, tag);
            }
        });
    }

    /**
     * Background Async Task to Create new product
     */
    class CreateNewProduct extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(NewNFCActivity.this);
            pDialog.setMessage("新增學習內容..");
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
         */
        protected String doInBackground(String... args) {
            String name = args[0],
                    desc = args[1],
                    tag = args[2];

            // Building Parameters
            List<NameValuePair> params1 = new ArrayList<NameValuePair>();
            params1.add(new BasicNameValuePair("name", name));
            params1.add(new BasicNameValuePair("description", desc));
            params1.add(new BasicNameValuePair("tag", tag));
            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_create_learning,
                    "POST", params1);
            // check log cat fro response

            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully created product
                    Intent i = new Intent(getApplicationContext(), AllNFCActivity.class);
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
         **/
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

}