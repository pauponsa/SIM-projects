package com.eventic.src.presentation.activities.companyPublicPage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.eventic.src.domain.Event;
import com.eventic.src.presentation.activities.companyHomePage.companyHomePagePresenter;
import com.eventic.src.presentation.activities.userHomePage.userHomePageContract;
import com.eventic.src.presentation.activities.userHomePage.userHomePagePresenter;
import com.eventic.src.presentation.fragments.MyLocationFragment;
import com.eventic.src.presentation.fragments.UserProfileFragment;
import com.eventic.src.presentation.fragments.eventListFragments.CompanyEventsFragment;
import com.eventic.src.presentation.fragments.eventListFragments.CompanyProfileFragment;
import com.eventic.src.presentation.fragments.eventListFragments.EventDisplayFragment;
import com.eventic.src.presentation.fragments.eventListFragments.EventListFragment;
import com.eventic.src.presentation.fragments.eventListFragments.JoinedEventListFragment;
import com.eventic.src.presentation.fragments.eventListFragments.MyEventsFragment;
import com.eventic.src.presentation.fragments.eventListFragments.eventItem.EventItem;
import com.eventic.src.presentation.fragments.userChatChooserFragment.UserChatDisplayFragment;
import com.example.eventic.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class companyPublicPageActivity extends AppCompatActivity  implements companyPublicPageContract.View {
    TextView userName;
    ShapeableImageView userProfilePic;
    companyPublicPageContract.Presenter mPresenter;
    RecyclerView recyclerView;
    PublicCompanyAdapter companyAdapter;
    RatingBar ratingBar;
    List<EventItem> eventos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_public_page);

        Intent intent = getIntent();
        eventos = new ArrayList<>();

        Integer id = intent.getIntExtra("company_id",0);

        mPresenter = new companyPublicPagePresenter(this, id);

        mPresenter.getCompanyEvents(id);
        ratingBar = findViewById(R.id.companyPublicRatingBar);
        userName = findViewById(R.id.usernameTitle);
        userProfilePic = findViewById(R.id.userProfilePic);
        userName.setText(intent.getStringExtra("company_name"));

        mPresenter.getProfilePicURL();
        mPresenter.loadRating();
    }

    public void setRating(float rating)
    {
        ratingBar.setRating(rating);
    }



    public void setupCompanyEvents(List<Event> l) {
        for(Event e: l) {
            Map<String, String> eventImageURL = e.getImages_url();
            String url = "ImageNotFound";
            if (eventImageURL!=null) url = eventImageURL.get("0");
            eventos.add(new EventItem(e.getId(),e.getTitle(), e.getStart_date() , e.getParticipants() , e.getCapacity(),url, e.getPrice()));
        }
        companyAdapter = new PublicCompanyAdapter(this, eventos);
        recyclerView = findViewById(R.id.companyCreatedEventsEventList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(companyAdapter);
    }

    @Override
    public void loadImage(String image) {
        Picasso.get().load(image).error(R.drawable.eventic_e).placeholder(R.drawable.eventic_e).into(userProfilePic);

    }
}