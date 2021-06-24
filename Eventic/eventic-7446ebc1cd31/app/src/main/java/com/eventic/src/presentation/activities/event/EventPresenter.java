package com.eventic.src.presentation.activities.event;

import android.graphics.Paint;
import android.widget.ImageButton;

import com.eventic.src.domain.Event;
import com.eventic.src.domain.Tag;
import com.eventic.src.domain.User;
import com.eventic.src.presentation.activities.userHomePage.userHomePageContract;
import com.example.eventic.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EventPresenter implements EventContract.Presenter {

    private EventContract.View mView;

    public EventPresenter(EventContract.View view) {
        mView = view;
    }
    private Integer creatorId;
    private Integer event_id;
    private String event_title;
    private boolean liked;
    private boolean joined;
    private boolean followed;
    private String login_token;
    private String user_role;
    private Integer user_id;
    private Event event = new Event();
    double userLatitude;
    double userLongitude;

    private int deletedTags;
    private List<Tag> eventTags;

    public Integer getId() {
        return event_id;
    }

    public void setId(Integer id) {
        this.event_id = id;
    }

    public String getEventUri()
    {
        return "https://eventic-api.herokuapp.com/evento/" + String.valueOf(event_id);
    }

    public void shareEvent()
    {
        String text = mView.getContext().getResources().getString(R.string.share_event_text, event_title);
        text = text + "\n\n" + getEventUri();
        mView.startShareIntent(text);
    }

    public Integer getAuthor() {
        return event.getId_creator();
    }

    public void loadEvent() {
        login_token = mView.getUserPreferences().getString("token", null);
        user_id = mView.getUserPreferences().getInt("id", 0);
        user_role = mView.getUserPreferences().getString("role", null);

        loadEventInfo(event_id);
        loadLikeState(login_token, event_id);


//        loadJoinedState(user_id);
    }

    public void likeEvent() {
        liked = !liked;
        mView.setLiked(liked);

        if (liked) putLike(login_token, event_id);
        else quitLike(login_token, event_id);
    }

    @Override
    public void openCalendar() {

    }

    public void haReportat(){

        haReportat();

    }

    public void joinEvent() {
        if (user_role.equals("company")) {
            deleteEvent(event_id);
        }

        else if (!joined && event.getParticipants() >= event.getCapacity())
            mView.showToast("You can't join a full event.");

        else {
            joined = !joined;
            mView.setJoined(joined);

            if (joined) joinEvent(login_token, event_id);
            else dropEvent(login_token, event_id);
        }
    }

    public void chatEvent() {
        if (user_role.equals("company")) mView.changeToEditEvent(event);
        else mView.changeToChat();
    }

    public void followCompany(){
        //CRIDA PER AFEGIR EL FOLLOW
                Integer company_id = getAuthor();
                followed = !followed;

                if(followed) followCompany(user_id,company_id, login_token);
                else quitFollowCompany(user_id,company_id, login_token);

    }



    /** --- RETROFIT Calls --- **/


    public void loadLikeState(String token, Integer evento_id) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://eventic-api.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        EventContract.JsonHttpApi jsonHttpApi = retrofit.create(EventContract.JsonHttpApi.class);

        Call<Boolean> call = jsonHttpApi.isLiked(token,evento_id);

        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (!response.isSuccessful()) {
                    System.out.println("NO SE HA PODIDO CARGAR EL LIKE");
                    System.out.println(response.message());
                    return;
                }

                liked = response.body();

                mView.setLiked(liked);
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
            }
        });
    }

    public void loadFollowState(Integer customer_id,Integer company_id,String token){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://eventic-api.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        EventContract.JsonHttpApi jsonHttpApi = retrofit.create(EventContract.JsonHttpApi.class);

        Call<Boolean> call = jsonHttpApi.isFollowed(customer_id, company_id, token);
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (!response.isSuccessful()) {
                    System.out.println("NO SE HA PODIDO CARGAR EL FOLLOW");
                    System.out.println(response.message());
                    return;
                }

                followed = response.body();

                mView.setFollowed(followed);
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
            }
        });

    }

    public String getCreatorName(){
        return event.getAuthor();
    }

    public Integer getCreatorId(){
        return event.getId_creator();
    }

    public void joinEvent(String token, Integer evento_id) {
        event.setParticipants(event.getParticipants() + 1);
        mView.setCapacity(event.getParticipants() + "/" + event.getCapacity());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://eventic-api.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        EventContract.JsonHttpApi jsonHttpApi = retrofit.create(EventContract.JsonHttpApi.class);

        Call<Void> call = jsonHttpApi.joinEvent(token,evento_id);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                System.out.println("NO SE HA PODIDO CARGAR EL JOIN STATE");
                if (!response.isSuccessful()) System.out.println(response.message());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
            }
        });
    }

    public void dropEvent(String token, Integer evento_id) {
        event.setParticipants(event.getParticipants() - 1);
        mView.setCapacity(event.getParticipants() + "/" + event.getCapacity());


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://eventic-api.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        EventContract.JsonHttpApi jsonHttpApi = retrofit.create(EventContract.JsonHttpApi.class);

        Call<Void> call = jsonHttpApi.dropEvent(token,evento_id);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) System.out.println(response.message());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
            }
        });
    }
    public void followCompany(Integer customer_id, Integer company_id, String token){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://eventic-api.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        EventContract.JsonHttpApi jsonHttpApi = retrofit.create(EventContract.JsonHttpApi.class);

        Call<Void> call = jsonHttpApi.followCompany(customer_id,company_id, token);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()){
                    System.out.println(response.message());
                    followed = !followed;
                }
                mView.setFollowed(followed);
            }


            @Override
            public void onFailure(Call<Void> call, Throwable t) {
            }
        });
    }
    public void quitFollowCompany(Integer customer_id, Integer company_id, String token) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://eventic-api.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        EventContract.JsonHttpApi jsonHttpApi = retrofit.create(EventContract.JsonHttpApi.class);

        Call<Void> call = jsonHttpApi.quitFollowCompany(customer_id,company_id,token);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()){
                    System.out.println(response.message());
                    followed = !followed;
                }
                mView.setFollowed(followed);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
            }
        });

    }


    public void putLike(String token, Integer evento_id) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://eventic-api.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        EventContract.JsonHttpApi jsonHttpApi = retrofit.create(EventContract.JsonHttpApi.class);

        Call<Void> call = jsonHttpApi.putLike(token,evento_id);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) System.out.println(response.message());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
            }
        });
    }

    public void quitLike(String token, Integer evento_id) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://eventic-api.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        EventContract.JsonHttpApi jsonHttpApi = retrofit.create(EventContract.JsonHttpApi.class);

        Call<Void> call = jsonHttpApi.quitLike(token,evento_id);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) System.out.println(response.message());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
            }
        });
    }

    public void loadJoinedState(Integer creator_id) {
        if (user_role.equals("company")) {
            if (mView.getUserPreferences().getInt("id", 0) == creator_id) mView.setEditMode(true);
            else mView.setViewOnlyMode(true);
            return;
        }
        else mView.setEditMode(false);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://eventic-api.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        EventContract.JsonHttpApi jsonHttpApi = retrofit.create(EventContract.JsonHttpApi.class);

        //Call<List<Event>> call = jsonHttpApi.getParticipations(user_id);
        Call<Boolean> call = jsonHttpApi.getParticipa(login_token, event_id);

        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (!response.isSuccessful()) {
                    System.out.println(response.message());
                    return;
                }

                mView.setJoined(response.body());
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
            }
        });

        Call<Boolean> call2 = jsonHttpApi.haParticipat(login_token, event_id);

        call2.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call2, Response<Boolean> response2) {
                if (!response2.isSuccessful()) {
                    return;
                }

                boolean participa = response2.body();
                if (participa) {
                    mView.setParticipatMode();
                    loadUserRating();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call2, Throwable t) {
            }
        });
    }

    public void loadUserRating()
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://eventic-api.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        EventContract.JsonHttpApi jsonHttpApi = retrofit.create(EventContract.JsonHttpApi.class);

        Call<Float> call = jsonHttpApi.getRating(user_id, login_token, event_id);

        call.enqueue(new Callback<Float>() {
            @Override
            public void onResponse(Call<Float> call, Response<Float> response) {
                if (!response.isSuccessful()) {
                    System.out.println("NO HA CARGADO BIEN EL RATING DEL USUARIO AL EVENTO");
                    System.out.println(response.message());
                    return;
                }

                System.out.println("RESPUESTA DEL RATING = " + response.body());

                mView.setEventRating(response.body());
            }

            @Override
            public void onFailure(Call<Float> call, Throwable t) {
            }
        });
    }

    public void rateEvent(float rating)
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://eventic-api.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        EventContract.JsonHttpApi jsonHttpApi = retrofit.create(EventContract.JsonHttpApi.class);


        Call<Void> call = jsonHttpApi.modificarValoracio(rating, login_token, event_id, user_id, creatorId);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    System.out.println("NO SE HA PODIDO MODIFICAR EL RATING DEL EVENTO");
                    System.out.println(response.message());
                    return;
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
            }
        });
    }


    @Override
    public void deleteEvent(Integer id) {
        mView.setLoading(true);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://eventic-api.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        EventContract.JsonHttpApi jsonHttpApi = retrofit.create(EventContract.JsonHttpApi.class);

        Call<Void> call = jsonHttpApi.deleteEvent(id);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    System.out.println("NO SE HA PODIDO BORRAR EL EVENTO");
                    System.out.println(response.message());
                    return;
                }
                //mView.changeToCompanyHomePage();
                mView.finishActivity();

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                mView.setLoading(false);
            }
        });
    }

    /*
    private void deleteTag(Integer tag_id)
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://eventic-api.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        EventContract.JsonHttpApi jsonHttpApi = retrofit.create(EventContract.JsonHttpApi.class);

        Call<Void> call = jsonHttpApi.deleteEvent(event_id);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                tagDeleted();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                tagDeleted();
            }
        });
    }
*/

    public void reportEvent() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://eventic-api.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        EventContract.JsonHttpApi jsonHttpApi = retrofit.create(EventContract.JsonHttpApi.class);

        String login_token = mView.getUserPreferences().getString("token", "");

        Call<Void> call = jsonHttpApi.reportEvent(event_id, login_token);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful())
                {
                    if (response.code() == 403)
                        mView.showError(mView.getContext().getString(R.string.report_fail_title), mView.getContext().getString(R.string.report_fail_already_reported));
                    else
                        mView.showError(mView.getContext().getString(R.string.report_fail_title), mView.getContext().getString(R.string.report_fail_must_participate));
                }
                else {
                    mView.showToast("Event reported successfully.");
                    mView.finishActivity();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                mView.showToast("Connection failed");
            }
        });
    }
    public void compEvent() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://eventic-api.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        EventContract.JsonHttpApi jsonHttpApi = retrofit.create(EventContract.JsonHttpApi.class);

        String login_token = mView.getUserPreferences().getString("token", "");

        Call<Boolean> call = jsonHttpApi.compEvent(event_id, login_token);

        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (!response.isSuccessful())
                    System.out.println("f");
                else {
                    if (response.body()) {
                        System.out.println("RESPONSE REPORT:");
                        System.out.println(response.body());
                        mView.haReportat();
                    }
                    else{
                        System.out.println("RESPONSE  SI FALS:");
                        System.out.println(response.body());

                    }

                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {

            }
        });
    }

    public void loadEventInfo(Integer event_id) {
        mView.setLoading(true);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://eventic-api.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        EventContract.JsonHttpApi jsonHttpApi = retrofit.create(EventContract.JsonHttpApi.class);

        Call<Event> call = jsonHttpApi.getEventById(event_id);

        call.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {
                Event eventResponse = response.body();
                if (!response.isSuccessful()) {
                    mView.setLoading(false);
                    return;
                }
                creatorId = eventResponse.getId_creator();
                event = eventResponse;
                event.setLongitude(eventResponse.getLongitude());
                event.setLatitude(eventResponse.getLatitude());
                mView.loadMap();
                mView.setLocation(eventResponse.getLatitude(),eventResponse.getLongitude());
                mView.setTitle(event.getTitle());
                event_title = event.getTitle();
                mView.setDescription(event.getDescription());
                mView.setAuthor(event.getAuthor());
                loadUserProfilePic(event.getId_creator());
                loadAuthorRating();
                mView.setDate(event.getStart_date() + " - " + event.getEnd_date());
                if(event.getPrice()!=0) mView.setPrice(event.getPrice().toString() + "€");
                else mView.setPrice(mView.getContext().getString(R.string.FREE));
                mView.setCapacity(event.getParticipants() + "/" + event.getCapacity());
                mView.setDateIni(event.getStart_date());
                mView.setDateFin(event.getEnd_date());
                mView.setHoraIni(event.getStart_time());
                mView.setHoraFin(event.getEnd_time());
                loadFollowState(user_id, event.getId_creator(), login_token);
                loadJoinedState(event.getId_creator());
                loadEventTags(event_id);

                if(event.getImages_url() != null){
                    String[] urls = new String[event.getImages_url().size()];
                    for (int i=0 ; i<event.getImages_url().size(); ++i){
                        urls[i]=event.getImages_url().get(i+"");
                    }
                    mView.setImagesURLs(urls);
                }
            }

            @Override
            public void onFailure(Call<Event> call, Throwable t) {
                mView.showError("Connection Error", "Event was created but tags are not correctly assigned due to a connection error. Try again later.");
                mView.setLoading(false);
                return;
            }
        });
    }

    private void loadUserProfilePic(Integer id) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://eventic-api.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        userHomePageContract.JsonHttpApi jsonHttpApi = retrofit.create(userHomePageContract.JsonHttpApi.class);

        Call<User> call = jsonHttpApi.getUser(id);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User usuari = response.body();
                if (!response.isSuccessful()) {
                    System.out.println("NO SE HA PODIDO CARGAR LA FOTO DEL USER");
                    System.out.println(response.message());
                }
                mView.setAuthorProfilePic(usuari.getImage());
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });
    }

    private void loadAuthorRating() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://eventic-api.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        EventContract.JsonHttpApi jsonHttpApi = retrofit.create(EventContract.JsonHttpApi.class);

        Call<Float> call = jsonHttpApi.getCompanyRating(creatorId);

        call.enqueue(new Callback<Float>() {
            @Override
            public void onResponse(Call<Float> call, Response<Float> response) {
                if (!response.isSuccessful()) {
                    System.out.println("NO SE HA PODIDO CARGAR EL RATING DEL ORGANIZADOR");
                    System.out.println(response.message());
                }
                mView.setAuthorRating(response.body());
            }
            @Override
            public void onFailure(Call<Float> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });
    }

    public void loadEventTags(Integer event_id)
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://eventic-api.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        EventContract.JsonHttpApi jsonHttpApi = retrofit.create(EventContract.JsonHttpApi.class);

        Call<List<Tag>> call = jsonHttpApi.getTagsById(event_id);

        call.enqueue(new Callback<List<Tag>>() {
            @Override
            public void onResponse(Call<List<Tag>> call, Response<List<Tag>> response) {
                List<Tag> tagsResponse = response.body();
                if (!response.isSuccessful()) {
                    mView.showError("Error loading tags", "Unexpected error.");
                    mView.setLoading(false);
                    return;
                }

                eventTags = tagsResponse;
                mView.resetTags();
                for (int i = 0; i < tagsResponse.size(); i++) {
                    mView.addTag(tagsResponse.get(i));
                }
                mView.setLoading(false);
            }


            @Override
            public void onFailure(Call<List<Tag>> call, Throwable t) {
                mView.showError("Connection Error", "Event was created but tags are not correctly assigned due to a connection error. Try again later.");
                mView.setLoading(false);
                return;
            }
        });
    }



}
