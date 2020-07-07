package api;

import exceptions.JSONException;

public interface IJSONAble {

    IJson convertToJSON() throws JSONException;
}
