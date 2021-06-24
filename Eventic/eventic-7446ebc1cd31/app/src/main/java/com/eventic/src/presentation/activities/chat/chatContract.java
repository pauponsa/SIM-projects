package com.eventic.src.presentation.activities.chat;

import android.content.SharedPreferences;

import com.eventic.src.domain.Event;
import com.eventic.src.domain.User;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface chatContract {
    interface View {
        void setupMessages();
        void setChatTitle(String title);
        void setChatCreatorName(String name);
        void setUserProfileImage(String url);

        SharedPreferences getUserPreferences();

    }

    interface Presenter {
        void loadInfo(Integer event_id, Integer user_id);
    }

    interface JsonHttpApi {
        @GET("users/{id}")
        Call<User> getUser(@Path("id") Integer id);

        @GET("evento/{id}")
        Call<Event> getEventById(@Path("id") Integer id);
    }
}
