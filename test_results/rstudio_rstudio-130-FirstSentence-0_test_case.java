@Test
public void testKeyDifference() {
    // Setup
    int terminalCount = 5;
    TerminalManager terminalManager = new TerminalManager(terminalCount);

    // Test for terminalClosing > 0
    int terminalClosing = 3;
    int expectedTerminalToShow = terminalClosing - 1;
    assertEquals(expectedTerminalToShow, terminalManager.terminalToShowWhenClosing(terminalClosing));

    // Test for terminalClosing + 1 < terminalCount
    terminalClosing = 0;
    expectedTerminalToShow = terminalClosing + 1;
    assertEquals(expectedTerminalToShow, terminalManager.terminalToShowWhenClosing(terminalClosing));

    // Test for terminalClosing + 1 >= terminalCount
    terminalClosing = terminalCount - 1;
    expectedTerminalToShow = -1;
    assertEquals(expectedTerminalToShow, terminalManager.terminalToShowWhenClosing(terminalClosing));

    // Edge case: Test for terminalClosing < 0, which is not handled in the old code
    terminalClosing = -1;
    try {
        terminalManager.terminalToShowWhenClosing(terminalClosing);
        fail("Expected an IndexOutOfBoundsException to be thrown");
    } catch (IndexOutOfBoundsException e) {
        // Test passed
    }
}