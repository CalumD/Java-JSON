package com.clumd.projects.javajson.api;

import com.clumd.projects.javajson.exceptions.SchemaException;
import com.clumd.projects.javajson.exceptions.schema.InvalidSchemaException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class JsonSchemaEnforcerTest {

    private static Json EMPTY_OBJECT;

    @BeforeAll
    public static void setStatics() {
        EMPTY_OBJECT = JsonParser.parse("{}");
    }

    @Test
    public void schemaMustBeAnObject() {
        try {
            JsonSchemaEnforcer.validate(JsonParser.parse("{}"), JsonParser.parse("true"));
            fail("The previous method call should have thrown an exception");
        } catch (SchemaException e) {
            assertEquals("JSON Schemas MUST be a valid JSON Object with defined mandatory keys and structure. You provided a BOOLEAN.", e.getMessage());
        }
    }

    @Test
    public void schemaExceptionShouldBeReturnedAsFalseWhenStrict() {
        assertFalse(JsonSchemaEnforcer.validateStrict(JsonParser.parse("{}"), JsonParser.parse("true")));
    }

    @Test
    public void objectToValidateShouldNeverBeNull() {
        try {
            JsonSchemaEnforcer.validate(null, JsonParser.parse("{}"));
            fail("The previous method call should have thrown an exception");
        } catch (SchemaException e) {
            assertEquals("A null object cannot be validated against a schema.", e.getMessage());
        }
    }

    @Test
    public void schemaShouldNeverBeNull() {
        try {
            JsonSchemaEnforcer.validate(JsonParser.parse("{}"), null);
            fail("The previous method call should have thrown an exception");
        } catch (SchemaException e) {
            assertEquals("You cannot validate against a null schema.", e.getMessage());
        }
    }

    @Test
    public void baseObjectShouldntAcceptUnrecognisedStrings() {
        try {
            JsonSchemaEnforcer.validate(JsonParser.parse("{}"), JsonParser.parse("{'unknown':123}"));
        } catch (InvalidSchemaException e) {
            assertEquals("Unrecognised/Unsupported constraint used (unknown).\n" +
                    "Therefore unable to verify all schema requirements.", e.getMessage());
        }
    }

    @Test
    public void baseObjectShouldIgnoreNonConstraintStrings() {
        assertTrue(JsonSchemaEnforcer.validate(JsonParser.parse("{}"), JsonParser.parse("{'description':'123'}")));
    }

}