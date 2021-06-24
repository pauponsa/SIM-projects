package com.eventic.src.presentation.activities.companyHomePage;

import android.content.SharedPreferences;

import com.eventic.src.domain.Event;
import com.eventic.src.domain.User;
import com.eventic.src.presentation.fragments.eventListFragments.CompanyProfileFragment;
import com.eventic.src.presentation.fragments.eventListFragments.CompanyEventsFragment;

import java.io.File;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface companyHomePageContract {
    interface View {

        void setupEvents(List<Event> events);

        void setEventDisplayFragment();

        void setMyEventsFragment();

        void setProfileFragment();

        void setQrScanFragment();

        void finishActivity();

        CompanyEventsFragment getCompanyEventsFragment();

        SharedPreferences getUserPreferences();

        void changeToLogin();

        void setChatDisplayFragment();

        void sendChats(Map<String,String> a);

        void getProfilePicURL(Integer id);

        void setupCompanyEvents(List<Event> eventResponse);
    }

    interface Presenter {

        void getEvents();

        void eventDisplayFragment();

        void qrScanFragment();

        void myEventsFragment();

        void profileFragment();

        void saveImage(File f);

        void deleteImage(String token);

        void deleteAccount(Integer id, String token);

        void getChats();

        void chatDisplayFragment();

        void getProfilePicURL(Integer id, CompanyProfileFragment companyProfileFragment);
        void getRating(Integer id, CompanyProfileFragment companyProfileFragment);

        void getCompanyEvents();
    }

    interface JsonHttpApi {

        @GET("users/{id}")
        Call<User> getUser(@Path("id") Integer id);

        @GET("evento_comp/{company_id}")
        Call<List<Event>> getCompanyEvents(@Path("company_id") Integer company_id);

        @Multipart
        @PUT("users/{id}")
        Call<User> saveImage(@Path("id") Integer id, @Query("login_token") String token, @Part MultipartBody.Part image);

        @DELETE("profile_pic")
        Call<Void> deleteImage(@Query("login_token") String token);

        @DELETE("users/{id}")
        Call<Void> deleteAccount(@Path("id") Integer id, @Query("login_token") String token);

        @GET("ratings_company")
        Call<Float> getCompanyRating(@Query("company_id") Integer company_id);
    }
}
