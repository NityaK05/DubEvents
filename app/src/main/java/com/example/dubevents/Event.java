package com.example.dubevents;

import android.os.Parcel;
import android.os.Parcelable;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Event implements Parcelable {
    private String title;
    private String description;
    private ZonedDateTime date;
    private String location;

    public Event(String title, String description, ZonedDateTime date, String location) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.location = location;
    }

    // Parcelable constructor
    protected Event(Parcel in) {
        title = in.readString();
        description = in.readString();
        location = in.readString();
        String dateString = in.readString();
        date = ZonedDateTime.parse(dateString);
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public String getLocation() {
        return location;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(location);
        dest.writeString(date.format(DateTimeFormatter.ISO_ZONED_DATE_TIME));
    }
}
