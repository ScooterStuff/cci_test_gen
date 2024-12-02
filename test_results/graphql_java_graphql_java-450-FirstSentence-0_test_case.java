```java
@Test
public void testKeyDifference() {
    // Arrange
    QueryVisitor queryVisitor = mock(QueryVisitor.class);
    Node node = mock(Node.class);
    when(node.accept(any(), any())).thenReturn(TraversalControl.CONTINUE);
    List<Node> roots = new ArrayList<>();
    roots.add(node);

    // Act
    Node result = null;
    try {
        result = transform(queryVisitor);
    } catch (IllegalArgumentException e) {
        fail("Single root node is required for transformation.");
    }

    // Assert
    assertNotNull(result);
    verify(node, times(1)).accept(any(), any());
    try {
        Field transformerField = result.getClass().getDeclaredField("transformer");
        transformerField.setAccessible(true);
        Object transformer = transformerField.get(result);
        assertTrue(transformer instanceof AstTransformer);
    } catch (NoSuchFieldException | IllegalAccessException e) {
        fail("AstTransformer is expected but not found.");
    }
}
```

This test case is designed to verify the old behavior of the `transform` method, which is expected to use an instance of `AstTransformer` for the transformation process. The test case will fail if the transformation is performed using a different class (like `TreeTransformer` in the new implementation).