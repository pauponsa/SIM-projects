package com.eventic.src.presentation.activities.event;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.eventic.src.domain.Event;
import com.eventic.src.domain.Tag;
import com.eventic.src.presentation.activities.chat.chatActivity;
import com.eventic.src.presentation.activities.companyHomePage.companyHomePageActivity;
import com.eventic.src.presentation.activities.companyPublicPage.companyPublicPageActivity;
import com.eventic.src.presentation.activities.createEvent.CreateEventActivity;
import com.eventic.src.presentation.activities.login.LoginActivity;
import com.eventic.src.presentation.components.ImageAdapter;
import com.eventic.src.presentation.components.TagChip;
import com.eventic.src.presentation.fragments.EventLocationFragment;
import com.example.eventic.R;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;

public class EventActivity extends AppCompatActivity implements EventContract.View, View.OnClickListener {

    // Components
    private EventContract.Presenter mPresenter;
    private Button joinButton, ticketButton;
    private Button followButton;
    private TextView title;
    private TextView description;
    private TextView author;
    private ImageButton authorProfilePic;
    private CardView cardCompanyProfileImage;
    private ViewPager viewPager;
    private Button chatButton;
    private ImageButton likeButton, shareButton, calendarButton, optionsButton;
    private Chip fecha, distancia, asistentes, precio;
    private String fechaIni, fechaFin, horaIni, horaFin;
    private ChipGroup eventTags;
    private String latitude;
    private String longitude;
    private String userLatitude;
    private String userLongitude;
    private ProgressBar progressBar;
    private String[] imagesURLs;
    private RatingBar companyRating;
    private ConstraintLayout ratingLayout;
    private RatingBar eventRating;
    EventLocationFragment eventLocationFragment;

    public EventActivity() {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        initViews();

        mPresenter = new EventPresenter(this);


        Intent intent = getIntent();
        Uri data = intent.getData();

        SharedPreferences userPreferences = getUserPreferences();
        if (userPreferences.getInt("id", 0) == 0)
        {   // If not logged-in
            logOut();
            //setViewOnlyMode(true);
        }

        Integer id = 0;

        if (data != null) {   // Entering from URI
            String path = data.getPath();
            path = path.replace("/evento/","");
            id = Integer.parseInt(path);
        }

        else {   // Entering from in-app Intent
            id = intent.getIntExtra("id",0);
        }

        mPresenter.setId(id);
        mPresenter.compEvent();
    }

