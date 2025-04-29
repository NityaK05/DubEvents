package com.example.dubevents;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
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
        Toast.makeText(this, "Swipe left to discard and right to add to calendar!", Toast.LENGTH_LONG).show();

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Drawer
        drawerLayout = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.nav_home) {
                drawerLayout.closeDrawers();
            } else if (id == R.id.nav_calendar) {
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

        // RecyclerView setup
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Sample events
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

        // Swipe behavior with green gradient for right swipe
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if (direction == ItemTouchHelper.RIGHT) {
                    addToCalendar(events.get(position));
                    events.remove(position);
                    eventAdapter.notifyItemRemoved(position);
                } else if (direction == ItemTouchHelper.LEFT) {
                    Toast.makeText(MainActivity.this, "Event discarded", Toast.LENGTH_SHORT).show();
                    events.remove(position);
                    eventAdapter.notifyItemRemoved(position);
                } else if (direction == ItemTouchHelper.UP) {
                    Event maybeEvent = events.get(position);
                    events.remove(position);
                    events.add(maybeEvent);
                    eventAdapter.notifyItemRemoved(position);
                    eventAdapter.notifyItemInserted(events.size() - 1);
                    Toast.makeText(MainActivity.this, "Maybe: moved to end", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                    int actionState, boolean isCurrentlyActive) {

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    int itemTop = viewHolder.itemView.getTop();
                    int itemBottom = viewHolder.itemView.getBottom();
                    int itemRight = viewHolder.itemView.getRight();
                    int itemLeft = viewHolder.itemView.getLeft();
                    int itemHeight = itemBottom - itemTop;
                    int itemCenterY = itemTop + itemHeight / 2;

                    Paint paint = new Paint();

                    if (dX > 0) {
                        // ✅ Right swipe — Green glow (smaller radius)
                        float radius = Math.max(55f, dX * 1.1f);  // was 70f+, now more compact
                        float glowCenterX = itemRight - dX * 0.1f;

                        RadialGradient glow = new RadialGradient(
                                glowCenterX, itemCenterY, radius,
                                new int[]{
                                        Color.argb(190, 46, 125, 50),  // bold green with transparency
                                        Color.TRANSPARENT
                                },
                                new float[]{0.3f, 1f},
                                Shader.TileMode.CLAMP
                        );

                        paint.setShader(glow);
                        c.drawCircle(glowCenterX, itemCenterY, radius, paint);

                    } else if (dX < 0) {
                        // ❌ Left swipe — Softer red glow (smaller radius)
                        float radius = Math.max(40f, Math.abs(dX) * 0.9f);  // was 50f+, now tighter
                        float glowCenterX = itemLeft - dX * 0.1f;

                        RadialGradient glow = new RadialGradient(
                                glowCenterX, itemCenterY, radius,
                                new int[]{
                                        Color.argb(140, 198, 40, 40),  // clean red with transparency
                                        Color.TRANSPARENT
                                },
                                new float[]{0.4f, 1f},
                                Shader.TileMode.CLAMP
                        );

                        paint.setShader(glow);
                        c.drawCircle(glowCenterX, itemCenterY, radius, paint);
                    }
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void addToCalendar(Event event) {
        savedEvents.add(event);
        Toast.makeText(this, "Event saved for Calendar", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return toggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }
}
