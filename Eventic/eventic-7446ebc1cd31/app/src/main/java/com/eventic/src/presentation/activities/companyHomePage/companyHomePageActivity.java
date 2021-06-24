package com.eventic.src.presentation.activities.companyHomePage;

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

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import com.eventic.src.domain.Chat;
import com.eventic.src.domain.Event;
import com.eventic.src.presentation.activities.createEvent.CreateEventActivity;
import com.eventic.src.presentation.activities.login.LoginActivity;
import com.eventic.src.presentation.fragments.QrScanFragment;
import com.eventic.src.presentation.fragments.eventListFragments.CompanyProfileFragment;
import com.eventic.src.presentation.fragments.chatChooserFragment.ChatDisplayFragment;
import com.eventic.src.presentation.fragments.eventListFragments.CompanyEventsFragment;
import com.eventic.src.presentation.fragments.eventListFragments.MyEventsFragment;
import com.eventic.src.presentation.fragments.eventListFragments.eventItem.EventItem;
import com.example.eventic.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class companyHomePageActivity extends AppCompatActivity implements companyHomePageContract.View, View.OnClickListener {

    private companyHomePageContract.Presenter mPresenter;
    private ImageButton HomeButton;
    private ImageButton ChatButton;
    private ImageButton ProfileButton;
    private ImageButton ScanButton;

    FragmentTransaction transaction;
    CompanyEventsFragment eventDisplayFragment;
    CompanyProfileFragment companyProfileFragment;
    ChatDisplayFragment chatDisplayFragment;
    QrScanFragment qrScanFragment;
    MyEventsFragment myEvents;
    GoogleSignInClient mGoogleSignInClient;
    Bitmap image;
    File f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_home_page);
        mPresenter = new companyHomePagePresenter(this);

        setupViews();

        setupFragments();

        selectButton(HomeButton);

        setupAccount();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.getEvents();
    }

    public void eventListFragmentReady() {
        mPresenter.getEvents();
    }

    public void eventListCreatedFragmentReady() {
        mPresenter.getCompanyEvents();
    }

    public void setupEvents(List<Event> l) {
        List<EventItem> eventList = new ArrayList<>();

        String[] tags = {"Entretenimiento", "Comida", "Cultura"};
        int[] eventImages = { R.drawable.event01, R.drawable.event02, R.drawable.event03, R.drawable.event04, R.drawable.event05, R.drawable.event06, R.drawable.event07, R.drawable.event08, R.drawable.event09, R.drawable.event10, R.drawable.event11, R.drawable.event12};

        /*for (int i = 1; i <= 10; ++i) {
            eventList.add(new EventItem("Evento de usuario " + i, "17/03/2021", 2 * i, 302, 500, tags, eventImages[i%eventImages.length], i % 4 == 0));
        }*/
        for(Event e: l) {
            Map<String, String> eventImageURL = e.getImages_url();
            String url = "ImageNotFound";
            if (eventImageURL!=null) url = eventImageURL.get("0");
            eventList.add(new EventItem(e.getId(),e.getTitle(), e.getStart_date() , e.getParticipants() , e.getCapacity(),url, e.getPrice()));
        }
        eventDisplayFragment.setEvents(eventList);
    }

    public void setupCompanyEvents(List<Event> l) {
        List<EventItem> eventList = new ArrayList<>();

        int[] eventImages = { R.drawable.event01, R.drawable.event02, R.drawable.event03, R.drawable.event04, R.drawable.event05, R.drawable.event06, R.drawable.event07, R.drawable.event08, R.drawable.event09, R.drawable.event10, R.drawable.event11, R.drawable.event12};

        /*for (int i = 1; i <= 10; ++i) {
            eventList.add(new EventItem("Evento de usuario " + i, "17/03/2021", 2 * i, 302, 500, tags, eventImages[i%eventImages.length], i % 4 == 0));
        }*/
        for(Event e: l) {
            Map<String, String> eventImageURL = e.getImages_url();
            String url = "ImageNotFound";
            if (eventImageURL!=null) url = eventImageURL.get("0");
            eventList.add(new EventItem(e.getId(),e.getTitle(), e.getStart_date() , e.getParticipants() , e.getCapacity(),url, e.getPrice()));
        }
        companyProfileFragment.setEvents(eventList);
    }

    void setupViews() {
        HomeButton = findViewById(R.id.companyHomeButton);
        ChatButton = findViewById(R.id.companyChatButton);
        ProfileButton = findViewById(R.id.userProfileButton);
        ScanButton = findViewById(R.id.scanButton);

        HomeButton.setOnClickListener(this);
        ChatButton.setOnClickListener(this);
        ProfileButton.setOnClickListener(this);
        ScanButton.setOnClickListener(this);
    }

    void setupFragments() {
        eventDisplayFragment = new CompanyEventsFragment();
        myEvents = new MyEventsFragment();
        companyProfileFragment = new CompanyProfileFragment();
        chatDisplayFragment = new ChatDisplayFragment();
        qrScanFragment = new QrScanFragment();

        getSupportFragmentManager().beginTransaction().add(R.id.contenedorCompany,eventDisplayFragment).commit();
    }

    void setupAccount() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();
            //System.out.println("nombre: " + personName);
            //System.out.println("email: " + personEmail);
            //System.out.println("id usuario: " + personId);
            //System.out.println("url foto: " + personPhoto);
        }
    }

    @Override
    public void onClick(View v) {
        transaction = getSupportFragmentManager().beginTransaction();
        if (v.getId() == R.id.companyHomeButton) {
            selectButton(HomeButton);
            mPresenter.eventDisplayFragment();
            //mPresenter.getEvents();
        }
        else if (v.getId() == R.id.companyChatButton) {
            selectButton(ChatButton);
            mPresenter.chatDisplayFragment();
            mPresenter.getChats();
        }
        else if (v.getId() == R.id.userProfileButton) {
            selectButton(ProfileButton);
            mPresenter.profileFragment();
        }
        else if (v.getId() == R.id.scanButton) {
            selectButton(ScanButton);
            mPresenter.qrScanFragment();
        }
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

            if (button == ScanButton) {
                ScanButton.setImageDrawable(getDrawable(R.drawable.ic_scan_bold));

                ScanButton.setColorFilter(getColor(R.color.eventic_blue));
            } else {
                ScanButton.setImageDrawable(getDrawable(R.drawable.ic_scan_regular));
                ScanButton.setColorFilter(getColor(R.color.mainText));
            }

        }
    }


    public void setMyEventsFragment() {
        transaction.replace(R.id.contenedorCompany,myEvents).commit();
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

                    if (ContextCompat.checkSelfPermission(companyHomePageActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) { // If app has permission
                        // You can use the API that requires the permission.
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pickPhoto , 1);

                    }
                    else if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) { // If user denied permission
                        // In an educational UI, explain to the user why your app requires this
                        // permission for a specific feature to behave as expected. In this UI,
                        // include a "cancel" or "no thanks" button that allows the user to
                        // continue using your app without granting the permission.
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(companyHomePageActivity.this);

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
                    companyProfileFragment.deleteImage();
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
            companyProfileFragment.addImage(image);
        }
    }

    public File convertImage(Bitmap image) throws IOException {
        //create a file to write bitmap data
        SharedPreferences userPreferences = getUserPreferences();
        Integer id = userPreferences.getInt("id",0);
        f = new File(this.getCacheDir(), "company_" + id + "_logo.jpg");
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
    public void setProfileFragment() {
        transaction.replace(R.id.contenedorCompany, companyProfileFragment).commit();
    }

    @Override
    public void setChatDisplayFragment() {
        transaction.replace(R.id.contenedorCompany, chatDisplayFragment).commit();

    }

    @Override
    public void setQrScanFragment() {
        transaction.replace(R.id.contenedorCompany, qrScanFragment).commit();
    }

    @Override
    public void sendChats(Map<String,String> a) {
        List<Chat> chat = new ArrayList<>();
        //set up chats
        for (Map.Entry<String, String> entry : a.entrySet()) {
            chat.add(new Chat(Integer.parseInt(entry.getValue()),Integer.parseInt(entry.getKey()), "evento" + entry.getKey() + " " + entry.getValue() ));
        }
        chatDisplayFragment.setChats(chat);
    }


    public void setEventDisplayFragment(){
        transaction.replace(R.id.contenedorCompany,eventDisplayFragment).commit();

    }

    public void changeToCreateEventActivity() {
        Intent intent = new Intent(companyHomePageActivity.this, CreateEventActivity.class);
        this.startActivity(intent);
    }

    public void signOut() {
        SharedPreferences userPreferences = getUserPreferences();
        userPreferences.edit().remove("token").apply();
        userPreferences.edit().remove("role").apply();
        userPreferences.edit().remove("id").apply();

        Toast.makeText(companyHomePageActivity.this, getText(R.string.successful_signed_out), Toast.LENGTH_LONG).show();

        Intent intent = new Intent(companyHomePageActivity.this, LoginActivity.class);
        this.startActivity(intent);
        finishActivity();
    }

    public SharedPreferences getUserPreferences() {
        return getSharedPreferences("userPreferences", this.MODE_PRIVATE);
    }

    public SharedPreferences getChatPreferences() {
        return getSharedPreferences("chatPreferences", this.MODE_PRIVATE);
    }

    @Override
    public void changeToLogin() {
        Intent intent = new Intent(companyHomePageActivity.this, LoginActivity.class);
        this.startActivity(intent);
        finishActivity();
    }


    @Override
    public void finishActivity() {
        this.finish();
    }

    public void deleteAccount() {
        SharedPreferences userPreferences = getUserPreferences();
        Integer id = userPreferences.getInt("id",0);
        String token = userPreferences.getString("token",null);
        userPreferences.edit().remove("token").apply();
        userPreferences.edit().remove("id").apply();
        mPresenter.deleteAccount(id, token);
    }

    public String getUsername() {
        return getUserPreferences().getString("username",null);
    }

    public CompanyEventsFragment getCompanyEventsFragment()
    {
        return eventDisplayFragment;
    }

    public void postProfilePic() {
        mPresenter.saveImage(f);
    }

    public void deleteProfilePic() {
        SharedPreferences userPreferences = getUserPreferences();
        String token = userPreferences.getString("token",null);
        mPresenter.deleteImage(token);
    }

    public String getEmail() {
        return getUserPreferences().getString("email",null);
    }

    public void getProfilePicURL(Integer id){
        mPresenter.getProfilePicURL(id,companyProfileFragment);
    }

    public void getRating(Integer id) {
        mPresenter.getRating(id, companyProfileFragment);
    }
}