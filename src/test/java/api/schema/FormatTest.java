package api.schema;

import api.JsonParser;
import api.JsonSchemaEnforcer;
import exceptions.schema.InvalidSchemaException;
import exceptions.schema.SchemaViolationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FormatTest {

    @Test
    public void formatHasToBeAString() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("'0000-00-00'"),
                    JsonParser.parse("{'format': 1}")
            );
        } catch (InvalidSchemaException e) {
            assertEquals("Wrong type for schema property: <base element>.format\n" +
                    "Expected: STRING  ->  Received: LONG", e.getMessage());
        }
    }

    @Test
    public void objectToValidateHasToBeAString() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("12345"),
                    JsonParser.parse("{'format': 'date'}")
            );
        } catch (SchemaViolationException e) {
            assertEquals("Mismatched data type.\n" +
                    "Schema constraint violated: <base element>.format\n" +
                    "Expected: STRING  ->  Received: LONG", e.getMessage());
        }
    }

    @Test
    public void validateMessageWhenValueDoesntMatch() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("'0000-00-00'"),
                    JsonParser.parse("{'format': 'date'}")
            );
        } catch (SchemaViolationException e) {
            assertEquals("Unexpected value.\n" +
                    "Schema constraint violated: <base element>.format\n" +
                    "Value failed to match against the format constraint.", e.getMessage());
        }
    }

    @Test
    public void test1Date() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'0000-01-01'"),
                JsonParser.parse("{'format': 'date'}")
        ));
    }

    @Test
    public void testMiddleDate() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'2000-06-10'"),
                JsonParser.parse("{'format': 'date'}")
        ));
    }

    @Test
    public void testMaxDate() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'9999-12-31'"),
                JsonParser.parse("{'format': 'date'}")
        ));
    }

    @Test
    public void testAboveMaxYear() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'10000-12-31'"),
                JsonParser.parse("{'format': 'date'}")
        ));
    }

    @Test
    public void testAboveMaxMonth() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'2010-13-31'"),
                JsonParser.parse("{'format': 'date'}")
        ));
    }

    @Test
    public void testAboveMaxDay() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'2010-06-35'"),
                JsonParser.parse("{'format': 'date'}")
        ));
    }

    @Test
    public void testDontRequireLeading0() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'3000-6-08'"),
                JsonParser.parse("{'format': 'date'}")
        ));
    }

    @Test
    public void testOtherDelimiters() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'3000/06/08'"),
                JsonParser.parse("{'format': 'date'}")
        ));
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'3000.06.08'"),
                JsonParser.parse("{'format': 'date'}")
        ));
    }

    @Test
    public void testDateInWrongOrder() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'10-10-1000'"),
                JsonParser.parse("{'format': 'date'}")
        ));
    }

    @Test
    public void testMaxDate30InJune() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'3000-06-30'"),
                JsonParser.parse("{'format': 'date'}")
        ));
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'3000-06-31'"),
                JsonParser.parse("{'format': 'date'}")
        ));
    }

    @Test
    public void testValidTime() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'08:30:06.283185Z'"),
                JsonParser.parse("{'format': 'time'}")
        ));
    }

    @Test
    public void testValid12HrTime() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'1:01 AM'"),
                JsonParser.parse("{'format': 'time'}")
        ));
    }

    @Test
    public void testValid12HrTimeWithSeconds() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'03.24.36 PM'"),
                JsonParser.parse("{'format': 'time'}")
        ));
    }

    @Test
    public void testMidnight() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'00:00:00'"),
                JsonParser.parse("{'format': 'time'}")
        ));
    }

    @Test
    public void testMidDay() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'12:00:00'"),
                JsonParser.parse("{'format': 'time'}")
        ));
    }

    @Test
    public void testBeyondMidNight() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'23:59:60'"),
                JsonParser.parse("{'format': 'time'}")
        ));
    }

    @Test
    public void testTimeWithTimezone() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'08:30:06 PST'"),
                JsonParser.parse("{'format': 'time'}")
        ));
    }

    @Test
    public void testTimeWithInvalidSubSecondDelimiter() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'01:01:01,1111'"),
                JsonParser.parse("{'format': 'time'}")
        ));
    }

    @Test
    public void testTimeWithOutLeadingZeros() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'1:1:1'"),
                JsonParser.parse("{'format': 'time'}")
        ));
    }

    @Test
    public void testTimeAbove24Hr() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'24:60:60'"),
                JsonParser.parse("{'format': 'time'}")
        ));
    }

    @Test
    public void test12HrLeadingZero() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'9:9 PM'"),
                JsonParser.parse("{'format': 'time'}")
        ));
    }

    @Test
    public void testValidDateTime1() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'2009-12T12:34'"),
                JsonParser.parse("{'format': 'datetime'}")
        ));
    }

    @Test
    public void testValidDateTime2() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'2009'"),
                JsonParser.parse("{'format': 'datetime'}")
        ));
    }

    @Test
    public void testValidDateTime3() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'2009-05-19'"),
                JsonParser.parse("{'format': 'datetime'}")
        ));
    }

    @Test
    public void testValidDateTime4() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'2009-05-19'"),
                JsonParser.parse("{'format': 'datetime'}")
        ));
    }

    @Test
    public void testValidDateTime5() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'20090519'"),
                JsonParser.parse("{'format': 'datetime'}")
        ));
    }

    @Test
    public void testValidDateTime6() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'2009123'"),
                JsonParser.parse("{'format': 'datetime'}")
        ));
    }

    @Test
    public void testValidDateTime7() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'2009-05'"),
                JsonParser.parse("{'format': 'datetime'}")
        ));
    }

    @Test
    public void testValidDateTime8() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'2009-123'"),
                JsonParser.parse("{'format': 'datetime'}")
        ));
    }

    @Test
    public void testValidDateTime9() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'2009-222'"),
                JsonParser.parse("{'format': 'datetime'}")
        ));
    }

    @Test
    public void testValidDateTime10() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'2009-001'"),
                JsonParser.parse("{'format': 'datetime'}")
        ));
    }

    @Test
    public void testValidDateTime11() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'2009-W01-1'"),
                JsonParser.parse("{'format': 'datetime'}")
        ));
    }

    @Test
    public void testValidDateTime12() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'2009-W51-1'"),
                JsonParser.parse("{'format': 'datetime'}")
        ));
    }

    @Test
    public void testValidDateTime13() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'2009-W511'"),
                JsonParser.parse("{'format': 'datetime'}")
        ));
    }

    @Test
    public void testValidDateTime14() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'2009-W33'"),
                JsonParser.parse("{'format': 'datetime'}")
        ));
    }

    @Test
    public void testValidDateTime15() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'2009W511'"),
                JsonParser.parse("{'format': 'datetime'}")
        ));
    }

    @Test
    public void testValidDateTime16() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'2009-05-19'"),
                JsonParser.parse("{'format': 'datetime'}")
        ));
    }

    @Test
    public void testValidDateTime17() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'2009-05-19 00:00'"),
                JsonParser.parse("{'format': 'datetime'}")
        ));
    }

    @Test
    public void testValidDateTime18() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'2009-05-19 14'"),
                JsonParser.parse("{'format': 'datetime'}")
        ));
    }

    @Test
    public void testValidDateTime19() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'2009-05-19 14:31'"),
                JsonParser.parse("{'format': 'datetime'}")
        ));
    }

    @Test
    public void testValidDateTime20() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'2009-05-19 14:39:22'"),
                JsonParser.parse("{'format': 'datetime'}")
        ));
    }

    @Test
    public void testValidDateTime21() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'2009-05-19T14:39Z'"),
                JsonParser.parse("{'format': 'datetime'}")
        ));
    }

    @Test
    public void testValidDateTime22() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'2009-W21-2'"),
                JsonParser.parse("{'format': 'datetime'}")
        ));
    }

    @Test
    public void testValidDateTime23() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'2009-W21-2T01:22'"),
                JsonParser.parse("{'format': 'datetime'}")
        ));
    }

    @Test
    public void testValidDateTime24() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'2009-139'"),
                JsonParser.parse("{'format': 'datetime'}")
        ));
    }

    @Test
    public void testValidDateTime25() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'2009-05-19 14:39:22-06:00'"),
                JsonParser.parse("{'format': 'datetime'}")
        ));
    }

    @Test
    public void testValidDateTime26() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'2009-05-19 14:39:22+0600'"),
                JsonParser.parse("{'format': 'datetime'}")
        ));
    }

    @Test
    public void testValidDateTime27() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'2009-05-19 14:39:22-01'"),
                JsonParser.parse("{'format': 'datetime'}")
        ));
    }

    @Test
    public void testValidDateTime28() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'20090621T0545Z'"),
                JsonParser.parse("{'format': 'datetime'}")
        ));
    }

    @Test
    public void testValidDateTime29() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'2007-04-06T00:00'"),
                JsonParser.parse("{'format': 'datetime'}")
        ));
    }

    @Test
    public void testValidDateTime30() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'2007-04-05T24:00'"),
                JsonParser.parse("{'format': 'datetime'}")
        ));
    }

    @Test
    public void testValidDateTime31() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'2010-02-18T16:23:48.5'"),
                JsonParser.parse("{'format': 'datetime'}")
        ));
    }

    @Test
    public void testValidDateTime32() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'2010-02-18T16:23:48,444'"),
                JsonParser.parse("{'format': 'datetime'}")
        ));
    }

    @Test
    public void testValidDateTime33() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'2010-02-18T16:23:48,3-06:00'"),
                JsonParser.parse("{'format': 'datetime'}")
        ));
    }

    @Test
    public void testValidDateTime34() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'2010-02-18T16:23.4'"),
                JsonParser.parse("{'format': 'datetime'}")
        ));
    }

    @Test
    public void testValidDateTime35() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'2010-02-18T16:23,25'"),
                JsonParser.parse("{'format': 'datetime'}")
        ));
    }

    @Test
    public void testValidDateTime36() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'2010-02-18T16:23.33+0600'"),
                JsonParser.parse("{'format': 'datetime'}")
        ));
    }

    @Test
    public void testValidDateTime37() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'2010-02-18T16.23334444'"),
                JsonParser.parse("{'format': 'datetime'}")
        ));
    }

    @Test
    public void testValidDateTime38() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'2010-02-18T16,2283'"),
                JsonParser.parse("{'format': 'datetime'}")
        ));
    }

    @Test
    public void testValidDateTime39() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'2009-05-19 143922.500'"),
                JsonParser.parse("{'format': 'datetime'}")
        ));
    }

    @Test
    public void testValidDateTime40() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'2009-05-19 1439,55'"),
                JsonParser.parse("{'format': 'datetime'}")
        ));
    }

    @Test
    public void testInvalidDateTime1() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'200905'"),
                JsonParser.parse("{'format': 'datetime'}")
        ));
    }

    @Test
    public void testInvalidDateTime2() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'2009367'"),
                JsonParser.parse("{'format': 'datetime'}")
        ));
    }

    @Test
    public void testInvalidDateTime3() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'2009-'"),
                JsonParser.parse("{'format': 'datetime'}")
        ));
    }

    @Test
    public void testInvalidDateTime4() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'2007-04-05T24:50'"),
                JsonParser.parse("{'format': 'datetime'}")
        ));
    }

    @Test
    public void testInvalidDateTime5() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'2009-000'"),
                JsonParser.parse("{'format': 'datetime'}")
        ));
    }

    @Test
    public void testInvalidDateTime6() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'2009-M511'"),
                JsonParser.parse("{'format': 'datetime'}")
        ));
    }

    @Test
    public void testInvalidDateTime7() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'2009M511'"),
                JsonParser.parse("{'format': 'datetime'}")
        ));
    }

    @Test
    public void testInvalidDateTime8() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'2009-05-19T14a39r'"),
                JsonParser.parse("{'format': 'datetime'}")
        ));
    }

    @Test
    public void testInvalidDateTime9() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'2009-05-19T14:3924'"),
                JsonParser.parse("{'format': 'datetime'}")
        ));
    }

    @Test
    public void testInvalidDateTime10() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'2009-0519'"),
                JsonParser.parse("{'format': 'datetime'}")
        ));
    }

    @Test
    public void testInvalidDateTime11() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'2009-05-1914:39'"),
                JsonParser.parse("{'format': 'datetime'}")
        ));
    }

    @Test
    public void testInvalidDateTime12() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'2009-05-19 14:'"),
                JsonParser.parse("{'format': 'datetime'}")
        ));
    }

    @Test
    public void testInvalidDateTime13() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'2009-05-19r14:39'"),
                JsonParser.parse("{'format': 'datetime'}")
        ));
    }

    @Test
    public void testInvalidDateTime14() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'2009-05-19 14a39a22'"),
                JsonParser.parse("{'format': 'datetime'}")
        ));
    }

    @Test
    public void testInvalidDateTime15() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'200912-01'"),
                JsonParser.parse("{'format': 'datetime'}")
        ));
    }

    @Test
    public void testInvalidDateTime16() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'2009-05-19 14:39:22+06a00'"),
                JsonParser.parse("{'format': 'datetime'}")
        ));
    }

    @Test
    public void testInvalidDateTime17() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'2009-05-19 146922.500'"),
                JsonParser.parse("{'format': 'datetime'}")
        ));
    }

    @Test
    public void testInvalidDateTime18() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'2010-02-18T16.5:23.35:48'"),
                JsonParser.parse("{'format': 'datetime'}")
        ));
    }

    @Test
    public void testInvalidDateTime19() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'2010-02-18T16:23.35:48'"),
                JsonParser.parse("{'format': 'datetime'}")
        ));
    }

    @Test
    public void testInvalidDateTime20() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'2010-02-18T16:23.35:48.45'"),
                JsonParser.parse("{'format': 'datetime'}")
        ));
    }

    @Test
    public void testInvalidDateTime21() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'2009-05-19 14.5.44'"),
                JsonParser.parse("{'format': 'datetime'}")
        ));
    }

    @Test
    public void testInvalidDateTime22() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'2010-02-18T16:23.33.600'"),
                JsonParser.parse("{'format': 'datetime'}")
        ));
    }

    @Test
    public void testInvalidDateTime23() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'2010-02-18T16,25:23:48,444'"),
                JsonParser.parse("{'format': 'datetime'}")
        ));
    }

    @Test
    public void validDuration() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'P3Y6M4DT12H30M5S'"),
                JsonParser.parse("{'format': 'duration'}")
        ));
    }

    @Test
    public void validDuration2() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'P4DT12H30M5S'"),
                JsonParser.parse("{'format': 'duration'}")
        ));
    }

    @Test
    public void validDuration3() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'P3Y6M4DT12H30M5S'"),
                JsonParser.parse("{'format': 'duration'}")
        ));
    }

    @Test
    public void validDuration4() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'P4Y'"),
                JsonParser.parse("{'format': 'duration'}")
        ));
    }

    @Test
    public void validDuration5() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'PT0S'"),
                JsonParser.parse("{'format': 'duration'}")
        ));
    }

    @Test
    public void validDuration6() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'P0D'"),
                JsonParser.parse("{'format': 'duration'}")
        ));
    }

    @Test
    public void validDuration7() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'P1M'"),
                JsonParser.parse("{'format': 'duration'}")
        ));
    }

    @Test
    public void validDuration8() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'PT1M'"),
                JsonParser.parse("{'format': 'duration'}")
        ));
    }

    @Test
    public void validDuration9() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'PT36H'"),
                JsonParser.parse("{'format': 'duration'}")
        ));
    }

    @Test
    public void validDuration10() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'P1DT12H'"),
                JsonParser.parse("{'format': 'duration'}")
        ));
    }

    @Test
    public void validDuration11() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'P2W'"),
                JsonParser.parse("{'format': 'duration'}")
        ));
    }

    @Test
    public void invalidDuration1() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'PT1D'"),
                JsonParser.parse("{'format': 'duration'}")
        ));
    }

    @Test
    public void invalidDuration2() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'P'"),
                JsonParser.parse("{'format': 'duration'}")
        ));
    }

    @Test
    public void invalidDuration3() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'P1YT'"),
                JsonParser.parse("{'format': 'duration'}")
        ));
    }

    @Test
    public void invalidDuration4() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'PT'"),
                JsonParser.parse("{'format': 'duration'}")
        ));
    }

    @Test
    public void invalidDuration5() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'P2D1Y'"),
                JsonParser.parse("{'format': 'duration'}")
        ));
    }

    @Test
    public void invalidDuration6() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'P1D2H'"),
                JsonParser.parse("{'format': 'duration'}")
        ));
    }

    @Test
    public void invalidDuration7() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'P2S'"),
                JsonParser.parse("{'format': 'duration'}")
        ));
    }

    @Test
    public void validRegex() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'^([0-9A-Fa-f]{8}(?:-[0-9A-Fa-f]{4}){3}-[0-9A-Fa-f]{12})$'"),
                JsonParser.parse("{'format': 'regex'}")
        ));
    }

    @Test
    public void invalidRegex() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'^([0-9A-Fa-f{3}-)$'"),
                JsonParser.parse("{'format': 'regex'}")
        ));
    }

    @Test
    public void validEmail1() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'john.appleseed@example.com'"),
                JsonParser.parse("{'format': 'email'}")
        ));
    }

    @Test
    public void validEmail2() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'te~st@example.com'"),
                JsonParser.parse("{'format': 'email'}")
        ));
    }

    @Test
    public void validEmail3() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'~test@example.com'"),
                JsonParser.parse("{'format': 'email'}")
        ));
    }

    @Test
    public void validEmail4() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'test~@example.com'"),
                JsonParser.parse("{'format': 'email'}")
        ));
    }

    @Test
    public void validEmail5() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'te.s.t@example.com'"),
                JsonParser.parse("{'format': 'email'}")
        ));
    }

    @Test
    public void validEmail6() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'te.s.t@examp.le.co.uk'"),
                JsonParser.parse("{'format': 'email'}")
        ));
    }

    @Test
    public void validEmail7() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'google+example@gmail.com'"),
                JsonParser.parse("{'format': 'email'}")
        ));
    }

    @Test
    public void invalidEmail1() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'2962'"),
                JsonParser.parse("{'format': 'email'}")
        ));
    }

    @Test
    public void invalidEmail2() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'.test@example.com'"),
                JsonParser.parse("{'format': 'email'}")
        ));
    }

    @Test
    public void invalidEmail3() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'test.@example.com'"),
                JsonParser.parse("{'format': 'email'}")
        ));
    }

    @Test
    public void invalidEmail4() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'te..st@example.com'"),
                JsonParser.parse("{'format': 'email'}")
        ));
    }

    @Test
    public void invalidEmail5() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'test@example..com'"),
                JsonParser.parse("{'format': 'email'}")
        ));
    }

    @Test
    public void invalidEmail6() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'test@example.com.'"),
                JsonParser.parse("{'format': 'email'}")
        ));
    }

    @Test
    public void invalidEmail8() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'test@example.com-'"),
                JsonParser.parse("{'format': 'email'}")
        ));
    }

    @Test
    public void invalidEmail9() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'testexample.com'"),
                JsonParser.parse("{'format': 'email'}")
        ));
    }

    @Test
    public void validPhone1() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'(999) 999 9999'"),
                JsonParser.parse("{'format': 'phone'}")
        ));
    }

    @Test
    public void validPhone2() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'(999) 999-9999'"),
                JsonParser.parse("{'format': 'phone'}")
        ));
    }

    @Test
    public void validPhone3() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'999-999-9999'"),
                JsonParser.parse("{'format': 'phone'}")
        ));
    }

    @Test
    public void validPhone4() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'+9 999-999-9999'"),
                JsonParser.parse("{'format': 'phone'}")
        ));
    }

    @Test
    public void validPhone5() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'+9 (999) 999-9999'"),
                JsonParser.parse("{'format': 'phone'}")
        ));
    }

    @Test
    public void validPhone6() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'999-9999'"),
                JsonParser.parse("{'format': 'phone'}")
        ));
    }

    @Test
    public void validPhone7() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'99-9999'"),
                JsonParser.parse("{'format': 'phone'}")
        ));
    }

    @Test
    public void validPhone8() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'+44 (1746) 746 0927'"),
                JsonParser.parse("{'format': 'phone'}")
        ));
    }

    @Test
    public void validPhone9() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'12345678 ext 1.3.5'"),
                JsonParser.parse("{'format': 'phone'}")
        ));
    }

    @Test
    public void validPhone10() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'111'"),
                JsonParser.parse("{'format': 'phone'}")
        ));
    }

    @Test
    public void validPhone11() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'01234567890'"),
                JsonParser.parse("{'format': 'phone'}")
        ));
    }

    @Test
    public void invalidPhone1() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'abc'"),
                JsonParser.parse("{'format': 'phone'}")
        ));
    }

    @Test
    public void invalidPhone2() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'+999 (999.9) 99-999-9999'"),
                JsonParser.parse("{'format': 'phone'}")
        ));
    }

    @Test
    public void invalidPhone3() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'999999999999999999999999'"),
                JsonParser.parse("{'format': 'phone'}")
        ));
    }

    @Test
    public void validSemVer1() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'v1'"),
                JsonParser.parse("{'format': 'version'}")
        ));
    }

    @Test
    public void validSemVer2() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'version 1.0'"),
                JsonParser.parse("{'format': 'version'}")
        ));
    }

    @Test
    public void validSemVer3() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'v1.0.0'"),
                JsonParser.parse("{'format': 'version'}")
        ));
    }

    @Test
    public void validSemVer4() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'v1.0.0-beta'"),
                JsonParser.parse("{'format': 'version'}")
        ));
    }

    @Test
    public void validSemVer5() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'v1.0.0-beta+test1'"),
                JsonParser.parse("{'format': 'version'}")
        ));
    }

    @Test
    public void validSemVer6() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'45.20.1-beta.123'"),
                JsonParser.parse("{'format': 'version'}")
        ));
    }

    @Test
    public void validSemVer7() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'1.0.2-a'"),
                JsonParser.parse("{'format': 'version'}")
        ));
    }

    @Test
    public void invalidSemVer1() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'ab'"),
                JsonParser.parse("{'format': 'version'}")
        ));
    }

    @Test
    public void invalidSemVer2() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'123.'"),
                JsonParser.parse("{'format': 'version'}")
        ));
    }

    @Test
    public void invalidSemVer3() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'.123'"),
                JsonParser.parse("{'format': 'version'}")
        ));
    }

    @Test
    public void invalidSemVer4() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'1.a.3'"),
                JsonParser.parse("{'format': 'version'}")
        ));
    }
}
