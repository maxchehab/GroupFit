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
    private ImageView activityIcon;
    private TextView date;
    private TextView time;
    private TextView attendees;

    private void init(final Event event) {
        inflate(getContext(), R.layout.card_layout, this);
        title = (TextView) findViewById(R.id.card_title);
        thumbnail = (ImageView) findViewById(R.id.card_thumbnail);
        activityIcon = (ImageView) findViewById(R.id.card_activityIcon);
        date = (TextView) findViewById(R.id.card_date);
        time = (TextView) findViewById(R.id.card_time);
        attendees = (TextView) findViewById(R.id.card_attendees);

        title.setText(event.title);
        Picasso.with(getContext()).load("https://maps.googleapis.com/maps/api/staticmap?center=" + event.longitude + "," + event.latitude + "&zoom=18&size=400x400&scale=2&maptype=terrain&markers=%7C" + event.longitude + "," + event.latitude + "&key=AIzaSyCCadh2fDBt5EI-wgAsBeMIIDQUTWcdSGI").into(thumbnail);
        date.setText(event.date);
        time.setText(event.time);
        attendees.setText(event.attendeesCount + " people are going • " + event.remainingCount +  " spots left.");

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
