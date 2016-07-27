package com.example.user.learningnfc;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.Profile;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class LearnInNFCActivity extends AppCompatActivity {
    private static String learn_id;
    String[] results,results_student;
    TextView learn_tag2,learn_name2,learn_desc2;
    Button btnBack2,btnComments;
    ImageView img2;
    private String image;
    public static long learningTime_local ;
    public static String watch_time_local;
    public static String watch_numbers_local;
    public static String descriptionOfStudent="";
    public static String evaluation = "";
    JSONArray students = null;
    JSONArray products = null;

    CharSequence getEnterAcitvityTime,getLeaveAcitvityTime;
    private static final String url_learning_details = "http://163.21.245.192/android_connect/get_learning_details.php";
    private static String url_all_learnings = "http://163.21.245.192/android_connect/get_all_learnings.php";
    private static String url_create_item = "http://163.21.245.192/android_connect/create_item.php";
    private static String url_all_items = "http://163.21.245.192/android_connect/get_all_items.php";
    private static String url_update_item = "http://163.21.245.192/android_connect/update_item.php";
    private static String url_update_student = "http://163.21.245.192/android_connect/update_student.php";
    private static String url_get_item_details = "http://163.21.245.192/android_connect/get_item_details.php";
    private static String url_all_students = "http://163.21.245.192/android_connect/get_all_students.php";
    private static String[] itemsForLearning = new String[100];
    private static String[] itemsForUser = new String[100];

    // JSON parser class
    JSONParser jsonParser = new JSONParser();
    // Progress Dialog
    private ProgressDialog pDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_in_nfc);
        Intent in = getIntent();
        learn_id = in.getStringExtra("learn_id");

        new GetLearnDetails().execute();
        Calendar mCal = Calendar.getInstance();
        getEnterAcitvityTime = DateFormat.format("kk:mm:ss", mCal.getTime());
        Log.d("CurrentTime" , ""+getEnterAcitvityTime);
        final Date curDate   =   new   Date(System.currentTimeMillis());

        btnBack2 = (Button)findViewById(R.id.btnBackToLearn);
        btnBack2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setClass(LearnInNFCActivity.this,LearnActivity.class);
                startActivity(i);
                finish();
                Calendar mCal = Calendar.getInstance();
                getLeaveAcitvityTime = DateFormat.format("kk:mm:ss", mCal.getTime());
                Log.d("LeaveCurrentTime" , ""+getLeaveAcitvityTime);
                Date   endDate   =   new   Date(System.currentTimeMillis());

                learningTime_local = endDate.getTime() - curDate.getTime() ;
                Long sec=learningTime_local/1000;

                //是否新增新的學習資料或是更新學習資料
                if(itemsForLearning[0] != null && itemsForUser[0] != null) {
                    for (int j = 0; j < products.length(); j++) {
                        if (itemsForLearning[j].equals(learn_id)
                                && itemsForUser[j].equals(Profile.getCurrentProfile().getId())) {
                            int watch_numbers_final = Integer.valueOf(watch_numbers_local);
                            watch_numbers_final++;
                            watch_numbers_local = String.valueOf(watch_numbers_final);

                            long watch_time_final = Integer.valueOf(watch_time_local);
                            watch_time_final += sec ;
                            watch_time_local = String.valueOf(watch_time_final);

                            Log.d("GET" ,watch_time_local+ "  , "+watch_numbers_local );
                            new SaveItem().execute();
                            Log.d("相同" , "嘿嘿！！");
                            break;

                        }else {
                            if (j == products.length()-1){
                                watch_numbers_local = "0";
                                watch_time_local = "0";
                                int watch_numbers_final = 0;
                                watch_numbers_final =Integer.valueOf(watch_numbers_local);
                                watch_numbers_final++;
                                watch_numbers_local = String.valueOf(watch_numbers_final);

                                long watch_time_final = 0;
                                watch_time_final = Integer.valueOf(watch_time_local);
                                watch_time_final += sec ;
                                watch_time_local = String.valueOf(watch_time_final);
                                Log.d("GET" ,watch_time_local+ "  , "+watch_numbers_local );
                                new CreateNewItem().execute();
                                Log.d("不同" , "product");
                            }
                        }
                    }
                }else{
                    int watch_numbers_final = Integer.valueOf(watch_numbers_local)+0;
                    watch_numbers_final++;
                    watch_numbers_local = String.valueOf(watch_numbers_final);

                    long watch_time_final = Integer.valueOf(watch_time_local);
                    watch_time_final += sec ;
                    watch_time_local = String.valueOf(watch_time_final);
                    Log.d("GET" ,watch_time_local+ "  , "+watch_numbers_local );
                    new CreateNewItem().execute();
                    Log.d("不同" , "null");
                }


            }
        });

        btnComments = (Button)findViewById(R.id.btnComments);
        btnComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setClass(LearnInNFCActivity.this , CommentsActivity.class);
                i.putExtra("learn_id" , learn_id);
                i.putExtra("descriptionOfStudent" , descriptionOfStudent);
                i.putExtra("evaluation",evaluation);
                startActivity(i);
                finish();
            }
        });
    }

    class GetLearnDetails extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(LearnInNFCActivity.this);
            pDialog.setMessage("請稍候...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);

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
                        params.add(new BasicNameValuePair("learn_id", learn_id));

                        // getting product details by making HTTP request
                        // Note that product details url will use GET request
                        JSONObject json = jsonParser.makeHttpRequest(
                                url_learning_details, "GET", params);

                        // Building Parameters
                        List<NameValuePair> params_student = new ArrayList<NameValuePair>();
                        // getting JSON string from URL
                        JSONObject json_student = jsonParser.makeHttpRequest(url_all_students, "GET", params_student);

                        List<NameValuePair> params2 = new ArrayList<NameValuePair>();
                        params2.add(new BasicNameValuePair("learning", learn_id));
                        params2.add(new BasicNameValuePair("user", Profile.getCurrentProfile().getId()));

                        // getting product details by making HTTP request
                        // Note that product details url will use GET request
                        JSONObject json2 = jsonParser.makeHttpRequest(
                                url_get_item_details, "GET", params2);
                        // check your log for json response
                        Log.d("All student Details", json_student.toString());


                        success = json.getInt("success");
                        if (success == 1) {

                            // successfully received product details
                            JSONArray productObj2 = json2
                                    .getJSONArray("item"); // JSON Array

                            // get first product object from JSON Array
                            JSONObject product2 = productObj2.getJSONObject(0);

                            watch_numbers_local = product2.getString("watch_numbers");
                            watch_time_local = product2.getString("watch_time");
                            descriptionOfStudent = product2.getString("description");
                            evaluation = product2.getString("evaluation");
                            Log.d("GET" ,watch_time_local+ "  , "+watch_numbers_local );


                            // successfully received product details
                            JSONArray productObj = json
                                    .getJSONArray("learning"); // JSON Array

                            // get first product object from JSON Array
                            JSONObject product = productObj.getJSONObject(0);
                            learn_name2 = (TextView)findViewById(R.id.learn_name);
                            learn_tag2 = (TextView)findViewById(R.id.learn_tag);
                            learn_desc2 = (TextView)findViewById(R.id.learn_desc);
                            learn_tag2.setText(product.getString("tag"));
                            learn_name2.setText(product.getString("name"));
                            learn_desc2.setText(product.getString("description"));

                            image = product.getString("image");
                            img2 = (ImageView)findViewById(R.id.img_learn);
                            img2.setImageBitmap(ImgUtils.getBitmapFromURL("http://163.21.245.192/android_connect/uploadedimages/"+image));

                            students = json_student.getJSONArray("students");

                            for (int i = 0; i < students.length(); i++) {
                                JSONObject c = students.getJSONObject(i);
                                String pid_student = c.getString("pid");
                                String action_completed = c.getString("action_completed");
                                String species = c.getString("species");
                                String student = c.getString("student");

                                if (student.equals(Profile.getCurrentProfile().getLastName()
                                        +Profile.getCurrentProfile().getFirstName())){

                                    String[] student_items = species.split(",");
                                    if (student_items.length !=0){
                                        results_student = new String[student_items.length];
                                        for (int k = 0; k < student_items.length; k++) {
                                            results_student[k] = student_items[k].trim();

                                            if(results_student[k].equals(String.valueOf(learn_id))){
                                                break;
                                            }else if (student_items.length == k+1){
                                                species += ","+learn_id;
                                                action_completed = String.valueOf(Integer.parseInt(action_completed)+1);
                                            }
                                        }
                                    } else{
                                        species += learn_id;
                                        action_completed = String.valueOf(Integer.parseInt(action_completed)+1);
                                    }

                                }
                                List<NameValuePair> params_updateStudent = new ArrayList<NameValuePair>();
                                params_updateStudent.add(new BasicNameValuePair("student", student));
                                params_updateStudent.add(new BasicNameValuePair("action_completed", action_completed));
                                params_updateStudent.add(new BasicNameValuePair("species", species));
                                params_updateStudent.add(new BasicNameValuePair("pid", pid_student));
                                JSONObject json_student1 = jsonParser.makeHttpRequest(url_update_student,
                                        "POST", params_updateStudent);

                            }

                        }

                        else{
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

        }
    }

    public class GetItemDetails extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(LearnInNFCActivity.this);
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
                        List<NameValuePair> params2 = new ArrayList<NameValuePair>();
                        params2.add(new BasicNameValuePair("learning", learn_id));
                        params2.add(new BasicNameValuePair("user", Profile.getCurrentProfile().getId()));

                        // getting product details by making HTTP request
                        // Note that product details url will use GET request
                        JSONObject json2 = jsonParser.makeHttpRequest(
                                url_get_item_details, "GET", params2);

                        // check your log for json response
                        Log.d("Single Item Details", json2.toString());

                        // json success tag
                        success = json2.getInt("success");
                        if (success == 1) {
                            // successfully received product details
                            JSONArray productObj = json2
                                    .getJSONArray("item"); // JSON Array

                            // get first product object from JSON Array
                            JSONObject product = productObj.getJSONObject(0);

                            watch_numbers_local = product.getString("watch_numbers");
                            watch_time_local = product.getString("watch_time");
                            descriptionOfStudent = product.getString("description");
                            evaluation = product.getString("evaluation");
                            Log.d("GET" ,watch_time_local+ "  , "+watch_numbers_local );


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

    class SaveItem extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(LearnInNFCActivity.this);
            pDialog.setMessage("請稍候...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Saving product
         * */
        protected String doInBackground(String... args) {

            Log.d("What????" ,watch_time_local+ "  , "+watch_numbers_local );
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("learning", learn_id));
            params.add(new BasicNameValuePair("user", Profile.getCurrentProfile().getId()));
            params.add(new BasicNameValuePair("watch_numbers", ""+watch_numbers_local));
            params.add(new BasicNameValuePair("watch_time", watch_time_local));
            params.add(new BasicNameValuePair("description", descriptionOfStudent));
            params.add(new BasicNameValuePair("evaluation", evaluation));
            Log.d("GET What????" ,watch_time_local+ "  , "+watch_numbers_local );
            // sending modified data through http request
            // Notice that update product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_update_item,
                    "POST", params);
            Log.d("Update Items", json.toString());
            // check json success tag
            try {
                int success = json.getInt("success");

                if (success == 1) {
                } else {
                    // failed to update product
                }
            }catch(JSONException e){
                Log.e("log_tag", "Error parsing data "+e.toString());
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

    class CreateNewItem extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(LearnInNFCActivity.this);
            pDialog.setMessage("請稍候..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
        @Override
        protected void onProgressUpdate(String... values) {
        }
        /**
         * Creating product
         * */
        protected String doInBackground(String... args) {
            Log.d("items","time:"+watch_time_local+" numbers "+watch_numbers_local+" desc:"
                    +descriptionOfStudent+" evaluation:"+evaluation);

            // Building Parameters
            List<NameValuePair> params1 = new ArrayList<NameValuePair>();
            params1.add(new BasicNameValuePair("watch_time", watch_time_local));
            params1.add(new BasicNameValuePair("watch_numbers", ""+watch_numbers_local));
            params1.add(new BasicNameValuePair("description", descriptionOfStudent));
            params1.add(new BasicNameValuePair("evaluation", evaluation));
            params1.add(new BasicNameValuePair("learning", learn_id));
            params1.add(new BasicNameValuePair("user", Profile.getCurrentProfile().getId()));


            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_create_item,
                    "POST", params1);
            // check log cat fro response
            Log.d("Create Response", json.toString());
            try {
                int success = json.getInt("success");

                if (success == 1) {
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
        pDialog.dismiss();
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub

        if (keyCode == KeyEvent.KEYCODE_BACK) { // 攔截返回鍵
            new android.support.v7.app.AlertDialog.Builder(LearnInNFCActivity.this)
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
