package com.eventic.src.presentation.activities.login;

import android.content.SharedPreferences;

import com.eventic.src.domain.Customer;
import com.eventic.src.domain.User;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface LoginContract {

    /** Represents the View in MVP. */
    interface View {

        void clearInputs();

        void setLoading(boolean loading);

        void changeToUserHomepage();

        void changeToUserSignUp();

        void changeToCompanySignUp();

        void failedLogin();

        void failedLoginGoogle();

        void failedConnection();

        SharedPreferences getUserPreferences();

        void changeToCompanyHomepage();

        void finishActivity();

        void sendVerificationEmail();

        void setUserExists(boolean userExists, GoogleSignInAccount account) throws IOException;
    }

    /** Represents the Presenter in MVP. */
    interface Presenter {

        //void checkLogin(String email, String password, String role);

        void checkLogin(String email, String password);

        void userSignUp();

        void companySignUp();

        void changeToUserHomePage();

        void changeToCompanyHomePage();

        void userForgotText();

        void registerUserGoogle(String name, String username, String email, String password, String role) throws IOException;

        void userExists(GoogleSignInAccount email);

        void checkLoginGoogle(String email, String pablomotoso69);
    }


    interface JsonHttpApi {
        @POST("login")
        Call<User> checkLogin(@Body User user);

        @GET("users")
        Call<List<Customer>> getUsers();
    }
}
