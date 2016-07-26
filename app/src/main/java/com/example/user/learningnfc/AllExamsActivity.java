package com.example.user.learningnfc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.content.Context;

public class AllExamsActivity extends Activity {

    // Progress Dialog
    private ProgressDialog pDialog;

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();
    ArrayList<HashMap<String, String>> productsList;
    private GroupListAdapter adapter = null;
    private ListView lv = null;
    private List<String> list = new ArrayList<String>();
    private List<String> listTag = new ArrayList<String>();

    // url to get all products list
    private static String url_all_products = "http://163.21.245.192/android_connect/get_all_products.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCTS = "products";
    private static final String TAG_PID = "pid";
    private static final String TAG_NAME = "name";

    // products JSONArray
    JSONArray products = null;
    private static String[] A = new String[301];
    private  int[] getPosition = new int[301];
    int k=1;
    private  int id=0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_exams);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        // Loading products in Background Thread
        new LoadAllProducts().execute();
        setData();
        adapter = new GroupListAdapter(this, list, listTag);
        lv = (ListView)findViewById(R.id.group_list);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {


                    Log.d("position","is"+getPosition[position]+" and id is "+id);
                String pid = Integer.toString(getPosition[position]);
                Intent in = new Intent(getApplicationContext(),
                        EditExamActivity.class);
                // sending pid to next activity
                in.putExtra(TAG_PID,pid);

                // starting new activity and expecting some response back
                startActivityForResult(in, 101);
            }
        });



    }

    // Response from Edit Product Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if result code 100
        if (resultCode == 100) {
            // if result code 100 is received
            // means user edited/deleted product
            // reload this screen again
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }

    }

    /**
     * Background Async Task to Load all product by making HTTP Request
     * */
    class LoadAllProducts extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AllExamsActivity.this);
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
            JSONObject json = jParser.makeHttpRequest(url_all_products, "GET", params);

            // Check your log cat for JSON reponse
            Log.d("All Products: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    products = json.getJSONArray(TAG_PRODUCTS);

                    // looping through All Products
                    for (int i = 0; i < products.length(); i++) {
                        JSONObject c = products.getJSONObject(i);

                        // Storing each json item in variable
                        String id = c.getString(TAG_PID);
                        String name = c.getString(TAG_NAME);
                        String suggest = c.getString("suggest");
                        Log.d("suggest", suggest);
                            A[k] = id;
                            A[k+1] = name;
                            A[k+2] = suggest;
                            Log.d("AAAAA",A[k]);
                            k+=3;

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
        }

    }
    @Override
    protected void onDestroy() {
        //pDialog.dismiss();
        super.onDestroy();
    }
    public void setData(){
        list.add("主機板");
        listTag.add("主機板");
        getPosition[id] = id;
        id++;
        for(int i=1;i<301;i+=3){
            if (A[i]==null){
                break;
            }else {
                if (A[i + 2].equals("主機板")) {
                    list.add(A[i + 1]);
                    getPosition[id] = Integer.parseInt(A[i]);
                    id++;

                }
            }
        }
        list.add("CPU");
        listTag.add("CPU");
        getPosition[id] = id;
        id++;
        for(int i=1;i<301;i+=3){
            if (A[i]==null){
                break;
            }else {
                if (A[i + 2].equals("CPU")) {
                    list.add(A[i + 1]);
                    getPosition[id] = Integer.parseInt(A[i]);
                    id++;
                }
            }
        }
        list.add("音效");
        listTag.add("音效");
        getPosition[id] = id;
        id++;
        for(int i=1;i<301;i+=3){
            if (A[i]==null){
                break;
            }else {
                if (A[i + 2].equals("音效")) {
                    list.add(A[i + 1]);
                    getPosition[id] = Integer.parseInt(A[i]);
                    id++;
                }
            }
        }

        list.add("記憶體");
        listTag.add("記憶體");
        getPosition[id] = id;
        id++;
        for(int i=1;i<301;i+=3){
            if (A[i]==null){
                break;
            }else {
                if (A[i + 2].equals("記憶體")) {
                    list.add(A[i + 1]);
                    getPosition[id] = Integer.parseInt(A[i]);
                    id++;
                }
            }
        }

        list.add("電源");
        listTag.add("電源");
        getPosition[id] = id;
        id++;
        for(int i=1;i<301;i+=3){
            if (A[i]==null){
                break;
            }else {
                if (A[i + 2].equals("電源")) {
                    list.add(A[i + 1]);
                    getPosition[id] = Integer.parseInt(A[i]);
                    id++;
                }
            }
        }

        list.add("顯示卡");
        listTag.add("顯示卡");
        getPosition[id] = id;
        id++;
        for(int i=1;i<301;i+=3){
            if (A[i]==null){
                break;
            }else {
                if (A[i + 2].equals("顯示卡")) {
                    list.add(A[i + 1]);
                    getPosition[id] = Integer.parseInt(A[i]);
                    id++;
                }
            }
        }

        list.add("硬碟");
        listTag.add("硬碟");
        getPosition[id] = id;
        id++;
        for(int i=1;i<301;i+=3){
            if (A[i]==null){
                break;
            }else {
                if (A[i + 2].equals("硬碟")) {
                    list.add(A[i + 1]);
                    getPosition[id] = Integer.parseInt(A[i]);
                    id++;
                }
            }
        }

        list.add("網路");
        listTag.add("網路");
        getPosition[id] = id;
        id++;
        for(int i=1;i<301;i+=3){
            if (A[i]==null){
                break;
            }else {
                if (A[i + 2].equals("網路")) {
                    list.add(A[i + 1]);
                    getPosition[id] = Integer.parseInt(A[i]);
                    id++;
                }
            }
        }

        list.add("輸入裝置");
        listTag.add("輸入裝置");
        getPosition[id] = id;
        id++;
        for(int i=1;i<301;i+=3){
            if (A[i]==null){
                break;
            }else {
                if (A[i + 2].equals("輸入裝置")) {
                    list.add(A[i + 1]);
                    getPosition[id] = Integer.parseInt(A[i]);
                    id++;
                }
            }
        }

        list.add("輸出裝置");
        listTag.add("輸出裝置");
        getPosition[id] = id;
        id++;
        for(int i=1;i<301;i+=3){
            if (A[i]==null){
                break;
            }else {
                if (A[i + 2].equals("輸出裝置")) {
                    list.add(A[i + 1]);
                    getPosition[id] = Integer.parseInt(A[i]);
                    id++;
                }
            }
        }

        list.add("機殼");
        listTag.add("機殼");
        getPosition[id] = id;
        id++;
        for(int i=1;i<301;i+=3){
            if (A[i]==null){
                break;
            }else {
                if (A[i + 2].equals("機殼")) {
                    list.add(A[i + 1]);
                    getPosition[id] = Integer.parseInt(A[i]);
                    id++;
                }
            }
        }
    }
    private static class GroupListAdapter extends ArrayAdapter<String>{

        private List<String> listTag = null;
        public GroupListAdapter(Context context, List<String> objects, List<String> tags) {
            super(context, 0, objects);
            this.listTag = tags;
        }

        @Override
        public boolean isEnabled(int position) {
            if(listTag.contains(getItem(position))){
                return false;
            }
            return super.isEnabled(position);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if(listTag.contains(getItem(position))){
                view = LayoutInflater.from(getContext()).inflate(R.layout.group_list_item_tag, null);
            }else{
                view = LayoutInflater.from(getContext()).inflate(R.layout.group_list_item, null);
            }
            TextView textView = (TextView) view.findViewById(R.id.group_list_item_text);
            textView.setText(getItem(position));
            return view;
        }
    }

}