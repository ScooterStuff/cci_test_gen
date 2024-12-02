@Test
public void testIsBuffered() {
    // Setup
    MyClass myClass = new MyClass();
    myClass.buffered = true;

    // Execute
    boolean result = myClass.isBuffered();

    // Assert
    assertTrue(result);

    // Change state
    myClass.buffered = false;

    // Execute
    result = myClass.isBuffered();

    // Assert
    assertFalse(result);
}