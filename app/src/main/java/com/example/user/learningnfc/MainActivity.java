package com.example.user.learningnfc;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
        public static String imageUrl;
        private ProgressDialog pDialog;
        public static int privilege;

        JSONParser jsonParser = new JSONParser();

        private static final String TAG_SUCCESS = "success";

    private static String url_create_user = "http://163.21.245.192/android_connect/create_user.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View v = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);

        Log.d("id" , Profile.getCurrentProfile().getId());
        Bundle inBundle = getIntent().getExtras();
        TextView nametext = (TextView) v.findViewById(R.id.nameAndSurname);

        if(inBundle!=null) {
            String name = inBundle.getString("name").toString();
            String surname = inBundle.getString("surname").toString();
            imageUrl = inBundle.getString("imageUrl").toString();

            nametext.setText("" + name + surname);
        } else {
            nametext.setText("" + Profile.getCurrentProfile().getLastName()+Profile.getCurrentProfile().getFirstName());
        }
        new DownloadImage((ImageView) v.findViewById(R.id.profileImage)).execute(imageUrl);

        if (Profile.getCurrentProfile().getId().equals("1067218249991918")) {
            new CreateUser().execute(Profile.getCurrentProfile().getId(), Profile.getCurrentProfile().getLastName() + Profile.getCurrentProfile().getFirstName(), "2");
            privilege = 2;
        }  else {
            new CreateUser().execute(Profile.getCurrentProfile().getId(), Profile.getCurrentProfile().getLastName() + Profile.getCurrentProfile().getFirstName(), "1");
            privilege = 1;
        }
    }




    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            logout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_Learning) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, LearnActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_homework) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, HomeworkActivity.class);
                startActivity(intent);
                finish();
        } else if (id == R.id.nav_exam) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, ExamActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_manage) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, ManangeActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_home) {

        } else if (id == R.id.nav_history) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub

        if (keyCode == KeyEvent.KEYCODE_BACK) { // 攔截返回鍵
            new AlertDialog.Builder(MainActivity.this)
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

    public void logout() {
        LoginManager.getInstance().logOut();
        Intent login = new Intent(MainActivity.this, WelcomeActivity.class);
        startActivity(login);
        finish();
    }

    class CreateUser extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("請稍候");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            String user_id = args[0],
                    name = args[1],
                    privilege = args[2];

            // Building Parameters
            List<NameValuePair> params1 = new ArrayList<NameValuePair>();
            params1.add(new BasicNameValuePair("user_id", user_id));
            params1.add(new BasicNameValuePair("name", name));
            params1.add(new BasicNameValuePair("privilege", privilege));

            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_create_user,
                    "POST", params1);
            // check log cat fro response

            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    Log.d("success create user", user_id);
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

}
