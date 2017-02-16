package com.openu.a2017_app1.screens;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.openu.a2017_app1.R;
import com.openu.a2017_app1.models.ModelSaveListener;
import com.openu.a2017_app1.models.Place;
import com.openu.a2017_app1.models.Recommendation;
import com.openu.a2017_app1.services.LocationService;
import com.openu.a2017_app1.models.LocationPoint;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;


//import com.openu.a2017_app1.screens;

public class PostReviewActivity extends AppCompatActivity implements ModelSaveListener{

    private LocationService locservice;
    private Uri pictureUri;
    public static final int CAMERA_REQUEST = 10;
    private ImageView imgSpecimenPhoto;
    private EditText titleText;
    private EditText nameText;
    private EditText descriptionText;
    private EditText locationText;
    private  LocationPoint myLocation;
    Bitmap recommendationImage = null;

    boolean newlocation;
    Place selectedplace = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_review);

        locservice = new LocationService(this);
        locservice.mGoogleApiClient.connect();

        Button photobutton = (Button) findViewById(R.id.AddPhotoButton);
        photobutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                btnTakePhotoClicked(v);
            }
        });
        Button postbutton = (Button) findViewById(R.id.PostButton);
        postbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                btnPostClicked(v);
            }
        });
        imgSpecimenPhoto = (ImageView) findViewById(R.id.imgReviewPhoto);
        titleText = (EditText) findViewById(R.id.TitleEditText);
        nameText = (EditText) findViewById(R.id.NameEditText);

        descriptionText = (EditText) findViewById(R.id.DescriptionEditText);
        locationText = (EditText) findViewById(R.id.LocationText);
        /*if(locservice.mGoogleApiClient == null) {
            //locservice = new LocationService(this);
            locservice.mGoogleApiClient.connect();
        }*/
        Toast.makeText(this, "Connected to API client", Toast.LENGTH_LONG).show();

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        newlocation = !((boolean) bundle.getBoolean("existinglocation"));
        if(!newlocation) {
            setLocationDataAndLock(bundle);
        }

        //buildGoogleApiClient();
        //setMyLocation(locservice.GetLocationPoint());
    }


    protected void onStart() {
        //locservice.mGoogleApiClient.connect();
        //Toast.makeText(this, "Connected to API client", Toast.LENGTH_LONG).show();

        super.onStart();
    }

    protected void onStop() {
        locservice.mGoogleApiClient.disconnect();
        super.onStop();
    }

    public void btnPostClicked(View v) {
        Toast.makeText(this, "Starting post", Toast.LENGTH_LONG).show();
        Recommendation rec = new Recommendation();

        if(newlocation) {
            rec.setTitle(titleText.getText().toString());
            rec.setDescription(descriptionText.getText().toString());
            //rec.setPhoto(recommendationImage);
            selectedplace = new Place();
            selectedplace.setName(nameText.getText().toString());
            setMyLocation(locservice.GetLocationPoint());
            Toast.makeText(this, "Valid location? " + (myLocation != null), Toast.LENGTH_LONG).show();
            selectedplace.setLocation(myLocation);
            selectedplace.setName(nameText.getText().toString());
            selectedplace.addRecommendation(rec);
            //rec.setPlace(selectedplace);
        }else{
            rec.setTitle(titleText.getText().toString());
            rec.setDescription(descriptionText.getText().toString());
            //rec.setPlace(selectedplace);

        }
        //Are we supposed to save the place or the recommendation or both? we are getting an error here: at com.openu.a2017_app1.data.DaoFactory.create(DaoFactory.java:46)
        //rec.save();
        selectedplace.saveAsync(this);

    }

    void setLocationDataAndLock(Bundle bundle){
        selectedplace =
                (Place) bundle.getSerializable("value");

        locationText.setText(selectedplace.getLocation().getLatitude() + " , " + selectedplace.getLocation().getLongitude());
        locationText.setKeyListener(null);
        nameText.setText(selectedplace.getName());
        nameText.setKeyListener(null);
    }

    //listOfPlaces = getQuery(Place.class).where("name", "==" , "falafal")..getAll();


    public void onSave(boolean succeeded, Object id){
        if(succeeded){
            Toast.makeText(this, "Save successful", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this, "ERROR SAVING", Toast.LENGTH_LONG).show();
        }
    }

    public void setMyLocation(LocationPoint lcp){
        myLocation = lcp;
        if(myLocation != null) {
            locationText.setText(myLocation.getLatitude() + " , " + myLocation.getLongitude());
        }
        else{
            Toast.makeText(this, "Failed To Get Location", Toast.LENGTH_LONG).show();

        }
    }

    public void btnTakePhotoClicked(View v) {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String pictureName = getPictureName();
        File imageFile = new File(pictureDirectory, pictureName);
        pictureUri = Uri.fromFile(imageFile);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, pictureUri);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    private String getPictureName() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = sdf.format(new Date());
        return "PlantPlacesImage" + timestamp + ".jpg";

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Did the user choose OK?  If so, the code inside these curly braces will execute.
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST) {
                // we are hearing back from the camera.
                recommendationImage = (Bitmap) data.getExtras().get("data");
                // at this point, we have the image from the camera.
                imgSpecimenPhoto.setImageBitmap(recommendationImage);
            }
        }
    }

    public void DisplayMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        myLocation = locservice.onRequestPermissionsResultHandler(requestCode,permissions,grantResults);
    }

}
