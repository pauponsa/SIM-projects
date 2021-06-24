package com.eventic.src.presentation.activities.editUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.eventic.src.domain.User;
import com.eventic.src.presentation.activities.login.LoginActivity;
import com.eventic.src.presentation.activities.login.LoginContract;
import com.eventic.src.presentation.activities.register.user.UserRegisterContract;
import com.eventic.src.presentation.activities.register.user.UserRegisterPresenter;
import com.eventic.src.presentation.activities.userHomePage.userHomePageActivity;
import com.example.eventic.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class editUserActivity extends AppCompatActivity implements editUserContract.View, View.OnClickListener{

    private editUserContract.Presenter mPresenter;

    private EditText actualPassword;
    private EditText newPassword;
    private EditText repeatPassword;
    private Button continueButton;

    private Boolean bool = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);
        initViews();
        mPresenter = new editUserPresenter(this);
    }

    public SharedPreferences getUserPreferences() {
        return getSharedPreferences("userPreferences", this.MODE_PRIVATE);
    }



    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.Continue) {
            SharedPreferences userPreferences = getUserPreferences();
            if (newPassword.getText().toString().equals(repeatPassword.getText().toString())){
                Integer id = userPreferences.getInt("id",0);
                String emailusu = userPreferences.getString("email",null);
                String fullNameText = userPreferences.getString("name", null);
                String username = userPreferences.getString("username", null);
                mPresenter.checkPassActual(emailusu,actualPassword.getText().toString(),id, fullNameText, emailusu, newPassword.getText().toString(), username);

            }
            else failedLogin();
        }
    }



    private void initViews() {
        continueButton = findViewById(R.id.Continue);
        continueButton.setOnClickListener(this);
        newPassword = findViewById(R.id.newPassword);
        actualPassword = findViewById(R.id.actualPassword);
        repeatPassword = findViewById(R.id.RepeatNewPassword);

        Intent intent = getIntent();



    }

    public void failedLogin() {
       /*
        PopupWindow popupWindow = new PopupWindow();
        popupWindow.showAsDropDown(findViewById(R.id.loginView));

        Toast toast = new Toast(this);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setText( getResources().getText(R.string.login_error));
        toast.show();
        //Toast.makeText(this, getResources().getText(R.string.login_error), 5).show();
        */
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle(getText(R.string.wrong_parameters));
        alertDialogBuilder
                .setMessage(getText(R.string.email_password_incorrect))
                .setCancelable(true);

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    @Override
    public void signOut() {
        SharedPreferences userPreferences = getUserPreferences();
        userPreferences.edit().remove("token").apply();
        userPreferences.edit().remove("role").apply();
        userPreferences.edit().remove("id").apply();


        Intent intent = new Intent(editUserActivity.this, LoginActivity.class);
        this.startActivity(intent);
        this.finish();
    }


}