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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class EditExamActivity extends Activity {

    EditText txtName;
    EditText txtAnswer;
    EditText txtopa;
    EditText txtopb;
    EditText txtopc;
    EditText txtopd;
    EditText txtdesc;
    EditText inputName;
    EditText inputanswer;
    EditText inputopa;
    EditText inputopb;
    EditText inputopc;
    EditText inputopd;
    EditText inputdesc;
    TextView actionText,actionText_first;
    Button btnSave;
    Button btnDelete;

     String pid;

    private Spinner spinner;
    private ArrayAdapter<String> actionList;
    private Context mContext;
    private String[] suggest  = {"主機板","CPU","音效","記憶體","電源","顯示卡","硬碟","網路","輸入裝置","輸出裝置","機殼"};
    public static String species;

    // Progress Dialog
    private ProgressDialog pDialog;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();
    // single product url
    private static final String url_product_details = "http://163.21.245.192/android_connect/get_product_details.php";

    // url to update product
    private static final String url_update_product = "http://163.21.245.192/android_connect/update_product.php";

    // url to delete product
    private static final String url_delete_product = "http://163.21.245.192/android_connect/delete_product.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCT = "product";
    private static final String TAG_PID = "pid";
    private static final String TAG_NAME = "name";
    private static final String TAG_Answer = "answer";
    private static final String TAG_OPTIONA = "optiona";
    private static final String TAG_DESC = "description";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_exam);

        // save button
        btnSave = (Button) findViewById(R.id.btnSave);
        btnDelete = (Button) findViewById(R.id.btnDelete);

        actionText = (TextView)findViewById(R.id.actionText_exam);
        spinner = (Spinner)findViewById(R.id.inputaction_exam);
        actionList = new ArrayAdapter<>(EditExamActivity.this, android.R.layout.simple_spinner_item, suggest);
        spinner.setAdapter(actionList);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,int position, long arg3) {
                species = suggest[position];
                actionText.setText("你後來選的類別是："+species);

            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        // getting product details from intent
        Intent i = getIntent();

        // getting product id (pid) from intent
        pid = i.getStringExtra(TAG_PID);
        inputName = (EditText) findViewById(R.id.inputName);
        inputanswer = (EditText) findViewById(R.id.inputanswer);
        inputopa = (EditText) findViewById(R.id.inputopa);
        inputopb = (EditText) findViewById(R.id.inputopb);
        inputopc = (EditText) findViewById(R.id.inputopc);
        inputopd = (EditText) findViewById(R.id.inputopd);
        inputdesc = (EditText)findViewById(R.id.inputdesc);
        // Getting complete product details in background thread
        new GetProductDetails().execute();

        // save button click event
        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                String name = inputName.getText().toString();
                String answer = inputanswer.getText().toString();
                String optiona = inputopa.getText().toString();
                String optionb = inputopb.getText().toString();
                String optionc = inputopc.getText().toString();
                String optiond = inputopd.getText().toString();
                String desc = inputdesc.getText().toString();
                // starting background task to update product
                new SaveProductDetails().execute(name,answer,optiona,optionb,optionc,optiond,desc);
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
            pDialog = new ProgressDialog(EditExamActivity.this);
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
                            txtName = (EditText) findViewById(R.id.inputName);
                            txtAnswer = (EditText) findViewById(R.id.inputanswer);
                            txtopa = (EditText) findViewById(R.id.inputopa);
                            txtopb = (EditText)findViewById(R.id.inputopb);
                            txtopc = (EditText)findViewById(R.id.inputopc);
                            txtopd = (EditText)findViewById(R.id.inputopd);
                            txtdesc = (EditText)findViewById(R.id.inputdesc);
                            actionText_first = (TextView)findViewById(R.id.actionText_examfirst);

                            // display product data in EditText
                            txtName.setText(product.getString(TAG_NAME));
                            txtAnswer.setText(product.getString(TAG_Answer));
                            txtopa.setText(product.getString(TAG_OPTIONA));
                            txtopb.setText(product.getString("optionb"));
                            txtopc.setText(product.getString("optionc"));
                            txtopd.setText(product.getString("optiond"));
                            txtdesc.setText(product.getString(TAG_DESC));
                            actionText_first.setText("你原本選的類別是："+product.getString("suggest"));

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
            pDialog = new ProgressDialog(EditExamActivity.this);
            pDialog.setMessage("Saving product ...");
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
                   answer = args[1],
                   optiona = args[2],
                    optionb = args[3],
                    optionc = args[4],
                    optiond = args[5],
                    desc = args[6];


            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(TAG_PID, pid));
            params.add(new BasicNameValuePair(TAG_NAME, name));
            params.add(new BasicNameValuePair(TAG_Answer, answer));
            params.add(new BasicNameValuePair(TAG_OPTIONA, optiona));
            params.add(new BasicNameValuePair("optionb", optionb));
            params.add(new BasicNameValuePair("optionc", optionc));
            params.add(new BasicNameValuePair("optiond", optiond));
            params.add(new BasicNameValuePair(TAG_DESC, desc));
            params.add(new BasicNameValuePair("suggest", species));

            // sending modified data through http request
            // Notice that update product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_update_product,
                    "POST", params);
            Log.d("Update Product", json.toString());
            // check json success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully updated
                    Intent i = getIntent();
                    // send result code 100 to notify about product update
                    setResult(100, i);
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
            pDialog = new ProgressDialog(EditExamActivity.this);
            pDialog.setMessage("Deleting Product...");
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
                        url_delete_product, "POST", params);

                // check your log for json response
                Log.d("Delete Product", json.toString());

                // json success tag
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    // product successfully deleted
                    // notify previous activity by sending code 100
                    Intent i = getIntent();
                    // send result code 100 to notify about product deletion
                    setResult(100, i);
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
}