@Test
public void testKeyDifference() {
    ChatSection chatSection = new ChatSection();
    chatSection.setArg("true");
    assertTrue(chatSection.isBoolean());
    chatSection.setArg("false");
    assertTrue(chatSection.isBoolean());
    chatSection.setArg("notboolean");
    assertFalse(chatSection.isBoolean());
    chatSection.setArg(null);
    assertFalse(chatSection.isBoolean());
    chatSection.setRaw("true");
    assertFalse(chatSection.isBoolean());
    chatSection.setRaw("false");
    assertFalse(chatSection.isBoolean());
}