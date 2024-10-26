// From chatgpt, openai, "write a java implementation of UserTest Class", 2024-10-25
package com.example.myapplication;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import android.content.Context;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class UserTest {

    private User user;

    /**
     * Set up the test environment
     */
    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        user = new User(context);
    }

    /**
     * Test the setName method with valid
     */
    @Test
    public void testSetNameValidInput() {
        user.setName("John Doe");
        assertEquals("John Doe", user.getName());
    }

    /**
     * Test the setName method with invalid input
     */
    @Test
    public void testSetNameInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> {
            user.setName("");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            user.setName(null);
        });
    }

    /**
     * Test the setEmail method with valid input
     */
    @Test
    public void testSetEmailValidInput() {
        user.setEmail("john@example.com");
        assertEquals("john@example.com", user.getEmail());
    }

    /**
     * Test the setEmail method with invalid input
     */
    @Test
    public void testSetEmailInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> {
            user.setEmail("invalidEmail");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            user.setEmail(null);
        });
    }

    /**
     * Test the setPhoneNumber method with valid input
     */
    @Test
    public void testSetPhoneNumberValidInput() {
        user.setPhoneNumber("1234567890");
        assertEquals("1234567890", user.getPhoneNumber());
    }

    /**
     * Test the setPhoneNumber method with invalid input
     */
    @Test
    public void testSetPhoneNumberInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> {
            user.setPhoneNumber("12345"); // Too short
        });
        assertThrows(IllegalArgumentException.class, () -> {
            user.setPhoneNumber("abcdefghij"); // Non-numeric
        });
        assertThrows(IllegalArgumentException.class, () -> {
            user.setPhoneNumber(null);
        });
    }

    /**
     * Test the setIsEntrant method with valid input
     */
    @Test
    public void testSetIsEntrantValidInput() {
        user.setIsEntrant(true);
        assertEquals(true, user.getIsEntrant());
    }

    /**
     * Test the setIsEntrant method with invalid input
     */
    @Test
    public void testSetIsEntrantInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> {
            user.setIsEntrant(null);
        });

    }
    /**
     * Test the setIsOrganizer method with valid input
     */
    @Test
    public void testSetIsOrganizerValidInput() {
        user.setIsOrganizer(true);
        assertEquals(true, user.getIsOrganizer());
    }
    /**
     * Test the setIsOrganizer method with invalid input
     */
    @Test
    public void testSetIsOrganizerInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> {
            user.setIsOrganizer(null);
        });
    }
    /**
     * Test the setIsAdmin method with valid input
     */
    @Test
    public void testSetIsAdminValidInput() {
        user.setIsAdmin(true);
        assertEquals(true, user.getIsAdmin());
    }
    /**
     * Test the setIsAdmin method with invalid input
     */
    @Test
    public void testSetIsAdminInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> {
            user.setIsAdmin(null);
        });
    }
    /**
     * Test the setIsFacility method with valid input
     */
    @Test
    public void testSetIsFacilityValidInput() {
        user.setIsFacility(true);
        assertEquals(true, user.getIsFacility());
    }
    /**
     * Test the setIsFacility method with invalid input
    */
    @Test
    public void testSetIsFacilityInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> {
            user.setIsFacility(null);
        });
    }
}