@Test
public void testHasParams() {
    // Setup
    Site site = new Site();
    site.setCookieParams(Collections.singletonMap("cookie", "value"));
    site.setUrlParams(Collections.emptyMap());
    site.setFormParams(Collections.emptyMap());

    // Execute
    boolean result = site.hasParams();

    // Verify
    assertTrue(result);

    // Setup for edge case
    site.setCookieParams(Collections.emptyMap());
    site.setUrlParams(Collections.emptyMap());
    site.setFormParams(Collections.emptyMap());
    site.setHeaderParams(Collections.singletonMap("header", "value"));

    // Execute for edge case
    result = site.hasParams();

    // Verify for edge case
    assertFalse(result);
}