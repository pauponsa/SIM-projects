package com.eventic.src.presentation.activities.register.user;

import android.graphics.Bitmap;

import com.eventic.src.domain.User;

import java.io.IOException;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserRegisterContract {

    /* Represents the View in MVP. */
    interface View {

        void changeToLogin();

        void closeAndLogin(String email, String password);

        void showToast(String message);
        void showDialog(String title, String description);

        void setLoading(boolean loading);

        void failedConnection();

        String getStringById(int id);
    }

    /* Represents the Presenter in MVP. */
    interface Presenter {

        void signIn();

        void register(String fullName, String username, String email, String password, String repeatPassword, String role, boolean terms) throws IOException;

    }

    interface JsonHttpApi {
        @POST("users")
        Call<User> registerUser(@Body User user);

    }
}
