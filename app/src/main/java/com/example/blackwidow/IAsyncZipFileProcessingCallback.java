package com.example.blackwidow;

/**
 * Created by Ryan on 11/21/2017.
 */

public interface IAsyncZipFileProcessingCallback {
    void ZipFileProcessingCompleted(boolean isError, String message);
    void ZipFileProcessingProgressUpdate(int progress, String message);
}
