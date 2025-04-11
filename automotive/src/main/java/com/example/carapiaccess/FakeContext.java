package com.example.carapiaccess;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.car.Car;
import android.car.VehicleAreaType;
import android.car.hardware.CarPropertyConfig;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.car.app.CarContext;

import org.lsposed.hiddenapibypass.HiddenApiBypass;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;


public class FakeContext extends ContextWrapper {
    public FakeContext(Context base) { super(base); }

    @Override
    public int checkCallingOrSelfPermission(String permission) {
        return PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public int checkPermission(String permission, int pid, int uid) {
        return PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void enforcePermission(String permission, int pid, int uid, String message) {
        // Bypass enforcement
    }

    @Override
    public void enforceCallingOrSelfPermission(String permission, String message) {
        // Bypass enforcement
    }

    /* Works
    @Override
    public Object getSystemService(String name) {
        if (CarContext.CAR_SERVICE.equals(name)) {
            try {
                Class<?> carClass = Class.forName("android.car.Car");
                Method createCar = carClass.getMethod("createCar", Context.class, Handler.class,
                        long.class, Car.CarServiceLifecycleListener.class);
                return createCar.invoke(null, this, null, 0, null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return super.getSystemService(name);
    }
    */

    // Update getSystemService in FakeContext.java
    @Override
    public Object getSystemService(String name) {
        if (CarContext.CAR_SERVICE.equals(name)) {
            try {
                Class<?> carClass = Class.forName("android.car.Car");
                Method createCar = carClass.getMethod("createCar", Context.class, Handler.class,
                        long.class, Car.CarServiceLifecycleListener.class);

                // Create fake lifecycle listener
                Car.CarServiceLifecycleListener fakeListener = new Car.CarServiceLifecycleListener() {
                    @Override
                    public void onLifecycleChanged(Car car, boolean ready) {
                        // Force service to report as always ready
                        Log.d("FakeListener", "Spoofing service connection");
                    }
                };

                return createCar.invoke(null, this, null, 0, fakeListener);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return super.getSystemService(name);
    }
}