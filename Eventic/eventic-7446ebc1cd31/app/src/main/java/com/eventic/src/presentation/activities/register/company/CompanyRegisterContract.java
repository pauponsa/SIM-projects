package com.eventic.src.presentation.activities.register.company;

import com.eventic.src.domain.Company;
import com.eventic.src.domain.User;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface CompanyRegisterContract {

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

        void register(String fullName, String username, String email, String password, String repeatPassword, String role, boolean terms);

        void signIn();
    }

    interface JsonHttpApi {
        @POST("users")
        Call<Company> registerCompany(@Body Company company);

        @Multipart
        @POST("users")
        Call<User> addImage(@Part MultipartBody.Part image);
    }
}
