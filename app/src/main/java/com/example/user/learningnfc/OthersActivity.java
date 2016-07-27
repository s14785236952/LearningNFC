package com.example.user.learningnfc;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

import com.facebook.login.LoginManager;

public class OthersActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    Button btnViewHistory,btnRankLearn,btnRankScore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_others);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        btnViewHistory = (Button)findViewById(R.id.btnViewHistory);
        btnViewHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(OthersActivity.this, ViewHistoryActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnRankScore = (Button)findViewById(R.id.btnRankScore);
        btnRankScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(OthersActivity.this, RankScoreActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnRankLearn =(Button)findViewById(R.id.btnRankLearn);
        btnRankLearn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(OthersActivity.this, RankLearnActivity.class);
                startActivity(intent);
                finish();
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
        getMenuInflater().inflate(R.menu.others, menu);
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
            intent.setClass(OthersActivity.this, LearnActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_homework) {
            Intent intent = new Intent();
            intent.setClass(OthersActivity.this, HomeworkActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_exam) {
            Intent intent = new Intent();
            intent.setClass(OthersActivity.this, ExamActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_manage) {
            Intent intent = new Intent();
            intent.setClass(OthersActivity.this, ManangeActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_home) {
            Intent intent = new Intent();
            intent.setClass(OthersActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_history) {

        }else if (id == R.id.nav_logout) {
            logout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void logout() {
        LoginManager.getInstance().logOut();
        Intent login = new Intent(OthersActivity.this, WelcomeActivity.class);
        startActivity(login);
        finish();
    }
}
