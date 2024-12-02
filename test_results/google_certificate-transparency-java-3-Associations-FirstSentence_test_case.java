@Test
public void testParseMerkleTreeLeaf() {
    // Create a mock InputStream
    InputStream mockInputStream = mock(InputStream.class);

    // Set up the mock to return specific values for version and leafType
    when(mockInputStream.read(any(byte[].class), anyInt(), eq(CTConstants.VERSION_LENGTH))).thenReturn(Ct.Version.V1.getNumber());
    when(mockInputStream.read(any(byte[].class), anyInt(), eq(1))).thenReturn(Ct.MerkleLeafType.TIMESTAMPED_ENTRY_VALUE);

    // Call the method under test
    Ct.MerkleTreeLeaf result = parseMerkleTreeLeaf(mockInputStream);

    // Assert that the returned object is not null
    assertNotNull(result);

    // Assert that the version and type are as expected
    assertEquals(Ct.Version.V1, result.getVersion());
    assertEquals(Ct.MerkleLeafType.TIMESTAMPED_ENTRY_VALUE, result.getType());

    // Assert that the timestamped entry is not null
    assertNotNull(result.getTimestampedEntry());

    // Now, set up the mock to return a version and leafType that should cause exceptions
    when(mockInputStream.read(any(byte[].class), anyInt(), eq(CTConstants.VERSION_LENGTH))).thenReturn(-1);
    when(mockInputStream.read(any(byte[].class), anyInt(), eq(1))).thenReturn(-1);

    // Call the method under test and assert that it throws an exception
    assertThrows(SerializationException.class, () -> parseMerkleTreeLeaf(mockInputStream));
}