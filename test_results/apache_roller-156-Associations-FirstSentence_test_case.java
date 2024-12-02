@Test
public void testKeyDifference() {
    // Test case implementation
    try {
        Roller roller = Roller.getRoller();
        assertNotNull(roller);
    } catch (IllegalStateException e) {
        fail("Roller Weblogger should have been bootstrapped");
    }

    try {
        Weblogger weblogger = Weblogger.getRoller();
        fail("Weblogger should not have been bootstrapped yet");
    } catch (IllegalStateException e) {
        // Expected exception
    }
}