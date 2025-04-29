package com.example.dubevents;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.HashSet;
import java.util.Set;

public class ProfileActivity extends AppCompatActivity {

    private int currentStep = 0;
    private View[] steps;
    private SharedPreferences sharedPreferences;
    private boolean isSetupMode = true;

    private Button nextButton, saveButton;
    private Spinner livingSpinner;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences("UserProfile", Context.MODE_PRIVATE);

        if (sharedPreferences.contains("name")) {
            isSetupMode = false;
            displayProfile();
        } else {
            isSetupMode = true;
            setContentView(R.layout.activity_profile_setup);

            steps = new View[]{
                    findViewById(R.id.step_name),
                    findViewById(R.id.step_major),
                    findViewById(R.id.step_graduation_year),
                    findViewById(R.id.step_interests),
                    findViewById(R.id.step_living),
                    findViewById(R.id.step_background)
            };

            nextButton = findViewById(R.id.next_button);
            saveButton = findViewById(R.id.save_button);

            nextButton.setOnClickListener(v -> handleNextStep());
            saveButton.setOnClickListener(v -> saveProfileData());

            livingSpinner = findViewById(R.id.input_living);
            if (livingSpinner != null) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item,
                        new String[]{"On Campus", "Off Campus", "Commuter"});
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                livingSpinner.setAdapter(adapter);
            }

            showStep(currentStep);
        }
    }

    private void showStep(int stepIndex) {
        for (int i = 0; i < steps.length; i++) {
            steps[i].setVisibility(i == stepIndex ? View.VISIBLE : View.GONE);
            if (i == stepIndex) {
                steps[i].startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left));
            }
        }

        if (stepIndex == steps.length - 1) {
            nextButton.setVisibility(View.GONE);
            saveButton.setVisibility(View.VISIBLE);
        } else {
            nextButton.setVisibility(View.VISIBLE);
            saveButton.setVisibility(View.GONE);
        }
    }

    private void handleNextStep() {
        if (currentStep < steps.length - 1) {
            currentStep++;
            showStep(currentStep);
        }
    }

    private void saveProfileData() {
        String name = ((EditText) findViewById(R.id.input_name)).getText().toString();
        String major = ((EditText) findViewById(R.id.input_major)).getText().toString();
        String graduationYear = ((EditText) findViewById(R.id.input_graduation_year)).getText().toString();

        String living = "";
        if (livingSpinner != null && livingSpinner.getSelectedItem() != null) {
            living = livingSpinner.getSelectedItem().toString();
        }

        LinearLayout interestsLayout = findViewById(R.id.interests_layout);
        Set<String> interests = new HashSet<>();
        if (interestsLayout != null) {
            for (int i = 0; i < interestsLayout.getChildCount(); i++) {
                View child = interestsLayout.getChildAt(i);
                if (child instanceof CheckBox && ((CheckBox) child).isChecked()) {
                    interests.add(((CheckBox) child).getText().toString());
                }
            }
        }

        String customInterest = ((EditText) findViewById(R.id.input_custom_interest)).getText().toString();
        if (!customInterest.isEmpty()) {
            interests.add(customInterest);
        }

        String background = ((EditText) findViewById(R.id.input_background)).getText().toString();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name", name);
        editor.putString("major", major);
        editor.putString("graduationYear", graduationYear);
        editor.putStringSet("interests", interests);
        editor.putString("living", living);
        editor.putString("background", background);
        editor.apply();

        Toast.makeText(this, "Profile saved!", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void displayProfile() {
        setContentView(R.layout.activity_profile_display);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
                startActivity(new Intent(this, MainActivity.class));
            } else if (id == R.id.nav_calendar) {
                startActivity(new Intent(this, CalendarActivity.class));
            } else if (id == R.id.nav_profile) {
                drawerLayout.closeDrawers();
            }
            return true;
        });

        SharedPreferences prefs = getSharedPreferences("UserProfile", MODE_PRIVATE);

        ((TextView) findViewById(R.id.display_name)).setText(prefs.getString("name", ""));
        ((TextView) findViewById(R.id.display_major)).setText(prefs.getString("major", ""));
        ((TextView) findViewById(R.id.display_graduation_year)).setText(prefs.getString("graduationYear", ""));
        ((TextView) findViewById(R.id.display_interests)).setText(String.join(", ", prefs.getStringSet("interests", new HashSet<>())));
        ((TextView) findViewById(R.id.display_living)).setText(prefs.getString("living", ""));
        ((TextView) findViewById(R.id.display_background)).setText(prefs.getString("background", ""));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return toggle != null && toggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }
}
