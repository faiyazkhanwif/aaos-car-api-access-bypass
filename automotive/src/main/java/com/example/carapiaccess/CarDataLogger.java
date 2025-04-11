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
    private static final String LOG_FILE_NAME = "car_data_errors.log";
    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

    public static synchronized void logError(Context context, Exception e, String message) {
        try {
            File logFile = new File(context.getFilesDir(), LOG_FILE_NAME);
            String timestamp = DATE_FORMAT.format(new Date());
            String stackTrace = getStackTraceString(e);

            String logEntry = String.format(
                    "[%s] ERROR: %s\nException: %s\nStack Trace:\n%s\n\n",
                    timestamp,
                    message,
                    e.toString(),
                    stackTrace
            );

            // Append to log file
            try (FileOutputStream fos = new FileOutputStream(logFile, true)) {
                fos.write(logEntry.getBytes());
            }
        } catch (IOException ioException) {
            Log.e("CarDataLogger", "Failed to write to log file", ioException);
        }
    }

    private static String getStackTraceString(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
}