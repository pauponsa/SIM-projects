package com.eventic.src.presentation.fragments.eventListFragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

import com.eventic.src.domain.User;
import com.eventic.src.presentation.activities.companyHomePage.companyHomePageActivity;
import com.eventic.src.presentation.activities.editUser.editUserActivity;
import com.eventic.src.presentation.activities.event.EventActivity;
import com.eventic.src.presentation.activities.settings.SettingsActivity;
import com.eventic.src.presentation.activities.userHomePage.userHomePageActivity;
import com.eventic.src.presentation.fragments.eventListFragments.EventContainer;
import com.eventic.src.presentation.fragments.eventListFragments.EventListFragment;
import com.eventic.src.presentation.fragments.eventListFragments.eventItem.EventItem;
import com.example.eventic.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;


public class CompanyProfileFragment extends EventContainer {
    TextView userName, email;
    ShapeableImageView userProfilePic;
    ImageButton settingsButton;
    RatingBar ratingBar;

    public CompanyProfileFragment() {
        super();
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_company_profile, container, false);
        userName = v.findViewById(R.id.usernameTitle);
        email = v.findViewById(R.id.emailTitle);
        settingsButton = v.findViewById(R.id.settings);
        userProfilePic = v.findViewById(R.id.userProfilePic);
        ratingBar = v.findViewById(R.id.companyProfileRatingBar);


        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                getActivity().startActivity(intent);
            }
        });

        userProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences userPreferences = ((companyHomePageActivity)getActivity()).getUserPreferences();
                if(userPreferences.getString("role",null).equals("company")) ((companyHomePageActivity)getActivity()).selectImage((companyHomePageActivity)getActivity());
            }
        });
         return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        eventList = new EventListFragment();
        eventList.setContainer(this);
        loadUserInfo();

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.companyCreatedEventsEventList, eventList).commit();

        ((companyHomePageActivity)getActivity()).eventListCreatedFragmentReady();

    }

    @Override
    public void clickEvent(EventItem item) {
        Intent intent = new Intent(getActivity(), EventActivity.class);
        intent.putExtra("id", item.getId());

        getActivity().startActivity(intent);
    }

    public void addImage(Bitmap image) {
        userProfilePic.setImageBitmap(image);

        //POST IMAGE TO BD
        ((companyHomePageActivity)getActivity()).postProfilePic();
    }

    public  void deleteImage() {

        Bitmap logoEventic = BitmapFactory.decodeResource(getResources(),R.drawable.eventic_e);
        userProfilePic.setImageBitmap(logoEventic);

        //DELETE IMAGE FROM BD
        ((companyHomePageActivity)getActivity()).deleteProfilePic();

    }

    public void loadUserInfo() {
        userName.setText(((companyHomePageActivity) getActivity()).getUsername());
        email.setText(((companyHomePageActivity) getActivity()).getEmail());
        SharedPreferences userPreferences = ((companyHomePageActivity)getActivity()).getUserPreferences();
        Integer id = userPreferences.getInt("id",0);

        ((companyHomePageActivity)getActivity()).getProfilePicURL(id);
        ((companyHomePageActivity)getActivity()).getRating(id);
    }

    public void loadImage(String url){
        Picasso.get().load(url).error(R.drawable.eventic_e).placeholder(R.drawable.eventic_e).into(userProfilePic);
    }

    public void setRating(float rating) {
        ratingBar.setRating(rating);
    }



}