package com.eventic.src.presentation.activities.chat;

import com.eventic.src.domain.Event;
import com.eventic.src.domain.User;
import com.eventic.src.presentation.activities.event.EventContract;
import com.example.eventic.R;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class chatPresenter implements chatContract.Presenter {

    private Integer event_id, user_id;
    private String event_name, username;
    private chatContract.View mView;
    public chatPresenter(chatContract.View view) {
        mView = view;
    }

    public void loadInfo(Integer event_id, Integer user_id)
    {
        //mView.setupMessages();
        this.event_id = event_id;
        this.user_id = user_id;

        loadChatInfo();

        /*
        if (mView.getUserPreferences().getInt("id", 0) == user_id)
        {   // Chat is shown from the user view
            mView.setChatTitle(event_name);
        }
        else
        {   // Chat is shown from the company view
            mView.setChatTitle(username);
        }
        */
    }


    void loadChatInfo()
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://eventic-api.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        chatContract.JsonHttpApi jsonHttpApi = retrofit.create(chatContract.JsonHttpApi.class);


        Call<Event> call = jsonHttpApi.getEventById(event_id);

        call.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {
                Event eventResponse = response.body();
                if (!response.isSuccessful()) {
                    return;
                }

                event_name = eventResponse.getTitle();
                mView.setChatTitle(event_name);

                if (mView.getUserPreferences().getInt("id", 0) == user_id) {
                    int idCreator = eventResponse.getId_creator();
                    loadUserInfo(idCreator);
                }
                else {
                    loadUserInfo(user_id);
                }

            }

            @Override
            public void onFailure(Call<Event> call, Throwable t) {
                return;
            }
        });
    }

    void loadUserInfo(Integer chatUser_id)
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://eventic-api.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        chatContract.JsonHttpApi jsonHttpApi = retrofit.create(chatContract.JsonHttpApi.class);

        Call<User> call = jsonHttpApi.getUser(chatUser_id);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User userResponse = response.body();

                if (!response.isSuccessful()) {
                    return;
                }

                username = userResponse.getName();

                if (username == null || username.isEmpty()) username = userResponse.getUsername();
                mView.setChatCreatorName(username);

                mView.setUserProfileImage(userResponse.getImage());


            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                return;
            }
        });
    }

}
