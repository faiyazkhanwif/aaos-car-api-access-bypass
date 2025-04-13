package com.example.carapiaccess;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CarDataLogger {
    // In CarDataLogger.java
    File logDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);


    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

    public static synchronized void logError(Context context, Exception e, String message) {
        File logDirectory = context.getExternalFilesDir(null); // Use app's external files directory root

        // Fallback to internal storage if external is not available
        if (logDirectory == null) {
            logDirectory = context.getFilesDir();
            Log.d("CarDataLogger", "Using internal storage: " + logDirectory.getAbsolutePath());
        } else {
            Log.d("CarDataLogger", "Using external storage: " + logDirectory.getAbsolutePath());
        }

        // Ensure directory exists
        if (!logDirectory.exists() && !logDirectory.mkdirs()) {
            Log.e("CarDataLogger", "Failed to create log directory");
            return;
        }

        File logFile = new File(logDirectory, "car_data_errors.log");
        String timestamp = DATE_FORMAT.format(new Date());
        String stackTrace = getStackTraceString(e);
        String logEntry = String.format(
                "[%s] ERROR: %s\nException: %s\nStack Trace:\n%s\n\n",
                timestamp,
                message,
                e.toString(),
                stackTrace
        );

        try (FileOutputStream fos = new FileOutputStream(logFile, true)) {
            fos.write(logEntry.getBytes());
            Log.d("CarDataLogger", "Log entry written to: " + logFile.getAbsolutePath());
        } catch (IOException ioException) {
            Log.e("CarDataLogger", "Failed to write to log file: " + logFile, ioException);
        }
    }

    private static String getStackTraceString(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
}