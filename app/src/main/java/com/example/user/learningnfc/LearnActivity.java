package com.example.user.learningnfc;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.format.DateFormat;
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
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.nfc.Tag;
import android.os.Parcelable;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class LearnActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    TextView learn_tag2,learn_name2,learn_desc2;
    ImageView img2;
    private Button about;
    private NfcAdapter mAdapter;
    public static String value;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;
    private EditText mNote;
    private TextView mNFCContent;
    private Button mBtn;
    private AlertDialog dialog;
    private Context context;
    Button btnBack2;
    CharSequence getEnterAcitvityTime,getLeaveAcitvityTime;
    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    ArrayList<HashMap<String, String>> productsList;

    JSONArray products = null;

    private boolean writeMode = false;
    private boolean passByOnNewIntent = false;
    private boolean enabledNFC = false;

    private static String learn_id;
    private String image;
    String tag;
    String tagString = "";
    String[] results;
    String[] items;
    private static String[] itemsForLearning = new String[100];
    private static String[] itemsForUser = new String[100];


    // Progress Dialog
    private ProgressDialog pDialog;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();

    private static final String url_learning_details = "http://163.21.245.192/android_connect/get_learning_details.php";
    private static String url_all_learnings = "http://163.21.245.192/android_connect/get_all_learnings.php";
    private static String url_create_item = "http://163.21.245.192/android_connect/create_item.php";
    private static String url_all_items = "http://163.21.245.192/android_connect/get_all_items.php";
    private static String url_update_item = "http://163.21.245.192/android_connect/update_items.php";
    private static String url_get_item_details = "http://163.21.245.192/android_connect/get_item_details.php";


    public static long learningTime_local ;
    public static String watch_time_local;
    public static String watch_numbers_local;
    public static String descriptionOfStudent = "很好很好";
    public static String evaluation = "5";
    // 關閉 NFC tag 寫入模式
    private void disableTagWriteMode() {
        mAdapter.disableForegroundDispatch(this);
        writeMode = false;
    }

    // 判斷使用者有無開啓 NFC
    private boolean isEnabledNFC() {

        NfcManager manager = (NfcManager) context.getSystemService(Context.NFC_SERVICE);
        NfcAdapter adapter = manager.getDefaultAdapter();
        if (adapter != null && adapter.isEnabled()) {
            Toast.makeText(context, "NFC 已開啓 !", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            // 開啓 NFC 設定畫面
            Intent intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
            startActivityForResult(intent, 101);
        }
        return false;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        context = this;

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

        new DownloadImage((ImageView) v.findViewById(R.id.profileImage)).execute(MainActivity.imageUrl);
        TextView nametext = (TextView) v.findViewById(R.id.nameAndSurname);
        nametext.setText("" + Profile.getCurrentProfile().getLastName()+Profile.getCurrentProfile().getFirstName());


        // 1.建立 NFC Adapter
        mAdapter = NfcAdapter.getDefaultAdapter(this);

        // 2.創建一個PendingIntent，當掃描NFC目標端標記時 Android 系統可以讀取標記的資訊。
        mPendingIntent = PendingIntent.getActivity(this, 200,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                PendingIntent.FLAG_UPDATE_CURRENT);

        // 3.宣告IntentFilter來偵聽讀取標記資訊的intent
        IntentFilter tag = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);

        // 4.定義 IntentFilter[] 過濾器
        // 由於每一次發現標記的過程中都會收到TAG_DISCOVERED intent，
        // 因此可藉由定義一個IntentFilter[]來存放所有的IntentFilter物件
        mFilters = new IntentFilter[]{
                tag
        };

        mNFCContent = (TextView) findViewById(R.id.mNFCContent);
        mNFCContent.setText("");
        new LoadAllLearnings().execute();
    }
    @Override
    public void onStart() {
        super.onStart();
        enabledNFC = isEnabledNFC();
    }

    @Override
    public void onResume() {
        super.onResume();
        enableForegroundDispatchSystem();
        if (!enabledNFC) return;

        // 啟動前景Activity調度
        mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters, null);

        if (!passByOnNewIntent) {
            // 判斷標簽模式
            if ((NfcAdapter.ACTION_TAG_DISCOVERED.equals(getIntent().getAction()) ||
                    (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())))) {
                Tag detectedTag = getIntent().getParcelableExtra(NfcAdapter.EXTRA_TAG);
                // 寫入標簽: (欲寫入之記錄 [NdefMessage format], 標簽資訊)
                if (writeMode) {
                } else {
                    showNFCTagContent(getIntent());
                }
            }
        }
    }

    // 取得最新Intent資訊
    @Override
    public void onNewIntent(Intent intent) {

        if (!enabledNFC) return;

        // 初始UI資料
        passByOnNewIntent = true;
        mNFCContent.setText("");


        // 判斷標簽模式
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction()) ||
                (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction()))) {
            Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            // 寫入標簽: (欲寫入之記錄 [NdefMessage format], 標簽資訊)
            if (writeMode) {
            } else {
                showNFCTagContent(intent);
            }
        }
    }


    private void showNFCTagContent(Intent intent) {
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        NdefMessage[] msgs = null;
        if (rawMsgs != null) {
            msgs = new NdefMessage[rawMsgs.length];
            for (int i = 0; i < rawMsgs.length; i++) {
                msgs[i] = (NdefMessage) rawMsgs[i];
            }
            byte[] payload = msgs[0].getRecords()[0].getPayload();
            //讀取的tag
            value = new String(payload);

            // 顯示NFC內容
            mNFCContent.setText("標記內容 : " + value);
            for(int i = 1 ; i<=results.length ; i+=2) {
                tag = results[i];
                if (value.equals(tag)) {
                    learn_id = results[i-1];
                    new GetLearnDetails().execute();
                   setContentView(R.layout.learning);

                    Calendar mCal = Calendar.getInstance();
                    getEnterAcitvityTime = DateFormat.format("kk:mm:ss", mCal.getTime());
                    Log.d("CurrentTime" , ""+getEnterAcitvityTime);
                    final Date   curDate   =   new   Date(System.currentTimeMillis());
                    new GetItemDetails().execute();

                    btnBack2 = (Button)findViewById(R.id.btnBackToLearn);
                    btnBack2.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent();
                            i.setClass(LearnActivity.this,LearnActivity.class);
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
                                            int watch_numbers_final = Integer.valueOf(watch_numbers_local);
                                            watch_numbers_final++;
                                            watch_numbers_local = String.valueOf(watch_numbers_final);

                                            long watch_time_final = Integer.valueOf(watch_time_local);
                                            watch_time_final += sec ;
                                            watch_time_local = String.valueOf(watch_time_final);
                                            Log.d("GET" ,watch_time_local+ "  , "+watch_numbers_local );
                                            new CreateNewItem().execute();
                                            Log.d("不同" , "product");
                                        }
                                    }
                                }
                            }else{
                                int watch_numbers_final = Integer.valueOf(watch_numbers_local);
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

                }
            }


        }

    }


    private void enableForegroundDispatchSystem() {

        Intent intent = new Intent(this, MainActivity.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        IntentFilter[] intentFilters = new IntentFilter[]{};

        mAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return false;
    }



    class LoadAllLearnings extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(LearnActivity.this);
            pDialog.setMessage("請稍候...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting All products from url
         */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            List<NameValuePair> params_learning = new ArrayList<NameValuePair>();
            // getting JSON string from URL
            JSONObject json_learning = jParser.makeHttpRequest(url_all_learnings, "GET", params_learning);
            JSONObject json = jParser.makeHttpRequest(url_all_items, "GET", params);
            // Check your log cat for JSON reponse
           Log.d("All Learnings: ", json.toString());



            try {
                // Checking for SUCCESS TAG
                int success_learning = json_learning.getInt("success");
                int success = json.getInt("success");
                if (success_learning == 1) {
                    // products found
                    // Getting Array of Products
                    products = json_learning.getJSONArray("learnings");

                    // looping through All Products
                    for (int i = 0; i < products.length(); i++) {
                        JSONObject c = products.getJSONObject(i);

                        // Storing each json item in variable
                        String learn_id = c.getString("learn_id");
                        String tag = c.getString("tag");
//                        Log.d("tag" ,learn_id +" and " +tag);
                       tagString += learn_id+","+tag+",";


                    }
                } else {
                }
                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    products = json.getJSONArray("items");

                    // looping through All Products
                    for (int i = 0; i < products.length(); i++) {
                        JSONObject c = products.getJSONObject(i);

                        // Storing each json item in variable
                        String learn_id_server = c.getString("learning");
                        String user_server = c.getString("user");
                        itemsForLearning[i] = learn_id_server;
                        itemsForUser[i] = user_server;
                        Log.d("hahahahaha" , itemsForLearning[i]);


                    }
                } else {
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

             items = tagString.split(",");
            // items.length 是所有項目的個數
            results = new String[items.length];
            for (int k = 0; k < items.length; k++) {
                results[k] = items[k].trim();
            }
            // 將結果放入 results，
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
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
            pDialog = new ProgressDialog(LearnActivity.this);
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

                        // check your log for json response
//                        Log.d("Single Leanring Details", json.toString());

                        // json success tag
                        success = json.getInt("success");
                        if (success == 1) {
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

        }
    }



    class CreateNewItem extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(LearnActivity.this);
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
    //已經寫進LoadAllLearning 裡
    class LoadAllItems extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(LearnActivity.this);
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
            JSONObject json = jParser.makeHttpRequest(url_all_items, "GET", params);

            // Check your log cat for JSON reponse
            Log.d("All Products: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt("success");
                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    products = json.getJSONArray("items");

                    // looping through All Products
                    for (int i = 0; i < products.length(); i++) {
                        JSONObject c = products.getJSONObject(i);

                        // Storing each json item in variable
                        String learn_id_server = c.getString("learning");
                        String user_server = c.getString("user");
                        itemsForLearning[i] = learn_id_server;
                        itemsForUser[i] = user_server;


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


        }

    }


    class SaveItem extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(LearnActivity.this);
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

    class GetItemDetails extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(LearnActivity.this);
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
                        params.add(new BasicNameValuePair("learning", learn_id));
                        params.add(new BasicNameValuePair("user", Profile.getCurrentProfile().getId()));

                        // getting product details by making HTTP request
                        // Note that product details url will use GET request
                        JSONObject json = jsonParser.makeHttpRequest(
                                url_get_item_details, "GET", params);

                        // check your log for json response
                        Log.d("Single Item Details", json.toString());

                        // json success tag
                        success = json.getInt("success");
                        if (success == 1) {
                            // successfully received product details
                            JSONArray productObj = json
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

    @Override
    protected void onDestroy() {
        pDialog.dismiss();
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub

        if (keyCode == KeyEvent.KEYCODE_BACK) { // 攔截返回鍵
            new android.support.v7.app.AlertDialog.Builder(LearnActivity.this)
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
