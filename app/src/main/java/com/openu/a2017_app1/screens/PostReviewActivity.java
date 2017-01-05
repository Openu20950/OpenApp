package com.openu.a2017_app1.screens;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;



import com.openu.a2017_app1.R;
import com.openu.a2017_app1.Services.LocationService;
import com.openu.a2017_app1.models.LocationPoint;
import com.openu.a2017_app1.models.Place;
import com.openu.a2017_app1.models.Recommendation;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;


//import com.openu.a2017_app1.screens;

public class PostReviewActivity extends AppCompatActivity{

    private LocationService locservice;
    private Uri pictureUri;
    public static final int CAMERA_REQUEST = 10;
    private ImageView imgSpecimenPhoto;
    private EditText titleText;
    private EditText descriptionText;
    private  LocationPoint myLocation;
    Bitmap recommendationImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_review);


        imgSpecimenPhoto = (ImageView) findViewById(R.id.imgReviewPhoto);
        titleText = (EditText) findViewById(R.id.TitleEditText);
        descriptionText = (EditText) findViewById(R.id.DescriptionEditText);
        locservice = new LocationService(this);
        //buildGoogleApiClient();
    }


    protected void onStart() {
        locservice.mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        locservice.mGoogleApiClient.disconnect();
        super.onStop();
    }

    public void btnPostClicked(View v) {
        Recommendation rec = new Recommendation();
        rec.setTitle(titleText.getText().toString());
        rec.setDescription(descriptionText.getText().toString());
        rec.setPhoto(recommendationImage);
        Place place = new Place();
        myLocation = locservice.GetLocationPoint();
        place.setLocation(myLocation);
        rec.setPlace(place);
    }

    public void setMyLocation(LocationPoint lcp){
        myLocation = lcp;
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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        myLocation = locservice.onRequestPermissionsResultHandler(requestCode,permissions,grantResults);
    }

}
