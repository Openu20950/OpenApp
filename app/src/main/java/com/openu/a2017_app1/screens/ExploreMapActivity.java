package com.openu.a2017_app1.screens;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.openu.a2017_app1.R;
import com.openu.a2017_app1.data.GetAllListener;
import com.openu.a2017_app1.models.LocationPoint;
import com.openu.a2017_app1.models.Model;
import com.openu.a2017_app1.models.Place;
import com.openu.a2017_app1.utils.DirectionsJSONParser;
import com.openu.a2017_app1.utils.LocationService;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ExploreMapActivity extends FragmentActivity implements  OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks {

    private GoogleMap mMap;
    private MapView mv;
    private LocationService mService;
    private static final int LOCATION_REQUEST = 0;
    CameraPosition lastCameraPos = null;
    HashMap<Marker, String> markers = new HashMap<Marker, String>();
    Marker lastPressed = null;

    private LocationPoint mLocation;
    private GoogleApiClient mGoogleApiClient;
    private EditText mapSearchBox;
    private EditText mapSecondSearchBox;

    int placenum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //mLocation = new LocationPoint();
        //mLocation.setLatitude(32.117033);
        //mLocation.setLongitude(34.809126);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_explore_map);

        mapSearchBox = (EditText) findViewById(R.id.mapSearchBox);
        //mapSecondSearchBox = (EditText) findViewById(R.id.mapSecondSearchBox);
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

        /*mapSecondSearchBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    GetDirectionsTest();
                    return true;
                }
                return false;
            }
        });*/




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
                            .snippet("Reviews: " + item.getReviews().count()).icon(CategoryIcon(item.getCategory()))), item.getId());
                    //item.getCategory();

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
                    lastCameraPos = mMap.getCameraPosition();
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

        //Toast.makeText(this, "map ready with " + placenum + " results", Toast.LENGTH_SHORT).show();
        if (mLocation != null && mMap != null) {
            CenterOnLocalPlaces(mLocation);
            //Toast.makeText(this, "centering through map", Toast.LENGTH_SHORT).show();
        }
    }

    public void showMessage(String msg){
        //Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void CenterOnLocalPlaces(LocationPoint loc){
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        if(lastCameraPos == null) {
            mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
            //mMap.moveCamera(CameraUpdateFactory.zoomIn());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(loc.getLatitude(), loc.getLongitude())));
        }else{
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(lastCameraPos));
            mMap.moveCamera(CameraUpdateFactory.zoomTo(lastCameraPos.zoom));
        }
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
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(result.getLatitude(), result.getLongitude()))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                            .title("result " + results.indexOf(result)));
                }
            }else{
                Toast.makeText(ExploreMapActivity.this, "No results found", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(ExploreMapActivity.this, "Something went wrong please try again", Toast.LENGTH_SHORT).show();

        }
    }

    BitmapDescriptor CategoryIcon(String category){
        float color;
        switch (category){
            case "Restaurant":
                color = BitmapDescriptorFactory.HUE_AZURE;
                break;
            case "Amusement":
                color = BitmapDescriptorFactory.HUE_BLUE;
                break;
            case "Bar":
                color = BitmapDescriptorFactory.HUE_CYAN;
                break;
            case "Café":
                color = BitmapDescriptorFactory.HUE_GREEN;
                break;
            case "Store":
                color = BitmapDescriptorFactory.HUE_MAGENTA;
                break;
            case "Gym":
                color = BitmapDescriptorFactory.HUE_ORANGE;
                break;
            case "Home":
                color = BitmapDescriptorFactory.HUE_ROSE;
                break;
            case "Park":
                color = BitmapDescriptorFactory.HUE_VIOLET;
                break;
            case "Shopping Mall":
                color = BitmapDescriptorFactory.HUE_YELLOW;
                break;
            default:
                color = BitmapDescriptorFactory.HUE_RED;
                break;
        }
        return BitmapDescriptorFactory.defaultMarker(color);
    }

    public LatLng GetAddres(String searchtext){
        List<android.location.Address> results = null;
        Geocoder gc = new Geocoder(ExploreMapActivity.this, Locale.getDefault());
        try {
            results = gc.getFromLocationName(searchtext, 20);

        }catch(Exception e){
            Toast.makeText(ExploreMapActivity.this, "Something went wrong please try again", Toast.LENGTH_SHORT).show();
        }
        if(results != null){
            if(results.size() > 0) {
                for (android.location.Address result : results) {
                    return new LatLng(results.get(0).getLatitude(), results.get(0).getLongitude());

                   // mMap.addMarker(new MarkerOptions().position(new LatLng(result.getLatitude(), result.getLongitude())).title("result " + results.indexOf(result)));
                }
            }else{
                Toast.makeText(ExploreMapActivity.this, "No results found", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(ExploreMapActivity.this, "Something went wrong please try again", Toast.LENGTH_SHORT).show();

        }
        return null;
    }

    public void SearchSingle(){
        List<android.location.Address> results = null;

        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
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

    public void SearchRoute(){
        List<android.location.Address> results = null;
        Geocoder gc = new Geocoder(ExploreMapActivity.this, Locale.getDefault());
        try {
            //results = gc.getFromLocationName(mapSecondSearchBox.getText().toString(), 20);

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

        mLocation = mService.lastLocation();
        //Toast.makeText(this, "done with location", Toast.LENGTH_SHORT).show();

        if (mLocation != null && mMap != null) {
            CenterOnLocalPlaces(mLocation);
            //Toast.makeText(this, "centering through location", Toast.LENGTH_SHORT).show();
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

    void GetDirectionsTest(){

        LatLng origin = GetAddres(mapSearchBox.getText().toString());
        LatLng dest = GetAddres("");//GetAddres(mapSecondSearchBox.getText().toString());

        // Getting URL to the Google Directions API
        String url = getDirectionsUrl(origin, dest);

        DownloadTask downloadTask = new DownloadTask();
        Toast.makeText(this, "download starting", Toast.LENGTH_SHORT).show();

        // Start downloading json data from Google Directions API
        downloadTask.execute(url);

        //Toast.makeText(this, "download done", Toast.LENGTH_SHORT).show();
    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String>{

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            showMessage("starting parse");
            parserTask.execute(result);
        }
    }


    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> > {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(2);
                lineOptions.color(Color.RED);
            }

            // Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions);
        }
    }

    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Exception in url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private String getDirectionsUrl(LatLng origin,LatLng dest){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }

}