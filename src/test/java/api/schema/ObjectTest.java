package api.schema;

import api.JsonParser;
import api.JsonSchemaEnforcer;
import exceptions.schema.InvalidSchemaException;
import exceptions.schema.SchemaViolationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class ObjectTest {

    @Test
    public void requiredConstraintMustBeArray() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("''"),
                    JsonParser.parse("{'required': ''}")
            );
            fail("Previous method call should have thrown an exception.");
        } catch (InvalidSchemaException e) {
            assertEquals("Wrong type for schema property: <base element>.required\n" +
                    "Required constraint must be a non-empty array of strings representing property names.\n" +
                    "Expected: ARRAY  ->  Received: STRING", e.getMessage());
        }
    }

    @Test
    public void requiredArrayMustHaveAtLeastOneElement() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("''"),
                    JsonParser.parse("{'required': []}")
            );
            fail("Previous method call should have thrown an exception.");
        } catch (InvalidSchemaException e) {
            assertEquals("Unexpected value for schema property: <base element>.required\n" +
                    "Must provide at least one required property.", e.getMessage());
        }
    }

    @Test
    public void requiredArrayCanOnlyContainStrings() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("''"),
                    JsonParser.parse("{'required': ['', '', 123]}")
            );
            fail("Previous method call should have thrown an exception.");
        } catch (InvalidSchemaException e) {
            assertEquals("Wrong type for schema property: <base element>.required\n" +
                    "Required constraint must be a non-empty array of strings representing property names.\n" +
                    "Expected: STRING  ->  Received: LONG", e.getMessage());
        }
    }

    @Test
    public void objectToValidateMustBeObject() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("''"),
                    JsonParser.parse("{'required': ['key1', 'key2']}")
            );
            fail("Previous method call should have thrown an exception.");
        } catch (SchemaViolationException e) {
            assertEquals("Mismatched data type.\n" +
                    "Schema constraint violated: <base element>.required\n" +
                    "Required constraint can only be applied to Object properties.", e.getMessage());
        }
    }

    @Test
    public void objectIsMissingARequiredKey() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("{'key1':1,'missing key 2':2}"),
                    JsonParser.parse("{'required': ['key1', 'key2']}")
            );
            fail("Previous method call should have thrown an exception.");
        } catch (SchemaViolationException e) {
            assertEquals("Missing property.\n" +
                    "Schema constraint violated: <base element>.required\n" +
                    "Property (key2) is mandatory, but couldn't be found.", e.getMessage());
        }
    }

    @Test
    public void objectContainsOneKey() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("{'key1':1}"),
                JsonParser.parse("{'required': ['key1']}")
        ));
    }

    @Test
    public void objectContainsAllKeys() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("{'key1':1,'otherKey':2}"),
                JsonParser.parse("{'required': ['key1', 'otherKey']}")
        ));
    }

    @Test
    public void objectCanHaveOtherKeys() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("{'key1':1,'otherKey':2, 'this key is not validated':3}"),
                JsonParser.parse("{'required': ['key1', 'otherKey']}")
        ));
    }

    @Test
    public void minPropertiesMustBeNumber() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("''"),
                    JsonParser.parse("{'minProperties': []}")
            );
            fail("Previous method call should have thrown an exception.");
        } catch (InvalidSchemaException e) {
            assertEquals("Wrong type for schema property: <base element>.minProperties\n" +
                    "Constraint must provide a non-negative integer.\n" +
                    "Expected: LONG  ->  Received: ARRAY", e.getMessage());
        }
    }

    @Test
    public void minPropertiesMustBeNonNegative() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("''"),
                    JsonParser.parse("{'minProperties': -5}")
            );
            fail("Previous method call should have thrown an exception.");
        } catch (InvalidSchemaException e) {
            assertEquals("Unexpected value for schema property: <base element>.minProperties\n" +
                    "Value must be >= 0.", e.getMessage());
        }
    }

    @Test
    public void minPropertiesMustValidateAgainstAnObject() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("''"),
                    JsonParser.parse("{'minProperties': 2}")
            );
            fail("Previous method call should have thrown an exception.");
        } catch (SchemaViolationException e) {
            assertEquals("Mismatched data type.\n" +
                    "Schema constraint violated: <base element>.minProperties\n" +
                    "This constraint can only be used against an object.", e.getMessage());
        }
    }

    @Test
    public void minPropertiesHasTooFew() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("{'key1':1}"),
                    JsonParser.parse("{'minProperties': 2}")
            );
            fail("Previous method call should have thrown an exception.");
        } catch (SchemaViolationException e) {
            assertEquals("Unexpected value.\n" +
                    "Schema constraint violated: <base element>.minProperties\n" +
                    "Object doesn't have enough properties to pass the constraint.", e.getMessage());
        }
    }

    @Test
    public void minPropertiesHasEnough() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("{'key1':1,'key2':2}"),
                JsonParser.parse("{'minProperties': 2}")
        ));
    }

    @Test
    public void maxPropertiesMustBeNumber() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("''"),
                    JsonParser.parse("{'maxProperties': []}")
            );
            fail("Previous method call should have thrown an exception.");
        } catch (InvalidSchemaException e) {
            assertEquals("Wrong type for schema property: <base element>.maxProperties\n" +
                    "Constraint must provide a non-negative integer.\n" +
                    "Expected: LONG  ->  Received: ARRAY", e.getMessage());
        }
    }

    @Test
    public void maxPropertiesMustBeNonNegative() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("''"),
                    JsonParser.parse("{'maxProperties': -5}")
            );
            fail("Previous method call should have thrown an exception.");
        } catch (InvalidSchemaException e) {
            assertEquals("Unexpected value for schema property: <base element>.maxProperties\n" +
                    "Value must be >= 0.", e.getMessage());
        }
    }

    @Test
    public void maxPropertiesMustValidateAgainstAnObject() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("''"),
                    JsonParser.parse("{'maxProperties': 2}")
            );
            fail("Previous method call should have thrown an exception.");
        } catch (SchemaViolationException e) {
            assertEquals("Mismatched data type.\n" +
                    "Schema constraint violated: <base element>.maxProperties\n" +
                    "This constraint can only be used against an object.", e.getMessage());
        }
    }

    @Test
    public void maxPropertiesHasTooMany() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("{'key1':1,'key2':2,'key3':3}"),
                    JsonParser.parse("{'maxProperties': 2}")
            );
            fail("Previous method call should have thrown an exception.");
        } catch (SchemaViolationException e) {
            assertEquals("Unexpected value.\n" +
                    "Schema constraint violated: <base element>.maxProperties\n" +
                    "Object has more properties than the constraint allows.", e.getMessage());
        }
    }

    @Test
    public void maxPropertiesHasEnough() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("{'key1':1,'key2':2}"),
                JsonParser.parse("{'maxProperties': 2}")
        ));
    }

    @Test
    public void propertiesMustBeObject() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("''"),
                    JsonParser.parse("{'properties': 2}")
            );
            fail("Previous method call should have thrown an exception.");
        } catch (InvalidSchemaException e) {
            assertEquals("Wrong type for schema property: <base element>.properties\n" +
                    "Expected: OBJECT  ->  Received: LONG", e.getMessage());
        }
    }

    @Test
    public void propertiesShouldIgnoreIfNotAnObject() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("''"),
                JsonParser.parse("{'properties': {'key1': {}}}")
        ));
    }

    @Test
    public void propertiesShouldIgnoreIfValueIsArray() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("[]"),
                JsonParser.parse("{'properties': {'key1': {}}}")
        ));
    }

    @Test
    public void ifPropertyDoesntExistInObjectThenContentsDontMatter() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("{}"),
                JsonParser.parse("{'properties': {'key1': 12312312312}}")
        ));
    }

    @Test
    public void ifPropertyDoesExistInObjectThenContentMustBeAnObject_withSpacesInKey() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("{'prop 1':1}"),
                    JsonParser.parse("{'properties': {'prop 1': 1}}")
            );
            fail("Previous method call should have thrown an exception.");
        } catch (InvalidSchemaException e) {
            assertEquals("Wrong type for schema property: <base element>.properties[`prop 1`]\n" +
                    "Expected: OBJECT  ->  Received: LONG", e.getMessage());
        }
    }

    @Test
    public void ifPropertyDoesExistInObjectThenContentMustBeAnObject_withBackSlashInKey() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("{'prop\\\\1':1}"),
                    JsonParser.parse("{'properties': {'prop\\\\1': 1}}")
            );
            fail("Previous method call should have thrown an exception.");
        } catch (InvalidSchemaException e) {
            assertEquals("Wrong type for schema property: <base element>.properties[`prop\\1`]\n" +
                    "Expected: OBJECT  ->  Received: LONG", e.getMessage());
        }
    }

    @Test
    public void ifPropertyDoesExistInObjectThenContentMustBeAnObject_withoutSpaceInKey() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("{'prop1':1}"),
                    JsonParser.parse("{'properties': {'prop1': 1}}")
            );
            fail("Previous method call should have thrown an exception.");
        } catch (InvalidSchemaException e) {
            assertEquals("Wrong type for schema property: <base element>.properties.prop1\n" +
                    "Expected: OBJECT  ->  Received: LONG", e.getMessage());
        }
    }

    @Test
    public void allPropertiesValidate() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("{'prop1':1, 'prop2':[]}"),
                JsonParser.parse("{'properties': {'prop1': {'const':1}, 'prop2': {'type':'array'}}}")
        ));
    }

    @Test
    public void notAllPropertiesValidate() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("{'prop1':1, 'prop2':123}"),
                    JsonParser.parse("{'properties': {'prop1': {'const':1}, 'prop2': {'type':'array'}}}")
            );
            fail("Previous method call should have thrown an exception.");
        } catch (SchemaViolationException e) {
            assertEquals("Mismatched data type.\n" +
                    "Schema constraint violated: properties.prop2.type\n" +
                    "Expected one of [ARRAY], got LONG.", e.getMessage());
        }
    }

    @Test
    public void propertyNamesSchemaMustBeObject() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("''"),
                    JsonParser.parse("{'propertyNames':[]}")
            );
            fail("Previous method call should have thrown an exception.");
        } catch (InvalidSchemaException e) {
            assertEquals("Wrong type for schema property: <base element>.propertyNames\n" +
                    "Schema value must be a valid sub-schema.\n" +
                    "Expected: OBJECT  ->  Received: ARRAY", e.getMessage());
        }
    }

    @Test
    public void propertyNamesShouldNotEvaluateIfNotObject() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("''"),
                JsonParser.parse("{'propertyNames':{'minLength':2}}")
        ));
    }

    @Test
    public void propertyNamesMayStillEvaluateIfArrayFail() {
        try {
            assertTrue(JsonSchemaEnforcer.validate(
                    JsonParser.parse("[1,2,3]"),
                    JsonParser.parse("{'propertyNames':{'oneOf':[{'const':1},{'const':2},{'const':3}]}}")
            ));
            fail("Previous method call should have thrown an exception.");
        } catch (SchemaViolationException e) {
            assertEquals("Unexpected value.\n" +
                    "Schema constraint violated: propertyNames.oneOf\n" +
                    "A provided JSON value failed to match any of the sub-schemas provided.", e.getMessage());
        }
    }

    @Test
    public void propertyNamesMayStillEvaluateIfArrayPass() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("[1,2,3]"),
                JsonParser.parse("{'propertyNames':{'oneOf':[{'const':'0'},{'const':'1'},{'const':'2'}]}}")
        ));
    }

    @Test
    public void propertyNamesForObjectFail() {
        try {
            assertTrue(JsonSchemaEnforcer.validate(
                    JsonParser.parse("{'hey':1}"),
                    JsonParser.parse("{'propertyNames':{'const':'hey1'}}")
            ));
            fail("Previous method call should have thrown an exception.");
        } catch (SchemaViolationException e) {
            assertEquals("Unexpected value.\n" +
                    "Schema constraint violated: propertyNames.const\n" +
                    "Value MUST match the schema's constant.", e.getMessage());
        }
    }

    @Test
    public void propertyNamesForObjectPass() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("{'hey1':1}"),
                JsonParser.parse("{'propertyNames':{'const':'hey1'}}")
        ));
    }

    @Test
    public void propertyNamesForObjectsWithEscapedCharacters() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("{'he\\\\y1':1}"),
                JsonParser.parse("{'propertyNames':{'const':'he\\\\y1'}}")
        ));
    }

    @Test
    public void additionalPropertiesWithTrueAllowsAnything() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("123456789"),
                JsonParser.parse("{'additionalProperties':true}")
        ));
    }

    @Test
    public void patternPropertiesMustBeObject() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("{}"),
                    JsonParser.parse("{'patternProperties': ''}")
            );
            fail("Previous method call should have thrown an exception.");
        } catch (InvalidSchemaException e) {
            assertEquals("Wrong type for schema property: <base element>.patternProperties\n" +
                    "Expected: OBJECT  ->  Received: STRING", e.getMessage());
        }
    }

    @Test
    public void patternPropertiesShouldIgnoreNonObjects() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("[]"),
                JsonParser.parse("{'patternProperties': {}}")
        ));
    }

    @Test
    public void patternPropertiesShouldWarnIfNotValidRegex() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("{}"),
                    JsonParser.parse("{'patternProperties': {'^[':{}}}")
            );
            fail("Previous method call should have thrown an exception.");
        } catch (InvalidSchemaException e) {
            assertEquals("Unexpected value for schema property: <base element>.patternProperties\n" +
                    "Key in patternProperties (^[) was not a valid regex.\n" +
                    "(Unclosed character class near index 1)", e.getMessage());
        }
    }

    @Test
    public void patternPropertiesHasNoMatchesDoesNothing() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("{'int':1}"),
                JsonParser.parse("{'patternProperties': {'^str':{'const':'5'}}}")
        ));
    }

    @Test
    public void patternPropertiesMatchesPass() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("{'int':1}"),
                JsonParser.parse("{'patternProperties': {" +
                        "'^str':{'const':'5'}," +
                        "'^int':{'const':1}" +
                        "}}"
                )
        ));
    }

    @Test
    public void patternPropertiesMatchesFails() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("{'int':1}"),
                    JsonParser.parse("{'patternProperties': {" +
                            "'^str':{'const':'5'}," +
                            "'^int':{'const':4}" +
                            "}}"
                    )
            );
            fail("Previous method call should have thrown an exception.");
        } catch (SchemaViolationException e) {
            assertEquals("Unexpected value.\n" +
                    "Schema constraint violated: patternProperties[`^int`].const\n" +
                    "Value MUST match the schema's constant.", e.getMessage());
        }
    }

    @Test
    public void patternPropertiesMultipleMatchesFails() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("{" +
                            "'int-1':1," +
                            "'int-2':4," +
                            "'str-1':'wow'," +
                            "'str-2':'owo'" +
                            "}"),
                    JsonParser.parse("{'patternProperties': {" +
                            "'^str':{'minLength':3, 'maxLength':3}," +
                            "'^int':{'oneOf':[{'const':1},{'const':9}]}" +
                            "}}"
                    )
            );
            fail("Previous method call should have thrown an exception.");
        } catch (SchemaViolationException e) {
            assertEquals("Unexpected value.\n" +
                    "Schema constraint violated: patternProperties[`^int`].oneOf\n" +
                    "A provided JSON value failed to match any of the sub-schemas provided.", e.getMessage());
        }
    }

    @Test
    public void patternPropertiesMultipleMatchesPasses() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("{" +
                        "'int-1':1," +
                        "'int-2':4," +
                        "'str-1':'wow'," +
                        "'str-2':'owo'" +
                        "}"),
                JsonParser.parse("{'patternProperties': {" +
                        "'^str':{'minLength':3, 'maxLength':3}," +
                        "'^int':{'oneOf':[{'const':1},{'const':4}]}" +
                        "}}"
                )
        ));
    }

    @Test
    public void patternPropertiesWithExtraKeyStillPasses() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("{" +
                        "'int-1':1," +
                        "'int-2':4," +
                        "'str-1':'wow'," +
                        "'str-2':'owo'" +
                        "}"),
                JsonParser.parse("{'patternProperties': {" +
                        "'^str':{'minLength':3, 'maxLength':3}," +
                        "'^int':{'oneOf':[{'const':1},{'const':4}]}," +
                        "'^other':{'const': 123}" +
                        "}}"
                )
        ));
    }

    @Test
    public void dependentMustBeObject() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("{}"),
                    JsonParser.parse("{'dependentRequired': []}")
            );
            fail("Previous method call should have thrown an exception.");
        } catch (InvalidSchemaException e) {
            assertEquals("Wrong type for schema property: <base element>.dependentRequired\n" +
                    "Expected: OBJECT  ->  Received: ARRAY", e.getMessage());
        }
    }

    @Test
    public void dependentShouldIgnoreNonObject() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("[]"),
                JsonParser.parse("{'dependentRequired': {'a':1}}")
        ));
    }

    @Test
    public void dependentsKeysShouldBeArrays() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("{'a':1}"),
                    JsonParser.parse("{'dependentRequired': {'a': {}}}")
            );
            fail("Previous method call should have thrown an exception.");
        } catch (InvalidSchemaException e) {
            assertEquals("Wrong type for schema property: <base element>.dependentRequired.a\n" +
                    "Dependents can only be specified in an ARRAY.\n" +
                    "Expected: ARRAY  ->  Received: OBJECT", e.getMessage());
        }
    }

    @Test
    public void dependentObjectToValidateHasNoMatching() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("{'a':1}"),
                JsonParser.parse("{'dependentRequired': {}}")
        ));
    }

    @Test
    public void dependentArrayContainsANonString() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("{'a':1}"),
                    JsonParser.parse("{'dependentRequired': {'a': [123]}}")
            );
            fail("Previous method call should have thrown an exception.");
        } catch (InvalidSchemaException e) {
            assertEquals("Wrong type for schema property: <base element>.dependentRequired.a\n" +
                    "Constraint must be the keys of dependent properties.\n" +
                    "Expected: STRING  ->  Received: LONG", e.getMessage());
        }
    }

    @Test
    public void dependentContainsAllRequiredDependents() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("{'a':1,'b':2,'c':3}"),
                JsonParser.parse("{'dependentRequired': {'a': ['b', 'c']}}")
        ));
    }

    @Test
    public void dependentContainsAllRequiredDependentsWithAdvancedKey() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("{'a':1,'b\\\"':2,'c':3}"),
                JsonParser.parse("{'dependentRequired': {'a': ['b\\\"', 'c']}}")
        ));
    }

    @Test
    public void dependentDoesntContainAllRequiredDependentsWithAdvancedKey() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("{'a':1,'b':2,'c':3}"),
                    JsonParser.parse("{'dependentRequired': {'a': ['b\\\"', 'c']}}")
            );
            fail("Previous method call should have thrown an exception.");
        } catch (SchemaViolationException e) {
            assertEquals("Missing property.\n" +
                    "Schema constraint violated: <base element>.dependentRequired.a[0]\n" +
                    "Missing dependent property (b\") required because property (a) present.", e.getMessage());
        }
    }

    @Test
    public void dependentDoesNotContainAllRequiredDependents() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("{'a':1,'b':2,'d':3}"),
                    JsonParser.parse("{'dependentRequired': {'a': ['b', 'c']}}")
            );
            fail("Previous method call should have thrown an exception.");
        } catch (SchemaViolationException e) {
            assertEquals("Missing property.\n" +
                    "Schema constraint violated: <base element>.dependentRequired.a[1]\n" +
                    "Missing dependent property (c) required because property (a) present.", e.getMessage());
        }
    }

    @Test
    public void additionalPropertiesCannotBeString() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("''"),
                    JsonParser.parse("{'additionalProperties': ''}")
            );
            fail("Previous method call should have thrown an exception.");
        } catch (InvalidSchemaException e) {
            assertEquals("Wrong type for schema property: <base element>.additionalProperties\n" +
                    "Expected one of [BOOLEAN, ARRAY], got STRING.", e.getMessage());
        }
    }
}
