package com.eventic.src.presentation.activities.createEvent;

import android.content.SharedPreferences;
import android.graphics.Bitmap;

import com.eventic.src.domain.Event;
import com.eventic.src.domain.EventTag;
import com.eventic.src.domain.Tag;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CreateEventContract {
    interface View {

        String getEventTitle();
        String getEventDescription();
        String getEventAuthor();
        //getImages();
        String getEventStartDate();
        String getEventEndDate();
        String getEventStartTime();
        String getEventEndTime();
        int getEventCapacity();
        String getEventLatitude();
        String getEventLongitude();
        int getEventParticipants();
        int getEventPrice();

        void loadParameters();

        void changeToLogin();
        void changeToEvent(Integer event_id);

        void openDialog(String[] options);
        void addTag(Tag tag);
        void removeTag(int index);

        void showError(String title, String description);
        void showAddTagError(String title, String description);
        void showRemovedTagError(String title, String description);

        void setLoading(boolean loading);
        void finishActivity();

        SharedPreferences getEventPreferences();

        SharedPreferences getUserPreferences();
    }

    interface Presenter {

        void setExistingEvent(int event_id);
        void confirmEvent(ArrayList<File> images);
        void createNewEvent(Event e);
        void assignTag(Tag tag);
        void tagAdded();
        void tagRemoved();
        void editEvent(Event e);
        ArrayList<File> convertBitmap(ArrayList<Bitmap> images) throws IOException;
        void loadTags();
        void selectTags();
        void selectTag(int index);
        void removeTag(Tag tag);

    }


    interface JsonHttpApi {

        @POST("crearevento")
        Call<Event> newEvent(@Body Event e);

        @PUT("evento/{id}")
        Call<Event> editEvent(@Path("id") Integer id, @Body Event e);

        @Multipart
        @PUT("evento/{id}")
        Call<Event> addImages(@Path("id") Integer id, @Query("token") String token, @Part MultipartBody.Part[] event_image_data);

        @GET("eventos")
        Call<List<Event>> getEvents();

        @GET("tags")
        Call<List<Tag>> getTags();

        @GET("eventotag/{id}")
        Call<List<Tag>> getTagsById(@Path("id") Integer id);

        @POST("event_tags")
        Call<Void> assignTag(@Body EventTag eventTag);

        @DELETE("event_tags")
        Call<Void> unassignTag(@Query("evento_id") Integer eventId, @Query("tag_id") Integer tagId);

    }
}
