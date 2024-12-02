@Test
public void testGetDistancesMap() {
    // Setup
    Group run = mock(Group.class);
    String year = "2020";
    IWContext iwc = mock(IWContext.class);
    GroupBiz groupBiz = mock(GroupBiz.class);
    Group y = mock(Group.class);
    Group dis = mock(Group.class);
    when(run.getName()).thenReturn(year);
    when(y.getName()).thenReturn(year);
    when(dis.getPrimaryKey()).thenReturn(1);
    when(dis.getName()).thenReturn("10K");
    when(IWContext.getInstance()).thenReturn(iwc);
    when(getYears(run)).thenReturn(Arrays.asList(y));
    when(getGroupBiz(iwc).getChildGroupsRecursiveResultFiltered(y, Arrays.asList(IWMarathonConstants.GROUP_TYPE_RUN_DISTANCE), true)).thenReturn(Arrays.asList(dis));

    // Execute
    Map result = getDistancesMap(run, year);

    // Verify
    assertNotNull(result);
    assertTrue(result instanceof Map);
    assertEquals(1, result.size());
    assertTrue(result.containsKey("1"));
    assertEquals("10K", result.get("1"));

    // Edge case: Year not found
    when(y.getName()).thenReturn("2019");
    result = getDistancesMap(run, year);
    assertTrue(result.isEmpty());

    // Edge case: Exception thrown
    when(getGroupBiz(iwc).getChildGroupsRecursiveResultFiltered(y, Arrays.asList(IWMarathonConstants.GROUP_TYPE_RUN_DISTANCE), true)).thenThrow(new RuntimeException());
    result = getDistancesMap(run, year);
    assertTrue(result.isEmpty());
}