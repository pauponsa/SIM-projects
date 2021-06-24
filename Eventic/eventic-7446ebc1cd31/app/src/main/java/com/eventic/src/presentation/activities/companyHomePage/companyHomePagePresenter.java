package com.eventic.src.presentation.activities.companyHomePage;

import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.eventic.src.domain.Event;
import com.eventic.src.domain.User;
import com.eventic.src.presentation.activities.userHomePage.userHomePageContract;
import com.eventic.src.presentation.fragments.eventListFragments.CompanyProfileFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class companyHomePagePresenter implements companyHomePageContract.Presenter {

    private companyHomePageContract.View mView;
    private DatabaseReference mDatabase;
    private ArrayList<Event> events;
    private Map<String, String> map;

    public companyHomePagePresenter(companyHomePageContract.View view) {
        mView = view;
    }

    @Override
    public void eventDisplayFragment() {
        mView.setEventDisplayFragment();
    }

    @Override
    public void qrScanFragment()
    {
        mView.setQrScanFragment();
    }

    @Override
    public void myEventsFragment() {
        mView.setMyEventsFragment();
    }

    @Override
    public void profileFragment() {
        mView.setProfileFragment();
    }

    @Override
    public void chatDisplayFragment() {
        mView.setChatDisplayFragment();
    }

    public void getChats() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        map = new HashMap<>();
        for (Event ev : events) {
            mDatabase.child("chat").child(String.valueOf(ev.getId())).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());
                    } else {
                        if (task.getResult().getValue() != null) {
                            //String a = String.valueOf(task.getResult().getValue());
                            //System.out.println(a);
                            ArrayList<String> messages = new ArrayList<>();
                            if (task.getResult().getValue() != null) {
                                String a = String.valueOf(task.getResult().getValue());
                                String findstrText = "senderID=";
                                String findStr = ", text=";
                                int lastIndextext = 0;
                                int lastIndex = 0;
                                while (lastIndextext != -1) {
                                    lastIndextext = a.indexOf(findstrText, lastIndextext);
                                    lastIndex = a.indexOf(findStr, lastIndex);
                                    if (lastIndextext != -1) {
                                        lastIndextext += findstrText.length();
                                        lastIndex += 1;
                                        messages.add(a.substring(lastIndextext, lastIndex - 1));
                                    }
                                }
                                for (String m : messages) map.put(String.valueOf(ev.getId()), m);
                                mView.sendChats(map);
                            }
                        }

                    }

                }
            });
        }
    }

    public void getEvents() {
        //mView.getCompanyEventsFragment().setLoading(true);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://eventic-api.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        companyHomePageContract.JsonHttpApi jsonHttpApi = retrofit.create(companyHomePageContract.JsonHttpApi.class);

        Integer company_id = mView.getUserPreferences().getInt("id", 0);
        //System.out.println("USER ID = " + company_id);

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
                mView.setupEvents(eventResponse);
                events = new ArrayList<>();
                events.addAll(eventResponse);
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                //mView.getCompanyEventsFragment().setLoading(false);
                System.out.println("Connection FAILED");
            }
        });
    }

    public void getCompanyEvents() {
        //mView.getCompanyEventsFragment().setLoading(true);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://eventic-api.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        companyHomePageContract.JsonHttpApi jsonHttpApi = retrofit.create(companyHomePageContract.JsonHttpApi.class);

        Integer company_id = mView.getUserPreferences().getInt("id", 0);
        //System.out.println("USER ID = " + company_id);

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
                events = new ArrayList<>();
                events.addAll(eventResponse);
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                //mView.getCompanyEventsFragment().setLoading(false);
                System.out.println("Connection FAILED");
            }
        });
    }


    public void saveImage(File f) {
        RequestBody reqFile = RequestBody.create(MediaType.parse("image/jpeg"), f);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", f.getName(), reqFile);

        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://eventic-api.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        companyHomePageContract.JsonHttpApi jsonHttpApi = retrofit.create(companyHomePageContract.JsonHttpApi.class);

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
            public void onFailure(Call<User> call, Throwable t) {
                //failure message
                t.printStackTrace();
            }
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

    public void getProfilePicURL(Integer id, CompanyProfileFragment companyProfileFragment) {
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
                if (companyProfileFragment!=null){
                    companyProfileFragment.loadImage(usuari.getImage());
                }
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });
    }

    public void getRating(Integer id, CompanyProfileFragment companyProfileFragment) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://eventic-api.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        companyHomePageContract.JsonHttpApi jsonHttpApi = retrofit.create(companyHomePageContract.JsonHttpApi.class);

        Call<Float> call = jsonHttpApi.getCompanyRating(id);

        call.enqueue(new Callback<Float>() {
            @Override
            public void onResponse(Call<Float> call, Response<Float> response) {
                if (!response.isSuccessful()) {
                    System.out.println(response.message());
                }
                if (companyProfileFragment!=null){
                    companyProfileFragment.setRating(response.body());
                }
            }
            @Override
            public void onFailure(Call<Float> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });
    }

}
