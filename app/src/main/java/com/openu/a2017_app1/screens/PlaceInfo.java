package com.openu.a2017_app1.screens;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.openu.a2017_app1.R;
import com.openu.a2017_app1.data.FindListener;
import com.openu.a2017_app1.data.GetAllListener;
import com.openu.a2017_app1.models.Model;
import com.openu.a2017_app1.models.Place;
import com.openu.a2017_app1.models.Review;

import java.util.ArrayList;
import java.util.List;

public class PlaceInfo extends AppCompatActivity {

    public static final String EXTRA_PLACE_ID = "place_id";
    public static final String EXTRA_PLACE_NAME = "place_title";

    private static final int PAGE_SIZE = 50;

    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private ReviewsAdapter mAdapter;
    private EndlessRecyclerViewScrollListener mScroller;
    private String mPlaceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getIntent().getExtras().getString(EXTRA_PLACE_NAME));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TOOD: add a new review activity
            }
        });

        mPlaceId = getIntent().getExtras().getString(EXTRA_PLACE_ID);

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

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
        DividerItemDecoration horizontalDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        Drawable horizontalDivider = ContextCompat.getDrawable(this, R.drawable.horizontal_divider);
        horizontalDecoration.setDrawable(horizontalDivider);
        mRecyclerView.addItemDecoration(horizontalDecoration);
        mAdapter = new ReviewsAdapter();
        mRecyclerView.setAdapter(mAdapter);

        loadReviews(0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
        }
        return true;
    }

    private void loadReviews(final int offset) {
        mProgressBar.setVisibility(View.VISIBLE);

        Model.getQuery(Place.class).findAsync(mPlaceId, new FindListener() {
            @Override
            public void onItemFound(final Object item) {
                Place place = (Place) item;

                place.getReviews().skip(offset).limit(PAGE_SIZE).getAllAsync(new GetAllListener<Review>() {
                    @Override
                    public void onItemsReceived(List<Review> items) {
                        mProgressBar.setVisibility(View.GONE);
                        mRecyclerView.setVisibility(View.VISIBLE);

                        if (items.isEmpty()) {
                            mRecyclerView.removeOnScrollListener(mScroller);
                            return;
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

        public ReviewsAdapter() {
            this(new ArrayList<Review>());
        }

        public ReviewsAdapter(List<Review> reviewsList) {
            this.mReviewsList = reviewsList;
        }

        @Override
        public PlaceInfo.ReviewsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.place_list_row, parent, false);

            return new PlaceInfo.ReviewsAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(PlaceInfo.ReviewsAdapter.MyViewHolder holder, int position) {
            final Review review = mReviewsList.get(position);
            holder.title.setText(review.getTitle());
            holder.description.setText(review.getDescription());
        }

        @Override
        public int getItemCount() {
            return mReviewsList.size();
        }

        public List<Review> getItems() {
            return mReviewsList;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView since, title, description;
            public ImageView icon;

            public MyViewHolder(View view) {
                super(view);
                since = (TextView) view.findViewById(R.id.since);
                title = (TextView) view.findViewById(R.id.title);
                description = (TextView) view.findViewById(R.id.description);
                icon = (ImageView) view.findViewById(R.id.icon);
            }
        }
    }
}
