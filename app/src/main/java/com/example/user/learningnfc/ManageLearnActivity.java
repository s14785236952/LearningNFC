package com.example.user.learningnfc;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
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
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ManageLearnActivity extends AppCompatActivity {
    TextView learn_tag,learn_name,learn_desc;
    ImageView img;
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
    Button btnBack;
    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    ArrayList<HashMap<String, String>> productsList;

    JSONArray products = null;

    private boolean writeMode = false;
    private boolean passByOnNewIntent = false;
    private boolean enabledNFC = false;

    String learn_id;
    private String image;
    String tag;
    String tagString = "";
    String[] results;
    String[] items;

    // Progress Dialog
    private ProgressDialog pDialog;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();

    private static final String url_learning_details = "http://163.21.245.192/android_connect/get_learning_details.php";
    private static String url_all_learnings = "http://163.21.245.192/android_connect/get_all_learnings.php";



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
        setContentView(R.layout.activity_manage_learn);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        context = this;
        new LoadAllLearnings().execute();
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

        mNote = (EditText) findViewById(R.id.mNote);
        mNFCContent = (TextView) findViewById(R.id.mNFCContent);
        mBtn = (Button) findViewById(R.id.mBtn);

        mNFCContent.setText("");
        mBtn.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {

                // Write to a tag for as long as the dialog is shown.
                // enableTagWriteMode();
                writeMode = true;
                dialog = new AlertDialog.Builder(context)
                        .setTitle("請將NFC Tag放到NFC手機裝置感應處!")
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {

                            public void onCancel(DialogInterface dialog) {
                                disableTagWriteMode();
                            }

                        }).create();
                dialog.show();

            }

        });

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
                    writeTag(getNoteAsNdef(), detectedTag);
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
                writeTag(getNoteAsNdef(), detectedTag);
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
            value = new String(payload);

            // 顯示NFC內容
            mNFCContent.setText("標記內容 : " + value);
            mNote.setText(value);
            for(int i = 1 ; i<=results.length ; i+=2) {
                tag = results[i];
                if (value.equals(tag)) {
                    learn_id = results[i-1];
                    Log.d("learn_id" , learn_id);
                    new GetLearnDetails().execute();
                    setContentView(R.layout.learning);
                    btnBack = (Button)findViewById(R.id.btnBackToLearn);
                    btnBack.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent();
                            i.setClass(ManageLearnActivity.this,ManageLearnActivity.class);
                            startActivity(i);
                            finish();
                        }
                    });

                }
            }

            Toast.makeText(this, "標記內容 : " + value, Toast.LENGTH_SHORT).show();


        }

    }


    // 取得 NdefMessage
    private NdefMessage getNoteAsNdef() {
        // 1.將輸入的字串轉成byte[]
        byte[] textBytes = mNote.getText().toString().getBytes();
        // 2.將 byte[] 置入 Ndef 記錄列
        NdefRecord textRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
                "text/plain".getBytes(),
                new byte[]{}, textBytes);
        // 3.包裝成 NdefMessage 回傳
        return new NdefMessage(new NdefRecord[]{
                textRecord
        });
    }

    // 寫入標簽: (欲寫入之記錄 [NdefMessage format], 標簽資訊)
    // 注意:要區分格式化前與格式化後的寫入邏輯
    boolean writeTag(NdefMessage message, Tag tag) {
        // 1.訊息長度大小
        int size = message.toByteArray().length;
        try {
            // 2.取得 Ndef 物件
            Ndef ndef = Ndef.get(tag);
            if (ndef != null) { // 此標簽已格式化

                // 連接開啟I/O通道
                ndef.connect();

                if (!ndef.isWritable()) { // 判斷此卡是否是唯讀 ?
                    Toast.makeText(context, "此標簽 read-only.",
                            Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    return false;
                }
                if (ndef.getMaxSize() < size) { // 判斷此卡是否有足夠空間寫入資訊 ?
                    Toast.makeText(context, "標簽容量不足\n標簽容量為: " +
                            ndef.getMaxSize() + " bytes\n寫入訊息容量為: " +
                            size + " bytes.", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    return false;
                }
                // 寫入
                ndef.writeNdefMessage(message);
                Toast.makeText(context, "ＮＦＣ標簽資訊寫入成功(pre-formatted)",
                        Toast.LENGTH_SHORT).show();
                dialog.cancel();
                dialog.dismiss();
                writeMode = false;
                return true;
            } else { // 此標簽未格式化
                // 格式化標籤
                NdefFormatable format = NdefFormatable.get(tag);
                if (format != null) {
                    try {
                        // 連接開啟I/O通道
                        format.connect();
                        // format完成後寫入資訊
                        format.format(message);
                        Toast.makeText(context, "格式化成功並ＮＦＣ標簽資訊寫入成功",
                                Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                        dialog.dismiss();
                        return true;
                    } catch (IOException e) {
                        Toast.makeText(context, "格式化失敗", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        return false;
                    }
                } else {
                    Toast.makeText(context, "此ＮＦＣ標簽並不支援NDEF讀寫格式.",
                            Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    return false;
                }
            }
        } catch (Exception e) {

            if (dialog != null) {
                Toast.makeText(context, "寫入失敗", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        }

        return false;
    }

    private void enableForegroundDispatchSystem() {

        Intent intent = new Intent(this, MainActivity.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        IntentFilter[] intentFilters = new IntentFilter[]{};

        mAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
    }
    class LoadAllLearnings extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ManageLearnActivity.this);
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
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_all_learnings, "GET", params);

            // Check your log cat for JSON reponse
            Log.d("All Learnings: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt("success");
                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    products = json.getJSONArray("learnings");

                    // looping through All Products
                    for (int i = 0; i < products.length(); i++) {
                        JSONObject c = products.getJSONObject(i);

                        // Storing each json item in variable
                        String learn_id = c.getString("learn_id");
                        String tag = c.getString("tag");
                        Log.d("tag" ,learn_id +" and " +tag);
                        tagString += learn_id+","+tag+",";


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
            Log.d("tagString" ,tagString);

            items = tagString.split(",");
            // items.length 是所有項目的個數
            results = new String[items.length];
            for (int k = 0; k < items.length; k++) {
                results[k] = items[k].trim();
                Log.d("results" , ""+results[k]);
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
            pDialog = new ProgressDialog(ManageLearnActivity.this);
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
                        params.add(new BasicNameValuePair("learn_id", learn_id));

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
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Intent myIntent = new Intent();
            myIntent = new Intent(ManageLearnActivity.this, ManangeActivity.class);
            startActivity(myIntent);
            this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }

}
