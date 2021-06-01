package com.clumd.projects.javajson.api.schema;

import com.clumd.projects.javajson.api.JsonParser;
import com.clumd.projects.javajson.api.JsonSchemaEnforcer;
import com.clumd.projects.javajson.exceptions.schema.InvalidSchemaException;
import com.clumd.projects.javajson.exceptions.schema.SchemaViolationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class RefTest {

    @Test
    public void refMustBeString() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("''"),
                    JsonParser.parse("{'$ref':{}}")
            );
            fail("The previous method call should have thrown an exception");
        } catch (InvalidSchemaException e) {
            assertEquals("Wrong type for schema property: <base element>.$ref\n" +
                    "$ref must link to a valid sub-schema object.\n" +
                    "Expected: STRING  ->  Received: OBJECT", e.getMessage());
        }
    }

    @Test
    public void refNotFound() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("''"),
                    JsonParser.parse("{'$ref':'/thisDoesntExist'}")
            );
            fail("The previous method call should have thrown an exception");
        } catch (InvalidSchemaException e) {
            assertEquals("Missing property in schema at: <base element>.$ref\n\n" +
                    "Caused By:\nthisDoesntExist not found on element: <base element>", e.getMessage());
        }
    }

    @Test
    public void relativeReferencesNotSupported() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("''"),
                    JsonParser.parse("{'$ref':'relativeReference'}")
            );
            fail("The previous method call should have thrown an exception");
        } catch (InvalidSchemaException e) {
            assertEquals("Unexpected value for schema property: <base element>.$ref\n" +
                    "Relative sub-schema keys are not supported in this implementation.", e.getMessage());
        }
    }

    @Test
    public void nestedReferencesTestPass() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("7"),
                JsonParser.parse("{'allOf':[{'maximum':50,'$ref':'/wow.foo[0].bar'}],'final':{'const':7},'wow':{'other':{'maximum':20,'$ref':'/final'},'foo':[{'bar':{'minimum':5,'$ref':'#/wow.other'}},1]}}"))
        );
    }

    @Test
    public void nestedReferencesTestFail() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("7"),
                    JsonParser.parse("{'allOf':[{'maximum':50,'$ref':'/wow.foo[0].bar'}],'final':{'const':5},'wow':{'other':{'maximum':20,'$ref':'/final'},'foo':[{'bar':{'minimum':5,'$ref':'#/wow.other'}},1]}}")
            );
            fail("The previous method call should have thrown an exception");
        } catch (SchemaViolationException e) {
            assertEquals("Unexpected value.\n" +
                    "Schema constraint violated: allOf[0].$ref.$ref.$ref.const\n" +
                    "Value MUST match the schema's constant.", e.getMessage());
        }
    }

    @Test
    public void schemaCannotCyclicallyReference_1() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("''"),
                    JsonParser.parse("{'$ref':''}")
            );
            fail("The previous method call should have thrown an exception");
        } catch (InvalidSchemaException e) {
            assertEquals("Unexpected value for schema property: <base element>.$ref\n" +
                    "Schema Reference has a cyclic dependency.", e.getMessage());
        }
    }

    @Test
    public void schemaCannotCyclicallyReference_2() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("''"),
                    JsonParser.parse("{'$ref':'/reference','reference':{'$ref':'/reference'}}")
            );
            fail("The previous method call should have thrown an exception");
        } catch (InvalidSchemaException e) {
            assertEquals("Unexpected value for schema property: $ref.$ref\n" +
                    "Schema Reference has a cyclic dependency.", e.getMessage());
        }
    }

    @Test
    public void schemaCannotCyclicallyReference_3() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("''"),
                    JsonParser.parse("{'$ref':'/refs/one','refs':{'one':{'$ref':'/refs/two'},'two':{'$ref':'/refs/one'}}}")
            );
            fail("The previous method call should have thrown an exception");
        } catch (InvalidSchemaException e) {
            assertEquals("Unexpected value for schema property: $ref.$ref.$ref\n" +
                    "Schema Reference has a cyclic dependency.", e.getMessage());
        }
    }

    @Test
    public void refDoesNotApplyAlreadySeenConstraints() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("1"),
                JsonParser.parse("{'const':1}"))
        );
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("1"),
                JsonParser.parse("{'const':1, '$ref':'/one', 'one':{'const':5}}"))
        );
    }

    @Test
    public void refSupportsForwardSlashAbsoluteRef() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("5"),
                JsonParser.parse("{'$ref':'/one', 'one':{'const':5}}"))
        );
    }

    @Test
    public void refSupportsHashForwardSlashAbsoluteRef() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("5"),
                JsonParser.parse("{'$ref':'#/one', 'one':{'const':5}}"))
        );
    }

    @Test
    public void refSupportsHashAbsoluteRef() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("5"),
                JsonParser.parse("{'$ref':'#one', 'one':{'const':5}}"))
        );
    }

    @Test
    public void refWithoutEscapedSlash() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("5"),
                    JsonParser.parse("{'$ref':'#one/two', 'one/two':{'const':5}}")
            );
            fail("The previous method call should have thrown an exception");
        } catch (InvalidSchemaException e) {
            assertEquals("Missing property in schema at: <base element>.$ref\n\n" +
                    "Caused By:\none not found on element: <base element>", e.getMessage());
        }
    }

    @Test
    public void refWithEscapedSlash() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("5"),
                JsonParser.parse("{'$ref':'#one~1two', 'one/two':{'const':5}}")
        ));
    }

    @Test
    public void refWithoutEscapedTilde() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("5"),
                JsonParser.parse("{'$ref':'#one~1two', 'one/two':{'const':5}}")
        ));
    }

    @Test
    public void refWithEscapedTilde() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("5"),
                JsonParser.parse("{'$ref':'#one~01two', 'one/two':{'const':5}}")
        ));
    }

    @Test
    public void refToArray() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("5"),
                JsonParser.parse("{'$ref':'#one[0]', 'one':[{'const':5}]}")
        ));
    }

    @Test
    public void refWithSpaceKey() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("5"),
                JsonParser.parse("{'$ref':'#one two/1', 'one two':[1,{'const':5}]}")
        ));
    }

    @Test
    public void internalReferenceWhileValidating() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("{\n" +
                        "                \"description\": \"match\",\n" +
                        "                \"data\": {\"bar\": 3}\n" +
                        "            }"),
                JsonParser.parse("{\n" +
                        "            \"properties\": {\n" +
                        "                \"foo\": {\"type\": \"integer\"},\n" +
                        "                \"bar\": {\"$ref\": \"#/properties/foo\"}\n" +
                        "            }\n" +
                        "        }")
        ));
    }
}
