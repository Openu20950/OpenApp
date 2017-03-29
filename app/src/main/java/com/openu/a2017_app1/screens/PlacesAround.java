package com.openu.a2017_app1.screens;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.openu.a2017_app1.R;
import com.openu.a2017_app1.data.GetAllListener;
import com.openu.a2017_app1.models.IPlace;
import com.openu.a2017_app1.models.LocationPoint;
import com.openu.a2017_app1.models.Model;
import com.openu.a2017_app1.models.Place;
import com.openu.a2017_app1.models.Review;
import com.openu.a2017_app1.models.ReviewConclusion;

import java.util.List;

import static com.google.android.gms.location.LocationServices.FusedLocationApi;

public class PlacesAround extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks {

    private static final int LOCATION_REQUEST = 0;

    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private Spinner mSpinner;
    private GoogleApiClient mGoogleApiClient;
    private LocationPoint mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_around);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(PlacesAround.this, AddPlace.class);
                myIntent.putExtra(AddPlace.EXTRA_LOCATION, mLocation);
                PlacesAround.this.startActivity(myIntent);
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
                loadPlaces();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_COARSE_LOCATION  }, LOCATION_REQUEST );
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();

    }

    private void loadPlaces() {
        if (mLocation == null) {
            return;
        }

        mProgressBar.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);

        int radius = getResources().getIntArray(R.array.place_around_radius)[mSpinner.getSelectedItemPosition()];
        Model.getQuery(Place.class).whereNear(Place.FIELD_LOCATION, mLocation, radius).getAllAsync(new GetAllListener<Place>() {
            @Override
            public void onItemsReceived(List<Place> items) {
                PlacesAdapter adapter = new PlacesAdapter(items);
                adapter.setOnClickListener(new PlacesAdapter.OnPlaceClickListener() {
                    @Override
                    public void OnPlaceClicked(String placeId) {
                        Intent myIntent = new Intent(PlacesAround.this, PlaceInfo.class);
                        myIntent.putExtra(PlaceInfo.EXTRA_PLACE, placeId);
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
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
            mGoogleApiClient.connect();
        }
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        //noinspection MissingPermission
        mLocation = LocationPoint.fromLocation(FusedLocationApi.getLastLocation(mGoogleApiClient));
        if (mLocation != null) {
            loadPlaces();
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mGoogleApiClient.connect();
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
            holder.likes.setText(String.valueOf(place.getReviews().where(Review.FIELD_CONCLUSION, ReviewConclusion.Like.name()).count()));
            holder.dislikes.setText(String.valueOf(place.getReviews().where(Review.FIELD_CONCLUSION, ReviewConclusion.Dislike.name()).count()));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mClickListener != null) {
                        mClickListener.OnPlaceClicked(place.getId());
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
            public TextView placeName, likes, dislikes, category;

            public MyViewHolder(View view) {
                super(view);
                placeName = (TextView) view.findViewById(R.id.place_name);
                likes = (TextView) view.findViewById(R.id.likes);
                dislikes = (TextView) view.findViewById(R.id.dislikes);
                category = (TextView) view.findViewById(R.id.category);
            }


        }

        public interface OnPlaceClickListener {
            void OnPlaceClicked(String placeId);
        }
    }

}
