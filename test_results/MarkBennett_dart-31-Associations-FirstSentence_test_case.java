@Test
public void testKeyDifference() {
    // Create an instance of the class containing the getKey method
    MyClass instance = new MyClass();

    // Set the key to a specific StringLiteral
    StringLiteral expectedKey = new StringLiteral("expectedKey");
    instance.setKey(expectedKey);

    // Call the getKey method and check if it returns the expected StringLiteral
    StringLiteral actualKey = instance.getKey();

    // Assert that the actual key is the same as the expected key
    assertEquals(expectedKey, actualKey);

    // Now, let's test an edge case where the key is not a StringLiteral but an Expression
    // This should pass in the new implementation but fail in the old one
    Expression expressionKey = new Expression("expressionKey");
    instance.setKey(expressionKey);

    // Call the getKey method and check if it throws a ClassCastException
    try {
        actualKey = instance.getKey();
        fail("Expected a ClassCastException to be thrown");
    } catch (ClassCastException e) {
        // Test passed, ClassCastException was thrown as expected
    }
}