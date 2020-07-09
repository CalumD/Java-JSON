package api;

import exceptions.BuildException;

public interface IJsonBuilder extends IJSONAble {

    IJsonBuilder addBoolean(String path, boolean value) throws BuildException;

    IJsonBuilder addLong(String path, long value) throws BuildException;

    IJsonBuilder addDouble(String path, double value) throws BuildException;

    IJsonBuilder addString(String path, String value) throws BuildException;

    IJsonBuilder addBuilderBlock(String path, IJsonBuilder value) throws BuildException;

    IJsonBuilder addBuilderBlock(String path, IJson value) throws BuildException;

    IJsonBuilder convertFromJSON(IJson json);

    IJson build(); // change to .build()?
}
