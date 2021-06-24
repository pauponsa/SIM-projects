package com.eventic.src.presentation.activities.companyPublicPage;

import android.content.SharedPreferences;

import com.eventic.src.domain.Event;
import com.eventic.src.domain.User;
import com.eventic.src.presentation.fragments.eventListFragments.CompanyEventsFragment;
import com.eventic.src.presentation.fragments.eventListFragments.CompanyProfileFragment;

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

public interface companyPublicPageContract {
    interface View {

        void loadImage(String image);

        void setupCompanyEvents(List<Event> eventResponse);
        void setRating(float rating);
    }

    interface Presenter {

        void getCompanyEvents(Integer company_id);
        void getProfilePicURL();
        void loadRating();
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
