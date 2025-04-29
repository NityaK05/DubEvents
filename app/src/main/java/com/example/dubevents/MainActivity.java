package com.example.dubevents;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import android.content.SharedPreferences;

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
    private List<Event> events;
    private List<Event> savedEvents = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if the user profile exists
        SharedPreferences sharedPreferences = getSharedPreferences("UserProfile", MODE_PRIVATE);
        if (!sharedPreferences.contains("name")) {
            // Redirect to ProfileActivity for setup
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        // Show the message every time the activity is opened
        Toast.makeText(this, "Swipe left and right to interact with events!", Toast.LENGTH_LONG).show();

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
                intent.putParcelableArrayListExtra("acceptedEvents", new ArrayList<>(savedEvents));
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
        events.add(new Event("Welcome Party", "A fun welcome party for new students.", ZonedDateTime.of(2025, 10, 1, 14, 0, 0, 0, ZoneId.of("PST")), "Student Center"));
        events.add(new Event("Tech Talk", "Learn about the latest in AI and ML.", ZonedDateTime.of(2025, 10, 10, 16, 0, 0, 0, ZoneId.of("PST")), "Engineering Hall"));
        events.add(new Event("Hackathon", "Join us for a 24-hour coding challenge.", ZonedDateTime.of(2025, 10, 5, 12, 30, 0, 0, ZoneId.of("PST")), "Innovation Lab"));

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
