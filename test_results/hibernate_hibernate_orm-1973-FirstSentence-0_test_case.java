@Test
public void testKeyDifference() {
    // Create a mock EntityPersister
    EntityPersister persister = mock(EntityPersister.class);
    when(persister.hasCache()).thenReturn(true);

    // Create a mock EntityKey
    EntityKey entityKey = mock(EntityKey.class);
    when(entityKey.getIdentifier()).thenReturn("identifier");
    when(entityKey.getEntityName()).thenReturn("entityName");

    // Create a mock Session
    Session session = mock(Session.class);
    when(session.getTimestamp()).thenReturn(new Timestamp(System.currentTimeMillis()));

    // Create a mock CacheAccessStrategy
    CacheAccessStrategy cacheAccessStrategy = mock(CacheAccessStrategy.class);
    when(persister.getCacheAccessStrategy()).thenReturn(cacheAccessStrategy);

    // Create a mock CacheKey
    CacheKey cacheKey = mock(CacheKey.class);
    when(session.generateCacheKey(any(), any(), any())).thenReturn(cacheKey);

    // Test when cache is present
    when(cacheAccessStrategy.get(any(), any())).thenReturn(new Object());
    assertTrue(isCached(entityKey, persister));

    // Test when cache is not present
    when(cacheAccessStrategy.get(any(), any())).thenReturn(null);
    assertFalse(isCached(entityKey, persister));

    // Test when persister has no cache
    when(persister.hasCache()).thenReturn(false);
    assertFalse(isCached(entityKey, persister));

    // Test with a null entityKey
    assertThrows(NullPointerException.class, () -> isCached(null, persister));

    // Test with a null persister
    assertThrows(NullPointerException.class, () -> isCached(entityKey, null));
}