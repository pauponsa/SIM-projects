package com.eventic.src.presentation.activities.settings;


import android.content.SharedPreferences;

import com.eventic.src.domain.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SettingsContract {
    interface View {
        void finishActivity();

        void setLoading(boolean loading);

        SharedPreferences getUserPreferences();

        void changeToLogin();

        void changeToEditUser(String name, String username, String email);

        void signOut();

        void popup();

        void reportcovidpopup();

        void deleteAccountpopup();
    }

    interface Presenter {
        void changePassword();

        void editProfile();

        void signOut();

        void deleteAccount();

        void reportcovid();
    }

    interface JsonHttpApi {
        @GET("users/{id}")
        Call<User> getUser(@Path("id") Integer id);

        @GET("aviscas")
        Call<Void> reportcovid(@Query("id") Integer id);
    }
}
