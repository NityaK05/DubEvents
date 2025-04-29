package com.example.dubevents;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.view.View;
import android.graphics.Color;
import android.graphics.Typeface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class CalendarActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private ArrayList<Event> acceptedEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Drawer
        drawerLayout = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Navigation
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
            }
            drawerLayout.closeDrawers();
            return true;
        });

        // Get and sort events
        acceptedEvents = getIntent().getParcelableArrayListExtra("acceptedEvents");
        if (acceptedEvents == null) {
            acceptedEvents = new ArrayList<>();
        }

        Collections.sort(acceptedEvents, Comparator.comparing(Event::getDate));

        // Group events by day of week
        Map<DayOfWeek, List<Event>> eventsByDay = new TreeMap<>();
        for (DayOfWeek day : DayOfWeek.values()) {
            eventsByDay.put(day, new ArrayList<>());
        }
        for (Event e : acceptedEvents) {
            eventsByDay.get(e.getDate().getDayOfWeek()).add(e);
        }

        // Display events under each weekday
        LinearLayout calendarContainer = findViewById(R.id.calendarContainer);
        LayoutInflater inflater = LayoutInflater.from(this);

        for (DayOfWeek day : DayOfWeek.values()) {
            TextView dayHeader = new TextView(this);
            dayHeader.setText(day.toString());
            dayHeader.setTextSize(22f);
            dayHeader.setTextColor(Color.parseColor("#B7A57A"));  // UW Gold
            dayHeader.setTypeface(null, Typeface.BOLD);
            dayHeader.setPadding(0, 32, 0, 12);
            calendarContainer.addView(dayHeader);

            List<Event> events = eventsByDay.get(day);
            if (events.isEmpty()) {
                TextView empty = new TextView(this);
                empty.setText("No events");
                calendarContainer.addView(empty);
            } else {
                for (Event e : events) {
                    View card = inflater.inflate(R.layout.event_card, calendarContainer, false);

                    ((TextView) card.findViewById(R.id.eventTitle)).setText(e.getTitle());
                    ((TextView) card.findViewById(R.id.eventDescription)).setText(e.getDescription());
                    ((TextView) card.findViewById(R.id.eventDate)).setText(e.getDate().toLocalDate().toString());
                    ((TextView) card.findViewById(R.id.eventLocation)).setText(e.getLocation());

                    calendarContainer.addView(card);
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return toggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }
}
