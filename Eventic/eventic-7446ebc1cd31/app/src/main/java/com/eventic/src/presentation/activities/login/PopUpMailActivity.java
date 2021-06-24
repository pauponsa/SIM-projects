package com.eventic.src.presentation.activities.login;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.eventic.src.domain.Event;
import com.eventic.src.domain.User;
import com.eventic.src.presentation.activities.companyHomePage.companyHomePageActivity;
import com.eventic.src.presentation.activities.companyHomePage.companyHomePageContract;
import com.eventic.src.presentation.activities.userHomePage.userHomePageActivity;

import com.eventic.src.presentation.activities.register.company.CompanyRegisterActivity;
import com.eventic.src.presentation.activities.register.user.UserRegisterActivity;
import com.example.eventic.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class PopUpMailActivity extends AppCompatActivity implements  View.OnClickListener  {

    private Button send;
    private EditText email;


    interface JsonHttpApi {
        @GET("edit")
        Call<Void> sendMail(@Query("email") String email);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up_mail);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(dm);
        initViews();
        int w = dm.widthPixels;
        int h = dm.heightPixels;

        getWindow().setLayout((int)(w*.8),(int)(h*.33));

        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.gravity = Gravity.CENTER;
        p.x = 0;
        p.y = -20;
        getWindow().setAttributes(p);


    }
    private void initViews() {
        send = findViewById(R.id.buttonSend);
        email = findViewById(R.id.editTextEmail);
        send.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonSend) {
            sendEmail(email.getText().toString());
        }
    }

    private void sendEmail(String email) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://eventic-api.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        PopUpMailActivity.JsonHttpApi jsonHttpApi = retrofit.create(PopUpMailActivity.JsonHttpApi.class);


        Call<Void> call = jsonHttpApi.sendMail(email);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if (!response.isSuccessful()) {
                    System.out.println(response.message());
                    return;
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                //mView.getCompanyEventsFragment().setLoading(false);
                System.out.println("Connection FAILED");
            }
        });
    }
}