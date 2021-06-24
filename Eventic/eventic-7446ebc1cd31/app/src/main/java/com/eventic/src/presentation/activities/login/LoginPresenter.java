package com.eventic.src.presentation.activities.login;

import android.content.SharedPreferences;

import com.eventic.src.domain.Customer;
import com.eventic.src.domain.User;
import com.eventic.src.presentation.activities.register.user.UserRegisterContract;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginPresenter implements LoginContract.Presenter {
    private LoginContract.View mView;
    private User user;
    private boolean userExists = false;

    public LoginPresenter(LoginContract.View view) {
        mView = view;
    }

    @Override
    public void userSignUp() {
        mView.changeToUserSignUp();
    }

    @Override
    public void companySignUp() {
        mView.changeToCompanySignUp();
    }

    @Override
    public void changeToUserHomePage() {
        mView.changeToUserHomepage();
    }

    @Override
    public void changeToCompanyHomePage() {
        mView.changeToCompanyHomepage();
    }

    @Override
    public void userForgotText() { mView.sendVerificationEmail(); }


    public void checkLoginGoogle(String email, String password) {
        user = new User(email, password);
        mView.setLoading(true);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://eventic-api.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        LoginContract.JsonHttpApi jsonHttpApi = retrofit.create(LoginContract.JsonHttpApi.class);


        Call<User> call = jsonHttpApi.checkLogin(user);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User userResponse = response.body();
                //user.setId(userResponse.getId());
                mView.setLoading(false);

                if (!response.isSuccessful()) {
                    mView.failedLoginGoogle();
                    return;
                }
                if(userResponse.getRole().equals("company")) mView.changeToCompanyHomepage();
                else mView.changeToUserHomepage();
                //getUserInfo();

                SharedPreferences userPreferences = mView.getUserPreferences();
                userPreferences.edit().putString("token", userResponse.getLogin_token()).apply();
                userPreferences.edit().putString("role", userResponse.getRole()).apply();
                userPreferences.edit().putString("email", userResponse.getEmail()).apply();
                userPreferences.edit().putString("password", userResponse.getPassword()).apply();
                userPreferences.edit().putInt("id", userResponse.getId()).apply();
                userPreferences.edit().putString("username", userResponse.getUsername()).apply();
                userPreferences.edit().putString("name", userResponse.getName()).apply();

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                mView.setLoading(false);
                mView.failedConnection();
            }
        });
    }

    public void checkLogin(String email, String password) {
        user = new User(email, password);
        mView.setLoading(true);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://eventic-api.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        LoginContract.JsonHttpApi jsonHttpApi = retrofit.create(LoginContract.JsonHttpApi.class);


        Call<User> call = jsonHttpApi.checkLogin(user);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User userResponse = response.body();
                //user.setId(userResponse.getId());
                mView.setLoading(false);

                if (!response.isSuccessful()) {
                    mView.failedLogin();
                    return;
                }
                if(userResponse.getRole().equals("company")) mView.changeToCompanyHomepage();
                else mView.changeToUserHomepage();
                //getUserInfo();

                SharedPreferences userPreferences = mView.getUserPreferences();
                userPreferences.edit().putString("token", userResponse.getLogin_token()).apply();
                userPreferences.edit().putString("role", userResponse.getRole()).apply();
                userPreferences.edit().putString("email", userResponse.getEmail()).apply();
                userPreferences.edit().putString("password", userResponse.getPassword()).apply();
                userPreferences.edit().putInt("id", userResponse.getId()).apply();
                userPreferences.edit().putString("username", userResponse.getUsername()).apply();
                userPreferences.edit().putString("name", userResponse.getName()).apply();

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                mView.setLoading(false);
                mView.failedConnection();
            }
        });
    }

    public void registerUserGoogle(String name, String username, String email, String password, String role) {
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
                mView.setLoading(false);
                User userResponse = response.body();

                if (!response.isSuccessful()) {
                    System.out.println(response.message());
                    return;
                }
                System.out.println("REGISTER SUCCESSFUL");
                checkLoginGoogle(userResponse.getEmail(), "pablomotoso69");
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                System.out.println(t.getMessage());
                mView.setLoading(false);
                mView.failedConnection();
            }
        });

    }

    @Override
    public void userExists(GoogleSignInAccount account) {
        mView.setLoading(true);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://eventic-api.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        LoginContract.JsonHttpApi jsonHttpApi = retrofit.create(LoginContract.JsonHttpApi.class);


        Call<List<Customer>> call = jsonHttpApi.getUsers();

        call.enqueue(new Callback<List<Customer>>() {
            @Override
            public void onResponse(Call<List<Customer>> call, Response<List<Customer>> response) {
                mView.setLoading(false);
                List<Customer> userResponse = response.body();

                if (!response.isSuccessful()) {
                    System.out.println(response.message());
                    return;
                }
                for(Customer u: userResponse) {
                    if (u.getEmail().equals(account.getEmail())) {
                        userExists = true;
                        break;
                    }
                }
                try {
                    mView.setUserExists(userExists, account);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<List<Customer>> call, Throwable t) {
                System.out.println(t.getMessage());
                mView.setLoading(false);
                mView.failedConnection();
            }
        });
    }



}
