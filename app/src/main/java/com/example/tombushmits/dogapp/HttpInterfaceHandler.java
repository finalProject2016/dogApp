package com.example.tombushmits.dogapp;

import org.json.JSONException;

/**
 * Created by tombushmits on 1/2/16.
 */
public interface HttpInterfaceHandler {
    public void postHandle(String result) throws JSONException;
    public void getHandle(String result)throws JSONException;
}
