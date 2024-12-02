@Test
public void testKeyDifference() {
    // Create an instance of the class containing the method
    YourClass instance = new YourClass();

    // Call the method and store the result
    Object result = instance.createReportsTask();

    // Check that the result is of the expected type
    assertTrue(result instanceof ReportsTask);

    // Check that the url is correctly set in the ReportsTask object
    assertEquals(url, ((ReportsTask) result).getUrl());

    // Check that the result is not of the new type
    assertFalse(result instanceof IncidentsTask);
}