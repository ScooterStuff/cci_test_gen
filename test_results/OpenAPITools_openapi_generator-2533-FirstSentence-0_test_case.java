@Test
public void testKeyDifference() {
    // Test for StringProperty
    StringProperty stringProperty = new StringProperty();
    stringProperty.setDefault("default");
    assertEquals("@\"default\"", toDefaultValue(stringProperty));

    // Test for BooleanProperty
    BooleanProperty booleanProperty = new BooleanProperty();
    booleanProperty.setDefault(true);
    assertEquals("@(YES)", toDefaultValue(booleanProperty));
    booleanProperty.setDefault(false);
    assertEquals("@(NO)", toDefaultValue(booleanProperty));

    // Test for DoubleProperty
    DoubleProperty doubleProperty = new DoubleProperty();
    doubleProperty.setDefault(1.23);
    assertEquals("@1.23", toDefaultValue(doubleProperty));

    // Test for FloatProperty
    FloatProperty floatProperty = new FloatProperty();
    floatProperty.setDefault(1.23f);
    assertEquals("@1.23", toDefaultValue(floatProperty));

    // Test for IntegerProperty
    IntegerProperty integerProperty = new IntegerProperty();
    integerProperty.setDefault(123);
    assertEquals("@123", toDefaultValue(integerProperty));

    // Test for LongProperty
    LongProperty longProperty = new LongProperty();
    longProperty.setDefault(123L);
    assertEquals("@123", toDefaultValue(longProperty));

    // Test for null default value
    assertNull(toDefaultValue(new StringProperty()));

    // Test for property type not handled
    assertNull(toDefaultValue(new Property()));
}