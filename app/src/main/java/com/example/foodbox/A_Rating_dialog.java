package com.example.foodbox;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class A_Rating_dialog extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_activity_rating_dialog);

        Button rateUsButton = findViewById(R.id.submit_rating_button);
        rateUsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRatingDialog();
            }
        });
    }
    private void showRatingDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.a_activity_rating_dialog);
        dialog.setCancelable(true);

        TextView ratingTitle = dialog.findViewById(R.id.rating_title);
        RatingBar ratingBar = dialog.findViewById(R.id.rating_bar);
        Button submitButton = dialog.findViewById(R.id.submit_rating_button);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Do something with the rating value
                float ratingValue = ratingBar.getRating();
                Toast.makeText(A_Rating_dialog.this, "Rating: " + ratingValue, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

}