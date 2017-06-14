package com.openu.a2017_app1.screens;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.openu.a2017_app1.R;
import com.openu.a2017_app1.data.FindListener;
import com.openu.a2017_app1.models.LocationPoint;
import com.openu.a2017_app1.models.Model;
import com.openu.a2017_app1.models.ModelSaveListener;
import com.openu.a2017_app1.models.Place;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * An add place screen
 */
public class AddPlace extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    public static final String EXTRA_LOCATION = "location";
    public static final String EXTRA_PLACE_ID = "place_id";
    private static final int CAMERA_REQUEST = 1;

    private Place mPlace;
    private EditText mPlaceName;
    private Spinner mCategory;
    private EditText mDescription;
    private EditText mPhone;
    private ImageView mPhoto;
    private LocationPoint mLocation;
    private String myFacebookId;
    private Bitmap mCapturedPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDescription = (EditText) findViewById(R.id.description);
        mPhone = (EditText) findViewById(R.id.phone);
        mPhoto = (ImageView) findViewById(R.id.photo);
        mCategory = (Spinner) findViewById(R.id.category);
        mPlaceName = (EditText) findViewById(R.id.place_name);

        final Button addBtn = (Button) findViewById(R.id.add_place_button);

        String placeId = getIntent().getExtras().getString(EXTRA_PLACE_ID);
        if (placeId != null) {
            addBtn.setEnabled(false);
            Model.getQuery(Place.class).findAsync(placeId, new FindListener<Place>() {
                @Override
                public void onItemFound(Place item) {
                    mPlace = item;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            addBtn.setEnabled(true);
                            mPlaceName.setText(mPlace.getName());
                            mDescription.setText(mPlace.getDescription());
                            mPhone.setText(mPlace.getPhone());
                            if (mPlace.getPhoto() != null) {
                                mPhoto.setImageBitmap(mPlace.getPhoto());
                            }
                            mCategory.setSelection(Arrays.asList(getResources().getStringArray(R.array.place_categories)).indexOf(mPlace.getCategory()));
                        }
                    });
                }
            });
        } else {
            mPlace = new Place();
        }

        mLocation = (LocationPoint) getIntent().getExtras().get(EXTRA_LOCATION);
        myFacebookId=(String) getIntent().getExtras().get(Place.FIELD_FACEBOOK_ID);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.place_categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCategory.setAdapter(adapter);

        mPhoto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(AddPlace.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ) {
                    ActivityCompat.requestPermissions(AddPlace.this, new String[] {  android.Manifest.permission.CAMERA  }, CAMERA_REQUEST );
                    return;
                }
                OpenCamera();
            }
        });

        addBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                addBtn.setEnabled(false);
                if (mPlaceName.getText().toString().trim().length() == 0) {
                    Snackbar.make(mPlaceName, R.string.place_name_cannot_be_empty, Snackbar.LENGTH_LONG).show();
                }

                mPlace.setName(mPlaceName.getText().toString().trim());
                mPlace.setCategory(mCategory.getSelectedItem().toString());
                mPlace.setDescription(mDescription.getText().toString().trim());
                mPlace.setPhone(mPhone.getText().toString().trim());
                if (mPlace.getLocation() == null) {
                    mPlace.setLocation(mLocation);
                }
                if (mPlace.getFacebookId() == null) {
                    mPlace.setFacebookId(myFacebookId);
                }
                if(mCapturedPhoto != null) {
                    mPlace.setPhoto(mCapturedPhoto);
                }
                mPlace.saveAsync(new ModelSaveListener() {
                    @Override
                    public void onSave(final boolean succeeded, Object id) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (succeeded) {
                                    finish();
                                } else {
                                    Snackbar.make(addBtn, R.string.failed_to_save_place, Snackbar.LENGTH_LONG).show();
                                }
                                addBtn.setEnabled(true);
                            }
                        });
                    }
                });
            }
        });
    }

    private void OpenCamera() {
        Intent takePictureIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    OpenCamera();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            /*Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mPhoto.setImageBitmap(mCapturedPhoto = imageBitmap);*/

            Bitmap imageBitmap = null;
            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
            mPhoto.setImageBitmap(mCapturedPhoto = imageBitmap);
        }
    }
}

