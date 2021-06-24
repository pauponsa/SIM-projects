package com.eventic.src.presentation.activities.register.company;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.eventic.src.presentation.activities.login.LoginActivity;
import com.eventic.src.presentation.activities.register.user.UserRegisterActivity;
import com.example.eventic.R;
import com.google.android.material.imageview.ShapeableImageView;

public class CompanyRegisterActivity extends AppCompatActivity implements CompanyRegisterContract.View, View.OnClickListener {
    private CompanyRegisterContract.Presenter mPresenter;
    private TextView signIn;
    private EditText companyName;
    private EditText companyUsername;
    private EditText email;
    private EditText password;
    private EditText repeatPassword;
    private Button continueButton;
    private CheckBox terms;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_register);
        initViews();
        mPresenter = new CompanyRegisterPresenter(this);
    }

    private void initViews() {
        progressBar = findViewById(R.id.companyRegisterProgressBar);
        continueButton = findViewById(R.id.Continue);
        continueButton.setOnClickListener(this);
        companyName = findViewById(R.id.companyName);
        companyUsername = findViewById(R.id.companyUsername);
        email = findViewById(R.id.emailSignUp);
        password = findViewById(R.id.actualPassword);
        repeatPassword = findViewById(R.id.RepeatNewPassword);
        terms = findViewById(R.id.companyRegisterTerms);
        signIn = findViewById(R.id.SignIn);
        signIn.setOnClickListener(this);
        // Add any new view to setLoading() method to prevent spamming while loading
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.SignIn) mPresenter.signIn();
        else if(v.getId() == R.id.Continue){
            String n = companyName.getText().toString();
            String u = companyUsername.getText().toString();
            String e = email.getText().toString();
            String p = password.getText().toString();
            String r = repeatPassword.getText().toString();
            boolean t = terms.isChecked();
            mPresenter.register(n,u,e,p,r,"company", t);
        }
    }

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

    public void closeAndLogin(String email, String password) {
        Intent intent = new Intent(CompanyRegisterActivity.this, LoginActivity.class);
        intent.putExtra("email", email);
        intent.putExtra("password", password);
        this.startActivity(intent);
        this.finish();
    }

    @Override
    public void changeToLogin() {
        Intent intent = new Intent(CompanyRegisterActivity.this, LoginActivity.class);
        this.startActivity(intent);
    }

    @Override
    public void showToast(String message) {
        PopupWindow popupWindow = new PopupWindow();
        popupWindow.showAsDropDown(findViewById(R.id.registerView));

        Toast toast = new Toast(this);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setText(message);
        toast.show();
    }

    public String getStringById(int id)
    {
        return getString(id);
    }

    @Override
    public void setLoading(boolean loading) {
        if (loading)
            progressBar.setVisibility(ProgressBar.VISIBLE);
        else
            progressBar.setVisibility(ProgressBar.INVISIBLE);

        continueButton.setEnabled(!loading);
        companyName.setEnabled(!loading);
        companyUsername.setEnabled(!loading);
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
}