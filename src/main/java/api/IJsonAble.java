package api;

import exceptions.JsonException;

public interface IJsonAble {

    IJson convertToJSON() throws JsonException;
}
