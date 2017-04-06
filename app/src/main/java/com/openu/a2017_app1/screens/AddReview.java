package com.openu.a2017_app1.screens;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;

import com.openu.a2017_app1.R;
import com.openu.a2017_app1.data.FindListener;
import com.openu.a2017_app1.models.LocationPoint;
import com.openu.a2017_app1.models.Model;
import com.openu.a2017_app1.models.ModelSaveListener;
import com.openu.a2017_app1.models.Place;
import com.openu.a2017_app1.models.Review;

public class AddReview extends AppCompatActivity {

    public static final String EXTRA_PLACE_ID = "place";

    private EditText mComment;
    private RatingBar mRating;
    private Place mPlace;
    private Button mAddBtn;
    private boolean mShouldSave = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);

        Model.getQuery(Place.class).findAsync((String) getIntent().getExtras().get(EXTRA_PLACE_ID), new FindListener<Place>() {
            @Override
            public void onItemFound(Place item) {
                mPlace = item;
                if (mShouldSave) {
                    save();
                }
            }
        });

        mComment = (EditText) findViewById(R.id.comment);
        mRating = (RatingBar) findViewById(R.id.rating);

        mAddBtn = (Button) findViewById(R.id.add_review_button);
        mAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (updateErrors()) {
                    return;
                }
                if (mPlace == null) {
                    mShouldSave = true;
                    mAddBtn.setEnabled(false);
                    return;
                }
                save();
            }
        });
    }

    private void save() {
        Review review = new Review();
        review.setComment(mComment.getText().toString().trim());
        review.setScore((int)mRating.getRating());
        review.setPlace(mPlace);

        review.saveAsync(new ModelSaveListener() {
            @Override
            public void onSave(boolean succeeded, Object id) {
                if (succeeded) {
                    finish();
                } else {
                    mAddBtn.setEnabled(true);
                    Snackbar.make(mComment, R.string.failed_to_save_review, Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Updates the errors in the input views
     * @return true if some errors occur. false, if no error found.
     */
    private boolean updateErrors() {
        boolean errors = false;
        if (mRating.getRating() == 0) {
            ObjectAnimator
                    .ofFloat(mRating, "translationX", 0, 25, -25, 25, -25,15, -15, 6, -6, 0)
                    .start();
            errors = true;
        }
        return errors;
    }
}
