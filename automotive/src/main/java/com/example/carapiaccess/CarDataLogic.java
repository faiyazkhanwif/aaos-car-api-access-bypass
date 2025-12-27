package com.example.carapiaccess;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;

/**
 * Pure logic class: move all non-car-dependent function calls here.
 * onGetTemplate's "work" should be refactored into this class.
 */
public class CarDataLogic {

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


}
