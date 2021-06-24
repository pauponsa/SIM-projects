package com.eventic.src.presentation.activities.settings;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.eventic.src.domain.User;
import com.eventic.src.presentation.activities.companyHomePage.companyHomePageActivity;
import com.eventic.src.presentation.activities.editUser.editUserActivity;
import com.eventic.src.presentation.activities.login.LoginActivity;
import com.eventic.src.presentation.fragments.UserProfileFragment;
import com.example.eventic.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class SettingsActivity extends AppCompatActivity implements SettingsContract.View, View.OnClickListener {

    private SettingsContract.Presenter mPresenter;
    private CardView signOut;
    private CardView deleteAccount;
    private CardView editUser;
    private CardView covid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initViews();

    }

    private void initViews() {

        mPresenter = new SettingsPresenter(this);
        signOut = findViewById(R.id.userProfileSignOut);
        signOut.setOnClickListener(this);
        editUser = findViewById(R.id.userEditProfile);
        editUser.setOnClickListener(this);
        deleteAccount = findViewById(R.id.userDeleteAccount);
        deleteAccount.setOnClickListener(this);
        SharedPreferences userP = getUserPreferences();
        String r = userP.getString("role",null);
        covid = findViewById(R.id.reportcovidcase);
        covid.setOnClickListener(this);
        if(r.equals("company")){
            covid.setVisibility(CardView.GONE);
        }
        if (r.equals("google"))
            editUser.setVisibility(CardView.GONE);
    }


    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

    }

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.userEditProfile){

            mPresenter.editProfile();

        }
        else if(v.getId() == R.id.userProfileSignOut){
            mPresenter.signOut();
        }
        else if(v.getId() == R.id.userDeleteAccount){
            deleteAccountpopup();

        }
        else if(v.getId() == R.id.reportcovidcase){
            reportcovidpopup();
        }


    }

    @Override
    public void popup() {
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setMessage(R.string.report_successful).setNeutralButton(R.string.accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    public void reportcovidpopup() {
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setMessage(R.string.report_covid_notice).setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mPresenter.reportcovid();
                dialog.dismiss();
            }
        });
        builder.setMessage(R.string.report_covid_notice).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    public void deleteAccountpopup() {
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setMessage(R.string.remove_account_notice).setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mPresenter.deleteAccount();
                dialog.dismiss();
            }
        });
        builder.setMessage(R.string.remove_account_notice).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    public void finishActivity() {
        this.finish();
    }


    @Override
    public void setLoading(boolean loading) {
        //if (loading) progressBar.setVisibility(ProgressBar.VISIBLE);
        //else progressBar.setVisibility(ProgressBar.INVISIBLE);
    }


    public SharedPreferences getUserPreferences() {
        return getSharedPreferences("userPreferences", this.MODE_PRIVATE);
    }

    @Override
    public void changeToLogin() {
        SharedPreferences userPreferences = getUserPreferences();
        userPreferences.edit().remove("token").apply();
        userPreferences.edit().remove("role").apply();
        userPreferences.edit().remove("id").apply();

        Toast.makeText(SettingsActivity.this, getText(R.string.delete_account_message), Toast.LENGTH_LONG).show();

        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        this.startActivity(intent);
        finishActivity();
    }

    @Override
    public void changeToEditUser(String name, String username, String email) {
        Intent intent = new Intent(this, editUserActivity.class);
        intent.putExtra("name", name);
        intent.putExtra("username", username);
        intent.putExtra("email", email);
        startActivity(intent);
    }

    @Override
    public void signOut() {
        SharedPreferences userPreferences = getUserPreferences();
        userPreferences.edit().remove("token").apply();
        userPreferences.edit().remove("role").apply();
        userPreferences.edit().remove("id").apply();

        Toast.makeText(SettingsActivity.this, getText(R.string.successful_signed_out), Toast.LENGTH_LONG).show();

        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        this.startActivity(intent);
        finishActivity();
    }

    public void getUser(Integer userid){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://eventic-api.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        SettingsContract.JsonHttpApi jsonHttpApi = retrofit.create(SettingsContract.JsonHttpApi.class);

        Call<User> call = jsonHttpApi.getUser(userid);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User respUser = response.body();
                if (!response.isSuccessful()) {
                    System.out.println(response.message());

                }
                Intent intent = new Intent(SettingsActivity.this, editUserActivity.class);
                intent.putExtra("name", respUser.getName());
                intent.putExtra("username", respUser.getUsername());
                intent.putExtra("email", respUser.getEmail());
                startActivity(intent);
            }



            @Override
            public void onFailure(Call<User> call, Throwable t) {
            }
        });
    }
}