package com.example.dubevents;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class EventStorageHelper {

    private static List<Event> allEvents;

    private static List<Event> savedEvents;

    public static void setAllEvents() {
        allEvents = new ArrayList<>();
        allEvents.add(new Event("AI Community of Practice", "The UW AI Community of Practice is for everyone! We welcome participation from the entire university, including students. We want to build community and are planning all sorts of fun and interesting events", ZonedDateTime.of(2025, 04, 30, 10, 0, 0, 0, ZoneId.of("PST")), "Zoom"));
        allEvents.add(new Event("UW Botanic Gardens Tour", "UW Botanic Gardens is committed to enriching the lives of all community members with free public tours.", ZonedDateTime.of(2025, 5, 1, 11, 30, 0, 0, ZoneId.of("PST")), "Washington Park Arboretum"));
        allEvents.add(new Event("Social Media Marketing Skills for Entrepreneurs", "Join us to learn valuable skills to support your current (or future!) entrepreneurial ventures!", ZonedDateTime.of(2025, 5, 2, 13, 0, 0, 0, ZoneId.of("PST")), "Zoom"));
        allEvents.add(new Event("UW Planetarium First Friday Show", "Please reserve your spot!", ZonedDateTime.of(2025, 5, 3, 19, 0, 0, 0, ZoneId.of("PST")), "UW Planetarium"));
        allEvents.add(new Event("Data Storage Day", "Don’t miss this chance to explore a powerful, university-supported storage service that’s flexible, accessible, and ready for you!", ZonedDateTime.of(2025, 5, 5, 13, 0, 0, 0, ZoneId.of("PST")), "Physics / Astronomy Building (PAT)"));
        allEvents.add(new Event("Virtual Morning Flow with Pranify Yoga", "Start your day with intention, ease and energy.", ZonedDateTime.of(2025, 5, 6, 18, 0, 0, 0, ZoneId.of("PST")), "Intramural Activities Building (IMA)"));
        allEvents.add(new Event("Coffee Roasting Workshop", "Participants will tour the Husky Grind Coffee roasting lab and learn how coffee is roasted firsthand, taking home a sample of coffee from a batch they had a hand in roasting.", ZonedDateTime.of(2025, 5, 7, 15, 0, 0, 0, ZoneId.of("PST")), "Husky Grind Coffee Roastary"));
        allEvents.add(new Event("Git for Everyone!", "You'll learn how to track changes, collaborate with others using GitHub/GitLab, and structure your work for transparency and reproducibility.", ZonedDateTime.of(2025, 5, 8, 13, 0, 0, 0, ZoneId.of("PST")), "Suzzallo Library (SUZ)"));
    }

    public static void addSavedEvents(Event event) {
        if (savedEvents == null) {
            savedEvents = new ArrayList<>();
        }
        savedEvents.add(event);
    }

    public static List<Event> getSavedEvents() {
        return savedEvents;
    }

    public static List<Event> getAllEvents() {
        return allEvents;
    }

    public static void removeEvent(int index) {
        if (allEvents != null) {
            allEvents.remove(index);
        }
    }

}