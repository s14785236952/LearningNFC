package com.example.user.learningnfc;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class EditNFCActivity extends AppCompatActivity {
    TextView learn_tag,learn_name,learn_desc;
    ImageView img;
    private String image;
    // Progress Dialog
    private ProgressDialog pDialog;
    String pid;
    Button btnSave;
    Button btnDelete;
    EditText inputName_nfc;
    EditText inputdesc_nfc;
    EditText inputtag_nfc;
    EditText txtName_nfc;
    EditText txtdesc_nfc;
    EditText txttag_nfc;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();
    // single product url
    private static final String url_NFC_details = "http://163.21.245.192/android_connect/get_learning_details.php";

    // url to update product
    private static final String url_update_NFC = "http://163.21.245.192/android_connect/update_learning.php";

    // url to delete product
    private static final String url_delete_learning = "http://163.21.245.192/android_connect/delete_learning.php";

    private static final String url_learning_details = "http://163.21.245.192/android_connect/get_learning_details.php";


    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_NFC = "learning";
    private static final String TAG_PID = "learn_id";
    private static final String TAG_NAME = "name";
    private static final String TAG_DESC = "description";
    private static final String TAG_TAG = "tag";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_nfc);

        // save button
        btnSave = (Button) findViewById(R.id.btnSave_nfc);
        btnDelete = (Button) findViewById(R.id.btnDelete_nfc);

        // getting product details from intent
        Intent i = getIntent();

        // getting product id (pid) from intent
        pid = i.getStringExtra("learn_id");
        Log.d("learn_id",pid);

        inputName_nfc = (EditText) findViewById(R.id.inputName_nfc);
        inputdesc_nfc = (EditText)findViewById(R.id.inputdesc_nfc);
        inputtag_nfc = (EditText)findViewById(R.id.inputtag_nfc);

        // Getting complete product details in background thread
        new GetProductDetails().execute();

        // save button click event
        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String name = inputName_nfc.getText().toString();
                String desc = inputdesc_nfc.getText().toString();
                String tag = inputtag_nfc.getText().toString();

                Log.d("update details" , name +" and "+desc+" and "+tag);
                // starting background task to update product
                new SaveProductDetails().execute(name,desc,tag);
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

    class GetProductDetails extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EditNFCActivity.this);
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
                        params.add(new BasicNameValuePair("learn_id", pid));

                        // getting product details by making HTTP request
                        // Note that product details url will use GET request
                        JSONObject json = jsonParser.makeHttpRequest(
                                url_NFC_details, "GET", params);

                        // check your log for json response
                        Log.d("Single Homework Details", json.toString());

                        // json success tag
                        success = json.getInt(TAG_SUCCESS);
                        if (success == 1) {
                            // successfully received product details
                            JSONArray productObj = json
                                    .getJSONArray(TAG_NFC); // JSON Array

                            // get first product object from JSON Array
                            JSONObject product = productObj.getJSONObject(0);

                            // product with this pid found
                            // Edit Text
                            txtName_nfc = (EditText) findViewById(R.id.inputName_nfc);
                            txtdesc_nfc = (EditText)findViewById(R.id.inputdesc_nfc);
                            txttag_nfc = (EditText)findViewById(R.id.inputtag_nfc);


                            // display product data in EditText
                            txtName_nfc.setText(product.getString(TAG_NAME));
                            txtdesc_nfc.setText(product.getString(TAG_DESC));
                            txttag_nfc.setText(product.getString(TAG_TAG));


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
            pDialog = new ProgressDialog(EditNFCActivity.this);
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
                    tag = args[2];


            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(TAG_PID, pid));
            params.add(new BasicNameValuePair(TAG_NAME, name));
            params.add(new BasicNameValuePair(TAG_DESC, desc));
            params.add(new BasicNameValuePair(TAG_TAG, tag));

            // sending modified data through http request
            // Notice that update product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_update_NFC,
                    "POST", params);
            Log.d("Update Homework", json.toString());
            // check json success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    Intent i = new Intent();
                    i.setClass(EditNFCActivity.this , AllNFCActivity.class);
                    startActivity(i);
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
            pDialog = new ProgressDialog(EditNFCActivity.this);
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
                params.add(new BasicNameValuePair("learn_id", pid));

                // getting product details by making HTTP request
                JSONObject json = jsonParser.makeHttpRequest(
                        url_delete_learning, "POST", params);

                // check your log for json response
                Log.d("Delete Product", json.toString());

                // json success tag
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    // product successfully deleted
                    // notify previous activity by sending code 100
                    Intent i = new Intent();
                    i.setClass(EditNFCActivity.this , AllNFCActivity.class);
                    startActivity(i);
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

    class GetLearnDetails extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EditNFCActivity.this);
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
                        params.add(new BasicNameValuePair("learn_id", pid));

                        // getting product details by making HTTP request
                        // Note that product details url will use GET request
                        JSONObject json = jsonParser.makeHttpRequest(
                                url_learning_details, "GET", params);

                        // check your log for json response
                        Log.d("Single Leanring Details", json.toString());

                        // json success tag
                        success = json.getInt("success");
                        if (success == 1) {
                            // successfully received product details
                            JSONArray productObj = json
                                    .getJSONArray("learning"); // JSON Array

                            // get first product object from JSON Array
                            JSONObject product = productObj.getJSONObject(0);
                            learn_name = (TextView)findViewById(R.id.learn_name);
                            learn_tag = (TextView)findViewById(R.id.learn_tag);
                            learn_desc = (TextView)findViewById(R.id.learn_desc);
                            learn_tag.setText(product.getString("tag"));
                            learn_name.setText(product.getString("name"));
                            learn_desc.setText(product.getString("description"));

                            image = product.getString("image");
                            img = (ImageView)findViewById(R.id.img_learn);
                            img.setImageBitmap(ImgUtils.getBitmapFromURL("http://163.21.245.192/android_connect/uploadedimages/"+image));

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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub

        if (keyCode == KeyEvent.KEYCODE_BACK) { // 攔截返回鍵
            new android.support.v7.app.AlertDialog.Builder(EditNFCActivity.this)
                    .setTitle("確認視窗")
                    .setMessage("確定要結束應用程式嗎?")
                    .setIcon(R.mipmap.ic_launcher)
                    .setPositiveButton("確定",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    finish();
                                }
                            })
                    .setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    // TODO Auto-generated method stub

                                }
                            }).show();
        }
        return true;
    }
}
