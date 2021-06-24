package com.eventic.src.presentation.activities.companyPublicPage;

import com.eventic.src.domain.Event;
import com.eventic.src.domain.User;
import com.eventic.src.presentation.activities.companyHomePage.companyHomePageContract;
import com.eventic.src.presentation.activities.event.EventContract;
import com.eventic.src.presentation.activities.userHomePage.userHomePageContract;
import com.eventic.src.presentation.fragments.eventListFragments.CompanyProfileFragment;
import com.example.eventic.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class companyPublicPagePresenter implements companyPublicPageContract.Presenter {

    private companyPublicPageContract.View mView;
    private Integer company_id;

    public companyPublicPagePresenter(companyPublicPageContract.View view, Integer company_id) {
        mView = view;
        this.company_id = company_id;
    }

    public void getCompanyEvents(Integer company_id) {
        //mView.getCompanyEventsFragment().setLoading(true);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://eventic-api.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        companyHomePageContract.JsonHttpApi jsonHttpApi = retrofit.create(companyHomePageContract.JsonHttpApi.class);


        Call<List<Event>> call = jsonHttpApi.getCompanyEvents(company_id);

        call.enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {

                //mView.getCompanyEventsFragment().setLoading(false);
                List<Event> eventResponse = response.body();


                if (!response.isSuccessful()) {
                    System.out.println(response.message());
                    return;
                }
                mView.setupCompanyEvents(eventResponse);
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                //mView.getCompanyEventsFragment().setLoading(false);
                System.out.println("Connection FAILED");
            }
        });
    }

    @Override
    public void getProfilePicURL() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://eventic-api.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        userHomePageContract.JsonHttpApi jsonHttpApi = retrofit.create(userHomePageContract.JsonHttpApi.class);

        Call<User> call = jsonHttpApi.getUser(company_id);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User usuari = response.body();
                if (!response.isSuccessful()) {
                    System.out.println(response.message());
                }
                mView.loadImage(usuari.getImage());
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });
    }

    public void loadRating()
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://eventic-api.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        companyPublicPageContract.JsonHttpApi jsonHttpApi = retrofit.create(companyPublicPageContract.JsonHttpApi.class);

        Call<Float> call = jsonHttpApi.getCompanyRating(company_id);

        call.enqueue(new Callback<Float>() {
            @Override
            public void onResponse(Call<Float> call, Response<Float> response) {
                if (!response.isSuccessful()) {
                    System.out.println(response.message());
                }
                mView.setRating(response.body());
            }
            @Override
            public void onFailure(Call<Float> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });
    }
}
