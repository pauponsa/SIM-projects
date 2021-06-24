package com.eventic.src.presentation.activities.register.user;

import android.graphics.Bitmap;
import android.media.Image;

import com.eventic.src.domain.User;
import com.example.eventic.R;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class UserRegisterPresenter implements UserRegisterContract.Presenter {

    private UserRegisterContract.View mView;

    public UserRegisterPresenter(UserRegisterContract.View view) {
        mView = view;
    }


    @Override
    public void signIn() {
        mView.changeToLogin();
    }

    @Override
    public void register(String name, String username, String email, String password, String repeatPassword, String role, boolean terms) throws IOException {
        if (name.isEmpty()) mView.showDialog(mView.getStringById(R.string.missing_parameters), mView.getStringById(R.string.missing_name));
        else if (username.isEmpty()) mView.showDialog(mView.getStringById(R.string.missing_parameters), mView.getStringById(R.string.missing_username));
        else if (email.isEmpty()) mView.showDialog(mView.getStringById(R.string.missing_parameters), mView.getStringById(R.string.missing_email));
        else if (password.isEmpty()) mView.showDialog(mView.getStringById(R.string.missing_parameters), mView.getStringById(R.string.missing_password));
        else if (repeatPassword.isEmpty()) mView.showDialog(mView.getStringById(R.string.missing_parameters), mView.getStringById(R.string.missing_repeat_password));
        else if (!terms) mView.showDialog(mView.getStringById(R.string.missing_parameters), mView.getStringById(R.string.missing_terms));
        else if (!password.equals(repeatPassword)) mView.showToast(mView.getStringById(R.string.passwords_not_equals));
        else registerUser(name, username, email, password, role);
    }

    public void registerUser(String name, String username, String email, String password, String role) throws IOException {
        mView.setLoading(true);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://eventic-api.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        UserRegisterContract.JsonHttpApi jsonHttpApi = retrofit.create(UserRegisterContract.JsonHttpApi.class);


        final User user = new User(name, username, email, password, password, null, role);

        Call<User> call = jsonHttpApi.registerUser(user);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User userResponse = response.body();

                if (!response.isSuccessful()) {
                    mView.setLoading(false);
                    mView.showToast(response.message());
                    System.out.println(response.message());
                    return;
                }
                //mView.changeToLogin();
                mView.closeAndLogin(email, password);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                mView.setLoading(false);
                mView.failedConnection();
            }
        });

    }

}