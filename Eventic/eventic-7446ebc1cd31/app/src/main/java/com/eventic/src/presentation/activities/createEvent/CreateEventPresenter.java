package com.eventic.src.presentation.activities.createEvent;

import android.content.SharedPreferences;
import android.graphics.Bitmap;


import com.eventic.src.domain.Event;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import com.eventic.src.domain.EventTag;
import com.eventic.src.domain.Tag;
import com.eventic.src.presentation.activities.event.EventContract;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CreateEventPresenter implements CreateEventContract.Presenter {

    private boolean existingEvent = false;
    private int event_id;

    List<Tag> originalTags;
    List<Tag> availableTags, selectedTags;
    List<Tag> tagsToAdd, tagsToRemove;
    private int tagsAdded, tagsRemoved;


    private CreateEventContract.View mView;
    public CreateEventPresenter(CreateEventContract.View view) {
        mView = view;
    }
    private ArrayList<File> files;

    @Override
    public ArrayList<File> convertBitmap(ArrayList<Bitmap> images) throws IOException {
        files = new ArrayList<>();
        for(int i = 0; i<images.size(); ++i) {
            //create a file to write bitmap data
            File f = new File(((CreateEventActivity) mView).getCacheDir(), "event_image_" + i + ".jpg");
            f.createNewFile();

            //Convert bitmap to byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            images.get(i).compress(Bitmap.CompressFormat.JPEG, 100, bos);

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
            files.add(f);
        }
        return files;
    }

    public void setExistingEvent(int event_id) {
        existingEvent = true;
        this.event_id = event_id;
    }

    public void confirmEvent(ArrayList<File> images) {

        SharedPreferences userPreferences = mView.getUserPreferences();
        String client_token = userPreferences.getString("token", null);


        Event e = new Event(
                null,
                mView.getEventTitle(),
                mView.getEventDescription(),
                mView.getEventAuthor(),
                null,
                mView.getEventStartDate(),
                mView.getEventEndDate(),
                mView.getEventCapacity(),
                mView.getEventLatitude(),
                mView.getEventLongitude(),
                mView.getEventParticipants(),
                mView.getEventPrice(),
                mView.getEventStartTime(),
                mView.getEventEndTime(),
                client_token
        );
        e.setId_creator(userPreferences.getInt("id", 0));

        tagsToAdd = new ArrayList<Tag>();
        tagsToRemove = new ArrayList<Tag>();

        if(existingEvent) {
            e.setId(event_id);
            editEvent(e);
        }
        else {
            tagsAdded = 0;
            tagsRemoved = 0;
            createNewEvent(e);
        }
    }

    void setupTags(List<Tag> tags) {
        availableTags = tags;
        Collections.sort(availableTags);

        selectedTags = new ArrayList<Tag>();

        if (existingEvent) loadEventTags();
        else {
            originalTags = new ArrayList<Tag>();
            mView.setLoading(false);
        }
    }

    public void selectTags() {
        String[] tags = new String[availableTags.size()];
        for (int i = 0; i < availableTags.size(); ++i) tags[i] = availableTags.get(i).getTag_name();
        mView.openDialog(tags);
    }

    public void assignSelectedTags() {
        tagsToRemove = new ArrayList<Tag>();
        tagsToAdd = new ArrayList<Tag>(selectedTags);

        for (int i = 0; i < tagsToAdd.size(); ++i) assignTag(tagsToAdd.get(i));
    }

    public void editTags() {
        List<Tag> addedTags = new ArrayList<Tag>(selectedTags);
        List<Tag> removedTags = new ArrayList<Tag>(originalTags);
        addedTags.removeAll(originalTags);
        removedTags.removeAll(selectedTags);

        //System.out.print("\nORIGINAL TAGS: [");
        //for (int i = 0; i < originalTags.size(); ++i) System.out.print(originalTags.get(i).getTag_name() + " ");
        //System.out.print("]\nSELECTED TAGS: [");
        //for (int i = 0; i < selectedTags.size(); ++i) System.out.print(selectedTags.get(i).getTag_name() + " ");
        //System.out.print("]\nTAGS TO ADD: [");
        //for (int i = 0; i < addedTags.size(); ++i) System.out.print(addedTags.get(i).getTag_name() + " ");
        //System.out.print("]\nTAGS TO REMOVE: [");
        //for (int i = 0; i < removedTags.size(); ++i) System.out.print(removedTags.get(i).getTag_name() + " ");
        //System.out.println("]");

        tagsAdded = 0;
        tagsRemoved = 0;

        for (int i = 0; i < addedTags.size(); ++i) assignTag(addedTags.get(i));
        for (int i = 0; i < removedTags.size(); ++i) unassignTag(removedTags.get(i));
    }

    public void tagAdded() {
        tagsAdded++;

        if (tagsAdded >= tagsToAdd.size() && tagsRemoved >= tagsToRemove.size())
            tagsFinished();
    }

    public void tagRemoved() {
        tagsRemoved++;
        if (tagsAdded >= tagsToAdd.size() && tagsRemoved >= tagsToRemove.size())
            tagsFinished();
    }

    public void tagsFinished() {
        mView.setLoading(false);
        if (!existingEvent) mView.changeToEvent(event_id);
        mView.finishActivity();
    }


    void addTag(Tag tag) {
        int index = availableTags.indexOf(tag);

        System.out.print("Trying to find \"" + tag.getTag_name() + "\" in availableTags = {");
        for (int i = 0; i < availableTags.size(); ++i) System.out.print(availableTags.get(i).getTag_name() + " ");
        System.out.println("}.");

        if (index < 0) mView.showError("Unexpected Tag", "Did not found tag \"" + tag.getTag_name() + "\" in the system database.");
        else selectTag(index);
    }

    public void selectTag(int index) {
        Tag selectedTag = availableTags.get(index);

        selectedTags.add(selectedTag);
        availableTags.remove(index);
        mView.addTag(selectedTag);

        System.out.println("[NEW TAG SELECTED]: " + selectedTag.getTag_name());
        System.out.print("\nSELECTED TAGS: [");
        for (int i = 0; i < selectedTags.size(); ++i) System.out.print(selectedTags.get(i).getTag_name() + " ");
        System.out.println("]");

    }

    public void removeTag(Tag tag) {
        int index = selectedTags.indexOf(tag);

        if (index >= 0) {
            availableTags.add(tag);
            Collections.sort(availableTags);
            selectedTags.remove(index);
            mView.removeTag(index);
        }
    }

    public void createNewEvent(Event e) {
        mView.setLoading(true);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://eventic-api.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        CreateEventContract.JsonHttpApi jsonHttpApi = retrofit.create(CreateEventContract.JsonHttpApi.class);


        Call<Event> call = jsonHttpApi.newEvent(e);

        call.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {
                Event eventResponse = response.body();

                if (!response.isSuccessful()) {
                    mView.showError("Error creating the event", "Check that all parameters are correct.");
                    mView.setLoading(false);
                    return;
                }

                event_id = eventResponse.getId();
                assignSelectedTags();
                    addImage(files, event_id);
                    mView.changeToEvent(eventResponse.getId());
                mView.finishActivity();
            }

            @Override
            public void onFailure(Call<Event> call, Throwable t) {
                mView.showError("Connection Error", "Unable to connect to the server. Try again later.");
                mView.setLoading(false);
            }
        });

    }

    @Override
    public void assignTag(Tag tag) {

        System.out.println("ASSIGNING TAG " + tag.getTag_name());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://eventic-api.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        CreateEventContract.JsonHttpApi jsonHttpApi = retrofit.create(CreateEventContract.JsonHttpApi.class);

        Call<Void> call = jsonHttpApi.assignTag(new EventTag(event_id, tag.getId()));

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if (!response.isSuccessful()) {

                    mView.showAddTagError("Tag error", "There was an unexpected error trying to set the tag \"" + tag.getTag_name() + "\"");
                    return;
                }
                tagAdded();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                mView.showAddTagError("Connection Error", "Event was created but tags are not correctly assigned due to a connection error. Try again later.");
            }
        });
    }

    public void unassignTag(Tag tag) {
        System.out.println("UNASSIGNING TAGS:" + "\n - event_id = " + event_id + "\n - tag = " + tag.getId());
        tagRemoved();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://eventic-api.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        CreateEventContract.JsonHttpApi jsonHttpApi = retrofit.create(CreateEventContract.JsonHttpApi.class);

        Call<Void> call = jsonHttpApi.unassignTag(event_id, tag.getId());

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if (!response.isSuccessful()) mView.showRemovedTagError("Tag error", "There was an unexpected error trying to set the tag \"" + tag.getTag_name() + "\"");

                tagRemoved();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                mView.showRemovedTagError("Connection Error", "Event was created but tags are not correctly assigned due to a connection error. Try again later.");
            }
        });

    }

    private void addImage(ArrayList<File> files, Integer event_id) {
        MultipartBody.Part[] body =  new MultipartBody.Part[files.size()];
        for(int i=0;i<files.size();i++){
            File file = new File(String.valueOf(files.get(i)));
            RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
            body[i] = MultipartBody.Part.createFormData("event_image_data[]", file.getName(), reqFile);
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://eventic-api.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        CreateEventContract.JsonHttpApi jsonHttpApi = retrofit.create(CreateEventContract.JsonHttpApi.class);

        SharedPreferences userPreferences = mView.getUserPreferences();
        String token = userPreferences.getString("token", null);
        Call<Event> call = jsonHttpApi.addImages(event_id, token, body);

        call.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {
                Event eventResponse = response.body();

                if (!response.isSuccessful()) System.out.println(response.message());
            }

            @Override
            public void onFailure(Call<Event> call, Throwable t) { }
        });
    }

    public void editEvent(Event e) {
        mView.setLoading(true);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://eventic-api.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        CreateEventContract.JsonHttpApi jsonHttpApi = retrofit.create(CreateEventContract.JsonHttpApi.class);

        Call<Event> call = jsonHttpApi.editEvent(e.getId(), e);

        call.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {
                Event eventResponse = response.body();

                if (!response.isSuccessful()) {
                    mView.setLoading(false);
                }

                editTags();
                addImage(files, event_id);

            }

            @Override
            public void onFailure(Call<Event> call, Throwable t) {}
        });
    }

    public void loadTags() {
        mView.setLoading(true);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://eventic-api.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        CreateEventContract.JsonHttpApi jsonHttpApi = retrofit.create(CreateEventContract.JsonHttpApi.class);

        Call<List<Tag>> call = jsonHttpApi.getTags();

        call.enqueue(new Callback<List<Tag>>() {
            @Override
            public void onResponse(Call<List<Tag>> call, Response<List<Tag>> response) {
                List<Tag> eventResponse = response.body();

                if (!response.isSuccessful()) {
                    mView.showError("Error loading system tags", "Unexpected error.");
                    mView.setLoading(false);
                    return;
                }

                setupTags(eventResponse);
            }

            @Override
            public void onFailure(Call<List<Tag>> call, Throwable t) {
                mView.showRemovedTagError("Connection Error", "Error connecting to the server.\nNo available tags.");
                mView.setLoading(false);
            }
        });
    }

    void loadEventTags() {
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
                    mView.setLoading(false);
                    return;
                }

                originalTags = new ArrayList<Tag>();

                for (int i = 0; i < tagsResponse.size(); ++i) {
                    boolean found = false;
                    for (int j = 0; j < availableTags.size() && !found; ++j) {
                        if (tagsResponse.get(i).getId().equals(availableTags.get(j).getId())) {
                            found = true;
                            originalTags.add(availableTags.get(j));
                            //selectedTags.add(availableTags.get(j));
                            //availableTags.remove(j);
                            --j;
                        }
                    }
                    if (!found) mView.showError("Unexpected Tag", "This Event had the tag \"" + tagsResponse.get(i) + "\" but it doesn't exist in the system database.");
                }
                for (int i = 0; i < originalTags.size(); ++i) addTag(originalTags.get(i));
                mView.setLoading(false);
            }

            @Override
            public void onFailure(Call<List<Tag>> call, Throwable t) {
                mView.setLoading(false);
                return;
            }
        });
    }
}
