package com.example.dubevents.adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.dubevents.EventActivityDetails;
import com.example.dubevents.databinding.EventCardBinding;
import com.example.dubevents.Event;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private final Activity activity;
    private final List<Event> events;

    public EventAdapter(Activity activity, List<Event> events) {
        this.activity = activity;
        this.events = events;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        EventCardBinding binding = EventCardBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new EventViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = events.get(position);
        holder.bind(event);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), EventActivityDetails.class);
            intent.putExtra("eventTitle", event.getTitle());
            intent.putExtra("eventDescription", event.getDescription());
            intent.putExtra("eventDate", event.getDate().toString());
            intent.putExtra("eventLocation", event.getLocation());
            holder.itemView.getContext().startActivity(intent);

            activity.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        private final EventCardBinding binding;

        public EventViewHolder(EventCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Event event) {
            binding.eventTitle.setText(event.getTitle());
            binding.eventDate.setText(formatEventDate(event.getDate()));
            binding.eventLocation.setText(event.getLocation());
        }

        private String formatEventDate(ZonedDateTime date) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy 'at' h:mm a");
            return date.format(formatter);
        }
    }
}
