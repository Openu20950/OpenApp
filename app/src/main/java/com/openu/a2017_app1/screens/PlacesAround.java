package com.openu.a2017_app1.screens;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;

import android.view.MenuItem;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.openu.a2017_app1.R;
import com.openu.a2017_app1.data.GetAllListener;

import com.openu.a2017_app1.models.LocationPoint;
import com.openu.a2017_app1.models.Model;
import com.openu.a2017_app1.models.Place;
import com.openu.a2017_app1.models.Review;
import com.openu.a2017_app1.utils.LocationService;
import com.openu.a2017_app1.services.CircleTransform;
import com.openu.a2017_app1.services.UserLoginService;


import java.util.ArrayList;
import java.util.List;




public class PlacesAround extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks {

    private static final int LOCATION_REQUEST = 0;

    // index to identify current nav menu item
    public static int navItemIndex = 0;

    // tags used to attach the fragments
    private static final String TAG_HOME = "home";


    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private Spinner mSpinner;

    private LocationPoint mLocation;
    private LocationService mService;
    private Snackbar mNoLocationService;
     private UserLoginService user;
    private boolean friendFilter=false;

    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private ImageView imgProfile;
    private TextView txtName;
    private Toolbar toolbar;
    private ProfileTracker mProfileTracker;
    private LoginButton loginButton;
    private CallbackManager callbackManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_around);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        LoginManager.getInstance().logOut();


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navHeader = navigationView.getHeaderView(0);
        txtName = (TextView) navHeader.findViewById(R.id.name);

        imgProfile = (ImageView) navHeader.findViewById(R.id.img_profile);



        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton)navHeader.findViewById(R.id.login_button);
        user=new UserLoginService(this);

        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {

                if (currentAccessToken == null){
                    user.setMyFacebookName("Guest");
                    loadNavHeader();
                }
            }
        };

        mProfileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                if(newProfile!=null)

                {
                    user.setMyFacebookName(newProfile.getFirstName());
                    user.setMyProfilePicture(newProfile.getProfilePictureUri(150,150));

                    loadNavHeader();
                }


            }
        };

        loginButton.setReadPermissions("user_friends");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {

                user.setMyFacebookId(loginResult.getAccessToken().getUserId());
                user.graphRequest();
                mProfileTracker.startTracking();
            }

            @Override
            public void onCancel() {


                Toast.makeText(drawer.getContext(),"Login attempt canceled.", Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onError(FacebookException e) {

                Toast.makeText(drawer.getContext(),"Login attempt failed.", Toast.LENGTH_SHORT).show();

            }
        });





        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(PlacesAround.this, AddPlace.class);
                myIntent.putExtra(AddPlace.EXTRA_LOCATION, mLocation);
                myIntent.putExtra(Place.FIELD_FACEBOOK_ID, user.getMyFacebookId());
                PlacesAround.this.startActivity(myIntent);
            }
        });

        FloatingActionButton searchFbutton=(FloatingActionButton) findViewById(R.id.search);

        searchFbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!user.getMyFacebookName().equals("Guest"))
                {
                    friendFilter=true;
                    loadPlaces(false);

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

        loadNavHeader();
        setUpNavigationView();
        mService = new LocationService(this, this);


    }
    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_home:
                        drawer.closeDrawers();
                        return true;
                    case R.id.nav_map:
                        drawer.closeDrawers();
                        return true;
                    case R.id.nab_augmented_reality:
                        startActivity(new Intent(PlacesAround.this, AugmentedReality.class));
                        drawer.closeDrawers();
                        return true;
                    case R.id.nav_settings:
                        startActivity(new Intent(PlacesAround.this, SettingsActivity.class));
                        drawer.closeDrawers();
                        return true;
                    case R.id.nav_about_us:
                        // launch new intent
                       // startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
                        drawer.closeDrawers();
                        return true;
                    case R.id.nav_privacy_policy:
                        // launch new intent instead of loading fragment
                       //    startActivity(new Intent(MainActivity.this, PrivacyPolicyActivity.class));
                        drawer.closeDrawers();
                        return true;
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
               // menuItem.setChecked(true);



                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout

        drawer.addDrawerListener(actionBarDrawerToggle);
        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home


        super.onBackPressed();
    }





    public void loadNavHeader() {

        // name, website
         txtName.setText(user.getMyFacebookName());


        if(user.getMyFacebookName().equals("Guest"))
        {
            Glide.with(this).load(R.drawable.ic_user)
                    .crossFade()
                    .thumbnail(0.5f)
                    .bitmapTransform(new CircleTransform(this))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imgProfile);
        }else{
            // navigationView.
            // Loading profile image
            Glide.with(this).load(user.getMyProfilePicture())
                    .crossFade()
                    .thumbnail(0.5f)
                    .bitmapTransform(new CircleTransform(this))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imgProfile);

        }


    }




    private void loadPlaces(boolean shouldWarn) {
        if (mLocation == null) {
            if (shouldWarn) {
                mNoLocationService = Snackbar.make(mRecyclerView, R.string.location_service_off, Snackbar.LENGTH_INDEFINITE);
                mNoLocationService.setAction(R.string.turn_on, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
                        mNoLocationService.dismiss();
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
                        for (int i=0;i<user.getFreindsList().size();i++)
                        {
                            String id=user.getFreindsList().get(i);

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
                        myIntent.putExtra(Place.FIELD_FACEBOOK_ID,user.getMyFacebookId());
                        myIntent.putExtra(Review.FIELD_FACEBOOK_NAME,user.getMyFacebookName());
                        myIntent.putExtra(Review.FIELD_USER_PICTURE,user.getMyProfilePicture().toString());
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


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}
