package com.example.blackwidow;

/**
 * Created by Ryan on 11/21/2017.
 */

public interface IAsyncCommandCallback {
    void CommandCompletedCallback(boolean isError, String output);
}
