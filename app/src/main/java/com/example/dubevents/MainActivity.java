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
        setContentView(R.layout.activity_main);

        // Toolbar
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
        events.add(new Event("Welcome Party", "A fun welcome party for new students.",
                ZonedDateTime.of(2025, 10, 1, 14, 0, 0, 0, ZoneId.of("PST")), "Student Center"));
        events.add(new Event("Tech Talk", "Learn about the latest in AI and ML.",
                ZonedDateTime.of(2025, 10, 10, 16, 0, 0, 0, ZoneId.of("PST")), "Engineering Hall"));
        events.add(new Event("Hackathon", "Join us for a 24-hour coding challenge.",
                ZonedDateTime.of(2025, 10, 5, 12, 30, 0, 0, ZoneId.of("PST")), "Innovation Lab"));

        // Adapter
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
