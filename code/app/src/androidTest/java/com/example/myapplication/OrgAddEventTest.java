package com.example.myapplication;

import com.example.myapplication.view.organization.OrgAddEvent;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

public class OrgAddEventTest {
    private OrgAddEvent orgAddEvent;

    @Before
    public void setUp() {
        orgAddEvent = new OrgAddEvent();
    }

    @Test
    public void testIsPositiveInteger_validNumber() {
        assertTrue(orgAddEvent.isPositiveInteger("10"));
    }

    @Test
    public void testIsPositiveInteger_negativeNumber() {
        assertFalse(orgAddEvent.isPositiveInteger("-5"));
    }

    @Test
    public void testIsPositiveInteger_invalidInput() {
        assertFalse(orgAddEvent.isPositiveInteger("abc"));
    }

    @Test
    public void testIsPositiveDouble_validNumber() {
        assertTrue(orgAddEvent.isPositiveDouble("10.5"));
    }

    @Test
    public void testIsPositiveDouble_invalidInput() {
        assertFalse(orgAddEvent.isPositiveDouble("abc"));
    }

    @Test
    public void testParseDate_validDate() {
        String validDate = "2024-12-01 10:30";
        assertNotNull(orgAddEvent.parseDate(validDate));
    }

    @Test
    public void testParseDate_invalidDate() {
        String invalidDate = "invalid-date";
        assertNull(orgAddEvent.parseDate(invalidDate));
    }
}
