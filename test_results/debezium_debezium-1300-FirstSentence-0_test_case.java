@Test
public void testKeyDifference() {
    // Test with java.time.LocalDateTime
    LocalDateTime dateTime = LocalDateTime.of(2022, 1, 1, 12, 0, 0);
    long expectedNanoOfDay = dateTime.toLocalTime().toNanoOfDay();
    assertEquals(expectedNanoOfDay, toNanoOfDay(dateTime, null));

    // Test with java.time.LocalDate
    LocalDate date = LocalDate.of(2022, 1, 1);
    expectedNanoOfDay = LocalTime.MIDNIGHT.toNanoOfDay();
    assertEquals(expectedNanoOfDay, toNanoOfDay(date, null));

    // Test with java.time.LocalTime
    LocalTime time = LocalTime.of(12, 0, 0);
    expectedNanoOfDay = time.toNanoOfDay();
    assertEquals(expectedNanoOfDay, toNanoOfDay(time, null));

    // Test with java.util.Date
    Date utilDate = new Date();
    expectedNanoOfDay = utilDate.toInstant().atZone(ZoneId.systemDefault()).toLocalTime().toNanoOfDay();
    assertEquals(expectedNanoOfDay, toNanoOfDay(utilDate, null));

    // Test with java.sql.Date
    java.sql.Date sqlDate = new java.sql.Date(System.currentTimeMillis());
    expectedNanoOfDay = sqlDate.toLocalDate().atStartOfDay(ZoneId.systemDefault()).toLocalTime().toNanoOfDay();
    assertEquals(expectedNanoOfDay, toNanoOfDay(sqlDate, null));

    // Test with java.sql.Time
    java.sql.Time sqlTime = new java.sql.Time(System.currentTimeMillis());
    expectedNanoOfDay = sqlTime.toLocalTime().toNanoOfDay();
    assertEquals(expectedNanoOfDay, toNanoOfDay(sqlTime, null));

    // Test with java.sql.Timestamp
    java.sql.Timestamp sqlTimestamp = new java.sql.Timestamp(System.currentTimeMillis());
    expectedNanoOfDay = sqlTimestamp.toLocalDateTime().toLocalTime().toNanoOfDay();
    assertEquals(expectedNanoOfDay, toNanoOfDay(sqlTimestamp, null));

    // Test with Duration, which should fail in the new implementation
    Duration duration = Duration.ofHours(12);
    try {
        toNanoOfDay(duration, false);
        fail("Expected IllegalArgumentException for Duration input");
    } catch (IllegalArgumentException e) {
        // Expected exception
    }
}