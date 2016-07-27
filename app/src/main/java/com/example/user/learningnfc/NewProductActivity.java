package com.example.user.learningnfc;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
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
import android.widget.Toast;

public class NewProductActivity extends Activity {

    // Progress Dialog
    private ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();
    EditText inputName;
    EditText inputanswer;
    EditText inputopa;
    EditText inputopb;
    EditText inputopc;
    EditText inputopd;
    EditText inputdesc;
    EditText inputsuggest;
    TextView actionText;

    private Spinner spinner;
    private ArrayAdapter<String> actionList;
    private Context mContext;
    private String[] suggest  = {"主機板","CPU","音效","記憶體","電源","顯示卡","硬碟","網路","輸入裝置","輸出裝置","機殼"};
    public static String species;
    // url to create new product
    private static String url_create_product = "http://163.21.245.192/android_connect/create_product.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_product);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        actionText = (TextView)findViewById(R.id.actionText_exam);
        spinner = (Spinner)findViewById(R.id.inputaction_exam);
        actionList = new ArrayAdapter<>(NewProductActivity.this, android.R.layout.simple_spinner_item, suggest);
        spinner.setAdapter(actionList);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,int position, long arg3) {
                species = suggest[position];
                actionText.setText("你選的類別是："+species);

            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        // Edit Text
        inputName = (EditText) findViewById(R.id.inputName);
        inputanswer = (EditText) findViewById(R.id.inputanswer);
        inputopa = (EditText) findViewById(R.id.inputopa);
        inputopb = (EditText) findViewById(R.id.inputopb);
        inputopc = (EditText) findViewById(R.id.inputopc);
        inputopd = (EditText) findViewById(R.id.inputopd);
        inputdesc = (EditText) findViewById(R.id.inputdesc);

        // Create button
        Button btnCreateProduct = (Button) findViewById(R.id.btnCreateProduct);

        // button click event
        btnCreateProduct.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String name = inputName.getText().toString();
                String answer = inputanswer.getText().toString();
                String optiona = inputopa.getText().toString();
                String optionb = inputopb.getText().toString();
                String optionc = inputopc.getText().toString();
                String optiond = inputopd.getText().toString();
                String desc = inputdesc.getText().toString();
                // creating new product in background thread
                new CreateNewProduct().execute(name,answer,optiona,optionb,optionc,optiond,desc);
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
            pDialog = new ProgressDialog(NewProductActivity.this);
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
                    answer = args[1],
                    optiona = args[2],
                    optionb = args[3],
                    optionc = args[4],
                    optiond = args[5],
                    desc    = args[6];


            // Building Parameters
            List<NameValuePair> params1 = new ArrayList<NameValuePair>();
            params1.add(new BasicNameValuePair("name", name));
            params1.add(new BasicNameValuePair("answer", answer));
            params1.add(new BasicNameValuePair("optiona", optiona));
            params1.add(new BasicNameValuePair("optionb", optionb));
            params1.add(new BasicNameValuePair("optionc", optionc));
            params1.add(new BasicNameValuePair("optiond", optiond));
            params1.add(new BasicNameValuePair("description", desc));
            params1.add(new BasicNameValuePair("suggest", species));

            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_create_product,
                    "POST", params1);
            // check log cat fro response

            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully created product
                    Intent i = new Intent(getApplicationContext(), AllExamsActivity.class);
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


}


