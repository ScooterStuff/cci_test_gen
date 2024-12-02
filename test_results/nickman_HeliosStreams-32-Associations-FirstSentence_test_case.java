@Test
public void testKeyDifference() {
    // Initialize the object with a count
    YourClass yourObject = new YourClass();
    yourObject.count = 10;

    // Call the reset method
    long priorCount = yourObject.reset();

    // Assert that the count was reset to zero
    assertEquals(0, yourObject.count);

    // Assert that the prior count was returned
    assertEquals(10, priorCount);

    // Assert that the time window was not changed
    assertNull(yourObject.TIME_WINDOW_UPDATER);

    // Call the reset method with a new start ms and new count
    NVP<Long, Double> result = yourObject.reset(1000, 20);

    // Assert that the count was not reset to zero
    assertNotEquals(0, yourObject.count);

    // Assert that the time window was changed
    assertNotNull(yourObject.TIME_WINDOW_UPDATER);
}