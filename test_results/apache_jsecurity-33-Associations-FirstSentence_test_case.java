@Test
public void testKeyDifference() {
    // Create a mock session
    Session mockSession = Mockito.mock(Session.class);

    // Define the behavior of the mock session
    Mockito.when(mockSession.getTimeout()).thenReturn(5000L);

    // Create an instance of the class that contains the method to be tested
    YourClass yourClass = new YourClass();

    // Set the global session timeout to a different value
    yourClass.setGlobalSessionTimeout(3000);

    // Call the method with the old behavior
    int oldTimeout = yourClass.getTimeout(mockSession);

    // Assert that the old behavior returns the global session timeout
    Assert.assertEquals(3000, oldTimeout);

    // Call the method with the new behavior
    long newTimeout = yourClass.getTimeout(mockSession);

    // Assert that the new behavior returns the session's timeout
    // This should fail if the new implementation is used
    Assert.assertEquals(3000, newTimeout);
}