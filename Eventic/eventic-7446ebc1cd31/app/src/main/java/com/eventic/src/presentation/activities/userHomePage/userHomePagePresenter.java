package com.eventic.src.presentation.activities.userHomePage;


import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.eventic.src.domain.Event;
import com.eventic.src.domain.User;
import com.eventic.src.presentation.fragments.UserProfileFragment;
import com.eventic.src.presentation.fragments.eventListFragments.eventItem.EventItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class userHomePagePresenter implements userHomePageContract.Presenter {

    private userHomePageContract.View mView;
    private DatabaseReference mDatabase;
    private List<EventItem> joined;
    private List<EventItem> liked;
    private List<EventItem> followed;

    public userHomePagePresenter(userHomePageContract.View view) {
        mView = view;
    }

    @Override
    public void eventDisplayFragment() {
        mView.setEventDisplayFragment();
    }


    @Override
    public void profileFragment() {
        mView.setProfileFragment();
    }

    @Override
    public void myLocationFragment() {
        mView.setMyLocationFragment();
    }

    public void deleteAccount(Integer id, String token) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://eventic-api.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        userHomePageContract.JsonHttpApi jsonHttpApi = retrofit.create(userHomePageContract.JsonHttpApi.class);

        Call<Void> call = jsonHttpApi.deleteAccount(id,token);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if (!response.isSuccessful()) {
                    System.out.println(response.message());
                    return;
                }

                mView.changeToLogin();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
            }
        });
    }

    public void saveImage(File f) {
        RequestBody reqFile = RequestBody.create(MediaType.parse("image/jpeg"), f);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", f.getName(), reqFile);

        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://eventic-api.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        userHomePageContract.JsonHttpApi jsonHttpApi = retrofit.create(userHomePageContract.JsonHttpApi.class);

        SharedPreferences userPreferences = mView.getUserPreferences();
        Integer id = userPreferences.getInt("id", 0);
        String token = userPreferences.getString("token", null);

        Call<User> call = jsonHttpApi.saveImage(id, token, body);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                // Do Something with response
                if (!response.isSuccessful()) {
                    System.out.println(response.message());
                    return;
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) { }
        });
    }

    @Override
    public void deleteImage(String token) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://eventic-api.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        userHomePageContract.JsonHttpApi jsonHttpApi = retrofit.create(userHomePageContract.JsonHttpApi.class);

        Call<Void> call = jsonHttpApi.deleteImage(token);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if (!response.isSuccessful()) {
                    System.out.println(response.message());
                    return;
                }

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
            }
        });
    }

    private String reformatJSON(String a){
        String startString = "{";
        String endString = "}";
        int startIndex = 1;
        int endIndex = 1;
        while (startIndex != -1) {
            startIndex = a.indexOf(startString, startIndex);
            endIndex = a.indexOf(endString, endIndex);
            if (startIndex != -1 && endIndex != -1) {
                startIndex += 1;
                endIndex += 1;
                String b = a.substring(startIndex, endIndex );
                endIndex -= b.length();
                a = a.replace(b, "");
            }
        }

        a = a.replaceAll("\\{", "");
        a = a.replaceAll("\\}", "");
        a = a.replaceAll(",", "");
        a = a.replaceAll("=", "");

        return a;
    }

    @Override
    public void chatDisplayFragment() {
        mView.setChatDisplayFragment();
    }

    @Override
    public void getChats() {
        mDatabase = FirebaseDatabase.getInstance().getReference();

        SharedPreferences userPreferences = mView.getUserPreferences();
        mDatabase.child("users").child(String.valueOf(userPreferences.getInt("id",0))).get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    if (task.getResult().getValue() != null) {
                        //String a = String.valueOf(task.getResult().getValue());
                        if (task.getResult().getValue() != null) {
                            String s = reformatJSON(String.valueOf(task.getResult().getValue()));
                            String[] chats = s.split(" ");
                            mView.sendChats(chats);
                        }

                    }

                }

            }
                });

    }


    public void getJoined(Integer userid){
        joined = new ArrayList<>();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://eventic-api.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        userHomePageContract.JsonHttpApi jsonHttpApi = retrofit.create(userHomePageContract.JsonHttpApi.class);

        Call<List<Event>> call = jsonHttpApi.getJoinedEvents(userid);

        call.enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                List<Event> events = response.body();
                if (!response.isSuccessful()) {
                    System.out.println(response.message());
                }
                System.out.println(events);
                for(Event e: events){
                    String eventImage = null;
                    if(e.getImages_url() != null){
                        eventImage = e.getImages_url().get("0");
                    }
                    EventItem item = new EventItem(e.getId(), e.getTitle(), e.getStart_date(), e.getParticipants(), e.getCapacity(),  eventImage, e.getPrice());
                    joined.add(item);
                    System.out.println(e.getId());
                }
                mView.adapter().setJoinedEvents(joined);

            }
            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
            }
        });
    }

    public void getFollowed(Integer userid){
        followed = new ArrayList<>();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://eventic-api.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        userHomePageContract.JsonHttpApi jsonHttpApi = retrofit.create(userHomePageContract.JsonHttpApi.class);

        Call<List<Event>> call = jsonHttpApi.getFollowedEvents(userid);

        call.enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                List<Event> events = response.body();
                if (!response.isSuccessful()) {
                    System.out.println(response.message());
                }
                System.out.println(events);
                for(Event e: events){
                    String eventImage = null;
                    if(e.getImages_url() != null){
                        eventImage = e.getImages_url().get("0");
                    }
                    EventItem item = new EventItem(e.getId(), e.getTitle(), e.getStart_date(), e.getParticipants(), e.getCapacity(),  eventImage, e.getPrice());
                    followed.add(item);
                }
                mView.adapter().setFollowedEvents(followed);

            }
            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
            }
        });
    }

    public void getProfilePicURL(Integer id, UserProfileFragment userProfileFragment) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://eventic-api.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        userHomePageContract.JsonHttpApi jsonHttpApi = retrofit.create(userHomePageContract.JsonHttpApi.class);

        Call<User> call = jsonHttpApi.getUser(id);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User usuari = response.body();
                if (!response.isSuccessful()) {
                    System.out.println(response.message());
                }
                if (userProfileFragment!=null){
                    userProfileFragment.loadImage(usuari.getImage());
                }
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });
    }

    public void getLiked(Integer userid){
        liked = new ArrayList<>();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://eventic-api.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        userHomePageContract.JsonHttpApi jsonHttpApi = retrofit.create(userHomePageContract.JsonHttpApi.class);

        Call<List<Event>> call = jsonHttpApi.getLikedEvents(userid);

        call.enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                List<Event> events = response.body();
                if (!response.isSuccessful()) {
                    System.out.println(response.message());
                }
                System.out.println(events);
                for(Event e: events){
                    String eventImage = null;
                    if(e.getImages_url() != null){
                        eventImage = e.getImages_url().get("0");
                    }
                    EventItem item = new EventItem(e.getId(), e.getTitle(), e.getStart_date(), e.getParticipants(), /*e.getJoined() , cogerlo de retrofit*/ e.getCapacity(),  eventImage, e.getPrice());
                    liked.add(item);
                }

                mView.adapter().setLikedEvents(liked);

            }
            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });
    }
}
