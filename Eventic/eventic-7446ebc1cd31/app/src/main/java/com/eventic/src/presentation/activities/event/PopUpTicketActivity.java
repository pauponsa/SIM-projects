package com.eventic.src.presentation.activities.event;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.eventic.R;

import net.glxn.qrgen.android.QRCode;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class PopUpTicketActivity extends AppCompatActivity implements  View.OnClickListener  {

    private TextView eventTitle, userName;
    private ImageView QrImage;
    private Integer event_id;


    interface JsonHttpApi {
        @GET("participa")
        Call<String> getTicketString(@Query("token") String user_token, @Query("evento_id") Integer event_id);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up_ticket);
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
        event_id = intent.getIntExtra("event_id", 0);
        eventTitle.setText(intent.getStringExtra("event_title"));
        userName.setText(getSharedPreferences("userPreferences", this.MODE_PRIVATE).getString("username", ""));


        getTicketCode();
    }
    private void initViews() {
        eventTitle = findViewById(R.id.ticketEventTitle);
        userName = findViewById(R.id.ticketUserName);
        QrImage = findViewById(R.id.QrCodeImage);
    }

    @Override
    public void onClick(View v) {
    }

    private void getTicketCode() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://eventic-api.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        PopUpTicketActivity.JsonHttpApi jsonHttpApi = retrofit.create(PopUpTicketActivity.JsonHttpApi.class);


        String login_token = getSharedPreferences("userPreferences", this.MODE_PRIVATE).getString("token", "");

        Call<String> call = jsonHttpApi.getTicketString(login_token, event_id);


        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if (!response.isSuccessful()) {
                    System.out.println(response.message());
                    return;
                }

                String ticket_token = response.body();
                int backgroudColor = getResources().getColor(R.color.transparent);
                int foregroundColor = getResources().getColor(R.color.mainText);
                Bitmap QrBitmap = QRCode.from(ticket_token).withColor(foregroundColor, backgroudColor).bitmap();
                QrImage.setImageBitmap(QrBitmap);


            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                //mView.getCompanyEventsFragment().setLoading(false);
                System.out.println("Connection FAILED");
            }
        });
    }
}