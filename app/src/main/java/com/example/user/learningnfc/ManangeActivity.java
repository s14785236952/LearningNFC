package com.example.user.learningnfc;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import android.widget.Toast;
public class ManangeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    Button btnViewProducts;
    Button btnNewProduct;
    Button btnHomeWork;
    Button btnViewHomeWork;
    Button btnCreateNFC;
    Button btnViewNFC;
    Button btnManageLearn;
    Button btnStudentsHistory;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manange);
        context = this;

        if(MainActivity.privilege == 1){
            new AlertDialog.Builder(ManangeActivity.this)
                    .setTitle("你沒有權限進入這裡！")
                    .setMessage("將回到主畫面！")
                    .setIcon(R.mipmap.ic_launcher)
                    .setPositiveButton("確定",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    Intent i = new Intent();
                                    i.setClass(ManangeActivity.this , MainActivity.class);
                                    startActivity(i);
                                    finish();
                                }
                            })
                   .show();

        }
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

// Buttons
        btnViewProducts = (Button) findViewById(R.id.btnViewProducts);
        btnNewProduct = (Button) findViewById(R.id.btnCreateProduct);
        btnHomeWork = (Button)findViewById(R.id.btnHomework);
        btnViewHomeWork = (Button)findViewById(R.id.btnViewHomework);
        btnCreateNFC = (Button)findViewById(R.id.btnCreateNFC);
        btnViewNFC = (Button)findViewById(R.id.btnViewNFC);
        btnManageLearn = (Button)findViewById(R.id.btnManageLearn);
        btnStudentsHistory = (Button)findViewById(R.id.btnStudentsHistory);

        // view products click event
        btnViewProducts.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Launching All products Activity
                Intent i = new Intent(getApplicationContext(), AllExamsActivity.class);
                startActivity(i);

            }
        });

        // new products click event
        btnNewProduct.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Launching create new product activity
                Intent i = new Intent(getApplicationContext(), NewProductActivity.class);
                startActivity(i);

            }
        });

        btnViewNFC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), AllNFCActivity.class);
                startActivity(i);
            }
        });

        btnCreateNFC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), NewNFCActivity.class);
                startActivity(i);
            }
        });

        // view Homeworks click event
        btnHomeWork.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Launching HomeWork Activity
                Intent i = new Intent(getApplicationContext(), NewHomeworkActivity.class);
                startActivity(i);

            }
        });

        // view Homeworks click event
        btnViewHomeWork.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Launching HomeWork Activity
                Intent i = new Intent(getApplicationContext(), AllHomeworksActivity.class);
                startActivity(i);

            }
        });

        btnManageLearn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ManageLearnActivity.class);
                startActivity(i);

            }
        });

        btnStudentsHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), StudentsHistoryActivity.class);
                startActivity(i);
            }
        });
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
        getMenuInflater().inflate(R.menu.manange, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_Learning) {
            Intent intent = new Intent();
            intent.setClass(ManangeActivity.this, LearnActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_homework) {
            Intent intent = new Intent();
            intent.setClass(ManangeActivity.this, HomeworkActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_exam) {
            Intent intent = new Intent();
            intent.setClass(ManangeActivity.this, ExamActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_home) {
            Intent intent = new Intent();
            intent.setClass(ManangeActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_history) {
            Intent intent = new Intent();
            intent.setClass(ManangeActivity.this, OthersActivity.class);
            startActivity(intent);
            finish();
        }else if (id == R.id.nav_logout) {
            logout();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub

        if (keyCode == KeyEvent.KEYCODE_BACK) { // 攔截返回鍵
            new AlertDialog.Builder(ManangeActivity.this)
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
        Intent login = new Intent(ManangeActivity.this, WelcomeActivity.class);
        startActivity(login);
        finish();
    }
}
