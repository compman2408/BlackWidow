package com.example.blackwidow;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by Ryan on 11/20/2017.
 */

public class Utils {
    private static final String TAG = "Utils";

    private static Context _globalContext;
    private static IAsyncCommandCallback _cmdCallback;
    private static IAsyncZipFileProcessingCallback _zipCallback;
    public enum Executable {
        NCAT,
        NDIFF,
        NMAP,
        NPING
    }

    //==================================
    //          MISC METHODS
    //==================================
    public static void unzipFile(File zipFile, File targetDirectory) throws IOException {
        if (!zipFile.exists())
            throw new IOException("Provided zip file does not exist!");
        if (!targetDirectory.exists())
            targetDirectory.mkdirs();
        ZipInputStream zis = new ZipInputStream(
                new BufferedInputStream(new FileInputStream(zipFile)));
        try {
            ZipEntry ze;
            int count;
            byte[] buffer = new byte[8192];
            while ((ze = zis.getNextEntry()) != null) {
                File file = new File(targetDirectory, ze.getName());
                File dir = ze.isDirectory() ? file : file.getParentFile();
                if (!dir.isDirectory() && !dir.mkdirs())
                    throw new FileNotFoundException("Failed to ensure directory: " +
                            dir.getAbsolutePath());
                if (ze.isDirectory())
                    continue;
                FileOutputStream fout = new FileOutputStream(file);
                try {
                    while ((count = zis.read(buffer)) != -1)
                        fout.write(buffer, 0, count);
                } finally {
                    fout.close();
                }
            /* if time should be restored as well
            long time = ze.getTime();
            if (time > 0)
                file.setLastModified(time);
            */
            }
        } finally {
            zis.close();
        }
    }


    //==================================
    //       ASYNC MISC METHODS
    //==================================
    private static class AsyncResult {
        public AsyncResult(boolean isError, String msg) {
            _isError = isError;
            _msg = msg;
        }

        private boolean _isError = false;
        public boolean isError() {
            return _isError;
        }

        private String _msg = "";
        public String getMessage() {
            return _msg;
        }
    }
    //==================================
    //   ASYNC ZIP PROCESSING METHODS
    //==================================
    public static void ProcessZipFileAsync(Context context, String zipFileLocation, IAsyncZipFileProcessingCallback callback) {
        _globalContext = context;
        _zipCallback = callback;
        new AsyncZipFileProcessingTask().execute(zipFileLocation);
    }
    private static class ZipFileProcessProgress {
        public ZipFileProcessProgress(int progressValue, String msg) {
            _progressValue = progressValue;
            _msg = msg;
        }

        private int _progressValue = 0;
        public int getProgress() {
            return _progressValue;
        }

        private String _msg = "";
        public String getMessage() {
            return _msg;
        }
    }
    private static class AsyncZipFileProcessingTask extends AsyncTask<String, ZipFileProcessProgress, AsyncResult> {
        @Override
        protected AsyncResult doInBackground(String... fileToProcess) {
            String zipLocation = fileToProcess[0];
            Log.d(TAG, "Zip Async Processing --> file: '" + zipLocation + "'");
            AsyncResult output = null;

            // Once here the zip file should be copied
            // Go ahead and upzip it
            File file = new File(ActivityMain.binaryFileLocation);
            Log.d(TAG, "Checking if zip file has been extracted...");
            publishProgress(new ZipFileProcessProgress(15, "Checking for zip extraction..."));
            if (!file.exists()) {
                Log.d(TAG, "Files have not been extracted. Let's do that now...");
                try {
                    publishProgress(new ZipFileProcessProgress(25, "Extracting files from zip..."));
                    Log.d(TAG, "Unzipping zip file to local storage...");
                    Utils.unzipFile(new File(zipLocation), new File(ActivityMain.appDataDirectory));
                    Log.d(TAG, "File unzipped successfully!");
                } catch (IOException ex) {
                    Log.e(TAG, "IOException --> Unzipping File --> " + ex.getMessage());
                    output = new AsyncResult(true, "ERROR --> Unzipping File --> " + ex.getMessage());
                    ex.printStackTrace();
                } catch (Exception ex) {
                    Log.e(TAG, "Exception --> Unzipping File --> " + ex.getMessage());
                    output = new AsyncResult(true, "ERROR --> Unzipping File --> " + ex.getMessage());
                    ex.printStackTrace();
                }
                Log.d(TAG, "Done with file extraction!");

                publishProgress(new ZipFileProcessProgress(50, "Changing permissions..."));
                if (output == null) {
                    // Mark all files with read/write permissions using a recursive method
                    try {
                        Log.d(TAG, "Setting files with read/write permissions...");
                        // Mark all files with read/write permissions for everyone
                        markFilePermissionsRW(file);
                        Log.d(TAG, "Success!");
                    } catch (Exception ex) {
                        Log.d(TAG, "ERROR --> " + ex.getMessage());
                        output = new AsyncResult(true, "ERROR --> Setting permissions --> " + ex.getMessage());
                        ex.printStackTrace();
                    }
                    Log.d(TAG, "Done with read/write permissions!");
                }

                publishProgress(new ZipFileProcessProgress(80, "Changing executable permissions..."));
                if (output == null) {
                    // Mark executable files as executable
                    Log.d(TAG, "Now that that's done, let's make the executable files...well...executable");
                    int count = 0;
                    for (Map.Entry<Utils.Executable, String> item : ActivityMain.executableFileLocations.entrySet()) {
                        count++;
                        try {
                            Log.d(TAG, "Marking binary " + count + " of " + ActivityMain.executableFileLocations.size() + " as executable...");
                            Process proc = Runtime.getRuntime().exec("chmod 777 " + item.getValue());
                            proc.waitFor();
                            Log.d(TAG, "Binary executable!");
                        } catch (Exception ex) {
                            Log.d(TAG, "Error marking file as executable: " + ex.getMessage());
                            output = new AsyncResult(true, "ERROR --> Setting permissions --> " + ex.getMessage());
                            ex.printStackTrace();
                        }
                    }
                    Log.d(TAG, "Done with executable permissions!");
                }
                publishProgress(new ZipFileProcessProgress(100, "Done!"));
                if (output == null)
                    output = new AsyncResult(false, "Processing completed successfully!");
            } else {
                Log.d(TAG, "Looks like the zip file has been extracted already...");
                output = new AsyncResult(false, "Zip file already extracted!");
            }

            Log.d(TAG, "Zip Async Processing --> Done!");
            return output;
        }

