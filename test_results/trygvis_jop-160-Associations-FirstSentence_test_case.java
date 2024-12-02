@Test
public void testKeyDifference() {
    // Test case with signature, classname and descriptor
    String signature = "com.example.Class.method(ParamType)";
    String expected = "com.example.Class";
    assertEquals(expected, getClassName(signature, true));

    // Test case with signature, no classname and descriptor
    signature = "method(ParamType)";
    expected = "";
    assertEquals(expected, getClassName(signature, true));

    // Test case with signature, classname and no descriptor
    signature = "com.example.Class.method";
    expected = "com.example.Class";
    assertEquals(expected, getClassName(signature, true));

    // Test case with signature, no classname and no descriptor
    signature = "method";
    expected = "";
    assertEquals(expected, getClassName(signature, true));

    // Test case with classname only
    signature = "com.example.Class";
    expected = "com.example.Class";
    assertEquals(expected, getClassName(signature, false));

    // Test case with no classname, no descriptor
    signature = "";
    expected = "";
    assertEquals(expected, getClassName(signature, true));

    // Test case with alternative separator
    signature = "com.example.Class#method";
    expected = "com.example.Class";
    assertEquals(expected, getClassName(signature, true));

    // Test case with alternative separator, no classname
    signature = "#method";
    expected = "";
    assertEquals(expected, getClassName(signature, true));
}