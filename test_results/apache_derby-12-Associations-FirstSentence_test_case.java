@Test
public void testGetSourceTableName() {
    // Create a mock object for the source
    Source mockSource = mock(Source.class);
    when(mockSource.getTableName()).thenReturn("SourceTable");

    // Create a mock object for the tableName
    TableName mockTableName = mock(TableName.class);
    when(mockTableName.getTableName()).thenReturn("TableName");

    // Create an instance of the class under test and set the source and tableName
    MyClass myClass = new MyClass();
    myClass.setSource(mockSource);
    myClass.setTableName(mockTableName);

    // Test the getSourceTableName method
    String result = myClass.getSourceTableName();

    // Verify that the result is the name of the tableName, not the source
    assertEquals("TableName", result);

    // Now set the tableName to null and test again
    myClass.setTableName(null);
    result = myClass.getSourceTableName();

    // Verify that the result is the name of the source
    assertEquals("SourceTable", result);

    // Finally, set the source to null and test again
    myClass.setSource(null);
    result = myClass.getSourceTableName();

    // Verify that the result is null
    assertNull(result);
}