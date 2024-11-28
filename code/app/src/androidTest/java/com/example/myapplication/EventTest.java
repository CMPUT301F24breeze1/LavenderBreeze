package com.example.myapplication;
import com.example.myapplication.model.Event;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class EventTest {

    private Event event;

    /**
     * Set up the test environment
     */
    @Before
    public void setUp() {
        event = new Event("testEventId");
    }
    
    /**
     * Test for getEventId
     */
    @Test
    public void testGetEventId() {
        assertEquals("testEventId", event.getEventId());
    }

    /**
     * Test for getEventName and setEventName
     */
    @Test
    public void testGetAndSetEventName() {
        event.setEventName("Test Event");
        assertEquals("Test Event", event.getEventName());
    }

    /**
     *  Test for getEventDescription and setEventDescription
     */
    @Test
    public void testGetAndSetEventDescription() {
        event.setEventDescription("Test Description");
        assertEquals("Test Description", event.getEventDescription());
    }

    /**
     * Test for getEventStart and setEventStart
     */
    @Test
    public void testGetAndSetEventStart() {
        Date date = new Date();
        event.setEventStart(date);
        assertEquals(date, event.getEventStart());
    }

    /**
     * Test for getEventEnd and setEventEnd
     */
    @Test
    public void testGetAndSetEventEnd() {
        Date date = new Date();
        event.setEventEnd(date);
        assertEquals(date, event.getEventEnd());
    }

    /**
     * Test for getRegistrationStart and setRegistrationStart
     */
    @Test
    public void testGetAndSetRegistrationStart() {
        Date date = new Date();
        event.setRegistrationStart(date);
        assertEquals(date, event.getRegistrationStart());
    }

    /**
     * Test for getRegistrationEnd and setRegistrationEnd
     */
    @Test
    public void testGetAndSetRegistrationEnd() {
        Date date = new Date();
        event.setRegistrationEnd(date);
        assertEquals(date, event.getRegistrationEnd());
    }

    /**
     * Test for getLocation and setLocation
     */
    @Test
    public void testGetAndSetLocation() {
        event.setLocation("Test Location");
        assertEquals("Test Location", event.getLocation());
    }

    /**
     * Test for getCapacity and setCapacity
     */
    @Test
    public void testGetAndSetCapacity() {
        event.setCapacity(150);
        assertEquals(150, event.getCapacity());
    }

    /**
     * Test for getPrice and setPrice
     */
    @Test
    public void testGetAndSetPrice() {
        event.setPrice(25);
        assertEquals(25, event.getPrice());
    }

    /**
     * Test for getPosterUrl and setPosterUrl
     */
    @Test
    public void testGetAndSetPosterUrl() {
        event.setPosterUrl("Test URL");
        assertEquals("Test URL", event.getPosterUrl());
    }

    /**
     * Test for getQrCodeHash and setQrCodeHash
     */
    @Test
    public void testGetAndSetQrCodeHash() {
        event.setQrCodeHash("Test QR code");
        assertEquals("Test QR code", event.getQrCodeHash());
    }

    /**
     * Test for getOrganizerId and setOrganizerId
     */
    @Test
    public void testGetAndSetOrganizerId() {
        event.setOrganizerId("12345");
        assertEquals("12345", event.getOrganizerId());
    }

    /**
     * Test for getWaitlist and setWaitlist
     */
    @Test
    public void testGetAndSetWaitlist() {
        List<String> waitlist = new ArrayList<>();
        waitlist.add("Test entrant");
        event.setWaitlist(waitlist);
        assertEquals(waitlist, event.getWaitlist());
    }

    /**
     * Test for getSelectedEntrants and setSelectedEntrants
     */
    @Test
    public void testGetAndSetSelectedEntrants() {
        List<String> selected = new ArrayList<>();
        selected.add("Test entrant");
        event.setSelectedEntrants(selected);
        assertEquals(selected, event.getSelectedEntrants());
    }

    /**
     * Test for getAcceptedEntrants and setAcceptedEntrants
     */
    @Test
    public void testGetAndSetAcceptedEntrants() {
        List<String> accepted = new ArrayList<>();
        accepted.add("Test entrant");
        event.setSelectedEntrants(accepted);
        assertEquals(accepted, event.getSelectedEntrants());
    }

    /**
     * Test for getDeclinedEntrants and setDeclinedEntrants
     */
    @Test
    public void testGetAndSetDeclinedEntrants() {
        List<String> declined = new ArrayList<>();
        declined.add("Test entrant");
        event.setCancelledEntrants(declined);
        assertEquals(declined, event.getCancelledEntrants());
    }

    /**
     * Test for addToWaitlist
     */
    @Test
    public void testAddToWaitlist() {
        String userId = "888";
        event.addToWaitlist(userId);
        assertTrue((event.getWaitlist()).contains(userId));
    }

    /**
     * Test for removeFromWaitlist
     */
    @Test
    public void testRemoveFromWaitlist() {
        event.addToWaitlist("777");
        event.addToWaitlist("888");
        event.removeFromWaitlist("777");
        assertFalse(event.getWaitlist().contains("777"));
    }

    /**
     * Test for addToSelectedlist
     */
    @Test
    public void testAddToSelectedlist() {
        String userId = "888";
        event.addToSelectedlist(userId);
        assertTrue((event.getSelectedEntrants()).contains(userId));
    }

    /**
     * Test for removeFromSelectedlist
     */
    @Test
    public void testRemoveFromSelectedlist() {
        String userId = "888";
        event.addToSelectedlist(userId);
        event.removeFromSelectedlist(userId);
        assertFalse(event.getSelectedEntrants().contains(userId));
    }

    /**
     * Test for addToAcceptedlist
     */
    @Test
    public void testAddToAcceptedlist() {
        String userId = "888";
        event.addToAcceptedlist(userId);
        assertTrue((event.getAcceptedEntrants()).contains(userId));
    }

    /**
     * Test for removeFromAcceptedlist
     */
    @Test
    public void testRemoveFromAcceptedlist() {
        String userId = "888";
        event.addToAcceptedlist(userId);
        event.removeFromAcceptedlist(userId);
        assertFalse(event.getAcceptedEntrants().contains(userId));
    }

    /**
     * Test for addToDeclinedlist
     */
    @Test
    public void testAddToDeclinedlist() {
        String userId = "888";
        event.addToDeclinedlist(userId);
        assertTrue((event.getCancelledEntrants()).contains(userId));
    }

    /**
     * Test for removeFromDeclinedlist
     */
    @Test
    public void testRemoveFromDeclinedlist() {
        String userId = "888";
        event.addToDeclinedlist(userId);
        event.removeFromDeclinedlist(userId);
        assertFalse(event.getCancelledEntrants().contains(userId));
    }

}
