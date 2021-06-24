package com.eventic.src.presentation.activities.userHomePage;

import android.content.SharedPreferences;

import com.eventic.src.domain.Event;
import com.eventic.src.domain.User;
import com.eventic.src.presentation.fragments.FragmentAdapter;
import com.eventic.src.presentation.fragments.UserProfileFragment;

import java.io.File;
import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface userHomePageContract {
    interface View {

        void changeToLogin();

        void setEventDisplayFragment();

        void setMyEventsFragment();

        void setProfileFragment();

        void setMyLocationFragment();

        void finishActivity();

        SharedPreferences getUserPreferences();

        void postProfilePic();

        void deleteProfilePic();

        void setChatDisplayFragment();

        void sendChats(String[] map);

        FragmentAdapter adapter();

        void getProfilePicURL(Integer id);
    }

    interface Presenter {

        void eventDisplayFragment();

        void profileFragment();

        void myLocationFragment();

        void deleteAccount(Integer id, String token);

        void saveImage(File f);

        void deleteImage(String token);

        void chatDisplayFragment();

        void getChats();

        void getLiked(Integer id);

        void getJoined(Integer id);

        void getFollowed(Integer id);

        void getProfilePicURL(Integer id, UserProfileFragment userProfileFragment);
    }

    interface JsonHttpApi {

        @GET("users/{id}")
        Call<User> getUser(@Path("id") Integer id);

        @Multipart
        @PUT("users/{id}")
        Call<User> saveImage(@Path("id") Integer id, @Query("login_token") String token, @Part MultipartBody.Part image);

        @DELETE("users/{id}")
        Call<Void> deleteAccount(@Path("id") Integer id, @Query("login_token") String token);

        @DELETE("profile_pic")
        Call<Void> deleteImage(@Query("login_token") String token);

        @GET("entrada_usuarios/{id}")
        Call<List<Event>> getJoinedEvents(@Path("id") Integer id);

        @GET("followed_events/")
        Call<List<Event>> getFollowedEvents( @Query("customer_id") Integer id);

        @GET("liked/{id}")
        Call<List<Event>> getLikedEvents(@Path("id") Integer id);
    }

}

