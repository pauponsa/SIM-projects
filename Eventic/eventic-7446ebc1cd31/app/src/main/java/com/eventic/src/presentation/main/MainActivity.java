package com.eventic.src.presentation.main;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.eventic.src.presentation.activities.login.LoginActivity;
import com.eventic.src.presentation.activities.userHomePage.userHomePageActivity;
import com.example.eventic.R;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity implements MainContract.View {
    private MainContract.Presenter mPresenter;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        timer = new Timer();
        mPresenter = new MainPresenter(this);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                mPresenter.viewCreated();
                mPresenter.dontReturn();
            }
        }, 500);
    }



    @Override
    public void changeToSignIn() {
        if(mPresenter.checkIfSignedIn()) {
            Intent intent = new Intent(MainActivity.this, userHomePageActivity.class);
            startActivity(intent);
        }
        else {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void finishActivity() {
        this.finish();
    }

}

