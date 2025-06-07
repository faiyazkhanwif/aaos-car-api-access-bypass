package com.example.carapiaccess;

import android.util.Log;

import java.lang.reflect.Field;

public final class ReflectUtil {
    private static final String TAG = "ReflectUtil";

    // Load a class
    public static Class<?> safeForName(String className) {
        try {
            Log.d(TAG, "Loading class: " + className);
            return Class.forName(className);
        } catch (ClassNotFoundException | LinkageError e) {
            Log.w(TAG, "Cannot load class: " + className, e);
            return null;
        }
    }

    //Look up field
    public static Field safeGetField(Class<?> clazz, String fieldName) {
        if (clazz == null) {
            Log.w(TAG, "safeGetField: null clazz for “" + fieldName + "”");
            return null;
        }
        try {
            Log.d(TAG, "Getting field: " + clazz.getName() + "#" + fieldName);
            Field f = clazz.getDeclaredField(fieldName);
            f.setAccessible(true);
            return f;
        } catch (NoSuchFieldException | SecurityException e) {
            Log.w(TAG, "Field not found/inaccessible: " + clazz.getName() + "#" + fieldName, e);
            return null;
        }
    }

    public static int safeGetStaticInt(Field f, int defaultValue) {
        if (f == null) return defaultValue;
        try {
            Log.d(TAG, "Reading static int: " + f.getDeclaringClass().getName() + "#" + f.getName());
            return f.getInt(null);
        } catch (IllegalAccessException | IllegalArgumentException e) {
            Log.w(TAG, "Cannot read static int: " + f.getName(), e);
            return defaultValue;
        }
    }

    public static Object safeGetStaticObject(Field f) {
        if (f == null) return null;
        try {
            Log.d(TAG, "Reading static object: " + f.getDeclaringClass().getName() + "#" + f.getName());
            return f.get(null);
        } catch (IllegalAccessException | IllegalArgumentException e) {
            Log.w(TAG, "Cannot read static object: " + f.getName(), e);
            return null;
        }
    }

    public static Object safeGetInstanceObject(Field f, Object receiver) {
        if (f == null || receiver == null) return null;
        try {
            Log.d(TAG, "Reading instance field: " +
                    receiver.getClass().getName() + "#" + f.getName());
            return f.get(receiver);
        } catch (IllegalAccessException | IllegalArgumentException e) {
            Log.w(TAG, "Cannot read instance field: " + f.getName(), e);
            return null;
        }
    }
}
