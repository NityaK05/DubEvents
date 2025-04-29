package com.example.dubevents;

import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dubevents.adapters.EventAdapter;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;
import java.time.*;

public class MainActivity extends AppCompatActivity {

    private EventAdapter eventAdapter;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private List<Event> events;  // Make this a field so swipe functions can access it
    private List<Event> savedEvents = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up the navigation drawer
        drawerLayout = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.nav_home) {
                drawerLayout.closeDrawers();
            } else if (id == R.id.nav_calendar) {
                // Pass saved events to CalendarActivity
                Intent intent = new Intent(this, CalendarActivity.class);
                intent.putParcelableArrayListExtra("acceptedEvents", new ArrayList<>(savedEvents));  // Pass events
                startActivity(intent);
                drawerLayout.closeDrawers();
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                drawerLayout.closeDrawers();
            }
            return true;
        });

        // Set up RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Sample events data
        events = new ArrayList<>();
        events.add(new Event("AI Community of Practice", "The UW AI Community of Practice is for everyone! We welcome participation from the entire university, including students. We want to build community and are planning all sorts of fun and interesting events", ZonedDateTime.of(2025, 04, 30, 10, 0, 0, 0, ZoneId.of("PST")), "Zoom"));
        events.add(new Event("UW Botanic Gardens Tour", "UW Botanic Gardens is committed to enriching the lives of all community members with free public tours.", ZonedDateTime.of(2025, 5, 1, 11, 30, 0, 0, ZoneId.of("PST")), "Washington Park Arboretum"));
        events.add(new Event("Social Media Marketing Skills for Entrepreneurs", "Join us to learn valuable skills to support your current (or future!) entrepreneurial ventures!", ZonedDateTime.of(2025, 5, 2, 13, 0, 0, 0, ZoneId.of("PST")), "Zoom"));
        events.add(new Event("UW Planetarium First Friday Show", "Please reserve your spot!", ZonedDateTime.of(2025, 5, 3, 19, 0, 0, 0, ZoneId.of("PST")), "UW Planetarium"));
        events.add(new Event("Data Storage Day", "Don’t miss this chance to explore a powerful, university-supported storage service that’s flexible, accessible, and ready for you!", ZonedDateTime.of(2025, 5, 5, 13, 0, 0, 0, ZoneId.of("PST")), "Physics / Astronomy Building (PAT)"));
        events.add(new Event("Virtual Morning Flow with Pranify Yoga", "Start your day with intention, ease and energy.", ZonedDateTime.of(2025, 5, 6, 18, 0, 0, 0, ZoneId.of("PST")), "Intramural Activities Building (IMA)"));
        events.add(new Event("Coffee Roasting Workshop", "Participants will tour the Husky Grind Coffee roasting lab and learn how coffee is roasted firsthand, taking home a sample of coffee from a batch they had a hand in roasting.", ZonedDateTime.of(2025, 5, 7, 15, 0, 0, 0, ZoneId.of("PST")), "Husky Grind Coffee Roastary"));
        events.add(new Event("Git for Everyone!", "You'll learn how to track changes, collaborate with others using GitHub/GitLab, and structure your work for transparency and reproducibility.", ZonedDateTime.of(2025, 5, 8, 13, 0, 0, 0, ZoneId.of("PST")), "Suzzallo Library (SUZ)"));

        // Initialize EventAdapter
        eventAdapter = new EventAdapter(this, events);
        recyclerView.setAdapter(eventAdapter);
        recyclerView.setHasFixedSize(true);

        // Set up swipe actions
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP, // Allow up movement
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT // Allow left and right swipe
        ) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false; // no drag & drop
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                // new change
                if (direction == ItemTouchHelper.RIGHT) {
                    // ➡️ Swipe right: Add to calendar
                    addToCalendar(events.get(position));
                    events.remove(position);
                    eventAdapter.notifyItemRemoved(position);
                } else if (direction == ItemTouchHelper.LEFT) {
                    // ⬅️ Swipe left: Discard
                    Toast.makeText(MainActivity.this, "Event discarded", Toast.LENGTH_SHORT).show();
                    events.remove(position);
                    eventAdapter.notifyItemRemoved(position);
                } else if (direction == ItemTouchHelper.UP) {
                    // ⬆️ Swipe up: Maybe (move to end)
                    Event maybeEvent = events.get(position);
                    events.remove(position);
                    events.add(maybeEvent);
                    eventAdapter.notifyItemRemoved(position);
                    eventAdapter.notifyItemInserted(events.size() - 1);

                    Toast.makeText(MainActivity.this, "Maybe: moved to end", Toast.LENGTH_SHORT).show();
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void addToCalendar(Event event) {
        // Instead of opening the Calendar app immediately,
        // just store the event into the saved list
        savedEvents.add(event);
        Toast.makeText(this, "Event saved for Calendar", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
