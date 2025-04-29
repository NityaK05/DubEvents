package com.example.dubevents;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.time.*;
import java.time.format.DateTimeFormatter;

public class EventActivityDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_activity_details);

        // Get event data from the intent
        String eventTitle = getIntent().getStringExtra("eventTitle");
        String eventDescription = getIntent().getStringExtra("eventDescription");
        String eventDate = getIntent().getStringExtra("eventDate");
        String eventLocation = getIntent().getStringExtra("eventLocation");

        // Set the data to your views
        ((TextView) findViewById(R.id.eventTitle)).setText(eventTitle);
        ((TextView) findViewById(R.id.eventDescription)).setText(eventDescription);
        ((TextView) findViewById(R.id.eventDate)).setText(formatEventDate(eventDate));
        ((TextView) findViewById(R.id.eventLocation)).setText(eventLocation);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Event Details");
    }

    private String formatEventDate(String rawDate) {
        try {
            ZonedDateTime dateTime = ZonedDateTime.parse(rawDate);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy 'at' h:mm a");
            return dateTime.format(formatter);
        } catch (Exception e) {
            e.printStackTrace();
            return rawDate; // fallback
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
