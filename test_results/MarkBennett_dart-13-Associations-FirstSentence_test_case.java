@Test
public void testGetScriptSourcePath() {
    // Create a XmlTagNode with a source attribute
    XmlTagNode node = new XmlTagNode();
    XmlAttributeNode attribute = new XmlAttributeNode();
    attribute.setName(new Lexeme(SRC));
    attribute.setText("testPath");
    node.addAttribute(attribute);

    // Test the old behavior
    String result = getScriptSourcePath(node);
    assertEquals("testPath", result);

    // Test edge case where the source attribute exists but has no text
    attribute.setText("");
    result = getScriptSourcePath(node);
    assertNull(result);

    // Test edge case where the source attribute does not exist
    node = new XmlTagNode();
    result = getScriptSourcePath(node);
    assertNull(result);
}