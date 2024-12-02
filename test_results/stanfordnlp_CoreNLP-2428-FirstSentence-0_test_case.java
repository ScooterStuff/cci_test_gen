@Test
public void testMostCommonElementSelection() {
    // Create a mock sentence
    Sentence sentence = mock(Sentence.class);
    when(sentence.governors()).thenReturn(Arrays.asList(Optional.of(1), Optional.of(2), Optional.of(3), Optional.empty()));

    // Create a mock function
    Function<Sentence, List<String>> selector = mock(Function.class);
    when(selector.apply(any(Sentence.class))).thenReturn(Arrays.asList("word1", "word2", "word3", "word4"));

    // Create an instance of the class containing the old method
    MyClass myClass = new MyClass();

    // Test with valid range
    List<String> result = myClass.dependencyPathBetween(1, 3, selector);
    assertEquals(Arrays.asList("word2", "<-dep-", "word3"), result);

    // Test with start and end being the same
    result = myClass.dependencyPathBetween(2, 2, selector);
    assertEquals(Arrays.asList("word2"), result);

    // Test with invalid range (start > end)
    result = myClass.dependencyPathBetween(3, 1, selector);
    assertEquals(Collections.EMPTY_LIST, result);

    // Test with start or end out of bounds
    result = myClass.dependencyPathBetween(-1, 3, selector);
    assertEquals(Collections.EMPTY_LIST, result);

    result = myClass.dependencyPathBetween(1, 4, selector);
    assertEquals(Collections.EMPTY_LIST, result);
}