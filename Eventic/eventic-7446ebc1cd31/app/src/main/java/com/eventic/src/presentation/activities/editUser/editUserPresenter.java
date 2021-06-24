package com.eventic.src.presentation.activities.editUser;

import android.content.SharedPreferences;

import com.eventic.src.domain.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class editUserPresenter implements editUserContract.Presenter  {

    private editUserContract.View mView;

    public editUserPresenter(editUserContract.View view) {
        mView = view;
    }


    public void changeInfo(Integer id, String name, String login_token, String emailtext, String newPassword, String usernameText) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://eventic-api.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        editUserContract.JsonHttpApi jsonHttpApi = retrofit.create(editUserContract.JsonHttpApi.class);

        Call<User> call = jsonHttpApi.changeInfoUser(id, name, login_token, emailtext, newPassword, usernameText);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                if (!response.isSuccessful()) {
                    System.out.println(response.message());

                }
                mView.signOut();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
            }
        });

    }

    public void checkPassActual(String emailusu, String passwordactual, Integer id, String name, String emailtext, String newPassword, String usernameText){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://eventic-api.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        editUserContract.JsonHttpApi jsonHttpApi = retrofit.create(editUserContract.JsonHttpApi.class);

        Call<User> call = jsonHttpApi.checkPassActual(emailusu, passwordactual);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User userResponse = response.body();
                if (!response.isSuccessful()) {
                    mView.failedLogin();
                    return;

                }

                SharedPreferences userPreferences = mView.getUserPreferences();
                userPreferences.edit().putString("token", userResponse.getLogin_token()).apply();
                userPreferences.edit().putString("role", userResponse.getRole()).apply();
                userPreferences.edit().putString("email", userResponse.getEmail()).apply();
                userPreferences.edit().putString("password", userResponse.getPassword()).apply();
                userPreferences.edit().putInt("id", userResponse.getId()).apply();
                userPreferences.edit().putString("username", userResponse.getUsername()).apply();

                changeInfo(id,  name,  userPreferences.getString("token",null),  emailtext,  newPassword,  usernameText);

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
            }
        });


    }


}
