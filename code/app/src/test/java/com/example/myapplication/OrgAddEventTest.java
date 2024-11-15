package com.example.myapplication;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import com.example.myapplication.view.organization.OrgAddEvent;

import java.util.Date;

public class OrgAddEventTest {

    private OrgAddEvent fragment;

    @Before
    public void setUp() {
        fragment = new OrgAddEvent();
    }

    @Test
    public void testParseDate_validDate() {
        String validDateString = "2024-12-25 18:00";
        Date result = fragment.parseDate(validDateString);
        assertNotNull(result);  // Should return a valid date
    }

    @Test
    public void testParseDate_invalidDate() {
        Date result = fragment.parseDate("invalid-date");
        assertNull("Expected null for invalid date input", result);
    }

    @Test
    public void testValidateEventData_allValid() {
        String eventName = "Test Event";
        String eventDescription = "Description";
        String eventLocation = "Location";
        String eventPosterURL = "https://example.com/poster.png";
        int eventCapacityStr = 100;
        int eventPriceStr = 50;

        Date eventStart = fragment.parseDate("2024-12-25 10:00");
        Date eventEnd = fragment.parseDate("2024-12-25 18:00");
        Date registrationStart = fragment.parseDate("2024-11-01 10:00");
        Date registrationEnd = fragment.parseDate("2024-12-20 18:00");

        assertTrue(fragment.validateEventData(eventName, eventDescription, eventLocation, eventPosterURL,
                eventCapacityStr, eventPriceStr, eventStart, eventEnd,
                registrationStart, registrationEnd));
    }

    @Test
    public void testValidateEventData_invalidCapacity() {
        String eventName = "Test Event";
        String eventDescription = "Description";
        String eventLocation = "Location";
        String eventPosterURL = "https://example.com/poster.png";
        int eventCapacityStr = -10;  // Invalid capacity
        int eventPriceStr = 50;

        Date eventStart = fragment.parseDate("2024-12-25 10:00");
        Date eventEnd = fragment.parseDate("2024-12-25 18:00");
        Date registrationStart = fragment.parseDate("2024-11-01 10:00");
        Date registrationEnd = fragment.parseDate("2024-12-20 18:00");

        assertFalse(fragment.validateEventData(eventName, eventDescription, eventLocation, eventPosterURL,
                eventCapacityStr, eventPriceStr, eventStart, eventEnd,
                registrationStart, registrationEnd));
    }

    @Test
    public void testValidateEventData_registrationEndAfterEventStart() {
        String eventName = "Test Event";
        String eventDescription = "Description";
        String eventLocation = "Location";
        String eventPosterURL = "https://example.com/poster.png";
        int eventCapacityStr = 100;
        int eventPriceStr = 50;

        Date eventStart = fragment.parseDate("2024-12-25 10:00");
        Date eventEnd = fragment.parseDate("2024-12-25 18:00");
        Date registrationStart = fragment.parseDate("2024-11-01 10:00");
        Date registrationEnd = fragment.parseDate("2024-12-26 10:00");  // Invalid registration end date

        assertFalse(fragment.validateEventData(eventName, eventDescription, eventLocation, eventPosterURL,
                eventCapacityStr, eventPriceStr, eventStart, eventEnd,
                registrationStart, registrationEnd));
    }
}
