package com.openu.a2017_app1.ExploreMode;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.test.AndroidTestRunner;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Property;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.identity.intents.Address;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.openu.a2017_app1.R;
import com.openu.a2017_app1.data.GetAllListener;
import com.openu.a2017_app1.models.LocationPoint;
import com.openu.a2017_app1.models.Model;
import com.openu.a2017_app1.models.Place;
import com.openu.a2017_app1.models.Review;
import com.openu.a2017_app1.screens.PlaceInfo;
import com.openu.a2017_app1.screens.PlacesAround;
import com.openu.a2017_app1.utils.LocationService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.R.attr.radius;
import static com.google.android.gms.location.LocationServices.FusedLocationApi;

public class ExploreMapActivity extends FragmentActivity implements  OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks {

    private GoogleMap mMap;
    private MapView mv;
    private LocationService mService;
    private static final int LOCATION_REQUEST = 0;

    HashMap<Marker, String> markers = new HashMap<Marker, String>();
    Marker lastPressed = null;

    private LocationPoint mLocation;
    private GoogleApiClient mGoogleApiClient;
    private EditText mapSearchBox;

    int placenum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        //mLocation = new LocationPoint();
        //mLocation.setLatitude(32.117033);
        //mLocation.setLongitude(34.809126);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_explore_map);

        mapSearchBox = (EditText) findViewById(R.id.mapSearchBox);
        //mapSearchBox.addTextChangedListener(SearchSingle());
        /*if(mapSearchBox == null){
            Toast.makeText(ExploreMapActivity.this, "Still null", Toast.LENGTH_SHORT).show();

        }else {*/
           /* mapSearchBox.addTextChangedListener(new TextWatcher() {

                public void afterTextChanged(Editable s) {
                }

                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {
                    SearchSingle();
                }
            });*/



        mapSearchBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    SearchSingle();
                    return true;
                }
                return false;
            }






        });




        //setContentView(R.layout.activity_explore_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        mService = new LocationService(this, this);
    }




    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(0, 0))
                .title("Marker"));

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        Model.getQuery(Place.class).getAllAsync(new GetAllListener<Place>() {
            @Override
            public void onItemsReceived(List<Place> items) {
                mMap.clear();
                for (Place item : items) {
                    placenum++;
                    //showMessage("map ready with " + placenum + " results");
                    markers.put(
                    mMap.addMarker(new MarkerOptions().position(new LatLng(item.getLocation().getLatitude(), item.getLocation().getLongitude())).title(item.getName())
                            .snippet("Reviews: " + item.getReviews().count())), item.getId());

                }
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {

                // Retrieve the data from the marker.
                Integer clickCount = (Integer) marker.getTag();
                showMessage("clicked " + marker.getTitle());
                // Check if a click count was set, then display the click count.
                if (clickCount != null) {
                    clickCount = clickCount + 1;
                    marker.setTag(clickCount);
                    showMessage(marker.getTitle() + " has been clicked " + clickCount + " times.");
                }

                if(lastPressed != null && lastPressed.equals(marker)){
                    Intent myIntent = new Intent(ExploreMapActivity.this, PlaceInfo.class);
                    myIntent.putExtra(PlaceInfo.EXTRA_PLACE_ID, markers.get(marker));
                    //myIntent.putExtra(Place.FIELD_FACEBOOK_ID,user.getMyFacebookId());
                    //myIntent.putExtra(Review.FIELD_FACEBOOK_NAME,user.getMyFacebookName());
                    //myIntent.putExtra(Review.FIELD_USER_PICTURE,user.getMyProfilePicture().toString());
                    ExploreMapActivity.this.startActivity(myIntent);
                    return true;
                }

                lastPressed = marker;

                // Return false to indicate that we have not consumed the event and that we wish
                // for the default behavior to occur (which is for the camera to move such that the
                // marker is centered and for the marker's info window to open, if it has one).
                return false;
            }
        });

        Toast.makeText(this, "map ready with " + placenum + " results", Toast.LENGTH_SHORT).show();
        if (mLocation != null && mMap != null) {
            CenterOnLocalPlaces(mLocation);
            Toast.makeText(this, "centering through map", Toast.LENGTH_SHORT).show();
        }
    }

    public void showMessage(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void CenterOnLocalPlaces(LocationPoint loc){
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));

        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        //mMap.moveCamera(CameraUpdateFactory.zoomIn());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(loc.getLatitude(), loc.getLongitude())));
       /* Model.getQuery(Place.class).getAllAsync(new GetAllListener<Place>() {
            @Override
            public void onItemsReceived(List<Place> items) {
                mMap.clear();
                for (Place item : items) {
                    mMap.addMarker(new MarkerOptions().position(new LatLng(item.getLocation().getLatitude(), item.getLocation().getLongitude())).title(item.getName())
                    .snippet("Reviews: " + item.getReviews().count()));
                }
            }
        });*/
    }



    public void SearchMultiple(String searchText){
        List<android.location.Address> results = null;
        Geocoder gc = new Geocoder(ExploreMapActivity.this, Locale.getDefault());
        try {
            results = gc.getFromLocationName("searchText", 20);

        }catch(Exception e){
            Toast.makeText(ExploreMapActivity.this, "Something went wrong please try again", Toast.LENGTH_SHORT).show();
        }
        if(results != null){
            if(results.size() > 0) {
                for (android.location.Address result : results) {
                    mMap.addMarker(new MarkerOptions().position(new LatLng(result.getLatitude(), result.getLongitude())).title("result " + results.indexOf(result)));
                }
            }else{
                Toast.makeText(ExploreMapActivity.this, "No results found", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(ExploreMapActivity.this, "Something went wrong please try again", Toast.LENGTH_SHORT).show();

        }
    }

    public void SearchSingle(){
        List<android.location.Address> results = null;
        Geocoder gc = new Geocoder(ExploreMapActivity.this, Locale.getDefault());
        try {
            results = gc.getFromLocationName(mapSearchBox.getText().toString(), 20);

        }catch(Exception e){
            Toast.makeText(ExploreMapActivity.this, "Something went wrong please try again", Toast.LENGTH_SHORT).show();
        }
        if(results != null){
            if(results.size() > 0) {
                LocationPoint loc = new LocationPoint();
                loc.setLatitude(results.get(0).getLatitude());
                loc.setLongitude(results.get(0).getLongitude());
                CenterOnLocalPlaces(loc);
            }else{
                Toast.makeText(ExploreMapActivity.this, "No results found", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(ExploreMapActivity.this, "Something went wrong please try again", Toast.LENGTH_SHORT).show();

        }
    }

    protected void onStart() {
        mService.connect();
        super.onStart();
    }

    protected void onStop() {
        mService.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        //mLocation = mService.lastLocation();
        Toast.makeText(this, "done with location", Toast.LENGTH_SHORT).show();

        if (mLocation != null && mMap != null) {
            CenterOnLocalPlaces(mLocation);
            Toast.makeText(this, "centering through location", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        mService.connect();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mService.connect();
                } else {
                    Toast.makeText(this, "Cannot continue without location permission!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }

}
