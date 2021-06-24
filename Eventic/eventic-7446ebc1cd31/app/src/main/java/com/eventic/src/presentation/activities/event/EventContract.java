package com.eventic.src.presentation.activities.event;

import android.content.Context;
import android.content.SharedPreferences;

import com.eventic.src.domain.Event;
import com.eventic.src.domain.Tag;
import com.eventic.src.domain.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface EventContract {

    /** Represents the View in MVP. */
    interface View {

        Context getContext();

        void setTitle(String title);
        void setDescription(String description);
        void setAuthor(String author);
        void setLiked(boolean liked);
        void setDate(String date);
        void setPrice(String price);
        void haReportat();
        void setDistance(String distance);
        void setCapacity(String capacity);
        void setJoined(boolean participant);
        void setFollowed(boolean follow);
        void setFull();
        void setEditMode(boolean editMode);
        void setViewOnlyMode(boolean viewMode);
        void setParticipatMode();
        void setEvent(Event eventResponse);
        void addTag(Tag tag);
        void resetTags();

        void changeToEditEvent(Event e);
        void changeToCompanyHomePage();
        void startShareIntent(String text);

        void finishActivity();
        SharedPreferences getUserPreferences();

        void setLocation(String latitude, String longitude);

        void loadMap();

        double getUserLatitude();

        double getUserLongitude();

        void setLoading(boolean loading);
        void showError(String title, String description);
        void showToast(String text);

        void setDateIni(String start_date);
        void setDateFin(String end_date);
        void setHoraIni(String start_time);
        void setHoraFin(String end_time);

        void changeToChat();

        void setImagesURLs(String[] images_url);

        void setAuthorProfilePic(String author);

        void setEventRating(float rating);
        void setAuthorRating(float rating);
    }

    /** Represents the Presenter in MVP. */
    interface Presenter {

        void loadEvent();

        Integer getId();
        void setId(Integer id);
        String getEventUri();
        void compEvent();
        void likeEvent();
        void followCompany();
        void joinEvent();
        void chatEvent();
        void openCalendar();

        void shareEvent();

        void loadEventInfo(Integer id);
        void loadLikeState(String token,Integer evento_id);
        void loadJoinedState(Integer user_id);

        void putLike(String token ,Integer evento_id);
        void quitLike(String token ,Integer evento_id);
        void joinEvent(String token ,Integer evento_id);
        void deleteEvent(Integer id);
        void reportEvent();

        void rateEvent(float rating);

        String getCreatorName();

        Integer getCreatorId();
    }

    interface JsonHttpApi {

        @GET("users/{id}")
        Call<User> getUser(@Path("id") Integer id);

        @GET("evento/{id}")
        Call<Event> getEventById(@Path("id") Integer id);

        @GET("eventotag/{id}")
        Call<List<Tag>> getTagsById(@Path("id") Integer id);

        @GET("entrada_usuarios/{user_id}")
        Call<List<Event>> getParticipations(@Path("user_id") Integer user_id);

        @GET("es_participant")
        Call<Boolean> getParticipa(@Query("token") String token, @Query("evento_id") Integer evento_id);

        @POST("entrada_usuarios")
        Call<Void> joinEvent(@Query("token") String token, @Query("evento_id") Integer evento_id);

        @DELETE("entrada_usuarios")
        Call<Void> dropEvent(@Query("token") String token, @Query("evento_id") Integer evento_id);

        @DELETE("evento/{id}")
        Call<Void> deleteEvent(@Path("id") Integer id);

        @DELETE("event_tags/{id}")
        Call<Void> deleteTag(@Path("id") Integer id);

        @GET("like_event")
        Call<Boolean> isLiked(@Query("token") String token, @Query("evento_id") Integer evento_id);

        @POST("favourites")
        Call<Void> putLike(@Query("token") String token, @Query("evento_id") Integer evento_id);

        @POST("follower")
        Call<Void> followCompany( @Query("customer_id") Integer customer_id, @Query("company_id") Integer company_id, @Query("token") String token);

        @DELETE("favourites")
        Call<Void> quitLike(@Query("token") String token, @Query("evento_id") Integer evento_id);

        @PUT("report/{id}")
        Call<Void> reportEvent(@Path("id") Integer id, @Query("token") String token);

        @GET("report/{id}")
        Call<Boolean> compEvent(@Path("id") Integer id, @Query("token") String token);

        @DELETE("follower")
        Call<Void> quitFollowCompany(@Query("customer_id")Integer customer_id,@Query("company_id")Integer company_id,@Query("token") String token);

        @GET("follow_company")
        Call<Boolean> isFollowed(@Query("customer_id")Integer customer_id, @Query("company_id") Integer company_id,@Query("token") String token);

        @GET("es_confirmat")
        Call<Boolean> haParticipat(@Query("token")String token, @Query("evento_id") Integer event_id);

        @POST("ratings")
        Call<Void> modificarValoracio(@Query("rating")float rating, @Query("token")String token, @Query("evento_id") Integer event_id, @Query("customer_id") Integer customer_id, @Query("company_id") Integer company_id);

        @GET("rating_user_evento")
        Call<Float> getRating(@Query("customer_id") Integer customer_id, @Query("token")String token, @Query("evento_id") Integer event_id);

        @GET("ratings_company")
        Call<Float> getCompanyRating(@Query("company_id") Integer company_id);
    }

}
