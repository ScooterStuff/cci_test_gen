@Test
public void testKeyDifference() {
    // Create a MetricsRegistry instance
    MetricsRegistry registry = new MetricsRegistry();

    // Create a TimerContext instance
    TimerContext context;

    // Get the timer with the given name
    Timer timer = registry.getTimer("testTimer");

    // Start the timer
    context = timer.time();

    // Simulate some work
    try {
        Thread.sleep(1000);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }

    // Stop the timer
    context.stop();

    // Assert that the timer is registered under the given name
    assertNotNull(registry.getTimers().get("testTimer"));

    // Assert that the timer measures elapsed time in milliseconds
    assertEquals(1000, timer.getSnapshot().getMax(), 50);

    // Assert that the timer measures invocations per second
    assertEquals(1, timer.getOneMinuteRate(), 0.1);

    // Edge case: Try to get a timer with a null name
    try {
        timer = registry.getTimer(null);
        fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
        // Expected exception
    }

    // Edge case: Try to get a timer with an empty name
    try {
        timer = registry.getTimer("");
        fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
        // Expected exception
    }
}