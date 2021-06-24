package com.eventic.src.presentation.activities.editUser;

import android.content.SharedPreferences;
import android.graphics.Bitmap;

import com.eventic.src.domain.User;

import java.io.IOException;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface editUserContract {
    interface View {


        SharedPreferences getUserPreferences();

        void failedLogin();


        void signOut();
    }

    /* Represents the Presenter in MVP. */
    interface Presenter {



        void changeInfo(Integer id, String name, String login_token, String emailtext, String newPassword,  String usernameText);

        void checkPassActual(String emailusu, String passwordactual, Integer id, String name, String emailtext, String newPassword,  String usernameText);
    }

    interface JsonHttpApi {

        @PUT("users/{id}")
        Call<User> changeInfoUser(@Path("id") Integer id,
                           @Query("name") String name,
                           @Query("login_token") String login_token,
                           @Query("email") String email,
                           @Query("password") String password,
                           @Query("username") String username );

        @POST("login")
        Call<User> checkPassActual(@Query("email") String email,
                                  @Query("password") String password
                                  );



    }
}
