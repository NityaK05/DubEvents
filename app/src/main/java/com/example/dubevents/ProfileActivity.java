package com.example.dubevents;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashSet;
import java.util.Set;

public class ProfileActivity extends AppCompatActivity {

    private int currentStep = 0;
    private View[] steps;
    private SharedPreferences sharedPreferences;
    private boolean isSetupMode = true; // Track if we're in setup or display mode

    private Button nextButton, saveButton;
    private Spinner livingSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences("UserProfile", Context.MODE_PRIVATE);

        if (sharedPreferences.contains("name")) {
            // If user profile exists, show the profile display immediately
            isSetupMode = false;
            displayProfile();
        } else {
            isSetupMode = true;
            setContentView(R.layout.activity_profile_setup); // <-- Make sure it's activity_profile_setup.xml

            // Initialize step views
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

            // Setup Living Situation Spinner
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
            if (i == stepIndex) {
                steps[i].setVisibility(View.VISIBLE);
                steps[i].startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left));
            } else {
                steps[i].setVisibility(View.GONE);
            }
        }

        // If we are at the last step, hide "Next" button and show "Save" button
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
        // Collect data from inputs
        String name = ((EditText) findViewById(R.id.input_name)).getText().toString();
        String major = ((EditText) findViewById(R.id.input_major)).getText().toString();
        String graduationYear = ((EditText) findViewById(R.id.input_graduation_year)).getText().toString();

        // Safe collection of living situation
        String living = "";
        if (livingSpinner != null && livingSpinner.getSelectedItem() != null) {
            living = livingSpinner.getSelectedItem().toString();
        }

        // Collect interests
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

        // Save data to SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name", name);
        editor.putString("major", major);
        editor.putString("graduationYear", graduationYear);
        editor.putStringSet("interests", interests);
        editor.putString("living", living);
        editor.putString("background", background);
        editor.apply();

        Toast.makeText(this, "Profile saved!", Toast.LENGTH_SHORT).show();

        // After saving, go back to MainActivity
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isSetupMode && sharedPreferences.contains("name")) {
            displayProfile();
        }
    }

    private void displayProfile() {
        setContentView(R.layout.activity_profile_display);

        ((TextView) findViewById(R.id.display_name)).setText(sharedPreferences.getString("name", ""));
        ((TextView) findViewById(R.id.display_major)).setText(sharedPreferences.getString("major", ""));
        ((TextView) findViewById(R.id.display_graduation_year)).setText(sharedPreferences.getString("graduationYear", ""));
        ((TextView) findViewById(R.id.display_interests)).setText(String.join(", ", sharedPreferences.getStringSet("interests", new HashSet<>())));
        ((TextView) findViewById(R.id.display_living)).setText(sharedPreferences.getString("living", ""));
        ((TextView) findViewById(R.id.display_background)).setText(sharedPreferences.getString("background", ""));
    }
}
