package com.eventic.src.presentation.activities.companyHomePage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.eventic.src.domain.Customer;
import com.eventic.src.domain.Entrada;
import com.eventic.src.domain.Event;
import com.eventic.src.domain.User;
import com.example.eventic.R;

import net.glxn.qrgen.android.QRCode;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class PopUpScanActivity extends AppCompatActivity implements  View.OnClickListener  {

    private TextView eventTitle, userName, textError;
    private String scan;
    private ImageView scanImage;
    private Integer event_id;
    private ProgressBar progressBar;

    private boolean nameLoaded, titleLoaded;
    private String username, eventtitle;


    interface JsonHttpApi {
        @PUT("participa")
        Call<Entrada> getTicketString(@Query("code") String code, @Query("id_creator") Integer id_creator);

        @GET("evento/{id}")
        Call<Event> getEventById(@Path("id") Integer id);

        @GET("users/{id}")
        Call<Customer> getUserById(@Path("id") Integer id);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up_scan);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(dm);
        initViews();
        int w = dm.widthPixels;
        int h = dm.heightPixels;

        getWindow().setLayout((int)(w),(int)(h));

        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.gravity = Gravity.CENTER;
        p.x = 0;
        p.y = -20;
        getWindow().setAttributes(p);

        Intent intent = getIntent();
        scan = intent.getStringExtra("scan");

        VerifyAssistance();
    }

    private void initViews() {
        eventTitle = findViewById(R.id.ticketEventTitle);
        userName = findViewById(R.id.ticketUserName);
        scanImage = findViewById(R.id.verifyScanImage);
        progressBar = findViewById(R.id.ScanProgressBar);
        textError = findViewById(R.id.scanTextError);
    }

    @Override
    public void onClick(View v) {
    }

    private void VerifyAssistance() {
        setLoading(true);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://eventic-api.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        PopUpScanActivity.JsonHttpApi jsonHttpApi = retrofit.create(PopUpScanActivity.JsonHttpApi.class);


        Integer creator_id = getSharedPreferences("userPreferences", this.MODE_PRIVATE).getInt("id", 0);

        Call<Entrada> call = jsonHttpApi.getTicketString(scan, creator_id);

        call.enqueue(new Callback<Entrada>() {
            @Override
            public void onResponse(Call<Entrada> call, Response<Entrada> response) {

                if (!response.isSuccessful()) {
                    setCorrect(false);
                    textError.setText(response.message());
                    return;
                }

                Entrada entrada = response.body();

                loadEventName(entrada.getEvento_id());
                loadUsername(entrada.getUser_id());

            }

            @Override
            public void onFailure(Call<Entrada> call, Throwable t) {
                //mView.getCompanyEventsFragment().setLoading(false);
                System.out.println("Connection FAILED");
            }
        });
    }

    private void loadUsername(Integer user_id)
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://eventic-api.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        PopUpScanActivity.JsonHttpApi jsonHttpApi = retrofit.create(PopUpScanActivity.JsonHttpApi.class);

        Call<Customer> call = jsonHttpApi.getUserById(user_id);

        call.enqueue(new Callback<Customer>() {
            @Override
            public void onResponse(Call<Customer> call, Response<Customer> response) {

                if (!response.isSuccessful()) {
                    eventTitle.setText("User Not Found");
                }

                Customer user = response.body();

                nameLoaded = true;
                if (!user.getName().isEmpty()) username = user.getName();
                else username = user.getUsername();
                if (titleLoaded) setCorrect(true);
            }

            @Override
            public void onFailure(Call<Customer> call, Throwable t) {
                System.out.println("Connection FAILED");
            }
        });
    }

    private void loadEventName(Integer event_id)
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://eventic-api.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        PopUpScanActivity.JsonHttpApi jsonHttpApi = retrofit.create(PopUpScanActivity.JsonHttpApi.class);

        Call<Event> call = jsonHttpApi.getEventById(event_id);

        call.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {

                if (!response.isSuccessful()) eventTitle.setText("Event Not Found");


                Event event = response.body();


                titleLoaded = true;
                eventtitle = event.getTitle();
                if (nameLoaded) setCorrect(true);
            }

            @Override
            public void onFailure(Call<Event> call, Throwable t) {
                System.out.println("Connection FAILED");
            }
        });
    }

    void setCorrect(boolean correct)
    {
        scanImage.setVisibility(ImageView.VISIBLE);

        if (correct)
        {
            scanImage.setImageResource(R.drawable.ic_check_circle_solid);
            scanImage.setColorFilter(getResources().getColor(R.color.eventic_blue));

            eventTitle.setText(eventtitle);
            userName.setText(username);

            textError.setText(getString(R.string.assistance_verified));
        }
        else
        {
            scanImage.setImageResource(R.drawable.ic_cross_circle_solid);
            scanImage.setColorFilter(getResources().getColor(R.color.red));

            eventTitle.setText(getString(R.string.invalid_code));

            textError.setText(getString(R.string.invalid_code_message));
        }

        setLoading(false);
    }

    void setLoading(boolean loading)
    {
        if (loading) progressBar.setVisibility(ProgressBar.VISIBLE);
        else progressBar.setVisibility(ProgressBar.GONE);
    }
}