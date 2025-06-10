// ReflectUtil.java
package com.example.carapiaccess;

import android.util.Log;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Simplified reflection helper:
 *  • safeForName / safeGetField / safeGetStaticInt / safeGetStaticObject / safeGetInstanceObject
 *  • getDefaultValue: picks a dummy for any type
 *  • invokeMethod: logs PASS/FAIL
 */
public final class ReflectUtil {
    private static final String TAG = "ReflectUtil";

    public static Class<?> safeForName(String className) {
        try {
            Log.d(TAG, "====================================================================");
            Log.d(TAG, "Loading class: " + className);
            return Class.forName(className);
        } catch (Exception e) {
            Log.w(TAG, "Cannot load class: " + className, e);
            return null;
        }
    }

    public static Field safeGetField(Class<?> clazz, String fieldName) {
        if (clazz == null) return null;
        try {
            Field f = clazz.getDeclaredField(fieldName);
            f.setAccessible(true);
            return f;
        } catch (Exception e) {
            Log.w(TAG, "Field missing: " + clazz.getName() + "#" + fieldName, e);
            return null;
        }
    }

    public static int safeGetStaticInt(Field f, int def) {
        if (f == null) return def;
        try {
            return f.getInt(null);
        } catch (Exception e) {
            Log.w(TAG, "Cannot read static int: " + f.getName(), e);
            return def;
        }
    }

    public static Object safeGetStaticObject(Field f) {
        if (f == null) return null;
        try {
            return f.get(null);
        } catch (Exception e) {
            Log.w(TAG, "Cannot read static obj: " + f.getName(), e);
            return null;
        }
    }

    public static Object safeGetInstanceObject(Field f, Object receiver) {
        if (f == null || receiver == null) return null;
        try {
            return f.get(receiver);
        } catch (Exception e) {
            Log.w(TAG, "Cannot read inst obj: " + f.getName(), e);
            return null;
        }
    }

    /**
     * NEW: For any type, return a dummy “empty” value:
     *  - primitives → 0/false
     *  - String → ""
     *  - enum → first constant
     *  - array → zero-length
     *  - object → try no-arg ctor, else null
     */
    public static Object getDefaultValue(Class<?> t) {
        if (t == null) return null;
        if (!t.isPrimitive()) {
            if (t == String.class) return "";
            if (t.isEnum()) {
                Object[] c = t.getEnumConstants();
                return c != null && c.length>0 ? c[0] : null;
            }
            if (t.isArray()) return Array.newInstance(Objects.requireNonNull(t.getComponentType()), 0);
            // try ctor
            try {
                Constructor<?> ctor = t.getDeclaredConstructor();
                ctor.setAccessible(true);
                return ctor.newInstance();
            } catch (Exception e) {
                return null;
            }
        }
        // primitives
        if (t==boolean.class) return false;
        if (t==byte.class)    return (byte)0;
        if (t==char.class)    return (char)0;
        if (t==short.class)   return (short)0;
        if (t==int.class)     return 0;
        if (t==long.class)    return 0L;
        if (t==float.class)   return 0f;
        if (t==double.class)  return 0d;
        return null;
    }

    /**
     * NEW: Invoke m(receiver,args), log PASS or FAIL with details.
     */
    public static void invokeMethod(Method m, Object receiver, Object[] args, String label) {
        String sig = /*label + */ (receiver==null?"[static]":"["+receiver.getClass().getSimpleName()+"]")
                + "#" + m.getName() + "(";
        for (int i=0;i<args.length;i++) {
            sig += args[i]==null?"null":args[i].getClass().getSimpleName();
            if (i<args.length-1) sig += ",";
        }
        sig += ")";
        try {
            m.setAccessible(true);
            Object result = m.invoke(receiver, args);
            Log.d(TAG, "PASS: " + sig + " => " + (result==null?"null":result.toString()));
        } catch (Exception e) {
            Log.w(TAG, "FAIL: " + sig + " threw " + e.getClass().getSimpleName()
                    + ": " + e.getMessage());
        }
    }
}
