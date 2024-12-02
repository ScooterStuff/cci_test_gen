@Test
public void testKeyDifference() {
    // Mocking the StopwatchSource
    StopwatchSource<L> stopwatchSource = Mockito.mock(StopwatchSource.class);
    L location = Mockito.mock(L.class);
    Monitor monitor = Mockito.mock(Monitor.class);

    // When the location is monitored, return true and a mock Monitor
    Mockito.when(stopwatchSource.isMonitored(location)).thenReturn(true);
    Mockito.when(stopwatchSource.getMonitor(location)).thenReturn(monitor);

    // When the monitor is started, return a mock Split
    Split split = Mockito.mock(Split.class);
    Mockito.when(monitor.start()).thenReturn(split);

    // Create the object to test
    MyClass myClass = new MyClass(stopwatchSource);

    // Test when the location is monitored
    Assert.assertEquals(split, myClass.start(location));

    // When the location is not monitored, return false
    Mockito.when(stopwatchSource.isMonitored(location)).thenReturn(false);

    // Test when the location is not monitored
    Assert.assertNull(myClass.start(location));
}