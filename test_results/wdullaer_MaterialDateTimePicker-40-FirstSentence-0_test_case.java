@Test
public void testGetEnteredTime() {
    // Initialize the object and set the 24 hour mode to false
    TimePickerDialog timePickerDialog = new TimePickerDialog();
    timePickerDialog.setIs24HourMode(false);

    // Simulate the user entering time as 12:34 PM
    timePickerDialog.addTypedTime(timePickerDialog.getValFromKeyCode(1));
    timePickerDialog.addTypedTime(timePickerDialog.getValFromKeyCode(2));
    timePickerDialog.addTypedTime(timePickerDialog.getValFromKeyCode(3));
    timePickerDialog.addTypedTime(timePickerDialog.getValFromKeyCode(4));
    timePickerDialog.addTypedTime(timePickerDialog.getAmOrPmKeyCode(TimePickerDialog.PM));

    // Get the entered time
    int[] enteredTime = timePickerDialog.getEnteredTime(null);

    // Assert that the entered time is correctly returned as {12, 34, PM}
    Assert.assertEquals(12, enteredTime[0]);
    Assert.assertEquals(34, enteredTime[1]);
    Assert.assertEquals(TimePickerDialog.PM, enteredTime[2]);

    // Now simulate the user entering time as 00:00 AM (edge case)
    timePickerDialog.clearTypedTimes();
    timePickerDialog.addTypedTime(timePickerDialog.getValFromKeyCode(0));
    timePickerDialog.addTypedTime(timePickerDialog.getValFromKeyCode(0));
    timePickerDialog.addTypedTime(timePickerDialog.getValFromKeyCode(0));
    timePickerDialog.addTypedTime(timePickerDialog.getValFromKeyCode(0));
    timePickerDialog.addTypedTime(timePickerDialog.getAmOrPmKeyCode(TimePickerDialog.AM));

    // Get the entered time
    enteredTime = timePickerDialog.getEnteredTime(new Boolean[]{false, false});

    // Assert that the entered time is correctly returned as {0, 0, AM}
    Assert.assertEquals(0, enteredTime[0]);
    Assert.assertEquals(0, enteredTime[1]);
    Assert.assertEquals(TimePickerDialog.AM, enteredTime[2]);
}