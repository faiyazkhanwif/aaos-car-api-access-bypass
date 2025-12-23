package com.example.carapiaccess;

import android.content.Context;

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
        final int TOTAL_SAMPLE_WINDOW_MS = 20_000; // longer default probe window (20s)
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

}
