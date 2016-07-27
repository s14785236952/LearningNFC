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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RankScoreActivity extends ListActivity {
    // Progress Dialog
    private ProgressDialog pDialog;

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    ArrayList<HashMap<String, String>> wrongExamsList;

    // url to get all products list
    private static String url_student_wrong_exams = "http://163.21.245.192/android_connect/get_students_wrong_exams.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";

    private String[] results;
    private String wrongExamsScore;
    int length;
    private String[] sort = new String[104];

    // products JSONArray
    JSONArray wrong_exams = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank_score);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        // Hashmap for ListView
        wrongExamsList = new ArrayList<HashMap<String, String>>();

        //init the sort Array
        for (int i = 0; i<=103 ;i++){
            sort[i] = String.valueOf(-1);
        }

        // Loading products in Background Thread
        new LoadAllwrong().execute();

    }

    /**
     * Background Async Task to Load all product by making HTTP Request
     * */
    class LoadAllwrong extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(RankScoreActivity.this);
            pDialog.setMessage("請稍候...");
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
            JSONObject json = jParser.makeHttpRequest(url_student_wrong_exams, "GET", params);

            // Check your log cat for JSON reponse
            Log.d("All wrongExams: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    wrong_exams = json.getJSONArray("exams");

                    // looping through All Products
                    for (int i = 0; i < wrong_exams.length(); i++) {
                        JSONObject c = wrong_exams.getJSONObject(i);

                        // Storing each json item in variable
                        String id = c.getString("exam_id");
                        String wrongExams = c.getString("wrongExams");
                        String time = c.getString("time");

                        Log.d("wrongExams", wrongExams);


                        String[] exam_items = wrongExams.split(",");
                        // items.length 是所有項目的個數
                        results = new String[exam_items.length];
                        length = exam_items.length;
                        // 將結果放入 results，
                        // 並利用 Integer.parseInt 來將整數字串轉換為 int
                        for (int k = 0; k < exam_items.length; k++) {
                            results[k] = exam_items[k].trim();
                            Log.d("exam_items", "" + results[k]);
                        }

                        if (sort[2] == String.valueOf(-1)) {
                            sort[1] = results[0];
                            sort[2] = results[exam_items.length - 1];

                        } else if (sort[4] == String.valueOf(-1)) {
                            sort[3] = results[0];
                            sort[4] = results[exam_items.length - 1];
                            if (Integer.parseInt(sort[4])>=Integer.parseInt(sort[2])){
                                String tmp;
                                tmp = sort[4];
                                sort[4] = sort[2];
                                sort[2] = tmp;
                                String tmp2;
                                tmp2 = sort[3];
                                sort[3] = sort[1];
                                sort[1] = tmp2;
                            }
                        } else{
                            for (int j = 6; j < 101; j += 2) {
                                if (sort[2] == String.valueOf(-1) || sort[4] == String.valueOf(-1)) {
                                    break;
                                } else {
                                    if (sort[j] == String.valueOf(-1)) {
                                        sort[j - 1] = results[0];
                                        sort[j] = results[exam_items.length - 1];
                                        for (int l = j - 2; l >= 2; l -= 2) {
                                            if (Integer.parseInt(sort[j]) <= Integer.parseInt(sort[l - 2]) &&
                                                    Integer.parseInt(sort[j]) >= Integer.parseInt(sort[l])) {
                                                String tmp;
                                                tmp = sort[l];
                                                sort[l] = sort[j];
                                                sort[j] = tmp;
                                                String tmp2;
                                                tmp2 = sort[j - 1];
                                                sort[j - 1] = sort[l - 1];
                                                sort[l - 1] = tmp2;
                                                break;

                                            }

                                        }
                                        break;
                                    }

                                }
                            }
                         }


                    }

                    for (int m = 2;m<101;m+=2){
                        if (sort[m] != String.valueOf(-1)){
                            // creating new HashMap
                            HashMap<String, String> map = new HashMap<String, String>();
                            map.put("ScoreRank","第"+(m/2)+"名: "+sort[m-1] +" 分數: "+sort[m]);
                            Log.d("Rank is", sort[m-1]+sort[m]);
                            // adding HashList to ArrayList
                            wrongExamsList.add(map);
                        }else{
                            break;
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
                            RankScoreActivity.this, wrongExamsList,
                            R.layout.list_item, new String[] { "exam_id",
                            "ScoreRank"},
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
            Intent in = new Intent(getApplicationContext(),
                    OthersActivity.class);
            startActivity(in);
            this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }

}

