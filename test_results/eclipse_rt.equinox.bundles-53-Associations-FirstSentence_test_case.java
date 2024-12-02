@Test
public void testDeprocess() {
    // Assuming LRM, LRE, PDF are defined as constants somewhere
    final char LRM = '\u200E';
    final char LRE = '\u202A';
    final char PDF = '\u202C';

    // Test case 1: Null string
    assertNull(deprocess(null));

    // Test case 2: Empty string
    assertEquals("", deprocess(""));

    // Test case 3: String with one character
    assertEquals("a", deprocess("a"));

    // Test case 4: String with no formatting characters
    assertEquals("abc", deprocess("abc"));

    // Test case 5: String with formatting characters
    assertEquals("abc", deprocess("a" + LRM + "b" + LRE + "c" + PDF));

    // Test case 6: String with only formatting characters
    assertEquals("", deprocess(String.valueOf(LRM) + LRE + PDF));

    // Test case 7: String with formatting characters at the beginning and end
    assertEquals("abc", deprocess(LRM + "a" + LRE + "b" + PDF + "c" + LRM));

    // Test case 8: String with formatting characters in the middle
    assertEquals("abc", deprocess("a" + LRM + "b" + LRE + "c" + PDF));

    // Test case 9: String with multiple consecutive formatting characters
    assertEquals("abc", deprocess("a" + LRM + LRM + "b" + LRE + LRE + "c" + PDF + PDF));

    // Test case 10: String with all formatting characters
    assertEquals("", deprocess(String.valueOf(LRM) + LRM + LRE + LRE + PDF + PDF));
}