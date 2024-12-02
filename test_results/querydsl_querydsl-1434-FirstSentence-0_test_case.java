@Test
public void testKeyDifference() {
    // Create an instance of the class containing the endsWith method
    MyClass myClass = new MyClass();

    // Test case 1: When caseSensitive is true, the old implementation should pass, but the new one should fail
    String left1 = "HelloWorld";
    String right1 = "WORLD";
    EBoolean result1 = myClass.endsWith(new Expr<>(left1), new Expr<>(right1), true);
    assertFalse(result1.getValue());

    // Test case 2: When caseSensitive is false, the old implementation should pass, but the new one should fail
    String left2 = "HelloWorld";
    String right2 = "WORLD";
    EBoolean result2 = myClass.endsWith(new Expr<>(left2), new Expr<>(right2), false);
    assertTrue(result2.getValue());

    // Test case 3: When caseSensitive is true, the old implementation should pass, and the new one should also pass
    String left3 = "HelloWorld";
    String right3 = "World";
    EBoolean result3 = myClass.endsWith(new Expr<>(left3), new Expr<>(right3), true);
    assertTrue(result3.getValue());
}