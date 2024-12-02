@Test
public void testKeyDifference() {
    // Test case implementation
    Expr<String> left = operationFactory.createString("HelloWorld");
    Expr<String> right = operationFactory.createString("HELLO");
    boolean caseSensitive = false;

    // Old behavior: should pass as it ignores case
    EBoolean result = startsWith(left, right, caseSensitive);
    assertTrue(result.getValue());

    // New behavior: should fail as it does not ignore case
    EBoolean resultNew = startsWith(left, right);
    assertFalse(resultNew.getValue());

    // Edge case: when both strings are empty
    Expr<String> emptyLeft = operationFactory.createString("");
    Expr<String> emptyRight = operationFactory.createString("");
    EBoolean resultEmpty = startsWith(emptyLeft, emptyRight, caseSensitive);
    assertTrue(resultEmpty.getValue());

    // New behavior: should pass as both strings are empty
    EBoolean resultNewEmpty = startsWith(emptyLeft, emptyRight);
    assertTrue(resultNewEmpty.getValue());
}