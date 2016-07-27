package com.example.user.learningnfc;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
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

public class RankLearnActivity extends ListActivity {
    private ProgressDialog pDialog;
    JSONParser jParser = new JSONParser();
    // products JSONArray
    JSONArray history,users = null;

    String[] Array_user = new String[100];
    int[] Array_numbers = new int[100];


    private static String url_get_all_items = "http://163.21.245.192/android_connect/get_all_items.php";
    private static String url_get_all_users = "http://163.21.245.192/android_connect/get_all_users.php";

    ArrayList<HashMap<String, String>> rankLearnList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank_learn);
        for (int i =0 ; i<100 ; i++){
            Array_user[i] = String.valueOf(0);
        }
        new LoadAllHistory().execute();
    }

    class LoadAllHistory extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(RankLearnActivity.this);
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
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_get_all_items, "GET", params);

            // Building Parameters
            List<NameValuePair> params_user = new ArrayList<NameValuePair>();
            // getting JSON string from URL
            JSONObject json_user = jParser.makeHttpRequest(url_get_all_users, "GET", params_user);
            // Check your log cat for JSON reponse
            Log.d("All RankLearn: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt("success");
                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    history = json.getJSONArray("items");
                    users = json_user.getJSONArray("users");

                    // looping through All Products
                    for (int i = 0; i < history.length(); i++) {
                        JSONObject c = history.getJSONObject(i);

                        // Storing each json item in variable
                        String user = c.getString("user");
                        int numbers = Integer.parseInt(c.getString("watch_numbers"));
                            for (int k = 0; k < history.length(); k++) {
                                if (Array_user[k].equals(user)) {
                                    Array_numbers[k] += numbers;
                                    break;
                                }
                                if(history.length() == k+1){
                                    for (int j = 0 ; j<history.length() ; j++){
                                        if (Array_user[j].equals(String.valueOf(0))){
                                            Array_user[j] = user;
                                            Array_numbers[j] = numbers;
                                            break;
                                        }

                                    }

                                }
                            }

                    }

                    String[] Array_usersID = new String[users.length()];
                    String[] Array_name = new String[users.length()];
                    for (int i = 0; i < users.length(); i++) {
                        JSONObject c = users.getJSONObject(i);

                        // Storing each json item in variable
                        String users_id = c.getString("user_id");
                        String name = c.getString("name");
                        Array_usersID[i] = users_id;
                        Array_name[i] = name;

                    }


                    for (int l = 0; l<history.length(); l ++) {
                        if (!Array_user[l].equals(String.valueOf(0))) {
                            for (int n = 0 ;n<users.length(); n++) {
                                if (Array_usersID[n].equals(Array_user[l])) {
                                    HashMap<String, String> map = new HashMap<String, String>();
                                    map.put("content", "第" + (l + 1) + "名 : 姓名 : " + Array_name[n] +
                                            "，學習次數為: " + Array_numbers[l]+"次");
                                    rankLearnList.add(map);
                                    break;
                                }
                            }
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
                            RankLearnActivity.this, rankLearnList,
                            R.layout.list_item, new String[] { "user",
                            "content"},
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
            myIntent = new Intent(RankLearnActivity.this, OthersActivity.class);
            startActivity(myIntent);
            this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }

}
