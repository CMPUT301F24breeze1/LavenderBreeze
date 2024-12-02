package com.example.myapplication;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;

import static org.junit.Assert.*;
import android.content.Context;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.myapplication.model.Facility;


@RunWith(AndroidJUnit4.class)
public class FacilityTest {
    private Facility facility;

    @Before
    public void setUp() {
        // Initialize the Facility object
        facility = new Facility("Facility Name", "123 Address", "email@test.com",
                "987654321", "Organizer123");
    }

    @Test
    public void testDefaultConstructor() {
        Facility defaultFacility = new Facility();
        assertEquals("Default Name", defaultFacility.getFacilityName());
        assertEquals("Default Address", defaultFacility.getFacilityAddress());
        assertEquals("email@default.com", defaultFacility.getFacilityEmail());
        assertEquals("123456789", defaultFacility.getFacilityPhoneNumber());
    }

    @Test
    public void testParameterizedConstructor() {
        assertEquals("Facility Name", facility.getFacilityName());
        assertEquals("123 Address", facility.getFacilityAddress());
        assertEquals("email@test.com", facility.getFacilityEmail());
        assertEquals("987654321", facility.getFacilityPhoneNumber());
        assertEquals("Organizer123", facility.getOrganizerId());
    }

    @Test
    public void testSettersAndGetters() {
        // Update facility details
        facility.setFacilityName("New Facility");
        facility.setFacilityAddress("456 New Address");
        facility.setFacilityEmail("newemail@test.com");
        facility.setFacilityPhoneNumber("123456789");
        facility.setOrganizerId("NewOrganizer");
        facility.setFacilityId("Facility123");
        facility.setProfileImageUrl("https://new.image.url");
        facility.setEvents(Arrays.asList("Event1", "Event2"));

        // Validate updated values
        assertEquals("New Facility", facility.getFacilityName());
        assertEquals("456 New Address", facility.getFacilityAddress());
        assertEquals("newemail@test.com", facility.getFacilityEmail());
        assertEquals("123456789", facility.getFacilityPhoneNumber());
        assertEquals("NewOrganizer", facility.getOrganizerId());
        assertEquals("Facility123", facility.getFacilityId());
        assertEquals("https://new.image.url", facility.getProfileImageUrl());
        assertEquals(Arrays.asList("Event1", "Event2"), facility.getEvents());
    }
}
