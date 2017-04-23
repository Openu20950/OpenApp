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

import java.util.ArrayList;
import java.util.List;

import com.openu.a2017_app1.R;
import com.openu.a2017_app1.models.LocationPoint;
import com.openu.a2017_app1.models.ModelSaveListener;
import com.openu.a2017_app1.models.Place;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * An add place screen
 */
public class AddPlace extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    public static final String EXTRA_LOCATION = "location";

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

        mLocation = (LocationPoint) getIntent().getExtras().get(EXTRA_LOCATION);
        myFacebookId=(String) getIntent().getExtras().get(Place.FIELD_FACEBOOK_ID);
        mPlaceName = (EditText) findViewById(R.id.place_name);

        mCategory = (Spinner) findViewById(R.id.category);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.place_categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCategory.setAdapter(adapter);

        mDescription = (EditText) findViewById(R.id.description);
        mPhone = (EditText) findViewById(R.id.phone);
        mPhoto = (ImageView) findViewById(R.id.photo);

        mPhoto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });

        final Button addBtn = (Button) findViewById(R.id.add_place_button);
        addBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlaceName.getText().toString().trim().length() == 0) {
                    Snackbar.make(mPlaceName, R.string.place_name_cannot_be_empty, Snackbar.LENGTH_LONG).show();
                }

                Place place = new Place();
                place.setName(mPlaceName.getText().toString().trim());
                place.setCategory(mCategory.getSelectedItem().toString());
                place.setDescription(mDescription.getText().toString().trim());
                place.setPhone(mPhone.getText().toString().trim());
                place.setLocation(mLocation);
                place.setFacebookId(myFacebookId);
                if(mCapturedPhoto!=null)
                {
                    place.setPhoto(mCapturedPhoto);
                }else{
                    Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
                    Bitmap bmp = Bitmap.createBitmap(5, 5, conf);
                    place.setPhoto(bmp);
                }


                place.saveAsync(new ModelSaveListener() {
                    @Override
                    public void onSave(boolean succeeded, Object id) {
                        if (succeeded) {
                            finish();
                        } else {
                            Snackbar.make(addBtn, R.string.failed_to_save_place, Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mPhoto.setImageBitmap(mCapturedPhoto = imageBitmap);
        }
    }
}

