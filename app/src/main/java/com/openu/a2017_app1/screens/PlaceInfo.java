package com.openu.a2017_app1.screens;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.openu.a2017_app1.R;
import com.openu.a2017_app1.data.FindListener;
import com.openu.a2017_app1.data.GetAllListener;
import com.openu.a2017_app1.models.Model;
import com.openu.a2017_app1.models.Place;
import com.openu.a2017_app1.models.Review;
import com.openu.a2017_app1.services.CircleTransform;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PlaceInfo extends AppCompatActivity {

    public static final String EXTRA_PLACE_ID = "place_id";

    private static final int PAGE_SIZE = 50;

    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private ReviewsAdapter mAdapter;
    private ImageView mBackdrop;
    private RatingBar mRating;
    private TextView mCategory;
    private TextView mRatingText;
    private TextView mDescription;
    private TextView mPhone;
    private RelativeLayout mDetails;
    private CollapsingToolbarLayout mCollapsingToolbar;
    private EndlessRecyclerViewScrollListener mScroller;
    private String mPlaceId;
    private String myFacebookId;
    private String myFacebookName;
    private String myPicture;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(PlaceInfo.this, AddReview.class);
                myIntent.putExtra(AddReview.EXTRA_PLACE_ID, mPlaceId);
                myIntent.putExtra(Place.FIELD_FACEBOOK_ID, myFacebookId);
                myIntent.putExtra(Review.FIELD_FACEBOOK_NAME, myFacebookName);
                myIntent.putExtra(Review.FIELD_USER_PICTURE, myPicture);
                PlaceInfo.this.startActivity(myIntent);
            }
        });

        myFacebookId = (String)getIntent().getExtras().get(Place.FIELD_FACEBOOK_ID);
        myFacebookName = (String)getIntent().getExtras().get(Review.FIELD_FACEBOOK_NAME);
        myPicture = (String)getIntent().getExtras().get(Review.FIELD_USER_PICTURE);
        mPlaceId = getIntent().getExtras().getString(EXTRA_PLACE_ID);

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

        mBackdrop = (ImageView) findViewById(R.id.backdrop);

        mRating = (RatingBar) findViewById(R.id.rating);
        mRatingText = (TextView) findViewById(R.id.score_text);
        mCategory = (TextView) findViewById(R.id.category);
        mDescription = (TextView) findViewById(R.id.description);
        mPhone = (TextView) findViewById(R.id.phone);
        mCollapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        mDetails = (RelativeLayout) findViewById(R.id.place_details);

        mRecyclerView = (RecyclerView) findViewById(R.id.reviews_list);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mScroller = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadReviews(page);
            }
        };
        mRecyclerView.addOnScrollListener(mScroller);
        mAdapter = new ReviewsAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAdapter.getItems().clear();
        loadReviews(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_place_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
            case R.id.edit:
            {
                Intent myIntent = new Intent(this, AddPlace.class);
                myIntent.putExtra(AddPlace.EXTRA_PLACE_ID, mPlaceId);
                this.startActivity(myIntent);
            }
        }
        return true;
    }

    private void loadReviews(final int offset) {
        mProgressBar.setVisibility(View.VISIBLE);

        Model.getQuery(Place.class).findAsync(mPlaceId, new FindListener<Place>() {
            @Override
            public void onItemFound(final Place place) {

                mBackdrop.setImageBitmap(place.getPhoto());
                getSupportActionBar().setTitle(place.getName());
                double rating = place.getReviews().average(Review.FIELD_SCORE);
                mRating.setRating((float)rating);
                mRatingText.setText(String.format("%.1f", rating));
                if (place.getDescription() != null && place.getDescription().length() != 0) {
                    mDescription.setText(place.getDescription());
                    mDescription.setVisibility(View.VISIBLE);
                }
                mCategory.setText(place.getCategory());
                if (place.getPhone() != null && place.getPhone().length() != 0) {
                    mPhone.setText(place.getPhone());
                    mPhone.setVisibility(View.VISIBLE);
                }

                mDetails.post(new Runnable() {
                    @Override
                    public void run() {
                        mCollapsingToolbar.setExpandedTitleMarginBottom(mDetails.getHeight() + 2 * getResources().getDimensionPixelOffset(R.dimen.fab_margin));
                    }
                });

                place.getReviews().skip(offset).limit(PAGE_SIZE).getAllAsync(new GetAllListener<Review>() {
                    @Override
                    public void onItemsReceived(List<Review> items) {
                        mProgressBar.setVisibility(View.GONE);
                        mRecyclerView.setVisibility(View.VISIBLE);

                        if (items.size() < PAGE_SIZE) {
                            mRecyclerView.removeOnScrollListener(mScroller);
                        }

                        mAdapter.getItems().addAll(items);
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    private static class ReviewsAdapter extends RecyclerView.Adapter<PlaceInfo.ReviewsAdapter.MyViewHolder> {

        private List<Review> mReviewsList;
        private final Activity mOwner;

        public ReviewsAdapter(Activity owner) {
            this(new ArrayList<Review>(), owner);
        }

        public ReviewsAdapter(List<Review> reviewsList, Activity owner) {
            this.mReviewsList = reviewsList;
            mOwner = owner;
        }

        @Override
        public PlaceInfo.ReviewsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.review_list_item, parent, false);

            return new PlaceInfo.ReviewsAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(PlaceInfo.ReviewsAdapter.MyViewHolder holder, int position) {
            final Review review = mReviewsList.get(position);
            holder.author.setText(review.getAuthor());
            holder.description.setText(review.getComment());
            holder.since.setText(DateUtils.getRelativeTimeSpanString(review.getCreatedAt().getTime(), System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS));
            holder.ratingBar.setRating(review.getScore());
            if (review.getPhoto() != null) {
                holder.photo.setImageBitmap(review.getPhoto());
                holder.photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent myIntent = new Intent(mOwner, FullscreenImageActivity.class);
                        myIntent.putExtra(FullscreenImageActivity.EXTRA_PHOTO, review.getPhoto());
                        mOwner.startActivity(myIntent);
                    }
                });
            } else {
                holder.photo.setVisibility(View.GONE);
            }
            if(review.getUserPic()!=null && !review.getUserPic().equals(Uri.EMPTY.toString()))
            {
                Glide.with(holder.user.getContext()).load(Uri.parse(review.getUserPic()))
                        .crossFade()
                        .thumbnail(0.5f)
                        .bitmapTransform(new CircleTransform(holder.user.getContext()))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.user);
            }

        }

        @Override
        public int getItemCount() {
            return mReviewsList.size();
        }

        public List<Review> getItems() {
            return mReviewsList;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView since, author, description;
            public ImageView user;
            public RatingBar ratingBar;
            public ImageView photo;

            public MyViewHolder(View view) {
                super(view);
                since = (TextView) view.findViewById(R.id.since);
                author = (TextView) view.findViewById(R.id.author);
                description = (TextView) view.findViewById(R.id.description);
                user = (ImageView) view.findViewById(R.id.icon);
                ratingBar = (RatingBar) view.findViewById(R.id.rating);
                photo = (ImageView) view.findViewById(R.id.photo);
            }
        }
    }
}
