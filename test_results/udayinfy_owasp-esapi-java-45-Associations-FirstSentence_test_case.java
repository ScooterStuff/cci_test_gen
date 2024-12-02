@Test
public void testKeyDifference() {
    // Test case for union of two character arrays
    char[] c1 = {'a', 'b', 'c'};
    char[] c2 = {'c', 'd', 'e'};
    char[] expected = {'a', 'b', 'c', 'd', 'e'};
    
    char[] result = union(c1, c2);
    Arrays.sort(result);
    
    assertArrayEquals(expected, result);
    
    // Edge case: union of two character arrays where one is empty
    char[] c3 = {};
    char[] expected2 = {'a', 'b', 'c'};
    
    char[] result2 = union(c1, c3);
    Arrays.sort(result2);
    
    assertArrayEquals(expected2, result2);
    
    // Edge case: union of two identical character arrays
    char[] expected3 = {'a', 'b', 'c'};
    
    char[] result3 = union(c1, c1);
    Arrays.sort(result3);
    
    assertArrayEquals(expected3, result3);
    
    // Edge case: union of two character arrays where one contains characters not present in the other
    char[] c4 = {'x', 'y', 'z'};
    char[] expected4 = {'a', 'b', 'c', 'x', 'y', 'z'};
    
    char[] result4 = union(c1, c4);
    Arrays.sort(result4);
    
    assertArrayEquals(expected4, result4);
}