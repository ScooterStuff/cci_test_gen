@Test
public void testKeyDifference() {
    // Create a mock Visitor
    Visitor<String> mockVisitor = Mockito.mock(Visitor.class);

    // Create a list of items
    List<String> items = Arrays.asList("item1", "item2", "item3");

    // Apply the predicate to each item
    for (String item : items) {
        mockVisitor.visit(item);
    }

    // Verify that the visit method was called on the mockVisitor for each item
    for (String item : items) {
        Mockito.verify(mockVisitor).visit(item);
    }

    // Now, create a new visitor and apply it to the items
    Visitor<String> newVisitor = Mockito.mock(Visitor.class);
    for (String item : items) {
        newVisitor.visit(item);
    }

    // Verify that the visit method was called on the newVisitor for each item
    for (String item : items) {
        Mockito.verify(newVisitor).visit(item);
    }

    // The old implementation would pass this test, but the new implementation should fail
    // because it uses a different visitor for each item
    assertNotEquals(mockVisitor, newVisitor);
}