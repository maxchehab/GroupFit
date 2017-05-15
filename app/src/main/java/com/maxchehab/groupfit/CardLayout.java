package com.maxchehab.groupfit;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by Max on 5/10/2017.
 */

public class CardLayout extends CardView{

    public CardLayout(Context context, Event event) {
        super(context);
        init(event);
    }

    public CardLayout(Context context, AttributeSet attrs, Event event) {
        super(context, attrs);
        init(event);
    }

    public CardLayout(Context context, AttributeSet attrs, int defStyle, Event event) {
        super(context, attrs, defStyle);
        init(event);
    }

    private TextView title;
    private ImageView thumbnail;
    private ProgressBar progressBar;
    private TextView remaining;
    private ImageView activityIcon;
    private TextView date;

    private void init(final Event event) {
        inflate(getContext(), R.layout.card_layout, this);
        title = (TextView) findViewById(R.id.card_title);
        thumbnail = (ImageView) findViewById(R.id.card_thumbnail);
        progressBar = (ProgressBar) findViewById(R.id.card_progressBar);
        remaining = (TextView) findViewById(R.id.card_remaining);
        activityIcon = (ImageView) findViewById(R.id.card_activityIcon);
        date = (TextView) findViewById(R.id.card_time);

        title.setText(event.title);
        Picasso.with(getContext()).load("https://maps.googleapis.com/maps/api/staticmap?center=" + event.longitude + "," + event.latitude + "&zoom=18&size=400x400&scale=2&maptype=terrain&markers=%7C" + event.longitude + "," + event.latitude + "&key=AIzaSyCCadh2fDBt5EI-wgAsBeMIIDQUTWcdSGI").into(thumbnail);
        progressBar.setProgress(event.progress);
        remaining.setText(event.remaining + " remaining spots.");
        date.setText(event.date);

        if (event.activity.equals("cycling")) {
            activityIcon.setImageResource(R.drawable.ic_cycling);
        } else if (event.activity.equals("running")) {
            activityIcon.setImageResource(R.drawable.ic_running);
        } else if (event.activity.equals("crossfit")) {
            activityIcon.setImageResource(R.drawable.ic_crossfit);
        }


        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                FeedActivity.CURRENT_EVENT = event;
                Intent intent = new Intent(getContext(), ExpandedEventActivity.class);
                getContext().startActivity(intent);
            }
        });
    }
}
