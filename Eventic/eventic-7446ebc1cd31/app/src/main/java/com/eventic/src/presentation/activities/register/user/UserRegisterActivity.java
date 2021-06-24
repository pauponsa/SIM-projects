package com.eventic.src.presentation.activities.register.user;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;

import android.content.DialogInterface;
import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.eventic.src.presentation.activities.login.LoginActivity;

import com.example.eventic.R;


import java.io.IOException;

public class UserRegisterActivity extends AppCompatActivity implements UserRegisterContract.View, View.OnClickListener {
    private UserRegisterContract.Presenter mPresenter;
    private TextView signIn;
    private EditText fullName;
    private EditText username;
    private EditText email;
    private EditText password;
    private EditText repeatPassword;
    private Button continueButton;
    private CheckBox terms;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);
        initViews();
        mPresenter = new UserRegisterPresenter(this);
    }

    private void initViews() {
        continueButton = findViewById(R.id.Continue);
        continueButton.setOnClickListener(this);
        fullName = findViewById(R.id.firstName);
        username = findViewById(R.id.username);
        email = findViewById(R.id.emailSignUp);
        password = findViewById(R.id.actualPassword);
        repeatPassword= findViewById(R.id.RepeatNewPassword);
        signIn = findViewById(R.id.SignIn);
        signIn.setOnClickListener(this);
        terms = findViewById(R.id.userRegisterTerms);
        progressBar = findViewById(R.id.userRegisterProgressBar);
        // Add any new view to setLoading() method to prevent spamming while loading
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.SignIn) mPresenter.signIn();
        else if (v.getId() == R.id.Continue) {
            String n = fullName.getText().toString();
            String u = username.getText().toString();
            String e = email.getText().toString();
            String p = password.getText().toString();
            String r = repeatPassword.getText().toString();
            boolean t = terms.isChecked();
            try {
                mPresenter.register(n, u, e, p, r, "customer", t);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }


    @Override
    public void changeToLogin() {
        Intent intent = new Intent(UserRegisterActivity.this, LoginActivity.class);
        this.startActivity(intent);
        
    }

    public void closeAndLogin(String email, String password) {
        Intent intent = new Intent(UserRegisterActivity.this, LoginActivity.class);
        intent.putExtra("email", email);
        intent.putExtra("password", password);
        this.startActivity(intent);
        this.finish();
    }

    @Override
    public void showToast(String message) {

        PopupWindow popupWindow = new PopupWindow();
        popupWindow.showAsDropDown(findViewById(R.id.registerView));

        Toast toast = new Toast(this);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setText(message);
        toast.show();
        //Toast.makeText(this, getResources().getText(R.string.login_error), 5).show();
    }

    @Override
    public void showDialog(String title, String description)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle(title);
        alertDialogBuilder
                .setMessage(description)
                .setNeutralButton(getText(R.string.accept), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    @Override
    public void setLoading(boolean loading) {
        if (loading) progressBar.setVisibility(ProgressBar.VISIBLE);
        else progressBar.setVisibility(ProgressBar.INVISIBLE);

        continueButton.setEnabled(!loading);
        fullName.setEnabled(!loading);
        username.setEnabled(!loading);
        email.setEnabled(!loading);
        password.setEnabled(!loading);
        repeatPassword.setEnabled(!loading);
        signIn.setEnabled(!loading);
    }

    public void failedConnection() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle(getText(R.string.connection_error));
        alertDialogBuilder
                .setMessage(getText(R.string.connection_error_msg))
                .setCancelable(true);

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    @Override
    public String getStringById(int id)
    {
        return getString(id);
    }

}