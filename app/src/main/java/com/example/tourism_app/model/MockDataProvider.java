package com.example.tourism_app.model;

import com.example.tourism_app.R;
import java.util.ArrayList;
import java.util.List;

public class MockDataProvider {

    public static List<Category> getCategories() {
        List<Category> list = new ArrayList<>();
        list.add(new Category("1", "All", R.drawable.ic_search, true));
        list.add(new Category("2", "Temples", R.drawable.ic_temple, false));
        list.add(new Category("3", "Nature", R.drawable.ic_nature, false));
        list.add(new Category("4", "Food", R.drawable.ic_food, false));
        list.add(new Category("5", "Culture", R.drawable.ic_temple, false));
        return list;
    }

    public static List<Place> getFeaturedPlaces() {
        List<Place> list = new ArrayList<>();
        list.add(new Place(
                "f1",
                "Angkor Wat",
                "Siem Reap",
                "Temple",
                4.9,
                "$37",
                "Angkor Wat is a temple complex in Cambodia and the largest religious monument in the world by land area. It is the heart and soul of Cambodia.",
                true,
                R.drawable.placeholder_image
        ));
        list.add(new Place(
                "f2",
                "Bayon Temple",
                "Angkor Thom, Siem Reap",
                "Temple",
                4.8,
                "Included in Pass",
                "Famous for its 216 giant smiling stone faces of Avalokiteshvara on 54 towers representing the provinces of ancient Khmer Empire.",
                false,
                R.drawable.placeholder_image
        ));
        list.add(new Place(
                "f3",
                "Ta Prohm Temple",
                "Siem Reap",
                "Temple",
                4.9,
                "Included in Pass",
                "Famous for the massive silk-cotton tree roots wrapping around ancient stone walls, popularised by the Tomb Raider movie.",
                true,
                R.drawable.placeholder_image
        ));
        return list;
    }

    public static List<Place> getPopularPlaces() {
        List<Place> list = new ArrayList<>();
        list.add(new Place(
                "p1",
                "Banteay Srei",
                "Siem Reap",
                "Culture",
                4.8,
                "Included in Pass",
                "A 10th-century Cambodian temple dedicated to the Hindu god Shiva. Built of red sandstone with exquisite intricate carvings.",
                false,
                R.drawable.placeholder_image
        ));
        list.add(new Place(
                "p2",
                "Phnom Bakheng",
                "Siem Reap",
                "Nature",
                4.6,
                "Free Sunset",
                "A temple mountain in honor of Hindu god Shiva, famous for spectacular sunset views over Angkor Wat.",
                true,
                R.drawable.placeholder_image
        ));
        list.add(new Place(
                "p3",
                "Tonle Sap Floating Village",
                "Siem Reap",
                "Nature",
                4.5,
                "$20",
                "The largest freshwater lake in Southeast Asia featuring vibrant stilted and floating fishing villages.",
                false,
                R.drawable.placeholder_image
        ));
        list.add(new Place(
                "p4",
                "Pub Street & Night Market",
                "Downtown Siem Reap",
                "Food",
                4.7,
                "Free Entry",
                "The lively nightlife epicenter of Siem Reap with night markets, Khmer street food, and vibrant cafes.",
                true,
                R.drawable.placeholder_image
        ));
        return list;
    }

    public static List<Trip> getTrips() {
        List<Trip> list = new ArrayList<>();
        list.add(new Trip(
                "t1",
                "Angkor Grand Heritage Tour",
                "Siem Reap, Cambodia",
                "Nov 15 - Nov 18, 2025",
                "Upcoming",
                R.drawable.placeholder_image
        ));
        list.add(new Trip(
                "t2",
                "Tonle Sap Lake Explorer",
                "Siem Reap",
                "Oct 05 - Oct 07, 2025",
                "Completed",
                R.drawable.placeholder_image
        ));
        return list;
    }

    public static List<Review> getReviews() {
        List<Review> list = new ArrayList<>();
        list.add(new Review("r1", "Sophea Chan", 5.0f, "2 days ago", "Unbelievable sunrise view! Arrive around 5 AM to get a prime spot near the reflection pond. Truly magical."));
        list.add(new Review("r2", "David Miller", 4.5f, "1 week ago", "Exploring the stone faces of Bayon was the highlight of our trip to Cambodia. Great historical guide!"));
        return list;
    }

    public static List<ChatMessage> getInitialChatMessages() {
        List<ChatMessage> list = new ArrayList<>();
        list.add(new ChatMessage("Hello! I am your Angkor AI Travel Assistant. How can I help you plan your travel in Siem Reap and Cambodia today?", false, "Just now"));
        return list;
    }

    public static List<NotificationItem> getNotifications() {
        List<NotificationItem> list = new ArrayList<>();
        list.add(new NotificationItem("n1", "Trip Reminder", "Your Angkor Grand Heritage Tour starts in 3 days! Check your itinerary.", "10 mins ago", true));
        list.add(new NotificationItem("n2", "Special Festival", "Angkor Night Cultural Festival begins this Friday at Pub Street.", "1 hour ago", true));
        list.add(new NotificationItem("n3", "Weather Alert", "Sunny skies expected in Siem Reap this weekend. Perfect for temple photography!", "1 day ago", false));
        return list;
    }

    public static List<EventItem> getEvents() {
        List<EventItem> list = new ArrayList<>();
        list.add(new EventItem("e1", "Angkor Night Cultural Festival", "Pub Street, Siem Reap", "NOV 28", R.drawable.placeholder_image));
        list.add(new EventItem("e2", "Siem Reap International Half Marathon", "Angkor Wat Complex", "DEC 07", R.drawable.placeholder_image));
        return list;
    }
}