package com.campusconnect.model;

public enum Category {
    INFRASTRUCTURE("Infrastructure", "Damaged buildings, classroom issues, leaking roofs, etc.", "building"),
    FACILITIES("Facilities", "Projectors, fans, AC, electricity, water supply, etc.", "wrench"),
    FACULTY_ACADEMICS("Faculty & Academics", "Teaching-related concerns, academic issues, timetable problems, etc.", "graduation-cap"),
    FOOD_CANTEEN("Food & Canteen", "Food quality, hygiene, pricing, and canteen facilities.", "utensils"),
    TRANSPORTATION("Transportation", "College buses, routes, timings, and transport-related issues.", "bus"),
    HOSTEL("Hostel", "Room maintenance, water, electricity, and hostel facilities.", "home"),
    LIBRARY("Library", "Books, seating, computer availability, and library services.", "book-open"),
    SANITATION_HYGIENE("Sanitation & Hygiene", "Washrooms, cleanliness, waste disposal, etc.", "sparkles"),
    CAMPUS_SAFETY("Campus Safety & Security", "Safety concerns, damaged equipment, and security-related problems.", "shield-alert"),
    OTHER("Other Issues", "Any complaint that does not fit the above categories.", "help-circle");

    private final String displayName;
    private final String description;
    private final String icon;

    Category(String displayName, String description, String icon) {
        this.displayName = displayName;
        this.description = description;
        this.icon = icon;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public String getIcon() {
        return icon;
    }

    public static Category fromName(String name) {
        for (Category category : values()) {
            if (category.name().equalsIgnoreCase(name)) {
                return category;
            }
        }
        return OTHER;
    }
}

