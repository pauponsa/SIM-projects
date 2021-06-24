package com.eventic.src.presentation.activities.createEvent;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.eventic.src.domain.Tag;
import com.eventic.src.presentation.activities.login.LoginActivity;
import com.eventic.src.presentation.components.TagChip;
import com.eventic.src.presentation.activities.event.EventActivity;
import com.eventic.src.presentation.fragments.DatePickerFragment;
import com.eventic.src.presentation.fragments.MapFragment;
import com.eventic.src.presentation.fragments.TimePickerFragment;
import com.example.eventic.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.ShapeAppearanceModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class CreateEventActivity extends AppCompatActivity implements CreateEventContract.View, View.OnClickListener {

    private Button confirmButton, cancelButton;
    private EditText eventTitleInput, eventDescriptionInput, eventStartDateInput, eventEndDateInput, eventStartTimeInput, eventEndTimeInput, eventCapacityInput, eventPriceInput;
    private CreateEventContract.Presenter mPresenter;
    private ImageButton addImageButton;
    private LinearLayout imagesContainer, createEventTagsContainer;
    private Chip addTagChip;
    //private String[] selectedTags;
    private Integer event_id;
    private boolean existingEvent;
    private ArrayList<String> selectedTags;
    private MapFragment mapView;
    private ProgressBar progressBar;
    private ArrayList<Bitmap> images;
    private Bitmap image;
    private ShapeableImageView addedImage;
    private boolean eventHasImages;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        selectedTags = new ArrayList<String>();
        mPresenter = new CreateEventPresenter(this);
        eventHasImages = false;
        initView();
        loadParameters();
        mPresenter.loadTags();

        //Initialize fragment
        mapView = new MapFragment();

        //Open fragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.mapview, mapView)
                .commit();

    }

    public void loadParameters() {
        Intent intent = getIntent();
        existingEvent = intent.getBooleanExtra("existing", false);

        if (existingEvent) {
            cancelButton.setVisibility(View.VISIBLE);
            confirmButton.setText(getText(R.string.save_changes));

            int event_id = intent.getIntExtra("event_id", 0);
            String title = intent.getStringExtra("event_title");
            String description = intent.getStringExtra("event_description");
            String start_date = intent.getStringExtra("event_start_date");
            String end_date = intent.getStringExtra("event_end_date");
            String start_time = intent.getStringExtra("event_start_time");
            String end_time = intent.getStringExtra("event_end_time");
            Integer capacity = intent.getIntExtra("event_capacity",0);
            Integer price = intent.getIntExtra("event_price", 0);

            mPresenter.setExistingEvent(event_id);

            eventTitleInput.setText(title);
            eventDescriptionInput.setText(description);
            eventStartDateInput.setText(start_date);
            eventStartTimeInput.setText(start_time);
            eventEndDateInput.setText(end_date);
            eventEndTimeInput.setText(end_time);
            eventCapacityInput.setText(String.valueOf(capacity));
            eventPriceInput.setText(String.valueOf(price));
        }
        else {
            cancelButton.setVisibility(View.GONE);
            confirmButton.setText(getText(R.string.create_event));
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public void initView(){
        images = new ArrayList<>();
        ScrollView mainScrollView = findViewById(R.id.mainScrollView);
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

        addImageButton = findViewById(R.id.companyProfileImage);
        addImageButton.setOnClickListener(this);
        imagesContainer = findViewById(R.id.imagesContainter);
        confirmButton = findViewById(R.id.createEventConfirmButton);
        confirmButton.setOnClickListener(this);
        cancelButton = findViewById(R.id.createEventCancelButton);
        cancelButton.setOnClickListener(this);
        eventTitleInput = findViewById(R.id.eventTitleInput);
        eventTitleInput.setOnClickListener(this);
        eventDescriptionInput = findViewById(R.id.eventDescriptionInput);
        eventDescriptionInput.setOnClickListener(this);
        eventStartDateInput = findViewById(R.id.eventDateInput);
        eventStartDateInput.setOnClickListener(this);
        eventEndDateInput = findViewById(R.id.eventEndDateInput);
        eventEndDateInput.setOnClickListener(this);
        eventStartTimeInput = findViewById(R.id.eventStartTimeInput);
        eventStartTimeInput.setOnClickListener(this);
        eventCapacityInput = findViewById(R.id.eventCapacityInput);
        eventCapacityInput.setOnClickListener(this);
        eventPriceInput = findViewById(R.id.eventPriceInput);
        eventPriceInput.setOnClickListener(this);
        eventEndTimeInput = findViewById(R.id.eventEndTimeInput);
        eventEndTimeInput.setOnClickListener(this);
        createEventTagsContainer = findViewById(R.id.createEventTagsContainer);
        addTagChip = findViewById(R.id.addTagChip);
        addTagChip.setOnClickListener(this);
        progressBar = findViewById(R.id.createEventProgressBar);
        // Add any new view to "setLoading" method
    }



    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.createEventConfirmButton) {
            if(missingParams()){
                //Here we have to show a message error to the user
                System.out.println("Rellena los campos obligatorios");
            }
            else {
                ArrayList<File> files = new ArrayList<>();
                try {
                    files = mPresenter.convertBitmap(images);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mPresenter.confirmEvent(files);
            }
        }
        else if (v.getId() == R.id.createEventCancelButton) {
            this.finish();
        }
        else if (v.getId() == R.id.eventDateInput) {
            showStartDatePickerDialog();
        }
        else if (v.getId() == R.id.eventEndDateInput) {
            showEndDatePickerDialog();
        }
        else if (v.getId() == R.id.eventStartTimeInput) {
            showStartTimePickerDialog();
        }
        else if (v.getId() == R.id.eventEndTimeInput) {
            showEndTimePickerDialog();
        }
        else if (v.getId() == R.id.companyProfileImage) {
            selectImage(CreateEventActivity.this);
        }
        else if (v.getId() == R.id.addTagChip) {
            mPresenter.selectTags();
        }
        else if (v.getParent() != null && ((View)v.getParent()).getId() == R.id.createEventTagsContainer) {
            mPresenter.removeTag(((TagChip)v).getTag());
        }
    }

    private boolean missingParams() {
        if(eventTitleInput.getText().toString().equals("")) return true;
        else if(eventDescriptionInput.getText().toString().equals("")) return true;
        else if(eventStartDateInput.getText().toString().equals("")) return true;
        else if(eventEndDateInput.getText().toString().equals("")) return true;
        else if(eventStartTimeInput.getText().toString().equals("")) return true;
        else if(eventEndTimeInput.getText().toString().equals("")) return true;
        else if(eventCapacityInput.getText().toString().equals("")) return true;
        else if(eventPriceInput.getText().toString().equals("")) return true;
        else return !eventHasImages;
    }


    public void openDialog(String[] options) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getText(R.string.select_tag));

        for (int i = 0; i < options.length; ++i) { // Assign tag labels
            int textId = getResources().getIdentifier("tag_" + options[i], "string", getPackageName());
            if (textId != 0) {
                //textId = R.string.tag_not_found;
                options[i] = (getResources().getText(textId)).toString();
            }
        }

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mPresenter.selectTag(which);
            }
        });
        builder.show();
    }

    public void addTag(Tag tag) {
        TagChip newTag = new TagChip(this);
        newTag.setTag(tag);

        newTag.setChipIcon(getResources().getDrawable(R.drawable.ic_tag_remove));
        newTag.setChipBackgroundColorResource(R.color.eventic_light_blue);
        newTag.setChipIconTintResource(R.color.mainText);

        newTag.setOnClickListener(this);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(16, 0, 0, 0);
        createEventTagsContainer.addView(newTag, layoutParams);
    }


    public void removeTag(int index) {
        createEventTagsContainer.removeViewAt(index + 1);
    }

    private void addImage(Bitmap image) {
        addedImage = new ShapeableImageView(this);
        addedImage.setShapeAppearanceModel(ShapeAppearanceModel.builder().build().withCornerSize(getResources().getDimension(R.dimen.rounded_corners)));

        addedImage.setImageBitmap(image);

        addedImage.setLayoutParams(addImageButton.getLayoutParams());
        addedImage.setScaleType(ImageView.ScaleType.CENTER_CROP);

        imagesContainer.addView(addedImage);
        imagesContainer.invalidate();
        images.add(image);
        if(!eventHasImages) eventHasImages = true;
    }

    private void selectImage(Context context) {
        final CharSequence[] options = { getText(R.string.take_photo), getText(R.string.choose_from_gallery), getText(R.string.cancel)};

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

                    if (ContextCompat.checkSelfPermission(CreateEventActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) { // If app has permission
                        // You can use the API that requires the permission.
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pickPhoto , 1);

                    }
                    else if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) { // If user denied permission
                        // In an educational UI, explain to the user why your app requires this
                        // permission for a specific feature to behave as expected. In this UI,
                        // include a "cancel" or "no thanks" button that allows the user to
                        // continue using your app without granting the permission.
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CreateEventActivity.this);

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

                } else if (options[item].equals(getText(R.string.cancel))) dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) return;
        image = null;

        if (requestCode == 0 && resultCode == RESULT_OK && data != null) image = (Bitmap) data.getExtras().get("data"); // Get image from camera
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
        if (image != null) addImage(image);
    }

    @Override
    public void changeToLogin() {
        Intent intent = new Intent(CreateEventActivity.this, LoginActivity.class);
        this.startActivity(intent);
    }

    public void changeToEvent(Integer event_id) {
        Intent intent = new Intent(this, EventActivity.class);
        intent.putExtra("id", event_id);
        startActivity(intent);
    }

    @Override
    public SharedPreferences getEventPreferences() {
        return getSharedPreferences("eventPreferences", this.MODE_PRIVATE);
    }

    @Override
    public SharedPreferences getUserPreferences() {
        return getSharedPreferences("userPreferences", this.MODE_PRIVATE);
    }

    private void showStartDatePickerDialog() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // +1 because January is zero
                month = month+1;
                String m = String.valueOf(month);
                String d = String.valueOf(day);
                if(month < 10) m = "0" + month;
                if(day < 10) d = "0" + day;
                final String selectedDate = d + "/" + m + "/" + year;
                eventStartDateInput.setText(selectedDate);
            }
        });

        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private void showEndDatePickerDialog() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // +1 because January is zero
                month = month+1;
                String m = String.valueOf(month);
                String d = String.valueOf(day);
                if(month < 10) m = "0" + month;
                if(day < 10) d = "0" + day;
                final String selectedDate = d + "/" + m + "/" + year;
                eventEndDateInput.setText(selectedDate);
            }
        });

        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private void showStartTimePickerDialog() {
        TimePickerFragment newFragment = TimePickerFragment.newInstance(new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                String h = String.valueOf(hour);
                String m = String.valueOf(minute);
                if(hour < 10) h = "0" + hour;
                if(minute < 10) m = "0" + minute;
                final String selectedDate = h + ":" + m;
                eventStartTimeInput.setText(selectedDate);
            }
        });
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private void showEndTimePickerDialog() {
        TimePickerFragment newFragment = TimePickerFragment.newInstance(new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                String h = String.valueOf(hour);
                String m = String.valueOf(minute);
                if(hour < 10) h = "0" + hour;
                if(minute < 10) m = "0" + minute;
                final String selectedDate = h + ":" + m;
                eventEndTimeInput.setText(selectedDate);
            }
        });
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public String getEventTitle() {
        return eventTitleInput.getText().toString();
    }
    public String getEventDescription() {
        return eventDescriptionInput.getText().toString();
    }

    public String getEventStartDate() {
        return eventStartDateInput.getText().toString();
    }
    public String getEventEndDate() {
        return eventEndDateInput.getText().toString();
    }
    public String getEventStartTime() {
        return eventStartTimeInput.getText().toString();
    }
    public String getEventEndTime() {
        return eventEndTimeInput.getText().toString();
    }
    public int getEventCapacity() {
        return Integer.parseInt(String.valueOf(eventCapacityInput.getText()));
    }
    public String getEventLatitude() {
        return mapView.getLatitude();
    }
    public String getEventLongitude() {
        return mapView.getLongitude();
    }
    public String getEventAuthor(){
        String pp= "dgfsfda";
        return pp;
    }
    public int getEventParticipants() {
        //TODO
        return 0;
    }
    public int getEventPrice() {
        return Integer.parseInt(String.valueOf(eventPriceInput.getText()));
    }

    public void setLoading(boolean loading) {
        if (loading) progressBar.setVisibility(ProgressBar.VISIBLE);
        else progressBar.setVisibility(ProgressBar.INVISIBLE);

        addImageButton.setEnabled(!loading);
        imagesContainer.setEnabled(!loading);
        confirmButton.setEnabled(!loading);
        cancelButton.setEnabled(!loading);
        eventTitleInput.setEnabled(!loading);
        eventDescriptionInput.setEnabled(!loading);
        eventStartDateInput.setEnabled(!loading);
        eventEndDateInput.setEnabled(!loading);
        eventStartTimeInput.setEnabled(!loading);
        eventCapacityInput.setEnabled(!loading);
        eventPriceInput.setEnabled(!loading);
        eventEndTimeInput.setEnabled(!loading);
        createEventTagsContainer.setEnabled(!loading);
        addTagChip.setEnabled(!loading);
    }

    public void showError(String title, String description) {
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

    public void showAddTagError(String title, String description) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle(title);
        alertDialogBuilder
                .setMessage(description)
                .setCancelable(false)
                .setNeutralButton(getText(R.string.accept), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mPresenter.tagAdded();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public void showRemovedTagError(String title, String description) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle(title);
        alertDialogBuilder
                .setMessage(description)
                .setCancelable(false)
                .setNeutralButton(getText(R.string.accept), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mPresenter.tagRemoved();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public void finishActivity()
    {
        finish();
    }

}