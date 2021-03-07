package com.clumd.projects.javajson.api.schema;

import com.clumd.projects.javajson.api.JsonParser;
import com.clumd.projects.javajson.api.JsonSchemaEnforcer;
import com.clumd.projects.javajson.exceptions.schema.InvalidSchemaException;
import com.clumd.projects.javajson.exceptions.schema.SchemaViolationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class FormatTest {

    @Test
    public void formatHasToBeAString() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("'0000-00-00'"),
                    JsonParser.parse("{'format': 1}")
            );
            fail("Previous method call should have thrown an exception.");
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
            fail("Previous method call should have thrown an exception.");
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
            fail("Previous method call should have thrown an exception.");
        } catch (SchemaViolationException e) {
            assertEquals("Unexpected value.\n" +
                    "Schema constraint violated: <base element>.format\n" +
                    "Value failed to match against the format (date) constraint.", e.getMessage());
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
    public void validPhone12() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'+1-816-555-1212'"),
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


    @Test
    public void validHostname1() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'www.example.com'"),
                JsonParser.parse("{'format': 'hostname'}")
        ));
    }

    @Test
    public void validHostname2() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'xn--4gbwdl.xn--wgbh1c'"),
                JsonParser.parse("{'format': 'hostname'}")
        ));
    }

    @Test
    public void validHostname3() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijk.com'"),
                JsonParser.parse("{'format': 'hostname'}")
        ));
    }

    @Test
    public void validHostname4() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'localhost'"),
                JsonParser.parse("{'format': 'hostname'}")
        ));
    }

    @Test
    public void validHostname5() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'localhost123'"),
                JsonParser.parse("{'format': 'hostname'}")
        ));
    }

    @Test
    public void validHostname9() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'www.google.com'"),
                JsonParser.parse("{'format': 'hostname'}")
        ));
    }

    @Test
    public void validHostname10() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghi.abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijk.com'"),
                JsonParser.parse("{'format': 'hostname'}")
        ));
    }

    @Test
    public void invalidHostname1() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'www..example.com'"),
                JsonParser.parse("{'format': 'hostname'}")
        ));
    }

    @Test
    public void invalidHostname2() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'http://www.google.com/search?q=regular%20expression'"),
                JsonParser.parse("{'format': 'hostname'}")
        ));
    }

    @Test
    public void invalidHostname3() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'http://www.google.com/search?q=regular%20expression'"),
                JsonParser.parse("{'format': 'hostname'}")
        ));
    }

    @Test
    public void invalidHostname4() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'http://www.google.com/'"),
                JsonParser.parse("{'format': 'hostname'}")
        ));
    }

    @Test
    public void invalidHostname5() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'-a-host-name-that-starts-with--'"),
                JsonParser.parse("{'format': 'hostname'}")
        ));
    }

    @Test
    public void invalidHostname6() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'not_a_valid_host_name'"),
                JsonParser.parse("{'format': 'hostname'}")
        ));
    }

    @Test
    public void invalidHostname7() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'a-vvvvvvvvvvvvvvvveeeeeeeeeeeeeeeerrrrrrrrrrrrrrrryyyyyyyyyyyyyyyy-long-host-name-component'"),
                JsonParser.parse("{'format': 'hostname'}")
        ));
    }

    @Test
    public void invalidHostname8() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'-hostname'"),
                JsonParser.parse("{'format': 'hostname'}")
        ));
    }

    @Test
    public void invalidHostname9() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'hostname-'"),
                JsonParser.parse("{'format': 'hostname'}")
        ));
    }

    @Test
    public void invalidHostname10() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'_hostname'"),
                JsonParser.parse("{'format': 'hostname'}")
        ));
    }

    @Test
    public void invalidHostname11() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'hostname_'"),
                JsonParser.parse("{'format': 'hostname'}")
        ));
    }

    @Test
    public void invalidHostname12() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'host_name'"),
                JsonParser.parse("{'format': 'hostname'}")
        ));
    }

    @Test
    public void invalidHostname13() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijkl.com'"),
                JsonParser.parse("{'format': 'hostname'}")
        ));
    }


    @Test
    public void validIPV4_1() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'192.168.0.1'"),
                JsonParser.parse("{'format': 'ipv4'}")
        ));
    }

    @Test
    public void validIPV4_2() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'127.0.0.1'"),
                JsonParser.parse("{'format': 'ipv4'}")
        ));
    }

    @Test
    public void validIPV4_3() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'10.10.20.10'"),
                JsonParser.parse("{'format': 'ipv4'}")
        ));
    }

    @Test
    public void validIPV4_4() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'100.100.100.100'"),
                JsonParser.parse("{'format': 'ipv4'}")
        ));
    }

    @Test
    public void validIPV4_5() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'255.255.255.255'"),
                JsonParser.parse("{'format': 'ipv4'}")
        ));
    }

    @Test
    public void validIPV4_6() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'0.0.0.0'"),
                JsonParser.parse("{'format': 'ipv4'}")
        ));
    }

    @Test
    public void invalidIPV4_1() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'127.0.0.0.1'"),
                JsonParser.parse("{'format': 'ipv4'}")
        ));
    }

    @Test
    public void invalidIPV4_2() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'256.256.256.256'"),
                JsonParser.parse("{'format': 'ipv4'}")
        ));
    }

    @Test
    public void invalidIPV4_3() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'127.0'"),
                JsonParser.parse("{'format': 'ipv4'}")
        ));
    }

    @Test
    public void invalidIPV4_4() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'0x7f000001'"),
                JsonParser.parse("{'format': 'ipv4'}")
        ));
    }

    @Test
    public void invalidIPV4_5() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'2130706433'"),
                JsonParser.parse("{'format': 'ipv4'}")
        ));
    }

    @Test
    public void invalidIPV4_6() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'192.168.001.001'"),
                JsonParser.parse("{'format': 'ipv4'}")
        ));
    }

    @Test
    public void invalidIPV4_7() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'192.168.001.001'"),
                JsonParser.parse("{'format': 'ipv4'}")
        ));
    }

    @Test
    public void invalidIPV4_8() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'192,168,1,1'"),
                JsonParser.parse("{'format': 'ipv4'}")
        ));
    }

    @Test
    public void invalidIPV4_9() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'256.0.0.0'"),
                JsonParser.parse("{'format': 'ipv4'}")
        ));
    }

    @Test
    public void invalidIPV4_10() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'192.256.0.0'"),
                JsonParser.parse("{'format': 'ipv4'}")
        ));
    }

    @Test
    public void invalidIPV4_11() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'192.192.256.0'"),
                JsonParser.parse("{'format': 'ipv4'}")
        ));
    }

    @Test
    public void invalidIPV4_12() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'192.192.192.256'"),
                JsonParser.parse("{'format': 'ipv4'}")
        ));
    }


    @Test
    public void validIPV6_1() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'1::d6:192.168.0.1'"),
                JsonParser.parse("{'format': 'ipv6'}")
        ));
    }

    @Test
    public void validIPV6_2() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'1000:1000:1000:1000:1000:1000:255.255.255.255'"),
                JsonParser.parse("{'format': 'ipv6'}")
        ));
    }

    @Test
    public void validIPV6_3() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'0000:0000:0000:0000:0000:0000:0000:0001'"),
                JsonParser.parse("{'format': 'ipv6'}")
        ));
    }

    @Test
    public void validIPV6_4() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'::1'"),
                JsonParser.parse("{'format': 'ipv6'}")
        ));
    }

    @Test
    public void validIPV6_5() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'::'"),
                JsonParser.parse("{'format': 'ipv6'}")
        ));
    }

    @Test
    public void validIPV6_6() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'684D:1111:222:3333:4444:5555:6:77'"),
                JsonParser.parse("{'format': 'ipv6'}")
        ));
    }

    @Test
    public void validIPV6_7() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'::42:ff:1'"),
                JsonParser.parse("{'format': 'ipv6'}")
        ));
    }

    @Test
    public void validIPV6_8() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'d6::'"),
                JsonParser.parse("{'format': 'ipv6'}")
        ));
    }

    @Test
    public void validIPV6_9() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'1:2::192.168.0.1'"),
                JsonParser.parse("{'format': 'ipv6'}")
        ));
    }

    @Test
    public void validIPV6_10() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'::ffff:192.168.0.1'"),
                JsonParser.parse("{'format': 'ipv6'}")
        ));
    }

    @Test
    public void validIPV6_11() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'1:2:3:4:5:6:7:8'"),
                JsonParser.parse("{'format': 'ipv6'}")
        ));
    }

    @Test
    public void validIPV6_12() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'fe80::1ff:fe23:4567:890a%eth2'"),
                JsonParser.parse("{'format': 'ipv6'}")
        ));
    }

    @Test
    public void validIPV6_13() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'fe80::a%eth1'"),
                JsonParser.parse("{'format': 'ipv6'}")
        ));
    }

    @Test
    public void validIPV6_14() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'fe80::1ff:fe23:4567:890a%3'"),
                JsonParser.parse("{'format': 'ipv6'}")
        ));
    }

    @Test
    public void validIPV6_15() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'fe80::/64'"),
                JsonParser.parse("{'format': 'ipv6'}")
        ));
    }

    @Test
    public void validIPV6_16() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'2001:0db8:0000:0000:0000:ff00:0042:8329'"),
                JsonParser.parse("{'format': 'ipv6'}")
        ));
    }

    @Test
    public void validIPV6_17() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'2001:db8:0:0:0:ff00:42:8329'"),
                JsonParser.parse("{'format': 'ipv6'}")
        ));
    }

    @Test
    public void validIPV6_18() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'2001:db8::ff00:42:8329'"),
                JsonParser.parse("{'format': 'ipv6'}")
        ));
    }

    @Test
    public void validIPV6_19() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'2001:db8:ffff:ffff:ffff:ffff:ffff:ffff'"),
                JsonParser.parse("{'format': 'ipv6'}")
        ));
    }

    @Test
    public void validIPV6_20() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'100::ffff:ffff:ffff:ffff'"),
                JsonParser.parse("{'format': 'ipv6'}")
        ));
    }

    @Test
    public void invalidIPV6_1() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'100:100:100:100:100:100:255.255.255.255.255'"),
                JsonParser.parse("{'format': 'ipv6'}")
        ));
    }

    @Test
    public void invalidIPV6_2() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'100:100:100:100:100:100:100:255.255.255.255'"),
                JsonParser.parse("{'format': 'ipv6'}")
        ));
    }

    @Test
    public void invalidIPV6_3() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'  ::1'"),
                JsonParser.parse("{'format': 'ipv6'}")
        ));
    }

    @Test
    public void invalidIPV6_4() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'::1111111111'"),
                JsonParser.parse("{'format': 'ipv6'}")
        ));
    }

    @Test
    public void invalidIPV6_5() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'12345::'"),
                JsonParser.parse("{'format': 'ipv6'}")
        ));
    }

    @Test
    public void invalidIPV6_6() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'1:1:1:1:1:1:1:1:1:1:1:1:1:1:1:1'"),
                JsonParser.parse("{'format': 'ipv6'}")
        ));
    }

    @Test
    public void invalidIPV6_7() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'::laptop'"),
                JsonParser.parse("{'format': 'ipv6'}")
        ));
    }

    @Test
    public void invalidIPV6_8() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("':2:3:4:5:6:7:8'"),
                JsonParser.parse("{'format': 'ipv6'}")
        ));
    }

    @Test
    public void invalidIPV6_9() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'1:2:3:4:5:6:7:'"),
                JsonParser.parse("{'format': 'ipv6'}")
        ));
    }

    @Test
    public void invalidIPV6_10() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("':2:3:4::8'"),
                JsonParser.parse("{'format': 'ipv6'}")
        ));
    }

    @Test
    public void invalidIPV6_11() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'1::d6::42'"),
                JsonParser.parse("{'format': 'ipv6'}")
        ));
    }

    @Test
    public void invalidIPV6_12() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'1::2:192.168.256.1'"),
                JsonParser.parse("{'format': 'ipv6'}")
        ));
    }

    @Test
    public void invalidIPV6_13() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'1::2:192.168.ff.1'"),
                JsonParser.parse("{'format': 'ipv6'}")
        ));
    }

    @Test
    public void invalidIPV6_14() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'1:2:3:4:5:::8'"),
                JsonParser.parse("{'format': 'ipv6'}")
        ));
    }

    @Test
    public void invalidIPV6_15() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'1:2:3:4:5:6:7'"),
                JsonParser.parse("{'format': 'ipv6'}")
        ));
    }

    @Test
    public void invalidIPV6_16() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'1'"),
                JsonParser.parse("{'format': 'ipv6'}")
        ));
    }

    @Test
    public void invalidIPV6_17() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'127.0.0.1'"),
                JsonParser.parse("{'format': 'ipv6'}")
        ));
    }


    @Test
    public void validMAC_1() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'01:23:45:67:89:ab'"),
                JsonParser.parse("{'format': 'mac'}")
        ));
    }

    @Test
    public void validMAC_2() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'01-23-45-67-89-ab'"),
                JsonParser.parse("{'format': 'mac'}")
        ));
    }

    @Test
    public void validMAC_3() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'0123456789ab'"),
                JsonParser.parse("{'format': 'mac'}")
        ));
    }

    @Test
    public void validMAC_4() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'FF:FF:FF:FF:FF:FF'"),
                JsonParser.parse("{'format': 'mac'}")
        ));
    }

    @Test
    public void validMAC_5() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'00:00:00:00:00:00'"),
                JsonParser.parse("{'format': 'mac'}")
        ));
    }

    @Test
    public void validMAC_6() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'0123.4567.89ab'"),
                JsonParser.parse("{'format': 'mac'}")
        ));
    }

    @Test
    public void invalidMAC_1() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'0123:45:67:89:ab'"),
                JsonParser.parse("{'format': 'mac'}")
        ));
    }

    @Test
    public void invalidMAC_2() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("''"),
                JsonParser.parse("{'format': 'mac'}")
        ));
    }

    @Test
    public void invalidMAC_3() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'01:GG:45:67:89:ab'"),
                JsonParser.parse("{'format': 'mac'}")
        ));
    }


    @Test
    public void validURL1() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'http://www.google.com/'"),
                JsonParser.parse("{'format': 'url'}")
        ));
    }

    @Test
    public void validURL2() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'http://www.google.com/search?q=regular%20expression#anotherOne'"),
                JsonParser.parse("{'format': 'url'}")
        ));
    }

    @Test
    public void validURL3() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'http://foo.bar/?baz=qux#quux'"),
                JsonParser.parse("{'format': 'url'}")
        ));
    }

    @Test
    public void validURL4() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'http://foo.com/blah_(wikipedia)_blah#cite-1'"),
                JsonParser.parse("{'format': 'url'}")
        ));
    }

    @Test
    public void validURL5() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'http://foo.bar/?q=Test%20URL-encoded%20stuff'"),
                JsonParser.parse("{'format': 'url'}")
        ));
    }

    @Test
    public void validURL6() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'http://xn--nw2a.xn--j6w193g/'"),
                JsonParser.parse("{'format': 'url'}")
        ));
    }

    @Test
    public void validURL7() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("`http://-.~_!$&'()*+,;=:%40:80%2f::::::@example.com`"),
                JsonParser.parse("{'format': 'url'}")
        ));
    }

    @Test
    public void validURL8() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'http://223.255.255.254'"),
                JsonParser.parse("{'format': 'url'}")
        ));
    }

    @Test
    public void validURL9() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'ftp://ftp.is.co.za/rfc/rfc1808.txt'"),
                JsonParser.parse("{'format': 'url'}")
        ));
    }

    @Test
    public void validURL10() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'http://www.ietf.org/rfc/rfc2396.txt'"),
                JsonParser.parse("{'format': 'url'}")
        ));
    }

    @Test
    public void validURL11() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'ldap://[2001:db8::7]/c=GB?objectClass?one'"),
                JsonParser.parse("{'format': 'url'}")
        ));
    }

    @Test
    public void validURL12() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'mailto:John.Doe@example.com'"),
                JsonParser.parse("{'format': 'url'}")
        ));
    }

    @Test
    public void validURL13() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'news:comp.infosystems.www.servers.unix'"),
                JsonParser.parse("{'format': 'url'}")
        ));
    }

    @Test
    public void validURL14() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'tel:+1-816-555-1212'"),
                JsonParser.parse("{'format': 'url'}")
        ));
    }

    @Test
    public void validURL15() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'urn:oasis:names:specification:docbook:dtd:xml:4.1.2'"),
                JsonParser.parse("{'format': 'url'}")
        ));
    }

    @Test
    public void invalidURL1() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'test?huh.straight%20away#subPart'"),
                JsonParser.parse("{'format': 'url'}")
        ));
    }

    @Test
    public void invalidURL2() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'/search?q=regular%20expression'"),
                JsonParser.parse("{'format': 'url'}")
        ));
    }

    @Test
    public void invalidURL3() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'www.google.com/'"),
                JsonParser.parse("{'format': 'url'}")
        ));
    }

    @Test
    public void invalidURL4() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'//foo.bar/?baz=qux#quux'"),
                JsonParser.parse("{'format': 'url'}")
        ));
    }

    @Test
    public void invalidURL5() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'/abc'"),
                JsonParser.parse("{'format': 'url'}")
        ));
    }

    @Test
    public void invalidURL6() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'abc'"),
                JsonParser.parse("{'format': 'url'}")
        ));
    }

    @Test
    public void invalidURL7() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'../relative/backup/../../.././anotherOne'"),
                JsonParser.parse("{'format': 'url'}")
        ));
    }

    @Test
    public void invalidURL8() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'bar,baz:foo'"),
                JsonParser.parse("{'format': 'url'}")
        ));
    }

    @Test
    public void invalidURL9() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'\\\\WINDOWS\\fileshare'"),
                JsonParser.parse("{'format': 'url'}")
        ));
    }

    @Test
    public void invalidURL10() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'http:// shouldfail.com'"),
                JsonParser.parse("{'format': 'url'}")
        ));
    }

    @Test
    public void invalidURL11() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("':// should fail'"),
                JsonParser.parse("{'format': 'url'}")
        ));
    }


    @Test
    public void validURL_ref1() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'http://www.google.com/'"),
                JsonParser.parse("{'format': 'uri-reference'}")
        ));
    }

    @Test
    public void validURL_ref2() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'http://www.google.com/search?q=regular%20expression#anotherOne'"),
                JsonParser.parse("{'format': 'uri-reference'}")
        ));
    }

    @Test
    public void validURL_ref3() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'http://foo.bar/?baz=qux#quux'"),
                JsonParser.parse("{'format': 'uri-reference'}")
        ));
    }

    @Test
    public void validURL_ref4() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'http://foo.com/blah_(wikipedia)_blah#cite-1'"),
                JsonParser.parse("{'format': 'uri-reference'}")
        ));
    }

    @Test
    public void validURL_ref5() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'http://foo.bar/?q=Test%20URL-encoded%20stuff'"),
                JsonParser.parse("{'format': 'uri-reference'}")
        ));
    }

    @Test
    public void validURL_ref6() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'http://xn--nw2a.xn--j6w193g/'"),
                JsonParser.parse("{'format': 'uri-reference'}")
        ));
    }

    @Test
    public void validURL_ref7() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'http://-.~_!$&'()*+,;=:%40:80%2f::::::@example.com'"),
                JsonParser.parse("{'format': 'uri-reference'}")
        ));
    }

    @Test
    public void validURL_ref8() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'http://223.255.255.254'"),
                JsonParser.parse("{'format': 'uri-reference'}")
        ));
    }

    @Test
    public void validURL_ref9() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'ftp://ftp.is.co.za/rfc/rfc1808.txt'"),
                JsonParser.parse("{'format': 'uri-reference'}")
        ));
    }

    @Test
    public void validURL_ref10() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'http://www.ietf.org/rfc/rfc2396.txt'"),
                JsonParser.parse("{'format': 'uri-reference'}")
        ));
    }

    @Test
    public void validURL_ref11() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'ldap://[2001:db8::7]/c=GB?objectClass?one'"),
                JsonParser.parse("{'format': 'uri-reference'}")
        ));
    }

    @Test
    public void validURL_ref12() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'mailto:John.Doe@example.com'"),
                JsonParser.parse("{'format': 'uri-reference'}")
        ));
    }

    @Test
    public void validURL_ref13() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'news:comp.infosystems.www.servers.unix'"),
                JsonParser.parse("{'format': 'uri-reference'}")
        ));
    }

    @Test
    public void validURL_ref14() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'tel:+1-816-555-1212'"),
                JsonParser.parse("{'format': 'uri-reference'}")
        ));
    }

    @Test
    public void validURL_ref15() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'urn:oasis:names:specification:docbook:dtd:xml:4.1.2'"),
                JsonParser.parse("{'format': 'uri-reference'}")
        ));
    }

    @Test
    public void validURL_ref16() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'test?huh.straight%20away#subPart'"),
                JsonParser.parse("{'format': 'uri-reference'}")
        ));
    }

    @Test
    public void validURL_ref17() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'/search?q=regular%20expression'"),
                JsonParser.parse("{'format': 'uri-reference'}")
        ));
    }

    @Test
    public void validURL_ref18() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'www.google.com/'"),
                JsonParser.parse("{'format': 'uri-reference'}")
        ));
    }

    @Test
    public void validURL_ref19() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'//foo.bar/?baz=qux#quux'"),
                JsonParser.parse("{'format': 'uri-reference'}")
        ));
    }

    @Test
    public void validURL_ref20() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'/abc'"),
                JsonParser.parse("{'format': 'uri-reference'}")
        ));
    }

    @Test
    public void validURL_ref21() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'abc'"),
                JsonParser.parse("{'format': 'uri-reference'}")
        ));
    }

    @Test
    public void validURL_ref22() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'../relative/backup/../../.././anotherOne'"),
                JsonParser.parse("{'format': 'uri-reference'}")
        ));
    }

    @Test
    public void validURL_ref23() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'bar,baz:foo'"),
                JsonParser.parse("{'format': 'uri-reference'}")
        ));
    }

    @Test
    public void invalidURL_ref1() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'\\\\WINDOWS\\fileshare'"),
                JsonParser.parse("{'format': 'uri-reference'}")
        ));
    }

    @Test
    public void invalidURL_ref2() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'http:// shouldfail.com'"),
                JsonParser.parse("{'format': 'uri-reference'}")
        ));
    }

    @Test
    public void invalidURL_ref3() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("':// should fail'"),
                JsonParser.parse("{'format': 'uri-reference'}")
        ));
    }


    @Test
    public void validUUID1() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'2EB8AA08-AA98-11EA-B4AA-73B441D16380'"),
                JsonParser.parse("{'format': 'uuid'}")
        ));
    }

    @Test
    public void validUUID2() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'2eb8aa08-aa98-11ea-b4aa-73b441d16380'"),
                JsonParser.parse("{'format': 'uuid'}")
        ));
    }

    @Test
    public void validUUID3() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'2eb8aa08-AA98-11ea-B4Aa-73B441D16380'"),
                JsonParser.parse("{'format': 'uuid'}")
        ));
    }

    @Test
    public void validUUID4() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'00000000-0000-0000-0000-000000000000'"),
                JsonParser.parse("{'format': 'uuid'}")
        ));
    }

    @Test
    public void validUUID5() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'98d80576-482e-427f-8434-7f86890ab222'"),
                JsonParser.parse("{'format': 'uuid'}")
        ));
    }

    @Test
    public void validUUID6() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'99c17cbb-656f-564a-940f-1a4568f03487'"),
                JsonParser.parse("{'format': 'uuid'}")
        ));
    }

    @Test
    public void validUUID7() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'99c17cbb-656f-664a-940f-1a4568f03487'"),
                JsonParser.parse("{'format': 'uuid'}")
        ));
    }

    @Test
    public void validUUID8() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'99c17cbb-656f-f64a-940f-1a4568f03487'"),
                JsonParser.parse("{'format': 'uuid'}")
        ));
    }

    @Test
    public void validUUID9() {
        assertTrue(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF'"),
                JsonParser.parse("{'format': 'uuid'}")
        ));
    }

    @Test
    public void invalidUUID1() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'2eb8aa08-aa98-11ea-b4aa-73b441d1638'"),
                JsonParser.parse("{'format': 'uuid'}")
        ));
    }

    @Test
    public void invalidUUID2() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'2eb8aa08-aa98-11ea-b4aa-73b441d1638'"),
                JsonParser.parse("{'format': 'uuid'}")
        ));
    }

    @Test
    public void invalidUUID3() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'2eb8aa08-aa98-11ea-73b441d16380'"),
                JsonParser.parse("{'format': 'uuid'}")
        ));
    }

    @Test
    public void invalidUUID4() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'2eb8aa08-aa98-11ea-b4ga-73b441d16380'"),
                JsonParser.parse("{'format': 'uuid'}")
        ));
    }

    @Test
    public void invalidUUID5() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'99c17cbb:656f:f64a:940f:1a4568f03487'"),
                JsonParser.parse("{'format': 'uuid'}")
        ));
    }

    @Test
    public void invalidUUID6() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'99c17cbb_656f_f64a_940f_1a4568f03487'"),
                JsonParser.parse("{'format': 'uuid'}")
        ));
    }

    @Test
    public void invalidUUID7() {
        assertFalse(JsonSchemaEnforcer.validateStrict(
                JsonParser.parse("'000000G0-0000-0000-0000-000000000000'"),
                JsonParser.parse("{'format': 'uuid'}")
        ));
    }

    @Test
    public void unsupportedFormatIsDisabled() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("''"),
                    JsonParser.parse("{'format': 'blah'}")
            );
            fail("The previous method call should have thrown an exception.");
        } catch (InvalidSchemaException e) {
            assertEquals("Unexpected value for schema property: <base element>.format\n" +
                    "Unrecognised/Unsupported format provided (blah).", e.getMessage());
        }
    }
}
