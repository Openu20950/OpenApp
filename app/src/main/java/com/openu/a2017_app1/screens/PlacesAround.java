package com.openu.a2017_app1.screens;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.google.android.gms.common.api.GoogleApiClient;
import com.openu.a2017_app1.R;
import com.openu.a2017_app1.data.GetAllListener;
import com.openu.a2017_app1.models.LocationPoint;
import com.openu.a2017_app1.models.Model;
import com.openu.a2017_app1.models.Place;
import com.openu.a2017_app1.models.Review;
import com.openu.a2017_app1.services.LocationService;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class PlacesAround extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks {

    private static final int LOCATION_REQUEST = 0;

    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private Spinner mSpinner;
    private LocationPoint mLocation;
    private LocationService mService;
    private Snackbar mNoLocationService;
    private String friendListString;
    private JSONArray friendListJSONArray;
    private String myFacebookId;
    private String myFacebookName;
    private boolean friendFilter=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_around);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        myFacebookId=(String)getIntent().getExtras().get(Place.FIELD_FACEBOOK_ID);

        if(myFacebookId.equals("guest"))
        {
            myFacebookName="Guest";
        }else{
            ProfileTracker mProfileTracker = new ProfileTracker() {
                @Override
                protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                    if(newProfile!=null)
                     myFacebookName=newProfile.getName();


                }
            };
        }


        if(friendListString!=null)
        {
            try {
                friendListJSONArray = new JSONArray(friendListString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(PlacesAround.this, AddPlace.class);
                myIntent.putExtra(AddPlace.EXTRA_LOCATION, mLocation);
                myIntent.putExtra(Place.FIELD_FACEBOOK_ID, myFacebookId);
                PlacesAround.this.startActivity(myIntent);
            }
        });

        FloatingActionButton searchFbutton=(FloatingActionButton) findViewById(R.id.search);

        searchFbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!myFacebookName.equals("Guest"))
                {
                    new GraphRequest(
                            AccessToken.getCurrentAccessToken(),
                            "me/friends",
                            null,
                            HttpMethod.GET,
                            new GraphRequest.Callback() {
                                public void onCompleted(GraphResponse response) {
                                    try {
                                        friendListJSONArray=response.getJSONObject().getJSONArray("data");//.getJSONObject(0).getString("name");
                                        friendFilter=true;
                                        loadPlaces(false);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                    ).executeAsync();


                }


            }
        });

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

        mRecyclerView = (RecyclerView) findViewById(R.id.places_list);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration horizontalDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        Drawable horizontalDivider = ContextCompat.getDrawable(this, R.drawable.horizontal_divider);
        horizontalDecoration.setDrawable(horizontalDivider);
        mRecyclerView.addItemDecoration(horizontalDecoration);

        mSpinner = (Spinner) findViewById(R.id.radius_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.place_around_radius_text, R.layout.drop_title);
        adapter.setDropDownViewResource(R.layout.drop_list);
        mSpinner.setAdapter(adapter);

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadPlaces(false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_COARSE_LOCATION  }, LOCATION_REQUEST );
        }

        mService = new LocationService(this, this);
    }

    private void loadPlaces(boolean shouldWarn) {
        if (mLocation == null) {
            if (shouldWarn) {
                mNoLocationService = Snackbar.make(mRecyclerView, R.string.location_service_off, Snackbar.LENGTH_INDEFINITE);
                mNoLocationService.setAction(R.string.turn_on, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
                    }
                });
                mNoLocationService.show();
            }
            return;
        }
        if (mNoLocationService != null) {
            mNoLocationService.dismiss();
        }

        mProgressBar.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        int radius = getResources().getIntArray(R.array.place_around_radius)[mSpinner.getSelectedItemPosition()];
        //friendListJSONArray


        Model.getQuery(Place.class).whereNear(Place.FIELD_LOCATION, mLocation, radius).getAllAsync(new GetAllListener<Place>() {
            @Override
            public void onItemsReceived(List<Place> items) {
                PlacesAdapter adapter;
                if(friendFilter)
                {
                    List<Place> newItems=new ArrayList<Place>();
                    List<Review> reviews=new ArrayList<Review>();
                    for(Place p:items)
                    {
                        reviews=p.getReviews().getAll();
                        for (int i=0;i<friendListJSONArray.length();i++)
                        {
                            String id="";
                            try {
                                id =friendListJSONArray.getJSONObject(i).getString("id");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if(p.getFacebookId().equals(id))
                            {
                                newItems.add(p);
                            }

                            for(Review r:reviews)
                            {
                                if(r.getFacebookId().equals(id))
                                {
                                    newItems.add(p);
                                }
                            }
                        }
                    }
                    adapter = new PlacesAdapter(newItems);
                    friendFilter=false;
                }else{
                    adapter = new PlacesAdapter(items);
                }

                adapter.setOnClickListener(new PlacesAdapter.OnPlaceClickListener() {
                    @Override
                    public void OnPlaceClicked(Place place) {
                        Intent myIntent = new Intent(PlacesAround.this, PlaceInfo.class);
                        myIntent.putExtra(PlaceInfo.EXTRA_PLACE_ID, place.getId());
                        myIntent.putExtra(Place.FIELD_FACEBOOK_ID,myFacebookId);

                        myIntent.putExtra(Review.FIELD_FACEBOOK_NAME,myFacebookName);
                        PlacesAround.this.startActivity(myIntent);
                    }
                });
                mRecyclerView.setAdapter(adapter);

                mProgressBar.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
            }
        });


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

        loadPlaces(true);
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

    private static class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.MyViewHolder> {

        private List<Place> mPlacesList;

        private OnPlaceClickListener mClickListener;

        public PlacesAdapter(List<Place> placesList) {
            this.mPlacesList = placesList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.place_list_row, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            final Place place = mPlacesList.get(position);
            holder.placeName.setText(place.getName());
            holder.category.setText(place.getCategory());
            holder.rating.setRating((float) place.getReviews().average(Review.FIELD_SCORE));
            holder.photo.setImageBitmap(place.getPhoto());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mClickListener != null) {
                        mClickListener.OnPlaceClicked(place);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mPlacesList.size();
        }

        public void setOnClickListener(OnPlaceClickListener mClickListener) {
            this.mClickListener = mClickListener;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView placeName, category;
            public RatingBar rating;
            public ImageView photo;

            public MyViewHolder(View view) {
                super(view);
                placeName = (TextView) view.findViewById(R.id.place_name);
                rating = (RatingBar) view.findViewById(R.id.rating);
                category = (TextView) view.findViewById(R.id.category);
                photo = (ImageView) view.findViewById(R.id.icon);
            }


        }

        public interface OnPlaceClickListener {
            void OnPlaceClicked(Place place);
        }
    }

}
