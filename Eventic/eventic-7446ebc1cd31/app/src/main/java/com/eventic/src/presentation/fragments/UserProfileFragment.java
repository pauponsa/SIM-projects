package com.eventic.src.presentation.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.eventic.src.presentation.activities.settings.SettingsActivity;
import com.eventic.src.presentation.activities.userHomePage.userHomePageActivity;
import com.example.eventic.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;


public class UserProfileFragment extends Fragment {
    View view;
    TextView userName, email;
    ShapeableImageView userProfilePic;
    ImageButton settingsButton;
    TabLayout tabLayout;
    ViewPager2 pager2;
    FragmentAdapter adapter;

        public UserProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        setupViews();

        return view;
    }

    public void setupViews() {
        tabLayout = view.findViewById(R.id.tabLayout);
        pager2 = view.findViewById(R.id.userEvents);
        pager2.setSaveEnabled(false);
        FragmentManager fm = getChildFragmentManager();
        adapter = new FragmentAdapter(fm, getLifecycle());
        pager2.setAdapter(adapter);
        userName = view.findViewById(R.id.usernameTitle);
        email = view.findViewById(R.id.emailTitle);
        settingsButton = view.findViewById(R.id.settings);
        userProfilePic = view.findViewById(R.id.userProfilePic);

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                System.out.println("POSITION FRAGMENT");
                System.out.println(tab.getPosition());
                pager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        pager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });


        userProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences userPreferences = ((userHomePageActivity)getActivity()).getUserPreferences();
                if(userPreferences.getString("role",null).equals("customer")) ((userHomePageActivity)getActivity()).selectImage((userHomePageActivity)getActivity());
            }
        });

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        loadUserInfo();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void addImage(Bitmap image) {
        userProfilePic.setImageBitmap(image);

        //POST IMAGE TO BD
        ((userHomePageActivity)getActivity()).postProfilePic();
    }

    public  void deleteImage() {

        Bitmap logoEventic = BitmapFactory.decodeResource(getResources(),R.drawable.eventic_e);
        userProfilePic.setImageBitmap(logoEventic);

        //DELETE IMAGE FROM DB
        ((userHomePageActivity)getActivity()).deleteProfilePic();

    }

    public void loadUserInfo() {
        userName.setText(((userHomePageActivity) getActivity()).getUsername());
        email.setText(((userHomePageActivity) getActivity()).getEmail());
        SharedPreferences userPreferences = ((userHomePageActivity)getActivity()).getUserPreferences();
        Integer id = userPreferences.getInt("id",0);

        ((userHomePageActivity)getActivity()).getProfilePicURL(id);
        /*
        String texto = "tonto";
        Bitmap QR = QRCode.from(texto).bitmap();
        userProfilePic.setImageBitmap(QR);
        */

    }

    public void loadImage(String url){
        Picasso.get().load(url).error(R.drawable.eventic_e).placeholder(R.drawable.eventic_e).into(userProfilePic);
    }

    public FragmentAdapter getFragmentAdapter(){
        return adapter;
    }



}