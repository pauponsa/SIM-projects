package com.eventic.src.presentation.activities.settings;


import android.content.Intent;
import android.content.SharedPreferences;

import com.eventic.src.domain.User;
import com.eventic.src.presentation.activities.editUser.editUserActivity;
import com.eventic.src.presentation.activities.userHomePage.userHomePageActivity;
import com.eventic.src.presentation.activities.userHomePage.userHomePageContract;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SettingsPresenter implements SettingsContract.Presenter{
    private SettingsContract.View mView;


    public SettingsPresenter(SettingsContract.View view) {
        mView = view;
    }


    @Override
    public void reportcovid() {

        Integer userid = mView.getUserPreferences().getInt("id",0);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://eventic-api.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        SettingsContract.JsonHttpApi jsonHttpApi = retrofit.create(SettingsContract.JsonHttpApi.class);

        Call<Void> call = jsonHttpApi.reportcovid(userid);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if (!response.isSuccessful()) {
                    System.out.println(response.message());
                }
                mView.popup();

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

                System.out.println(call);
                System.out.println(t);
            }
        });
    }

    @Override
    public void changePassword() {

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
                mView.changeToEditUser(respUser.getName(), respUser.getUsername(), respUser.getEmail());
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

               System.out.println(call);
                System.out.println(t);
            }
        });
    }

    @Override
    public void editProfile() {
        SharedPreferences userPreferences = (mView.getUserPreferences());


        mView.changeToEditUser(userPreferences.getString("name",null),userPreferences.getString("username",null),userPreferences.getString("email",null));


    }

    @Override
    public void signOut() {
        mView.signOut();
    }

    @Override
    public void deleteAccount() {
        Integer id = mView.getUserPreferences().getInt("id",0);
        String token = mView.getUserPreferences().getString("token",null);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://eventic-api.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        userHomePageContract.JsonHttpApi jsonHttpApi = retrofit.create(userHomePageContract.JsonHttpApi.class);

        Call<Void> call = jsonHttpApi.deleteAccount(id,token);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if (!response.isSuccessful()) {
                    System.out.println(response.message());
                    return;
                }
                System.out.println("ON RESPONSE");
                mView.changeToLogin();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                System.out.println("ON FAILURE");
            }
        });

    }
}