        @Override
        protected void onProgressUpdate(ZipFileProcessProgress... values) {
            _zipCallback.ZipFileProcessingProgressUpdate(values[0].getProgress(), values[0].getMessage());
        }

        @Override
        protected void onPostExecute(AsyncResult asyncResult) {
            _zipCallback.ZipFileProcessingCompleted(asyncResult.isError(), asyncResult.getMessage());
        }

        private void markFilePermissionsRW(File fileLocation) throws IOException {
            if (!fileLocation.exists())
                throw new IOException("File/Directory does not exist!");

            if (fileLocation.isDirectory()) {
                File[] files = fileLocation.listFiles();
                Log.d(TAG, "Setting read/write permissions for " + String.valueOf(files.length) + " files in '" + fileLocation.getAbsolutePath() + "'");
                try {
                    for (int i = 0; i < files.length; i++) {
                        if (files[i].isDirectory()) {
                            markFilePermissionsRW(files[i]);
                        } else {
                            Log.v(TAG, "----- Setting read/write permissions for file '" + files[i].getAbsolutePath() + "'");
                            Process proc  = Runtime.getRuntime().exec("chmod 666 " + files[i].getAbsolutePath());
                            proc.waitFor();
                            Log.v(TAG, "----- Success!");
                        }
                    }
                } catch (Exception ex) {
                    Log.v(TAG, "XXXXX ERROR --> " + ex.getMessage());
                }
            } else {
                try {
                    Log.v(TAG, "----- Setting read/write permissions for file '" + fileLocation.getAbsolutePath() + "'");
                    Process proc  = Runtime.getRuntime().exec("chmod 666 " + fileLocation.getAbsolutePath());
                    proc.waitFor();
                    Log.v(TAG, "----- Success!");
                } catch (Exception ex) {
                    Log.v(TAG, "XXXXX ERROR --> " + ex.getMessage());
                }
            }
        }
    }
    //==================================
    //   ASYNC CMD EXECUTING METHODS
    //==================================
    public static void ExecuteCommandAsync(Context context, String cmdToExecute, IAsyncCommandCallback callback) {
        _globalContext = context;
        _cmdCallback = callback;
        new AsyncCommandTask().execute(cmdToExecute);
    }
    private static class AsyncCommandTask extends AsyncTask<String, Void, AsyncResult> {
        @Override
        protected AsyncResult doInBackground(String... cmdsToExecute) {
            String cmdToExecute = cmdsToExecute[0];
            Log.d(TAG, "Async Command --> cmd: '" + cmdToExecute + "'");
            AsyncResult output = null;

            StringBuffer cmdOutput = new StringBuffer();
            Log.d("COMMAND", cmdToExecute);
            Process scanProcess;
            try {
                ProcessBuilder pBuilder = new ProcessBuilder("su");
                pBuilder.redirectErrorStream(true);
                scanProcess = pBuilder.start();
                // Executes the command.
                //    Process process = Runtime.getRuntime().exec(cmdToExecute);
                //       Process process = Runtime.getRuntime().exec(cmdToExecute,null, null);
                DataOutputStream dos = new DataOutputStream(scanProcess.getOutputStream());
                dos.writeBytes(cmdToExecute + "\n");
                dos.flush();
                dos.writeBytes("exit\n");
                dos.flush();
                dos.close();//*/
                // Reads stdout.
                // NOTE: You can write to stdin of the command using
                //       process.getOutputStream().
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(scanProcess.getInputStream()));
                int read;
                char[] buffer = new char[4096];
                while ((read = reader.read(buffer)) > 0) {
                    cmdOutput.append(buffer, 0, read);
                }
                reader.close();

                // Waits for the command to finish.
                scanProcess.waitFor();

                //Toast.makeText(this, "Done!", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(_globalContext, "IOException ERROR: " + e.getMessage(), Toast.LENGTH_LONG).show();
                output = new AsyncResult(true, "IOException ERROR: " + e.getMessage());
                e.printStackTrace();
            } catch (InterruptedException e) {
                Toast.makeText(_globalContext, "InterruptedException ERROR: " + e.getMessage(), Toast.LENGTH_LONG).show();
                output = new AsyncResult(true, "InterruptedException ERROR: " + e.getMessage());
                e.printStackTrace();
            } catch (Exception e) {
                Toast.makeText(_globalContext, "Exception ERROR: " + e.getMessage(), Toast.LENGTH_LONG).show();
                output = new AsyncResult(true, "Exception ERROR: " + e.getMessage());
                e.printStackTrace();
            }
            Log.d("OUTPUT", cmdOutput.toString());

            if (output == null)
                output = new AsyncResult(false, cmdOutput.toString());
            Log.d(TAG, "Async Command --> Done!");
            return output;
        }

        @Override
        protected void onPostExecute(AsyncResult asyncResult) {
            _cmdCallback.CommandCompletedCallback(asyncResult.isError(), asyncResult.getMessage());
        }
    }
}
