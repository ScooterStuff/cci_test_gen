@Test
public void testBackupSession() {
    // Create a mock session
    MemcachedBackupSession session = mock(MemcachedBackupSession.class);
    when(session.getId()).thenReturn("testSession");

    // Create a mock BackupSessionTask
    BackupSessionTask task = mock(BackupSessionTask.class);
    when(task.backupSession()).thenReturn(new BackupResult());

    // Create a mock logger
    Logger log = mock(Logger.class);
    when(log.isInfoEnabled()).thenReturn(true);

    // Create the object to test and inject the mock dependencies
    SessionBackupService service = new SessionBackupService();
    service.setLog(log);
    service.setBackupSessionTask(task);

    // Call the method to test
    BackupResult result = service.backupSession(session);

    // Verify that the session was stored in memcached
    verify(task).backupSession();

    // Verify that the logger was called with the expected message
    verify(log).debug("Trying to store session in memcached: " + session.getId());

    // Assert that the result is not null
    assertNotNull(result);
}