    public void logOut() {
        SharedPreferences userPreferences = getUserPreferences();
        userPreferences.edit().remove("token").apply();
        userPreferences.edit().remove("role").apply();
        userPreferences.edit().remove("id").apply();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.startActivity(intent);
        finishActivity();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.loadEvent();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initViews() {
        ScrollView mainScrollView = findViewById(R.id.scrollView);
        ImageView transparentImageView = findViewById(R.id.transparent_image);

        transparentImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:

                    case MotionEvent.ACTION_MOVE:
                        // Disallow ScrollView to intercept touch events.
                        mainScrollView.requestDisallowInterceptTouchEvent(true);
                        // Disable touch on transparent view
                        return false;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        mainScrollView.requestDisallowInterceptTouchEvent(false);
                        return true;

                    default:
                        return true;
                }
            }
        });

        viewPager = findViewById(R.id.Foto);
        title = findViewById(R.id.EventTitle);
        author = findViewById(R.id.textAuthor);
        authorProfilePic = findViewById(R.id.CompanyProfileImage);
        authorProfilePic.setOnClickListener(this);
        cardCompanyProfileImage = findViewById(R.id.CardCompanyProfileImage);
        description = findViewById(R.id.eventDesc);
        calendarButton = findViewById(R.id.calendarButton);
        calendarButton.setOnClickListener(this);
        likeButton = findViewById(R.id.likeButton);
        likeButton.setOnClickListener(this);

        followButton = findViewById(R.id.follow);
        followButton.setOnClickListener(this);

        shareButton = findViewById(R.id.shareButton);
        shareButton.setOnClickListener(this);
        joinButton = findViewById(R.id.JoinButton);
        joinButton.setOnClickListener(this);
        ticketButton = findViewById(R.id.ticketButton);
        ticketButton.setOnClickListener(this);
        chatButton = findViewById(R.id.chat);
        chatButton.setOnClickListener(this);
        optionsButton = findViewById(R.id.eventOtherOptionsButton);
        optionsButton.setOnClickListener(this);
        fecha = findViewById(R.id.fecha);
        precio = findViewById(R.id.precio);
        distancia = findViewById(R.id.distancia);
        asistentes = findViewById(R.id.asistentes);
        eventTags = findViewById(R.id.eventTagsContainer);

        progressBar = findViewById(R.id.eventProgressBar);

        companyRating = findViewById(R.id.ratingBar);

        ratingLayout = findViewById(R.id.rating_layout);

        eventRating = findViewById(R.id.eventRatingBar);
        eventRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (fromUser) changedEventRating(rating);
            }
        });
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }

    public void setAuthor(String author){ this.author.setText(author);}

    public void setAuthorProfilePic(String url){
        Picasso.get().load(url)
                .centerCrop()
                .fit()
                .placeholder(R.drawable.eventic_e)
                .error(R.drawable.eventic_e)
                .into(this.authorProfilePic);
    }

    public void setDescription(String description) {
        this.description.setText(description);
    }

    public void haReportat(){
        optionsButton.setVisibility(ImageButton.GONE);

    }
    public void setLiked(boolean liked) {
        if (liked) {
            likeButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_heart_solid));
            likeButton.setColorFilter(getResources().getColor(R.color.red));
        }
        else {
            likeButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_heart_regular));
            likeButton.setColorFilter(getResources().getColor(R.color.mainText));
        }
    }
    public void setDateIni(String dateIni) { fechaIni = dateIni; }
    public void setDateFin(String dateFin) { fechaFin = dateFin; }
    public void setHoraIni(String horaIn) { horaIni = horaIn; }
    public void setHoraFin(String horaFi) { horaFin = horaFi; }

    public void setDate(String date) {
        this.fecha.setText(date);
    }

    public void setPrice(String price) {
        this.precio.setText(price);
    }

    public void changedEventRating(float newRating)
    {
        mPresenter.rateEvent(newRating);
    }

    public void setEventRating(float rating)
    {
        eventRating.setRating(rating);
    }
    public void setAuthorRating(float rating)
    {
        companyRating.setRating(rating);
    }

    public void setDistance(String distance) {

        this.distancia.setText(distance);
    }



    public void setCapacity(String capacity) {
        this.asistentes.setText(capacity);
    }

    public void addTag(Tag tag) {
        TagChip newTag = new TagChip(this);
        newTag.setChipIconResource(R.drawable.ic_tag);
        newTag.setChipIconTintResource(R.color.mainText);
        newTag.setTag(tag);

        eventTags.addView(newTag);
    }

    public void resetTags()
    {
        eventTags.removeAllViews();
    }

    public void setJoined(boolean participant) {

        if (participant) {
            joinButton.setBackgroundColor(getResources().getColor(R.color.red));
            joinButton.setText(getText(R.string.drop_event));
            joinButton.setCompoundDrawables(getDrawable(R.drawable.ic_account_multiple_minus), null, null, null);
            joinButton.setTextColor(getResources().getColor(R.color.white));
            ticketButton.setVisibility(ImageButton.VISIBLE);
        }
        else {
            joinButton.setBackgroundColor(getResources().getColor(R.color.eventic_blue));
            joinButton.setText(getText(R.string.join_event));
            joinButton.setTextColor(getResources().getColor(R.color.white));
            joinButton.setCompoundDrawables(getDrawable(R.drawable.ic_account_multiple_plus), null, null, null);
            ticketButton.setVisibility(ImageButton.GONE);
        }
    }

    public void setFollowed(boolean followed){
        if(followed){
            followButton.setBackgroundColor(getResources().getColor(R.color.red));
            followButton.setText("UNFOLLOW");
        }
        else{
            followButton.setBackgroundColor(getResources().getColor(R.color.eventic_blue));
            followButton.setText("FOLLOW");
        }
    }

    public void setFull() {
        joinButton.setBackgroundColor(getResources().getColor(R.color.altBackground));
        joinButton.setText(getText(R.string.join_event));
        joinButton.setTextColor(getResources().getColor(R.color.textAlt));
        joinButton.setCompoundDrawables(getDrawable(R.drawable.ic_account_multiple_plus), null, null, null);
    }

    public void setEditMode(boolean editMode) {
        if (editMode) {
            joinButton.setBackgroundColor(getResources().getColor(R.color.red));
            joinButton.setText(getText(R.string.remove_event));
            joinButton.setCompoundDrawables(getDrawable(R.drawable.ic_trash_solid), null, null, null);

            chatButton.setBackgroundColor(getResources().getColor(R.color.eventic_lightest));
            chatButton.setText(getText(R.string.edit_event));
            chatButton.setCompoundDrawables(getDrawable(R.drawable.ic_edit_solid), null, null, null);
            ticketButton.setVisibility(ImageButton.GONE);

            likeButton.setVisibility(ImageButton.GONE);
            optionsButton.setVisibility(ImageButton.GONE);

            followButton.setVisibility(Button.GONE);
        }

        else {
            chatButton.setBackgroundColor(getResources().getColor(R.color.eventic_lightest));
            chatButton.setText(getText(R.string.chat));
            chatButton.setCompoundDrawables(getDrawable(R.drawable.ic_bubbles_solid), null, null, null);
        }
    }

    public void setViewOnlyMode(boolean viewMode) {
        if (viewMode) {
            joinButton.setVisibility(Button.GONE);
            chatButton.setVisibility(Button.GONE);
            ticketButton.setVisibility(ImageButton.GONE);

            likeButton.setVisibility(ImageButton.GONE);
            optionsButton.setVisibility(ImageButton.GONE);
            followButton.setVisibility(Button.GONE);
        }

        else {
            joinButton.setVisibility(Button.VISIBLE);
            chatButton.setVisibility(Button.VISIBLE);
        }
    }

    public void setParticipatMode() {
        joinButton.setVisibility(Button.GONE);
        ratingLayout.setVisibility(RatingBar.VISIBLE);
    }

    @Override
    public void changeToChat() {
        Intent intent = getIntent();
        Integer id = intent.getIntExtra("id",0);
        intent = new Intent(EventActivity.this, chatActivity.class);
        SharedPreferences chatPreferences = getChatPreferences();
        SharedPreferences userPreferences = getUserPreferences();
        chatPreferences.edit().putInt("creatorID",userPreferences.getInt("id",0)).apply();
        chatPreferences.edit().putInt("eventID",id).apply();
        startActivity(intent);
    }

    @Override
    public void setImagesURLs(String[] images_url) {
        imagesURLs = images_url;
        ImageAdapter adapter = new ImageAdapter(this, imagesURLs);
        viewPager.setAdapter(adapter);
    }

    public void setRatingValue(Integer ratingValue) {
        companyRating.setRating(ratingValue);
    }


    public void changeToEditEvent(Event e){
        Intent intent = new Intent(EventActivity.this, CreateEventActivity.class);
        String title = e.getTitle();
        String description = e.getDescription();
        String[] images = new String[]{};
        String start_date = e.getStart_date();
        String end_date = e.getEnd_date();
        String start_time = e.getStart_time();
        String end_time = e.getEnd_time();
        Integer capacity = e.getCapacity();
        Integer price = e.getPrice();
        String location_lat = e.getLatitude();
        String location_long = e.getLongitude();

        // Event parameters:
        intent.putExtra("existing", true);
        intent.putExtra("event_id", mPresenter.getId());
        intent.putExtra("event_title", title);
        intent.putExtra("event_description", description);
        intent.putExtra("event_images",images);
        intent.putExtra("event_start_date",start_date);
        intent.putExtra("event_start_time",start_time);
        intent.putExtra("event_end_date",end_date);
        intent.putExtra("event_end_time",end_time);
        intent.putExtra("event_capacity",capacity);
        intent.putExtra("event_price",price);
        intent.putExtra("event_latitude",location_lat);
        intent.putExtra("event_longitude",location_long);

        startActivity(intent);
    }

    public void setEvent(Event e){
        title.setText(e.getTitle());
        description.setText(e.getDescription());
        author.setText(e.getAuthor());
    }

    @Override
    public void changeToCompanyHomePage() {
        Intent intent = new Intent(EventActivity.this, companyHomePageActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.JoinButton) mPresenter.joinEvent();
        else if(v.getId() == R.id.chat) mPresenter.chatEvent();
        else if(v.getId() == R.id.likeButton) mPresenter.likeEvent();
        else if (v.getId() == R.id.ticketButton) {
            Intent intent = new Intent(EventActivity.this, PopUpTicketActivity.class);
            intent.putExtra("event_id", mPresenter.getId());
            intent.putExtra("event_title", getEventTitle());
            this.startActivity(intent);
        }
        else if(v.getId() == R.id.follow) mPresenter.followCompany();
        else if (v.getId() == R.id.eventOtherOptionsButton) {
            PopupMenu menu = new PopupMenu(this, optionsButton);
            menu.getMenu().add(getString(R.string.report_event));

            menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getTitle() == getString(R.string.report_event) ) {
                        showReportDialog();
                        //dialogBuilder.show();
                    }
                        //mPresenter.reportEvent();
                    return true;
                }
            });

            menu.show();
        }
        else if(v.getId() == R.id.calendarButton){
            Intent intent = new Intent(Intent.ACTION_INSERT);
            intent.setData(CalendarContract.Events.CONTENT_URI);
            intent.putExtra(CalendarContract.Events.TITLE, title.getText().toString());
            intent.putExtra(CalendarContract.Events.DESCRIPTION, description.getText().toString());

            String[] di = fechaIni.replaceAll("\\s", "").split("/");
            String[] df = fechaFin.replaceAll("\\s", "").split("/");

            String[] hi = horaIni.split(":");
            String[] he = horaFin.split(":");
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(di[0]));
            cal.set(Calendar.MONTH, Integer.parseInt(di[1])-1);
            cal.set(Calendar.YEAR, Integer.parseInt(di[2]));
            cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hi[0]));
            cal.set(Calendar.MINUTE, Integer.parseInt(hi[1]));

            Calendar cal2 = Calendar.getInstance();
            cal2.set(Calendar.DAY_OF_MONTH, Integer.parseInt(df[0]));
            cal2.set(Calendar.MONTH, Integer.parseInt(df[1])-1);
            cal2.set(Calendar.YEAR, Integer.parseInt(df[2]));
            cal2.set(Calendar.HOUR_OF_DAY, Integer.parseInt(he[0]));
            cal2.set(Calendar.MINUTE, Integer.parseInt(he[1]));

            //LatLng loc = new LatLng(Double.valueOf(latitude), Double.valueOf(longitude));

            //String add = getCityName(loc);

            intent.putExtra(CalendarContract.Events.EVENT_LOCATION, latitude + ", "+ longitude);
            intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, cal.getTimeInMillis());
            intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, cal2.getTimeInMillis());

            startActivity(intent);
        }
        else if(v.getId() == R.id.shareButton) {
            mPresenter.shareEvent();
        }
        else if(v.getId() == R.id.CompanyProfileImage){
            // TODO Añadir informacion de la empresa en el intent
            Intent intent = new Intent(this, companyPublicPageActivity.class);
            intent.putExtra("company_name", mPresenter.getCreatorName());
            intent.putExtra("company_id", mPresenter.getCreatorId());
            startActivity(intent);
        }
    }

    public Context getContext()
    {
        return this;
    }

    public void startShareIntent(String text)
    {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }

    public void showReportDialog()
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle(getString(R.string.report_confirm_title));
        dialogBuilder.setMessage(getString(R.string.irreversible_action));
        dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        dialogBuilder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mPresenter.reportEvent();
            }
        });
        dialogBuilder.create().show();
    }

    public void finishActivity() {
        finish();
    }


    public SharedPreferences getUserPreferences() {
        return getSharedPreferences("userPreferences", this.MODE_PRIVATE);
    }

    private SharedPreferences getChatPreferences() {
        return getSharedPreferences("chatPreferences", this.MODE_PRIVATE);
    }

    @Override
    public void setLocation(String latitude, String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public LatLng getLocation(){
        if(latitude == null || longitude == null){
            return new LatLng(Double.parseDouble("0.0"),Double.parseDouble("0.0"));
        }
        return new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude));

    }

    public String getEventTitle(){
        return title.getText().toString();
    }

    public void loadMap(){
        //Initialize fragment
        eventLocationFragment = new EventLocationFragment();

        //Open fragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.mapview, eventLocationFragment)
                .commit();
    }

    @Override
    public double getUserLatitude() {
        return Double.parseDouble(userLatitude);
    }

    @Override
    public double getUserLongitude() {
        return Double.parseDouble(userLongitude);
    }

    public double getDistance(double lat1, double lat2, double lon1, double lon2, double el1, double el2){

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c; // convert to kilometers

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return round(Math.sqrt(distance),2);
    }

    public void setUserLocation(LatLng userLoc) {
        userLatitude = String.valueOf(userLoc.latitude);
        userLongitude = String.valueOf(userLoc.longitude);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public void showError(String title, String description)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle(title);
        alertDialogBuilder
                .setMessage(description)
                .setNeutralButton(getText(R.string.accept), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public void showToast(String text) {
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(this, text, duration);
        toast.show();
    }

    public void setLoading(boolean loading) {
        if (loading) progressBar.setVisibility(ProgressBar.VISIBLE);
        else progressBar.setVisibility(ProgressBar.INVISIBLE);
    }
}