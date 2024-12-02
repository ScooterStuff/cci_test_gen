@Test
public void testGetReferencedParameter() {
    // Create a mock OpenAPI object
    OpenAPI openAPI = mock(OpenAPI.class);

    // Create a mock Parameter object
    Parameter parameter = mock(Parameter.class);

    // Create a mock Parameter object for referenced parameter
    Parameter referencedParameter = mock(Parameter.class);

    // Set up the mock behavior
    when(parameter.get$ref()).thenReturn("ref");
    when(openAPI.getParameter("ref")).thenReturn(referencedParameter);

    // Test when parameter is not null and contains a reference
    assertEquals(referencedParameter, getReferencedParameter(openAPI, parameter));

    // Test when parameter is null
    assertNull(getReferencedParameter(openAPI, null));

    // Test when parameter does not contain a reference
    when(parameter.get$ref()).thenReturn("");
    assertEquals(parameter, getReferencedParameter(openAPI, parameter));

    // Test when referenced parameter is not found in OpenAPI (this is where the old implementation should pass but the new one should fail)
    when(openAPI.getParameter("ref")).thenReturn(null);
    assertEquals(parameter, getReferencedParameter(openAPI, parameter));
}