package com.example.user.learningnfc;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Profile;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ExamGoActivity extends AppCompatActivity {

    TextView t_name;
    RadioButton rb1;
    RadioButton rb2;
    RadioButton rb3;
    RadioButton rb4;
    // JSON parser class
    JSONParser jsonParser = new JSONParser();

    private static String url_all_products = "http://163.21.245.192/android_connect/get_all_products.php";

    private static final String url_product_details = "http://163.21.245.192/android_connect/get_product_details.php";

    private static final String url_create_mylist = "http://163.21.245.192/android_connect/create_wrongExams.php";

    // Progress Dialog
    private ProgressDialog pDialog;

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCTS = "products";
    private static final String TAG_PID = "pid";
    private static final String TAG_NAME = "name";
    private static final String TAG_OPA = "optiona";
    private static final String TAG_OPB = "optionb";
    private static final String TAG_OPC = "optionc";
    private static final String TAG_OPD = "optiond";
    private static final String TAG_DESC = "description";
    // products JSONArray
    JSONArray products = null;

    public static int exam_score;
    public static int wrong_exams_number;
    public static String exam_answer1;
    public static String exam_answer2;
    public static String exam_answer3;
    public static String exam_answer4;
    public static String exam_answer5;
    public static String exam_answer6;
    public static String exam_answer7;
    public static String exam_answer8;
    public static String exam_answer9;
    public static String exam_answer10;

    public static String user_answer1;
    public static String user_answer2;
    public static String user_answer3;
    public static String user_answer4;
    public static String user_answer5;
    public static String user_answer6;
    public static String user_answer7;
    public static String user_answer8;
    public static String user_answer9;
    public static String user_answer10;

    public static String id1;
    public static String id2;
    public static String id3;
    public static String id4;
    public static String id5;
    public static String id6;
    public static String id7;
    public static String id8;
    public static String id9;
    public static String id10;

    public static String exam_desc1;
    public static String exam_desc2;
    public static String exam_desc3;
    public static String exam_desc4;
    public static String exam_desc5;
    public static String exam_desc6;
    public static String exam_desc7;
    public static String exam_desc8;
    public static String exam_desc9;
    public static String exam_desc10;

    public static ArrayList<String> mylist = new ArrayList();

    String wrongExams ="";

    String nonselected ="你沒有作答的題目有:";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_go);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        new LoadExam().execute();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Button btn_examsubmit = (Button)findViewById(R.id.btn_examsubmmit);
            btn_examsubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //init wrong_exams_number and score
                    wrongExams = "";
                   wrong_exams_number = 0 ;
                   exam_score = 0 ;
                    mylist.add(Profile.getCurrentProfile().getLastName());
                    mylist.add(Profile.getCurrentProfile().getFirstName());
                    wrongExams += Profile.getCurrentProfile().getLastName();
                    wrongExams += Profile.getCurrentProfile().getFirstName();
                    RadioGroup radioGroup = (RadioGroup)findViewById(R.id.rgroup);
                    RadioGroup radioGroup2 = (RadioGroup)findViewById(R.id.rgroup_2);
                    RadioGroup radioGroup3 = (RadioGroup)findViewById(R.id.rgroup_3);
                    RadioGroup radioGroup4 = (RadioGroup)findViewById(R.id.rgroup_4);
                    RadioGroup radioGroup5 = (RadioGroup)findViewById(R.id.rgroup_5);
                    RadioGroup radioGroup6 = (RadioGroup)findViewById(R.id.rgroup_6);
                    RadioGroup radioGroup7 = (RadioGroup)findViewById(R.id.rgroup_7);
                    RadioGroup radioGroup8 = (RadioGroup)findViewById(R.id.rgroup_8);
                    RadioGroup radioGroup9 = (RadioGroup)findViewById(R.id.rgroup_9);
                    RadioGroup radioGroup10 = (RadioGroup)findViewById(R.id.rgroup_10);

                    switch(radioGroup.getCheckedRadioButtonId()){
                        case R.id.radioButton:
                           Log.d("click a", "true");
                           if(exam_answer1.equals("A")||exam_answer1.equals("a")){
                               Log.d("click", "work");
                            exam_score += 10;
                           }else {
                            mylist.add(id1);
                               mylist.add("A");
                               wrong_exams_number++;
                               wrongExams += ","+id1;
                               user_answer1 = "A";
                           }
                            break;
                        case R.id.radioButton2:
                            if(exam_answer1.equals("B")||exam_answer1.equals("b"))
                                exam_score+=10;
                            else{
                                mylist.add(id1);
                                mylist.add("B");
                                wrong_exams_number++;
                                wrongExams += ","+id1;
                                user_answer1 = "B";
                            }
                            break;
                        case R.id.radioButton3:
                            if(exam_answer1.equals("C")||exam_answer1.equals("c")){
                                exam_score+=10;
                            }else {
                                mylist.add(id1);
                                mylist.add("C");
                                wrong_exams_number++;
                                wrongExams += ","+id1;
                                user_answer1 = "C";
                            }
                            break;
                        case R.id.radioButton4:
                            if(exam_answer1.equals("D")||exam_answer1.equals("d")){
                                exam_score+=10;
                            }else{
                                mylist.add(id1);
                                mylist.add("D");
                                wrong_exams_number++;
                                wrongExams += ","+id1;
                                user_answer1 = "D";
                            }
                            break;

                    }

                    switch(radioGroup2.getCheckedRadioButtonId()){
                        case R.id.radioButton_2:
                            Log.d("click a", "true");
                            if(exam_answer2.equals("A")||exam_answer2.equals("a")){
                                Log.d("click", "work");
                                exam_score += 10;
                            }else {
                                mylist.add(id2);
                                mylist.add("A");
                                wrong_exams_number++;
                                wrongExams += ","+id2;
                                user_answer2 = "A";
                            }
                            break;
                        case R.id.radioButton2_2:
                            if(exam_answer2.equals("B")||exam_answer2.equals("b"))
                                exam_score+=10;
                            else{
                                mylist.add(id2);
                                mylist.add("B");
                                wrong_exams_number++;
                                wrongExams += ","+id2;
                                user_answer2 = "B";

                            }
                            break;
                        case R.id.radioButton3_2:
                            if(exam_answer2.equals("C")||exam_answer2.equals("c")){
                                exam_score+=10;
                            }else {
                                mylist.add(id2);
                                mylist.add("C");
                                wrong_exams_number++;
                                wrongExams += ","+id2;
                                user_answer2 = "C";

                            }
                            break;
                        case R.id.radioButton4_2:
                            if(exam_answer2.equals("D")||exam_answer2.equals("d")){
                                exam_score+=10;
                            }else{
                                mylist.add(id2);
                                mylist.add("D");
                                wrong_exams_number++;
                                wrongExams += ","+id2;
                                user_answer2 = "D";
                            }
                            break;
                    }

                    switch(radioGroup3.getCheckedRadioButtonId()){
                        case R.id.radioButton_3:
                            Log.d("click a", "true");
                            if(exam_answer3.equals("A")||exam_answer3.equals("a")){
                                Log.d("click", "work");
                                exam_score += 10;
                            }else {
                                mylist.add(id1);
                                mylist.add("A");
                                wrong_exams_number++;
                                wrongExams += ","+id3;
                                user_answer3 = "A";

                            }
                            break;
                        case R.id.radioButton2_3:
                            if(exam_answer3.equals("B")||exam_answer3.equals("b"))
                                exam_score+=10;
                            else{
                                mylist.add(id3);
                                mylist.add("B");
                                wrong_exams_number++;
                                wrongExams += ","+id3;
                                user_answer3 = "B";

                            }
                            break;
                        case R.id.radioButton3_3:
                            if(exam_answer3.equals("C")||exam_answer3.equals("c")){
                                exam_score+=10;
                            }else {
                                mylist.add(id3);
                                mylist.add("C");
                                wrong_exams_number++;
                                wrongExams += ","+id3;
                                user_answer3 = "C";

                            }
                            break;
                        case R.id.radioButton4_3:
                            if(exam_answer3.equals("D")||exam_answer3.equals("d")){
                                exam_score+=10;
                            }else{
                                mylist.add(id3);
                                mylist.add("D");
                                wrong_exams_number++;
                                wrongExams += ","+id3;
                                user_answer3 = "D";

                            }
                            break;
                    }

                    switch(radioGroup4.getCheckedRadioButtonId()){
                        case R.id.radioButton_4:
                            Log.d("click a", "true");
                            if(exam_answer4.equals("A")||exam_answer4.equals("a")){
                                Log.d("click", "work");
                                exam_score += 10;
                            }else {
                                mylist.add(id4);
                                mylist.add("A");
                                wrong_exams_number++;
                                wrongExams += ","+id4;
                                user_answer4 = "A";

                            }
                            break;
                        case R.id.radioButton2_4:
                            if(exam_answer4.equals("B")||exam_answer4.equals("b"))
                                exam_score+=10;
                            else{
                                mylist.add(id4);
                                mylist.add("B");
                                wrong_exams_number++;
                                wrongExams += ","+id4;
                                user_answer4 = "B";

                            }
                            break;
                        case R.id.radioButton3_4:
                            if(exam_answer4.equals("C")||exam_answer4.equals("c")){
                                exam_score+=10;
                            }else {
                                mylist.add(id4);
                                mylist.add("C");
                                wrong_exams_number++;
                                wrongExams += ","+id4;
                                user_answer4 = "C";

                            }
                            break;
                        case R.id.radioButton4_4:
                            if(exam_answer4.equals("D")||exam_answer4.equals("d")){
                                exam_score+=10;
                            }else{
                                mylist.add(id4);
                                mylist.add("D");
                                wrong_exams_number++;
                                wrongExams += ","+id4;
                                user_answer4 = "D";

                            }
                            break;
                    }

                    switch(radioGroup5.getCheckedRadioButtonId()){
                        case R.id.radioButton_5:
                            Log.d("click a", "true");
                            if(exam_answer5.equals("A")||exam_answer5.equals("a")){
                                Log.d("click", "work");
                                exam_score += 10;
                            }else {
                                mylist.add(id5);
                                mylist.add("A");
                                wrong_exams_number++;
                                wrongExams += ","+id5;
                                user_answer5 = "A";

                            }
                            break;
                        case R.id.radioButton2_5:
                            if(exam_answer5.equals("B")||exam_answer5.equals("b"))
                                exam_score+=10;
                            else{
                                mylist.add(id5);
                                mylist.add("B");
                                wrong_exams_number++;
                                wrongExams += ","+id5;
                                user_answer5 = "B";

                            }
                            break;
                        case R.id.radioButton3_5:
                            if(exam_answer5.equals("C")||exam_answer5.equals("c")){
                                exam_score+=10;
                            }else {
                                mylist.add(id5);
                                mylist.add("C");
                                wrong_exams_number++;
                                wrongExams += ","+id5;
                                user_answer5 = "C";

                            }
                            break;
                        case R.id.radioButton4_5:
                            if(exam_answer5.equals("D")||exam_answer5.equals("d")){
                                exam_score+=10;
                            }else{
                                mylist.add(id5);
                                mylist.add("D");
                                wrong_exams_number++;
                                wrongExams += ","+id5;
                                user_answer5 = "D";

                            }
                            break;
                    }

                    switch(radioGroup6.getCheckedRadioButtonId()){
                        case R.id.radioButton_6:
                            Log.d("click a", "true");
                            if(exam_answer6.equals("A")||exam_answer6.equals("a")){
                                Log.d("click", "work");
                                exam_score += 10;
                            }else {
                                mylist.add(id6);
                                mylist.add("A");
                                wrong_exams_number++;
                                wrongExams += ","+id6;
                                user_answer6 = "A";

                            }
                            break;
                        case R.id.radioButton2_6:
                            if(exam_answer6.equals("B")||exam_answer6.equals("b"))
                                exam_score+=10;
                            else{
                                mylist.add(id6);
                                mylist.add("B");
                                wrong_exams_number++;
                                wrongExams += ","+id6;
                                user_answer6 = "B";

                            }
                            break;
                        case R.id.radioButton3_6:
                            if(exam_answer6.equals("C")||exam_answer6.equals("c")){
                                exam_score+=10;
                            }else {
                                mylist.add(id6);
                                mylist.add("C");
                                wrong_exams_number++;
                                wrongExams += ","+id6;
                                user_answer6 = "C";

                            }
                            break;
                        case R.id.radioButton4_6:
                            if(exam_answer6.equals("D")||exam_answer6.equals("d")){
                                exam_score+=10;
                            }else{
                                mylist.add(id6);
                                mylist.add("D");
                                wrong_exams_number++;
                                wrongExams += ","+id6;
                                user_answer6 = "D";

                            }
                            break;
                    }

                    switch(radioGroup7.getCheckedRadioButtonId()){
                        case R.id.radioButton_7:
                            Log.d("click a", "true");
                            if(exam_answer7.equals("A")||exam_answer7.equals("a")){
                                Log.d("click", "work");
                                exam_score += 10;
                            }else {
                                mylist.add(id7);
                                mylist.add("A");
                                wrong_exams_number++;
                                wrongExams += ","+id7;
                                user_answer7 = "A";

                            }
                            break;
                        case R.id.radioButton2_7:
                            if(exam_answer7.equals("B")||exam_answer7.equals("b"))
                                exam_score+=10;
                            else{
                                mylist.add(id7);
                                mylist.add("B");
                                wrong_exams_number++;
                                wrongExams += ","+id7;
                                user_answer7 = "B";

                            }
                            break;
                        case R.id.radioButton3_7:
                            if(exam_answer7.equals("C")||exam_answer7.equals("c")){
                                exam_score+=10;
                            }else {
                                mylist.add(id7);
                                mylist.add("C");
                                wrong_exams_number++;
                                wrongExams += ","+id7;
                                user_answer7 = "C";

                            }
                            break;
                        case R.id.radioButton4_7:
                            if(exam_answer7.equals("D")||exam_answer7.equals("d")){
                                exam_score+=10;
                            }else{
                                mylist.add(id7);
                                mylist.add("D");
                                wrong_exams_number++;
                                wrongExams += ","+id7;
                                user_answer7 = "D";

                            }
                            break;
                    }

                    switch(radioGroup8.getCheckedRadioButtonId()){
                        case R.id.radioButton_8:
                            Log.d("click a", "true");
                            if(exam_answer8.equals("A")||exam_answer8.equals("a")){
                                Log.d("click", "work");
                                exam_score += 10;
                            }else {
                                mylist.add(id8);
                                mylist.add("A");
                                wrong_exams_number++;
                                wrongExams += ","+id8;
                                user_answer8 = "A";

                            }
                            break;
                        case R.id.radioButton2_8:
                            if(exam_answer8.equals("B")||exam_answer8.equals("b"))
                                exam_score+=10;
                            else{
                                mylist.add(id8);
                                mylist.add("B");
                                wrong_exams_number++;
                                wrongExams += ","+id8;
                                user_answer8 = "B";

                            }
                            break;
                        case R.id.radioButton3_8:
                            if(exam_answer8.equals("C")||exam_answer8.equals("c")){
                                exam_score+=10;
                            }else {
                                mylist.add(id8);
                                mylist.add("C");
                                wrong_exams_number++;
                                wrongExams += ","+id8;
                                user_answer8 = "C";

                            }
                            break;
                        case R.id.radioButton4_8:
                            if(exam_answer8.equals("D")||exam_answer8.equals("d")){
                                exam_score+=10;
                            }else{
                                mylist.add(id8);
                                mylist.add("D");
                                wrong_exams_number++;
                                wrongExams += ","+id8;
                                user_answer8 = "D";
                            }
                            break;
                    }

                    switch(radioGroup9.getCheckedRadioButtonId()){
                        case R.id.radioButton_9:
                            Log.d("click a", "true");
                            if(exam_answer9.equals("A")||exam_answer9.equals("a")){
                                Log.d("click", "work");
                                exam_score += 10;
                            }else {
                                mylist.add(id9);
                                mylist.add("A");
                                wrong_exams_number++;
                                wrongExams += ","+id9;
                                user_answer9 = "A";

                            }
                            break;
                        case R.id.radioButton2_9:
                            if(exam_answer9.equals("B")||exam_answer9.equals("b"))
                                exam_score+=10;
                            else{
                                mylist.add(id9);
                                mylist.add("B");
                                wrong_exams_number++;
                                wrongExams += ","+id9;
                                user_answer9 = "B";

                            }
                            break;
                        case R.id.radioButton3_9:
                            if(exam_answer9.equals("C")||exam_answer9.equals("c")){
                                exam_score+=10;
                            }else {
                                mylist.add(id9);
                                mylist.add("C");
                                wrong_exams_number++;
                                wrongExams += ","+id9;
                                user_answer9 = "C";

                            }
                            break;
                        case R.id.radioButton4_9:
                            if(exam_answer9.equals("D")||exam_answer9.equals("d")){
                                exam_score+=10;
                            }else{
                                mylist.add(id9);
                                mylist.add("D");
                                wrong_exams_number++;
                                wrongExams += ","+id9;
                                user_answer9 = "D";

                            }
                            break;
                    }

                    switch(radioGroup10.getCheckedRadioButtonId()){
                        case R.id.radioButton_10:
                            Log.d("click a", "true");
                            if(exam_answer10.equals("A")||exam_answer10.equals("a")){
                                Log.d("click", "work");
                                exam_score += 10;
                            }else {
                                mylist.add(id10);
                                mylist.add("A");
                                wrong_exams_number++;
                                wrongExams += ","+id10;
                                user_answer10 = "A";

                            }
                            break;
                        case R.id.radioButton2_10:
                            if(exam_answer10.equals("B")||exam_answer10.equals("b"))
                                exam_score+=10;
                            else{
                                mylist.add(id10);
                                mylist.add("B");
                                wrong_exams_number++;
                                wrongExams += ","+id10;
                                user_answer10 = "B";

                            }
                            break;
                        case R.id.radioButton3_10:
                            if(exam_answer10.equals("C")||exam_answer10.equals("c")){
                                exam_score+=10;
                            }else {
                                mylist.add(id10);
                                mylist.add("C");
                                wrong_exams_number++;
                                wrongExams += ","+id10;
                                user_answer10 = "C";

                            }
                            break;
                        case R.id.radioButton4_10:
                            if(exam_answer10.equals("D")||exam_answer10.equals("d")){
                                exam_score+=10;
                            }else{
                                mylist.add(id10);
                                mylist.add("D");
                                wrong_exams_number++;
                                wrongExams += ","+id10;
                                user_answer10 = "D";

                            }
                            break;
                    }

                    if(radioGroup.getCheckedRadioButtonId() == R.id.radioButton
                            || radioGroup.getCheckedRadioButtonId() == R.id.radioButton2
                            || radioGroup.getCheckedRadioButtonId() == R.id.radioButton3
                            || radioGroup.getCheckedRadioButtonId() == R.id.radioButton4)
                    {

                    }else{
                        nonselected += "第一題 ,";
                    }
                    if(radioGroup2.getCheckedRadioButtonId() == R.id.radioButton_2
                            || radioGroup2.getCheckedRadioButtonId() == R.id.radioButton2_2
                            || radioGroup2.getCheckedRadioButtonId() == R.id.radioButton3_2
                            || radioGroup2.getCheckedRadioButtonId() == R.id.radioButton4_2) {

                    }else{
                        nonselected += "第二題 ,";
                    }
                    if(radioGroup3.getCheckedRadioButtonId() == R.id.radioButton_3
                            || radioGroup3.getCheckedRadioButtonId() == R.id.radioButton2_3
                            || radioGroup3.getCheckedRadioButtonId() == R.id.radioButton3_3
                            || radioGroup3.getCheckedRadioButtonId() == R.id.radioButton4_3) {

                    }else{
                        nonselected += "第三題 ,";
                    }
                    if(radioGroup4.getCheckedRadioButtonId() == R.id.radioButton_4
                            || radioGroup4.getCheckedRadioButtonId() == R.id.radioButton2_4
                            || radioGroup4.getCheckedRadioButtonId() == R.id.radioButton3_4
                            || radioGroup4.getCheckedRadioButtonId() == R.id.radioButton4_4) {

                    }else{
                        nonselected += "第四題 ,";
                    }
                    if(radioGroup5.getCheckedRadioButtonId() == R.id.radioButton_5
                            || radioGroup5.getCheckedRadioButtonId() == R.id.radioButton2_5
                            || radioGroup5.getCheckedRadioButtonId() == R.id.radioButton3_5
                            || radioGroup5.getCheckedRadioButtonId() == R.id.radioButton4_5) {

                    }else{
                        nonselected += "第五題 ,";
                    }
                    if(radioGroup6.getCheckedRadioButtonId() == R.id.radioButton_6
                            || radioGroup6.getCheckedRadioButtonId() == R.id.radioButton2_6
                            || radioGroup6.getCheckedRadioButtonId() == R.id.radioButton3_6
                            || radioGroup6.getCheckedRadioButtonId() == R.id.radioButton4_6) {

                    }else{
                        nonselected += "第六題 ,";
                    }
                    if(radioGroup7.getCheckedRadioButtonId() == R.id.radioButton_7
                            || radioGroup7.getCheckedRadioButtonId() == R.id.radioButton2_7
                            || radioGroup7.getCheckedRadioButtonId() == R.id.radioButton3_7
                            || radioGroup7.getCheckedRadioButtonId() == R.id.radioButton4_7) {

                    }else{
                        nonselected += "第七題 ,";
                    }
                    if(radioGroup8.getCheckedRadioButtonId() == R.id.radioButton_8
                            || radioGroup8.getCheckedRadioButtonId() == R.id.radioButton2_8
                            || radioGroup8.getCheckedRadioButtonId() == R.id.radioButton3_8
                            || radioGroup8.getCheckedRadioButtonId() == R.id.radioButton4_8) {

                    }else{
                        nonselected += "第八題 ,";
                    }
                    if(radioGroup9.getCheckedRadioButtonId() == R.id.radioButton_9
                            || radioGroup9.getCheckedRadioButtonId() == R.id.radioButton2_9
                            || radioGroup9.getCheckedRadioButtonId() == R.id.radioButton3_9
                            || radioGroup9.getCheckedRadioButtonId() == R.id.radioButton4_9) {

                    }else{
                        nonselected += "第九題 ,";
                    }
                    if(radioGroup10.getCheckedRadioButtonId() == R.id.radioButton_10
                            || radioGroup10.getCheckedRadioButtonId() == R.id.radioButton2_10
                            || radioGroup10.getCheckedRadioButtonId() == R.id.radioButton3_10
                            || radioGroup10.getCheckedRadioButtonId() == R.id.radioButton4_10) {

                    }else{
                        nonselected += "第十題 ,";
                    }
                    nonselected += "請繼續作答,謝謝";
                    Log.d("nonselected" , nonselected);

                    if(!nonselected.equals("你沒有作答的題目有:請繼續作答,謝謝")) {
                        new AlertDialog.Builder(ExamGoActivity.this)
                                .setTitle("您尚未答題完畢")
                                .setMessage(""+nonselected)
                                .setIcon(R.mipmap.ic_launcher)
                                .setPositiveButton("確定",
                                        new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog,
                                                                int which) {
                                            }
                                        }).show();

                        nonselected = "你沒有作答的題目有:";
                        wrong_exams_number = 0 ;
                        exam_score = 0 ;
                        mylist.clear();
                    }

                    while (nonselected.equals("你沒有作答的題目有:請繼續作答,謝謝")){
                        wrongExams += "," + exam_score;
                        for (int k = 0 ; k <= wrong_exams_number*2+1 ; k++){
                            Log.d("mylist with answer" ,mylist.get(k));
                        }

                        new CreateMylist().execute(wrongExams);
                        Intent intent = new Intent();
                        intent.setClass(ExamGoActivity.this, ResultActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    }

                }
            });


    }

    class LoadExam extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ExamGoActivity.this);
            pDialog.setMessage("Loading exams. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting All products from url
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

                        // getting product details by making HTTP request
                        // Note that product details url will use GET request
                        JSONObject json = jsonParser.makeHttpRequest(
                                url_all_products, "GET", params);

                        // check your log for json response
                        Log.d("Load Exams", json.toString());
                        // json success tag
                        success = json.getInt(TAG_SUCCESS);
                        if (success == 1) {

                            // successfully received product details
                            JSONArray products = json.getJSONArray(TAG_PRODUCTS);

                             int[] random_array = new int[10];
                             int random;

                             for(int i = 0 ; i < 10 ;i++) {
                                 random = (int) (Math.random() * (products.length()));
                                 Log.d("i", Integer.toString(i));
                                 Log.d("random loop", Integer.toString(random));
                                 random_array[i] = random;
                                 Log.d("ARRAY", Integer.toString(random_array[i]));
                                 for(int k = i-1 ; k >= 0 ; k--) {
                                     Log.d("k", Integer.toString(k));
                                     if (random_array[k] == random) {
                                         i--;
                                         break;
                                     }
                                 }
                             }
                                JSONObject exam1 = products.getJSONObject(random_array[0]);
                                rb1 = (RadioButton) findViewById(R.id.radioButton);
                                rb2 = (RadioButton) findViewById(R.id.radioButton2);
                                rb3 = (RadioButton) findViewById(R.id.radioButton3);
                                rb4 = (RadioButton) findViewById(R.id.radioButton4);
                                t_name = (TextView)findViewById(R.id.textView_showtext);
                                rb1.setText(exam1.getString(TAG_OPA));
                                rb2.setText(exam1.getString(TAG_OPB));
                                rb3.setText(exam1.getString(TAG_OPC));
                                rb4.setText(exam1.getString(TAG_OPD));
                                t_name.setText(exam1.getString(TAG_NAME));
                            exam_answer1 = exam1.getString("answer");
                            id1 = exam1.getString(TAG_PID);
                            exam_desc1 = exam1.getString(TAG_DESC);

                            JSONObject exam2 = products.getJSONObject(random_array[1]);
                            rb1 = (RadioButton) findViewById(R.id.radioButton_2);
                            rb2 = (RadioButton) findViewById(R.id.radioButton2_2);
                            rb3 = (RadioButton) findViewById(R.id.radioButton3_2);
                            rb4 = (RadioButton) findViewById(R.id.radioButton4_2);
                            t_name = (TextView)findViewById(R.id.textView_showtext_2);
                            rb1.setText(exam2.getString(TAG_OPA));
                            rb2.setText(exam2.getString(TAG_OPB));
                            rb3.setText(exam2.getString(TAG_OPC));
                            rb4.setText(exam2.getString(TAG_OPD));
                            t_name.setText(exam2.getString(TAG_NAME));
                            exam_answer2 = exam2.getString("answer");
                            id2 = exam2.getString(TAG_PID);
                            exam_desc2 = exam2.getString(TAG_DESC);

                            JSONObject exam3 = products.getJSONObject(random_array[2]);
                            rb1 = (RadioButton) findViewById(R.id.radioButton_3);
                            rb2 = (RadioButton) findViewById(R.id.radioButton2_3);
                            rb3 = (RadioButton) findViewById(R.id.radioButton3_3);
                            rb4 = (RadioButton) findViewById(R.id.radioButton4_3);
                            t_name = (TextView)findViewById(R.id.textView_showtext_3);
                            rb1.setText(exam3.getString(TAG_OPA));
                            rb2.setText(exam3.getString(TAG_OPB));
                            rb3.setText(exam3.getString(TAG_OPC));
                            rb4.setText(exam3.getString(TAG_OPD));
                            t_name.setText(exam3.getString(TAG_NAME));
                            exam_answer3 = exam3.getString("answer");
                            id3 = exam3.getString(TAG_PID);
                            exam_desc3 = exam3.getString(TAG_DESC);

                            JSONObject exam4 = products.getJSONObject(random_array[3]);
                            rb1 = (RadioButton) findViewById(R.id.radioButton_4);
                            rb2 = (RadioButton) findViewById(R.id.radioButton2_4);
                            rb3 = (RadioButton) findViewById(R.id.radioButton3_4);
                            rb4 = (RadioButton) findViewById(R.id.radioButton4_4);
                            t_name = (TextView)findViewById(R.id.textView_showtext_4);
                            rb1.setText(exam4.getString(TAG_OPA));
                            rb2.setText(exam4.getString(TAG_OPB));
                            rb3.setText(exam4.getString(TAG_OPC));
                            rb4.setText(exam4.getString(TAG_OPD));
                            t_name.setText(exam4.getString(TAG_NAME));
                            exam_answer4 = exam4.getString("answer");
                            id4 = exam4.getString(TAG_PID);
                            exam_desc4 = exam4.getString(TAG_DESC);

                            JSONObject exam5 = products.getJSONObject(random_array[4]);
                            rb1 = (RadioButton) findViewById(R.id.radioButton_5);
                            rb2 = (RadioButton) findViewById(R.id.radioButton2_5);
                            rb3 = (RadioButton) findViewById(R.id.radioButton3_5);
                            rb4 = (RadioButton) findViewById(R.id.radioButton4_5);
                            t_name = (TextView)findViewById(R.id.textView_showtext_5);
                            rb1.setText(exam5.getString(TAG_OPA));
                            rb2.setText(exam5.getString(TAG_OPB));
                            rb3.setText(exam5.getString(TAG_OPC));
                            rb4.setText(exam5.getString(TAG_OPD));
                            t_name.setText(exam5.getString(TAG_NAME));
                            exam_answer5 = exam5.getString("answer");
                            id5 = exam5.getString(TAG_PID);
                            exam_desc5 = exam5.getString(TAG_DESC);

                            JSONObject exam6 = products.getJSONObject(random_array[5]);
                            rb1 = (RadioButton) findViewById(R.id.radioButton_6);
                            rb2 = (RadioButton) findViewById(R.id.radioButton2_6);
                            rb3 = (RadioButton) findViewById(R.id.radioButton3_6);
                            rb4 = (RadioButton) findViewById(R.id.radioButton4_6);
                            t_name = (TextView)findViewById(R.id.textView_showtext_6);
                            rb1.setText(exam6.getString(TAG_OPA));
                            rb2.setText(exam6.getString(TAG_OPB));
                            rb3.setText(exam6.getString(TAG_OPC));
                            rb4.setText(exam6.getString(TAG_OPD));
                            t_name.setText(exam6.getString(TAG_NAME));
                            exam_answer6 = exam6.getString("answer");
                            id6 = exam6.getString(TAG_PID);
                            exam_desc6 = exam6.getString(TAG_DESC);

                            JSONObject exam7 = products.getJSONObject(random_array[6]);
                            rb1 = (RadioButton) findViewById(R.id.radioButton_7);
                            rb2 = (RadioButton) findViewById(R.id.radioButton2_7);
                            rb3 = (RadioButton) findViewById(R.id.radioButton3_7);
                            rb4 = (RadioButton) findViewById(R.id.radioButton4_7);
                            t_name = (TextView)findViewById(R.id.textView_showtext_7);
                            rb1.setText(exam7.getString(TAG_OPA));
                            rb2.setText(exam7.getString(TAG_OPB));
                            rb3.setText(exam7.getString(TAG_OPC));
                            rb4.setText(exam7.getString(TAG_OPD));
                            t_name.setText(exam7.getString(TAG_NAME));
                            exam_answer7 = exam7.getString("answer");
                            id7 = exam7.getString(TAG_PID);
                            exam_desc7 = exam7.getString(TAG_DESC);

                            JSONObject exam8 = products.getJSONObject(random_array[7]);
                            rb1 = (RadioButton) findViewById(R.id.radioButton_8);
                            rb2 = (RadioButton) findViewById(R.id.radioButton2_8);
                            rb3 = (RadioButton) findViewById(R.id.radioButton3_8);
                            rb4 = (RadioButton) findViewById(R.id.radioButton4_8);
                            t_name = (TextView)findViewById(R.id.textView_showtext_8);
                            rb1.setText(exam8.getString(TAG_OPA));
                            rb2.setText(exam8.getString(TAG_OPB));
                            rb3.setText(exam8.getString(TAG_OPC));
                            rb4.setText(exam8.getString(TAG_OPD));
                            t_name.setText(exam8.getString(TAG_NAME));
                            exam_answer8 = exam8.getString("answer");
                            id8 = exam8.getString(TAG_PID);
                            exam_desc8 = exam8.getString(TAG_DESC);

                            JSONObject exam9 = products.getJSONObject(random_array[8]);
                            rb1 = (RadioButton) findViewById(R.id.radioButton_9);
                            rb2 = (RadioButton) findViewById(R.id.radioButton2_9);
                            rb3 = (RadioButton) findViewById(R.id.radioButton3_9);
                            rb4 = (RadioButton) findViewById(R.id.radioButton4_9);
                            t_name = (TextView)findViewById(R.id.textView_showtext_9);
                            rb1.setText(exam9.getString(TAG_OPA));
                            rb2.setText(exam9.getString(TAG_OPB));
                            rb3.setText(exam9.getString(TAG_OPC));
                            rb4.setText(exam9.getString(TAG_OPD));
                            t_name.setText(exam9.getString(TAG_NAME));
                            exam_answer9 = exam9.getString("answer");
                            id9 = exam9.getString(TAG_PID);
                            exam_desc9 = exam9.getString(TAG_DESC);

                            JSONObject exam10 = products.getJSONObject(random_array[9]);
                            rb1 = (RadioButton) findViewById(R.id.radioButton_10);
                            rb2 = (RadioButton) findViewById(R.id.radioButton2_10);
                            rb3 = (RadioButton) findViewById(R.id.radioButton3_10);
                            rb4 = (RadioButton) findViewById(R.id.radioButton4_10);
                            t_name = (TextView)findViewById(R.id.textView_showtext_10);
                            rb1.setText(exam10.getString(TAG_OPA));
                            rb2.setText(exam10.getString(TAG_OPB));
                            rb3.setText(exam10.getString(TAG_OPC));
                            rb4.setText(exam10.getString(TAG_OPD));
                            t_name.setText(exam10.getString(TAG_NAME));
                            exam_answer10 = exam10.getString("answer");
                            id10 = exam10.getString(TAG_PID);
                            exam_desc10 = exam10.getString(TAG_DESC);



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
            // dismiss the dialog after getting all products
            pDialog.dismiss();

        }

    }

    class CreateMylist extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ExamGoActivity.this);
            pDialog.setMessage("請稍候..");
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
         */
        protected String doInBackground(String... args) {

            String wrongExams = args[0];

            // Building Parameters
            List<NameValuePair> params1 = new ArrayList<NameValuePair>();
            params1.add(new BasicNameValuePair("wrongExams",wrongExams));
            Log.d("wrongExams" , wrongExams);



            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_create_mylist,
                    "POST", params1);
            // check log cat fro response

            try {
                int success = json.getInt(TAG_SUCCESS);

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
         **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
        }
    }


        @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Intent myIntent = new Intent();
            myIntent = new Intent(ExamGoActivity.this, ExamActivity.class);
            startActivity(myIntent);
            this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }


}
