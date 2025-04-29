package com.example.dubevents;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.view.Gravity;
import android.view.ViewGroup;
import android.graphics.Typeface;
import java.util.HashMap;
import java.util.Map;

public class CalendarActivity extends AppCompatActivity {

    // Sample hardcoded events for each weekday
    private final Map<String, String[]> weeklyEvents = new HashMap<String, String[]>() {{
        put("Monday", new String[]{"CS Lecture at 10 AM", "Math Study Group at 2 PM"});
        put("Tuesday", new String[]{"Hackathon Prep at 3 PM"});
        put("Wednesday", new String[]{"Club Meeting at 12 PM"});
        put("Thursday", new String[]{"Open Gym at 5 PM"});
        put("Friday", new String[]{"Tech Talk at 1 PM", "Dinner Social at 7 PM"});
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 32, 32, 32);
        layout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        // Title
        TextView title = new TextView(this);
        title.setText("Weekly Calendar");
        title.setTextSize(24);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER_HORIZONTAL);
        layout.addView(title);

        // Loop through weekdays and display events
        for (String day : new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"}) {
            TextView dayView = new TextView(this);
            dayView.setText(day);
            dayView.setTextSize(20);
            dayView.setTypeface(null, Typeface.BOLD);
            dayView.setPadding(0, 20, 0, 10);
            layout.addView(dayView);

            String[] events = weeklyEvents.getOrDefault(day, new String[]{});
            if (events.length == 0) {
                TextView noEvent = new TextView(this);
                noEvent.setText("  • No events");
                layout.addView(noEvent);
            } else {
                for (String event : events) {
                    TextView eventView = new TextView(this);
                    eventView.setText("  • " + event);
                    layout.addView(eventView);
                }
            }
        }

        setContentView(layout);
    }
}
