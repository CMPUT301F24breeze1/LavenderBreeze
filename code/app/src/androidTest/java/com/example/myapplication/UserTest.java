// From chatgpt, openai, "write a java implementation of UserTest Class", 2024-10-25
package com.example.myapplication;

import static androidx.core.content.ContentProviderCompat.requireContext;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.myapplication.controller.UserController;
import com.example.myapplication.model.User;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class UserTest {

    private UserController user;

    /**
     * Set up the test environment
     */
    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        user = new UserController(context, new UserController.OnUserDataLoadedListener() {
            @Override
            public void onUserDataLoaded() {
                // Notify completion of async operation if needed
            }
        });
    }

    /**
     * Test the setName method with valid
     */
    @Test
    public void testSetNameValidInput() {
        user.setUserName("John Doe");
        assertEquals("John Doe", user.getUserName());
    }

    /**
     * Test the setName method with invalid input
     */
    @Test
    public void testSetNameInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> {
            user.setUserName("");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            user.setUserName(null);
        });
    }

    /**
     * Test the setEmail method with valid input
     */
    @Test
    public void testSetEmailValidInput() {
        user.setUserEmail("john@example.com");
        assertEquals("john@example.com", user.getUserEmail());
    }

    /**
     * Test the setEmail method with invalid input
     */
    @Test
    public void testSetEmailInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> {
            user.setUserEmail("invalidEmail");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            user.setUserEmail(null);
        });
    }

    /**
     * Test the setPhoneNumber method with valid input
     */
    @Test
    public void testSetPhoneNumberValidInput() {
        user.setUserPhoneNumber("1234567890");
        assertEquals("1234567890", user.getUserPhoneNumber());
    }

    /**
     * Test the setPhoneNumber method with invalid input
     */
    @Test
    public void testSetPhoneNumberInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> {
            user.setUserPhoneNumber("12345"); // Too short
        });
        assertThrows(IllegalArgumentException.class, () -> {
            user.setUserPhoneNumber(null);
        });
    }

    /**
     * Test the setIsEntrant method with valid input
     */
    @Test
    public void testSetIsEntrantValidInput() {
        user.setUserIsEntrant(true);
        assertEquals(true, user.getUserIsEntrant());
    }

    /**
     * Test the setIsEntrant method with invalid input
     */
    @Test
    public void testSetIsEntrantInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> {
            user.setUserIsEntrant(null);
        });

    }
    /**
     * Test the setIsOrganizer method with valid input
     */
    @Test
    public void testSetIsOrganizerValidInput() {
        user.setUserIsOrganizer(true);
        assertEquals(true, user.getUserIsOrganizer());
    }
    /**
     * Test the setIsOrganizer method with invalid input
     */
    @Test
    public void testSetIsOrganizerInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> {
            user.setUserIsOrganizer(null);
        });
    }
    /**
     * Test the setIsAdmin method with valid input
     */
    @Test
    public void testSetIsAdminValidInput() {
        user.setUserIsAdmin(true);
        assertEquals(true, user.getUserIsAdmin());
    }
    /**
     * Test the setIsAdmin method with invalid input
     */
    @Test
    public void testSetIsAdminInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> {
            user.setUserIsAdmin(null);
        });
    }
    /**
     * Test the setIsFacility method with valid input
     */
    @Test
    public void testSetIsFacilityValidInput() {
        user.setUserIsFacility(true);
        assertEquals(true, user.getUserIsFacility());
    }
    /**
     * Test the setIsFacility method with invalid input
    */
    @Test
    public void testSetIsFacilityInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> {
            user.setUserIsFacility(null);
        });
    }

    /**
     * Test the addRequestedEvent method
     */
    @Test
    public void testAddRequestedEvent() {
        String eventId = "event123";
        user.addRequestedEvent(eventId);
        assertTrue(user.getUserRequestedEvents().contains(eventId));
    }

    /**
     * Test the removeRequestedEvent method
     */
    @Test
    public void testRemoveRequestedEvent() {
        String eventId = "event123";
        user.addRequestedEvent(eventId);
        user.removeRequestedEvent(eventId);
        assertFalse(user.getUserRequestedEvents().contains(eventId));
    }

    /**
     * Test the addSelectedEvent method
     */
    @Test
    public void testAddSelectedEvent() {
        String eventId = "event456";
        user.addSelectedEvent(eventId);
        assertTrue(user.getUserSelectedEvents().contains(eventId));
    }

    /**
     * Test the removeSelectedEvent method
     */
    @Test
    public void testRemoveSelectedEvent() {
        String eventId = "event456";
        user.addSelectedEvent(eventId);
        user.removeSelectedEvent(eventId);
        assertFalse(user.getUserSelectedEvents().contains(eventId));
    }

    /**
     * Test the addCancelledEvent method
     */
    @Test
    public void testAddCancelledEvent() {
        String eventId = "event789";
        user.addCancelledEvent(eventId);
        assertTrue(user.getUserCancelledEvents().contains(eventId));
    }

    /**
     * Test the removeCancelledEvent method
     */
    @Test
    public void testRemoveCancelledEvent() {
        String eventId = "event789";
        user.addCancelledEvent(eventId);
        user.removeCancelledEvent(eventId);
        assertFalse(user.getUserCancelledEvents().contains(eventId));
    }

    /**
     * Test the addAcceptedEvent method
     */
    @Test
    public void testAddAcceptedEvent() {
        String eventId = "event101";
        user.addAcceptedEvent(eventId);
        assertTrue(user.getUserAcceptedEvents().contains(eventId));
    }

    /**
     * Test the removeAcceptedEvent method
     */
    @Test
    public void testRemoveAcceptedEvent() {
        String eventId = "event101";
        user.addAcceptedEvent(eventId);
        user.removeAcceptedEvent(eventId);
        assertFalse(user.getUserAcceptedEvents().contains(eventId));
    }
}
