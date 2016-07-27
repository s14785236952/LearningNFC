package com.example.user.learningnfc;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.content.DialogInterface;

import com.facebook.Profile;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CommentsActivity extends Activity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mViewAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    // Progress Dialog
    private ProgressDialog pDialog;

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();
    // url to get all products list
    private static String url_get_comments = "http://163.21.245.192/android_connect/get_comments.php";
    private static String url_get_users = "http://163.21.245.192/android_connect/get_all_users.php";


    Button btn_newComments, btn_backToLearning ;

    String learn_id;
    String descriptionOfStudent;
    String evaluation;
    String userFromComments,userFromUsers;

    String[] Array_userFromComments = new String[1000];
    String[] Array_userFromUsers = new String[1000];
    String[] Array_name = new String[1000];
    String[] Array_desc = new String[1000];
    String[] Array_eval = new String[1000];

    private List<Comments> commentList = new ArrayList<>();

    JSONArray comments = null;
    JSONArray users = null;


    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mViewAdapter = new commentsAdapter(commentList);
        mRecyclerView.setAdapter(mViewAdapter);

        Intent i = getIntent();
        learn_id = i.getStringExtra("learn_id");
        descriptionOfStudent = i.getStringExtra("descriptionOfStudent");
        evaluation = i.getStringExtra("evaluation");

        new LoadAllComments().execute();


        mViewAdapter.notifyDataSetChanged();
        btn_newComments = (Button)findViewById(R.id.btn_newComments);
        btn_newComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View item = LayoutInflater.from(CommentsActivity.this).inflate(R.layout.newcomments, null);
                new AlertDialog.Builder(CommentsActivity.this)
                        .setTitle(R.string.newcomments)
                        .setView(item)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText editText = (EditText)item.findViewById(R.id.edittext);
                                descriptionOfStudent = editText.getText().toString();
                                Comments comment = new Comments(Profile.getCurrentProfile().getLastName()+Profile.getCurrentProfile().getFirstName()
                                        , descriptionOfStudent, "");
                                commentList.add(comment);
                            }
                        })
                        .setNegativeButton(R.string.notok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
            }

        });

//        btn_backToLearning = (Button)findViewById(R.id.btn_backToLearning);
//        btn_backToLearning.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setContentView(R.layout.learning);
//            }
//        });

    }

    class LoadAllComments extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(CommentsActivity.this);
            pDialog.setMessage("Loading Comments. Please wait...");
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
            params.add(new BasicNameValuePair("learning", learn_id));
            JSONObject json = jParser.makeHttpRequest(url_get_comments, "GET", params);
            JSONObject json2 = jParser.makeHttpRequest(url_get_users, "GET", params2);


            // Check your log cat for JSON reponse
            Log.d("All user_status: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt("success");
                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    comments = json.getJSONArray("items");
                    users = json2.getJSONArray("users");
                    for (int i = 0; i < users.length(); i++) {
                        JSONObject c = users.getJSONObject(i);

                        // Storing each json item in variable
                        userFromUsers = c.getString("user_id");
                        String name = c.getString("name");

                        Array_userFromUsers[i] = userFromUsers;
                        Array_name[i] = name;


                        Log.d("infor" ,userFromUsers+"   "+ name);

                    }

                    for (int i = 0; i < comments.length(); i++) {
                        JSONObject c = comments.getJSONObject(i);

                        // Storing each json item in variable
                        userFromComments = c.getString("user");
                        String description_net = c.getString("description");
                        String evaluation_net = c.getString("evaluation");

                        Array_userFromComments[i] = userFromComments;
                        Array_desc[i] = description_net;
                        Array_eval[i] = evaluation_net;

                        Log.d("infor2" ,userFromComments+"   "+ description_net+"   " + "   "+evaluation_net);

                        for (int k = 0 ; k <users.length(); k++){
                            if (Array_userFromUsers[k].equals(Array_userFromComments[i])){
                                Comments comment = new Comments(Array_name[k], Array_desc[i], Array_eval[i]);
                                commentList.add(comment);
                                break;
                            }

                        }

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
            Intent i = new Intent();
            i.putExtra("learn_id",learn_id);
            i.setClass(CommentsActivity.this , LearnInNFCActivity.class);
            startActivity(i);
            finish();

        }
        return super.onKeyDown(keyCode, event);
    }


    }

