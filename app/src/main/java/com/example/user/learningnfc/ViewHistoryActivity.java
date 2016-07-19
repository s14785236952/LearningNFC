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
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

import com.facebook.Profile;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ViewHistoryActivity extends ListActivity {
    private ProgressDialog pDialog;
    JSONParser jParser = new JSONParser();
    // products JSONArray
    JSONArray homeworks = null;
    JSONArray products = null;

    String[] Array_learnID = new String[100];
    String[] Array_name    = new String[100];

    private static String url_get_user_status = "http://163.21.245.192/android_connect/get_user_status.php";
    private static String url_all_NFC = "http://163.21.245.192/android_connect/get_all_learnings.php";

    ArrayList<HashMap<String, String>> user_status_list = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_history);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        new LoadAllHomeworks().execute();
    }

    class LoadAllHomeworks extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ViewHistoryActivity.this);
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
            List<NameValuePair> params2 = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("user", Profile.getCurrentProfile().getId()));
            JSONObject json2 = jParser.makeHttpRequest(url_all_NFC, "GET", params2);
            JSONObject json = jParser.makeHttpRequest(url_get_user_status, "GET", params);

            // Check your log cat for JSON reponse
            Log.d("All user_status: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt("success");
                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    homeworks = json.getJSONArray("items");
                    products = json2.getJSONArray("learnings");
                    for (int i = 0; i < products.length(); i++) {
                        JSONObject c = products.getJSONObject(i);

                        // Storing each json item in variable
                        String learn_id = c.getString("learn_id");
                        String name = c.getString("name");

                        Array_learnID[i] = learn_id;
                        Array_name[i] = name;

                    }


                    // looping through All Products
                    for (int i = 0; i < homeworks.length(); i++) {
                        JSONObject c = homeworks.getJSONObject(i);

                        // Storing each json item in variable
                        String number = c.getString("watch_numbers");
                        String time = c.getString("watch_time");
                        String learning = c.getString("learning");
                        String name="";

                        for (int k = 0 ; k<products.length() ; k++){
                            if (Array_learnID[k].equals(learning)){
                                name = Array_name[k];
                                break;
                            }
                        }

                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        map.put("user", Profile.getCurrentProfile().getId());
                        map.put("name", "你看過了 " + name + " 總共: "+time+" 秒"+"，且看了 "+ number+" 次");

                        // adding HashList to ArrayList
                        user_status_list.add(map);

                    }
                } else {

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
                            ViewHistoryActivity.this, user_status_list,
                            R.layout.list_item, new String[] { "user",
                            "name"},
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
            myIntent = new Intent(ViewHistoryActivity.this, OthersActivity.class);
            startActivity(myIntent);
            this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }

}
