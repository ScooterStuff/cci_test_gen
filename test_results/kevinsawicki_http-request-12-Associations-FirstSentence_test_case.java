@Test
public void testAppendParametersToUrl() {
    // Test case for normal scenario
    Map<String, Object> params = new HashMap<>();
    params.put("key1", "value1");
    params.put("key2", "value2");
    String url = "http://example.com";
    String expected = "http://example.com/?key1=value1&key2=value2";
    assertEquals(expected, append(url, params));

    // Test case for URL without trailing slash
    url = "http://example.com";
    expected = "http://example.com/?key1=value1&key2=value2";
    assertEquals(expected, append(url, params));

    // Test case for URL with trailing slash
    url = "http://example.com/";
    expected = "http://example.com/?key1=value1&key2=value2";
    assertEquals(expected, append(url, params));

    // Test case for empty parameters
    params.clear();
    url = "http://example.com";
    expected = "http://example.com";
    assertEquals(expected, append(url, params));

    // Test case for null parameters
    params = null;
    url = "http://example.com";
    expected = "http://example.com";
    assertEquals(expected, append(url, params));

    // Test case for URL with existing parameters
    params = new HashMap<>();
    params.put("key1", "value1");
    params.put("key2", "value2");
    url = "http://example.com?existingKey=existingValue";
    expected = "http://example.com?existingKey=existingValue&key1=value1&key2=value2";
    assertEquals(expected, append(url, params));
}