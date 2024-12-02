@Test
public void testKeyDifference() {
    // Setup
    RequestLog current = RLOG.get();
    record(new Durations(current.aggregateDurations()));
    record(new Threads(current.threadIds));
    String expectedJson = "";
    try {
        expectedJson = current.mapper.writeValueAsString(current.info);
    } catch (JsonProcessingException jpe) {
        String msg = String.format("Exporting mega log line with id: '%s' to JSON failed.", current.logId);
        LOG.warn(msg, jpe);
        expectedJson = msg;
    }

    // Execute
    String actualJson = export();

    // Verify
    assertEquals(expectedJson, actualJson);

    // Edge case: test with a log that causes JsonProcessingException
    RequestLog edgeCaseLog = new RequestLog();
    edgeCaseLog.info = new LogInfo(); // set up this object in a way that it will cause JsonProcessingException
    RLOG.set(edgeCaseLog);
    String expectedErrorMsg = String.format("Exporting mega log line with id: '%s' to JSON failed.", edgeCaseLog.logId);
    String actualErrorMsg = export();

    // Verify
    assertEquals(expectedErrorMsg, actualErrorMsg);
}