package com.clumd.projects.javajson.core;

import org.junit.jupiter.api.Test;

public abstract class JsonTest {

    @Test
    public abstract void testDataType();

    @Test
    public abstract void simpleCreateFromString(); // this is actually just a pass-through to the JSONtape.

    @Test
    public abstract void simpleCreateFromMultilineString(); // this is actually just a pass-through to the JSONtape.

    @Test
    public abstract void containsTheEmptyDoubleQuoteIsTrue();

    @Test
    public abstract void containsNullIsFalse();

    @Test
    public abstract void containsAllTheseKeys();

    @Test
    public abstract void getKeys();

    @Test
    public abstract void getValue();

    @Test
    public abstract void getValues();

    @Test
    public abstract void getArray();

    @Test
    public abstract void getBoolean();

    @Test
    public abstract void getDouble();

    @Test
    public abstract void getLong();

    @Test
    public abstract void getString();

    @Test
    public abstract void getJSONObject();

    @Test
    public abstract void getAny();

    @Test
    public abstract void getValueAt();

    @Test
    public abstract void getDataTypeOf();

    @Test
    public abstract void getKeysOf();

    @Test
    public abstract void getValuesOf();

    @Test
    public abstract void getJSONObjectAt();

    @Test
    public abstract void getArrayAt();

    @Test
    public abstract void getBooleanAt();

    @Test
    public abstract void getDoubleAt();

    @Test
    public abstract void getLongAt();

    @Test
    public abstract void getStringAt();

    @Test
    public abstract void getAnyAt();

    @Test
    public abstract void testToString();

    @Test
    public abstract void toPrettyString();

    @Test
    public abstract void asString();

    @Test
    public abstract void asPrettyString();

    @Test
    public abstract void asPrettyStringWithDepth();

    @Test
    public abstract void asPrettyStringWithDepthAndIndent();

    @Test
    public abstract void testEquals();

    @Test
    public abstract void getHashCode();

    @Test
    public abstract void jsonIsConvertible();
}
