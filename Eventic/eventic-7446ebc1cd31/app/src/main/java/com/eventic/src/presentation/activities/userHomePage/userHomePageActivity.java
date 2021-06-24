package com.eventic.src.presentation.activities.userHomePage;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import com.eventic.src.domain.Chat;
import com.eventic.src.domain.Event;
import com.eventic.src.presentation.activities.createEvent.CreateEventContract;
import com.eventic.src.presentation.activities.editUser.editUserActivity;
import com.eventic.src.presentation.activities.event.EventActivity;
import com.eventic.src.presentation.activities.login.LoginActivity;
import com.eventic.src.presentation.fragments.FragmentAdapter;
import com.eventic.src.presentation.fragments.MyLocationFragment;
import com.eventic.src.presentation.fragments.eventListFragments.EventDisplayFragment;
import com.eventic.src.presentation.fragments.eventListFragments.MyEventsFragment;
import com.eventic.src.presentation.fragments.eventListFragments.JoinedEventListFragment;
import com.eventic.src.presentation.fragments.eventListFragments.eventItem.EventItem;
import com.eventic.src.presentation.fragments.userChatChooserFragment.UserChatDisplayFragment;
import com.eventic.src.presentation.fragments.UserProfileFragment;
import com.example.eventic.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class userHomePageActivity extends AppCompatActivity implements userHomePageContract.View, View.OnClickListener {

    private userHomePageContract.Presenter mPresenter;
    private ImageButton HomeButton;
    private ImageButton MapButton;
    private ImageButton ProfileButton;
    private ImageButton ChatButton;

    FragmentTransaction transaction;
    EventDisplayFragment eventDisplay;
    UserProfileFragment userProfileFragment;
    UserChatDisplayFragment userchatDisplayFragment;
    JoinedEventListFragment userEventListFragment;
    MyEventsFragment myEvents;
    MyLocationFragment googleMap;
    GoogleSignInClient mGoogleSignInClient;
    Bitmap image;
    File f;
    ArrayList<EventItem> eventList;
    ArrayList<EventItem> eventListFull;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home_page);

        setupViews();

        setupFragments();

        selectButton(HomeButton);

        setupAccount();

        //getEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getEvents();
    }

    void setupViews() {
        mPresenter = new userHomePagePresenter(this);

        HomeButton = findViewById(R.id.homeButton);
        MapButton = findViewById(R.id.mapButton);
        ProfileButton = findViewById(R.id.userProfileButton);
        ChatButton = findViewById(R.id.chatButton);

        ChatButton.setOnClickListener(this);
        HomeButton.setOnClickListener(this);
        MapButton.setOnClickListener(this);
        ProfileButton.setOnClickListener(this);

    }

    void setupFragments() {
        eventDisplay = new EventDisplayFragment();
        myEvents = new MyEventsFragment();
        googleMap = new MyLocationFragment();
        userProfileFragment = new UserProfileFragment();
        userchatDisplayFragment = new UserChatDisplayFragment();
        userEventListFragment = new JoinedEventListFragment();

        getSupportFragmentManager().beginTransaction().add(R.id.contenedorUser, eventDisplay).commit();
    }

    void setupEvents(List<Event> l) {
        eventList = new ArrayList<>();
        eventListFull = new ArrayList<>();
        int[] eventImages = { R.drawable.event01, R.drawable.event02, R.drawable.event03, R.drawable.event04, R.drawable.event05, R.drawable.event06, R.drawable.event07, R.drawable.event08, R.drawable.event09, R.drawable.event10, R.drawable.event11, R.drawable.event12};

        /*for (int i = 1; i <= 10; ++i) {
            eventList.add(new EventItem("Evento de usuario " + i, "17/03/2021", 2 * i, 302, 500, tags, eventImages[i%eventImages.length], i % 4 == 0));
        }*/
        for(Event e: l) {
            Map<String, String> eventImageURL = e.getImages_url();
            String url = "ImageNotFound";
            if (eventImageURL!=null) url = eventImageURL.get("0");
            eventList.add(new EventItem(e.getId(),e.getTitle(), e.getStart_date() , e.getParticipants() , e.getCapacity(), url, e.getPrice()));
        }
        eventListFull.addAll(eventList);
        eventDisplay.setEvents(eventList);
        eventDisplay.setEventsForFilters(eventList);
    }
    public List<EventItem> getFilterEvents(){

        return eventListFull;
    }

    void setupAccount() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    public void eventListFragmentReady() {
        getEvents();
    }

    public void joinedFragmentReady() {
        mPresenter.getJoined(getUserPreferences().getInt("id",0));
    }

    public void likedFragmentReady() {
        mPresenter.getLiked(getUserPreferences().getInt("id",0));
    }

    public void followedFragmentReady(){
        mPresenter.getFollowed(getUserPreferences().getInt("id",0));
    }

    public void getEvents() {
        eventDisplay.setLoading(true);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://eventic-api.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        CreateEventContract.JsonHttpApi jsonHttpApi = retrofit.create(CreateEventContract.JsonHttpApi.class);

        Call<List<Event>> call = jsonHttpApi.getEvents();

        call.enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                eventDisplay.setLoading(false);
                List<Event> eventResponse = response.body();

                if (!response.isSuccessful()) {
                    System.out.println(response.message());
                    return;
                }
                setupEvents(eventResponse);
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                eventDisplay.setLoading(false);
                System.out.println("Connection FAILED");
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.homeButton) {
            selectButton(HomeButton);
            mPresenter.eventDisplayFragment();
        }
        else if (v.getId() == R.id.chatButton) {
            selectButton(ChatButton);
            mPresenter.chatDisplayFragment();
            mPresenter.getChats();
        }
        else if(v.getId() == R.id.userProfileButton){
            selectButton(ProfileButton);
            mPresenter.profileFragment();
        }
        else if(v.getId() == R.id.mapButton){
            selectButton(MapButton);
            mPresenter.myLocationFragment();
        }
    }

    public void changeToEvent(){
        Intent intent = new Intent(userHomePageActivity.this, EventActivity.class);
        startActivity(intent);
    }

    private void selectButton(ImageButton button) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (button == HomeButton) {
                HomeButton.setImageDrawable(getDrawable(R.drawable.ic_home_solid));
                HomeButton.setColorFilter(getColor(R.color.eventic_blue));
            } else {
                HomeButton.setImageDrawable(getDrawable(R.drawable.ic_home_regular));
                HomeButton.setColorFilter(getColor(R.color.mainText));
            }

            if (button == ProfileButton) {
                ProfileButton.setImageDrawable(getDrawable(R.drawable.ic_user_solid));
                ProfileButton.setColorFilter(getColor(R.color.eventic_blue));
            } else {
                ProfileButton.setImageDrawable(getDrawable(R.drawable.ic_user_regular));
                ProfileButton.setColorFilter(getColor(R.color.mainText));
            }

            if (button == ChatButton) {
                ChatButton.setImageDrawable(getDrawable(R.drawable.ic_bubbles_solid));
                ChatButton.setColorFilter(getColor(R.color.eventic_blue));
            } else {
                ChatButton.setImageDrawable(getDrawable(R.drawable.ic_bubbles_regular));
                ChatButton.setColorFilter(getColor(R.color.mainText));
            }

            if (button == MapButton) {
                MapButton.setImageDrawable(getDrawable(R.drawable.ic_location_solid));
                MapButton.setColorFilter(getColor(R.color.eventic_blue));
            } else {
                MapButton.setImageDrawable(getDrawable(R.drawable.ic_location_regular));
                MapButton.setColorFilter(getColor(R.color.mainText));
            }
        }
    }

    public void setMyEventsFragment() {
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.contenedorUser,myEvents).commit();
        getEvents();
    }

    public void setProfileFragment() {
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.contenedorUser, userProfileFragment).commit();

    }

    public void setMyLocationFragment() {
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.contenedorUser,googleMap).commit();
    }

    public void setEventDisplayFragment(){
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.contenedorUser,eventDisplay).commit();
        getEvents();
    }

    public void signOut() {
        SharedPreferences userPreferences = getUserPreferences();
        userPreferences.edit().remove("token").apply();
        userPreferences.edit().remove("role").apply();
        userPreferences.edit().remove("id").apply();

        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(userHomePageActivity.this, getText(R.string.successful_signed_out), Toast.LENGTH_LONG).show();
                        finish();
                    }
                });

        Intent intent = new Intent(userHomePageActivity.this, LoginActivity.class);
        this.startActivity(intent);
        finishActivity();
    }



    public void selectImage(Context context) {
        final CharSequence[] options = { getText(R.string.take_photo), getText(R.string.choose_from_gallery), getText(R.string.delete_image), getText(R.string.cancel) };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(getText(R.string.choose_profile_picture));

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals(getText(R.string.take_photo))) {
                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 0);

                } else if (options[item].equals(getText(R.string.choose_from_gallery))) {

                    if (ContextCompat.checkSelfPermission(userHomePageActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) { // If app has permission
                        // You can use the API that requires the permission.
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pickPhoto , 1);

                    }
                    else if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) { // If user denied permission
                        // In an educational UI, explain to the user why your app requires this
                        // permission for a specific feature to behave as expected. In this UI,
                        // include a "cancel" or "no thanks" button that allows the user to
                        // continue using your app without granting the permission.
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(userHomePageActivity.this);

                        alertDialogBuilder.setTitle(getText(R.string.permission_needed));
                        alertDialogBuilder
                                .setMessage(getText(R.string.need_storage_permission))
                                .setCancelable(true);

                        // create alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();

                        // show it
                        alertDialog.show();
                        return;
                    }
                    else { // If permission is not set
                        // You can directly ask for the permission.
                        requestPermissions(new String[] { Manifest.permission.READ_EXTERNAL_STORAGE }, 1);
                        return;
                    }

                } else if (options[item].equals(getText(R.string.cancel))) {
                    dialog.dismiss();
                } else if (options[item].equals(getText(R.string.delete_image))){
                    userProfileFragment.deleteImage();
                    dialog.dismiss();
                }
            }
        });
        builder.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) return;

        image = null;

        if (requestCode == 0 && resultCode == RESULT_OK && data != null) { // Get image from camera
            image = (Bitmap) data.getExtras().get("data");
        }

        else if (requestCode == 1 && resultCode == RESULT_OK && data != null) { // Get image from gallery
            Uri selectedImage = data.getData();
            InputStream imageStream = null;
            try {
                imageStream = getContentResolver().openInputStream(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            image = BitmapFactory.decodeStream(imageStream);
        }

           if(image!= null){
               try {
                   f = convertImage(image);
               } catch (IOException e) {
                   e.printStackTrace();
               }
               userProfileFragment.addImage(image);
           }
    }


    public File convertImage(Bitmap image) throws IOException {
        //create a file to write bitmap data
        SharedPreferences userPreferences = getUserPreferences();
        Integer id = userPreferences.getInt("id",0);
        f = new File(this.getCacheDir(), "user_" + id + "_profile.jpg");
        f.createNewFile();

        //Convert bitmap to byte array
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100 /*ignored for PNG*/, bos);
        byte[] bitmapdata = bos.toByteArray();

        //write the bytes in file
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return f;
    }

    @Override
    public void changeToLogin() {
        Intent intent = new Intent(userHomePageActivity.this, LoginActivity.class);
        this.startActivity(intent);
        finishActivity();
    }

    @Override
    public void finishActivity() {
        this.finish();
    }

    public String getUsername() {
        return getUserPreferences().getString("username",null);
    }

    public void deleteAccount() {
        SharedPreferences userPreferences = getUserPreferences();
        Integer id = userPreferences.getInt("id",0);
        String token = userPreferences.getString("token",null);
        userPreferences.edit().remove("token").apply();
        userPreferences.edit().remove("id").apply();
        mPresenter.deleteAccount(id, token);
    }

    public SharedPreferences getUserPreferences() {
        return getSharedPreferences("userPreferences", this.MODE_PRIVATE);
    }

    public void changePassword() {
        Intent intent = new Intent(userHomePageActivity.this, editUserActivity.class);
        this.startActivity(intent);
    }

    public void postProfilePic() {
        mPresenter.saveImage(f);
    }

    public void deleteProfilePic() {
        SharedPreferences userPreferences = getUserPreferences();
        String token = userPreferences.getString("token",null);
        mPresenter.deleteImage(token);
    }

    @Override
    public void setChatDisplayFragment() {

        transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.contenedorUser, userchatDisplayFragment).commit();

    }

    @Override
    public void sendChats(String [] a) {
        SharedPreferences userPreferences = getUserPreferences();

        List<Chat> chat = new ArrayList<>();

        for (String s: a) {
            try {
                Integer num = Integer.parseInt(s);
                chat.add(new Chat(userPreferences.getInt("id", 0), num, "eventos"));
            } catch (Exception e) {}

        }
        userchatDisplayFragment.setChats(chat);
    }

    @Override
    public FragmentAdapter adapter() {
        return userProfileFragment.getFragmentAdapter();
    }

    public String getEmail() {
        return getUserPreferences().getString("email",null);
    }


    public SharedPreferences getChatPreferences() {
        return getSharedPreferences("chatPreferences", this.MODE_PRIVATE);

    }

    public void getProfilePicURL(Integer id){
        mPresenter.getProfilePicURL(id,userProfileFragment);
    }

    /*private void initColors(){

        white = ContextCompat.getColor(getApplicationContext(), R.color.design_default_color_primary);
        red = ContextCompat.getColor(getApplicationContext(), R.color.red);
        darkGray = ContextCompat.getColor(getApplicationContext(), R.color.eventic_darkest);
    }
    private void unselectAllFilterButtons()
    {
        lookUnSelected(EntretenimientoButton);
        lookUnSelected(CulturaButton);
        lookUnSelected(ComidaButton);
    }

    private void lookSelected(Button parsedButton){
        parsedButton.setTextColor(white);
        parsedButton.setBackgroundColor(red);


    }
    private void lookUnSelected(Button parsedButton){
        parsedButton.setTextColor(red);
        parsedButton.setBackgroundColor(darkGray);
    }*/

}