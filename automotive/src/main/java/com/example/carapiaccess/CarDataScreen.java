package com.example.carapiaccess;

import static com.google.common.reflect.Reflection.getPackageName;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.MotionEvent;

//AndroidX imports
import androidx.annotation.NonNull;
import androidx.annotation.OptIn;

//import androidx.car.app.media.--- add all
//import androidx.car.app.--- add all
//import androidx.car.app.--- all classes --- add all
//import androidx.car.app.--- all packages --- add all



// update the method handling androidx.packages -> specify names
// update the method handling androidx.classes -> specify names
// update the method handling androidx

//import androidx.car.app.CarAppBinder;
import androidx.car.app.CarContext;
import androidx.car.app.IStartCarApp;
import androidx.car.app.Screen;
//import androidx.car.app.hardware.common.PropertyRequestProcessor;
import androidx.car.app.SessionInfo;
import androidx.car.app.activity.CarAppViewModel;
import androidx.car.app.activity.ErrorHandler;
import androidx.car.app.activity.LogTags;
import androidx.car.app.activity.ServiceDispatcher;
import androidx.car.app.model.Action;
import androidx.car.app.model.CarColor;
import androidx.car.app.model.constraints.ActionsConstraints;
import androidx.car.app.model.constraints.CarTextConstraints;
//import androidx.car.app.activity.renderer.surface.RemoteProxyInputConnection;
//import androidx.car.app.activity.ActivityLifecycleDelegate;
//import androidx.car.app.activity.CarAppViewModelFactory;
import androidx.car.app.annotations.ExperimentalCarApi;
//import androidx.car.app.utils.LogTags;
import androidx.car.app.notification.CarNotificationManager;
import androidx.car.app.hardware.CarHardwareManager;
import androidx.car.app.hardware.common.CarValue;
import androidx.car.app.hardware.common.CarZone;
import androidx.car.app.hardware.common.CarPropertyResponse;
import androidx.car.app.hardware.common.PropertyManager;
import androidx.car.app.hardware.common.OnCarPropertyResponseListener;
import androidx.car.app.hardware.climate.AutomotiveCarClimate;
import androidx.car.app.hardware.info.AutomotiveCarInfo;
import androidx.car.app.hardware.info.AutomotiveCarSensors;
import androidx.car.app.model.Pane;
import androidx.car.app.model.PaneTemplate;
import androidx.car.app.model.Row;
import androidx.car.app.model.Template;


import androidx.car.app.serialization.BundlerException;
import androidx.core.app.NotificationChannelCompat;
import androidx.core.app.NotificationChannelGroupCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.IconCompat;

//Android.Car Imports
//import android.car.ApiVersion;
//import android.car.CarVersion;
//import android.car.GsrComplianceType;
//import android.car.PlatformVersion;
//import android.car.PlatformVersionMismatchException;
//import android.car.hardware.property.AreaIdConfig;
//import android.car.hardware.property.EvChargeState;
//import android.car.hardware.property.EvRegenerativeBrakingState;
//import android.car.hardware.property.LocationCharacterization;
//import android.car.hardware.property.PropertyNotAvailableErrorCode;
//import android.car.input.CarInputManager;
//import android.car.remoteaccess.CarRemoteAccessManager;
//import android.car.remoteaccess.RemoteTaskClientRegistrationInfo;

//Reflection Imports

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;

public class CarDataScreen extends Screen {
    private static final String TAG = "CarDataScreen";
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Map<String, Integer> rowIndexMap = new HashMap<>();
    private final List<Row> rows = new ArrayList<>();

    // Hold real instances so we can call their methods
    private final Map<Class<?>,Object> instanceMap = new HashMap<>();

    private boolean dumped = false;
    private boolean systemInfoDumped = false;

    public CarDataScreen(@NonNull CarContext carContext) {
        super(carContext);
        initializeDefaultRows();
    }

    private void initializeDefaultRows() {
        addDynamicRow("STATUS","Init...");
        //addDynamicRow("PROGRESS","Discovering...");
    }

    @NonNull
    @Override
    public Template onGetTemplate() {
        //fetchAllCarProperties();
        //registerRealInstances();
        //dumpCarAppHierarchyAndroidX();
        //dumpCarHardwareHierarchyAndroid();
        //exercisePropertyRequestProcessor();
        //updateDynamicRow("STATUS", "Done in " + elapsed + " ms");
        if (!dumped) {
            updateDynamicRow("STATUS", "Dumping AndroidX hierarchy…");
            long start = System.currentTimeMillis();
            /*
            dumpCarAppHierarchyAndroidX();
            exercisePropertyRequestProcessor();
            */
            //exercisePropertyRequestProcessorToGenerateErrorLog();
            //fetchAllCarProperties();
            //dumpCarAppHierarchyComAndroid();

            //exerciseAutomotiveCarClimate();

            //dumpCarAppHierarchyAndroidX();

            //exerciseAutomotiveCarSensors();

            //exerciseAutomotiveCarInfo();

            //exerciseAutomotiveCarInfo();

            //fetchAllCarProperties();

            //exerciseCarConnection();

            //exerciseHostDispatcherNavigationRpc();

            //exerciseHostValidator();

            exerciseBaseCarAppActivityReflection();
            //exercisePropertyRequestProcessor();

            long elapsed = System.currentTimeMillis() - start;
            updateDynamicRow("STATUS", "Background task done in " + elapsed + " ms");
            Log.d(TAG, "dumpCarAppHierarchyAndroidX execution time: " + elapsed + " ms");
            dumped = true;
        }

        if (!systemInfoDumped) {
            dumpSystemImageDetails();
        }

        return buildDynamicTemplate();
    }



    // function to fetch vehicle properties - previous reflection
    @OptIn(markerClass = ExperimentalCarApi.class)
    @SuppressLint({"PrivateApi", "RestrictedApi"})
    private void fetchAllCarProperties() {
        try {
            //Dump androidx.car.app.hardware

            CarHardwareManager hardwareManager = getCarContext().getCarService(CarHardwareManager.class);
            PropertyManager propertyManager = getPropertyManager(hardwareManager);

            if (propertyManager == null) {
                updateDynamicRow("STATUS", "Failed to access PropertyManager");
                return;
            }

            // Dump methods/fields of PropertyManager class
            Class<?> pmClass = propertyManager.getClass();
            Log.d(TAG, "\n--- Inspecting class: " + pmClass.getName() + " ---");
            dumpClassDetails(pmClass);

            List<Integer> allProperties = getAllVehiclePropertyIds();
            if (allProperties.isEmpty()) {
                updateDynamicRow("STATUS", "No vehicle properties found");
                return;
            }

            Map<Integer, List<CarZone>> request = createGlobalRequest(allProperties);
            propertyManager.submitRegisterListenerRequest(
                    request,
                    0.1f,
                    new OnCarPropertyResponseListener() {
                        @Override
                        public void onCarPropertyResponses(@NonNull List<CarPropertyResponse<?>> responses) {
                            handlePropertyResponses(responses);
                        }
                    },
                    getCarContext().getMainExecutor()
            );
        } catch (Exception e) {
            updateDynamicRow("STATUS", "Error: " + e.getClass().getSimpleName());
            Log.e(TAG, "Reflection failed", e);
            CarDataLogger.logError(getCarContext(), e, "Reflection failed in fetchAllCarProperties");
        }
    }


    /*
    // old function for dump - working
    private void dumpCarAppHardwareHierarchy() {
        String basePkg = "androidx.car.app.hardware";
        Log.d(TAG, "\n=== Accessing package: " + basePkg + " ===");

        // Known subpackages
        String[] subPackages = {
                "common",
                "climate",
                "info"
        };

        for (String sub : subPackages) {
            String pkg = basePkg + "." + sub;
            Log.d(TAG, "====================================================================");
            Log.d(TAG, "====================================================================");
            Log.d(TAG, "====================================================================");
            Log.d(TAG, "\n--- Subpackage: " + pkg + " ---");

            // List of classes manually derived from imports
            String[] classNames;
            switch (sub) {
                case "common":
                    classNames = new String[]{
                            "androidx.car.app.hardware.common.CarValue",
                            "androidx.car.app.hardware.common.CarZone",
                            "androidx.car.app.hardware.common.CarPropertyResponse",
                            "androidx.car.app.hardware.common.PropertyManager",
                            "androidx.car.app.hardware.common.CarUnit",
                            "androidx.car.app.hardware.common.CarZoneAreaIdConstants",
                            "androidx.car.app.hardware.common.CarZoneUtils",
                            "androidx.car.app.hardware.common.GlobalCarZoneAreaIdConverter",
                            "androidx.car.app.hardware.common.SeatCarZoneAreaIdConverter",
                            "androidx.car.app.hardware.common.CarPropertyProfile",
                            "androidx.car.app.hardware.common.CarValueUtils",
                            "androidx.car.app.hardware.common.GetPropertyRequest",
                            "androidx.car.app.hardware.common.PropertyIdAreaId",
                            "androidx.car.app.hardware.common.PropertyUtils",
                            "androidx.car.app.hardware.common.OnCarPropertyResponseListener",
                            "androidx.car.app.hardware.common.OnCarDataAvailableListener",
                            "androidx.car.app.hardware.common.CarSetOperationStatusCallback",
                            "androidx.car.app.hardware.common.CarZoneAreaIdConverter"
                    };
                    break;
                case "climate":
                    classNames = new String[]{
                            "androidx.car.app.hardware.climate.AutomotiveCarClimate",
                            "androidx.car.app.hardware.climate.CabinTemperatureProfile",
                            "androidx.car.app.hardware.climate.CarClimate",
                            "androidx.car.app.hardware.climate.CarClimateFeature",
                            "androidx.car.app.hardware.climate.CarClimateProfileCallback",
                            "androidx.car.app.hardware.climate.CarClimateStateCallback",
                            "androidx.car.app.hardware.climate.CarZoneMappingInfoProfile",
                            "androidx.car.app.hardware.climate.ClimateProfileRequest",
                            "androidx.car.app.hardware.climate.ClimateStateRequest",
                            "androidx.car.app.hardware.climate.DefrosterProfile",
                            "androidx.car.app.hardware.climate.ElectricDefrosterProfile",
                            "androidx.car.app.hardware.climate.FanDirectionProfile",
                            "androidx.car.app.hardware.climate.FanSpeedLevelProfile",
                            "androidx.car.app.hardware.climate.HvacAcProfile",
                            "androidx.car.app.hardware.climate.HvacAutoModeProfile",
                            "androidx.car.app.hardware.climate.HvacAutoRecirculationProfile",
                            "androidx.car.app.hardware.climate.HvacDualModeProfile",
                            "androidx.car.app.hardware.climate.HvacMaxAcModeProfile",
                            "androidx.car.app.hardware.climate.HvacPowerProfile",
                            "androidx.car.app.hardware.climate.HvacRecirculationProfile",
                            "androidx.car.app.hardware.climate.MaxDefrosterProfile",
                            "androidx.car.app.hardware.climate.RegisterClimateStateRequest",
                            "androidx.car.app.hardware.climate.SeatTemperatureProfile",
                            "androidx.car.app.hardware.climate.SeatVentilationProfile",
                            "androidx.car.app.hardware.climate.SteeringWheelHeatProfile"
                    };
                    break;
                case "info":
                    classNames = new String[]{
                            "androidx.car.app.hardware.info.Accelerometer",
                            "androidx.car.app.hardware.info.AutomotiveCarInfo",
                            "androidx.car.app.hardware.info.AutomotiveCarSensors",
                            "androidx.car.app.hardware.info.CarHardwareLocation",
                            "androidx.car.app.hardware.info.CarInfo",
                            "androidx.car.app.hardware.info.CarSensors",
                            "androidx.car.app.hardware.info.Compass",
                            "androidx.car.app.hardware.info.EnergyLevel",
                            "androidx.car.app.hardware.info.EnergyProfile",
                            "androidx.car.app.hardware.info.EvStatus",
                            "androidx.car.app.hardware.info.Gyroscope",
                            "androidx.car.app.hardware.info.Mileage",
                            "androidx.car.app.hardware.info.Model",
                            "androidx.car.app.hardware.info.Speed",
                            "androidx.car.app.hardware.info.TollCard"
                    };
                    break;
                default:
                    classNames = new String[0];
            }

            // Log discovered classes
            Log.d(TAG, "Classes in " + pkg + ":");
            for (String fqcn : classNames) {
                Log.d(TAG, "  - " + fqcn);
            }

            // Inspect each class in detail
            for (String fqcn : classNames) {
                Class<?> cls = ReflectUtil.safeForName(fqcn);
                if (cls == null) {
                    Log.w(TAG, "Could not load class: " + fqcn);
                    continue;
                }
                Log.d(TAG, "\n>>> Inspecting class: " + fqcn + " <<<");
                dumpClassDetails(cls);
                executeClassMembers(cls);
            }
        }
    }
    */

    /**
     * Logs fields (with static values), methods, and annotations for a given class.
     */
    private void dumpClassDetails(Class<?> cls) {
        // 1) Annotations on the class
        Annotation[] classAnnos = cls.getAnnotations();
        if (classAnnos.length > 0) {
            Log.d(TAG, "Class Annotations:");
            for (Annotation a : classAnnos) {
                Log.d(TAG, "  @" + a.annotationType().getSimpleName());
            }
        } else {
            Log.d(TAG, "No class-level annotations.");
        }

        // 2) Fields
        Field[] fields = cls.getDeclaredFields();
        if (fields.length > 0) {
            Log.d(TAG, " Fields:");
            for (Field f : fields) {
                f.setAccessible(true);
                StringBuilder sb = new StringBuilder();
                sb.append("  - ").append(f.getType().getSimpleName()).append(" ").append(f.getName());
                // Annotations on the field
                Annotation[] fieldAnnos = f.getAnnotations();
                if (fieldAnnos.length > 0) {
                    sb.append(" annotations=[");
                    for (int i = 0; i < fieldAnnos.length; i++) {
                        sb.append(fieldAnnos[i].annotationType().getSimpleName());
                        if (i < fieldAnnos.length - 1) sb.append(", ");
                    }
                    sb.append("]");
                }
                // If static, attempt to read its value
                if ((f.getModifiers() & java.lang.reflect.Modifier.STATIC) != 0) {
                    Object val = null;
                    try {
                        val = f.get(null);
                    } catch (Exception ignored) { }
                    sb.append(" value=").append(val);
                }
                Log.d(TAG, sb.toString());
            }
        } else {
            Log.d(TAG, " No declared fields.");
        }

        // 3) Methods
        Method[] methods = cls.getDeclaredMethods();
        if (methods.length > 0) {
            Log.d(TAG, " Methods:");
            for (Method m : methods) {
                m.setAccessible(true);
                StringBuilder sig = new StringBuilder();
                sig.append("  - ").append(m.getReturnType().getSimpleName())
                        .append(" ").append(m.getName()).append("(");
                Class<?>[] params = m.getParameterTypes();
                for (int i = 0; i < params.length; i++) {
                    sig.append(params[i].getSimpleName());
                    if (i < params.length - 1) sig.append(", ");
                }
                sig.append(")");
                // Annotations on the method
                Annotation[] methodAnnos = m.getAnnotations();
                if (methodAnnos.length > 0) {
                    sig.append(" annotations=[");
                    for (int i = 0; i < methodAnnos.length; i++) {
                        sig.append(methodAnnos[i].annotationType().getSimpleName());
                        if (i < methodAnnos.length - 1) sig.append(", ");
                    }
                    sig.append("]");
                }
                Log.d(TAG, sig.toString());
            }
        } else {
            Log.d(TAG, " No declared methods.");
        }
    }

    @SuppressLint("RestrictedApi")
    private PropertyManager getPropertyManager(CarHardwareManager hardwareManager) throws Exception {
        Object rawCarInfo = hardwareManager.getCarInfo();
        if (!(rawCarInfo instanceof AutomotiveCarInfo)) {
            Log.e(TAG, "CarInfo returned is not AutomotiveCarInfo; got: " +
                    (rawCarInfo == null ? "null" : rawCarInfo.getClass().getName()));
            return null;
        }
        AutomotiveCarInfo carInfo = (AutomotiveCarInfo) rawCarInfo;

        // Fetch the private instance field "mPropertyManager"
        Field pmField = ReflectUtil.safeGetField(AutomotiveCarInfo.class, "mPropertyManager");
        if (pmField == null) {
            Log.e(TAG, "Field 'mPropertyManager' not found in AutomotiveCarInfo");
            return null;
        }

        Object fieldValue = ReflectUtil.safeGetInstanceObject(pmField, carInfo);
        if (!(fieldValue instanceof PropertyManager)) {
            Log.e(TAG, "mPropertyManager is not a PropertyManager; class=" +
                    (fieldValue == null ? "null" : fieldValue.getClass().getName()));
            return null;
        }
        return (PropertyManager) fieldValue;
    }

    @SuppressLint("RestrictedApi")
    private PropertyManager getSensorManager(CarHardwareManager hardwareManager) throws Exception {
        Object rawSensors = hardwareManager.getCarSensors();
        if (!(rawSensors instanceof AutomotiveCarSensors)) {
            Log.e(TAG, "CarSensors returned is not AutomotiveCarSensors; got: " +
                    (rawSensors == null ? "null" : rawSensors.getClass().getName()));
            return null;
        }
        AutomotiveCarSensors carSensors = (AutomotiveCarSensors) rawSensors;

        // Fetch the private instance field "mSensorManager"
        Field smField = ReflectUtil.safeGetField(AutomotiveCarSensors.class, "mPropertyManager");
        if (smField == null) {
            Log.e(TAG, "Field 'mSensorManager' not found in AutomotiveCarSensors");
            return null;
        }

        Object fieldValue = ReflectUtil.safeGetInstanceObject(smField, carSensors);
        if (!(fieldValue instanceof PropertyManager)) {
            Log.e(TAG, "mSensorManager is not a SensorManager; class=" +
                    (fieldValue == null ? "null" : fieldValue.getClass().getName()));
            return null;
        }
        return (PropertyManager) fieldValue;
    }

    @OptIn(markerClass = ExperimentalCarApi.class)
    @SuppressLint("RestrictedApi")
    private PropertyManager getClimatePropertyManager(CarHardwareManager hardwareManager) throws Exception {
        Object rawClimate = hardwareManager.getCarClimate();
        if (!(rawClimate instanceof AutomotiveCarClimate)) {
            Log.e(TAG, "CarClimate returned is not AutomotiveCarClimate; got: " +
                    (rawClimate == null ? "null" : rawClimate.getClass().getName()));
            return null;
        }
        AutomotiveCarClimate carClimate = (AutomotiveCarClimate) rawClimate;

        // Fetch the private instance field "mPropertyManager"
        Field pmField = ReflectUtil.safeGetField(AutomotiveCarClimate.class, "mPropertyManager");
        if (pmField == null) {
            Log.e(TAG, "Field 'mPropertyManager' not found in AutomotiveCarClimate");
            return null;
        }

        Object fieldValue = ReflectUtil.safeGetInstanceObject(pmField, carClimate);
        if (!(fieldValue instanceof PropertyManager)) {
            Log.e(TAG, "mPropertyManager is not a PropertyManager; class=" +
                    (fieldValue == null ? "null" : fieldValue.getClass().getName()));
            return null;
        }
        return (PropertyManager) fieldValue;
    }


    private List<Integer> getAllVehiclePropertyIds() throws Exception {
        List<Integer> propertyIds = new ArrayList<>();

        // Load VehiclePropertyIds class
        Class<?> vehicleIdsClass = ReflectUtil.safeForName("android.car.VehiclePropertyIds");
        if (vehicleIdsClass == null) {
            Log.e(TAG, "Could not find android.car.VehiclePropertyIds");
            throw new ClassNotFoundException("android.car.VehiclePropertyIds");
        }

        // Dump fields/methods of VehiclePropertyIds
        dumpClassDetails(vehicleIdsClass);

        // Enumerate all int constants
        for (Field field : vehicleIdsClass.getDeclaredFields()) {
            if (field.getType() != int.class) continue;
            int propId = ReflectUtil.safeGetStaticInt(field, -1);
            if (propId != -1) {
                propertyIds.add(propId);
                Log.d(TAG, "Discovered property: " + field.getName() + " = " + propId);
            }
        }

        // Patch PropertyUtils.PERMISSION_READ_PROPERTY
        Class<?> utilsClass = ReflectUtil.safeForName("androidx.car.app.hardware.common.PropertyUtils");
        if (utilsClass == null) {
            Log.e(TAG, "Could not find PropertyUtils");
            throw new ClassNotFoundException("androidx.car.app.hardware.common.PropertyUtils");
        }

        // Dump fields/methods of PropertyUtils
        dumpClassDetails(utilsClass);

        Field permField = ReflectUtil.safeGetField(utilsClass, "PERMISSION_READ_PROPERTY");
        if (permField == null) {
            Log.e(TAG, "Field 'PERMISSION_READ_PROPERTY' not found");
            throw new NoSuchFieldException("PERMISSION_READ_PROPERTY");
        }

        @SuppressWarnings("unchecked")
        SparseArray<String> permMap = (SparseArray<String>) ReflectUtil.safeGetStaticObject(permField);
        if (permMap == null) {
            Log.e(TAG, "PropertyUtils.PERMISSION_READ_PROPERTY was null");
            throw new IllegalStateException("PERMISSION_READ_PROPERTY is null");
        }

        String dummyPermission = "android.car.permission.CAR_VENDOR_EXTENSION";
        for (int propId : propertyIds) {
            if (permMap.get(propId) == null) {
                permMap.put(propId, dummyPermission);
                Log.d(TAG, "Patched permission for property: " + propId);
            }
        }

        return propertyIds;
    }

    @OptIn(markerClass = ExperimentalCarApi.class)
    private Map<Integer, List<CarZone>> createGlobalRequest(List<Integer> propertyIds) {
        Map<Integer, List<CarZone>> request = new HashMap<>();
        CarZone globalZone = new CarZone.Builder()
                .setRow(CarZone.CAR_ZONE_ROW_ALL)
                .setColumn(CarZone.CAR_ZONE_COLUMN_ALL)
                .build();

        for (Integer propId : propertyIds) {
            request.put(propId, Collections.singletonList(globalZone));
        }
        return request;
    }

    @SuppressLint({"RestrictedApi", "DefaultLocale"})
    private void handlePropertyResponses(@SuppressLint("RestrictedApi") List<CarPropertyResponse<?>> responses) {
        Map<Integer, Boolean> successMap = new HashMap<>();

        for (CarPropertyResponse<?> response : responses) {
            int propId = response.getPropertyId();
            boolean success = response.getStatus() == CarValue.STATUS_SUCCESS;
            successMap.put(propId, success);

            if (success) {
                updateDynamicRow(
                        "PROP_" + propId,
                        String.format("%s: %s", getPropertyName(propId), response.getValue())
                );
            }
        }

        long successCount = successMap.values().stream().filter(b -> b).count();
        updateDynamicRow(
                "STATUS",
                String.format("Success: %d/%d properties", successCount, successMap.size())
        );
    }

    private void addDynamicRow(String key, String text) {
        handler.post(() -> {
            synchronized (rows) {
                rows.add(new Row.Builder().setTitle(text).build());
                rowIndexMap.put(key, rows.size() - 1);
                invalidate();
            }
        });
    }

    private void updateDynamicRow(String key, String data) {
        handler.post(() -> {
            synchronized (rows) {
                Integer index = rowIndexMap.get(key);
                Row newRow = new Row.Builder().setTitle(data).build();

                if (index != null) {
                    rows.set(index, newRow);
                } else {
                    rows.add(newRow);
                    rowIndexMap.put(key, rows.size() - 1);
                }
                invalidate();
            }
        });
    }

    private PaneTemplate buildDynamicTemplate() {
        Pane.Builder paneBuilder = new Pane.Builder();
        synchronized (rows) {
            for (Row row : rows) {
                paneBuilder.addRow(row);
            }
        }
        return new PaneTemplate.Builder(paneBuilder.build())
                .setHeaderAction(Action.APP_ICON)
                .build();
    }

    private String getPropertyName(int propId) {
        Class<?> cls = ReflectUtil.safeForName("android.car.VehiclePropertyIds");
        if (cls == null) return "UNKNOWN_PROPERTY_" + propId;

        for (Field field : cls.getDeclaredFields()) {
            if (field.getType() != int.class) continue;
            int value = ReflectUtil.safeGetStaticInt(field, -1);
            if (value == propId) {
                return field.getName().replace("VEHICLE_PROPERTY_", "");
            }
        }
        return "UNKNOWN_PROPERTY_" + propId;
    }

/*
    //Dynamic Reflection Test
    private void executeClassMembers(Class<?> cls) {
        Log.d(TAG, "\n>>> Executing members of " + cls.getName() + " <<<");

        // 1) Fields
        for (Field f : cls.getDeclaredFields()) {
            f.setAccessible(true);
            boolean isStatic = Modifier.isStatic(f.getModifiers());
            String target = isStatic ? "[static]" : "[instance]";
            Object receiver = null;
            if (!isStatic) {
                // attempt to create instance
                receiver = ReflectUtil.getDefaultValue(cls);
                if (receiver == null) {
                    Log.w(TAG, "SKIP: cannot instantiate " + cls.getSimpleName() + " for field " + f.getName());
                    continue;
                }
            }
            try {
                Object val = f.get(receiver);
                Log.d(TAG, "FIELD PASS: " + target + " " + f.getName() + " => " + val);
            } catch (Exception e) {
                Log.w(TAG, "FIELD FAIL: " + target + " " + f.getName() +
                        " threw " + e.getClass().getSimpleName());
            }
        }

        // 2) Methods
        for (Method m : cls.getDeclaredMethods()) {
            m.setAccessible(true);
            boolean isStatic = Modifier.isStatic(m.getModifiers());
            Object receiver = isStatic ? null : ReflectUtil.getDefaultValue(cls);
            if (!isStatic && receiver == null) {
                Log.w(TAG, "SKIP: no instance for method " + m.getName());
                continue;
            }
            // build default args
            Class<?>[] pTypes = m.getParameterTypes();
            Object[] args = new Object[pTypes.length];
            for (int i = 0; i < pTypes.length; i++) {
                args[i] = ReflectUtil.getDefaultValue(pTypes[i]);
            }
            ReflectUtil.invokeMethod(m, receiver, args);
        }
    }
*/


    private String[] getClassListForAndroidX(String sub) {
        switch (sub) {
            /*
            case "mediaextensions":
                return new String[]{
                        "androidx.car.app.mediaextensions.MetadataExtras"
                };
            case "validation":
                return new String[]{
                        "androidx.car.app.validation.HostValidator"
                };
            case "versioning":
                return new String[]{
                        "androidx.car.app.versioning.CarAppApiLevels",
                        "androidx.car.app.versioning.CarAppApiLevel"
                };
            case "suggestion":
                return new String[]{
                        "androidx.car.app.suggestion.SuggestionManager",
                        "androidx.car.app.suggestion.ISuggestionHost",
                        "androidx.car.app.suggestion.model.Suggestion"
                };
            case "annotations":
                return new String[]{
                        "androidx.car.app.annotations.ExperimentalCarApi",
                        "androidx.car.app.annotations.CarProtocol",
                        "androidx.car.app.annotations.RequiresCarApi",
                        "androidx.car.app.annotations.KeepFields"
                };
            case "activity":
                return new String[]{
                        "androidx.car.app.activity.ActivityLifecycleDelegate",
                        "androidx.car.app.activity.BaseCarAppActivity",
                        "androidx.car.app.activity.CarAppActivity",
                        "androidx.car.app.activity.CarAppViewModel",
                        "androidx.car.app.activity.CarAppViewModelFactory",
                        "androidx.car.app.activity.ErrorHandler",
                        "androidx.car.app.activity.HostUpdateReceiver",
                        "androidx.car.app.activity.LauncherActivity",
                        "androidx.car.app.activity.LogTags",
                        "androidx.car.app.activity.ResultManagerAutomotive",
                        "androidx.car.app.activity.ServiceConnectionManager",
                        "androidx.car.app.activity.ServiceDispatcher",
                        // renderer
                        "androidx.car.app.activity.renderer.ICarAppActivity",
                        "androidx.car.app.activity.renderer.IInsetsListener",
                        "androidx.car.app.activity.renderer.IProxyInputConnection",
                        "androidx.car.app.activity.renderer.IRendererCallback",
                        "androidx.car.app.activity.renderer.IRendererService",
                        // renderer.surface
                        "androidx.car.app.activity.renderer.surface.ISurfaceControl",
                        "androidx.car.app.activity.renderer.surface.ISurfaceListener",
                        "androidx.car.app.activity.renderer.surface.LegacySurfacePackage",
                        "androidx.car.app.activity.renderer.surface.OnBackPressedListener",
                        "androidx.car.app.activity.renderer.surface.OnCreateInputConnectionListener",
                        "androidx.car.app.activity.renderer.surface.RemoteProxyInputConnection",
                        "androidx.car.app.activity.renderer.surface.SurfaceControlCallback",
                        "androidx.car.app.activity.renderer.surface.SurfaceHolderListener",
                        "androidx.car.app.activity.renderer.surface.SurfaceWrapper",
                        "androidx.car.app.activity.renderer.surface.SurfaceWrapperProvider",
                        "androidx.car.app.activity.renderer.surface.TemplateSurfaceView",
                        // ui
                        "androidx.car.app.activity.ui.ErrorMessageView",
                        "androidx.car.app.activity.ui.LoadingView"
                };
            case "serialization":
                return new String[]{
                        "androidx.car.app.serialization.Bundleable",
                        "androidx.car.app.serialization.BundlerException",
                        "androidx.car.app.serialization.Bundler"
                };
            case "utils":
                return new String[]{
                        "androidx.car.app.utils.CollectionUtils",
                        "androidx.car.app.utils.CommonUtils",
                        "androidx.car.app.utils.LogTags",
                        "androidx.car.app.utils.RemoteUtils",
                        "androidx.car.app.utils.StringUtils",
                        "androidx.car.app.utils.ThreadUtils"
                };
            case "navigation":
                return new String[]{
                        "androidx.car.app.navigation.NavigationManager",
                        "androidx.car.app.navigation.NavigationManagerCallback",
                        "androidx.car.app.navigation.INavigationHost",
                        "androidx.car.app.navigation.INavigationManager",
                        // navigation.model
                        "androidx.car.app.navigation.model.Destination",
                        "androidx.car.app.navigation.model.IPanModeListener",
                        "androidx.car.app.navigation.model.Lane",
                        "androidx.car.app.navigation.model.LaneDirection",
                        "androidx.car.app.navigation.model.Maneuver",
                        "androidx.car.app.navigation.model.MapController",
                        "androidx.car.app.navigation.model.MapTemplate",
                        "androidx.car.app.navigation.model.MapWithContentTemplate",
                        "androidx.car.app.navigation.model.MessageInfo",
                        "androidx.car.app.navigation.model.NavigationTemplate",
                        "androidx.car.app.navigation.model.PanModeDelegate",
                        "androidx.car.app.navigation.model.PanModeDelegateImpl",
                        "androidx.car.app.navigation.model.PanModeListener",
                        "androidx.car.app.navigation.model.PlaceListNavigationTemplate",
                        "androidx.car.app.navigation.model.RoutePreviewNavigationTemplate",
                        "androidx.car.app.navigation.model.RoutingInfo",
                        "androidx.car.app.navigation.model.Step",
                        "androidx.car.app.navigation.model.TravelEstimate",
                        "androidx.car.app.navigation.model.Trip"
                };
            case "messaging":
                return new String[]{
                        "androidx.car.app.messaging.MessagingServiceConstants",
                        "androidx.car.app.messaging.model.CarMessage",
                        "androidx.car.app.messaging.model.ConversationCallback",
                        "androidx.car.app.messaging.model.ConversationCallbackDelegate",
                        "androidx.car.app.messaging.model.ConversationItem",
                        "androidx.car.app.messaging.model.IConversationCallback"
                };
             */
            /*
            case "connection":
                return new String[]{
                        "androidx.car.app.connection.CarConnection",
                        "androidx.car.app.connection.CarConnectionTypeLiveData"
                };
            case "managers":
                return new String[]{
                        "androidx.car.app.managers.Manager",
                        "androidx.car.app.managers.ManagerFactory",
                        "androidx.car.app.managers.ManagerCache",
                        "androidx.car.app.managers.ResultManager"
                };
            case "hardware":
                return new String[]{
                        "androidx.car.app.hardware.CarHardwareManager",
                        "androidx.car.app.hardware.AutomotiveCarHardwareManager"
                };

            case "hardware.common":
                return new String[]{
                        "androidx.car.app.hardware.common.CarValue",
                        "androidx.car.app.hardware.common.CarZone",
                        "androidx.car.app.hardware.common.CarPropertyResponse",
                        "androidx.car.app.hardware.common.PropertyManager",
                        "androidx.car.app.hardware.common.CarUnit",
                        "androidx.car.app.hardware.common.CarZoneAreaIdConstants",
                        "androidx.car.app.hardware.common.CarZoneUtils",
                        "androidx.car.app.hardware.common.PropertyResponseCache",
                        "androidx.car.app.hardware.common.GlobalCarZoneAreaIdConverter",
                        "androidx.car.app.hardware.common.SeatCarZoneAreaIdConverter",
                        "androidx.car.app.hardware.common.CarPropertyProfile",
                        "androidx.car.app.hardware.common.CarValueUtils",
                        "androidx.car.app.hardware.common.GetPropertyRequest",
                        "androidx.car.app.hardware.common.PropertyIdAreaId",
                        "androidx.car.app.hardware.common.PropertyUtils",
                        "androidx.car.app.hardware.common.CarInternalError",
                        "androidx.car.app.hardware.common.OnCarPropertyResponseListener",
                        "androidx.car.app.hardware.common.OnCarDataAvailableListener",
                        "androidx.car.app.hardware.common.CarSetOperationStatusCallback",
                        "androidx.car.app.hardware.common.CarZoneAreaIdConverter",
                        "androidx.car.app.hardware.common.PropertyRequestProcessor"
                };
            case "navigation":
                return new String[]{
                        "androidx.car.app.navigation.NavigationManager",
                        "androidx.car.app.navigation.NavigationManagerCallback",
                        "androidx.car.app.navigation.INavigationHost",
                        "androidx.car.app.navigation.INavigationManager",
                        // navigation.model
                        "androidx.car.app.navigation.model.Destination",
                        "androidx.car.app.navigation.model.IPanModeListener",
                        "androidx.car.app.navigation.model.Lane",
                        "androidx.car.app.navigation.model.LaneDirection",
                        "androidx.car.app.navigation.model.Maneuver",
                        "androidx.car.app.navigation.model.MapController",
                        "androidx.car.app.navigation.model.MapTemplate",
                        "androidx.car.app.navigation.model.MapWithContentTemplate",
                        "androidx.car.app.navigation.model.MessageInfo",
                        "androidx.car.app.navigation.model.NavigationTemplate",
                        "androidx.car.app.navigation.model.PanModeDelegate",
                        "androidx.car.app.navigation.model.PanModeDelegateImpl",
                        "androidx.car.app.navigation.model.PanModeListener",
                        "androidx.car.app.navigation.model.PlaceListNavigationTemplate",
                        "androidx.car.app.navigation.model.RoutePreviewNavigationTemplate",
                        "androidx.car.app.navigation.model.RoutingInfo",
                        "androidx.car.app.navigation.model.Step",
                        "androidx.car.app.navigation.model.TravelEstimate",
                        "androidx.car.app.navigation.model.Trip"
                };
            case "notification":
                return new String[]{
                        "androidx.car.app.notification.CarAppExtender",
                        "androidx.car.app.notification.CarNotificationManager",
                        "androidx.car.app.notification.CarPendingIntent",
                        "androidx.car.app.notification.CarAppNotificationBroadcastReceiver"
                };
            case "model":
                return new String[]{
                        // core model
                        "androidx.car.app.model.Action",
                        "androidx.car.app.model.ActionStrip",
                        "androidx.car.app.model.Alert",
                        "androidx.car.app.model.AlertCallback",
                        "androidx.car.app.model.AlertCallbackDelegate",
                        "androidx.car.app.model.Badge",
                        "androidx.car.app.model.CarColor",
                        "androidx.car.app.model.CarIcon",
                        "androidx.car.app.model.CarIconSpan",
                        "androidx.car.app.model.CarLocation",
                        "androidx.car.app.model.CarSpan",
                        "androidx.car.app.model.CarText",
                        "androidx.car.app.model.ClickableSpan",
                        "androidx.car.app.model.Content",
                        "androidx.car.app.model.DateTimeWithZone",
                        "androidx.car.app.model.Distance",
                        "androidx.car.app.model.DistanceSpan",
                        "androidx.car.app.model.DurationSpan",
                        "androidx.car.app.model.ForegroundCarColorSpan",
                        "androidx.car.app.model.GridItem",
                        "androidx.car.app.model.GridTemplate",
                        "androidx.car.app.model.Header",
                        "androidx.car.app.model.IAlertCallback",
                        "androidx.car.app.model.IInputCallback",
                        "androidx.car.app.model.IOnCheckedChangeListener",
                        "androidx.car.app.model.IOnClickListener",
                        "androidx.car.app.model.IOnContentRefreshListener",
                        "androidx.car.app.model.IOnItemVisibilityChangedListener",
                        "androidx.car.app.model.IOnSelectedListener",
                        "androidx.car.app.model.ISearchCallback",
                        "androidx.car.app.model.ITabCallback",
                        "androidx.car.app.model.InputCallback",
                        "androidx.car.app.model.InputCallbackDelegate",
                        "androidx.car.app.model.Item",
                        "androidx.car.app.model.ItemList",
                        "androidx.car.app.model.ListTemplate",
                        "androidx.car.app.model.LongMessageTemplate",
                        "androidx.car.app.model.MessageTemplate",
                        "androidx.car.app.model.Metadata",
                        "androidx.car.app.model.ModelUtils",
                        "androidx.car.app.model.OnCheckedChangeDelegate",
                        // signin
                        "androidx.car.app.model.signin.InputSignInMethod",
                        "androidx.car.app.model.signin.PinSignInMethod",
                        "androidx.car.app.model.signin.ProviderSignInMethod",
                        "androidx.car.app.model.signin.SignInTemplate",
                        "androidx.car.app.model.signin.QRCodeSignInMethod",
                        // extra model classes
                        "androidx.car.app.model.Pane",
                        "androidx.car.app.model.PaneTemplate",
                        "androidx.car.app.model.Row",
                        "androidx.car.app.model.Template"
                };

            case "media":
                return new String[]{
                        "androidx.car.app.media.AutomotiveCarAudioRecord",
                        "androidx.car.app.media.CarAudioCallback",
                        "androidx.car.app.media.CarAudioCallbackDelegate",
                        "androidx.car.app.media.CarAudioRecord",
                        "androidx.car.app.media.ICarAudioCallback",
                        "androidx.car.app.media.IMediaPlaybackHost",
                        "androidx.car.app.media.MediaPlaybackManager",
                        "androidx.car.app.media.OpenMicrophoneRequest",
                        "androidx.car.app.media.OpenMicrophoneResponse"
                };

            case "hardware.info":
                return new String[]{
                        "androidx.car.app.hardware.info.Accelerometer",
                        "androidx.car.app.hardware.info.AutomotiveCarInfo",
                        "androidx.car.app.hardware.info.AutomotiveCarSensors",
                        "androidx.car.app.hardware.info.CarHardwareLocation",
                        "androidx.car.app.hardware.info.CarInfo",
                        "androidx.car.app.hardware.info.CarSensors",
                        "androidx.car.app.hardware.info.Compass",
                        "androidx.car.app.hardware.info.EnergyLevel",
                        "androidx.car.app.hardware.info.EnergyProfile",
                        "androidx.car.app.hardware.info.EvStatus",
                        "androidx.car.app.hardware.info.Gyroscope",
                        "androidx.car.app.hardware.info.Mileage",
                        "androidx.car.app.hardware.info.Model",
                        "androidx.car.app.hardware.info.Speed",
                        "androidx.car.app.hardware.info.TollCard"
                };

            case "hardware.climate":
                return new String[]{
                        "androidx.car.app.hardware.climate.AutomotiveCarClimate",
                        "androidx.car.app.hardware.climate.CabinTemperatureProfile",
                        "androidx.car.app.hardware.climate.CarClimate",
                        "androidx.car.app.hardware.climate.CarClimateFeature",
                        "androidx.car.app.hardware.climate.CarClimateProfileCallback",
                        "androidx.car.app.hardware.climate.CarClimateStateCallback",
                        "androidx.car.app.hardware.climate.CarZoneMappingInfoProfile",
                        "androidx.car.app.hardware.climate.ClimateProfileRequest",
                        "androidx.car.app.hardware.climate.ClimateStateRequest",
                        "androidx.car.app.hardware.climate.DefrosterProfile",
                        "androidx.car.app.hardware.climate.ElectricDefrosterProfile",
                        "androidx.car.app.hardware.climate.FanDirectionProfile",
                        "androidx.car.app.hardware.climate.FanSpeedLevelProfile",
                        "androidx.car.app.hardware.climate.HvacAcProfile",
                        "androidx.car.app.hardware.climate.HvacAutoModeProfile",
                        "androidx.car.app.hardware.climate.HvacAutoRecirculationProfile",
                        "androidx.car.app.hardware.climate.HvacDualModeProfile",
                        "androidx.car.app.hardware.climate.HvacMaxAcModeProfile",
                        "androidx.car.app.hardware.climate.HvacPowerProfile",
                        "androidx.car.app.hardware.climate.HvacRecirculationProfile",
                        "androidx.car.app.hardware.climate.MaxDefrosterProfile",
                        "androidx.car.app.hardware.climate.RegisterClimateStateRequest",
                        "androidx.car.app.hardware.climate.SeatTemperatureProfile",
                        "androidx.car.app.hardware.climate.SeatVentilationProfile",
                        "androidx.car.app.hardware.climate.SteeringWheelHeatProfile"
                };

            case "constraints":
                return new String[]{
                        "androidx.car.app.model.constraints.ActionsConstraints",
                        "androidx.car.app.model.constraints.CarColorConstraints",
                        "androidx.car.app.model.constraints.CarIconConstraints",
                        "androidx.car.app.model.constraints.CarTextConstraints",
                        "androidx.car.app.model.constraints.RowConstraints",
                        "androidx.car.app.model.constraints.RowListConstraints",
                        "androidx.car.app.model.constraints.TabContentsConstraints",
                        "androidx.car.app.model.constraints.TabsConstraints",
                        "androidx.car.app.constraints.ConstraintManager",
                        "androidx.car.app.constraints.IConstraintHost"
                };

            case "app":
                return new String[]{
                        "androidx.car.app.AppInfo",
                        "androidx.car.app.AppManager",
                        "androidx.car.app.CarAppBinder",
                        "androidx.car.app.CarAppMetadataHolderService",
                        "androidx.car.app.CarAppPermission",
                        "androidx.car.app.CarAppPermissionActivity",
                        "androidx.car.app.CarAppService",
                        "androidx.car.app.CarContext",
                        "androidx.car.app.CarToast",
                        "androidx.car.app.FailureResponse",
                        "androidx.car.app.HandshakeInfo",
                        "androidx.car.app.HostCall",
                        "androidx.car.app.HostDispatcher",
                        "androidx.car.app.HostException",
                        "androidx.car.app.HostInfo",
                        "androidx.car.app.IAppHost",
                        "androidx.car.app.IAppManager",
                        "androidx.car.app.ICarApp",
                        "androidx.car.app.ICarHost",
                        "androidx.car.app.IOnDoneCallback",
                        "androidx.car.app.IOnRequestPermissionsListener",
                        "androidx.car.app.IStartCarApp",
                        "androidx.car.app.ISurfaceCallback",
                        "androidx.car.app.OnDoneCallback",
                        "androidx.car.app.OnRequestPermissionsListener",
                        "androidx.car.app.OnScreenResultListener",
                        "androidx.car.app.Screen",
                        "androidx.car.app.ScreenManager",
                        "androidx.car.app.Session",
                        "androidx.car.app.SessionInfo",
                        "androidx.car.app.SessionInfoIntentEncoder",
                        "androidx.car.app.SurfaceCallback",
                        "androidx.car.app.SurfaceContainer"
                };
*/
            case "hardware.common":
                return new String[]{
                        "androidx.car.app.hardware.common.CarInternalError",
                        "androidx.car.app.hardware.common.PropertyUtils",
                        "androidx.car.app.hardware.common.GetPropertyRequest",
                        "androidx.car.app.hardware.common.PropertyIdAreaId",

                        /*

                        "androidx.car.app.hardware.common.CarValue",
                        "androidx.car.app.hardware.common.CarZone",
                        "androidx.car.app.hardware.common.CarPropertyResponse",
                        "androidx.car.app.hardware.common.PropertyManager",
                        "androidx.car.app.hardware.common.CarUnit",
                        "androidx.car.app.hardware.common.CarZoneAreaIdConstants",
                        "androidx.car.app.hardware.common.CarZoneUtils",
                        "androidx.car.app.hardware.common.PropertyResponseCache",
                        "androidx.car.app.hardware.common.GlobalCarZoneAreaIdConverter",
                        "androidx.car.app.hardware.common.SeatCarZoneAreaIdConverter",
                        "androidx.car.app.hardware.common.CarPropertyProfile",
                        "androidx.car.app.hardware.common.CarValueUtils",
                        "androidx.car.app.hardware.common.GetPropertyRequest",
                        "androidx.car.app.hardware.common.PropertyIdAreaId",
                        "androidx.car.app.hardware.common.PropertyUtils",
                        "androidx.car.app.hardware.common.CarInternalError",
                        "androidx.car.app.hardware.common.OnCarPropertyResponseListener",
                        "androidx.car.app.hardware.common.OnCarDataAvailableListener",
                        "androidx.car.app.hardware.common.CarSetOperationStatusCallback",
                        "androidx.car.app.hardware.common.CarZoneAreaIdConverter",
                        "androidx.car.app.hardware.common.PropertyRequestProcessor"

                        */
                };
            default:
                return new String[0];
        }
    }

    private void dumpCarAppHierarchyAndroidX() {
        String[] subs = {
                "app", "media", "messaging", "navigation", "model",
                "signin", "constraints", "activity", "annotations",
                "connection", "managers", "mediaextensions", "suggestion",
                "validation", "versioning", "serialization", "utils",
                "notification", "hardware", "hardware.common",
                "hardware.climate", "hardware.info"
        };
        for (String sub : subs) {
            String pkg = "androidx.car.app" + (sub.isEmpty() ? "" : "." + sub);
            Log.d(TAG, "====================================================================");
            Log.d(TAG, "--- Subpackage: " + pkg + " ---");
            String[] classNames = getClassListForAndroidX(sub);
            for (String fqcn : classNames) {
                Class<?> cls = ReflectUtil.safeForName(fqcn);
                if (cls == null) {
                    Log.w(TAG, "Could not load class: " + fqcn);
                    continue;
                }
                Log.d(TAG, "\n>>> Inspecting class: " + fqcn + " <<<");
                dumpClassDetails(cls);
                // FIRST: invoke on a dummy (empty) instance/args
                executeClassMembers(cls, "DUMMY ");
                // THEN: invoke on the real instance, if we have one
                // executeClassMembers(cls, "REAL  ");
            }
        }
    }

    //For android.car
    private String[] getClassListForAndroid(String sub) {
        switch (sub) {
            /*
            case "hardware":
                return new String[]{
                        "android.car.hardware.CarPropertyConfig",
                        "android.car.hardware.CarPropertyValue",
                        "android.car.hardware.CarSensorEvent",
                        "android.car.hardware.CarSensorManager"
                };
            */
            case "hardware.power":
                return new String[]{
                        "android.car.hardware.power.ICarPower",
                        "android.car.hardware.power.CarPowerManager",
                        "android.car.hardware.power.CarPowerPolicy",
                        "android.car.hardware.power.CarPowerPolicyFilter",
                        "android.car.hardware.power.PowerComponent"
                };
            /*
            case "hardware.property":
                return new String[]{
                        "android.car.hardware.property.AreaIdConfig",
                        "android.car.hardware.property.CarInternalErrorException",
                        "android.car.hardware.property.CarPropertyManager",
                        "android.car.hardware.property.EvChargeState",
                        "android.car.hardware.property.EvChargingConnectorType",
                        "android.car.hardware.property.EvRegenerativeBrakingState",
                        "android.car.hardware.property.LocationCharacterization",
                        "android.car.hardware.property.PropertyAccessDeniedSecurityException",
                        "android.car.hardware.property.PropertyNotAvailableAndRetryException",
                        "android.car.hardware.property.PropertyNotAvailableErrorCode",
                        "android.car.hardware.property.PropertyNotAvailableException",
                        "android.car.hardware.property.VehicleElectronicTollCollectionCardStatus",
                        "android.car.hardware.property.VehicleElectronicTollCollectionCardType"
                };
            case "content.pm":
                return new String[]{
                        "android.car.content.pm.CarPackageManager"
                };
            case "drivingstate":
                return new String[]{
                        "android.car.drivingstate.CarUxRestrictions",
                        "android.car.drivingstate.CarUxRestrictionsManager"
                };
            case "input":
                return new String[]{
                        "android.car.input.CarInputManager"
                };
            case "media":
                return new String[]{
                        "android.car.media.CarAudioManager",
                        "android.car.media.CarMediaIntents"
                };
            case "remoteaccess":
                return new String[]{
                        "android.car.remoteaccess.CarRemoteAccessManager",
                        "android.car.remoteaccess.RemoteTaskClientRegistrationInfo"
                };
            case "watchdog":
                return new String[]{
                        "android.car.watchdog.CarWatchdogManager",
                        "android.car.watchdog.IoOveruseStats",
                        "android.car.watchdog.PerStateBytes",
                        "android.car.watchdog.ResourceOveruseStats"
                };
            */
            default:
                return new String[0];
        }
    }
    private void dumpCarHardwareHierarchyAndroid() {
        String[] subs = {"hardware", "hardware.power", "hardware.property", "content.pm", "drivingstate", "input", "media", "remoteaccess", "watchdog"};
        for (String sub : subs) {
            String pkg = "android.car." + sub;
            Log.d(TAG, "====================================================================");
            Log.d(TAG, "====================================================================");
            Log.d(TAG, "====================================================================");
            Log.d(TAG, "\n--- Subpackage: " + pkg + " ---");

            String[] classNames = getClassListForAndroid(sub);
            for (String fqcn : classNames) {
                Class<?> cls = ReflectUtil.safeForName(fqcn);
                if (cls == null) {
                    Log.w(TAG, "Could not load class: " + fqcn);
                    continue;
                }
                Log.d(TAG, "\n>>> Inspecting class: " + fqcn + " <<<");
                dumpClassDetails(cls);

                // FIRST: invoke on a dummy (empty) instance/args
                executeClassMembers(cls, "DUMMY ");

                // THEN: invoke on the real instance, if we have one
                //executeClassMembers(cls, "REAL  ");
            }
        }
    }

    private void registerRealInstances() {
        try {
            // CarHardwareManager
            CarHardwareManager hwMgr = getCarContext()
                    .getCarService(CarHardwareManager.class);
            instanceMap.put(hwMgr.getClass(), hwMgr);

            // PropertyManager (hidden inside AutomotiveCarHardwareManager - AutomotiveCarInfo)
            @SuppressLint("RestrictedApi") PropertyManager pm = getPropertyManager(hwMgr);
            if (pm != null) {
                instanceMap.put(PropertyManager.class, pm);
            } else {
                Log.w(TAG, "registerRealInstances: PropertyManager is null");
            }

        } catch (Exception e) {
            Log.e(TAG, "Error registering real instances", e);
        }
    }


    private String[] getClassListForComAndroid(String sub) {
        switch (sub) {
            case "power":
                return new String[]{
                        //"androidx.car.app.hardware.info.Accelerometer",
                        "com.android.car.power.CarPowerManagementService"
                };
            default:
                return new String[0];
        }
    }

    private void dumpCarAppHierarchyComAndroid() {
        String[] subs = {
                "power"
        };
        for (String sub : subs) {
            String pkg = "com.android.car" + (sub.isEmpty() ? "" : "." + sub);
            Log.d(TAG, "====================================================================");
            Log.d(TAG, "--- Subpackage: " + pkg + " ---");
            String[] classNames = getClassListForComAndroid(sub);
            for (String fqcn : classNames) {
                Class<?> cls = ReflectUtil.safeForName(fqcn);
                if (cls == null) {
                    Log.w(TAG, "Could not load class: " + fqcn);
                    continue;
                }
                Log.d(TAG, "\n>>> Inspecting class: " + fqcn + " <<<");
                dumpClassDetails(cls);
                // FIRST: invoke on a dummy (empty) instance/args
                executeClassMembers(cls, "DUMMY ");
                // THEN: invoke on the real instance, if we have one
                // executeClassMembers(cls, "REAL  ");
            }
        }
    }


    //Takes a label to know whether this is DUMMY or REAL pass.
    private void executeClassMembers(Class<?> cls, String label) {
        Log.d(TAG, /*label +*/ "Executing members of " + cls.getName());

        // --- FIELDS ---
        for (Field f : cls.getDeclaredFields()) {
            f.setAccessible(true);
            boolean isStatic = Modifier.isStatic(f.getModifiers());

            Object receiver = isStatic
                    ? null
                    : instanceMap.getOrDefault(cls, ReflectUtil.getDefaultValue(cls));

            try {
                Object val = f.get(receiver);
                Log.d(TAG, "FIELD " /*+ label*/
                        + cls.getSimpleName() + "#" + f.getName() + " => " + val);
            } catch (Exception e) {
                Log.w(TAG, "FIELD " /*+ label*/
                        + cls.getSimpleName() + "#" + f.getName()
                        + " threw " + e.getClass().getSimpleName());
            }
        }

        // --- METHODS ---
        for (Method m : cls.getDeclaredMethods()) {
            m.setAccessible(true);
            boolean isStatic = Modifier.isStatic(m.getModifiers());

            // Build an args[] of defaults
            Class<?>[] pts = m.getParameterTypes();
            Object[] args = new Object[pts.length];
            for (int i = 0; i < pts.length; i++) {
                args[i] = ReflectUtil.getDefaultValue(pts[i]);
            }

            // Choose receiver: static=null, else real or dummy
            Object receiver = isStatic
                    ? null
                    : instanceMap.getOrDefault(cls, ReflectUtil.getDefaultValue(cls));

            ReflectUtil.invokeMethod(m, receiver, args, label);
        }
    }



    private void dumpSystemImageDetails() {
        addDynamicRow("IMG_NAME", "Device: " + Build.DEVICE + " (" + Build.MODEL + ")");
        addDynamicRow("BUILD_FINGERPRINT","Fingerprint: " + Build.FINGERPRINT);
        addDynamicRow("ANDROID_VERSION","Android Version: " + Build.VERSION.RELEASE + " (SDK " + Build.VERSION.SDK_INT + ")");
        addDynamicRow("BUILD_DATE", "Build Date: " + new Date(Build.TIME).toString());

        //App privileges & UID
        ApplicationInfo ai = getCarContext().getApplicationContext().getApplicationInfo();
        int uid = ai.uid;
        boolean isSystemApp = (ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
        addDynamicRow("APP_UID", "APP UID: " + String.valueOf(uid));
        addDynamicRow("SYSTEM_APP", isSystemApp ? "System App: yes" : "System App: no");

        //Declared vs. granted permissions
        try {
            PackageManager pm = getCarContext().getPackageManager();
            PackageInfo pi = pm.getPackageInfo(
                    getCarContext().getPackageName(),
                    PackageManager.GET_PERMISSIONS
            );

            String[] requested = pi.requestedPermissions;
            int[] flags = pi.requestedPermissionsFlags;
            if (requested != null && flags != null) {
                int grantedCount = 0;
                List<String> grantedList = new ArrayList<>();
                for (int i = 0; i < requested.length; i++) {
                    boolean granted = (flags[i] & PackageInfo.REQUESTED_PERMISSION_GRANTED) != 0;
                    if (granted) grantedList.add(requested[i]);
                    if (granted) grantedCount++;
                }
                addDynamicRow("PERMS_REQ", String.valueOf(requested.length));
                addDynamicRow("PERMS_GRANTED", grantedCount + " / " + requested.length);

                addDynamicRow(
                        "REQ_PERMS",
                        TextUtils.join(", ", requested)
                );
                addDynamicRow(
                        "GRANT_PERMS",
                        TextUtils.join(", ", grantedList)
                );
                Log.d(TAG, "GRANT_PERMS: " + TextUtils.join(", ", grantedList));
            } else {
                addDynamicRow("PERMS_REQ", "none");
            }
        } catch (PackageManager.NameNotFoundException e) {
            addDynamicRow("PERMS_ERROR", e.getMessage());
        }

        systemInfoDumped = true;
    }


    @SuppressLint("RestrictedApi")
    private void exercisePropertyRequestProcessorToGenerateErrorLog() {
        try {
            CarHardwareManager hardwareManager = getCarContext().getCarService(CarHardwareManager.class);
            PropertyManager pm = getPropertyManager(hardwareManager);

            //PropertyManager pm = (PropertyManager) instanceMap.get(PropertyManager.class);
            if (pm == null) {
                Log.w(TAG, "No PropertyManager registered; cannot exercise processor");
                return;
            }

            //Reflect out mPropertyRequestProcessor field
            Field procField = ReflectUtil.safeGetField(
                    pm.getClass(), "mPropertyRequestProcessor");
            Object processor = ReflectUtil.safeGetInstanceObject(procField, pm);
            if (processor == null) {
                Log.w(TAG, "PropertyRequestProcessor instance is null");
                return;
            }
            Class<?> prpClass = processor.getClass();
            Log.d(TAG, "Got PropertyRequestProcessor: " + prpClass.getName());

            Class<?> vehicleIds = ReflectUtil.safeForName("android.car.VehiclePropertyIds");
            Field fanDirField = ReflectUtil.safeGetField(vehicleIds, "HVAC_FAN_DIRECTION");
            int fanDirId = (fanDirField != null)
                    ? ReflectUtil.safeGetStaticInt(fanDirField, /*def=*/-1)
                    : -1;
            if (fanDirId < 0) {
                Log.w(TAG, "Cannot find HVAC_FAN_DIRECTION constant");
                return;
            }
            Log.d(TAG, "Using propertyId = HVAC_FAN_DIRECTION (" + fanDirId + ")");

            // 4) Build a List<PropertyIdAreaId> via PropertyUtils.getPropertyIdWithAreaIds(...)
            // Map<Integer,List<CarZone>> map = Collections.singletonMap(fanDirId, Collections.singletonList(CarZone.CAR_ZONE_GLOBAL));
            Class<?> carZoneCls = ReflectUtil.safeForName(
                    "androidx.car.app.hardware.common.CarZone");
            // get CarZone.CAR_ZONE_GLOBAL static
            Field globalZoneField = ReflectUtil.safeGetField(carZoneCls, "CAR_ZONE_GLOBAL");
            Object globalZone = ReflectUtil.safeGetStaticObject(globalZoneField);

            // Build a Map<Integer,List<CarZone>>
            java.util.HashMap<Object,Object> reqMap = new java.util.HashMap<>();
            reqMap.put(fanDirId,
                    java.util.Collections.singletonList(globalZone));

            // Invoke PropertyUtils.getPropertyIdWithAreaIds(map)
            Class<?> utilsCls = ReflectUtil.safeForName(
                    "androidx.car.app.hardware.common.PropertyUtils");
            Method getIdWithAreas = utilsCls.getDeclaredMethod(
                    "getPropertyIdWithAreaIds", Map.class);
            getIdWithAreas.setAccessible(true);
            @SuppressWarnings("unchecked")
            List<?> pidAidList =
                    (List<?>) getIdWithAreas.invoke(null, reqMap);
            Log.d(TAG, "Built PropertyIdAreaId list of size " + pidAidList.size());

            // 5) Create a dynamic proxy for OnGetPropertiesListener
            Class<?> onGetPropsListenerIface = ReflectUtil.safeForName(
                    "androidx.car.app.hardware.common.PropertyRequestProcessor$OnGetPropertiesListener");
            Object getPropsListener = Proxy.newProxyInstance(
                    onGetPropsListenerIface.getClassLoader(),
                    new Class<?>[]{onGetPropsListenerIface},
                    new InvocationHandler() {
                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args)
                                throws Throwable {
                            if ("onGetProperties".equals(method.getName())
                                    && args.length == 2) {
                                @SuppressWarnings("unchecked")
                                List<Object> values = (List<Object>) args[0];
                                @SuppressWarnings("unchecked")
                                List<Object> errors = (List<Object>) args[1];
                                Log.d(TAG, "fetchCarPropertyValues callback: "
                                        + "values=" + values.size()
                                        + ", errors=" + errors.size());
                            }
                            return null;
                        }
                    });

            // 6) Call fetchCarPropertyValues(requests, listener)
            Method fetchValues = prpClass.getDeclaredMethod(
                    "fetchCarPropertyValues", List.class, onGetPropsListenerIface);
            fetchValues.setAccessible(true);
            fetchValues.invoke(processor, pidAidList, getPropsListener);

            // 7) Similarly for fetchCarPropertyProfiles
            Class<?> onGetProfilesIface = ReflectUtil.safeForName(
                    "androidx.car.app.hardware.common.PropertyRequestProcessor$OnGetCarPropertyProfilesListener");
            Object getProfilesListener = Proxy.newProxyInstance(
                    onGetProfilesIface.getClassLoader(),
                    new Class<?>[]{onGetProfilesIface},
                    (proxy, method, args) -> {
                        if ("onGetCarPropertyProfiles".equals(method.getName())
                                && args.length == 1) {
                            @SuppressWarnings("unchecked")
                            List<Object> profiles = (List<Object>) args[0];
                            Log.d(TAG, "fetchCarPropertyProfiles callback: profiles="
                                    + profiles.size());
                        }
                        return null;
                    });
            Method fetchProfiles = prpClass.getDeclaredMethod(
                    "fetchCarPropertyProfiles", List.class, onGetProfilesIface);
            fetchProfiles.setAccessible(true);
            // call with singleton property list
            fetchProfiles.invoke(processor,
                    java.util.Collections.singletonList(fanDirId),
                    getProfilesListener);

            // 8) registerProperty(int, float)
            Method registerProp = prpClass.getDeclaredMethod(
                    "registerProperty", int.class, float.class);
            registerProp.setAccessible(true);
            registerProp.invoke(processor, fanDirId, /*sampleRate=*/1.0f);

            // 9) unregisterProperty(int)
            Method unregisterProp = prpClass.getDeclaredMethod(
                    "unregisterProperty", int.class);
            unregisterProp.setAccessible(true);
            unregisterProp.invoke(processor, fanDirId);

        } catch (InvocationTargetException ite) {
            Log.e(TAG, "Underlying method threw: ", ite.getTargetException());
        } catch (Exception e) {
            Log.e(TAG, "Error exercising PropertyRequestProcessor", e);
        }
    }



    @SuppressLint("RestrictedApi")
    private void exercisePropertyRequestProcessor() {
        try {
            //Obtain PropertyRequestProcessor
            CarHardwareManager hwMgr = getCarContext().getCarService(CarHardwareManager.class);
            PropertyManager pm = getPropertyManager(hwMgr);
            if (pm == null) {
                Log.w(TAG, "No PropertyManager; aborting.");
                return;
            }
            Field procField = ReflectUtil.safeGetField(pm.getClass(), "mPropertyRequestProcessor");
            Object processor = ReflectUtil.safeGetInstanceObject(procField, pm);
            if (processor == null) {
                Log.w(TAG, "PropertyRequestProcessor is null");
                return;
            }
            Class<?> prpClass = processor.getClass();

            //Prepare CarPropertyManager.getPropertyList(...) lookup
            Field carPropMgrField = ReflectUtil.safeGetField(prpClass, "mCarPropertyManager");
            Object carPropMgr = ReflectUtil.safeGetInstanceObject(carPropMgrField, processor);
            if (carPropMgr == null) {
                Log.w(TAG, "Cannot access mCarPropertyManager");
                return;
            }
            Method getListMethod = null;
            for (Method m : carPropMgr.getClass().getMethods()) {
                if ("getPropertyList".equals(m.getName()) && m.getParameterCount() == 1) {
                    getListMethod = m;
                    getListMethod.setAccessible(true);
                    break;
                }
            }
            if (getListMethod == null) {
                Log.w(TAG, "No single‑arg getPropertyList found");
                return;
            }

            //Scan all VehiclePropertyIds
            Class<?> vehicleIds = ReflectUtil.safeForName("android.car.VehiclePropertyIds");
            Field[] vidFields = vehicleIds.getDeclaredFields();
            Map<Field, List<?>> workingMap = new LinkedHashMap<>();

            for (Field f : vidFields) {
                if (f.getType() != int.class) continue;
                f.setAccessible(true);
                int propId = ReflectUtil.safeGetStaticInt(f, -1);
                if (propId < 0) continue;

                // build correct arg
                Class<?> paramType = getListMethod.getParameterTypes()[0];
                Object arg = paramType.getName().equals("android.util.ArraySet")
                        ? new android.util.ArraySet<>(Collections.singleton(propId))
                        : Collections.singleton(propId);

                List<?> configs;
                try {
                    configs = (List<?>) getListMethod.invoke(carPropMgr, arg);
                    Log.d(TAG, "getPropertyList(" + f.getName() + "=" + propId
                            + ") returned " + configs.size() + " configs");
                } catch (InvocationTargetException ite) {
                    Log.w(TAG, "Skipping " + f.getName() + ": " +
                            ite.getTargetException().getClass().getSimpleName());
                    continue;
                }

                if (!configs.isEmpty()) {
                    workingMap.put(f, configs);
                }
            }
            if (workingMap.isEmpty()) {
                Log.w(TAG, "No implemented properties found");
                return;
            }

            //For each supported property, run fetch and log values
            for (Map.Entry<Field, List<?>> entry : workingMap.entrySet()) {
                Field f = entry.getKey();
                int propId = ReflectUtil.safeGetStaticInt(f, -1);
                Log.i(TAG, "=== Property " + f.getName() + " (" + propId + ") ===");

                // build a PropertyIdAreaId list
                Class<?> carZoneCls = ReflectUtil.safeForName(
                        "androidx.car.app.hardware.common.CarZone");
                Object globalZone = ReflectUtil.safeGetStaticObject(
                        ReflectUtil.safeGetField(carZoneCls, "CAR_ZONE_GLOBAL"));
                Map<Integer, List<Object>> map = new HashMap<>();
                map.put(propId, Collections.singletonList(globalZone));
                Class<?> utilsCls = ReflectUtil.safeForName(
                        "androidx.car.app.hardware.common.PropertyUtils");
                Method getIdWithAreas = utilsCls.getDeclaredMethod("getPropertyIdWithAreaIds", Map.class);
                getIdWithAreas.setAccessible(true);
                @SuppressWarnings("unchecked")
                List<?> pidAidList = (List<?>) getIdWithAreas.invoke(null, map);

                // prepare listener
                Class<?> onGetPropsIface = ReflectUtil.safeForName(
                        "androidx.car.app.hardware.common.PropertyRequestProcessor$OnGetPropertiesListener");
                Object getPropsListener = Proxy.newProxyInstance(
                        onGetPropsIface.getClassLoader(),
                        new Class[]{onGetPropsIface},
                        (proxy, method, args) -> {
                            if ("onGetProperties".equals(method.getName()) && args.length == 2) {
                                @SuppressWarnings("unchecked")
                                List<Object> values = (List<Object>) args[0];
                                @SuppressWarnings("unchecked")
                                List<Object> errors = (List<Object>) args[1];
                                Log.d(TAG, "fetchCarPropertyValues - values=" + values.size()
                                        + ", errors=" + errors.size());
                                // print value
                                for (Object valObj : values) {
                                    try {
                                        Class<?> respCls = valObj.getClass();
                                        Method getId = respCls.getMethod("getPropertyId");
                                        Method getVal = respCls.getMethod("getValue");
                                        Method getArea = respCls.getMethod("getAreaId");
                                        Object id = getId.invoke(valObj);
                                        Object area = getArea.invoke(valObj);
                                        Object v = getVal.invoke(valObj);
                                        Log.d(TAG, String.format(
                                                "  [%s] area=%s - %s",
                                                id, area, v));
                                    } catch (NoSuchMethodException nsme) {
                                        // fallback to toString()
                                        Log.d(TAG, "  value: " + valObj);
                                    }
                                }
                                // print errors
                                for (Object errObj : errors) {
                                    Log.w(TAG, "  error: " + errObj);
                                }
                            }
                            return null;
                        });

                // invoke fetchCarPropertyValues
                Method fetchValues = prpClass.getDeclaredMethod(
                        "fetchCarPropertyValues", List.class, onGetPropsIface);
                fetchValues.setAccessible(true);
                fetchValues.invoke(processor, pidAidList, getPropsListener);

                // have to wait if asynchronous values
            }

            //invoke no-arg getPropertyList()
            try {
                Method getAllList = carPropMgr.getClass().getMethod("getPropertyList");
                getAllList.setAccessible(true);
                @SuppressWarnings("unchecked")
                List<?> allConfigs = (List<?>) getAllList.invoke(carPropMgr);
                Log.d(TAG, "getPropertyList() [no‑arg] returned " + allConfigs.size() + " configs");
            } catch (NoSuchMethodException e) {
                Log.w(TAG, "No no‑arg getPropertyList() on CarPropertyManager");
            }

        } catch (InvocationTargetException ite) {
            Log.e(TAG, "Underlying throw:", ite.getTargetException());
        } catch (Exception e) {
            Log.e(TAG, "Error exercising PropertyRequestProcessor", e);
        }
    }

    @SuppressLint("RestrictedApi")
    private void exerciseAutomotiveCarClimate() {
        try {
            //Obtain PropertyManager
            CarHardwareManager hwMgr =
                    getCarContext().getCarService(CarHardwareManager.class);
            PropertyManager pm = getPropertyManager(hwMgr);
            if (pm == null) {
                Log.w(TAG, "No PropertyManager; aborting.");
                return;
            }

            //Instantiate AutomotiveCarClimate(PropertyManager)
            Class<?> accCls = Class.forName(
                    "androidx.car.app.hardware.climate.AutomotiveCarClimate");
            Constructor<?> accCtor = accCls.getConstructor(PropertyManager.class);
            Object climate = accCtor.newInstance(pm);
            Log.d(TAG, "Created AutomotiveCarClimate");

            //Grab the static feature
            Field mapField = accCls.getDeclaredField("sFeatureToPropertyId");
            mapField.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<Integer, Integer> featureMap =
                    (Map<Integer, Integer>) mapField.get(null);

            // ========= PROFILE PHASE =========

            //Build CarClimateFeature[] for every feature
            Class<?> featBuilderCls = Class.forName(
                    "androidx.car.app.hardware.climate.CarClimateFeature$Builder");
            Constructor<?> featBuilderCtor = featBuilderCls.getConstructor(int.class);
            Method buildFeatM = featBuilderCls.getDeclaredMethod("build");
            buildFeatM.setAccessible(true);

            Class<?> featCls = Class.forName(
                    "androidx.car.app.hardware.climate.CarClimateFeature");
            List<Object> featList = new ArrayList<>();
            for (Integer feature : featureMap.keySet()) {
                Object fb = featBuilderCtor.newInstance(feature);
                featList.add(buildFeatM.invoke(fb));
            }
            Object featArray = Array.newInstance(featCls, featList.size());
            for (int i = 0; i < featList.size(); i++) {
                Array.set(featArray, i, featList.get(i));
            }

            //Build ClimateProfileRequest
            Class<?> profReqBuilderCls = Class.forName(
                    "androidx.car.app.hardware.climate.ClimateProfileRequest$Builder");
            Object profReqBuilder = profReqBuilderCls.getConstructor().newInstance();
            Method addProfFeatures = profReqBuilderCls.getDeclaredMethod(
                    "addClimateProfileFeatures", featArray.getClass());
            addProfFeatures.setAccessible(true);
            addProfFeatures.invoke(profReqBuilder, new Object[]{ featArray });
            Object profReq = profReqBuilderCls.getDeclaredMethod("build")
                    .invoke(profReqBuilder);
            Log.d(TAG, "Built ClimateProfileRequest with " + featList.size() + " features");

            //Profile callback: log each toString()
            Class<?> profCbIface = Class.forName(
                    "androidx.car.app.hardware.climate.CarClimateProfileCallback");
            Object profCb = Proxy.newProxyInstance(
                    profCbIface.getClassLoader(),
                    new Class[]{ profCbIface },
                    (proxy, method, args) -> {
                        if (args != null && args.length == 1) {
                            Log.i(TAG, String.format(
                                    "ClimateProfileCallback.%s - %s",
                                    method.getName(), args[0].toString()));
                        }
                        return null;
                    });

            //Invoke fetchClimateProfile(...)
            Executor executor = Runnable::run;
            Method fetchProfM = accCls.getDeclaredMethod(
                    "fetchClimateProfile",
                    Executor.class,
                    profReq.getClass(),
                    profCbIface);
            fetchProfM.setAccessible(true);
            fetchProfM.invoke(climate, executor, profReq, profCb);

            // ========= STATE PHASE (ALL FEATURES) =========

            //CarZone.CAR_ZONE_GLOBAL for every feature
            Class<?> carZoneCls = Class.forName(
                    "androidx.car.app.hardware.common.CarZone");
            Field globalZoneField = carZoneCls.getDeclaredField("CAR_ZONE_GLOBAL");
            globalZoneField.setAccessible(true);
            Object globalZone = globalZoneField.get(null);

            //addCarZones(CarZone...) varargs
            Method addZonesM = featBuilderCls.getDeclaredMethod(
                    "addCarZones", Array.newInstance(carZoneCls,0).getClass());
            addZonesM.setAccessible(true);

            //uild new array of CarClimateFeature with CAR_ZONE_GLOBAL
            List<Object> stateFeatList = new ArrayList<>();
            for (Integer feature : featureMap.keySet()) {
                Object fb = featBuilderCtor.newInstance(feature);
                Object czArr = Array.newInstance(carZoneCls, 1);
                Array.set(czArr, 0, globalZone);
                addZonesM.invoke(fb, new Object[]{ czArr });
                stateFeatList.add(buildFeatM.invoke(fb));
            }
            Object stateFeatArray = Array.newInstance(
                    featCls, stateFeatList.size());
            for (int i = 0; i < stateFeatList.size(); i++) {
                Array.set(stateFeatArray, i, stateFeatList.get(i));
            }

            //Build RegisterClimateStateRequest(true allFeatures=false builder)
            Class<?> regReqBuilderCls = Class.forName(
                    "androidx.car.app.hardware.climate.RegisterClimateStateRequest$Builder");
            Constructor<?> regCtor = regReqBuilderCls.getConstructor(boolean.class);
            Object regBuilder = regCtor.newInstance(false);
            Method addRegsM = regReqBuilderCls.getDeclaredMethod(
                    "addClimateRegisterFeatures", stateFeatArray.getClass());
            addRegsM.setAccessible(true);
            addRegsM.invoke(regBuilder, new Object[]{ stateFeatArray });
            Object regReq = regReqBuilderCls
                    .getDeclaredMethod("build")
                    .invoke(regBuilder);

            //State callback: dump CarValue.toString()
            Class<?> stateCbIface = Class.forName(
                    "androidx.car.app.hardware.climate.CarClimateStateCallback");
            Object stateCb = Proxy.newProxyInstance(
                    stateCbIface.getClassLoader(),
                    new Class[]{ stateCbIface },
                    (proxy, method, args) -> {
                        if (args != null && args.length == 1) {
                            Log.i(TAG, String.format(
                                    "StateCallback.%s - %s",
                                    method.getName(), args[0].toString()));
                        }
                        return null;
                    });

            //Invoke registerClimateStateCallback(...)
            Method regM = accCls.getDeclaredMethod(
                    "registerClimateStateCallback",
                    Executor.class,
                    regReq.getClass(),
                    stateCbIface);
            regM.setAccessible(true);
            regM.invoke(climate, executor, regReq, stateCb);
            Log.d(TAG, "Registered climate state callback for ALL features");

            //Unregister after 2s
            handler.postDelayed(() -> {
                try {
                    Method unregM = accCls.getDeclaredMethod(
                            "unregisterClimateStateCallback", stateCbIface);
                    unregM.setAccessible(true);
                    unregM.invoke(climate, stateCb);
                    Log.d(TAG, "Unregistered climate state callback");
                } catch (Exception ex) {
                    Log.w(TAG, "Error unregistering climate callback", ex);
                }
            }, 2_000);

        } catch (InvocationTargetException ite) {
            Log.e(TAG, "Underlying exception in AutomotiveCarClimate:",
                    ite.getTargetException());
        } catch (Exception e) {
            Log.e(TAG, "Error exercising AutomotiveCarClimate", e);
        }
    }

    @SuppressLint("RestrictedApi")
    private void exerciseAutomotiveCarSensors() {
        try {
            //Load and instantiate AutomotiveCarSensors
            Class<?> sensorsCls = Class.forName(
                    "androidx.car.app.hardware.info.AutomotiveCarSensors");
            Constructor<?> sensorsCtor = sensorsCls.getConstructor();
            Object sensors = sensorsCtor.newInstance();
            Log.d(TAG, "Created AutomotiveCarSensors");

            Executor executor = Runnable::run;

            //Find the listener interface
            Class<?> listenerIface = Class.forName(
                    "androidx.car.app.hardware.common.OnCarDataAvailableListener");

            //Build a Proxy that logs the callback data
            Object listenerProxy = Proxy.newProxyInstance(
                    listenerIface.getClassLoader(),
                    new Class[]{ listenerIface },
                    (proxy, method, args) -> {
                        if ("onCarDataAvailable".equals(method.getName())
                                && args.length == 1) {
                            Object dataObj = args[0];
                            Log.i(TAG, "SensorCallback."
                                    + dataObj.getClass().getSimpleName()
                                    + " - " + dataObj.toString());
                        }
                        return null;
                    });

            String[] sensorTypes = {
                    "Accelerometer",
                    "Gyroscope",
                    "Compass",
                    "CarHardwareLocation"
            };

            for (String type : sensorTypes) {
                String addName    = "add"    + type + "Listener";
                String removeName = "remove" + type + "Listener";

                //addXListener(int rate, Executor, OnCarDataAvailableListener)
                Method addM = sensorsCls.getMethod(
                        addName, int.class, Executor.class, listenerIface);
                addM.setAccessible(true);
                addM.invoke(sensors, /*rate=*/1, executor, listenerProxy);
                Log.d(TAG, addName + "(1, executor, listener) invoked");

                //removeXListener(OnCarDataAvailableListener)
                Method remM = sensorsCls.getMethod(removeName, listenerIface);
                remM.setAccessible(true);
                remM.invoke(sensors, listenerProxy);
                Log.d(TAG, removeName + "(listener) invoked");
            }

        } catch (InvocationTargetException ite) {
            Log.e(TAG, "Underlying exception in AutomotiveCarSensors:",
                    ite.getTargetException());
        } catch (Exception e) {
            Log.e(TAG, "Error exercising AutomotiveCarSensors", e);
        }
    }

/*
    @SuppressLint("RestrictedApi")
    private void exerciseAutomotiveCarInfo() {
        try {
            // 1) Get your hidden PropertyManager
            CarHardwareManager hwMgr =
                    getCarContext().getCarService(CarHardwareManager.class);
            PropertyManager pm = getPropertyManager(hwMgr);
            if (pm == null) {
                Log.w(TAG, "No PropertyManager; cannot exercise AutomotiveCarInfo");
                return;
            }

            // 2) Instantiate AutomotiveCarInfo(PropertyManager)
            Class<?> infoCls = Class.forName(
                    "androidx.car.app.hardware.info.AutomotiveCarInfo");
            Constructor<?> infoCtor = infoCls.getConstructor(PropertyManager.class);
            Object info = infoCtor.newInstance(pm);
            Log.d(TAG, "Created AutomotiveCarInfo");

            // 3) Prepare Executor and listener proxy
            Executor executor = Runnable::run;
            Class<?> listenerIface = Class.forName(
                    "androidx.car.app.hardware.common.OnCarDataAvailableListener");
            Object dataListener = Proxy.newProxyInstance(
                    listenerIface.getClassLoader(),
                    new Class[]{ listenerIface },
                    (proxy, method, args) -> {
                        if ("onCarDataAvailable".equals(method.getName())
                                && args.length == 1) {
                            Object payload = args[0];
                            Log.i(TAG, "InfoCallback."
                                    + payload.getClass().getSimpleName()
                                    + " - " + payload.toString());
                        }
                        return null;
                    });

            // 4) DYNAMICALLY invoke every fetchXxx(...) with (Executor, Listener)
            for (Method m : infoCls.getMethods()) {
                if (m.getName().startsWith("fetch")
                        && m.getParameterCount() == 2
                        && Executor.class.isAssignableFrom(m.getParameterTypes()[0])
                        && listenerIface.isAssignableFrom(m.getParameterTypes()[1])) {
                    m.setAccessible(true);
                    m.invoke(info, executor, dataListener);
                    Log.d(TAG, m.getName() + "(executor, listener) invoked");
                }
            }

            // 5) Now invoke every addXxxListener(...) & removeXxxListener(...)
            //    Methods look like addFooListener(Executor, Listener) or just (Listener)
            //    We pair them by name: add... ↔ remove...
            List<String> addNames = new ArrayList<>();
            for (Method m : infoCls.getMethods()) {
                String name = m.getName();
                if (name.startsWith("add") && name.endsWith("Listener")
                        && m.getParameterCount() == 2
                        && Executor.class.isAssignableFrom(m.getParameterTypes()[0])
                        && listenerIface.isAssignableFrom(m.getParameterTypes()[1])) {
                    addNames.add(name);
                }
            }
            for (String addName : addNames) {
                String removeName = addName.replaceFirst("^add", "remove");
                // invoke add
                Method addM = infoCls.getMethod(addName,
                        Executor.class, listenerIface);
                addM.setAccessible(true);
                addM.invoke(info, executor, dataListener);
                Log.d(TAG, addName + "(executor, listener) invoked");

                // invoke remove
                try {
                    Method remM = infoCls.getMethod(removeName, listenerIface);
                    remM.setAccessible(true);
                    remM.invoke(info, dataListener);
                    Log.d(TAG, removeName + "(listener) invoked");
                } catch (NoSuchMethodException nsme) {
                    Log.w(TAG, "No matching remove method for " + addName);
                }
            }

        } catch (InvocationTargetException ite) {
            Log.e(TAG, "Underlying exception in AutomotiveCarInfo:",
                    ite.getTargetException());
        } catch (Exception e) {
            Log.e(TAG, "Error exercising AutomotiveCarInfo", e);
        }
    }
*/

    @SuppressLint("RestrictedApi")
    private void exerciseAutomotiveCarInfo() {
        try {
            //Obtain hidden PropertyManager
            CarHardwareManager hwMgr =
                    getCarContext().getCarService(CarHardwareManager.class);
            PropertyManager pm = getPropertyManager(hwMgr);
            if (pm == null) {
                Log.w(TAG, "No PropertyManager; cannot exercise AutomotiveCarInfo");
                return;
            }

            //Instantiate AutomotiveCarInfo(PropertyManager)
            Class<?> infoCls = Class.forName(
                    "androidx.car.app.hardware.info.AutomotiveCarInfo");
            Constructor<?> infoCtor = infoCls.getConstructor(PropertyManager.class);
            Object info = infoCtor.newInstance(pm);
            Log.d(TAG, "Created AutomotiveCarInfo");

            Executor executor = Runnable::run;

            //Build OnCarDataAvailableListener<T> proxy
            Class<?> listenerIface = Class.forName(
                    "androidx.car.app.hardware.common.OnCarDataAvailableListener");
            Object dataListener = Proxy.newProxyInstance(
                    listenerIface.getClassLoader(),
                    new Class[]{ listenerIface },
                    new InvocationHandler() {
                        // Stable identity for equals/hashCode
                        private final int identityHash = System.identityHashCode(this);

                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args)
                                throws Throwable {
                            String name = method.getName();

                            // equals(Object)
                            if ("equals".equals(name) && args != null && args.length == 1) {
                                return proxy == args[0];
                            }
                            // hashCode()
                            if ("hashCode".equals(name) && (args == null || args.length == 0)) {
                                return identityHash;
                            }
                            // toString()
                            if ("toString".equals(name) && (args == null || args.length == 0)) {
                                return "OnCarDataAvailableListenerProxy@" + identityHash;
                            }
                            // onCarDataAvailable(T data)
                            if ("onCarDataAvailable".equals(name)
                                    && args != null && args.length == 1) {
                                Object payload = args[0];
                                Log.i(TAG, "InfoCallback."
                                        + payload.getClass().getSimpleName()
                                        + " - " + payload.toString());
                                return null;
                            }
                            // default no‑op
                            return null;
                        }
                    });

            //Dynamically invoke all fetchXxx(executor, listener) methods
            for (Method m : infoCls.getMethods()) {
                if (m.getName().startsWith("fetch")
                        && m.getParameterCount() == 2
                        && Executor.class.isAssignableFrom(m.getParameterTypes()[0])
                        && listenerIface.isAssignableFrom(m.getParameterTypes()[1])) {
                    m.setAccessible(true);
                    m.invoke(info, executor, dataListener);
                    Log.d(TAG, m.getName() + "(executor, listener) invoked");
                }
            }

            // schedule removeXxxListener(listener) after 2 seconds
            List<String> addNames = new ArrayList<>();
            for (Method m : infoCls.getMethods()) {
                String nm = m.getName();
                if (nm.startsWith("add") && nm.endsWith("Listener")
                        && m.getParameterCount() == 2
                        && Executor.class.isAssignableFrom(m.getParameterTypes()[0])
                        && listenerIface.isAssignableFrom(m.getParameterTypes()[1])) {
                    addNames.add(nm);
                }
            }
            for (String addName : addNames) {
                String removeName = addName.replaceFirst("^add", "remove");

                // invoke addXxxListener(executor, listener)
                Method addM = infoCls.getMethod(addName, Executor.class, listenerIface);
                addM.setAccessible(true);
                addM.invoke(info, executor, dataListener);
                Log.d(TAG, addName + "(executor, listener) invoked");

                // schedule removeXxxListener(listener) after 2 seconds
                handler.postDelayed(() -> {
                    try {
                        Method remM = infoCls.getMethod(removeName, listenerIface);
                        remM.setAccessible(true);
                        remM.invoke(info, dataListener);
                        Log.d(TAG, removeName + "(listener) invoked");
                    } catch (NoSuchMethodException nsme) {
                        Log.w(TAG, "No matching remove method for " + addName);
                    } catch (Exception ex) {
                        Log.e(TAG, "Error during " + removeName, ex);
                    }
                }, 2_000);
            }

        } catch (InvocationTargetException ite) {
            Log.e(TAG, "Underlying exception in AutomotiveCarInfo:",
                    ite.getTargetException());
        } catch (Exception e) {
            Log.e(TAG, "Error exercising AutomotiveCarInfo", e);
        }
    }

    @SuppressLint("RestrictedApi")
    private void exerciseAutomotiveCarAudioRecord() {
        try {
            // 1) Load and instantiate AutomotiveCarAudioRecord(CarContext)
            Class<?> audioRecCls = Class.forName(
                    "androidx.car.app.media.AutomotiveCarAudioRecord");
            Constructor<?> audioRecCtor = audioRecCls.getConstructor(CarContext.class);
            Object audioRec = audioRecCtor.newInstance(getCarContext());
            Log.d(TAG, "Created AutomotiveCarAudioRecord");

            // 2) Build a CarAudioCallback proxy to log callback events
            Class<?> carAudioCbIface = Class.forName(
                    "androidx.car.app.media.CarAudioCallback");
            Object carAudioCb = Proxy.newProxyInstance(
                    carAudioCbIface.getClassLoader(),
                    new Class[]{ carAudioCbIface },
                    (proxy, method, args) -> {
                        Log.i(TAG, "CarAudioCallback." + method.getName()
                                + Arrays.toString(args));
                        return null;
                    });

            // 3) Build an OpenMicrophoneResponse via its Builder(CarAudioCallback)
            Class<?> obrBuilderCls = Class.forName(
                    "androidx.car.app.media.OpenMicrophoneResponse$Builder");
            Constructor<?> obrBuilderCtor = obrBuilderCls.getConstructor(carAudioCbIface);
            Object obrBuilder = obrBuilderCtor.newInstance(carAudioCb);
            Method buildObr = obrBuilderCls.getMethod("build");
            Object openMicResp = buildObr.invoke(obrBuilder);
            Log.d(TAG, "Built OpenMicrophoneResponse");

            // 4) Call startRecordingInternal(OpenMicrophoneResponse)
            Method startRecM = audioRecCls.getDeclaredMethod(
                    "startRecordingInternal", openMicResp.getClass());
            startRecM.setAccessible(true);
            startRecM.invoke(audioRec, openMicResp);
            Log.d(TAG, "startRecordingInternal(...) invoked");

            // 5) Wait for 5 seconds to capture audio
            Log.d(TAG, "Recording for 5 seconds…");
            Thread.sleep(5_000);

            // 6) Prepare buffer for readInternal(...)
            Class<?> baseCarAudioRecCls = Class.forName(
                    "androidx.car.app.media.CarAudioRecord");
            Field bufField = baseCarAudioRecCls.getDeclaredField("AUDIO_CONTENT_BUFFER_SIZE");
            bufField.setAccessible(true);
            int bufSize = bufField.getInt(null);
            byte[] buffer = new byte[bufSize];

            // 7) Call readInternal(byte[], int, int)
            Method readInternalM = audioRecCls.getDeclaredMethod(
                    "readInternal", byte[].class, int.class, int.class);
            readInternalM.setAccessible(true);
            int bytesRead = (Integer) readInternalM.invoke(
                    audioRec, buffer, 0, bufSize);
            Log.i(TAG, "readInternal returned bytesRead=" + bytesRead);

            // 8) Call stopRecordingInternal()
            Method stopRecM = audioRecCls.getDeclaredMethod("stopRecordingInternal");
            stopRecM.setAccessible(true);
            stopRecM.invoke(audioRec);
            Log.d(TAG, "stopRecordingInternal() invoked");

            // 9) Dump first few bytes
            int toDump = Math.min(bytesRead, 16);
            StringBuilder sb = new StringBuilder("First " + toDump + " bytes: ");
            for (int i = 0; i < toDump; i++) {
                sb.append(buffer[i]).append(" ");
            }
            Log.i(TAG, sb.toString());

        } catch (InvocationTargetException ite) {
            Log.e(TAG, "Underlying exception in AutomotiveCarAudioRecord:",
                    ite.getTargetException());
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            Log.e(TAG, "Interrupted while waiting during recording", ie);
        } catch (Exception e) {
            Log.e(TAG, "Error exercising AutomotiveCarAudioRecord", e);
        }
    }


    @SuppressLint("RestrictedApi")
    private void exerciseConversationCallbackDelegateImpl() {
        try {
            // 1) Load the delegate class
            Class<?> delegateCls = Class.forName(
                    "androidx.car.app.messaging.model.ConversationCallbackDelegateImpl");

            // 2) Prepare a dynamic ConversationCallback proxy
            Class<?> convoCbIface = Class.forName(
                    "androidx.car.app.messaging.model.ConversationCallback");
            Object convoCb = Proxy.newProxyInstance(
                    convoCbIface.getClassLoader(),
                    new Class[]{ convoCbIface },
                    (proxy, method, args) -> {
                        String name = method.getName();
                        if ("onMarkAsRead".equals(name)) {
                            Log.i(TAG, "ConversationCallback.onMarkAsRead() invoked");
                        } else if ("onTextReply".equals(name) && args.length == 1) {
                            Log.i(TAG, "ConversationCallback.onTextReply(\""
                                    + args[0] + "\") invoked");
                        }
                        return null;
                    });

            // 3) Instantiate ConversationCallbackDelegateImpl(ConversationCallback)
            Constructor<?> delegateCtor = delegateCls.getDeclaredConstructor(convoCbIface);
            delegateCtor.setAccessible(true);
            Object delegate = delegateCtor.newInstance(convoCb);
            Log.d(TAG, "Created ConversationCallbackDelegateImpl");

            // 4) Prepare an OnDoneCallback proxy that logs success/failure
            Class<?> onDoneIface = Class.forName(
                    "androidx.car.app.OnDoneCallback");
            Object onDoneCb = Proxy.newProxyInstance(
                    onDoneIface.getClassLoader(),
                    new Class[]{ onDoneIface },
                    (proxy, method, args) -> {
                        String name = method.getName();
                        if ("onSuccess".equals(name)) {
                            Log.i(TAG, "OnDoneCallback.onSuccess - "
                                    + (args != null && args.length == 1 ? args[0] : "null"));
                        } else if ("onFailure".equals(name)) {
                            Log.i(TAG, "OnDoneCallback.onFailure - "
                                    + (args != null && args.length == 1 ? args[0] : "null"));
                        }
                        return null;
                    });

            // 5) Invoke sendMarkAsRead(OnDoneCallback)
            Method sendMark = delegateCls.getDeclaredMethod(
                    "sendMarkAsRead", onDoneIface);
            sendMark.setAccessible(true);
            sendMark.invoke(delegate, onDoneCb);
            Log.d(TAG, "sendMarkAsRead(onDoneCb) invoked");

            // 6) Invoke sendTextReply(String, OnDoneCallback)
            Method sendReply = delegateCls.getDeclaredMethod(
                    "sendTextReply", String.class, onDoneIface);
            sendReply.setAccessible(true);
            sendReply.invoke(delegate, "Hello from reflection!", onDoneCb);
            Log.d(TAG, "sendTextReply(\"Hello from reflection!\", onDoneCb) invoked");

        } catch (InvocationTargetException ite) {
            Log.e(TAG, "Underlying exception in ConversationCallbackDelegateImpl:",
                    ite.getTargetException());
        } catch (Exception e) {
            Log.e(TAG, "Error exercising ConversationCallbackDelegateImpl", e);
        }
    }

    private void exerciseCarConnection() {
        try {
            // Instantiate CarConnection(Context)
            Class<?> connCls = Class.forName("androidx.car.app.connection.CarConnection");
            Constructor<?> connCtor = connCls.getConstructor(Context.class);
            Object carConn = connCtor.newInstance(getCarContext());
            Log.d(TAG, "Created CarConnection");

            // Get the LiveData<Integer> via getType()
            Method getTypeM = connCls.getDeclaredMethod("getType");
            getTypeM.setAccessible(true);
            Object liveData = getTypeM.invoke(carConn);  // LiveData<Integer>
            Class<?> liveDataCls = liveData.getClass();

            // Build a robust Observer<Integer> proxy
            Class<?> observerIface = Class.forName("androidx.lifecycle.Observer");
            Object observer = Proxy.newProxyInstance(
                    observerIface.getClassLoader(),
                    new Class[]{ observerIface },
                    new InvocationHandler() {
                        private final int identityHash = System.identityHashCode(this);

                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args)
                                throws Throwable {
                            String name = method.getName();
                            // equals(Object)
                            if ("equals".equals(name) && args != null && args.length == 1) {
                                return proxy == args[0];
                            }
                            // hashCode()
                            if ("hashCode".equals(name) && (args == null || args.length == 0)) {
                                return identityHash;
                            }
                            // toString()
                            if ("toString".equals(name) && (args == null || args.length == 0)) {
                                return "CarConnectionObserverProxy@" + identityHash;
                            }
                            // onChanged(T value)
                            if ("onChanged".equals(name) && args != null && args.length == 1) {
                                Object value = args[0];
                                Log.i(TAG, "CarConnection type changed - " + value);
                                return null;
                            }
                            // default no-op
                            return null;
                        }
                    });

            // observeForever(observer)
            Method observeForeverM = liveDataCls.getMethod("observeForever", observerIface);
            observeForeverM.setAccessible(true);
            observeForeverM.invoke(liveData, observer);
            Log.d(TAG, "Attached observer via observeForever(...)");

            // Immediately log current value: getValue()
            Method getValueM = liveDataCls.getMethod("getValue");
            getValueM.setAccessible(true);
            Object current = getValueM.invoke(liveData);
            Log.i(TAG, "Initial CarConnection type = " + current);

            // Fire the ACTION_CAR_CONNECTION_UPDATED broadcast to force a re-query
            String action = (String) connCls.getField("ACTION_CAR_CONNECTION_UPDATED").get(null);
            Intent intent = new Intent(action);
            getCarContext().sendBroadcast(intent);
            Log.d(TAG, "Sent broadcast " + action);

            // Wait
            Thread.sleep(500);

            //removeObserver(observer)
            Method removeObsM = liveDataCls.getMethod("removeObserver", observerIface);
            removeObsM.setAccessible(true);
            removeObsM.invoke(liveData, observer);
            Log.d(TAG, "Removed observer via removeObserver(...)");

        } catch (InvocationTargetException ite) {
            Log.e(TAG, "Underlying exception in CarConnection:",
                    ite.getTargetException());
        } catch (Exception e) {
            Log.e(TAG, "Error exercising CarConnection", e);
        }
    }

    @SuppressLint("RestrictedApi")
    private void exerciseHostDispatcher() {
        try {
            // 1) Instantiate HostDispatcher
            Class<?> hdCls = Class.forName("androidx.car.app.HostDispatcher");
            Object hostDispatcher = hdCls.getConstructor().newInstance();
            Log.d(TAG, "Created HostDispatcher");

            // 2) Build a fake ICarHost proxy (implements ICarHost & IInterface)
            Class<?> iCarHostIface    = Class.forName("androidx.car.app.ICarHost");
            Class<?> iInterfaceIface  = Class.forName("android.os.IInterface");
            Object fakeCarHost = Proxy.newProxyInstance(
                    iCarHostIface.getClassLoader(),
                    new Class[]{ iCarHostIface, iInterfaceIface },
                    (proxy, method, args) -> {
                        // asBinder() must return an IBinder, but for CAR_SERVICE we directly cast proxy IInterface
                        if ("asBinder".equals(method.getName())) {
                            return proxy;
                        }
                        // We never call getHost("car") when hostType == CAR_SERVICE
                        return null;
                    });

            // 3) Bind our fake ICarHost
            Method setCarHostM = hdCls.getDeclaredMethod("setCarHost", iCarHostIface);
            setCarHostM.setAccessible(true);
            setCarHostM.invoke(hostDispatcher, fakeCarHost);
            Log.d(TAG, "Bound fake ICarHost to HostDispatcher");

            // 4) Find dispatchForResult(...) & dispatch(...) reflectively
            Method dispatchForResultM = null, dispatchVoidM = null;
            for (Method m : hdCls.getMethods()) {
                if (m.getName().equals("dispatchForResult") && m.getParameterCount() == 3) {
                    dispatchForResultM = m;
                }
                if (m.getName().equals("dispatch") && m.getParameterCount() == 3) {
                    dispatchVoidM = m;
                }
            }
            if (dispatchForResultM == null || dispatchVoidM == null) {
                Log.e(TAG, "Cannot find dispatch methods");
                return;
            }
            dispatchForResultM.setAccessible(true);
            dispatchVoidM   .setAccessible(true);

            // 5) Build the HostCall<ServiceT,ReturnT> proxy from the actual third parameter type
            Class<?> hostCallType = dispatchForResultM.getParameterTypes()[2];
            InvocationHandler callHandler = new InvocationHandler() {
                private final int hash = System.identityHashCode(this);

                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    String name = method.getName();
                    if ("dispatch".equals(name) && args.length == 1) {
                        Object svc = args[0];
                        Log.i(TAG, "HostCall.dispatch() service = "
                                + (svc != null ? svc.getClass().getSimpleName() : "null"));
                        return "object";
                    }
                    if ("equals".equals(name) && args.length == 1) {
                        return proxy == args[0];
                    }
                    if ("hashCode".equals(name) && (args == null || args.length == 0)) {
                        return hash;
                    }
                    if ("toString".equals(name) && (args == null || args.length == 0)) {
                        return "HostCallProxy@" + hash;
                    }
                    return null;
                }
            };
            Object hostCallProxy = Proxy.newProxyInstance(
                    hostCallType.getClassLoader(),
                    new Class[]{ hostCallType },
                    callHandler);

            // 6) Invoke **only** the CAR_SERVICE path
            String carSvc = CarContext.CAR_SERVICE; // "car"
            Object result = dispatchForResultM.invoke(
                    hostDispatcher,
                    carSvc,
                    "testForResult:car",
                    hostCallProxy);
            Log.i(TAG, "dispatchForResult(car) - " + result);

            dispatchVoidM.invoke(
                    hostDispatcher,
                    carSvc,
                    "testDispatch:car",
                    hostCallProxy);
            Log.i(TAG, "dispatch(car) invoked");

            // 7) Clean up
            Method resetM = hdCls.getDeclaredMethod("resetHosts");
            resetM.setAccessible(true);
            resetM.invoke(hostDispatcher);
            Log.d(TAG, "resetHosts() invoked");

        } catch (InvocationTargetException ite) {
            Log.e(TAG, "Underlying exception in HostDispatcher:",
                    ite.getTargetException());
        } catch (Exception e) {
            Log.e(TAG, "Error exercising HostDispatcher", e);
        }
    }

    @SuppressLint("RestrictedApi")
    private void exerciseHostDispatcherNavigationRpc() {
        try {
            // 1) Grab HostDispatcher
            Field hdField = CarContext.class.getDeclaredField("mHostDispatcher");
            hdField.setAccessible(true);
            Object hostDispatcher = hdField.get(getCarContext());
            Class<?> hdCls = hostDispatcher.getClass();
            Log.d(TAG, "Found HostDispatcher: " + hdCls.getName());

            // 2) Locate dispatchForResult(String, String, HostCall) dynamically
            Method dispatchForResultM = null;
            Class<?> hostCallIface = null;
            for (Method m : hdCls.getMethods()) {
                if (m.getName().equals("dispatchForResult")
                        && m.getParameterCount() == 3
                        && m.getParameterTypes()[0] == String.class
                        && m.getParameterTypes()[1] == String.class) {
                    dispatchForResultM = m;
                    hostCallIface = m.getParameterTypes()[2];
                    break;
                }
            }
            if (dispatchForResultM == null) {
                Log.w(TAG, "Could not locate dispatchForResult(hostType,callName,hostCall)");
                return;
            }

            // 3) Build HostCall proxy that only calls start/stop navigation
            Object hostCallProxy = Proxy.newProxyInstance(
                    hostCallIface.getClassLoader(),
                    new Class[]{ hostCallIface },
                    (proxy, method, args) -> {
                        if (!"dispatch".equals(method.getName()) || args.length != 1) {
                            return null;
                        }
                        Object navHost = args[0];  // INavigationHost.Stub.Proxy
                        Class<?> navCls = navHost.getClass();

                        try {
                            // navigationStarted()
                            Method startM = navCls.getMethod("navigationStarted");
                            startM.invoke(navHost);
                            Log.i(TAG, "Called navigationStarted()");
                        } catch (NoSuchMethodException ignore) { /* skip */ }

                        try {
                            // navigationEnded()
                            Method endM = navCls.getMethod("navigationEnded");
                            endM.invoke(navHost);
                            Log.i(TAG, "Called navigationEnded()");
                        } catch (NoSuchMethodException ignore) { /* skip */ }

                        return null;
                    });

            // 4) Dispatch via HostDispatcher
            dispatchForResultM.setAccessible(true);
            String navService = CarContext.NAVIGATION_SERVICE;  // "navigation"
            dispatchForResultM.invoke(
                    hostDispatcher,
                    navService,
                    "navLifecycle",
                    hostCallProxy);

            Log.d(TAG, "dispatchForResult(“navigation”, “navLifecycle”, hostCall) done");

        } catch (InvocationTargetException ite) {
            Log.e(TAG, "Underlying exception in navigation RPC:",
                    ite.getTargetException());
        } catch (Exception e) {
            Log.e(TAG, "Error exercising HostDispatcher navigation RPC", e);
        }
    }

    @SuppressLint("RestrictedApi")
    private void exerciseHostValidator() {
        try {
            // Load HostValidator class
            Class<?> hvCls = Class.forName("androidx.car.app.validation.HostValidator");

            // Prepare HostInfo for our own app package & uid
            Class<?> hostInfoCls = Class.forName("androidx.car.app.HostInfo");
            Constructor<?> hiCtor = hostInfoCls.getConstructor(String.class, int.class);
            String myPackage = getCarContext().getPackageName();
            int myUid = android.os.Process.myUid();
            Object hostInfo = hiCtor.newInstance(myPackage, myUid);
            Log.d(TAG, "HostInfo for package=" + myPackage + ", uid=" + myUid);

            // Compute SHA‑256 digest of our app’s signing cert
            PackageManager pm = getCarContext().getPackageManager();
            PackageInfo pkgInfo;
            if (Build.VERSION.SDK_INT >= 28) {
                pkgInfo = pm.getPackageInfo(
                        myPackage,
                        PackageManager.GET_SIGNING_CERTIFICATES | PackageManager.GET_PERMISSIONS);
                Signature[] sigs = pkgInfo.signingInfo.getApkContentsSigners();
                pkgInfo.signatures = sigs;
            } else {
                pkgInfo = pm.getPackageInfo(
                        myPackage,
                        PackageManager.GET_SIGNATURES | PackageManager.GET_PERMISSIONS);
            }
            Signature[] signatures = pkgInfo.signatures;
            if (signatures == null || signatures.length == 0) {
                Log.w(TAG, "No signatures for package " + myPackage);
                return;
            }
            // Use first signature
            byte[] certBytes = signatures[0].toByteArray();
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digestBytes = md.digest(certBytes);
            // Build hex‐string (lowercase, two‐digit pairs separated by colon)
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < digestBytes.length; i++) {
                sb.append(String.format("%02x", digestBytes[i]));
                if (i < digestBytes.length - 1) {
                    sb.append(":");
                }
            }
            String realDigest = sb.toString();
            Log.d(TAG, "Computed SHA‑256 digest: " + realDigest);

            // Use the ALLOW_ALL singleton to show it always returns true
            Field allowAllField = hvCls.getField("ALLOW_ALL_HOSTS_VALIDATOR");
            Object allowAll = allowAllField.get(null);
            Method isValidM = hvCls.getMethod("isValidHost", hostInfoCls);
            boolean alwaysOk = (Boolean) isValidM.invoke(allowAll, hostInfo);
            Log.i(TAG, "ALLOW_ALL_VALIDATOR.isValidHost - " + alwaysOk);

            // Build a real HostValidator via Builder, adding our package + real digest
            Class<?> builderCls = Class.forName(
                    "androidx.car.app.validation.HostValidator$Builder");
            Constructor<?> builderCtor = builderCls.getConstructor(Context.class);
            Object builder = builderCtor.newInstance(getCarContext());

            Method addHostM = builderCls.getMethod("addAllowedHost",
                    String.class, String.class);
            addHostM.invoke(builder, myPackage, realDigest);
            //Log.d(TAG, "Builder.addAllowedHost(" + myPackage + ", " + realDigest + ")");

            Method buildM = builderCls.getMethod("build");
            Object customValidator = buildM.invoke(builder);
            Log.d(TAG, "Built custom HostValidator");

            // Inspect getAllowedHosts()
            Method getAllowedM = hvCls.getMethod("getAllowedHosts");
            // HostValidator.getAllowedHosts() returns Map<String,List<String>>
            @SuppressWarnings("unchecked")
            Map<String,List<String>> allowed =
                    (Map<String,List<String>>) getAllowedM.invoke(customValidator);
            Log.i(TAG, "Custom.getAllowedHosts - " + allowed);

            // Verify isValidHost(...) now returns true for our real HostInfo
            boolean customOk = (Boolean) isValidM.invoke(customValidator, hostInfo);
            Log.i(TAG, "CustomValidator.isValidHost(" + myPackage + ") - " + customOk);

        } catch (InvocationTargetException ite) {
            Log.e(TAG, "Underlying exception in HostValidator:",
                    ite.getTargetException());
        } catch (Exception e) {
            Log.e(TAG, "Error exercising HostValidator", e);
        }
    }


    @SuppressLint("RestrictedApi")
    private void exerciseCommonUtils() {
        try {
            //Load CommonUtils
            Class<?> cuCls = Class.forName("androidx.car.app.utils.CommonUtils");
            Method isAutoM = cuCls.getMethod("isAutomotiveOS", Context.class);

            //CarContext
            Context carCtx = getCarContext();
            boolean carRes = (Boolean) isAutoM.invoke(null, carCtx);
            Log.i(TAG, "isAutomotiveOS(getCarContext()) - " + carRes);

            //BaseContext
            Context appBase = carCtx.getApplicationContext();
            boolean baseRes = (Boolean) isAutoM.invoke(null, appBase);
            Log.i(TAG, "isAutomotiveOS(appBaseContext) - " + baseRes);

        } catch (Exception e) {
            Log.e(TAG, "Error exercising CommonUtils", e);
        }
    }


    @SuppressLint("RestrictedApi")
    private void exerciseNavigationManager() {
        try {
            Class<?> nmCls = Class.forName("androidx.car.app.navigation.NavigationManager");
            Method createM = nmCls.getDeclaredMethod(
                    "create",
                    Class.forName("androidx.car.app.CarContext"),
                    Class.forName("androidx.car.app.HostDispatcher"),
                    Class.forName("androidx.lifecycle.Lifecycle"));
            createM.setAccessible(true);

            // Grab CarContext, HostDispatcher, Lifecycle
            Object carCtx = getCarContext();
            Object hostDisp = CarContext.class
                    .getDeclaredField("mHostDispatcher")
                    .get(carCtx);
            Object lifecycle = this.getLifecycle();

            //Instantiate NavigationManager
            Object navMgr = createM.invoke(null, carCtx, hostDisp, lifecycle);
            Log.d(TAG, "Created NavigationManager via create(...)");

            Class<?> cbIface = Class.forName(
                    "androidx.car.app.navigation.NavigationManagerCallback");
            Executor mainExec = ContextCompat.getMainExecutor((Context) carCtx);

            Object callbackProxy = Proxy.newProxyInstance(
                    cbIface.getClassLoader(),
                    new Class[]{ cbIface },
                    (proxy, method, args) -> {
                        String name = method.getName();
                        if ("onStopNavigation".equals(name)) {
                            Log.i(TAG, "Callback: onStopNavigation()");
                        } else if ("onAutoDriveEnabled".equals(name)) {
                            Log.i(TAG, "Callback: onAutoDriveEnabled()");
                        }
                        return null;
                    });

            //navigationEnded()
            Method navEndM = nmCls.getDeclaredMethod("navigationEnded");
            navEndM.setAccessible(true);
            navEndM.invoke(navMgr);
            Log.d(TAG, "Called navigationEnded() before start (noop)");

            try {
                Method clearM = nmCls.getDeclaredMethod("clearNavigationManagerCallback");
                clearM.setAccessible(true);
                clearM.invoke(navMgr);
            } catch (InvocationTargetException ite) {
                Log.i(TAG, "clearNavigationManagerCallback() threw: "
                        + ite.getTargetException());
            }

            //setNavigationManagerCallback(Executor,Callback)
            Method setCbM = nmCls.getDeclaredMethod(
                    "setNavigationManagerCallback", Executor.class, cbIface);
            setCbM.setAccessible(true);
            setCbM.invoke(navMgr, mainExec, callbackProxy);
            Log.d(TAG, "Installed NavigationManagerCallback");

            // navigationStarted()
            Method navStartM = nmCls.getDeclaredMethod("navigationStarted");
            navStartM.setAccessible(true);
            navStartM.invoke(navMgr);
            Log.i(TAG, "Called navigationStarted()");

            // updateTrip(Trip)
            Class<?> tripCls = Class.forName("androidx.car.app.navigation.model.Trip");
            Constructor<?> tripCtor = tripCls.getDeclaredConstructor();
            tripCtor.setAccessible(true);
            Object emptyTrip = tripCtor.newInstance();

            Method updateM = nmCls.getDeclaredMethod("updateTrip", tripCls);
            updateM.setAccessible(true);
            updateM.invoke(navMgr, emptyTrip);
            Log.i(TAG, "Called updateTrip(emptyTrip)");

            //navigationEnded()
            navEndM.invoke(navMgr);
            Log.i(TAG, "Called navigationEnded()");

            Method autoDriveM = nmCls.getDeclaredMethod("onAutoDriveEnabled");
            autoDriveM.setAccessible(true);
            autoDriveM.invoke(navMgr);
            Log.i(TAG, "Called onAutoDriveEnabled()");

            Method getIfaceM = nmCls.getDeclaredMethod("getIInterface");
            getIfaceM.setAccessible(true);
            Object iNavStub = getIfaceM.invoke(navMgr);

            Class<?> onDoneCls = Class.forName("androidx.car.app.IOnDoneCallback");
            Object onDoneProxy = Proxy.newProxyInstance(
                    onDoneCls.getClassLoader(),
                    new Class[]{ onDoneCls },
                    (p, m, a) -> {
                        Log.i(TAG, "IOnDoneCallback." + m.getName() + " invoked");
                        return null;
                    });

            Method onStopNavM = iNavStub.getClass()
                    .getMethod("onStopNavigation", onDoneCls);
            onStopNavM.invoke(iNavStub, onDoneProxy);
            Log.i(TAG, "Invoked INavigationManager.Stub.onStopNavigation(...)");

        } catch (InvocationTargetException ite) {
            Log.e(TAG, "Underlying exception in NavigationManager:",
                    ite.getTargetException());
        } catch (Exception e) {
            Log.e(TAG, "Error exercising NavigationManager", e);
        }
    }

    @SuppressLint({"RestrictedApi", "MissingPermission"})
    private void exerciseCarAppExtender() {
        try {
            Context ctx = getCarContext();
            String pkg = ctx.getPackageName();

            //Load CarAppExtender
            Class<?> extenderCls = Class.forName(
                    "androidx.car.app.notification.CarAppExtender");
            Class<?> builderCls = Class.forName(
                    "androidx.car.app.notification.CarAppExtender$Builder");

            Class<?> carColorCls = Class.forName("androidx.car.app.model.CarColor");
            Field blueField = carColorCls.getField("BLUE");
            Object carColor = blueField.get(null);  // CarColor.BLUE

            //Parameters to set
            CharSequence title   = "Car Title";
            CharSequence text    = "Hello from reflection!";
            int smallIcon        = ctx.getApplicationInfo().icon;
            Bitmap largeBitmap   = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);

            Intent launchIntent  = ctx.getPackageManager()
                    .getLaunchIntentForPackage(pkg);
            PendingIntent contentPI = PendingIntent.getActivity(
                    ctx, 0, launchIntent,
                    PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent deletePI  = PendingIntent.getActivity(
                    ctx, 1, launchIntent,
                    PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent actionPI  = PendingIntent.getActivity(
                    ctx, 2, launchIntent,
                    PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

            String channelId      = "rfl_channel_id";
            int importance        = NotificationManagerCompat.IMPORTANCE_HIGH;

            //Instantiate builder
            Object builder = builderCls.getConstructor().newInstance();

            //Invoke setters
            builderCls.getMethod("setContentTitle", CharSequence.class)
                    .invoke(builder, title);
            builderCls.getMethod("setContentText", CharSequence.class)
                    .invoke(builder, text);
            builderCls.getMethod("setSmallIcon", int.class)
                    .invoke(builder, smallIcon);
            builderCls.getMethod("setLargeIcon", Bitmap.class)
                    .invoke(builder, largeBitmap);
            builderCls.getMethod("setContentIntent", PendingIntent.class)
                    .invoke(builder, contentPI);
            builderCls.getMethod("setDeleteIntent", PendingIntent.class)
                    .invoke(builder, deletePI);
            builderCls.getMethod("addAction", int.class, CharSequence.class, PendingIntent.class)
                    .invoke(builder, smallIcon, "Action!", actionPI);
            builderCls.getMethod("setImportance", int.class)
                    .invoke(builder, importance);
            builderCls.getMethod("setColor", carColorCls)
                    .invoke(builder, carColor);
            builderCls.getMethod("setChannelId", String.class)
                    .invoke(builder, channelId);

            //Build CarAppExtender
            Object extender = builderCls.getMethod("build").invoke(builder);
            Log.d(TAG, "Built CarAppExtender");

            //Create and extend a NotificationCompat.Builder
            NotificationCompat.Builder notifB =
                    new NotificationCompat.Builder(ctx, channelId)
                            .setContentTitle("Base Title")
                            .setContentText("Base Text")
                            .setSmallIcon(smallIcon);
            NotificationCompat.Builder extendedB =
                    (NotificationCompat.Builder) extenderCls
                            .getMethod("extend", NotificationCompat.Builder.class)
                            .invoke(extender, notifB);
            Log.d(TAG, "Extended NotificationCompat.Builder");

            //Build the Notification
            assert extendedB != null;
            Notification notif = extendedB.build();

            //Check static isExtended()
            boolean isExt = (Boolean) extenderCls
                    .getMethod("isExtended", Notification.class)
                    .invoke(null, notif);
            Log.i(TAG, "isExtended(...) - " + isExt);

            //Read back via new CarAppExtender
            Object readBackExt = extenderCls
                    .getConstructor(Notification.class)
                    .newInstance(notif);
            Log.d(TAG, "Reconstructed CarAppExtender from Notification");

            //Invoke every getter to verify round-trip
            String[] getters = {
                    "getContentTitle",
                    "getContentText",
                    "getSmallIcon",
                    "getLargeIcon",
                    "getContentIntent",
                    "getDeleteIntent",
                    "getActions",
                    "getImportance",
                    "getColor",
                    "getChannelId"
            };
            for (String gm : getters) {
                Method m = extenderCls.getMethod(gm);
                Object val = m.invoke(readBackExt);
                Log.i(TAG, gm + " - " + val);
            }

        } catch (InvocationTargetException ite) {
            Log.e(TAG, "Underlying exception in CarAppExtender:",
                    ite.getTargetException());
        } catch (Exception e) {
            Log.e(TAG, "Error exercising CarAppExtender", e);
        }
    }

    @SuppressLint({"RestrictedApi", "MissingPermission"})
    private void exerciseCarNotificationManagerGeneratesPath() {
        try {
            Context context = getCarContext(); // or any valid Context
            //Instantiate via from(Context)
            Class<?> mgrCls = Class.forName("androidx.car.app.notification.CarNotificationManager");
            Method fromM = mgrCls.getDeclaredMethod("from", Context.class);
            Object mgr = fromM.invoke(null, context);
            Log.d(TAG, "Created CarNotificationManager");

            // NotificationCompat.Builder
            Class<?> notifCompatBuilderCls = Class.forName("androidx.core.app.NotificationCompat$Builder");
            Constructor<?> notifBuilderCtor = notifCompatBuilderCls.getConstructor(Context.class, String.class);
            // use channel "default" for builder
            Object notifBuilder = notifBuilderCtor.newInstance(context, "default_channel");
            // set minimal required fields
            notifCompatBuilderCls.getMethod("setContentTitle", CharSequence.class)
                    .invoke(notifBuilder, "Title");
            notifCompatBuilderCls.getMethod("setContentText", CharSequence.class)
                    .invoke(notifBuilder, "Text");
            Method buildNotifM = notifCompatBuilderCls.getMethod("build");

            // NotificationChannelCompat & Group
            Class<?> channelCompatCls = Class.forName("androidx.core.app.NotificationChannelCompat");
            Class<?> channelCompatBuilderCls = Class.forName("androidx.core.app.NotificationChannelCompat$Builder");
            Constructor<?> chanBuilderCtor = channelCompatBuilderCls.getConstructor(String.class, int.class);
            Object chanBuilder = chanBuilderCtor.newInstance("chan1", NotificationManagerCompat.IMPORTANCE_DEFAULT);
            Method buildChanM = channelCompatBuilderCls.getMethod("build");
            Object channel1 = buildChanM.invoke(chanBuilder);

            Class<?> groupCompatBuilderCls = Class.forName("androidx.core.app.NotificationChannelGroupCompat$Builder");
            Constructor<?> grpBuilderCtor = groupCompatBuilderCls.getConstructor(String.class);
            Object grpBuilder = grpBuilderCtor.newInstance("group1");
            Method buildGrpM = groupCompatBuilderCls.getMethod("build");
            Object group1 = buildGrpM.invoke(grpBuilder);

            // Invoke public methods
            // cancel(int)
            mgrCls.getMethod("cancel", int.class).invoke(mgr, 1001);
            // cancel(tag, id)
            mgrCls.getMethod("cancel", String.class, int.class).invoke(mgr, "tag", 1002);
            // cancelAll()
            mgrCls.getMethod("cancelAll").invoke(mgr);

            // notify(id, Builder)
            mgrCls.getMethod("notify", int.class, notifCompatBuilderCls)
                    .invoke(mgr, 2001, notifBuilder);
            // notify(tag, id, Builder)
            mgrCls.getMethod("notify", String.class, int.class, notifCompatBuilderCls)
                    .invoke(mgr, "t2", 2002, notifBuilder);

            // areNotificationsEnabled()
            Boolean enabled = (Boolean) mgrCls.getMethod("areNotificationsEnabled").invoke(mgr);
            Log.d(TAG, "areNotificationsEnabled=" + enabled);
            // getImportance()
            Integer importance = (Integer) mgrCls.getMethod("getImportance").invoke(mgr);
            Log.d(TAG, "getImportance=" + importance);

            // createNotificationChannel(ChannelCompat)
            mgrCls.getMethod("createNotificationChannel", channelCompatCls)
                    .invoke(mgr, channel1);
            // createNotificationChannelGroup(GroupCompat)
            mgrCls.getMethod("createNotificationChannelGroup", Class.forName(
                            "androidx.core.app.NotificationChannelGroupCompat"))
                    .invoke(mgr, group1);

            // createNotificationChannels(List<ChannelCompat>)
            List<Object> chanList = new ArrayList<>();
            chanList.add(channel1);
            mgrCls.getMethod("createNotificationChannels", List.class)
                    .invoke(mgr, chanList);

            // createNotificationChannelGroups(List<GroupCompat>)
            List<Object> grpList = new ArrayList<>();
            grpList.add(group1);
            mgrCls.getMethod("createNotificationChannelGroups", List.class)
                    .invoke(mgr, grpList);

            // deleteNotificationChannel(String)
            mgrCls.getMethod("deleteNotificationChannel", String.class)
                    .invoke(mgr, "chan1");
            // deleteNotificationChannelGroup(String)
            mgrCls.getMethod("deleteNotificationChannelGroup", String.class)
                    .invoke(mgr, "group1");
            // deleteUnlistedNotificationChannels(Collection<String>)
            mgrCls.getMethod("deleteUnlistedNotificationChannels", Collection.class)
                    .invoke(mgr, Collections.singleton("chan1"));

            // getNotificationChannel(String)
            Object gotChan = mgrCls.getMethod("getNotificationChannel", String.class)
                    .invoke(mgr, "chan1");
            Log.d(TAG, "getNotificationChannel - " + gotChan);
            // getNotificationChannel(String,String)
            Object gotChan2 = mgrCls.getMethod("getNotificationChannel",
                            String.class, String.class)
                    .invoke(mgr, "chan1", "conv1");
            Log.d(TAG, "getNotificationChannel(tag) - " + gotChan2);

            // getNotificationChannelGroup(String)
            Object gotGroup = mgrCls.getMethod("getNotificationChannelGroup", String.class)
                    .invoke(mgr, "group1");
            Log.d(TAG, "getNotificationChannelGroup - " + gotGroup);

            // getNotificationChannels()
            List<?> chans = (List<?>) mgrCls.getMethod("getNotificationChannels").invoke(mgr);
            Log.d(TAG, "# channels=" + chans.size());
            // getNotificationChannelGroups()
            List<?> grps = (List<?>) mgrCls.getMethod("getNotificationChannelGroups").invoke(mgr);
            Log.d(TAG, "# groups=" + grps.size());

            // getEnabledListenerPackages(Context)
            Class<?> mgrClsStatic = Class.forName("androidx.car.app.notification.CarNotificationManager");
            Method enabledPkgsM = mgrClsStatic.getMethod("getEnabledListenerPackages", Context.class);
            Set<?> listeners = (Set<?>) enabledPkgsM.invoke(null, context);
            Log.d(TAG, "# enabled listener pkgs=" + listeners.size());

            // updateForCar(NotificationCompat.Builder)
            Method updateForCarM = mgrCls.getDeclaredMethod("updateForCar", notifCompatBuilderCls);
            updateForCarM.setAccessible(true);
            Notification carNotif = (Notification) updateForCarM.invoke(mgr, notifBuilder);
            Log.d(TAG, "updateForCar() - " + carNotif);

            // getColorInt(CarColor)  – need a CarColor instance
            Class<?> carColorCls = Class.forName("androidx.car.app.model.CarColor");
            // pick static BLUE
            Field blueF = carColorCls.getDeclaredField("BLUE");
            Object blueColor = blueF.get(null);
            Method getColorIntM = mgrCls.getDeclaredMethod("getColorInt", carColorCls);
            getColorIntM.setAccessible(true);
            Integer blueInt = (Integer) getColorIntM.invoke(mgr, blueColor);
            Log.d(TAG, "CarColor.BLUE - colorInt=" + blueInt);

            // loadThemeId(Context)
            Method loadThemeIdM = mgrCls.getDeclaredMethod("loadThemeId", Context.class);
            loadThemeIdM.setAccessible(true);
            int themeId = (int) loadThemeIdM.invoke(null, context);
            Log.d(TAG, "loadThemeId - " + themeId);

            // getColor(int, Resources.Theme)
            // retrieve a real Theme
            Resources.Theme theme = context.getTheme();
            Method getColorM = mgrCls.getDeclaredMethod("getColor", int.class, Resources.Theme.class);
            getColorM.setAccessible(true);
            // use a known attr (android.R.attr.colorAccent), fallback to NULL
            Integer accent = (Integer) getColorM.invoke(null,
                    android.R.attr.colorAccent, theme);
            Log.d(TAG, "getColor(android:colorAccent) - " + accent);

            Log.d(TAG, "Finished exercising CarNotificationManager");
        } catch (Exception e) {
            Log.e(TAG, "Error exercising CarNotificationManager", e);
        }
    }

    @SuppressLint("RestrictedApi")
    private void exerciseCarNotificationManagerGeneratesPath2() {
        try {
            //Obtain an instance of CarNotificationManager
            Class<?> cnmCls = Class.forName("androidx.car.app.notification.CarNotificationManager");
            Method fromM = cnmCls.getDeclaredMethod("from", Context.class);
            Object carNotifMgr = fromM.invoke(null, getCarContext());

            Log.d(TAG, "Created CarNotificationManager");

            //Build a sample NotificationCompat.Builder
            NotificationCompat.Builder notifBuilder = makeSampleNotificationBuilder(getCarContext());

            //public methods
            Method cancelInt = cnmCls.getDeclaredMethod("cancel", int.class);
            cancelInt.invoke(carNotifMgr, 123);

            Method cancelTag = cnmCls.getDeclaredMethod("cancel", String.class, int.class);
            cancelTag.invoke(carNotifMgr, "myTag", 123);

            Method cancelAll = cnmCls.getDeclaredMethod("cancelAll");
            cancelAll.invoke(carNotifMgr);

            Method notifyId = cnmCls.getDeclaredMethod("notify", int.class,
                    NotificationCompat.Builder.class);
            notifyId.invoke(carNotifMgr, 1001, notifBuilder);

            Method notifyTag = cnmCls.getDeclaredMethod("notify", String.class, int.class,
                    NotificationCompat.Builder.class);
            notifyTag.invoke(carNotifMgr, "tagX", 1002, notifBuilder);

            Method areEnabled = cnmCls.getDeclaredMethod("areNotificationsEnabled");
            boolean enabled = (boolean) areEnabled.invoke(carNotifMgr);
            Log.d(TAG, "areNotificationsEnabled - " + enabled);

            Method getImp = cnmCls.getDeclaredMethod("getImportance");
            int imp = (int) getImp.invoke(carNotifMgr);
            Log.d(TAG, "getImportance - " + imp);

            Class<?> chanCompatCls = Class.forName("androidx.core.app.NotificationChannelCompat");

            // build NotificationChannelCompat
            Class<?> chanBuilderCls = Class.forName("androidx.core.app.NotificationChannelCompat$Builder");
            Constructor<?> chanBctor = chanBuilderCls.getConstructor(String.class, int.class);
            Object chanBuilder = chanBctor.newInstance("car_channel", NotificationManagerCompat.IMPORTANCE_HIGH);
            Method chanBuild = chanBuilderCls.getDeclaredMethod("build");
            Object chanCompat = chanBuild.invoke(chanBuilder);
            Method createChan = cnmCls.getDeclaredMethod("createNotificationChannel", chanCompatCls);
            createChan.invoke(carNotifMgr, chanCompat);

            Class<?> groupCompatCls = Class.forName("androidx.core.app.NotificationChannelGroupCompat");
            Class<?> groupBuilderCls = Class.forName("androidx.core.app.NotificationChannelGroupCompat$Builder");
            Constructor<?> groupBctor = groupBuilderCls.getConstructor(String.class);
            Object groupBuilder = groupBctor.newInstance("group1");
            Method groupBuild = groupBuilderCls.getDeclaredMethod("build");
            Object groupCompat = groupBuild.invoke(groupBuilder);
            Method createGroup = cnmCls.getDeclaredMethod("createNotificationChannelGroup", groupCompatCls);
            createGroup.invoke(carNotifMgr, groupCompat);

            Method createChans = cnmCls.getDeclaredMethod("createNotificationChannels", List.class);
            createChans.invoke(carNotifMgr, Collections.singletonList(chanCompat));

            Method createGrpList = cnmCls.getDeclaredMethod("createNotificationChannelGroups", List.class);
            createGrpList.invoke(carNotifMgr, Collections.singletonList(groupCompat));

            Method delChan = cnmCls.getDeclaredMethod("deleteNotificationChannel", String.class);
            delChan.invoke(carNotifMgr, "car_channel");

            Method delGroup = cnmCls.getDeclaredMethod("deleteNotificationChannelGroup", String.class);
            delGroup.invoke(carNotifMgr, "group1");

            Method delUnlisted = cnmCls.getDeclaredMethod("deleteUnlistedNotificationChannels", Collection.class);
            delUnlisted.invoke(carNotifMgr, Arrays.asList("foo", "bar"));

            Method getChan = cnmCls.getDeclaredMethod("getNotificationChannel", String.class);
            Object fetchedChan = getChan.invoke(carNotifMgr, "car_channel");
            Log.d(TAG, "getNotificationChannel - " + fetchedChan);

            Method getChanConv = cnmCls.getDeclaredMethod("getNotificationChannel", String.class, String.class);
            Object fetchedChan2 = getChanConv.invoke(carNotifMgr, "car_channel", "conv123");
            Log.d(TAG, "getNotificationChannel(tag) - " + fetchedChan2);

            Method getGrp = cnmCls.getDeclaredMethod("getNotificationChannelGroup", String.class);
            Object fetchedGroup = getGrp.invoke(carNotifMgr, "group1");
            Log.d(TAG, "getNotificationChannelGroup - " + fetchedGroup);

            Method getChansList = cnmCls.getDeclaredMethod("getNotificationChannels");
            List<?> channels = (List<?>) getChansList.invoke(carNotifMgr);
            Log.d(TAG, "getNotificationChannels - size=" + channels.size());

            Method getGrpList = cnmCls.getDeclaredMethod("getNotificationChannelGroups");
            List<?> groups = (List<?>) getGrpList.invoke(carNotifMgr);
            Log.d(TAG, "getNotificationChannelGroups - size=" + groups.size());

            Method getListeners = cnmCls.getDeclaredMethod("getEnabledListenerPackages", Context.class);
            @SuppressWarnings("unchecked")
            Set<String> enabledPkgs = (Set<String>) getListeners.invoke(null, getCarContext());
            Log.d(TAG, "getEnabledListenerPackages - " + enabledPkgs);

            //Call private / @VisibleForTesting

            Method updForCar = cnmCls.getDeclaredMethod("updateForCar", NotificationCompat.Builder.class);
            updForCar.setAccessible(true);
            Notification built = (Notification) updForCar.invoke(carNotifMgr, notifBuilder);
            Log.d(TAG, "updateForCar produced Notification: " + built);

            Method updAuto = cnmCls.getDeclaredMethod("updateForAutomotive", NotificationCompat.Builder.class);
            updAuto.setAccessible(true);

            Notification builtAuto = (Notification) updAuto.invoke(carNotifMgr, notifBuilder);
            Log.d(TAG, "updateForAutomotive produced Notification: " + builtAuto);

            Class<?> carColorCls = Class.forName("androidx.car.app.model.CarColor");
            Method createCustom = carColorCls.getDeclaredMethod("createCustom", int.class, int.class);
            Object customColor = createCustom.invoke(null, 0xFF3366, 0xCC2244);
            Method getColorInt = cnmCls.getDeclaredMethod("getColorInt", carColorCls);
            getColorInt.setAccessible(true);
            Integer resultColor = (Integer) getColorInt.invoke(carNotifMgr, customColor);
            Log.d(TAG, "getColorInt(custom) - " + resultColor);
            Method loadTheme = cnmCls.getDeclaredMethod("loadThemeId", Context.class);
            loadTheme.setAccessible(true);
            int themeId = (int) loadTheme.invoke(null, getCarContext());
            Log.d(TAG, "loadThemeId - " + themeId);

            Method getColorM = cnmCls.getDeclaredMethod("getColor", int.class, Resources.Theme.class);
            getColorM.setAccessible(true);
            Resources.Theme t = getCarContext().getTheme();
            Integer themeColor = (Integer) getColorM.invoke(null, themeId, t);
            Log.d(TAG, "getColor(metaAttr) - " + themeColor);

        } catch (InvocationTargetException ite) {
            Log.e(TAG, "Underlying exception in CarNotificationManager:", ite.getTargetException());
        } catch (Exception e) {
            Log.e(TAG, "Error exercising CarNotificationManager", e);
        }
    }

    // Helper to build a valid NotificationCompat.Builder
    private NotificationCompat.Builder makeSampleNotificationBuilder(Context ctx) {
        int smallIcon = android.R.drawable.ic_dialog_info;
        String channelId = "default_channel";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel nc = new NotificationChannel(
                    channelId,
                    "Default Channel",
                    NotificationManager.IMPORTANCE_DEFAULT);
            ctx.getSystemService(NotificationManager.class).createNotificationChannel(nc);
        }
        NotificationCompat.Builder b = new NotificationCompat.Builder(ctx, channelId)
                .setSmallIcon(smallIcon)
                .setContentTitle("Sample Title")
                .setContentText("Sample car notification")
                .setAutoCancel(true)
                .addAction(
                        new NotificationCompat.Action.Builder(
                                android.R.drawable.ic_media_play,
                                "Play",
                                PendingIntent.getActivity(ctx, 0,
                                        new Intent(ctx, getClass()), PendingIntent.FLAG_IMMUTABLE))
                                .build());

        return b;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void exerciseCarNotificationManager() {
        CarNotificationManager mgr = null;
        try {
            // CarContext is your 'this.getCarContext()'
            Context ctx = getCarContext();
            mgr = CarNotificationManager.from(ctx);
            Log.d(TAG, "Created CarNotificationManager: " + mgr);
        } catch (Exception e) {
            Log.e(TAG, "Could not create CarNotificationManager", e);
            return;
        }

        Context ctx = getCarContext();

        // Build NotificationCompat.Builder
        String channelId = "test_channel_" + System.currentTimeMillis();
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(ctx, channelId)
                        .setSmallIcon(android.R.drawable.sym_def_app_icon)
                        .setContentTitle("Reflective Test")
                        .setContentText("CarNotificationManager reflection")
                        .setOnlyAlertOnce(true);
        // Dummy PendingIntent
        Intent dummy = new Intent(ctx, getCarContext().getClass());
        PendingIntent pi = PendingIntent.getActivity(
                ctx, 0, dummy, PendingIntent.FLAG_IMMUTABLE);
        builder.setContentIntent(pi).setDeleteIntent(pi);

        // Create NotificationChannelCompat
        NotificationChannelCompat chan =
                new NotificationChannelCompat.Builder(channelId,
                        NotificationManagerCompat.IMPORTANCE_HIGH)
                        .setName("Reflect Test Channel")
                        .build();

        String groupId = "test_group";
        NotificationChannelGroupCompat group =
                new NotificationChannelGroupCompat.Builder(groupId)
                        .setName("Reflect Test Group")
                        .build();

        // Reflectively invoke public methods
        try {
            Method notify1 = CarNotificationManager.class.getMethod(
                    "notify", int.class, NotificationCompat.Builder.class);
            Log.d(TAG, "notify(int) - ");
            notify1.invoke(mgr, 123, builder);

            Method notify2 = CarNotificationManager.class.getMethod(
                    "notify", String.class, int.class, NotificationCompat.Builder.class);
            Log.d(TAG, "notify(String,int) - ");
            notify2.invoke(mgr, "tagA", 456, builder);

            CarNotificationManager.class.getMethod("cancel", int.class)
                    .invoke(mgr, 123);
            Log.d(TAG, "cancel(123)");

            CarNotificationManager.class.getMethod("cancel", String.class, int.class)
                    .invoke(mgr, "tagA", 456);
            Log.d(TAG, "cancel(\"tagA\",456)");

            CarNotificationManager.class.getMethod("cancelAll")
                    .invoke(mgr);
            Log.d(TAG, "cancelAll()");

            boolean enabled = (boolean) CarNotificationManager.class
                    .getMethod("areNotificationsEnabled")
                    .invoke(mgr);
            Log.d(TAG, "areNotificationsEnabled() = " + enabled);

            int imp = (int) CarNotificationManager.class
                    .getMethod("getImportance")
                    .invoke(mgr);
            Log.d(TAG, "getImportance() = " + imp);

            CarNotificationManager.class
                    .getMethod("createNotificationChannel", NotificationChannelCompat.class)
                    .invoke(mgr, chan);
            Log.d(TAG, "createNotificationChannel(chan)");

            CarNotificationManager.class
                    .getMethod("createNotificationChannelGroup", NotificationChannelGroupCompat.class)
                    .invoke(mgr, group);
            Log.d(TAG, "createNotificationChannelGroup(group)");

            CarNotificationManager.class
                    .getMethod("createNotificationChannels", List.class)
                    .invoke(mgr, Collections.singletonList(chan));
            Log.d(TAG, "createNotificationChannels(list)");

            CarNotificationManager.class
                    .getMethod("createNotificationChannelGroups", List.class)
                    .invoke(mgr, Collections.singletonList(group));
            Log.d(TAG, "createNotificationChannelGroups(list)");

            CarNotificationManager.class
                    .getMethod("deleteNotificationChannel", String.class)
                    .invoke(mgr, channelId);
            Log.d(TAG, "deleteNotificationChannel(" + channelId + ")");

            CarNotificationManager.class
                    .getMethod("deleteNotificationChannelGroup", String.class)
                    .invoke(mgr, groupId);
            Log.d(TAG, "deleteNotificationChannelGroup(" + groupId + ")");

            CarNotificationManager.class
                    .getMethod("deleteUnlistedNotificationChannels", Collection.class)
                    .invoke(mgr, Collections.singleton("foo"));
            Log.d(TAG, "deleteUnlistedNotificationChannels([foo])");

            NotificationChannelCompat gotChan = (NotificationChannelCompat) CarNotificationManager.class
                    .getMethod("getNotificationChannel", String.class)
                    .invoke(mgr, channelId);
            Log.d(TAG, "getNotificationChannel(" + channelId + ") = " + gotChan);

            NotificationChannelCompat gotChan2 = (NotificationChannelCompat) CarNotificationManager.class
                    .getMethod("getNotificationChannel", String.class, String.class)
                    .invoke(mgr, channelId, "someConv");
            Log.d(TAG, "getNotificationChannel(" + channelId + ", someConv) = " + gotChan2);

            NotificationChannelGroupCompat gotGroup = (NotificationChannelGroupCompat) CarNotificationManager.class
                    .getMethod("getNotificationChannelGroup", String.class)
                    .invoke(mgr, groupId);
            Log.d(TAG, "getNotificationChannelGroup(" + groupId + ") = " + gotGroup);

            List<NotificationChannelCompat> listCh = (List) CarNotificationManager.class
                    .getMethod("getNotificationChannels")
                    .invoke(mgr);
            Log.d(TAG, "getNotificationChannels() = " + listCh);

            List<NotificationChannelGroupCompat> listGr = (List) CarNotificationManager.class
                    .getMethod("getNotificationChannelGroups")
                    .invoke(mgr);
            Log.d(TAG, "getNotificationChannelGroups() = " + listGr);

            Set<String> listeners = (Set<String>) CarNotificationManager.class
                    .getMethod("getEnabledListenerPackages", Context.class)
                    .invoke(null, ctx);
            Log.d(TAG, "getEnabledListenerPackages(ctx) = " + listeners);
        } catch (Exception e) {
            Log.e(TAG, "Error exercising CarNotificationManager", e);
        }

        // Reflectively invoke private & @VisibleForTesting
        try {
            Method upd = CarNotificationManager.class.getDeclaredMethod(
                    "updateForCar", NotificationCompat.Builder.class);
            upd.setAccessible(true);
            Notification n1 = (Notification) upd.invoke(mgr, builder);
            Log.d(TAG, "updateForCar(builder) - " + n1);

            Method gc = CarNotificationManager.class.getDeclaredMethod(
                    "getColorInt", CarColor.class);
            gc.setAccessible(true);
            // Try each standard CarColor:
            for (CarColor c : new CarColor[]{
                    CarColor.DEFAULT, CarColor.PRIMARY, CarColor.SECONDARY,
                    CarColor.RED, CarColor.GREEN, CarColor.BLUE, CarColor.YELLOW})
            {
                Object val = gc.invoke(mgr, c);
                Log.d(TAG, "getColorInt(" + c + ") = " + val);
            }

            // private static loadThemeId(Context)
            Method loadTheme = CarNotificationManager.class.getDeclaredMethod("loadThemeId", Context.class);
            loadTheme.setAccessible(true);
            int themeId = (int) loadTheme.invoke(null, ctx);
            Log.d(TAG, "loadThemeId(ctx) = " + themeId);

            // private static getColor(int, Theme)
            Method getColor = CarNotificationManager.class.getDeclaredMethod(
                    "getColor", int.class, Resources.Theme.class);
            getColor.setAccessible(true);
            // test with your app theme:
            Resources.Theme t = ctx.getTheme();
            Integer col = (Integer) getColor.invoke(null, android.R.attr.colorAccent, t);
            Log.d(TAG, "getColor(android.R.attr.colorAccent, theme) - " + col);
        } catch (Exception e) {
            Log.e(TAG, "Error exercising private helpers in CarNotificationManager", e);
        }
    }

    private void exerciseCarPendingIntent() {
        try {
            // 1) Load the class
            Class<?> clazz = Class.forName("androidx.car.app.notification.CarPendingIntent");

            // 2) Prepare context
            Context ctx = getCarContext(); // your CarContext
            String pkg = ctx.getPackageName();

            // 3) Build Intents for each supported path:

            // 3a) Valid navigation Intent with lat/long
            Uri navUriLL = Uri.parse("geo:37.4219983,-122.084?q=37.4219983,-122.084");
            Intent navIntentLL = new Intent(CarContext.ACTION_NAVIGATE, navUriLL);

            // 3b) Valid navigation Intent with address query
            Uri navUriQ = Uri.parse("geo:0,0?q=1600+Amphitheatre+Parkway");
            Intent navIntentQ = new Intent(CarContext.ACTION_NAVIGATE, navUriQ);

            // 3c) Valid phone Intent (dial)
            Intent phoneDial = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:1234567890"));

            // 3d) Valid phone Intent (call)
            Intent phoneCall = new Intent(Intent.ACTION_CALL, Uri.parse("tel:0987654321"));

            // 3e) Explicit CarAppService start
            ComponentName svc = new ComponentName(pkg, MyCarAppService.class.getName());
            Intent svcIntent = new Intent().setComponent(svc);

            // 4) Find & invoke validateIntent (VisibleForTesting)
            Method mValidate = clazz.getDeclaredMethod(
                    "validateIntent", Context.class, Intent.class);
            mValidate.setAccessible(true);
            // should be no exception
            mValidate.invoke(null, ctx, navIntentLL);
            mValidate.invoke(null, ctx, navIntentQ);
            mValidate.invoke(null, ctx, phoneDial);
            mValidate.invoke(null, ctx, phoneCall);
            mValidate.invoke(null, ctx, svcIntent);

            Log.i("PendingIntentTest", "validateIntent passed");

            // 5) isLatitudeLongitude (private static)
            Method mIsLatLng = clazz.getDeclaredMethod(
                    "isLatitudeLongitude", String.class);
            mIsLatLng.setAccessible(true);
            boolean okLL = (boolean) mIsLatLng.invoke(null, "37.4219983,-122.084");
            Log.i("PendingIntentTest", "isLatitudeLongitude - " + okLL);

            // 6) getQueryString (private static)
            Method mGetQuery = clazz.getDeclaredMethod(
                    "getQueryString", Uri.class);
            mGetQuery.setAccessible(true);
            String q1 = (String) mGetQuery.invoke(null, navUriLL);
            String q2 = (String) mGetQuery.invoke(null, navUriQ);
            Log.i("PendingIntentTest", "getQueryString(LL) - " + q1);
            Log.i("PendingIntentTest", "getQueryString(Q) - " + q2);

            // 7) validatePhoneIntentIsValid
            Method mValidatePhone = clazz.getDeclaredMethod(
                    "validatePhoneIntentIsValid", Intent.class);
            mValidatePhone.setAccessible(true);
            mValidatePhone.invoke(null, phoneDial);
            mValidatePhone.invoke(null, phoneCall);
            Log.i("PendingIntentTest", "validatePhoneIntentIsValid passed");

            // 8) validateNavigationIntentIsValid
            Method mValidateNav = clazz.getDeclaredMethod(
                    "validateNavigationIntentIsValid", Intent.class);
            mValidateNav.setAccessible(true);
            mValidateNav.invoke(null, navIntentLL);
            mValidateNav.invoke(null, navIntentQ);
            Log.i("PendingIntentTest", "validateNavigationIntentIsValid passed");

            // 9) now call getCarApp (public static)
            Method mGetCarApp = clazz.getMethod(
                    "getCarApp", Context.class, int.class, Intent.class, int.class);

            PendingIntent piNav = (PendingIntent) mGetCarApp.invoke(
                    null, ctx, 42, navIntentLL, 0);
            Log.i("PendingIntentTest", "getCarApp(nav) - " + piNav);

            PendingIntent piDial = (PendingIntent) mGetCarApp.invoke(
                    null, ctx, 43, phoneDial, 0);
            Log.i("PendingIntentTest", "getCarApp(dial) - " + piDial);

            PendingIntent piSvc = (PendingIntent) mGetCarApp.invoke(
                    null, ctx, 44, svcIntent, 0);
            Log.i("PendingIntentTest", "getCarApp(explicit) - " + piSvc);

            // 10) Call createForAutomotive & createForProjected directly (private static)
            Method mCreateAuto = clazz.getDeclaredMethod(
                    "createForAutomotive", Context.class, int.class, Intent.class, int.class);
            Method mCreateProj = clazz.getDeclaredMethod(
                    "createForProjected", Context.class, int.class, Intent.class, int.class);
            mCreateAuto.setAccessible(true);
            mCreateProj.setAccessible(true);

            PendingIntent autoNav = (PendingIntent) mCreateAuto.invoke(
                    null, ctx, 100, navIntentLL, PendingIntent.FLAG_UPDATE_CURRENT);
            Log.i("PendingIntentTest", "createForAutomotive - " + autoNav);

            PendingIntent projSvc = (PendingIntent) mCreateProj.invoke(
                    null, ctx, 101, svcIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            Log.i("PendingIntentTest", "createForProjected - " + projSvc);

            Log.i("PendingIntentTest", "✅ CarPendingIntent reflection exercise completed successfully");

        } catch (InvocationTargetException ite) {
            Log.e("PendingIntentTest",
                    "Underlying exception in CarPendingIntent: ", ite.getTargetException());
        } catch (Exception e) {
            Log.e("PendingIntentTest", "Error exercising CarPendingIntent", e);
        }
    }


    public void exerciseOpenMicrophoneResponse() {
        final String TAG = "ExerciseOpenMicrophone";
        try {
            Class<?> respClass = Class.forName("androidx.car.app.media.OpenMicrophoneResponse");
            Class<?> builderClass = Class.forName("androidx.car.app.media.OpenMicrophoneResponse$Builder");
            Class<?> carAudioCallbackClass = Class.forName("androidx.car.app.media.CarAudioCallback");

            // Create a dynamic proxy
            Object carAudioCallbackProxy = Proxy.newProxyInstance(
                    carAudioCallbackClass.getClassLoader(),
                    new Class<?>[]{carAudioCallbackClass},
                    new InvocationHandler() {
                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                            // simple no-op proxy: log calls (if you want) and return default value
                            Log.d(TAG, "CarAudioCallback." + method.getName() + " invoked");
                            // return null or default primitive wrapper
                            Class<?> r = method.getReturnType();
                            if (r == boolean.class) return false;
                            if (r == byte.class) return (byte) 0;
                            if (r == short.class) return (short) 0;
                            if (r == int.class) return 0;
                            if (r == long.class) return 0L;
                            if (r == float.class) return 0f;
                            if (r == double.class) return 0d;
                            return null;
                        }
                    }
            );

            // Find Builder ctor
            Constructor<?> builderCtor = builderClass.getConstructor(carAudioCallbackClass);
            Object builder = builderCtor.newInstance(carAudioCallbackProxy);

            // simulate mic data
            ParcelFileDescriptor[] pfds = ParcelFileDescriptor.createReliablePipe();
            ParcelFileDescriptor readSide = pfds[0];
            ParcelFileDescriptor writeSide = pfds[1];

            // payload
            OutputStream out = new ParcelFileDescriptor.AutoCloseOutputStream(writeSide);
            byte[] testBytes = "123456".getBytes(StandardCharsets.UTF_8);
            try {
                out.write(testBytes);
                out.flush();
                out.close();
            } catch (Exception e) {
                Log.e(TAG, "Error writing to microphone pipe", e);
                try { out.close(); } catch (Exception ignored) {}
            }

            // Call builder.setCarMicrophoneDescriptor(readSide)
            Method setDescMethod = builderClass.getMethod("setCarMicrophoneDescriptor",
                    ParcelFileDescriptor.class);
            setDescMethod.invoke(builder, readSide);

            // Call builder.build()
            Method buildMethod = builderClass.getMethod("build");
            Object openMicResponse = buildMethod.invoke(builder);

            // Call getCarAudioCallback()
            Method getCallbackMethod = respClass.getMethod("getCarAudioCallback");
            Object callbackDelegate = getCallbackMethod.invoke(openMicResponse);
            Log.d(TAG, "getCarAudioCallback() returned: " + (callbackDelegate == null ? "null" : callbackDelegate.getClass().getName()));

            // Call getCarMicrophoneInputStream() and read the bytes
            Method getStreamMethod = respClass.getMethod("getCarMicrophoneInputStream");
            InputStream in = (InputStream) getStreamMethod.invoke(openMicResponse);

            byte[] buf = new byte[1024];
            int read = 0;
            StringBuilder sb = new StringBuilder();
            try {
                read = in.read(buf);
                if (read > 0) {
                    String s = new String(buf, 0, read, StandardCharsets.UTF_8);
                    sb.append(s);
                }
                while ((read = in.read(buf)) > 0) {
                    sb.append(new String(buf, 0, read, StandardCharsets.UTF_8));
                }
            } catch (Exception e) {
                Log.w(TAG, "Exception while reading microphone stream (may be fine)", e);
            } finally {
                try { in.close(); } catch (Exception ignored) {}
            }

            Log.d(TAG, "Read from getCarMicrophoneInputStream(): \"" + sb.toString() + "\"");

            try { readSide.close(); } catch (Exception ignored) {}

            Log.d(TAG, "OpenMicrophoneResponse reflection exercise completed successfully.");

        } catch (ClassNotFoundException cnfe) {
            Log.e("ExerciseOpenMicrophone", "OpenMicrophoneResponse or related class not found", cnfe);
        } catch (NoSuchMethodException nsme) {
            Log.e("ExerciseOpenMicrophone", "Expected method not found via reflection", nsme);
        } catch (InvocationTargetException ite) {
            Log.e("ExerciseOpenMicrophone", "Target method threw an exception", ite.getCause());
        } catch (Exception e) {
            Log.e("ExerciseOpenMicrophone", "Unexpected error exercising OpenMicrophoneResponse", e);
        }
    }

    @SuppressWarnings({"unchecked"})
    public void exerciseRegisterClimateStateRequest() {
        final String TAG = "ExerciseClimateReq";
        try {
            Class<?> reqClass = Class.forName("androidx.car.app.hardware.climate.RegisterClimateStateRequest");
            Class<?> builderClass = Class.forName("androidx.car.app.hardware.climate.RegisterClimateStateRequest$Builder");
            Class<?> carClimateFeatureClass = Class.forName("androidx.car.app.hardware.climate.CarClimateFeature");
            Class<?> carClimateFeatureBuilderClass =
                    Class.forName("androidx.car.app.hardware.climate.CarClimateFeature$Builder");

            // Access static ALL_FEATURES (package-private) to get valid flags
            java.lang.reflect.Field allFeaturesField = reqClass.getDeclaredField("ALL_FEATURES");
            allFeaturesField.setAccessible(true);
            java.util.Set<Integer> allFlags = (java.util.Set<Integer>) allFeaturesField.get(null);
            Log.i(TAG, "ALL_FEATURES flags count: " + (allFlags == null ? "null" : allFlags.size()));

            java.lang.reflect.Constructor<?> builderCtor = builderClass.getConstructor(boolean.class);
            Object builderTrue = builderCtor.newInstance(true);
            java.lang.reflect.Method buildMethod = builderClass.getMethod("build");
            Object requestAll = buildMethod.invoke(builderTrue);

            // Invoke public getClimateRegisterFeatures()
            java.lang.reflect.Method getFeaturesMethod = reqClass.getMethod("getClimateRegisterFeatures");
            java.util.List<?> featuresFromAll = (java.util.List<?>) getFeaturesMethod.invoke(requestAll);
            Log.i(TAG, "Request (registerAll=true) features count: " + (featuresFromAll == null ? "null" : featuresFromAll.size()));
            Log.i(TAG, "RequestAll.toString(): " + requestAll.toString());

            Object builderFalse = builderCtor.newInstance(false);
            // find addClimateRegisterFeatures
            Class<?> featureArrayClass = java.lang.reflect.Array.newInstance(carClimateFeatureClass, 0).getClass();
            java.lang.reflect.Method addMethod = builderClass.getMethod("addClimateRegisterFeatures", featureArrayClass);

            // get CarClimateFeature.Builder(int) constructor and build() method
            java.lang.reflect.Constructor<?> featureBuilderCtor = carClimateFeatureBuilderClass.getConstructor(int.class);
            java.lang.reflect.Method featureBuildMethod = carClimateFeatureBuilderClass.getMethod("build");

            // For each flag in ALL_FEATURES create a CarClimateFeature and add it to the builder
            java.util.List<Object> builtFeatures = new java.util.ArrayList<>();
            for (Integer flag : allFlags) {
                try {
                    Object featureBuilder = featureBuilderCtor.newInstance(flag.intValue());
                    Object feature = featureBuildMethod.invoke(featureBuilder);
                    builtFeatures.add(feature);
                } catch (InvocationTargetException ite) {
                    // If builder throws for some flags, log & continue
                    Log.w(TAG, "Failed building CarClimateFeature for flag " + flag, ite.getTargetException());
                }
            }

            // Convert list array of CarClimateFeature to call the varargs method
            Object featureArray = java.lang.reflect.Array.newInstance(carClimateFeatureClass, builtFeatures.size());
            for (int i = 0; i < builtFeatures.size(); i++) {
                java.lang.reflect.Array.set(featureArray, i, builtFeatures.get(i));
            }

            // invoke addClimateRegisterFeatures(array)
            addMethod.invoke(builderFalse, new Object[]{featureArray});

            Object requestByAdd = buildMethod.invoke(builderFalse);
            java.util.List<?> featuresFromAdd = (java.util.List<?>) getFeaturesMethod.invoke(requestByAdd);
            Log.i(TAG, "Request (explicit add) features count: " + (featuresFromAdd == null ? "null" : featuresFromAdd.size()));
            Log.i(TAG, "RequestByAdd.toString(): " + requestByAdd.toString());

            // Compare equals/hashCode
            boolean equals = requestAll.equals(requestByAdd);
            int hashAll = requestAll.hashCode();
            int hashAdd = requestByAdd.hashCode();
            Log.i(TAG, "requestAll.equals(requestByAdd) = " + equals);
            Log.i(TAG, "hash codes: all=" + hashAll + " add=" + hashAdd);

            // Call the private constructAllFeatures() method reflectively
            java.lang.reflect.Method constructAllFeaturesMethod = reqClass.getDeclaredMethod("constructAllFeatures");
            constructAllFeaturesMethod.setAccessible(true);
            java.util.List<?> constructedList = (java.util.List<?>) constructAllFeaturesMethod.invoke(requestAll);
            Log.i(TAG, "constructAllFeatures() returned " + (constructedList == null ? "null" : constructedList.size()) + " elements");

            int limit = 5;
            for (int i = 0; i < Math.min(limit, constructedList.size()); i++) {
                Object elem = constructedList.get(i);
                Log.i(TAG, "constructedList[" + i + "] = " + String.valueOf(elem));
            }

            Log.i(TAG, "featuresFromAll.toString() (first items): " +
                    (featuresFromAll.size() > 0 ? featuresFromAll.get(0).toString() : "<empty>"));
            Log.i(TAG, "featuresFromAdd.toString() (first items): " +
                    (featuresFromAdd.size() > 0 ? featuresFromAdd.get(0).toString() : "<empty>"));

            Log.i(TAG, "Finished exercising RegisterClimateStateRequest. " +
                    "allFeatures=" + allFlags.size() +
                    ", requestAllFeatures=" + featuresFromAll.size() +
                    ", requestByAdd=" + featuresFromAdd.size());

        } catch (ClassNotFoundException e) {
            Log.e("ExerciseClimateReq", "Required class not found", e);
        } catch (NoSuchMethodException e) {
            Log.e("ExerciseClimateReq", "Expected method not found", e);
        } catch (NoSuchFieldException e) {
            Log.e("ExerciseClimateReq", "Expected field not found", e);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            Log.e("ExerciseClimateReq", "Reflection creation/invocation failed", e);
        } catch (Exception e) {
            Log.e("ExerciseClimateReq", "Unexpected failure while exercising RegisterClimateStateRequest", e);
        }
    }

    @SuppressLint({"RestrictedApi", "VisibleForTests"})
    public void exerciseActionsConstraints() {
        final String TAG = "ExerciseActionsConstraints";
        StringBuilder summary = new StringBuilder();
        try {
            Log.d(TAG, "Starting exerciseActionsConstraints()");

            Class<?> cls = ActionsConstraints.class;
            Log.d(TAG, "Found class: " + cls.getName());

            String[] staticNames = {
                    "ACTIONS_CONSTRAINTS_HEADER",
                    "ACTIONS_CONSTRAINTS_MULTI_HEADER",
                    "ACTIONS_CONSTRAINTS_BODY",
                    "ACTIONS_CONSTRAINTS_SIMPLE",
                    "ACTIONS_CONSTRAINTS_NAVIGATION",
                    "ACTIONS_CONSTRAINTS_MAP",
                    "ACTIONS_CONSTRAINTS_ROW",
                    "ACTIONS_CONSTRAINTS_FAB"
            };

            for (String name : staticNames) {
                try {
                    Field f = cls.getField(name);
                    Object instance = f.get(null);
                    Log.d(TAG, "Static field " + name + " -> " + String.valueOf(instance));
                    Method getMax = cls.getMethod("getMaxActions");
                    Object max = getMax.invoke(instance);
                    Log.d(TAG, "  getMaxActions() = " + max);
                    Method getTitleConstraints = cls.getMethod("getTitleTextConstraints");
                    Object titleConstraints = getTitleConstraints.invoke(instance);
                    Log.d(TAG, "  getTitleTextConstraints() = " + titleConstraints);
                } catch (NoSuchFieldException nsf) {
                    Log.w(TAG, "No static field " + name + " on ActionsConstraints");
                }
            }

            @SuppressLint({"RestrictedApi", "VisibleForTests"}) ActionsConstraints.Builder builder = new ActionsConstraints.Builder();
            builder.setMaxActions(3)
                    .setMaxPrimaryActions(1)
                    .setMaxCustomTitles(1)
                    .setRequireActionIcons(true)
                    .setRequireActionBackgroundColor(true)
                    .setOnClickListenerAllowed(true)
                    .setRestrictBackgroundColorToPrimaryAction(true)
                    .setTitleTextConstraints(CarTextConstraints.COLOR_ONLY)
                    .addAllowedActionType(Action.TYPE_CUSTOM);

            ActionsConstraints constraints = builder.build();
            Log.d(TAG, "Built ActionsConstraints via Builder: " + constraints);

            // Call public getters
            int maxActions = constraints.getMaxActions();
            int maxPrimaryActions = constraints.getMaxPrimaryActions();
            int maxCustomTitles = constraints.getMaxCustomTitles();
            CarTextConstraints titleTextConstraints = constraints.getTitleTextConstraints();
            Set<Integer> required = constraints.getRequiredActionTypes();
            Set<Integer> disallowed = constraints.getDisallowedActionTypes();
            Set<Integer> allowed = constraints.getAllowedActionTypes();
            boolean requireIcons = constraints.areActionIconsRequired();
            boolean requireBg = constraints.isActionBackgroundColorRequired();
            boolean clickAllowed = constraints.isOnClickListenerAllowed();
            boolean restrictBgPrimary = constraints.restrictBackgroundColorToPrimaryAction();

            Log.d(TAG, "getMaxActions() = " + maxActions);
            Log.d(TAG, "getMaxPrimaryActions() = " + maxPrimaryActions);
            Log.d(TAG, "getMaxCustomTitles() = " + maxCustomTitles);
            Log.d(TAG, "getTitleTextConstraints() = " + titleTextConstraints);
            Log.d(TAG, "getRequiredActionTypes() = " + required);
            Log.d(TAG, "getDisallowedActionTypes() = " + disallowed);
            Log.d(TAG, "getAllowedActionTypes() = " + allowed);
            Log.d(TAG, "areActionIconsRequired() = " + requireIcons);
            Log.d(TAG, "isActionBackgroundColorRequired() = " + requireBg);
            Log.d(TAG, "isOnClickListenerAllowed() = " + clickAllowed);
            Log.d(TAG, "restrictBackgroundColorToPrimaryAction() = " + restrictBgPrimary);

            summary.append("Built constraints: maxActions=").append(maxActions).append(", maxPrimary=")
                    .append(maxPrimaryActions).append(", maxCustomTitles=").append(maxCustomTitles).append("\n");

            //Validate with an empty list
            try {
                constraints.validateOrThrow(Collections.emptyList());
                Log.d(TAG, "validateOrThrow(emptyList) succeeded (expected)");
                summary.append("validateOrThrow(empty) succeeded\n");
            } catch (IllegalArgumentException iae) {
                Log.e(TAG, "validateOrThrow(emptyList) threw", iae);
                summary.append("validateOrThrow(empty) threw: ").append(iae.getMessage()).append("\n");
            }

            // Use the copy-constructor
            ActionsConstraints.Builder copyBuilder = new ActionsConstraints.Builder(constraints);
            copyBuilder.setMaxActions(2)
                    .setRequireActionIcons(false);
            ActionsConstraints variant = copyBuilder.build();
            Log.d(TAG, "Variant constraints built via copy-builder: " + variant);
            summary.append("Variant built with maxActions=").append(variant.getMaxActions()).append("\n");

            // Reflectively access private fields
            try {
                Field fMax = cls.getDeclaredField("mMaxActions");
                fMax.setAccessible(true);
                Object privateMaxVal = fMax.get(constraints);
                Log.d(TAG, "private mMaxActions = " + privateMaxVal);
                summary.append("private mMaxActions=").append(privateMaxVal).append("\n");
            } catch (NoSuchFieldException nsf) {
                Log.w(TAG, "No private mMaxActions field found (API difference?)");
            }

            try {
                Field fRequired = cls.getDeclaredField("mRequiredActionTypes");
                fRequired.setAccessible(true);
                Object privateRequiredSet = fRequired.get(constraints);
                Log.d(TAG, "private mRequiredActionTypes = " + privateRequiredSet);
                summary.append("private mRequiredActionTypes=").append(privateRequiredSet).append("\n");
            } catch (NoSuchFieldException nsf) {
                Log.w(TAG, "No private mRequiredActionTypes field found (API difference?)");
            }

            try {
                Field fRestrictBg = cls.getDeclaredField("mRestrictBackgroundColorToPrimaryAction");
                fRestrictBg.setAccessible(true);
                Object privateRestrict = fRestrictBg.get(constraints);
                Log.d(TAG, "private mRestrictBackgroundColorToPrimaryAction = " + privateRestrict);
                summary.append("private mRestrictBackgroundColorToPrimaryAction=").append(privateRestrict).append("\n");
            } catch (NoSuchFieldException nsf) {
                Log.w(TAG, "No private mRestrictBackgroundColorToPrimaryAction field found (API difference?)");
            }

            // Exercise validation with a list of actions if the Action class is usable
            try {
                Class<?> actionCls = Action.class;
                Action sampleAction = null;
                try {
                    // Try Action.Builder if present
                    Class<?> actionBuilderCls = Class.forName("androidx.car.app.model.Action$Builder");
                    Constructor<?> abCtor = actionBuilderCls.getConstructor();
                    Object abInstance = abCtor.newInstance();
                    try {
                        Method setTitle = actionBuilderCls.getMethod("setTitle", CharSequence.class);
                        setTitle.invoke(abInstance, "Sample");
                    } catch (NoSuchMethodException e) {

                    }
                    Method buildMethod = actionBuilderCls.getMethod("build");
                    Object builtAction = buildMethod.invoke(abInstance);
                    if (builtAction instanceof Action) {
                        sampleAction = (Action) builtAction;
                    }
                } catch (ClassNotFoundException cnf) {
                    // Action.Builder not present - attempt a simple public constructor on Action
                    try {
                        Constructor<?> actCtor = actionCls.getConstructor(int.class, CharSequence.class);
                        Object o = actCtor.newInstance(Action.TYPE_CUSTOM, "Sample");
                        if (o instanceof Action) sampleAction = (Action) o;
                    } catch (NoSuchMethodException ignored) {
                    }
                }

                if (sampleAction != null) {
                    List<Action> oneList = Collections.singletonList(sampleAction);
                    try {
                        constraints.validateOrThrow(oneList);
                        Log.d(TAG, "validateOrThrow(sampleAction) succeeded");
                        summary.append("validateOrThrow(sampleAction) succeeded\n");
                    } catch (IllegalArgumentException iae) {
                        Log.w(TAG, "validateOrThrow(sampleAction) threw: " + iae.getMessage());
                        summary.append("validateOrThrow(sampleAction) threw: ").append(iae.getMessage()).append("\n");
                    }
                } else {
                    Log.w(TAG, "Could not create sample Action via reflection - skipping action validation test");
                    summary.append("sample Action creation skipped (no compatible constructor/builder found)\n");
                }
            } catch (Throwable t) {
                Log.w(TAG, "Action creation/validation attempt encountered issue", t);
                summary.append("Action creation attempt encountered issue: ").append(t.getClass().getSimpleName()).append("\n");
            }

            Log.d(TAG, "Finished exerciseActionsConstraints()");
        } catch (Exception e) {
            Log.e("ExerciseActionsConstraints", "Exception exercising ActionsConstraints", e);
            summary.append("Unexpected exception: ").append(e.toString()).append("\n");
        }

        String result = summary.toString();
        Log.d("ExerciseActionsConstraintsSummary", result);
    }

    public void exerciseCarIconConstraints() {

        final String TAG = "CarIconConstraintsTest";

        try {
            Context context = getCarContext();
            Class<?> constraintsClass = Class.forName(
                    "androidx.car.app.model.constraints.CarIconConstraints");

            try {
                Field unconField = constraintsClass.getField("UNCONSTRAINED");
                Object uncon = unconField.get(null);
                Log.i(TAG, "UNCONSTRAINED field value: " + uncon);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                Log.w(TAG, "Could not read UNCONSTRAINED field: " + e);
            }

            try {
                Field defaultField = constraintsClass.getField("DEFAULT");
                Object def = defaultField.get(null);
                Log.i(TAG, "DEFAULT field value: " + def);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                Log.w(TAG, "Could not read DEFAULT field: " + e);
            }

            Constructor<?> privateCtor = null;
            try {
                privateCtor = constraintsClass.getDeclaredConstructor(int[].class);
                privateCtor.setAccessible(true);
            } catch (NoSuchMethodException e) {
                for (Constructor<?> c : constraintsClass.getDeclaredConstructors()) {
                    Class<?>[] params = c.getParameterTypes();
                    if (params.length == 1 && params[0].isArray()
                            && params[0].getComponentType() == int.class) {
                        privateCtor = c;
                        privateCtor.setAccessible(true);
                        break;
                    }
                }
            }

            Object customConstraints = null;
            if (privateCtor != null) {
                int[] allowed = new int[]{
                        IconCompat.TYPE_BITMAP,
                        IconCompat.TYPE_RESOURCE
                };
                // Reflection newInstance expecting Object[] wrapper
                customConstraints = privateCtor.newInstance((Object) allowed);
                Log.i(TAG, "Created custom CarIconConstraints instance via private ctor: "
                        + customConstraints);
            } else {
                Log.w(TAG, "Private constructor not found; proceeding using static fields only.");
                // fall back to using DEFAULT if available
                try {
                    customConstraints = constraintsClass.getField("DEFAULT").get(null);
                } catch (Exception ex) {
                    customConstraints = null;
                }
            }

            // Find methods: validateOrThrow() and checkSupportedIcon()
            Method validateMethod = null;
            Method checkSupportedMethod = null;
            for (Method m : constraintsClass.getMethods()) { // public methods
                if ("validateOrThrow".equals(m.getName()) && m.getParameterTypes().length == 1) {
                    validateMethod = m;
                } else if ("checkSupportedIcon".equals(m.getName())
                        && m.getParameterTypes().length == 1) {
                    checkSupportedMethod = m;
                }
            }

            if (validateMethod == null) {
                for (Method m : constraintsClass.getDeclaredMethods()) {
                    if ("validateOrThrow".equals(m.getName()) && m.getParameterTypes().length == 1) {
                        validateMethod = m;
                        validateMethod.setAccessible(true);
                    }
                }
            }

            if (checkSupportedMethod == null) {
                for (Method m : constraintsClass.getDeclaredMethods()) {
                    if ("checkSupportedIcon".equals(m.getName())
                            && m.getParameterTypes().length == 1) {
                        checkSupportedMethod = m;
                        checkSupportedMethod.setAccessible(true);
                    }
                }
            }

            if (validateMethod != null && customConstraints != null) {
                try {
                    validateMethod.invoke(customConstraints, new Object[]{null});
                    Log.i(TAG, "validateOrThrow(null) invoked successfully (expected early return).");
                } catch (InvocationTargetException ite) {
                    Log.e(TAG, "validateOrThrow(null) threw: " + ite.getCause(), ite);
                }
            } else {
                Log.w(TAG, "validateOrThrow method or constraints instance is missing; skipping.");
            }

            if (checkSupportedMethod == null) {
                Log.w(TAG, "checkSupportedIcon method not found; aborting icon tests.");
                return;
            }
            if (customConstraints == null) {
                Log.w(TAG, "No constraints instance available; skipping icon checks.");
                return;
            }

            // TYPE_BITMAP
            try {
                Bitmap bmp = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
                IconCompat iconBitmap = IconCompat.createWithBitmap(bmp);
                Object result = checkSupportedMethod.invoke(customConstraints, iconBitmap);
                Log.i(TAG, "checkSupportedIcon(TYPE_BITMAP) ok -> returned: " + result);
            } catch (InvocationTargetException ite) {
                Log.e(TAG, "Unexpected failure for TYPE_BITMAP: " + ite.getCause(), ite);
            }

            // TYPE_RESOURCE
            try {
                IconCompat iconRes = IconCompat.createWithResource(context, android.R.drawable.ic_dialog_info);
                Object result = checkSupportedMethod.invoke(customConstraints, iconRes);
                Log.i(TAG, "checkSupportedIcon(TYPE_RESOURCE) ok -> returned: " + result);
            } catch (InvocationTargetException ite) {
                Log.e(TAG, "Unexpected failure for TYPE_RESOURCE: " + ite.getCause(), ite);
            }

            // TYPE_URI
            try {
                IconCompat iconContentUri = IconCompat.createWithContentUri("content://com.example/some");
                Object result = checkSupportedMethod.invoke(customConstraints, iconContentUri);
                Log.i(TAG, "checkSupportedIcon(content://...) ok -> returned: " + result);
            } catch (InvocationTargetException ite) {
                Log.e(TAG, "Unexpected failure for content:// URI: " + ite.getCause(), ite);
            }

            // TYPE_URI
            try {
                IconCompat iconHttpUri = IconCompat.createWithContentUri("http://example.com/some");
                try {
                    checkSupportedMethod.invoke(customConstraints, iconHttpUri);
                    Log.e(TAG, "checkSupportedIcon(http://...) unexpectedly succeeded (expected failure).");
                } catch (InvocationTargetException ite) {
                    Throwable cause = ite.getCause();
                    Log.i(TAG, "checkSupportedIcon(http://...) threw (expected): " + cause);
                }
            } catch (Exception e) {
                Log.e(TAG, "Could not construct or check http:// IconCompat: " + e, e);
            }


            try {
                // build a normal bitmap icon then change its type
                IconCompat iconForTamper = IconCompat.createWithBitmap(Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888));
                try {
                    // reflectively locate mType field and set to a dummy value
                    java.lang.reflect.Field mTypeField = IconCompat.class.getDeclaredField("mType");
                    mTypeField.setAccessible(true);
                    mTypeField.setInt(iconForTamper, -999); // unsupported type
                    try {
                        checkSupportedMethod.invoke(customConstraints, iconForTamper);
                        Log.e(TAG, "checkSupportedIcon(dummy-type) unexpectedly passed (expected failure).");
                    } catch (InvocationTargetException ite) {
                        Log.i(TAG, "checkSupportedIcon(dummy-type) threw (expected): " + ite.getCause());
                    }
                } catch (NoSuchFieldException nsf) {
                    Log.w(TAG, "IconCompat has no accessible mType field to tamper with (skipping dummy-type test).");
                }
            } catch (Exception e) {
                Log.w(TAG, "Could not create or mutate IconCompat for dummy-type test: " + e);
            }

            Log.i(TAG, "CarIconConstraints reflection exercise completed.");

        } catch (ClassNotFoundException e) {
            Log.e(TAG, "CarIconConstraints class not found in runtime: " + e);
        } catch (InvocationTargetException e) {
            Log.e(TAG, "InvocationTargetException while invoking reflected code: " + e.getCause(), e);
        } catch (Exception e) {
            Log.e(TAG, "Unexpected exception during CarIconConstraints reflection test: " + e, e);
        }
    }


    public void exerciseAppManagerReflection() {
        final String TAG = "ExerciseAppManager";
        try {
            Object appManager = getCarContext().getCarService(androidx.car.app.AppManager.class);
            Class<?> amClass = appManager.getClass();
            Log.i(TAG, "Found AppManager instance: " + amClass.getName());

            //  setSurfaceCallback(SurfaceCallback)
            try {
                // minimal SurfaceCallback implementation
                androidx.car.app.SurfaceCallback surfaceCallback = new androidx.car.app.SurfaceCallback() {
                    @SuppressLint("RestrictedApi")
                    public void onSurfaceAvailable(androidx.car.app.serialization.Bundleable surfaceContainer,
                                                   @SuppressLint("RestrictedApi") androidx.car.app.IOnDoneCallback callback) {
                        Log.i(TAG, "SurfaceCallback.onSurfaceAvailable called");
                        try {
                            if (callback != null) {
                                callback.onSuccess(null);
                            }
                        } catch (android.os.RemoteException e) {
                            Log.e(TAG, "SurfaceCallback onSuccess remote exception", e);
                        }
                    }

                    public void onVisibleAreaChanged(android.graphics.Rect visibleArea,
                                                     @SuppressLint("RestrictedApi") androidx.car.app.IOnDoneCallback callback) {
                        Log.i(TAG, "SurfaceCallback.onVisibleAreaChanged");
                    }

                    public void onStableAreaChanged(android.graphics.Rect stableArea,
                                                    @SuppressLint("RestrictedApi") androidx.car.app.IOnDoneCallback callback) {
                        Log.i(TAG, "SurfaceCallback.onStableAreaChanged");
                    }

                    public void onSurfaceDestroyed(androidx.car.app.serialization.Bundleable surfaceContainer,
                                                   @SuppressLint("RestrictedApi") androidx.car.app.IOnDoneCallback callback) {
                        Log.i(TAG, "SurfaceCallback.onSurfaceDestroyed");
                    }

                    @Override public void onScroll(float distanceX, float distanceY) {}
                    @Override public void onFling(float velocityX, float velocityY) {}
                    @Override public void onScale(float focusX, float focusY, float scaleFactor) {}
                    @Override public void onClick(float x, float y) {}
                };

                java.lang.reflect.Method setSurfaceCallback =
                        amClass.getMethod("setSurfaceCallback", androidx.car.app.SurfaceCallback.class);
                setSurfaceCallback.invoke(appManager, surfaceCallback);
                Log.i(TAG, "Called setSurfaceCallback(surfaceCallback)");
            } catch (NoSuchMethodException e) {
                Log.w(TAG, "setSurfaceCallback not found on AppManager (maybe API mismatch)", e);
            } catch (Throwable t) {
                Log.e(TAG, "Error calling setSurfaceCallback", t);
            }

            // invalidate()
            try {
                java.lang.reflect.Method invalidate = amClass.getMethod("invalidate");
                invalidate.invoke(appManager);
                Log.i(TAG, "Called invalidate()");
            } catch (NoSuchMethodException e) {
                Log.w(TAG, "invalidate() not found on AppManager", e);
            } catch (Throwable t) {
                Log.e(TAG, "Error calling invalidate()", t);
            }

            // showToast(CharSequence, int)
            try {
                java.lang.reflect.Method showToast =
                        amClass.getMethod("showToast", CharSequence.class, int.class);
                CharSequence toastText = "Reflection toast test";
                int duration = 0; // safe default; host library defines CarToast.Duration - 0 is acceptable as test
                showToast.invoke(appManager, toastText, duration);
                Log.i(TAG, "Called showToast(\"" + toastText + "\", " + duration + ")");
            } catch (NoSuchMethodException e) {
                Log.w(TAG, "showToast(CharSequence,int) not found", e);
            } catch (Throwable t) {
                Log.e(TAG, "Error calling showToast", t);
            }

            // showAlert(Alert)
            try {
                Class<?> alertClass = Class.forName("androidx.car.app.model.Alert");
                Object alertInstance = null;

                try {
                    Class<?> builderClass = Class.forName("androidx.car.app.model.Alert$Builder");
                    Object builder = null;
                    try {
                        java.lang.reflect.Constructor<?> c = builderClass.getConstructor(CharSequence.class);
                        builder = c.newInstance("Reflected alert");
                    } catch (NoSuchMethodException ignored) {
                        try {
                            builder = builderClass.getConstructor().newInstance();
                            // try to set a title/body if setters exist
                            try {
                                java.lang.reflect.Method setTitle = builderClass.getMethod("setTitle", CharSequence.class);
                                setTitle.invoke(builder, "Reflected alert");
                            } catch (NoSuchMethodException ignored2) {}
                            try {
                                java.lang.reflect.Method setBody = builderClass.getMethod("setBody", CharSequence.class);
                                setBody.invoke(builder, "Body from reflection");
                            } catch (NoSuchMethodException ignored2) {}
                        } catch (NoSuchMethodException ignored3) {
                            builder = null;
                        }
                    }
                    if (builder != null) {
                        try {
                            java.lang.reflect.Method build = builderClass.getMethod("build");
                            alertInstance = build.invoke(builder);
                        } catch (NoSuchMethodException ignored) {
                            alertInstance = null;
                        }
                    }
                } catch (ClassNotFoundException cnfe) {
                }

                if (alertInstance == null) {
                    try {
                        java.lang.reflect.Constructor<?> alertCtor = alertClass.getDeclaredConstructor();
                        alertCtor.setAccessible(true);
                        alertInstance = alertCtor.newInstance();
                    } catch (NoSuchMethodException ignored) {
                        alertInstance = null;
                    }
                }

                if (alertInstance != null) {
                    java.lang.reflect.Method showAlert = amClass.getMethod("showAlert", alertClass);
                    showAlert.invoke(appManager, alertInstance);
                    Log.i(TAG, "Called showAlert(alertInstance)");
                } else {
                    Log.w(TAG, "Could not construct Alert instance reflectively; skipping showAlert");
                }
            } catch (ClassNotFoundException e) {
                Log.w(TAG, "Alert class not found in this runtime; skipping showAlert", e);
            } catch (NoSuchMethodException e) {
                Log.w(TAG, "showAlert(Alert) not present on AppManager; skipping", e);
            } catch (Throwable t) {
                Log.e(TAG, "Error trying to call showAlert", t);
            }

            // dismissAlert(int)
            try {
                java.lang.reflect.Method dismissAlert = amClass.getMethod("dismissAlert", int.class);
                dismissAlert.invoke(appManager, 42);
                Log.i(TAG, "Called dismissAlert(42)");
            } catch (NoSuchMethodException e) {
                Log.w(TAG, "dismissAlert(int) not present", e);
            } catch (Throwable t) {
                Log.e(TAG, "Error calling dismissAlert", t);
            }

            // openMicrophone(OpenMicrophoneRequest) -> OpenMicrophoneResponse
            try {
                Class<?> reqClass = Class.forName("androidx.car.app.media.OpenMicrophoneRequest");
                Object reqInstance = null;

                try {
                    Class<?> builderClass = Class.forName("androidx.car.app.media.OpenMicrophoneRequest$Builder");
                    try {
                        Class<?> carAudioCallbackClass = Class.forName("androidx.car.app.media.CarAudioCallback");
                        java.lang.reflect.Constructor<?> ctor = builderClass.getConstructor(carAudioCallbackClass);
                        Object carAudioCallback = java.lang.reflect.Proxy.newProxyInstance(
                                carAudioCallbackClass.getClassLoader(),
                                new Class<?>[] { carAudioCallbackClass },
                                (proxy, method, args) -> {
                                    return null;
                                });
                        Object builder = ctor.newInstance(carAudioCallback);
                        try {
                            java.lang.reflect.Method build = builderClass.getMethod("build");
                            reqInstance = build.invoke(builder);
                        } catch (NoSuchMethodException ignored) {
                        }
                    } catch (NoSuchMethodException ignoredCtor) {
                        try {
                            java.lang.reflect.Constructor<?> c2 = builderClass.getDeclaredConstructor();
                            c2.setAccessible(true);
                            Object builder = c2.newInstance();
                            try {
                                java.lang.reflect.Method build = builderClass.getMethod("build");
                                reqInstance = build.invoke(builder);
                            } catch (NoSuchMethodException ignored) {}
                        } catch (NoSuchMethodException ignored2) {}
                    }
                } catch (ClassNotFoundException cnfe) {
                }

                if (reqInstance == null) {
                    try {
                        java.lang.reflect.Constructor<?> rctor = reqClass.getDeclaredConstructor();
                        rctor.setAccessible(true);
                        reqInstance = rctor.newInstance();
                    } catch (NoSuchMethodException ignored) {
                        reqInstance = null;
                    }
                }

                if (reqInstance != null) {
                    try {
                        java.lang.reflect.Method openMicrophone = amClass.getDeclaredMethod("openMicrophone",
                                Class.forName("androidx.car.app.media.OpenMicrophoneRequest"));
                        openMicrophone.setAccessible(true);
                        Object response = openMicrophone.invoke(appManager, reqInstance);
                        Log.i(TAG, "openMicrophone returned: " + String.valueOf(response));
                    } catch (NoSuchMethodException e) {
                        try {
                            java.lang.reflect.Method openMicrophonePublic = amClass.getMethod("openMicrophone",
                                    Class.forName("androidx.car.app.media.OpenMicrophoneRequest"));
                            Object response = openMicrophonePublic.invoke(appManager, reqInstance);
                            Log.i(TAG, "openMicrophone returned: " + String.valueOf(response));
                        } catch (NoSuchMethodException ex) {
                            Log.w(TAG, "openMicrophone(OpenMicrophoneRequest) not found on AppManager", ex);
                        }
                    }
                } else {
                    Log.w(TAG, "Could not construct OpenMicrophoneRequest reflectively; skipping openMicrophone");
                }
            } catch (ClassNotFoundException e) {
                Log.w(TAG, "OpenMicrophoneRequest not present in runtime; skipping openMicrophone");
            } catch (Throwable t) {
                Log.e(TAG, "Error calling openMicrophone", t);
            }

            // getIInterface()
            try {
                java.lang.reflect.Method getIInterface = amClass.getDeclaredMethod("getIInterface");
                getIInterface.setAccessible(true);
                Object iInterface = getIInterface.invoke(appManager);
                Log.i(TAG, "getIInterface() -> " + (iInterface == null ? "null" : iInterface.getClass().getName()));
            } catch (NoSuchMethodException e) {
                Log.w(TAG, "getIInterface not found (may be hidden API)", e);
            } catch (Throwable t) {
                Log.e(TAG, "Error calling getIInterface", t);
            }

            // getLifecycle()
            try {
                java.lang.reflect.Method getLifecycle = amClass.getDeclaredMethod("getLifecycle");
                getLifecycle.setAccessible(true);
                Object lifecycle = getLifecycle.invoke(appManager);
                Log.i(TAG, "getLifecycle() -> " + (lifecycle == null ? "null" : lifecycle.getClass().getName()));
            } catch (NoSuchMethodException e) {
                Log.w(TAG, "getLifecycle not found (may be hidden API)", e);
            } catch (Throwable t) {
                Log.e(TAG, "Error calling getLifecycle", t);
            }

            // startLocationUpdates() & stopLocationUpdates()
            try {
                // startLocationUpdates (throws SecurityException -> gives path)
                java.lang.reflect.Method startLocationUpdates =
                        amClass.getDeclaredMethod("startLocationUpdates");
                startLocationUpdates.setAccessible(true);
                try {
                    startLocationUpdates.invoke(appManager);
                    Log.i(TAG, "Invoked startLocationUpdates()");
                } catch (java.lang.reflect.InvocationTargetException ite) {
                    Throwable cause = ite.getCause();
                    if (cause instanceof SecurityException) {
                        Log.w(TAG, "startLocationUpdates() security exception (permissions missing)", cause);
                    } else {
                        Log.e(TAG, "startLocationUpdates threw", cause);
                    }
                }

                // stopLocationUpdates
                java.lang.reflect.Method stopLocationUpdates =
                        amClass.getDeclaredMethod("stopLocationUpdates");
                stopLocationUpdates.setAccessible(true);
                stopLocationUpdates.invoke(appManager);
                Log.i(TAG, "Invoked stopLocationUpdates()");
            } catch (NoSuchMethodException e) {
                Log.w(TAG, "startLocationUpdates/stopLocationUpdates not found", e);
            } catch (Throwable t) {
                Log.e(TAG, "Error calling start/stopLocationUpdates", t);
            }

            Log.i(TAG, "Finished exercising AppManager");
        } catch (Throwable t) {
            Log.e("ExerciseAppManager", "Unexpected error while exercising AppManager", t);
        }
    }

    @SuppressWarnings({"rawtypes"})
    public void exerciseCarAppBinder() {
        final String TAG = "Reflection.CarAppBinder";
        try {
            Class<?> targetClass = Class.forName("androidx.car.app.CarAppBinder");
            // Try to get the constructor (CarAppService, SessionInfo) if available,
            // otherwise fall back to any declared constructor.
            Constructor ctor = null;
            try {
                Class<?> carAppServiceCls = Class.forName("androidx.car.app.CarAppService");
                Class<?> sessionInfoCls = Class.forName("androidx.car.app.SessionInfo");
                ctor = targetClass.getDeclaredConstructor(carAppServiceCls, sessionInfoCls);
            } catch (Throwable e) {
                // fallback: pick the first declared constructor
                Constructor[] ctors = targetClass.getDeclaredConstructors();
                if (ctors.length > 0) {
                    ctor = ctors[0];
                }
            }

            if (ctor == null) {
                Log.e(TAG, "No constructor found for CarAppBinder");
                return;
            }
            ctor.setAccessible(true);

            // Attempt to construct a CarAppBinder instance.
            // Many apps create it with concrete CarAppService and SessionInfo; supply nulls when we can't produce them.
            Object binderInstance;
            try {
                Class<?>[] paramTypes = ctor.getParameterTypes();
                Object[] ctorArgs = new Object[paramTypes.length];
                for (int i = 0; i < paramTypes.length; i++) {
                    // Prefer nulls (safe start). If a parameter is primitive, fallback to zero-value.
                    Class<?> p = paramTypes[i];
                    if (p.isPrimitive()) {
                        if (p == boolean.class) ctorArgs[i] = false;
                        else if (p == byte.class) ctorArgs[i] = (byte) 0;
                        else if (p == short.class) ctorArgs[i] = (short) 0;
                        else if (p == int.class) ctorArgs[i] = 0;
                        else if (p == long.class) ctorArgs[i] = 0L;
                        else if (p == float.class) ctorArgs[i] = 0f;
                        else if (p == double.class) ctorArgs[i] = 0d;
                        else ctorArgs[i] = 0;
                    } else {
                        ctorArgs[i] = null;
                    }
                }
                binderInstance = ctor.newInstance(ctorArgs);
                Log.i(TAG, "Constructed CarAppBinder instance with nulls/defaults");
            } catch (Throwable e) {
                Log.w(TAG, "Constructor call with nulls failed, trying no-arg/newInstance fallback", e);
                // Try no-arg default instantiation as last resort (very unlikely to succeed)
                Constructor noArg = null;
                for (Constructor c : targetClass.getDeclaredConstructors()) {
                    if (c.getParameterCount() == 0) {
                        noArg = c;
                        break;
                    }
                }
                if (noArg != null) {
                    noArg.setAccessible(true);
                    binderInstance = noArg.newInstance();
                    Log.i(TAG, "Constructed CarAppBinder via no-arg constructor");
                } else {
                    Log.e(TAG, "Unable to instantiate CarAppBinder - aborting exercise");
                    return;
                }
            }

            // Prepare a reusable IOnDoneCallback stub used for callback parameters.
            @SuppressLint("RestrictedApi") final androidx.car.app.IOnDoneCallback onDoneStub = new androidx.car.app.IOnDoneCallback.Stub() {
                @Override
                public void onSuccess(androidx.car.app.serialization.Bundleable response) {
                    Log.i(TAG, "IOnDoneCallback.onSuccess response: " + (response == null ? "null" : response));
                }

                @Override
                public void onFailure(androidx.car.app.serialization.Bundleable failureResponse) {
                    Log.w(TAG, "IOnDoneCallback.onFailure response: " + (failureResponse == null ? "null" : failureResponse));
                }
            };

            // Helper that builds a Bundleable wrapping a HandshakeInfo (used by onHandshakeCompleted)
            java.lang.reflect.Method bundleableCreateMethod = null;
            try {
                Class<?> bundleableClass = Class.forName("androidx.car.app.serialization.Bundleable");
                bundleableCreateMethod = bundleableClass.getMethod("create", Object.class);
            } catch (Throwable ignored) { /* optional; we'll handle when needed */ }

            // Craft a handshake Bundleable when needed.
            Object handshakeBundleable = null;
            try {
                Class<?> handshakeCls = Class.forName("androidx.car.app.HandshakeInfo");
                Constructor<?> hCtor = handshakeCls.getConstructor(String.class, int.class);
                String pkg = (getCarContext() != null ? getCarContext().getPackageName() : "com.example");
                Object handshake = hCtor.newInstance(pkg, /*hostCarAppApiLevel*/ 1);
                if (bundleableCreateMethod != null) {
                    try {
                        handshakeBundleable = bundleableCreateMethod.invoke(null, handshake);
                    } catch (Throwable e) {
                        Log.w(TAG, "Bundleable.create failed for HandshakeInfo", e);
                        handshakeBundleable = null;
                    }
                }
            } catch (Throwable e) {
                Log.w(TAG, "Could not create HandshakeInfo Bundleable - will skip handshake invocation", e);
                handshakeBundleable = null;
            }

            // Pre-created simple objects for common parameter types.
            Intent simpleIntent = new Intent();
            android.content.res.Configuration simpleConfig = new android.content.res.Configuration();
            Intent navIntent = new Intent(android.content.Intent.ACTION_VIEW);
            // navigation URI example (geo:0,0?q=somewhere)
            navIntent.setData(android.net.Uri.parse("geo:0,0?q=1+Infinite+Loop"));
            // small helper for "type name" resolution to choose parameters
            for (Method m : targetClass.getDeclaredMethods()) {
                m.setAccessible(true);
                String mName = m.getName();
                Class<?>[] ptypes = m.getParameterTypes();
                Object[] args = new Object[ptypes.length];
                for (int i = 0; i < ptypes.length; i++) {
                    Class<?> p = ptypes[i];
                    if (p == androidx.car.app.IOnDoneCallback.class) {
                        args[i] = onDoneStub;
                    } else if (p == android.content.Intent.class) {
                        // for onNewIntent use a simple Intent; other calls accept empty Intent too
                        args[i] = simpleIntent;
                    } else if (p == android.content.res.Configuration.class) {
                        args[i] = simpleConfig;
                    } else if (p == java.lang.String.class) {
                        // Many API calls use CarContext service names (app/navigation). Use app service by default.
                        try {
                            args[i] = Class.forName("androidx.car.app.CarContext")
                                    .getField("APP_SERVICE").get(null);
                        } catch (Throwable e) {
                            args[i] = "app";
                        }
                    } else if (p == int.class || p == Integer.class) {
                        args[i] = 0;
                    } else if (p == androidx.car.app.serialization.Bundleable.class) {
                        // supply handshakeBundleable when asked
                        args[i] = handshakeBundleable;
                    } else {
                        // unknown param: try to satisfy common binder types:
                        if (p.isInterface()) {
                            // try dynamic proxy for simple interface requirements returning benign values
                            Object proxy = java.lang.reflect.Proxy.newProxyInstance(
                                    p.getClassLoader(),
                                    new Class[]{p},
                                    (proxyObj, method, methodArgs) -> {
                                        // return reasonable defaults for common return types
                                        Class<?> rt = method.getReturnType();
                                        if (rt == void.class) return null;
                                        if (rt.isPrimitive()) {
                                            if (rt == boolean.class) return false;
                                            if (rt == int.class) return 0;
                                            if (rt == long.class) return 0L;
                                            if (rt == double.class) return 0d;
                                        }
                                        return null;
                                    });
                            args[i] = proxy;
                        } else {
                            // otherwise null (many methods will gracefully throw or return)
                            args[i] = null;
                        }
                    }
                }

                // Try invoking the method and capture result / exception
                try {
                    Object result = m.invoke(binderInstance, args);
                    Log.i(TAG, String.format("Invoked %s(%d args) -> %s", mName, args.length,
                            result == null ? "null" : result.toString()));
                } catch (Throwable invokeError) {
                    // unwrap InvocationTargetException to get underlying cause
                    Throwable cause = (invokeError instanceof java.lang.reflect.InvocationTargetException)
                            ? invokeError.getCause() : invokeError;
                    Log.w(TAG, String.format("Invocation of %s failed: %s", mName,
                            cause == null ? invokeError.toString() : cause.toString()), cause);
                }
            }

            // Finally, exercise a couple of internal/private convenience methods by name if present.
            // onNewIntentInternal(Session, Intent) and onConfigurationChangedInternal(Session, Configuration)
            try {
                // try to call private onNewIntentInternal with (Session, Intent) by passing null session
                try {
                    Method onNewIntentInternal = targetClass.getDeclaredMethod("onNewIntentInternal",
                            Class.forName("androidx.car.app.Session"), Intent.class);
                    onNewIntentInternal.setAccessible(true);
                    onNewIntentInternal.invoke(binderInstance, /*session*/ null, simpleIntent);
                    Log.i(TAG, "Called onNewIntentInternal(null, simpleIntent)");
                } catch (NoSuchMethodException ignore) {
                    // maybe signature differs; skip.
                } catch (Throwable e) {
                    Log.w(TAG, "onNewIntentInternal call failed", e);
                }

                try {
                    Method onConfigInternal = targetClass.getDeclaredMethod("onConfigurationChangedInternal",
                            Class.forName("androidx.car.app.Session"),
                            android.content.res.Configuration.class);
                    onConfigInternal.setAccessible(true);
                    onConfigInternal.invoke(binderInstance, /*session*/ null, simpleConfig);
                    Log.i(TAG, "Called onConfigurationChangedInternal(null, simpleConfig)");
                } catch (NoSuchMethodException ignore) {
                    // skip if not present
                } catch (Throwable e) {
                    Log.w(TAG, "onConfigurationChangedInternal call failed", e);
                }
            } catch (Throwable e) {
                Log.w(TAG, "Attempt to call private internals failed", e);
            }

            Log.i(TAG, "exerciseCarAppBinder completed (see logs for per-method results)");
        } catch (Throwable e) {
            Log.e("Reflection.CarAppBinder", "Failed to exercise CarAppBinder", e);
        }
    }


    @SuppressWarnings({"BanUncheckedReflection"})
    public void exerciseCarAppServiceReflection() {
        final String TAG = "ExerciseCarAppService";
        try {
            // App context and package info used as "real" parameters
            android.content.Context appContext = getCarContext().getBaseContext();
            final String packageName = appContext.getPackageName();
            final int myUid = android.os.Process.myUid();

            Log.i(TAG, "Starting exerciseCarAppServiceReflection for package: " + packageName);

            // Create a minimal anonymous CarAppService subclass overriding only createHostValidator()
            androidx.car.app.CarAppService testService = new androidx.car.app.CarAppService() {
                @NonNull
                @Override
                public androidx.car.app.validation.HostValidator createHostValidator() {
                    // Use the permissive validator for test runs.
                    return androidx.car.app.validation.HostValidator.ALLOW_ALL_HOSTS_VALIDATOR;
                }

                // Do not call onCreateSession / lifecycle methods here; we won't exercise those.
            };

            // Attach the application Context to the Service instance (Service is a ContextWrapper).
            // attachBaseContext is protected in ContextWrapper; call via reflection.
            try {
                java.lang.reflect.Method attachBase = android.content.ContextWrapper.class
                        .getDeclaredMethod("attachBaseContext", android.content.Context.class);
                attachBase.setAccessible(true);
                attachBase.invoke(testService, appContext);
                Log.i(TAG, "Attached base context to testService");
            } catch (Exception e) {
                Log.e(TAG, "Failed to attach base context to testService", e);
            }

            // 1) setHostInfo (package-private) -> set a HostInfo using the current package name and uid
            try {
                Class<?> hostInfoClass = Class.forName("androidx.car.app.HostInfo");
                java.lang.reflect.Constructor<?> hostInfoCtor =
                        hostInfoClass.getConstructor(String.class, int.class);
                Object hostInfo = hostInfoCtor.newInstance(packageName, myUid);

                java.lang.reflect.Method setHostInfo =
                        androidx.car.app.CarAppService.class.getDeclaredMethod("setHostInfo", hostInfoClass);
                setHostInfo.setAccessible(true);
                setHostInfo.invoke(testService, hostInfo);
                Log.i(TAG, "Invoked setHostInfo -> " + hostInfo);
            } catch (Exception e) {
                Log.e(TAG, "Failed to call setHostInfo", e);
            }

            // 2) getHostInfo (public)
            try {
                java.lang.reflect.Method getHostInfo = androidx.car.app.CarAppService.class
                        .getMethod("getHostInfo");
                Object hostInfoResult = getHostInfo.invoke(testService);
                Log.i(TAG, "getHostInfo returned: " + hostInfoResult);
            } catch (Exception e) {
                Log.e(TAG, "Failed to call getHostInfo", e);
            }

            // 3) onBind(Intent) - craft a real Intent with SERVICE_INTERFACE, use our package
            android.content.Intent bindIntent = new android.content.Intent(
                    androidx.car.app.CarAppService.SERVICE_INTERFACE);
            bindIntent.setPackage(packageName); // real package
            try {
                java.lang.reflect.Method onBindMethod =
                        androidx.car.app.CarAppService.class.getMethod("onBind", android.content.Intent.class);
                Object binder = onBindMethod.invoke(testService, bindIntent);
                Log.i(TAG, "onBind returned IBinder: " + binder);
            } catch (Exception e) {
                Log.e(TAG, "Failed to call onBind(Intent)", e);
            }

            // 4) getCurrentSession() (deprecated helper) - may be null
            try {
                java.lang.reflect.Method getCurrentSessionMethod =
                        androidx.car.app.CarAppService.class.getMethod("getCurrentSession");
                Object currentSession = getCurrentSessionMethod.invoke(testService);
                Log.i(TAG, "getCurrentSession returned: " + currentSession);
            } catch (Exception e) {
                Log.e(TAG, "Failed to call getCurrentSession()", e);
            }

            // 5) getSession(SessionInfo) - craft a SessionInfo instance via SessionInfo.DEFAULT_SESSION_INFO
            try {
                Class<?> sessionInfoClass = Class.forName("androidx.car.app.SessionInfo");
                java.lang.reflect.Field defaultField = sessionInfoClass.getField("DEFAULT_SESSION_INFO");
                Object defaultSessionInfo = defaultField.get(null);
                java.lang.reflect.Method getSessionMethod =
                        androidx.car.app.CarAppService.class.getMethod("getSession", sessionInfoClass);
                Object session = getSessionMethod.invoke(testService, defaultSessionInfo);
                Log.i(TAG, "getSession(DEFAULT_SESSION_INFO) -> " + session);
            } catch (NoSuchFieldException nsf) {
                // Older/modified versions might not expose DEFAULT_SESSION_INFO; that's ok.
                Log.w(TAG, "SessionInfo.DEFAULT_SESSION_INFO not available; skipping getSession", nsf);
            } catch (Exception e) {
                Log.e(TAG, "Failed to call getSession(SessionInfo)", e);
            }

            // 6) getAppInfo() - should return AppInfo built from the attached context's package manager
            try {
                java.lang.reflect.Method getAppInfoMethod =
                        androidx.car.app.CarAppService.class.getDeclaredMethod("getAppInfo");
                Object appInfo = getAppInfoMethod.invoke(testService);
                Log.i(TAG, "getAppInfo returned: " + appInfo);
            } catch (Exception e) {
                Log.e(TAG, "Failed to call getAppInfo()", e);
            }

            // 7) dump(FileDescriptor, PrintWriter, String[]) - create a reliable pipe and writer
            java.io.PrintWriter dumpWriter = null;
            android.os.ParcelFileDescriptor[] pipe = null;
            try {
                pipe = android.os.ParcelFileDescriptor.createReliablePipe();
                // pipe[0] = read side, pipe[1] = write side
                java.io.OutputStream os = new android.os.ParcelFileDescriptor.AutoCloseOutputStream(pipe[1]);
                dumpWriter = new java.io.PrintWriter(new java.io.OutputStreamWriter(os));
                String[] dumpArgs = new String[] { "AUTO_DRIVE" }; // the service reacts to AUTO_DRIVE
                java.lang.reflect.Method dumpMethod = androidx.car.app.CarAppService.class.getMethod(
                        "dump", java.io.FileDescriptor.class, java.io.PrintWriter.class, String[].class);
                // pass the read-side FileDescriptor
                dumpMethod.invoke(testService, pipe[0].getFileDescriptor(), dumpWriter, dumpArgs);
                // flush writer so any text written is pushed into the pipe
                dumpWriter.flush();

                // read any bytes written to the pipe (non-blocking read loop)
                java.io.InputStream is = new android.os.ParcelFileDescriptor.AutoCloseInputStream(pipe[0]);
                StringBuilder sb = new StringBuilder();
                byte[] buffer = new byte[1024];
                // try to read available data with a small timeout loop
                long start = System.currentTimeMillis();
                while (System.currentTimeMillis() - start < 200) { // read for up to 200ms
                    int avail = is.available();
                    if (avail <= 0) {
                        Thread.sleep(20);
                        continue;
                    }
                    int read = is.read(buffer, 0, Math.min(buffer.length, avail));
                    if (read > 0) {
                        sb.append(new String(buffer, 0, read));
                    } else {
                        break;
                    }
                }
                Log.i(TAG, "dump() output (first chunk): " + sb.toString().trim());
                // close streams (AutoClose will close underlying fds)
                is.close();
                os.close();
            } catch (Exception e) {
                Log.e(TAG, "Failed to run dump(...)", e);
            } finally {
                if (dumpWriter != null) {
                    try {
                        dumpWriter.close();
                    } catch (Exception ignore) { }
                }
                if (pipe != null) {
                    // ensure both descriptors closed
                    try { pipe[0].close(); } catch (Exception ignore) {}
                    try { pipe[1].close(); } catch (Exception ignore) {}
                }
            }

            // 8) onUnbind(Intent) - call with same intent
            try {
                java.lang.reflect.Method onUnbindMethod =
                        androidx.car.app.CarAppService.class.getMethod("onUnbind", android.content.Intent.class);
                Object onUnbindResult = onUnbindMethod.invoke(testService, bindIntent);
                Log.i(TAG, "onUnbind returned: " + onUnbindResult);
            } catch (Exception e) {
                Log.e(TAG, "Failed to call onUnbind(Intent)", e);
            }

            // 9) onDestroy - call the lifecycle destroy (safe to call)
            try {
                java.lang.reflect.Method onDestroy = androidx.car.app.CarAppService.class.getMethod("onDestroy");
                onDestroy.invoke(testService);
                Log.i(TAG, "onDestroy invoked on testService");
            } catch (Exception e) {
                Log.e(TAG, "Failed to call onDestroy()", e);
            }

            Log.i(TAG, "Finished exerciseCarAppServiceReflection (no crash).");
        } catch (Exception outer) {
            Log.e("ExerciseCarAppService", "Unexpected failure in exerciseCarAppServiceReflection", outer);
        }
    }

    public void exerciseCarContextReflection() {
        final String TAG = "CarContextReflect";
        android.util.Log.d(TAG, "Starting CarContext reflection exercise...");

        try {
            // Find CarContext class and current instance
            Class<?> carCtxCls = Class.forName("androidx.car.app.CarContext");
            Object carCtxInstance = getCarContext();
            if (carCtxInstance == null) {
                android.util.Log.e(TAG, "getCarContext() returned null. Aborting.");
                return;
            }

            // Attempt to read the private mLifecycle field (useful for static create(Lifecycle))
            Object lifecycleObj = null;
            try {
                java.lang.reflect.Field lifecycleField = carCtxCls.getDeclaredField("mLifecycle");
                lifecycleField.setAccessible(true);
                lifecycleObj = lifecycleField.get(carCtxInstance);
                android.util.Log.d(TAG, "Obtained lifecycle via reflection: " + String.valueOf(lifecycleObj));
            } catch (Throwable t) {
                android.util.Log.w(TAG, "Could not read mLifecycle: " + t.getMessage());
            }

            // Helper: get common constant values (APP_SERVICE etc.)
            java.util.Map<String, Object> consts = new java.util.HashMap<>();
            String[] constNames = new String[] {
                    "APP_SERVICE", "NAVIGATION_SERVICE", "SCREEN_SERVICE", "CAR_SERVICE",
                    "CONSTRAINT_SERVICE", "HARDWARE_SERVICE", "SUGGESTION_SERVICE",
                    "MEDIA_PLAYBACK_SERVICE", "ACTION_NAVIGATE", "EXTRA_START_CAR_APP_BINDER_KEY"
            };
            for (String c : constNames) {
                try {
                    java.lang.reflect.Field f = carCtxCls.getField(c);
                    f.setAccessible(true);
                    consts.put(c, f.get(null));
                } catch (Throwable ignored) { /* not all exist on all versions */ }
            }

            // Reflection: iterate declared methods and attempt invocation with crafted args
            java.lang.reflect.Method[] methods = carCtxCls.getDeclaredMethods();
            for (java.lang.reflect.Method method : methods) {
                // skip synthetic/bridge helper methods
                method.setAccessible(true);
                Class<?>[] paramTypes = method.getParameterTypes();
                Object[] args = new Object[paramTypes.length];

                // Craft args
                for (int i = 0; i < paramTypes.length; i++) {
                    Class<?> p = paramTypes[i];
                    try {
                        // Strings -> try service constants or ACTION_NAVIGATE
                        if (p.equals(String.class)) {
                            if (consts.containsKey("APP_SERVICE")) {
                                args[i] = consts.get("APP_SERVICE");
                            } else if (consts.containsKey("NAVIGATION_SERVICE")) {
                                args[i] = consts.get("NAVIGATION_SERVICE");
                            } else {
                                args[i] = "app";
                            }
                            continue;
                        }

                        // Class parameter (e.g., getCarService(Class<T>))
                        if (p.equals(Class.class)) {
                            // default to AppManager.class if available
                            try {
                                args[i] = Class.forName("androidx.car.app.AppManager");
                            } catch (Throwable t) {
                                args[i] = Class.class; // fallback (unlikely to be useful)
                            }
                            continue;
                        }

                        // Intent
                        if (android.content.Intent.class.isAssignableFrom(p)) {
                            // create a navigation Intent (geo query)
                            android.content.Intent nav = new android.content.Intent();
                            if (consts.containsKey("ACTION_NAVIGATE")) {
                                nav.setAction((String) consts.get("ACTION_NAVIGATE"));
                            } else {
                                nav.setAction("androidx.car.app.action.NAVIGATE");
                            }
                            android.net.Uri uri = android.net.Uri.parse("geo:0,0?q=1600+Amphitheatre+Parkway");
                            nav.setData(uri);
                            // for two-Intent static startCarApp(notificationIntent, appIntent) we will
                            // put binder extra in the first Intent below when invoked
                            args[i] = nav;
                            continue;
                        }

                        // Context parameter -> application context
                        if (android.content.Context.class.isAssignableFrom(p)) {
                            args[i] = getCarContext().getApplicationContext();
                            continue;
                        }


                        // Executor
                        if (p.equals(java.util.concurrent.Executor.class)) {
                            args[i] = androidx.core.content.ContextCompat.getMainExecutor((android.content.Context) carCtxInstance);
                            continue;
                        }

                        // List / Collection -> permissions for requestPermissions call
                        if (java.util.List.class.isAssignableFrom(p) || java.util.Collection.class.isAssignableFrom(p)) {
                            args[i] = java.util.Arrays.asList(android.Manifest.permission.ACCESS_FINE_LOCATION);
                            continue;
                        }

                        // Primitive types
                        if (p.equals(int.class) || p.equals(Integer.class)) {
                            args[i] = 0;
                            continue;
                        }
                        if (p.equals(boolean.class) || p.equals(Boolean.class)) {
                            args[i] = false;
                            continue;
                        }
                        if (p.equals(long.class) || p.equals(Long.class)) {
                            args[i] = 0L;
                            continue;
                        }
                        if (p.equals(float.class) || p.equals(Float.class) || p.equals(double.class) || p.equals(Double.class)) {
                            args[i] = 0;
                            continue;
                        }

                        // Lifecycle - use lifecycleObj if available
                        if (p.getName().equals("androidx.lifecycle.Lifecycle")) {
                            args[i] = lifecycleObj;
                            continue;
                        }

                        // ComponentName
                        if (p.equals(android.content.ComponentName.class)) {
                            args[i] = new android.content.ComponentName((android.content.Context) carCtxInstance,
                                    Class.forName(getClass().getName())); // point to current class
                            continue;
                        }

                        // For interface types, create a Proxy that logs calls and returns null/0/false
                        if (p.isInterface()) {
                            Object proxy = java.lang.reflect.Proxy.newProxyInstance(
                                    p.getClassLoader(),
                                    new Class<?>[] { p },
                                    (proxyObj, invokedMethod, invokedArgs) -> {
                                        android.util.Log.d(TAG, "Proxy for " + p.getName()
                                                + " called method " + invokedMethod.getName()
                                                + " args=" + java.util.Arrays.toString(invokedArgs));
                                        // return default for primitives, null for objects
                                        Class<?> ret = invokedMethod.getReturnType();
                                        if (ret.equals(boolean.class)) return false;
                                        if (ret.equals(int.class)) return 0;
                                        if (ret.equals(long.class)) return 0L;
                                        if (ret.equals(float.class)) return 0f;
                                        if (ret.equals(double.class)) return 0d;
                                        return null;
                                    });
                            args[i] = proxy;
                            continue;
                        }

                        // Default attempt: try no-arg constructor
                        try {
                            java.lang.reflect.Constructor<?> ctor = p.getDeclaredConstructor();
                            ctor.setAccessible(true);
                            args[i] = ctor.newInstance();
                            continue;
                        } catch (NoSuchMethodException ignored) {
                            // can't instantiate, fallback to null
                        }

                        args[i] = null;
                    } catch (Throwable craftErr) {
                        android.util.Log.w(TAG, "Could not craft arg for parameter " + p + ": " + craftErr);
                        args[i] = null;
                    }
                } // end param loop

                // Special-case: the static deprecated startCarApp(notificationIntent, appIntent)
                // expects the notificationIntent to contain the EXTRA_START_CAR_APP_BINDER_KEY binder
                if (method.getName().equals("startCarApp") && method.getParameterTypes().length == 2
                        && method.getParameterTypes()[0].equals(android.content.Intent.class)
                        && method.getParameterTypes()[1].equals(android.content.Intent.class)) {
                    try {
                        // build notificationIntent with binder extra
                        android.content.Intent notif = new android.content.Intent();
                        android.os.IBinder binder = new IStartCarApp.Stub() {
                            @SuppressLint("RestrictedApi")
                            @Override
                            public void startCarApp(android.content.Intent appIntent) {
                                android.util.Log.d(TAG, "IStartCarApp.Stub.startCarApp invoked with: " + appIntent);
                            }
                        }.asBinder();
                        java.lang.reflect.Field extraKeyField = null;
                        try {
                            extraKeyField = carCtxCls.getField("EXTRA_START_CAR_APP_BINDER_KEY");
                        } catch (Throwable ignored) {}
                        String extraKey = extraKeyField != null ? (String) extraKeyField.get(null)
                                : "androidx.car.app.extra.START_CAR_APP_BINDER_KEY";
                        android.os.Bundle b = new android.os.Bundle();
                        b.putBinder(extraKey, binder);
                        notif.putExtras(b);
                        // build a real appIntent (component pointing to this package)
                        android.content.Intent appIntent = new android.content.Intent();
                        appIntent.setComponent(new android.content.ComponentName((android.content.Context) carCtxInstance,
                                Class.forName(getClass().getName())));
                        args = new Object[] { notif, appIntent };
                    } catch (Throwable t) {
                        android.util.Log.w(TAG, "Could not craft binder-backed notification Intent: " + t);
                    }
                }

                // Invoke: static vs instance
                Object target = java.lang.reflect.Modifier.isStatic(method.getModifiers()) ? null : carCtxInstance;
                try {
                    Object result = method.invoke(target, args);
                    android.util.Log.d(TAG, "Invoked " + method.getName() + " successfully. Return: "
                            + (result == null ? "null" : result.toString()));
                } catch (java.lang.reflect.InvocationTargetException ite) {
                    Throwable cause = ite.getCause();
                    android.util.Log.w(TAG, "InvocationTargetException invoking " + method.getName()
                            + ": " + (cause == null ? ite.toString() : cause.toString()), ite);
                } catch (Throwable invokeErr) {
                    android.util.Log.w(TAG, "Error invoking " + method.getName() + ": " + invokeErr, invokeErr);
                }
            } // end methods loop

            android.util.Log.d(TAG, "CarContext reflection exercise completed.");
        } catch (Throwable e) {
            android.util.Log.e("CarContextReflect", "Fatal error during reflection exercise", e);
        }
    }


    public void exerciseCarAppPermissionReflection() {
        final String TAG = "ExerciseCarAppPermission";
        android.content.Context context = null;

        // 1) Try to obtain a Context via ActivityThread.currentApplication()
        try {
            Class<?> activityThread = Class.forName("android.app.ActivityThread");
            java.lang.reflect.Method currentApp = activityThread.getMethod("currentApplication");
            Object app = currentApp.invoke(null);
            if (app instanceof android.content.Context) {
                context = (android.content.Context) app;
                android.util.Log.d(TAG, "Obtained Context from ActivityThread.currentApplication()");
            } else {
                android.util.Log.w(TAG, "ActivityThread.currentApplication() returned null or not a Context");
            }
        } catch (Throwable t) {
            android.util.Log.w(TAG, "Could not get Context via ActivityThread.currentApplication(): " + t);
        }

        // 2) If we still don't have a context, attempt some additional fallbacks (best-effort).
        if (context == null) {
            try {
                // Try to use android.app.ApplicationContext by reflective lookup of a running process.
                // This is best-effort; in many cases the ActivityThread approach already worked.
                Class<?> appClass = Class.forName("android.app.Application");
                // no reliable static getter here - so we just log and move on.
                android.util.Log.w(TAG,
                        "No Context available via reflection. If this runs inside an Activity/Service, "
                                + "consider calling the method from a place that has a Context.");
            } catch (Throwable ignored) {
                // ignore
            }
        }

        // If still null, we will still proceed and calls will fail with NPE; we catch and log those.
        // 3) Reflectively get CarAppPermission and its public methods & fields.
        try {
            Class<?> capClass = Class.forName("androidx.car.app.CarAppPermission");

            // Read the static permission fields (ACCESS_SURFACE, NAVIGATION_TEMPLATES, MAP_TEMPLATES)
            java.util.Map<String, String> constants = new java.util.LinkedHashMap<>();
            try {
                java.lang.reflect.Field f = capClass.getField("ACCESS_SURFACE");
                constants.put("ACCESS_SURFACE", (String) f.get(null));
            } catch (NoSuchFieldException ignore) { android.util.Log.w(TAG, "ACCESS_SURFACE not found"); }
            try {
                java.lang.reflect.Field f = capClass.getField("NAVIGATION_TEMPLATES");
                constants.put("NAVIGATION_TEMPLATES", (String) f.get(null));
            } catch (NoSuchFieldException ignore) { android.util.Log.w(TAG, "NAVIGATION_TEMPLATES not found"); }
            try {
                java.lang.reflect.Field f = capClass.getField("MAP_TEMPLATES");
                constants.put("MAP_TEMPLATES", (String) f.get(null));
            } catch (NoSuchFieldException ignore) { android.util.Log.w(TAG, "MAP_TEMPLATES not found"); }

            // Add an obviously invalid permission to test rejection path:
            constants.put("INVALID_PERMISSION", "com.example.this_permission_does_not_exist");

            // Locate methods
            java.lang.reflect.Method checkHasPermission =
                    capClass.getMethod("checkHasPermission", android.content.Context.class, String.class);

            java.lang.reflect.Method checkHasLibraryPermission =
                    capClass.getMethod("checkHasLibraryPermission", android.content.Context.class, String.class);

            // Iterate and invoke checkHasPermission
            for (java.util.Map.Entry<String, String> e : constants.entrySet()) {
                String name = e.getKey();
                String perm = e.getValue();
                String tag = "checkHasPermission - " + name;
                try {
                    android.util.Log.d(TAG, "Invoking " + tag + " with permission: " + perm);
                    // invoke static method: null target
                    checkHasPermission.invoke(null, context, perm);
                    String msg = tag + " -> SUCCESS (permission present)";
                    android.util.Log.i(TAG, msg);
                    System.out.println(msg);
                } catch (java.lang.reflect.InvocationTargetException ite) {
                    Throwable cause = ite.getCause();
                    String err = tag + " -> InvocationTargetException cause: " + cause;
                    android.util.Log.w(TAG, err, cause);
                    System.out.println(err + " :: " + cause);
                } catch (Throwable t) {
                    String err = tag + " -> Reflection error: " + t;
                    android.util.Log.e(TAG, err, t);
                    System.out.println(err);
                }
            }

            // Iterate and invoke checkHasLibraryPermission for library perms only (use the real constants)
            for (String fieldName : new String[]{"ACCESS_SURFACE", "NAVIGATION_TEMPLATES", "MAP_TEMPLATES"}) {
                try {
                    java.lang.reflect.Field ff = capClass.getField(fieldName);
                    String perm = (String) ff.get(null);
                    String tag = "checkHasLibraryPermission - " + fieldName;
                    try {
                        android.util.Log.d(TAG, "Invoking " + tag + " with permission: " + perm);
                        checkHasLibraryPermission.invoke(null, context, perm);
                        String msg = tag + " -> SUCCESS (declared or granted)";
                        android.util.Log.i(TAG, msg);
                        System.out.println(msg);
                    } catch (java.lang.reflect.InvocationTargetException ite) {
                        Throwable cause = ite.getCause();
                        String err = tag + " -> InvocationTargetException cause: " + cause;
                        android.util.Log.w(TAG, err, cause);
                        System.out.println(err + " :: " + cause);
                    } catch (Throwable t) {
                        String err = tag + " -> Reflection error: " + t;
                        android.util.Log.e(TAG, err, t);
                        System.out.println(err);
                    }
                } catch (NoSuchFieldException nsf) {
                    android.util.Log.w(TAG, "Field " + fieldName + " not present on CarAppPermission");
                }
            }

            // 4) Try to access and call the private constructor (utility classes often have private ctor).
            try {
                java.lang.reflect.Constructor<?> ctor = capClass.getDeclaredConstructor();
                ctor.setAccessible(true);
                Object instance = ctor.newInstance();
                String msg = "Successfully invoked private constructor of CarAppPermission, instance: " + instance;
                android.util.Log.i(TAG, msg);
                System.out.println(msg);
            } catch (java.lang.reflect.InvocationTargetException ite) {
                Throwable cause = ite.getCause();
                String err = "Invoking CarAppPermission private constructor failed: " + cause;
                android.util.Log.w(TAG, err, cause);
                System.out.println(err);
            } catch (NoSuchMethodException nsme) {
                android.util.Log.w(TAG, "No private constructor found on CarAppPermission");
                System.out.println("No private constructor found on CarAppPermission");
            } catch (Throwable t) {
                String err = "Error instantiating private constructor of CarAppPermission: " + t;
                android.util.Log.e(TAG, err, t);
                System.out.println(err);
            }

            android.util.Log.d(TAG, "Completed CarAppPermission reflection exercise.");
        } catch (ClassNotFoundException cnfe) {
            String err = "Class androidx.car.app.CarAppPermission not found: " + cnfe;
            android.util.Log.e(TAG, err, cnfe);
            System.out.println(err);
        } catch (Throwable t) {
            String err = "Unexpected error while exercising CarAppPermission reflectively: " + t;
            android.util.Log.e(TAG, err, t);
            System.out.println(err);
        }
    }


    public void exerciseCarAppPermissionActivityReflection() {
        final String LOG_TAG = "CarAppPermActivityRef";
        try {
            Class<?> activityCls = Class.forName("androidx.car.app.CarAppPermissionActivity");

            // 1) instantiate (no-arg ctor)
            java.lang.reflect.Constructor<?> ctor = activityCls.getDeclaredConstructor();
            ctor.setAccessible(true);
            Object activityInstance = ctor.newInstance();
            Log.d(LOG_TAG, "Instantiated CarAppPermissionActivity via reflection.");

            // 2) attach base context -> use current CarContext if available
            try {
                java.lang.reflect.Method attachBase =
                        android.content.ContextWrapper.class.getDeclaredMethod("attachBaseContext",
                                android.content.Context.class);
                attachBase.setAccessible(true);

                // Attempt to use getCarContext() if present on the host class; otherwise fall back
                // to the application context.
                android.content.Context ctx = null;
                try {
                    // This code assumes this method is inside a Screen/Service/Activity that has
                    // getCarContext(). Try it first (common in this repo's harness).
                    java.lang.reflect.Method getCarContextMethod =
                            this.getClass().getMethod("getCarContext");
                    Object got = getCarContextMethod.invoke(this);
                    if (got instanceof android.content.Context) {
                        ctx = (android.content.Context) got;
                    }
                } catch (NoSuchMethodException ignored) {
                    // Not present; try to use getContext() or fallback to app context
                    try {
                        java.lang.reflect.Method getContextMethod =
                                this.getClass().getMethod("getContext");
                        Object got2 = getContextMethod.invoke(this);
                        if (got2 instanceof android.content.Context) {
                            ctx = (android.content.Context) got2;
                        }
                    } catch (NoSuchMethodException ignored2) {
                        // fallback
                        ctx = androidx.core.content.ContextCompat.getMainExecutor(null) == null
                                ? null : null; // noop; keep below fallback
                    } catch (Throwable ignored3) {
                    }
                } catch (Throwable ignored) {
                }

                if (ctx == null) {
                    // final fallback: application context from any available context (this)
                    try {
                        java.lang.reflect.Method getApplicationContext =
                                this.getClass().getMethod("getContext");
                        Object got = getApplicationContext.invoke(this);
                        if (got instanceof android.content.Context) {
                            ctx = ((android.content.Context) got).getApplicationContext();
                        }
                    } catch (Throwable ignored) {
                    }
                }

                if (ctx == null) {
                    // If still null, use the current thread's default context via reflection to ActivityThread
                    try {
                        @SuppressLint("PrivateApi") Class<?> at = Class.forName("android.app.ActivityThread");
                        @SuppressLint("DiscouragedPrivateApi") java.lang.reflect.Method currentApplication =
                                at.getDeclaredMethod("currentApplication");
                        Object app = currentApplication.invoke(null);
                        if (app instanceof android.app.Application) {
                            ctx = ((android.app.Application) app).getApplicationContext();
                        }
                    } catch (Throwable ignored) {
                    }
                }

                if (ctx == null) {
                    Log.w(LOG_TAG, "No usable Context found to attach; some methods will fail.");
                } else {
                    attachBase.invoke(activityInstance, ctx);
                    Log.d(LOG_TAG, "Attached base context to CarAppPermissionActivity instance.");
                }
            } catch (Throwable t) {
                Log.w(LOG_TAG, "Failed to attach base context reflectively: " + t);
            }

            // 3) craft an Intent that matches CarContext.REQUEST_PERMISSIONS_ACTION and extras
            android.content.Intent intent = new android.content.Intent(
                    "androidx.car.app.action.REQUEST_PERMISSIONS"); // CarContext.REQUEST_PERMISSIONS_ACTION
            android.os.Bundle extras = new android.os.Bundle();

            // Create a stub listener binder for IOnRequestPermissionsListener
            try {
                final androidx.car.app.IOnRequestPermissionsListener.Stub listener =
                        new androidx.car.app.IOnRequestPermissionsListener.Stub() {
                            @SuppressLint("RestrictedApi")
                            @Override
                            public void onRequestPermissionsResult(String[] approvedPermissions,
                                                                   String[] rejectedPermissions) {
                                Log.d(LOG_TAG, "Stub listener invoked. approved="
                                        + java.util.Arrays.toString(approvedPermissions)
                                        + " rejected=" + java.util.Arrays.toString(rejectedPermissions));
                            }
                        };

                extras.putBinder("androidx.car.app.action.EXTRA_ON_REQUEST_PERMISSIONS_RESULT_LISTENER_KEY",
                        listener.asBinder());
            } catch (Throwable t) {
                Log.w(LOG_TAG, "Failed to create IOnRequestPermissionsListener stub: " + t);
            }

            // Put a sample permission list
            extras.putStringArray("androidx.car.app.action.EXTRA_PERMISSIONS_KEY",
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION});

            intent.putExtras(extras);

            // 4) setIntent(activityInstance, intent)
            try {
                java.lang.reflect.Method setIntent =
                        android.app.Activity.class.getMethod("setIntent", android.content.Intent.class);
                setIntent.invoke(activityInstance, intent);
                Log.d(LOG_TAG, "Set Intent on activity instance.");
            } catch (Throwable t) {
                Log.w(LOG_TAG, "Failed to set Intent on activity instance: " + t);
            }

            // 5) invoke lifecycle onCreate(null)
            try {
                java.lang.reflect.Method onCreate = android.app.Activity.class
                        .getDeclaredMethod("onCreate", android.os.Bundle.class);
                onCreate.setAccessible(true);
                onCreate.invoke(activityInstance, (Object) null);
                Log.d(LOG_TAG, "Called onCreate(null) on activity instance.");
            } catch (Throwable t) {
                Log.w(LOG_TAG, "Failed to invoke onCreate: " + t);
            }

            // 6) attempt to call private maybeSetCustomBackground()
            try {
                java.lang.reflect.Method maybeSet =
                        activityCls.getDeclaredMethod("maybeSetCustomBackground");
                maybeSet.setAccessible(true);
                maybeSet.invoke(activityInstance);
                Log.d(LOG_TAG, "Invoked maybeSetCustomBackground()");
            } catch (Throwable t) {
                Log.w(LOG_TAG, "Failed to invoke maybeSetCustomBackground(): " + t);
            }

            // 7) attempt to call private processInternal(Intent)
            try {
                java.lang.reflect.Method processInternal =
                        activityCls.getDeclaredMethod("processInternal", android.content.Intent.class);
                processInternal.setAccessible(true);
                processInternal.invoke(activityInstance, intent);
                Log.d(LOG_TAG, "Invoked processInternal(intent)");
            } catch (Throwable t) {
                Log.w(LOG_TAG, "Failed to invoke processInternal(Intent): " + t);
            }

            // 8) attempt to call private requestPermissions(Intent)
            try {
                java.lang.reflect.Method requestPerm =
                        activityCls.getDeclaredMethod("requestPermissions", android.content.Intent.class);
                requestPerm.setAccessible(true);
                requestPerm.invoke(activityInstance, intent);
                Log.d(LOG_TAG, "Invoked requestPermissions(intent)");
            } catch (Throwable t) {
                Log.w(LOG_TAG, "Failed to invoke requestPermissions(Intent): " + t);
            }

            // 9) try to call finish()
            try {
                java.lang.reflect.Method finish = android.app.Activity.class.getMethod("finish");
                finish.invoke(activityInstance);
                Log.d(LOG_TAG, "Called finish() on activity instance.");
            } catch (Throwable t) {
                Log.w(LOG_TAG, "Failed to call finish(): " + t);
            }

            Log.d(LOG_TAG, "Reflection exercise completed (attempted all methods).");
        } catch (Throwable e) {
            Log.e("CarAppPermActivityRef", "Unexpected failure exercising CarAppPermissionActivity", e);
        }
    }

    @SuppressWarnings({"rawtypes"})
    public void exerciseTemplateSurfaceViewReflection() {
        android.util.Log.i("ReflectionTest", "=== exerciseTemplateSurfaceViewReflection START ===");
        final java.util.ArrayList<String> log = new java.util.ArrayList<>();

        // Utility to add to log safely
        java.util.function.Consumer<String> L = s -> {
            android.util.Log.i("ReflectionTest", s);
            log.add(s);
        };

        android.content.Context ctx = null;
        try {
            try {
                java.lang.reflect.Method m = this.getClass().getMethod("getCarContext");
                Object carCtx = m.invoke(this);
                if (carCtx instanceof android.content.Context) {
                    ctx = (android.content.Context) carCtx;
                    L.accept("Using getCarContext()");
                }
            } catch (NoSuchMethodException ignored) { /* not a Screen */ }
            if (ctx == null) {
                try {
                    java.lang.reflect.Method m2 = this.getClass().getMethod("getContext");
                    Object c = m2.invoke(this);
                    if (c instanceof android.content.Context) {
                        ctx = (android.content.Context) c;
                        L.accept("Using getContext()");
                    }
                } catch (NoSuchMethodException ignored) { }
            }
        } catch (Exception e) {
            L.accept("Failed to obtain context reflectively: " + e);
        }
        if (ctx == null) {
            ctx = androidx.core.content.ContextCompat.getMainExecutor(null) == null ? null : null;
        }
        if (ctx == null) {
            try {
                android.content.Context appCtx =
                        (android.content.Context) Class.forName("android.app.AppGlobals")
                                .getMethod("getInitialApplication")
                                .invoke(null);
                ctx = appCtx;
                L.accept("Falling back to AppGlobals.getInitialApplication()");
            } catch (Exception e) {
                L.accept("Could not resolve any context: " + e);
            }
        }

        // Reflectively load TemplateSurfaceView
        Class<?> tvClass;
        try {
            tvClass = Class.forName("androidx.car.app.activity.renderer.surface.TemplateSurfaceView");
        } catch (Throwable t) {
            L.accept("TemplateSurfaceView class not found: " + t);
            return;
        }

        Object tvInstance = null;
        try {
            java.lang.reflect.Constructor<?> ctor = tvClass.getConstructor(android.content.Context.class, android.util.AttributeSet.class);
            // pass null for AttributeSet
            tvInstance = ctor.newInstance(ctx, null);
            L.accept("Constructed TemplateSurfaceView via (Context, AttributeSet) ctor");
        } catch (Throwable t1) {
            try {
                java.lang.reflect.Constructor<?> ctor2 = tvClass.getConstructor(android.content.Context.class);
                tvInstance = ctor2.newInstance(ctx);
                L.accept("Constructed TemplateSurfaceView via (Context) ctor");
            } catch (Throwable t2) {
                L.accept("Failed to construct TemplateSurfaceView: " + t2);
            }
        }

        if (tvInstance == null) {
            L.accept("No TemplateSurfaceView instance; aborting further calls.");
            return;
        }

        // Helper to call methods safely
        Object finalTvInstance = tvInstance;
        java.util.function.BiConsumer<java.lang.reflect.Method, Object[]> callSafe =
                (mth, args) -> {
                    try {
                        mth.setAccessible(true);
                        Object res = mth.invoke(finalTvInstance, args);
                        L.accept("Invoked " + mth + " -> " + (res == null ? "null" : res.toString()));
                    } catch (Throwable e) {
                        L.accept("Invocation failed " + mth + " : " + e);
                    }
                };

        try {
            Class<?> vmClass = Class.forName("androidx.car.app.activity.CarAppViewModel");
            Object vmProxy = null;
            try {
                // if it's an interface, create a Proxy
                if (vmClass.isInterface()) {
                    vmProxy = java.lang.reflect.Proxy.newProxyInstance(
                            vmClass.getClassLoader(),
                            new Class<?>[]{vmClass},
                            (proxy, method, args) -> {
                                L.accept("CarAppViewModel proxy called: " + method.getName());
                                return null;
                            });
                } else {
                    try {
                        java.lang.reflect.Constructor<?> vmCtor = vmClass.getDeclaredConstructor();
                        vmCtor.setAccessible(true);
                        vmProxy = vmCtor.newInstance();
                        L.accept("Instantiated CarAppViewModel by no-arg ctor");
                    } catch (Throwable t) {
                        vmProxy = null;
                    }
                }
            } catch (Throwable e) {
                L.accept("CarAppViewModel proxy/instantiation failed: " + e);
                vmProxy = null;
            }

            if (vmProxy != null) {
                try {
                    java.lang.reflect.Method setVm = tvClass.getMethod("setViewModel", vmClass);
                    setVm.invoke(tvInstance, vmProxy);
                    L.accept("Called setViewModel(...)");
                } catch (Throwable e) {
                    L.accept("setViewModel invocation failed: " + e);
                }
            } else {
                L.accept("No CarAppViewModel available; skipping setViewModel");
            }
        } catch (Throwable e) {
            L.accept("CarAppViewModel not found or failed: " + e);
        }

        Object serviceDispatcher = null;
        try {
            Class<?> sdClass = Class.forName("androidx.car.app.activity.ServiceDispatcher");
            if (sdClass.isInterface()) {
                serviceDispatcher = java.lang.reflect.Proxy.newProxyInstance(
                        sdClass.getClassLoader(),
                        new Class<?>[]{sdClass},
                        (proxy, method, args) -> {
                            String name = method.getName();
                            L.accept("ServiceDispatcher proxy invoked: " + name);
                            try {
                                if (args != null && args.length > 0) {
                                    for (Object a : args) {
                                        if (a instanceof Runnable) {
                                            ((Runnable) a).run();
                                        } else if (a instanceof java.util.concurrent.Callable) {
                                            try {
                                                return ((java.util.concurrent.Callable) a).call();
                                            } catch (Exception ce) {
                                                return null;
                                            }
                                        }
                                    }
                                }
                            } catch (Throwable ignored) {}
                            if (name != null && name.toLowerCase().contains("fetch")) {
                                return new android.view.inputmethod.EditorInfo();
                            }
                            return null;
                        });
                try {
                    java.lang.reflect.Method setSD = tvClass.getMethod("setServiceDispatcher", sdClass);
                    setSD.invoke(tvInstance, serviceDispatcher);
                    L.accept("Called setServiceDispatcher(proxy)");
                } catch (Throwable e) {
                    L.accept("setServiceDispatcher failed: " + e);
                }
            } else {
                try {
                    java.lang.reflect.Constructor<?> sdCtor = sdClass.getDeclaredConstructor();
                    sdCtor.setAccessible(true);
                    serviceDispatcher = sdCtor.newInstance();
                    try {
                        java.lang.reflect.Method setSD = tvClass.getMethod("setServiceDispatcher", sdClass);
                        setSD.invoke(tvInstance, serviceDispatcher);
                        L.accept("Instantiated and set concrete ServiceDispatcher");
                    } catch (Throwable e) {
                        L.accept("Could not set concrete ServiceDispatcher: " + e);
                    }
                } catch (Throwable e) {
                    L.accept("Could not instantiate ServiceDispatcher: " + e);
                }
            }
        } catch (Throwable e) {
            L.accept("ServiceDispatcher class not found or failed to prepare: " + e);
        }

        try {
            Class<?> listenerClass = Class.forName("androidx.car.app.activity.renderer.surface.TemplateSurfaceView$OnCreateInputConnectionListener");
            // IProxyInputConnection class
            Class<?> proxyInputConnClass = Class.forName("androidx.car.app.activity.renderer.IProxyInputConnection");

            Object proxyInputConn = null;
            if (proxyInputConnClass.isInterface()) {
                proxyInputConn = java.lang.reflect.Proxy.newProxyInstance(
                        proxyInputConnClass.getClassLoader(),
                        new Class<?>[]{proxyInputConnClass},
                        (proxy, method, args) -> {
                            String name = method.getName();
                            L.accept("IProxyInputConnection proxy called: " + name);
                            // implement getEditorInfo() -> return EditorInfo
                            if (name.equals("getEditorInfo")) {
                                return new android.view.inputmethod.EditorInfo();
                            }
                            // For other methods, return sensible defaults (null/0/false)
                            Class<?> rt = method.getReturnType();
                            if (rt == boolean.class) return false;
                            if (rt == int.class) return 0;
                            return null;
                        });
            } else {
                try {
                    java.lang.reflect.Constructor<?> c = proxyInputConnClass.getDeclaredConstructor();
                    c.setAccessible(true);
                    proxyInputConn = c.newInstance();
                } catch (Throwable t) {
                    proxyInputConn = null;
                }
            }

            Object finalProxyInputConn = proxyInputConn;
            Object listenerProxy = java.lang.reflect.Proxy.newProxyInstance(
                    listenerClass.getClassLoader(),
                    new Class<?>[]{listenerClass},
                    (proxy, method, args) -> {
                        L.accept("OnCreateInputConnectionListener invoked: " + method.getName());
                        // method should be onCreateInputConnection(EditorInfo)
                        return finalProxyInputConn;
                    });

            try {
                java.lang.reflect.Method setListener = tvClass.getMethod("setOnCreateInputConnectionListener", listenerClass);
                setListener.invoke(tvInstance, listenerProxy);
                L.accept("setOnCreateInputConnectionListener(proxy) invoked");
            } catch (NoSuchMethodException nsme) {
                for (java.lang.reflect.Method m : tvClass.getMethods()) {
                    if (m.getName().equals("setOnCreateInputConnectionListener") && m.getParameterTypes().length == 1) {
                        m.invoke(tvInstance, listenerProxy);
                        L.accept("setOnCreateInputConnectionListener (fallback) invoked: " + m);
                        break;
                    }
                }
            } catch (Throwable e) {
                L.accept("Failed to set OnCreateInputConnectionListener: " + e);
            }
        } catch (Throwable e) {
            L.accept("OnCreateInputConnectionListener/IProxyInputConnection not found: " + e);
        }

        try {
            java.lang.reflect.Method onStart = tvClass.getMethod("onStartInput");
            onStart.setAccessible(true);
            onStart.invoke(tvInstance);
            L.accept("Called onStartInput()");
        } catch (Throwable e) {
            L.accept("onStartInput failed: " + e);
        }
        try {
            java.lang.reflect.Method createIC = tvClass.getMethod("onCreateInputConnection", android.view.inputmethod.EditorInfo.class);
            createIC.setAccessible(true);
            Object editorInfo = new android.view.inputmethod.EditorInfo();
            Object ic = null;
            try {
                ic = createIC.invoke(tvInstance, editorInfo);
                L.accept("onCreateInputConnection returned: " + (ic == null ? "null" : ic.getClass().getName()));
            } catch (Throwable e) {
                L.accept("onCreateInputConnection invocation failed: " + e);
            }
        } catch (NoSuchMethodException nsme) {
            L.accept("onCreateInputConnection(EditorInfo) not found: " + nsme);
        } catch (Throwable t) {
            L.accept("onCreateInputConnection reflection failure: " + t);
        }

        try {
            java.lang.reflect.Method onUpdate = tvClass.getMethod("onUpdateSelection", int.class, int.class, int.class, int.class);
            onUpdate.invoke(tvInstance, 0, 0, 1, 1);
            L.accept("Called onUpdateSelection(0,0,1,1)");
        } catch (Throwable e) {
            L.accept("onUpdateSelection failed: " + e);
        }

        try {
            java.lang.reflect.Method onStop = tvClass.getMethod("onStopInput");
            onStop.invoke(tvInstance);
            L.accept("Called onStopInput()");
        } catch (Throwable e) {
            L.accept("onStopInput failed: " + e);
        }

        try {
            java.lang.reflect.Method checkEditor = tvClass.getMethod("onCheckIsTextEditor");
            Object isEditor = checkEditor.invoke(tvInstance);
            L.accept("onCheckIsTextEditor() -> " + isEditor);
        } catch (Throwable e) {
            L.accept("onCheckIsTextEditor failed: " + e);
        }

        try {
            java.lang.reflect.Method acc = tvClass.getMethod("getAccessibilityClassName");
            Object accName = acc.invoke(tvInstance);
            L.accept("getAccessibilityClassName() -> " + accName);
        } catch (Throwable e) {
            L.accept("getAccessibilityClassName failed: " + e);
        }
        try {
            java.lang.reflect.Method checkProxy = tvClass.getMethod("checkInputConnectionProxy", android.view.View.class);
            Object proxyRes = checkProxy.invoke(tvInstance, (Object) null);
            L.accept("checkInputConnectionProxy(null) -> " + proxyRes);
        } catch (Throwable e) {
            L.accept("checkInputConnectionProxy failed: " + e);
        }

        try {
            java.lang.reflect.Method attached = tvClass.getDeclaredMethod("onAttachedToWindow");
            attached.setAccessible(true);
            attached.invoke(tvInstance);
            L.accept("onAttachedToWindow invoked");
        } catch (Throwable e) {
            L.accept("onAttachedToWindow failed: " + e);
        }
        try {
            java.lang.reflect.Method detached = tvClass.getDeclaredMethod("onDetachedFromWindow");
            detached.setAccessible(true);
            detached.invoke(tvInstance);
            L.accept("onDetachedFromWindow invoked");
        } catch (Throwable e) {
            L.accept("onDetachedFromWindow failed: " + e);
        }

        try {
            long now = android.os.SystemClock.uptimeMillis();
            MotionEvent me = MotionEvent.obtain(now, now, MotionEvent.ACTION_DOWN, 10f, 10f, 0);
            java.lang.reflect.Method handleTouch = tvClass.getDeclaredMethod("handleTouchEvent", MotionEvent.class);
            handleTouch.setAccessible(true);
            Object touchRes = handleTouch.invoke(tvInstance, me);
            L.accept("handleTouchEvent(ACTION_DOWN) -> " + touchRes);
            me.recycle();
        } catch (Throwable e) {
            L.accept("handleTouchEvent failed: " + e);
        }

        try {
            KeyEvent ke = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_A);
            java.lang.reflect.Method dispatchKey = tvClass.getMethod("dispatchKeyEvent", KeyEvent.class);
            Object dispatchRes = dispatchKey.invoke(tvInstance, ke);
            L.accept("dispatchKeyEvent(KEYCODE_A) -> " + dispatchRes);
        } catch (Throwable e) {
            L.accept("dispatchKeyEvent failed: " + e);
        }

        try {
            java.lang.reflect.Method setSurfacePackageObject = tvClass.getMethod("setSurfacePackage", Object.class);
            setSurfacePackageObject.invoke(tvInstance, new Object());
            L.accept("setSurfacePackage(Object) invoked with plain Object (should log error path)");
        } catch (NoSuchMethodException nsme) {
            for (java.lang.reflect.Method m : tvClass.getMethods()) {
                if (m.getName().equals("setSurfacePackage") && m.getParameterTypes().length == 1) {
                    try {
                        m.invoke(tvInstance, new Object());
                        L.accept("setSurfacePackage(fallback) invoked");
                    } catch (Throwable ex) {
                        L.accept("setSurfacePackage(fallback) failed: " + ex);
                    }
                    break;
                }
            }
        } catch (Throwable e) {
            L.accept("setSurfacePackage invocation failed: " + e);
        }

        try {
            Class<?> surfacePackageClass = Class.forName("android.view.SurfaceControlViewHost$SurfacePackage");
            try {
                java.lang.reflect.Method msp = tvClass.getDeclaredMethod("setSurfacePackage", surfacePackageClass);
                msp.setAccessible(true);
                msp.invoke(tvInstance, new Object[]{null});
                L.accept("Called setSurfacePackage(SurfacePackage) with null (best-effort)");
            } catch (NoSuchMethodException nsme) {
                L.accept("No setSurfacePackage(SurfacePackage) method: " + nsme);
            }
        } catch (ClassNotFoundException cnfe) {
            L.accept("SurfacePackage type not present on this SDK: " + cnfe);
        } catch (Throwable e) {
            L.accept("Error while attempting SurfacePackage path: " + e);
        }

        try {
            java.lang.reflect.Method copyEditorInfo = tvClass.getDeclaredMethod("copyEditorInfo", android.view.inputmethod.EditorInfo.class, android.view.inputmethod.EditorInfo.class);
            copyEditorInfo.setAccessible(true);
            android.view.inputmethod.EditorInfo from = new android.view.inputmethod.EditorInfo();
            from.inputType = android.text.InputType.TYPE_CLASS_TEXT;
            android.view.inputmethod.EditorInfo to = new android.view.inputmethod.EditorInfo();
            copyEditorInfo.invoke(tvInstance, from, to);
            L.accept("copyEditorInfo invoked; to.inputType=" + to.inputType);
        } catch (NoSuchMethodException nsme) {
            L.accept("copyEditorInfo private method not found: " + nsme);
        } catch (Throwable e) {
            L.accept("copyEditorInfo invocation failed: " + e);
        }

        try {
            java.lang.reflect.Method getToken = tvClass.getMethod("getSurfaceToken");
            Object token = getToken.invoke(tvInstance);
            L.accept("getSurfaceToken() -> " + token);
        } catch (Throwable e) {
            L.accept("getSurfaceToken failed or not available: " + e);
        }

        L.accept("=== exerciseTemplateSurfaceViewReflection COMPLETE, " + log.size() + " log entries ===");
    }

    public void exerciseCarAppViewModel() {
        final String TAG = "exerciseCarAppViewModel";
        try {
            // Acquire real application / package info
            Application application = (Application) getCarContext().getApplicationContext();
            ComponentName componentName = new ComponentName(application.getPackageName(),
                    application.getClass().getName());

            // Use the library-provided DEFAULT_SESSION_INFO if available (safe fallback)
            SessionInfo sessionInfo = null;
            try {
                Field f = SessionInfo.class.getDeclaredField("DEFAULT_SESSION_INFO");
                f.setAccessible(true);
                sessionInfo = (SessionInfo) f.get(null);
            } catch (Throwable e) {
                // fallback: try to call a default constructor if present (rare)
                try {
                    sessionInfo = SessionInfo.DEFAULT_SESSION_INFO;
                } catch (Throwable ignored) {
                }
            }
            if (sessionInfo == null) {
                Log.w(TAG, "Couldn't obtain SessionInfo.DEFAULT_SESSION_INFO; attempting newInstance via no-arg");
                try {
                    sessionInfo = SessionInfo.class.newInstance();
                } catch (Throwable ex) {
                    Log.w(TAG, "Failed to create SessionInfo instance reflectively: " + ex);
                }
            }

            // Construct CarAppViewModel using real Application/ComponentName/SessionInfo
            @SuppressLint("RestrictedApi") CarAppViewModel vm = null;
            try {
                @SuppressLint("RestrictedApi") Constructor<CarAppViewModel> ctor =
                        CarAppViewModel.class.getConstructor(Application.class, ComponentName.class,
                                SessionInfo.class);
                vm = ctor.newInstance(application, componentName, sessionInfo);
                Log.i(TAG, "Constructed CarAppViewModel instance: " + vm);
            } catch (NoSuchMethodException nsme) {
                // try alternative (if signature differs)
                try {
                    @SuppressLint("RestrictedApi") Constructor<CarAppViewModel> alt =
                            CarAppViewModel.class.getDeclaredConstructor(Application.class,
                                    ComponentName.class, SessionInfo.class);
                    alt.setAccessible(true);
                    vm = alt.newInstance(application, componentName, sessionInfo);
                    Log.i(TAG, "Constructed CarAppViewModel (declared ctor): " + vm);
                } catch (Throwable t) {
                    Log.e(TAG, "Failed to construct CarAppViewModel: " + t);
                    return;
                }
            }

            // 1) getServiceConnectionManager()
            try {
                Method mGetScm = CarAppViewModel.class.getDeclaredMethod("getServiceConnectionManager");
                mGetScm.setAccessible(true);
                Object scm = mGetScm.invoke(vm);
                Log.i(TAG, "getServiceConnectionManager -> " + scm);
            } catch (Throwable t) {
                Log.w(TAG, "getServiceConnectionManager failed: " + t);
            }

            // 2) setServiceConnectionManager(...) - call with same object if available (safe), or null.
            try {
                Method mGetScm = CarAppViewModel.class.getDeclaredMethod("getServiceConnectionManager");
                mGetScm.setAccessible(true);
                Object scm = mGetScm.invoke(vm);
                Method mSetScm = CarAppViewModel.class.getDeclaredMethod("setServiceConnectionManager",
                        Class.forName("androidx.car.app.activity.ServiceConnectionManager"));
                mSetScm.setAccessible(true);
                // If we have scm, pass it, else pass null by reflection (allowed)
                mSetScm.invoke(vm, scm);
                Log.i(TAG, "setServiceConnectionManager invoked with " + scm);
            } catch (ClassNotFoundException cnfe) {
                // If ServiceConnectionManager class is hidden, try generic reflection fallback:
                try {
                    Method mSetScm = CarAppViewModel.class.getDeclaredMethod("setServiceConnectionManager",
                            Object.class);
                    mSetScm.setAccessible(true);
                    mSetScm.invoke(vm, (Object) null);
                    Log.i(TAG, "setServiceConnectionManager(Object) fallback invoked with null");
                } catch (Throwable t) {
                    Log.w(TAG, "setServiceConnectionManager fallback failed: " + t);
                }
            } catch (Throwable t) {
                Log.w(TAG, "setServiceConnectionManager failed: " + t);
            }

            // 3) getServiceDispatcher()
            try {
                Method mGetDispatcher = CarAppViewModel.class.getDeclaredMethod("getServiceDispatcher");
                mGetDispatcher.setAccessible(true);
                Object dispatcher = mGetDispatcher.invoke(vm);
                Log.i(TAG, "getServiceDispatcher -> " + dispatcher);
            } catch (Throwable t) {
                Log.w(TAG, "getServiceDispatcher failed: " + t);
            }

            // 4) setRendererCallback(IRendererCallback) — provide a dynamic proxy for the interface
            try {
                Class<?> ircCls = Class.forName("androidx.car.app.activity.renderer.IRendererCallback");
                Object proxy = Proxy.newProxyInstance(ircCls.getClassLoader(), new Class<?>[]{ircCls},
                        (p, method, args) -> {
                            Log.i(TAG, "IRendererCallback proxy invoked: " + method.getName());
                            // Methods frequently return void; return null by default (binder methods expect null)
                            if (method.getReturnType() == boolean.class) {
                                return false;
                            }
                            if (method.getReturnType() == int.class) {
                                return 0;
                            }
                            return null;
                        });
                Method mSetRenderer = CarAppViewModel.class.getDeclaredMethod("setRendererCallback", ircCls);
                mSetRenderer.setAccessible(true);
                mSetRenderer.invoke(vm, proxy);
                Log.i(TAG, "setRendererCallback invoked with dynamic proxy");
            } catch (ClassNotFoundException cnf) {
                Log.w(TAG, "IRendererCallback not found (skipping setRendererCallback): " + cnf);
            } catch (NoSuchMethodException nsme) {
                // fallback: try to find any method named setRendererCallback
                try {
                    for (Method mm : CarAppViewModel.class.getDeclaredMethods()) {
                        if (mm.getName().equals("setRendererCallback") && mm.getParameterTypes().length == 1) {
                            mm.setAccessible(true);
                            mm.invoke(vm, (Object) null);
                            Log.i(TAG, "Fallback invoked setRendererCallback with null");
                            break;
                        }
                    }
                } catch (Throwable t) {
                    Log.w(TAG, "Fallback setRendererCallback failed: " + t);
                }
            } catch (Throwable t) {
                Log.w(TAG, "setRendererCallback failed: " + t);
            }

            // 5) setActivity(Activity) - pass current activity if available, else null
            try {
                Activity maybeActivity = null;
                try {
                    // If running inside a Screen, try to obtain an Activity via CarContext (best-effort)
                    Object baseCtx = getCarContext().getBaseContext();
                    if (baseCtx instanceof Activity) {
                        maybeActivity = (Activity) baseCtx;
                    }
                } catch (Throwable ignored) { }

                Method setAct = CarAppViewModel.class.getDeclaredMethod("setActivity", Activity.class);
                setAct.setAccessible(true);
                setAct.invoke(vm, maybeActivity);
                Log.i(TAG, "setActivity invoked with " + maybeActivity);
            } catch (Throwable t) {
                Log.w(TAG, "setActivity failed: " + t);
            }

            // 6) resetState()
            try {
                Method reset = CarAppViewModel.class.getDeclaredMethod("resetState");
                reset.setAccessible(true);
                reset.invoke(vm);
                Log.i(TAG, "resetState invoked");
            } catch (Throwable t) {
                Log.w(TAG, "resetState failed: " + t);
            }

            // 7) onCleared() - triggers cleanup
            try {
                Method onCleared = CarAppViewModel.class.getDeclaredMethod("onCleared");
                onCleared.setAccessible(true);
                onCleared.invoke(vm);
                Log.i(TAG, "onCleared invoked");
            } catch (Throwable t) {
                Log.w(TAG, "onCleared failed: " + t);
            }

            // 8) getError(), getState() (LiveData getters)
            try {
                Method getError = CarAppViewModel.class.getDeclaredMethod("getError");
                getError.setAccessible(true);
                Object liveError = getError.invoke(vm);
                Log.i(TAG, "getError -> " + liveError);
            } catch (Throwable t) {
                Log.w(TAG, "getError failed: " + t);
            }
            try {
                Method getState = CarAppViewModel.class.getDeclaredMethod("getState");
                getState.setAccessible(true);
                Object liveState = getState.invoke(vm);
                Log.i(TAG, "getState -> " + liveState);
            } catch (Throwable t) {
                Log.w(TAG, "getState failed: " + t);
            }

            // 9) onError(ErrorHandler.ErrorType) - pick a safe enum value if possible
            try {
                Class<?> errCls = Class.forName("androidx.car.app.activity.ErrorHandler$ErrorType");
                Object[] enumConstants = errCls.getEnumConstants();
                Object chosen = enumConstants.length > 0 ? enumConstants[0] : null;
                Method onError = CarAppViewModel.class.getDeclaredMethod("onError", errCls);
                onError.setAccessible(true);
                onError.invoke(vm, chosen);
                Log.i(TAG, "onError invoked with " + chosen);
            } catch (ClassNotFoundException cnf) {
                // fallback to calling the public method signature directly via a runtime object
                Log.w(TAG, "ErrorType enum not found - skipping onError(enum)");
            } catch (Throwable t) {
                Log.w(TAG, "onError failed: " + t);
            }

            // 10) onConnect()
            try {
                Method onConnect = CarAppViewModel.class.getDeclaredMethod("onConnect");
                onConnect.setAccessible(true);
                onConnect.invoke(vm);
                Log.i(TAG, "onConnect invoked");
            } catch (Throwable t) {
                Log.w(TAG, "onConnect failed: " + t);
            }

            // 11) retryBinding() — this tries to recreate the Activity; risky but we'll call guardedly:
            try {
                Method retry = CarAppViewModel.class.getDeclaredMethod("retryBinding");
                retry.setAccessible(true);
                retry.invoke(vm);
                Log.i(TAG, "retryBinding invoked (Activity.recreate may have been called)");
            } catch (Throwable t) {
                Log.w(TAG, "retryBinding failed (likely no Activity available or recreate blocked): " + t);
            }

            // 12) onHostUpdated()
            try {
                Method onHostUpdated = CarAppViewModel.class.getDeclaredMethod("onHostUpdated");
                onHostUpdated.setAccessible(true);
                onHostUpdated.invoke(vm);
                Log.i(TAG, "onHostUpdated invoked");
            } catch (Throwable t) {
                Log.w(TAG, "onHostUpdated failed: " + t);
            }

            // 13) static helpers: setActivityResult(int, Intent) and getCallingActivity()
            try {
                Method setActRes = CarAppViewModel.class.getDeclaredMethod("setActivityResult", int.class, android.content.Intent.class);
                setActRes.setAccessible(true);
                setActRes.invoke(null, Activity.RESULT_CANCELED, null);
                Log.i(TAG, "static setActivityResult invoked with RESULT_CANCELED");
            } catch (Throwable t) {
                Log.w(TAG, "setActivityResult failed: " + t);
            }
            try {
                Method getCalling = CarAppViewModel.class.getDeclaredMethod("getCallingActivity");
                getCalling.setAccessible(true);
                Object calling = getCalling.invoke(null);
                Log.i(TAG, "static getCallingActivity -> " + calling);
            } catch (Throwable t) {
                Log.w(TAG, "getCallingActivity failed: " + t);
            }

            // 14) updateWindowInsets(Insets, DisplayCutoutCompat) — supply simple Insets and null cutout
            try {
                // Create Insets via reflection (android.graphics.Insets).
                Class<?> insetsCls = Class.forName("android.graphics.Insets");
                Method of = insetsCls.getMethod("of", int.class, int.class, int.class, int.class);
                Object zeroInsets = of.invoke(null, 0, 0, 0, 0);

                // DisplayCutoutCompat may be absent; pass null
                Method updateWindowInsets = CarAppViewModel.class.getDeclaredMethod("updateWindowInsets", insetsCls, Class.forName("androidx.core.view.DisplayCutoutCompat"));
                updateWindowInsets.setAccessible(true);
                updateWindowInsets.invoke(vm, zeroInsets, null);
                Log.i(TAG, "updateWindowInsets invoked with zero insets");
            } catch (ClassNotFoundException cnf) {
                // library types missing — attempt a fallback that calls the method signature with Object
                try {
                    for (Method mm : CarAppViewModel.class.getDeclaredMethods()) {
                        if (mm.getName().equals("updateWindowInsets")) {
                            mm.setAccessible(true);
                            mm.invoke(vm, (Object) null, (Object) null);
                            Log.i(TAG, "updateWindowInsets fallback invoked with nulls");
                            break;
                        }
                    }
                } catch (Throwable t) {
                    Log.w(TAG, "updateWindowInsets fallback failed: " + t);
                }
            } catch (Throwable t) {
                Log.w(TAG, "updateWindowInsets failed: " + t);
            }

            // 15) setInsetsListener(IInsetsListener) — try a dynamic proxy if interface exists
            try {
                Class<?> iInsetsCls = Class.forName("androidx.car.app.activity.renderer.IInsetsListener");
                Object proxyInsets = Proxy.newProxyInstance(iInsetsCls.getClassLoader(), new Class<?>[]{iInsetsCls},
                        (p, method, args) -> {
                            Log.i(TAG, "IInsetsListener proxy method: " + method.getName());
                            return null;
                        });
                Method setInsets = CarAppViewModel.class.getDeclaredMethod("setInsetsListener", iInsetsCls);
                setInsets.setAccessible(true);
                setInsets.invoke(vm, proxyInsets);
                Log.i(TAG, "setInsetsListener invoked with dynamic proxy");
            } catch (ClassNotFoundException cnf) {
                Log.w(TAG, "IInsetsListener not available - skipping setInsetsListener: " + cnf);
            } catch (Throwable t) {
                Log.w(TAG, "setInsetsListener failed: " + t);
            }

            // 16) invoke private dispatchInsetsUpdates() via reflection
            try {
                Method dispatchInsets = CarAppViewModel.class.getDeclaredMethod("dispatchInsetsUpdates");
                dispatchInsets.setAccessible(true);
                dispatchInsets.invoke(vm);
                Log.i(TAG, "dispatchInsetsUpdates invoked (private)");
            } catch (NoSuchMethodException nsme) {
                Log.w(TAG, "dispatchInsetsUpdates not present: " + nsme);
            } catch (Throwable t) {
                Log.w(TAG, "dispatchInsetsUpdates invocation failed: " + t);
            }

            // 17) getSafeInsets(DisplayCutoutCompat) private method — call with null
            try {
                Method getSafe = CarAppViewModel.class.getDeclaredMethod("getSafeInsets", Class.forName("androidx.core.view.DisplayCutoutCompat"));
                getSafe.setAccessible(true);
                Object safeInsets = getSafe.invoke(vm, (Object) null);
                Log.i(TAG, "getSafeInsets(null) -> " + safeInsets);
            } catch (ClassNotFoundException cnf) {
                // fallback: try any method named getSafeInsets
                try {
                    for (Method mm : CarAppViewModel.class.getDeclaredMethods()) {
                        if (mm.getName().equals("getSafeInsets")) {
                            mm.setAccessible(true);
                            Object safe = mm.invoke(vm, (Object) null);
                            Log.i(TAG, "getSafeInsets fallback -> " + safe);
                            break;
                        }
                    }
                } catch (Throwable t) {
                    Log.w(TAG, "getSafeInsets fallback failed: " + t);
                }
            } catch (Throwable t) {
                Log.w(TAG, "getSafeInsets invocation failed: " + t);
            }

            Log.i(TAG, "exerciseCarAppViewModel completed (check logs for successes and failures).");
        } catch (Throwable outer) {
            Log.e(TAG, "Top-level failure exercising CarAppViewModel: " + outer);
        }
    }

    public void exerciseServiceConnectionManager() {
        final String TAG = "ExerciseServiceConnMgr";
        try {
            android.content.Context context;
            try {
                context = (android.content.Context) this.getClass()
                        .getMethod("getCarContext")
                        .invoke(this);
            } catch (Exception e) {
                    context = (android.content.Context) this.getClass()
                            .getMethod("getContext")
                            .invoke(this);
            }

            Class<?> scmClass = Class.forName("androidx.car.app.activity.ServiceConnectionManager");
            Class<?> sessionInfoClass = Class.forName("androidx.car.app.SessionInfo");

            Object sessionInfo = null;
            try {
                java.lang.reflect.Field defField =
                        sessionInfoClass.getField("DEFAULT_SESSION_INFO");
                sessionInfo = defField.get(null);
            } catch (NoSuchFieldException nsfe) {
                try {
                    sessionInfo = sessionInfoClass.getDeclaredConstructor().newInstance();
                } catch (Exception ignore) {
                    sessionInfo = null;
                }
            }

            android.content.ComponentName serviceComponentName = new android.content.ComponentName(
                    context, this.getClass());

            Class<?> listenerInterface = Class.forName(
                    "androidx.car.app.activity.ServiceConnectionManager$ServiceConnectionListener");
            Object listener = java.lang.reflect.Proxy.newProxyInstance(
                    listenerInterface.getClassLoader(),
                    new Class[]{listenerInterface},
                    (proxy, method, args) -> {
                        String mname = method.getName();
                        if ("onConnect".equals(mname)) {
                            android.util.Log.i(TAG, "ServiceConnectionListener.onConnect()");
                            return null;
                        }
                        if ("onError".equals(mname) && args != null && args.length == 1) {
                            android.util.Log.w(TAG, "ServiceConnectionListener.onError: " + args[0]);
                            return null;
                        }
                        // methods from ErrorHandler or other interface: log then return defaults
                        android.util.Log.d(TAG, "ServiceConnectionListener." + mname + " invoked");
                        return null;
                    });

            java.lang.reflect.Constructor<?> ctor = null;
            for (java.lang.reflect.Constructor<?> c : scmClass.getDeclaredConstructors()) {
                Class<?>[] params = c.getParameterTypes();
                if (params.length == 4) {
                    ctor = c;
                    break;
                }
            }
            if (ctor == null) {
                android.util.Log.e(TAG, "Unable to find SCM constructor");
                return;
            }
            ctor.setAccessible(true);
            Object scm = ctor.newInstance(context, serviceComponentName, sessionInfo, listener);
            android.util.Log.i(TAG, "Created ServiceConnectionManager instance: " + scm);

            java.util.function.BiFunction<String, Object[], Object> invokeMethod = (name, args) -> {
                try {
                    Method found = null;
                    Method[] methods = scmClass.getDeclaredMethods();
                    for (Method m : methods) {
                        if (m.getName().equals(name)
                                && (args == null ? m.getParameterTypes().length == 0
                                : m.getParameterTypes().length == args.length)) {
                            found = m;
                            break;
                        }
                    }
                    if (found == null) {
                        found = scmClass.getMethod(name, (Class<?>[]) null);
                    }
                    found.setAccessible(true);
                    Object res;
                    if (args == null || args.length == 0) {
                        res = found.invoke(scm);
                    } else {
                        res = found.invoke(scm, args);
                    }
                    android.util.Log.i(TAG, "Invoked " + name + " -> " + res);
                    return res;
                } catch (Exception e) {
                    android.util.Log.w(TAG, "Invocation of " + name + " failed: " + e);
                    return null;
                }
            };

            invokeMethod.apply("getServiceComponentName", null);
            invokeMethod.apply("getServiceDispatcher", null);
            invokeMethod.apply("getServiceConnection", null);
            invokeMethod.apply("getHandshakeInfo", null);
            Object bound = invokeMethod.apply("isBound", null);

            try {
                Method setSC = scmClass.getDeclaredMethod("setServiceConnection",
                        android.content.ServiceConnection.class);
                setSC.setAccessible(true);
                setSC.invoke(scm, new Object[]{null});
                android.util.Log.i(TAG, "setServiceConnection(null) invoked");
            } catch (NoSuchMethodException nsme) {
                android.util.Log.d(TAG, "setServiceConnection(ServiceConnection) not present or not accessible");
            } catch (Exception e) {
                android.util.Log.w(TAG, "setServiceConnection(null) failed: " + e);
            }

            try {
                for (Method m : scmClass.getDeclaredMethods()) {
                    if (m.getName().equals("setRendererService") && m.getParameterTypes().length == 1) {
                        m.setAccessible(true);
                        m.invoke(scm, new Object[]{null});
                        android.util.Log.i(TAG, "setRendererService(null) invoked");
                        break;
                    }
                }
            } catch (Exception e) {
                android.util.Log.w(TAG, "setRendererService(null) failed: " + e);
            }

            try {
                android.content.Intent dummy = new android.content.Intent("com.example.NON_EXISTENT_RENDERER_ACTION");

                Class<?> iCarAppActivityClass = Class.forName("androidx.car.app.activity.renderer.ICarAppActivity");
                Object iCarAppActivityProxy = java.lang.reflect.Proxy.newProxyInstance(
                        iCarAppActivityClass.getClassLoader(),
                        new Class[]{iCarAppActivityClass},
                        (proxy, method, args) -> {
                            // log calls and return appropriate defaults
                            android.util.Log.d(TAG, "ICarAppActivity proxy method: " + method.getName());
                            Class<?> ret = method.getReturnType();
                            if (ret == boolean.class) return false;
                            if (ret == int.class) return 0;
                            return null;
                        });

                Method bindMethod = null;
                for (Method m : scmClass.getDeclaredMethods()) {
                    if (m.getName().equals("bind") && m.getParameterTypes().length == 3) {
                        bindMethod = m;
                        break;
                    }
                }
                if (bindMethod != null) {
                    bindMethod.setAccessible(true);
                    Object bindRes = bindMethod.invoke(scm, dummy, iCarAppActivityProxy, Integer.valueOf(-1));
                    android.util.Log.i(TAG, "bind(...) invoked, result: " + bindRes);
                } else {
                    android.util.Log.d(TAG, "bind(...) method not found");
                }
            } catch (ClassNotFoundException cnfe) {
                android.util.Log.d(TAG, "ICarAppActivity class not available: " + cnfe);
            } catch (Exception e) {
                android.util.Log.w(TAG, "bind(...) attempt failed: " + e);
            }

            invokeMethod.apply("unbind", null);

            invokeMethod.apply("initializeService", null);

            try {
                Method updateIntent = scmClass.getDeclaredMethod("updateIntent");
                updateIntent.setAccessible(true);
                Object updateRes = updateIntent.invoke(scm);
                android.util.Log.i(TAG, "updateIntent() -> " + updateRes);
            } catch (NoSuchMethodException nsme) {
                android.util.Log.d(TAG, "updateIntent() not present");
            } catch (Exception e) {
                android.util.Log.w(TAG, "updateIntent() invocation failed: " + e);
            }

            android.util.Log.i(TAG, "ServiceConnectionManager reflection exercise completed.");
        } catch (Throwable t) {
            android.util.Log.e("ExerciseServiceConnMgr", "Unexpected failure: ", t);
        }
    }

    public void exerciseBaseCarAppActivityReflection() {
        final String TAG = "ExerciseBaseCarAppActivity";
        StringBuilder out = new StringBuilder();
        try {
            Class<?> clazz = Class.forName("androidx.car.app.activity.BaseCarAppActivity");
            out.append("Class loaded: ").append(clazz.getName()).append("\n");

            //instantiate using no-arg ctor
            Object instance = null;
            try {
                instance = clazz.getDeclaredConstructor().newInstance();
                out.append("Instance created via no-arg ctor: ").append(instance).append("\n");
            } catch (Throwable t) {
                out.append("Failed to instantiate via no-arg ctor: ").append(t).append("\n");
            }

            // Helper: create default arg for a parameter type
            java.util.function.Function<Class<?>, Object> makeArg = (paramType) -> {
                try {
                    if (paramType.isPrimitive()) {
                        if (paramType == boolean.class) return false;
                        if (paramType == byte.class) return (byte) 0;
                        if (paramType == short.class) return (short) 0;
                        if (paramType == int.class) return 0;
                        if (paramType == long.class) return 0L;
                        if (paramType == float.class) return 0f;
                        if (paramType == double.class) return 0d;
                        if (paramType == char.class) return '\0';
                    }
                    if (paramType == java.lang.String.class) {
                        return "reflection-string";
                    }
                    if (android.os.Bundle.class.isAssignableFrom(paramType)) {
                        return new android.os.Bundle();
                    }
                    if (android.content.Intent.class.isAssignableFrom(paramType)) {
                        return new android.content.Intent();
                    }
                    if (android.content.ComponentName.class.isAssignableFrom(paramType)) {
                        return new android.content.ComponentName("com.example.carapiaccess",
                                "com.example.carapiaccess.DummyService");
                    }
                    if (paramType == Integer.class) return Integer.valueOf(0);
                    if (paramType == Boolean.class) return Boolean.FALSE;

                    // Special handling: SessionInfo.DEFAULT_SESSION_INFO if present
                    try {
                        if (paramType.getName().equals("androidx.car.app.SessionInfo")) {
                            try {
                                Class<?> sessionInfoClass = Class.forName("androidx.car.app.SessionInfo");
                                java.lang.reflect.Field f = null;
                                try {
                                    f = sessionInfoClass.getField("DEFAULT_SESSION_INFO");
                                } catch (NoSuchFieldException nsf) {
                                    try {
                                        f = sessionInfoClass.getDeclaredField("DEFAULT_SESSION_INFO");
                                        f.setAccessible(true);
                                    } catch (NoSuchFieldException ignore) {
                                        f = null;
                                    }
                                }
                                if (f != null) {
                                    Object val = f.get(null);
                                    if (val != null) return val;
                                }
                            } catch (Throwable ignore) {
                            }
                        }
                    } catch (Throwable ignore) { }

                    // If it's an interface, create a dynamic proxy returning sensible defaults.
                    if (paramType.isInterface()) {
                        java.lang.reflect.InvocationHandler handler = (proxy, method, args) -> {
                            Class<?> ret = method.getReturnType();
                            if (ret == void.class) return null;
                            if (ret.isPrimitive()) {
                                if (ret == boolean.class) return false;
                                if (ret == byte.class) return (byte) 0;
                                if (ret == short.class) return (short) 0;
                                if (ret == int.class) return 0;
                                if (ret == long.class) return 0L;
                                if (ret == float.class) return 0f;
                                if (ret == double.class) return 0d;
                                if (ret == char.class) return '\0';
                            }
                            if (ret == java.lang.String.class) return "";
                            return null;
                        };
                        return java.lang.reflect.Proxy.newProxyInstance(
                                paramType.getClassLoader(),
                                new Class<?>[]{paramType},
                                handler);
                    }

                    // Try to instantiate with no-arg constructor if available
                    try {
                        java.lang.reflect.Constructor<?> ctor = paramType.getDeclaredConstructor();
                        ctor.setAccessible(true);
                        return ctor.newInstance();
                    } catch (Throwable ignored) {
                    }

                    // Fallbacks for common Android types
                    if (android.view.Window.class.isAssignableFrom(paramType)) return null;
                    if (android.view.View.class.isAssignableFrom(paramType)) return null;
                    if (android.graphics.Bitmap.class.isAssignableFrom(paramType)) return null;

                    return null;
                } catch (Throwable t) {
                    return null;
                }
            };

            // Gather methods
            java.util.Set<java.lang.reflect.Method> methodsToTry = new java.util.HashSet<>();
            for (java.lang.reflect.Method m : clazz.getDeclaredMethods()) methodsToTry.add(m);
            for (java.lang.reflect.Method m : clazz.getMethods()) methodsToTry.add(m);

            out.append("Found methods to try: ").append(methodsToTry.size()).append("\n\n");

            int invokeCounter = 0;
            for (java.lang.reflect.Method method : methodsToTry) {
                // Skip synthetic / bridge methods
                if (method.isSynthetic() || method.isBridge()) continue;

                invokeCounter++;
                String mName = method.getName();
                Class<?>[] ptypes = method.getParameterTypes();

                // Build textual argument summary
                StringBuilder argSummary = new StringBuilder();
                Object[] args = new Object[ptypes.length];
                for (int i = 0; i < ptypes.length; i++) {
                    args[i] = makeArg.apply(ptypes[i]);
                    argSummary.append("arg[").append(i).append("] ")
                            .append(ptypes[i].getName()).append("=")
                            .append(String.valueOf(args[i]));
                    if (i < ptypes.length - 1) argSummary.append(", ");
                }

                // Append to the long out buffer
                out.append("=== Invoking #").append(invokeCounter).append(": ").append(mName).append(" ===\n");
                out.append("  signature: ").append(java.util.Arrays.toString(ptypes)).append("\n");
                out.append("  args: ").append(argSummary).append("\n");
                out.append("  modifiers: ").append(java.lang.reflect.Modifier.toString(method.getModifiers())).append("\n");

                // log to logcat before the invocation to see if process dies.
                String immediateLog = "INVOCATION #" + invokeCounter + " ts=" + System.currentTimeMillis()
                        + " thread=" + Thread.currentThread().getName()
                        + " method=" + method.getDeclaringClass().getName() + "#" + mName
                        + " params=" + java.util.Arrays.toString(ptypes)
                        + " args=[" + argSummary + "]";
                try {
                    android.util.Log.i(TAG, immediateLog);
                } catch (Throwable ignored) { }

                method.setAccessible(true);

                // If instance is null and method is not static, try to create an instance anyway
                Object target = instance;
                if (target == null && !java.lang.reflect.Modifier.isStatic(method.getModifiers())) {
                    try {
                        target = clazz.getDeclaredConstructor().newInstance();
                        out.append("  created fallback instance: ").append(target).append("\n");
                        try { android.util.Log.i(TAG, "Created fallback instance for method #" + invokeCounter + " -> " + target); } catch (Throwable ignored) {}
                    } catch (Throwable t) {
                        out.append("  could not create fallback instance: ").append(t).append("\n");
                        try { android.util.Log.i(TAG, "Could not create fallback instance for method #" + invokeCounter + " -> " + t); } catch (Throwable ignored) {}
                    }
                }

                try {
                    Object result = method.invoke(target, args);
                    out.append("  -> returned: ").append(result).append("\n");
                    try { android.util.Log.i(TAG, "RETURN from invocation #" + invokeCounter + " result=" + String.valueOf(result)); } catch (Throwable ignored) {}
                } catch (Throwable t) {
                    // unwrap target invocation exception if present
                    Throwable cause = t;
                    if (t instanceof java.lang.reflect.InvocationTargetException && t.getCause() != null) {
                        cause = t.getCause();
                    }
                    out.append("  -> threw: ").append(cause.getClass().getName())
                            .append(" : ").append(String.valueOf(cause.getMessage())).append("\n");
                    try { android.util.Log.w(TAG, "EXCEPTION in invocation #" + invokeCounter + " ex=" + cause, cause); } catch (Throwable ignored) {}
                }
                out.append("\n");
            }

        } catch (Throwable e) {
            out.append("Fatal reflection failure: ").append(e.getClass().getName())
                    .append(" - ").append(e.getMessage()).append("\n");
        }

        try {
            android.util.Log.i(TAG, out.toString());
        } catch (Throwable ignored) { }

    }


    public void exerciseServiceDispatcher() {
        try {
            Class<?> sdClass = Class.forName("androidx.car.app.activity.ServiceDispatcher");

            @SuppressLint("RestrictedApi") androidx.car.app.activity.ErrorHandler errorHandler = new androidx.car.app.activity.ErrorHandler() {
                @Override
                public void onError(@NonNull ErrorHandler.ErrorType errorType) {
                    android.util.Log.i("exerciseServiceDispatcher", "ErrorHandler.onError -> " + errorType);
                }
            };

            @SuppressLint("RestrictedApi") androidx.car.app.activity.ServiceDispatcher.OnBindingListener onBindTrue =
                    new androidx.car.app.activity.ServiceDispatcher.OnBindingListener() {
                        @Override
                        public boolean isBound() {
                            return true;
                        }
                    };

            @SuppressLint("RestrictedApi") java.lang.reflect.Constructor<?> ctor = sdClass.getConstructor(
                    androidx.car.app.activity.ErrorHandler.class,
                    androidx.car.app.activity.ServiceDispatcher.OnBindingListener.class);
            Object dispatcher = ctor.newInstance(errorHandler, onBindTrue);

            // Use reflection to fetch methods
            @SuppressLint("RestrictedApi") java.lang.reflect.Method setOnBindingListenerM =
                    sdClass.getMethod("setOnBindingListener",
                            androidx.car.app.activity.ServiceDispatcher.OnBindingListener.class);
            @SuppressLint("RestrictedApi") java.lang.reflect.Method dispatchM =
                    sdClass.getMethod("dispatch", String.class,
                            androidx.car.app.activity.ServiceDispatcher.OneWayCall.class);
            @SuppressLint("RestrictedApi") java.lang.reflect.Method dispatchNoFailM =
                    sdClass.getMethod("dispatchNoFail", String.class,
                            androidx.car.app.activity.ServiceDispatcher.OneWayCall.class);
            @SuppressLint("RestrictedApi") java.lang.reflect.Method fetchM =
                    sdClass.getMethod("fetch", String.class, Object.class,
                            androidx.car.app.activity.ServiceDispatcher.ReturnCall.class);
            @SuppressLint("RestrictedApi") java.lang.reflect.Method fetchNoFailM =
                    sdClass.getMethod("fetchNoFail", String.class, Object.class,
                            androidx.car.app.activity.ServiceDispatcher.ReturnCall.class);

            setOnBindingListenerM.invoke(dispatcher, onBindTrue);

            // OneWayCall - normal path
            @SuppressLint("RestrictedApi") androidx.car.app.activity.ServiceDispatcher.OneWayCall normalOneWay =
                    new androidx.car.app.activity.ServiceDispatcher.OneWayCall() {
                        @Override
                        public void invoke() {
                            android.util.Log.i("exerciseServiceDispatcher", "normalOneWay invoked");
                        }
                    };
            dispatchM.invoke(dispatcher, "oneway-normal", normalOneWay);

            // OneWayCall - throw RemoteException
            @SuppressLint("RestrictedApi") androidx.car.app.activity.ServiceDispatcher.OneWayCall remoteThrowingOneWay =
                    new androidx.car.app.activity.ServiceDispatcher.OneWayCall() {
                        @SuppressLint("RestrictedApi")
                        @Override
                        public void invoke() throws android.os.RemoteException {
                            throw new android.os.RemoteException("simulated RemoteException");
                        }
                    };
            try {
                dispatchM.invoke(dispatcher, "oneway-remote-ex", remoteThrowingOneWay);
            } catch (java.lang.reflect.InvocationTargetException ite) {
                // dispatch wraps into fetch and catches exceptions internally and will call onError.
                // InvocationTargetException may appear if reflection wrapper itself throws; log and continue.
                android.util.Log.i("exerciseServiceDispatcher", "invoked dispatch(remoteThrow) - outer exception: " + ite.getCause());
            }

            // OneWayCall - throw DeadObjectException
            @SuppressLint("RestrictedApi") androidx.car.app.activity.ServiceDispatcher.OneWayCall deadObjectOneWay =
                    new androidx.car.app.activity.ServiceDispatcher.OneWayCall() {
                        @SuppressLint("RestrictedApi")
                        @Override
                        public void invoke() throws android.os.DeadObjectException {
                            throw new android.os.DeadObjectException();
                        }
                    };
            try {
                dispatchM.invoke(dispatcher, "oneway-dead", deadObjectOneWay);
            } catch (java.lang.reflect.InvocationTargetException ite) {
                android.util.Log.i("exerciseServiceDispatcher", "invoked dispatch(deadThrow) - outer exception: " + ite.getCause());
            }

            // dispatchNoFail
            dispatchNoFailM.invoke(dispatcher, "oneway-no-fail", remoteThrowingOneWay);

            // fetch() success path
            @SuppressLint("RestrictedApi") androidx.car.app.activity.ServiceDispatcher.ReturnCall<String> successReturn =
                    new androidx.car.app.activity.ServiceDispatcher.ReturnCall<String>() {
                        @Override
                        public String invoke() {
                            return "OK";
                        }
                    };
            Object fetchSuccessRes = fetchM.invoke(dispatcher, "fetch-success", "fallback", successReturn);
            android.util.Log.i("exerciseServiceDispatcher", "fetch-success -> " + fetchSuccessRes);

            // fetch() throwing DeadObjectException
            @SuppressLint("RestrictedApi") androidx.car.app.activity.ServiceDispatcher.ReturnCall<String> deadReturn =
                    new androidx.car.app.activity.ServiceDispatcher.ReturnCall<String>() {
                        @SuppressLint("RestrictedApi")
                        @Override
                        public String invoke() throws android.os.DeadObjectException {
                            throw new android.os.DeadObjectException();
                        }
                    };
            Object fetchDeadRes = fetchM.invoke(dispatcher, "fetch-dead", "fallback-dead", deadReturn);
            android.util.Log.i("exerciseServiceDispatcher", "fetch-dead -> " + fetchDeadRes);

            // fetch() throwing BundlerException
            @SuppressLint("RestrictedApi") androidx.car.app.activity.ServiceDispatcher.ReturnCall<String> bundlerThrowReturn =
                    new androidx.car.app.activity.ServiceDispatcher.ReturnCall<String>() {
                        @SuppressLint("RestrictedApi")
                        @Override
                        public String invoke() throws androidx.car.app.serialization.BundlerException {
                            throw new androidx.car.app.serialization.BundlerException("simulated bundler failure");
                        }
                    };
            Object fetchBundlerRes = fetchM.invoke(dispatcher, "fetch-bundler", "fallback-bundler", bundlerThrowReturn);
            android.util.Log.i("exerciseServiceDispatcher", "fetch-bundler -> " + fetchBundlerRes);

            // fetch() throwing RuntimeException
            @SuppressLint("RestrictedApi") androidx.car.app.activity.ServiceDispatcher.ReturnCall<String> runtimeThrowReturn =
                    new androidx.car.app.activity.ServiceDispatcher.ReturnCall<String>() {
                        @SuppressLint("RestrictedApi")
                        @Override
                        public String invoke() {
                            throw new RuntimeException("simulated runtime");
                        }
                    };
            Object fetchRuntimeRes = fetchM.invoke(dispatcher, "fetch-runtime", "fallback-runtime", runtimeThrowReturn);
            android.util.Log.i("exerciseServiceDispatcher", "fetch-runtime -> " + fetchRuntimeRes);

            // fetchNoFail()
            Object fetchNoFailRes = fetchNoFailM.invoke(dispatcher, "fetchNoFail-bundler", "fallback-no-fail", bundlerThrowReturn);
            android.util.Log.i("exerciseServiceDispatcher", "fetchNoFail-bundler -> " + fetchNoFailRes);

            // Switch to isBound() == false
            @SuppressLint("RestrictedApi") androidx.car.app.activity.ServiceDispatcher.OnBindingListener onBindFalse =
                    new androidx.car.app.activity.ServiceDispatcher.OnBindingListener() {
                        @SuppressLint("RestrictedApi")
                        @Override
                        public boolean isBound() {
                            return false;
                        }
                    };
            setOnBindingListenerM.invoke(dispatcher, onBindFalse);

            dispatchM.invoke(dispatcher, "oneway-skipped", normalOneWay);
            Object fetchSkipped = fetchM.invoke(dispatcher, "fetch-skipped", "fallback-skipped", successReturn);
            android.util.Log.i("exerciseServiceDispatcher", "fetch-skipped (should be fallback) -> " + fetchSkipped);

            android.util.Log.i("exerciseServiceDispatcher", "exerciseServiceDispatcher finished.");
        } catch (Throwable t) {
            android.util.Log.e("exerciseServiceDispatcher", "Exception while exercising ServiceDispatcher", t);
        }
    }

    @SuppressLint({"RestrictedApi"})
    public void ExerciseSurfaceHolderListener() {
        final String TAG = "EXERCISE_SurfaceHolderListener";
        try {
            // Obtain Application context reflectively
            android.app.Application app =
                    (android.app.Application) Class.forName("android.app.ActivityThread")
                            .getMethod("currentApplication")
                            .invoke(null);

            if (app == null) {
                android.util.Log.w(TAG, "No Application instance available; aborting exercise.");
                return;
            }

            @SuppressLint("RestrictedApi") androidx.car.app.activity.renderer.surface.TemplateSurfaceView templateView =
                    new androidx.car.app.activity.renderer.surface.TemplateSurfaceView(app, null);

            @SuppressLint("RestrictedApi") androidx.car.app.activity.renderer.surface.SurfaceWrapperProvider surfaceWrapperProvider =
                    new androidx.car.app.activity.renderer.surface.SurfaceWrapperProvider(templateView);

            @SuppressLint("RestrictedApi") androidx.car.app.activity.ErrorHandler errorHandler = new androidx.car.app.activity.ErrorHandler() {
                @SuppressLint("RestrictedApi")
                @Override
                public void onError(@SuppressLint("RestrictedApi") @NonNull ErrorHandler.ErrorType errorType) {
                    android.util.Log.e(TAG, "ErrorHandler.onError: " + errorType);
                }
            };

            @SuppressLint("RestrictedApi") androidx.car.app.activity.ServiceDispatcher.OnBindingListener onBindingListener =
                    new androidx.car.app.activity.ServiceDispatcher.OnBindingListener() {
                        @SuppressLint("RestrictedApi")
                        @Override
                        public boolean isBound() {
                            // pretend bound so fetch/dispatch will attempt calls
                            return true;
                        }
                    };

            @SuppressLint("RestrictedApi") androidx.car.app.activity.ServiceDispatcher serviceDispatcher =
                    new androidx.car.app.activity.ServiceDispatcher(errorHandler, onBindingListener);

            @SuppressLint("RestrictedApi") androidx.car.app.activity.renderer.surface.SurfaceHolderListener listener =
                    new androidx.car.app.activity.renderer.surface.SurfaceHolderListener(
                            serviceDispatcher, surfaceWrapperProvider);

            // Minimal stub SurfaceHolder implementation
            android.view.SurfaceHolder dummyHolder = new android.view.SurfaceHolder() {
                private android.view.SurfaceHolder.Callback storedCallback;

                @Override public void addCallback(Callback callback) { storedCallback = callback; }
                @Override public void removeCallback(Callback callback) { if (storedCallback == callback) storedCallback = null; }
                @Override public boolean isCreating() { return false; }
                @Override public void setType(int i) { /* no-op */ }
                @Override public void setFixedSize(int i, int i1) { /* no-op */ }
                @Override public void setSizeFromLayout() { /* no-op */ }
                @Override public void setFormat(int i) { /* no-op */ }

                @Override
                public void setKeepScreenOn(boolean b) {

                }

                @Override public android.graphics.Rect getSurfaceFrame() { return new android.graphics.Rect(0,0,100,100); }
                @Override public android.view.Surface getSurface() { return null; }
                @Override public android.graphics.Canvas lockCanvas() { return null; }
                @Override public android.graphics.Canvas lockCanvas(android.graphics.Rect rect) { return null; }
                @Override public void unlockCanvasAndPost(android.graphics.Canvas canvas) { /* no-op */ }
                @Override public android.graphics.Canvas lockHardwareCanvas() { return null; }
            };

            // lifecycle callbacks
            try {
                listener.surfaceCreated(dummyHolder);
                android.util.Log.d(TAG, "surfaceCreated invoked successfully");
            } catch (Throwable t) {
                android.util.Log.e(TAG, "surfaceCreated threw", t);
            }

            try {
                listener.surfaceChanged(dummyHolder, PixelFormat.RGBA_8888, 320, 240);
                android.util.Log.d(TAG, "surfaceChanged invoked successfully");
            } catch (Throwable t) {
                android.util.Log.e(TAG, "surfaceChanged threw", t);
            }

            try {
                listener.surfaceDestroyed(dummyHolder);
                android.util.Log.d(TAG, "surfaceDestroyed invoked successfully");
            } catch (Throwable t) {
                android.util.Log.e(TAG, "surfaceDestroyed threw", t);
            }

            // mock ISurfaceListener
            androidx.car.app.activity.renderer.surface.ISurfaceListener clientSurfaceListener =
                    new androidx.car.app.activity.renderer.surface.ISurfaceListener() {
                        @Override
                        public IBinder asBinder() {
                            return null;
                        }

                        @Override
                        public void onSurfaceAvailable(@NonNull androidx.car.app.serialization.Bundleable wrapper) {
                            android.util.Log.d(TAG, "ISurfaceListener.onSurfaceAvailable -> " + (wrapper == null ? "null" : wrapper.getClass()));
                        }

                        @Override
                        public void onSurfaceChanged(@NonNull androidx.car.app.serialization.Bundleable wrapper) {
                            android.util.Log.d(TAG, "ISurfaceListener.onSurfaceChanged -> " + (wrapper == null ? "null" : wrapper.getClass()));
                        }

                        @Override
                        public void onSurfaceDestroyed(@NonNull androidx.car.app.serialization.Bundleable wrapper) {
                            android.util.Log.d(TAG, "ISurfaceListener.onSurfaceDestroyed -> " + (wrapper == null ? "null" : wrapper.getClass()));
                        }
                    };

            try {
                listener.setSurfaceListener(clientSurfaceListener);
                android.util.Log.d(TAG, "setSurfaceListener(non-null) succeeded");
            } catch (Throwable t) {
                android.util.Log.e(TAG, "setSurfaceListener(non-null) threw", t);
            }

            try {
                listener.setSurfaceListener(null);
                android.util.Log.d(TAG, "setSurfaceListener(null) succeeded");
            } catch (Throwable t) {
                android.util.Log.e(TAG, "setSurfaceListener(null) threw", t);
            }

            try {
                listener.surfaceCreated(dummyHolder);
                listener.setSurfaceListener(clientSurfaceListener);
                android.util.Log.d(TAG, "surfaceCreated + setSurfaceListener triggered onSurfaceAvailable path");
            } catch (Throwable t) {
                android.util.Log.e(TAG, "re-triggering created/listener threw", t);
            }

            try {
                java.lang.reflect.Method mCreated =
                        androidx.car.app.activity.renderer.surface.SurfaceHolderListener.class
                                .getDeclaredMethod("notifySurfaceCreated");
                mCreated.setAccessible(true);
                mCreated.invoke(listener);
                android.util.Log.d(TAG, "notifySurfaceCreated (reflective) invoked");
            } catch (Throwable t) {
                android.util.Log.w(TAG, "notifySurfaceCreated reflection failed", t);
            }

            try {
                java.lang.reflect.Method mChanged =
                        androidx.car.app.activity.renderer.surface.SurfaceHolderListener.class
                                .getDeclaredMethod("notifySurfaceChanged");
                mChanged.setAccessible(true);
                mChanged.invoke(listener);
                android.util.Log.d(TAG, "notifySurfaceChanged (reflective) invoked");
            } catch (Throwable t) {
                android.util.Log.w(TAG, "notifySurfaceChanged reflection failed", t);
            }

            try {
                java.lang.reflect.Method mDestroyed =
                        androidx.car.app.activity.renderer.surface.SurfaceHolderListener.class
                                .getDeclaredMethod("notifySurfaceDestroyed");
                mDestroyed.setAccessible(true);
                mDestroyed.invoke(listener);
                android.util.Log.d(TAG, "notifySurfaceDestroyed (reflective) invoked");
            } catch (Throwable t) {
                android.util.Log.w(TAG, "notifySurfaceDestroyed reflection failed", t);
            }

            android.util.Log.i(TAG, "Aggressive exercise completed.");
        } catch (Throwable outer) {
            android.util.Log.e("EXERCISE_SurfaceHolderListener", "Unexpected failure exercising SurfaceHolderListener", outer);
        }
    }

    public void exerciseCabinTemperatureProfile() {
        final String TAG = "CabinTempProfileReflex";
        try {
            Class<?> cpClass = Class.forName(
                    "androidx.car.app.hardware.climate.CabinTemperatureProfile");
            Class<?> builderClass = Class.forName(
                    "androidx.car.app.hardware.climate.CabinTemperatureProfile$Builder");
            Class<?> carZoneClass = Class.forName(
                    "androidx.car.app.hardware.common.CarZone");

            android.util.Pair<Float, Float> celsiusRange =
                    new android.util.Pair<>(18.0f, 30.0f);
            android.util.Pair<Float, Float> fahrenheitRange =
                    new android.util.Pair<>(64.4f, 86.0f);

            java.util.HashSet<Object> zoneSet = new java.util.HashSet<>();
            Object carZoneGlobal = carZoneClass.getField("CAR_ZONE_GLOBAL").get(null);
            zoneSet.add(carZoneGlobal);
            java.util.Map<java.util.Set<Object>, android.util.Pair<Float, Float>> zoneMap =
                    new java.util.HashMap<>();
            zoneMap.put(zoneSet, celsiusRange);

            // Build a populated profile via direct API
            Object populatedBuilder =
                    builderClass.getDeclaredConstructor().newInstance();
            try {
                // use reflection to chain builder setters
                builderClass.getMethod("setSupportedMinMaxCelsiusRange", android.util.Pair.class)
                        .invoke(populatedBuilder, celsiusRange);
                builderClass.getMethod("setSupportedMinMaxFahrenheitRange", android.util.Pair.class)
                        .invoke(populatedBuilder, fahrenheitRange);
                builderClass.getMethod("setCarZoneSetsToCabinCelsiusTemperatureRanges",
                        java.util.Map.class).invoke(populatedBuilder, zoneMap);
                builderClass.getMethod("setCelsiusSupportedIncrement", float.class)
                        .invoke(populatedBuilder, 0.5f);
                builderClass.getMethod("setFahrenheitSupportedIncrement", float.class)
                        .invoke(populatedBuilder, 1.0f);
            } catch (NoSuchMethodException nsme) {
                Log.w(TAG, "Some Builder methods not found by reflection: " + nsme);
            }
            Object populatedProfile =
                    builderClass.getMethod("build").invoke(populatedBuilder);

            // Build a default profile
            Object defaultBuilder = builderClass.getDeclaredConstructor().newInstance();
            Object defaultProfile = builderClass.getMethod("build").invoke(defaultBuilder);

            java.util.function.Consumer<Object> runChecks = (instance) -> {
                try {
                    Log.i(TAG, "=== Running checks on instance: " + instance.getClass().getName()
                            + " @ " + System.identityHashCode(instance));
                    java.lang.reflect.Method[] methods = cpClass.getDeclaredMethods();
                    for (java.lang.reflect.Method m : methods) {
                        m.setAccessible(true);
                        if (m.getParameterCount() == 0) {
                            try {
                                Object result = m.invoke(instance);
                                Log.i(TAG, "Invoked " + m.getName() + " -> " + result);
                            } catch (java.lang.reflect.InvocationTargetException ite) {
                                Throwable cause = ite.getCause();
                                Log.w(TAG, "Invocation of " + m.getName() + " threw: "
                                        + (cause == null ? ite.toString() : cause.toString()));
                            } catch (Exception e) {
                                Log.w(TAG, "Failed to invoke " + m.getName() + ": " + e);
                            }
                        } else {
                            // log method signature for awareness; aggressive: try small known patterns
                            Log.d(TAG, "Skipping non-zero-arg method: " + m);
                        }
                    }

                    try {
                        java.lang.reflect.Method toStr = cpClass.getMethod("toString");
                        Object s = toStr.invoke(instance);
                        Log.i(TAG, "toString(): " + s);
                    } catch (Exception ignored) {}
                    try {
                        java.lang.reflect.Method hash = cpClass.getMethod("hashCode");
                        Object h = hash.invoke(instance);
                        Log.i(TAG, "hashCode(): " + h);
                    } catch (Exception ignored) {}
                } catch (Exception e) {
                    Log.e(TAG, "runChecks generic failure", e);
                }
            };

            runChecks.accept(populatedProfile);
            runChecks.accept(defaultProfile);

            try {
                java.lang.reflect.Constructor<?> ctor = cpClass.getDeclaredConstructor(builderClass);
                ctor.setAccessible(true);
                Object profileFromCtor = ctor.newInstance(populatedBuilder);
                Log.i(TAG, "Constructed via package-private ctor: " + profileFromCtor);
                runChecks.accept(profileFromCtor);
            } catch (NoSuchMethodException nsme) {
                Log.w(TAG, "Package-private ctor(builder) not found: " + nsme);
            } catch (Exception e) {
                Log.w(TAG, "Failed to call package-private ctor(builder): " + e);
            }

            // Inspect static fields
            java.lang.reflect.Field[] fields = cpClass.getDeclaredFields();
            for (java.lang.reflect.Field f : fields) {
                try {
                    f.setAccessible(true);
                    if (java.lang.reflect.Modifier.isStatic(f.getModifiers())) {
                        Object val = f.get(null);
                        Log.i(TAG, "Static field: " + f.getName() + " = " + String.valueOf(val));
                    }
                } catch (Exception e) {
                    Log.d(TAG, "Could not read static field " + f.getName() + " : " + e);
                }
            }

            // exercise Builder methods
            try {
                Object chainBuilder = builderClass.getDeclaredConstructor().newInstance();
                java.lang.reflect.Method m1 = builderClass.getMethod("setSupportedMinMaxCelsiusRange",
                        android.util.Pair.class);
                java.lang.reflect.Method m2 = builderClass.getMethod("setCelsiusSupportedIncrement",
                        float.class);
                java.lang.reflect.Method buildMethod = builderClass.getMethod("build");

                // chain by invoking and reusing returned Builder (these setters return Builder)
                Object ret1 = m1.invoke(chainBuilder, celsiusRange);
                Log.i(TAG, "Builder.setSupportedMinMaxCelsiusRange returned: " + ret1);
                Object ret2 = m2.invoke(chainBuilder, 0.5f);
                Log.i(TAG, "Builder.setCelsiusSupportedIncrement returned: " + ret2);

                Object built = buildMethod.invoke(chainBuilder);
                Log.i(TAG, "Built profile via reflection build(): " + built);
                runChecks.accept(built);
            } catch (NoSuchMethodException nsm) {
                Log.w(TAG, "Builder methods not found for chaining: " + nsm);
            } catch (Exception e) {
                Log.w(TAG, "Problem exercising Builder via reflection: " + e);
            }

            try {
                java.lang.reflect.Method getter = cpClass.getMethod("getSupportedMinMaxCelsiusRange");
                Object res = getter.invoke(defaultProfile);
                Log.i(TAG, "DEFAULT profile celsius range (unexpectedly present): " + res);
            } catch (java.lang.reflect.InvocationTargetException ite) {
                Log.i(TAG, "Expected exception when calling getter on defaultProfile: "
                        + ite.getCause());
            } catch (Exception e) {
                Log.w(TAG, "Unexpected error when calling getter on defaultProfile: " + e);
            }

            Log.i(TAG, "CabinTemperatureProfile reflection exercise completed.");
        } catch (Exception e) {
            Log.e("CabinTempProfileReflex", "Unexpected top-level error", e);
        }
    }


    @SuppressLint("RestrictedApi")
    @OptIn(markerClass = ExperimentalCarApi.class)
    public void exerciseCarPropertyProfileReflection() {
        final String TAG = "ReflectionTest";
        try {
            // Obtain the static builder()
            java.lang.reflect.Method builderMethod =
                    androidx.car.app.hardware.common.CarPropertyProfile.class.getMethod("builder");
            Object builder = builderMethod.invoke(null);

            //setPropertyId(int)
            java.lang.reflect.Method setPropertyId =
                    builder.getClass().getMethod("setPropertyId", int.class);
            setPropertyId.invoke(builder, 12345);

            //setStatus(int)
            java.lang.reflect.Method setStatus =
                    builder.getClass().getMethod("setStatus", int.class);
            setStatus.invoke(builder, 0);

            //setCarZones(List<Set<CarZone>>)
            java.util.Set<androidx.car.app.hardware.common.CarZone> carZoneSet =
                    new java.util.HashSet<>();
            carZoneSet.add(androidx.car.app.hardware.common.CarZone.CAR_ZONE_GLOBAL);
            java.util.List<java.util.Set<androidx.car.app.hardware.common.CarZone>> carZones =
                    new java.util.ArrayList<>();
            carZones.add(carZoneSet);
            java.lang.reflect.Method setCarZones =
                    builder.getClass().getMethod("setCarZones", java.util.List.class);
            setCarZones.invoke(builder, carZones);

            //setCelsiusRange(Pair<Float,Float>)
            android.util.Pair<Float, Float> celsiusRange = new android.util.Pair<>(16f, 28f);
            java.lang.reflect.Method setCelsiusRange =
                    builder.getClass().getMethod("setCelsiusRange", android.util.Pair.class);
            setCelsiusRange.invoke(builder, celsiusRange);

            //setFahrenheitRange(Pair<Float,Float>)
            android.util.Pair<Float, Float> fahrRange = new android.util.Pair<>(60f, 82f);
            java.lang.reflect.Method setFahrenheitRange =
                    builder.getClass().getMethod("setFahrenheitRange", android.util.Pair.class);
            setFahrenheitRange.invoke(builder, fahrRange);

            //setCelsiusIncrement(float)
            java.lang.reflect.Method setCelsiusIncrement =
                    builder.getClass().getMethod("setCelsiusIncrement", float.class);
            setCelsiusIncrement.invoke(builder, 0.5f);

            //setFahrenheitIncrement(float)
            java.lang.reflect.Method setFahrenheitIncrement =
                    builder.getClass().getMethod("setFahrenheitIncrement", float.class);
            setFahrenheitIncrement.invoke(builder, 1.0f);

            //setCarZoneSetsToMinMaxRange(Map<Set<CarZone>, Pair<T,T>>)
            java.util.Map<java.util.Set<androidx.car.app.hardware.common.CarZone>,
                    android.util.Pair<Float, Float>> carZoneToRange = new java.util.HashMap<>();
            carZoneToRange.put(carZoneSet, celsiusRange);
            java.lang.reflect.Method setCarZoneSetsToMinMaxRange =
                    builder.getClass().getMethod("setCarZoneSetsToMinMaxRange", java.util.Map.class);
            setCarZoneSetsToMinMaxRange.invoke(builder, carZoneToRange);

            //setHvacFanDirection(Map<Set<CarZone>, Set<Integer>>)
            java.util.Map<java.util.Set<androidx.car.app.hardware.common.CarZone>,
                    java.util.Set<Integer>> hvacMap = new java.util.HashMap<>();
            java.util.Set<Integer> hvacDirections = new java.util.HashSet<>();
            hvacDirections.add(androidx.car.app.hardware.common.CarPropertyProfile.FACE);
            hvacMap.put(carZoneSet, hvacDirections);
            java.lang.reflect.Method setHvac =
                    builder.getClass().getMethod("setHvacFanDirection", java.util.Map.class);
            setHvac.invoke(builder, hvacMap);

            // Build the CarPropertyProfile instance via Builder.build()
            java.lang.reflect.Method buildMethod = builder.getClass().getMethod("build");
            Object profile = buildMethod.invoke(builder);


            // getPropertyId()
            java.lang.reflect.Method getPropertyId =
                    profile.getClass().getMethod("getPropertyId");
            int gotPropertyId = (Integer) getPropertyId.invoke(profile);
            android.util.Log.i(TAG, "getPropertyId() -> " + gotPropertyId);

            // getHvacFanDirection()
            java.lang.reflect.Method getHvacFanDirection =
                    profile.getClass().getMethod("getHvacFanDirection");
            Object gotHvac = getHvacFanDirection.invoke(profile);
            android.util.Log.i(TAG, "getHvacFanDirection() -> " + String.valueOf(gotHvac));

            // getCarZoneSetsToMinMaxRange()
            java.lang.reflect.Method getCarZoneSetsToMinMaxRange =
                    profile.getClass().getMethod("getCarZoneSetsToMinMaxRange");
            Object gotMap = getCarZoneSetsToMinMaxRange.invoke(profile);
            android.util.Log.i(TAG, "getCarZoneSetsToMinMaxRange() -> " + String.valueOf(gotMap));

            // getCelsiusRange()
            java.lang.reflect.Method getCelsiusRange =
                    profile.getClass().getMethod("getCelsiusRange");
            Object gotCelsiusRange = getCelsiusRange.invoke(profile);
            android.util.Log.i(TAG, "getCelsiusRange() -> " + String.valueOf(gotCelsiusRange));

            // getFahrenheitRange()
            java.lang.reflect.Method getFahrenheitRange =
                    profile.getClass().getMethod("getFahrenheitRange");
            Object gotFahrRange = getFahrenheitRange.invoke(profile);
            android.util.Log.i(TAG, "getFahrenheitRange() -> " + String.valueOf(gotFahrRange));

            // getCelsiusIncrement()
            java.lang.reflect.Method getCelsiusIncrement =
                    profile.getClass().getMethod("getCelsiusIncrement");
            float gotCelsiusInc = (Float) getCelsiusIncrement.invoke(profile);
            android.util.Log.i(TAG, "getCelsiusIncrement() -> " + gotCelsiusInc);

            // getFahrenheitIncrement()
            java.lang.reflect.Method getFahrenheitIncrement =
                    profile.getClass().getMethod("getFahrenheitIncrement");
            float gotFahrInc = (Float) getFahrenheitIncrement.invoke(profile);
            android.util.Log.i(TAG, "getFahrenheitIncrement() -> " + gotFahrInc);

            // getCarZones()
            java.lang.reflect.Method getCarZonesMethod =
                    profile.getClass().getMethod("getCarZones");
            Object gotCarZones = getCarZonesMethod.invoke(profile);
            android.util.Log.i(TAG, "getCarZones() -> " + String.valueOf(gotCarZones));

            // getStatus()
            java.lang.reflect.Method getStatus =
                    profile.getClass().getMethod("getStatus");
            int gotStatus = (Integer) getStatus.invoke(profile);
            android.util.Log.i(TAG, "getStatus() -> " + gotStatus);

            android.util.Log.i(TAG, "CarPropertyProfile reflection exercise completed successfully.");
        } catch (NoSuchMethodException nsme) {
            android.util.Log.e("ReflectionTest", "Expected method not found", nsme);
        } catch (IllegalArgumentException iae) {
            android.util.Log.e("ReflectionTest", "Bad argument", iae);
        } catch (java.lang.reflect.InvocationTargetException ite) {
            android.util.Log.e("ReflectionTest", "InvocationTargetException", ite.getCause());
        } catch (Exception e) {
            android.util.Log.e("ReflectionTest", "Unexpected error exercising CarPropertyProfile", e);
        }
    }

    public void exercisePropertyUtilsReflection() {
        final String TAG = "PropertyUtilsExercise";
        try {
            // Load the class under test.
            Class<?> utilsCls = Class.forName("androidx.car.app.hardware.common.PropertyUtils");

            // convertSpeedUnit(int vehicleUnit)
            try {
                java.lang.reflect.Method mConvertSpeed =
                        utilsCls.getMethod("convertSpeedUnit", int.class);
                // VEHICLE_UNIT_MILES_PER_HOUR used in source as 0x90 -> pass 0x90
                int convertedSpeed = (Integer) mConvertSpeed.invoke(null, 0x90);
                android.util.Log.i(TAG, "convertSpeedUnit(0x90) => " + convertedSpeed);
            } catch (Throwable t) {
                android.util.Log.e(TAG, "convertSpeedUnit failed", t);
            }

            // convertDistanceUnit(int vehicleUnit)
            try {
                java.lang.reflect.Method mConvertDistance =
                        utilsCls.getMethod("convertDistanceUnit", int.class);
                // VEHICLE_UNIT_METER (0x21) from source -> pass 0x21
                int convertedDistance = (Integer) mConvertDistance.invoke(null, 0x21);
                android.util.Log.i(TAG, "convertDistanceUnit(0x21) => " + convertedDistance);
            } catch (Throwable t) {
                android.util.Log.e(TAG, "convertDistanceUnit failed", t);
            }

            // convertVolumeUnit(int vehicleUnit)
            try {
                java.lang.reflect.Method mConvertVolume =
                        utilsCls.getMethod("convertVolumeUnit", int.class);
                // VEHICLE_UNIT_VOLUME_LITER used in source as 0x41
                int convertedVolume = (Integer) mConvertVolume.invoke(null, 0x41);
                android.util.Log.i(TAG, "convertVolumeUnit(0x41) => " + convertedVolume);
            } catch (Throwable t) {
                android.util.Log.e(TAG, "convertVolumeUnit failed", t);
            }

            // convertEvConnectorType(int vehicleEvConnectorType)
            try {
                java.lang.reflect.Method mConvertEv =
                        utilsCls.getMethod("convertEvConnectorType", int.class);
                int evType = (Integer) mConvertEv.invoke(null, 2);
                android.util.Log.i(TAG, "convertEvConnectorType(2) => " + evType);
            } catch (Throwable t) {
                android.util.Log.e(TAG, "convertEvConnectorType failed", t);
            }

            // convertPropertyValueToPropertyResponse(CarPropertyValue<?>)
            try {
                Class<?> carPropValCls = Class.forName("android.car.hardware.CarPropertyValue");
                java.lang.reflect.Method mConvertProp =
                        utilsCls.getMethod("convertPropertyValueToPropertyResponse", carPropValCls);

                Object carPropValInstance = null;
                try {
                    java.lang.reflect.Constructor<?>[] ctors = carPropValCls.getConstructors();
                    boolean made = false;
                    for (java.lang.reflect.Constructor<?> ctor : ctors) {
                        Class<?>[] params = ctor.getParameterTypes();
                        try {
                            if (params.length == 5 &&
                                    params[0] == int.class &&
                                    params[1] == int.class &&
                                    params[2] == long.class &&
                                    params[3] == int.class) {
                                int propertyId = 1;
                                int areaId = 0; // fallback to 0 (global)
                                try {
                                    Class<?> vehicleAreaType = Class.forName("android.car.VehicleAreaType");
                                    areaId = vehicleAreaType.getField("VEHICLE_AREA_TYPE_GLOBAL").getInt(null);
                                } catch (Throwable ignored) { /* fallback 0 */ }
                                long timestamp = java.lang.System.nanoTime();
                                int status = 0;
                                try {
                                    status = carPropValCls.getField("STATUS_AVAILABLE").getInt(null);
                                } catch (Throwable ignored) { /* fallback 0 */ }
                                Object value = Float.valueOf(21.5f);
                                carPropValInstance = ctor.newInstance(propertyId, areaId, timestamp, status, value);
                                made = true;
                                break;
                            } else if (params.length == 4 &&
                                    params[0] == int.class &&
                                    params[1] == int.class &&
                                    params[2] == long.class) {
                                // (propertyId, areaId, timestamp, value)
                                int propertyId = 1;
                                int areaId = 0;
                                try {
                                    Class<?> vehicleAreaType = Class.forName("android.car.VehicleAreaType");
                                    areaId = vehicleAreaType.getField("VEHICLE_AREA_TYPE_GLOBAL").getInt(null);
                                } catch (Throwable ignored) { /* fallback 0 */ }
                                long timestamp = java.lang.System.nanoTime();
                                Object value = Float.valueOf(19.0f);
                                carPropValInstance = ctor.newInstance(propertyId, areaId, timestamp, value);
                                made = true;
                                break;
                            }
                        } catch (Throwable ctorEx) {
                        }
                    }
                    if (!made) {
                        android.util.Log.w(TAG,
                                "No matching CarPropertyValue constructor found or instantiation failed; will try null placeholder.");
                    }
                } catch (Throwable createEx) {
                    android.util.Log.w(TAG, "CarPropertyValue construction attempt failed", createEx);
                }

                try {
                    Object response = mConvertProp.invoke(null, carPropValInstance);
                    android.util.Log.i(TAG, "convertPropertyValueToPropertyResponse => " + response);
                } catch (Throwable tInvoke) {
                    android.util.Log.w(TAG,
                            "convertPropertyValueToPropertyResponse invocation failed (CarPropertyValue may be unavailable):", tInvoke);
                }
            } catch (Throwable t) {
                android.util.Log.e(TAG, "convertPropertyValueToPropertyResponse reflection setup failed", t);
            }

            // getReadPermissionsByPropertyIds(List<Integer>)
            try {
                java.lang.reflect.Method mGetReadPerms =
                        utilsCls.getDeclaredMethod("getReadPermissionsByPropertyIds", java.util.List.class);
                mGetReadPerms.setAccessible(true);
                // craft parameter: empty list -> safe path (no SecurityException)
                java.util.List<Integer> emptyList = java.util.Collections.emptyList();
                emptyList.add(287310851);
                //emptyList.add(287311364);
                //emptyList.add(289408513);
                java.util.Set<String> readPerms = (java.util.Set<String>) mGetReadPerms.invoke(null, emptyList);
                android.util.Log.i(TAG, "getReadPermissionsByPropertyIds(empty) => " + readPerms);
            } catch (Throwable t) {
                android.util.Log.e(TAG, "getReadPermissionsByPropertyIds failed", t);
            }

            // getWritePermissions(List<Pair<Integer,Integer>>)
            try {
                java.lang.reflect.Method mGetWritePerms =
                        utilsCls.getDeclaredMethod("getWritePermissions", java.util.List.class);
                mGetWritePerms.setAccessible(true);
                // craft parameter: empty list -> safe path
                java.util.List<?> emptyPairs = java.util.Collections.emptyList();
                java.util.Set<String> writePerms = (java.util.Set<String>) mGetWritePerms.invoke(null, emptyPairs);
                android.util.Log.i(TAG, "getWritePermissions(empty) => " + writePerms);
            } catch (Throwable t) {
                android.util.Log.e(TAG, "getWritePermissions failed", t);
            }

            // isGlobalProperty(int propertyId)
            try {
                java.lang.reflect.Method mIsGlobal = utilsCls.getDeclaredMethod("isGlobalProperty", int.class);
                mIsGlobal.setAccessible(true);
                // pass VEHICLE_AREA_GLOBAL (0x01000000) to match mask check in source
                boolean isGlobal = (Boolean) mIsGlobal.invoke(null, 0x01000000);
                android.util.Log.i(TAG, "isGlobalProperty(0x01000000) => " + isGlobal);
            } catch (Throwable t) {
                android.util.Log.e(TAG, "isGlobalProperty failed", t);
            }

            // isOnChangeProperty(int propertyId)
            try {
                java.lang.reflect.Method mIsOnChange = utilsCls.getDeclaredMethod("isOnChangeProperty", int.class);
                mIsOnChange.setAccessible(true);
                // choose a likely-non-onchange id (e.g., 99999) to get false
                boolean onChange = (Boolean) mIsOnChange.invoke(null, 99999);
                android.util.Log.i(TAG, "isOnChangeProperty(99999) => " + onChange);
            } catch (Throwable t) {
                android.util.Log.e(TAG, "isOnChangeProperty failed", t);
            }

            // mapToStatusCodeInCarValue(int carPropertyStatus)
            try {
                java.lang.reflect.Method mMapStatus = utilsCls.getDeclaredMethod("mapToStatusCodeInCarValue", int.class);
                mMapStatus.setAccessible(true);
                int statusAvailable = -12345;
                try {
                    Class<?> carPropValCls = Class.forName("android.car.hardware.CarPropertyValue");
                    statusAvailable = carPropValCls.getField("STATUS_AVAILABLE").getInt(null);
                } catch (Throwable t) {
                    // Fallback to 0 which is likely STATUS_AVAILABLE in many Android versions
                    statusAvailable = 0;
                }
                int mappedCode = (Integer) mMapStatus.invoke(null, statusAvailable);
                android.util.Log.i(TAG, "mapToStatusCodeInCarValue(statusAvailable=" + statusAvailable + ") => " + mappedCode);
            } catch (Throwable t) {
                android.util.Log.e(TAG, "mapToStatusCodeInCarValue failed", t);
            }

            // getPropertyIdWithAreaIds(Map<Integer, List<CarZone>> propertyIdToCarZones)
            try {
                java.lang.reflect.Method mGetPropWithArea =
                        utilsCls.getDeclaredMethod("getPropertyIdWithAreaIds", java.util.Map.class);
                mGetPropWithArea.setAccessible(true);

                // Build CarZone.ALL via CarZone.Builder reflection:
                Class<?> carZoneCls = Class.forName("androidx.car.app.hardware.common.CarZone");
                Class<?> carZoneBuilderCls = Class.forName("androidx.car.app.hardware.common.CarZone$Builder");
                Object builder = carZoneBuilderCls.getConstructor().newInstance();

                // fetch constants CAR_ZONE_ROW_ALL and CAR_ZONE_COLUMN_ALL
                int rowAll = (Integer) carZoneCls.getField("CAR_ZONE_ROW_ALL").getInt(null);
                int colAll = (Integer) carZoneCls.getField("CAR_ZONE_COLUMN_ALL").getInt(null);
                // builder.setRow(rowAll).setColumn(colAll)
                java.lang.reflect.Method setRow = carZoneBuilderCls.getMethod("setRow", int.class);
                java.lang.reflect.Method setCol = carZoneBuilderCls.getMethod("setColumn", int.class);
                java.lang.reflect.Method build = carZoneBuilderCls.getMethod("build");
                setRow.invoke(builder, rowAll);
                setCol.invoke(builder, colAll);
                Object carZoneAll = build.invoke(builder);

                java.util.List<Object> zonesList = new java.util.ArrayList<>();
                zonesList.add(carZoneAll);
                java.util.Map<Integer, java.util.List<Object>> propMap = new java.util.HashMap<>();
                propMap.put(1, zonesList); // propertyId=1 (arbitrary valid id)

                java.util.List<?> uids = (java.util.List<?>) mGetPropWithArea.invoke(null, propMap);
                android.util.Log.i(TAG, "getPropertyIdWithAreaIds(...) => size: " + (uids == null ? "null" : uids.size()) + " entries: " + uids);
            } catch (Throwable t) {
                android.util.Log.w(TAG, "getPropertyIdWithAreaIds failed or host classes unavailable", t);
            }

            // getMinMaxProfileIntegerMap(Map<Set<CarZone>, ? extends Pair<?, ?>>)
            try {
                java.lang.reflect.Method mMinMaxInt =
                        utilsCls.getMethod("getMinMaxProfileIntegerMap", java.util.Map.class);
                // create a map: key = Set<CarZone> with CAR_ZONE_ALL, value = Pair<Integer,Integer>(10, 20)
                Class<?> carZoneCls = Class.forName("androidx.car.app.hardware.common.CarZone");
                java.util.HashSet<Object> keySet = new java.util.HashSet<>();
                // reuse CAR_ZONE_ALL building
                Class<?> carZoneBuilderCls = Class.forName("androidx.car.app.hardware.common.CarZone$Builder");
                Object builder = carZoneBuilderCls.getConstructor().newInstance();
                int rowAll = (Integer) carZoneCls.getField("CAR_ZONE_ROW_ALL").getInt(null);
                int colAll = (Integer) carZoneCls.getField("CAR_ZONE_COLUMN_ALL").getInt(null);
                java.lang.reflect.Method setRow = carZoneBuilderCls.getMethod("setRow", int.class);
                java.lang.reflect.Method setCol = carZoneBuilderCls.getMethod("setColumn", int.class);
                java.lang.reflect.Method build = carZoneBuilderCls.getMethod("build");
                setRow.invoke(builder, rowAll);
                setCol.invoke(builder, colAll);
                Object carZoneAll = build.invoke(builder);
                keySet.add(carZoneAll);
                java.util.Map<java.util.Set<Object>, android.util.Pair<Integer, Integer>> minMaxIntMap = new java.util.HashMap<>();
                minMaxIntMap.put(keySet, new android.util.Pair<>(10, 20));
                java.util.Map resultIntMap = (java.util.Map) mMinMaxInt.invoke(null, minMaxIntMap);
                android.util.Log.i(TAG, "getMinMaxProfileIntegerMap => " + resultIntMap);
            } catch (Throwable t) {
                android.util.Log.e(TAG, "getMinMaxProfileIntegerMap failed", t);
            }

            // 13) getMinMaxProfileFloatMap(Map<Set<CarZone>, ? extends Pair<?, ?>>)
            try {
                java.lang.reflect.Method mMinMaxFloat =
                        utilsCls.getMethod("getMinMaxProfileFloatMap", java.util.Map.class);
                // prepare map: Pair<Float,Float> - note Pair uses raw android.util.Pair
                Class<?> carZoneCls = Class.forName("androidx.car.app.hardware.common.CarZone");
                java.util.HashSet<Object> keySet = new java.util.HashSet<>();
                Class<?> carZoneBuilderCls = Class.forName("androidx.car.app.hardware.common.CarZone$Builder");
                Object builder = carZoneBuilderCls.getConstructor().newInstance();
                int rowAll = (Integer) carZoneCls.getField("CAR_ZONE_ROW_ALL").getInt(null);
                int colAll = (Integer) carZoneCls.getField("CAR_ZONE_COLUMN_ALL").getInt(null);
                java.lang.reflect.Method setRow2 = carZoneBuilderCls.getMethod("setRow", int.class);
                java.lang.reflect.Method setCol2 = carZoneBuilderCls.getMethod("setColumn", int.class);
                java.lang.reflect.Method build2 = carZoneBuilderCls.getMethod("build");
                setRow2.invoke(builder, rowAll);
                setCol2.invoke(builder, colAll);
                Object carZoneAll2 = build2.invoke(builder);
                keySet.add(carZoneAll2);
                java.util.Map<java.util.Set<Object>, android.util.Pair<Float, Float>> minMaxFloatMap = new java.util.HashMap<>();
                minMaxFloatMap.put(keySet, new android.util.Pair<>(15.5f, 25.0f));
                java.util.Map resultFloatMap = (java.util.Map) mMinMaxFloat.invoke(null, minMaxFloatMap);
                android.util.Log.i(TAG, "getMinMaxProfileFloatMap => " + resultFloatMap);
            } catch (Throwable t) {
                android.util.Log.e(TAG, "getMinMaxProfileFloatMap failed", t);
            }

            android.util.Log.i(TAG, "PropertyUtils reflection exercise completed.");
        } catch (Throwable topEx) {
            android.util.Log.e("PropertyUtilsExercise", "Unexpected failure setting up reflection.", topEx);
        }
    }



// -------------------------------------------------------Access system service test---------------------------------------------------------

    public static void checkRunningServices() {
        try {
            Process process = Runtime.getRuntime().exec("dumpsys -l");
            java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(process.getInputStream()));

            String line;
            System.out.println("Running system services:");
            while ((line = reader.readLine()) != null) {
                System.out.println("  - " + line);
            }

        } catch (Exception e) {
            System.out.println("Failed to check running services: " + e.getMessage());
        }
    }

    public static IBinder getAAOSSystemService(String serviceName) {
        try {
            @SuppressLint("PrivateApi") Class<?> serviceManagerClass = Class.forName("android.os.ServiceManager");
            Method getServiceMethod = serviceManagerClass.getMethod("getService", String.class);
            IBinder binder = (IBinder) getServiceMethod.invoke(null, serviceName);
            return binder;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void accessCarService() {
        try {
            IBinder carServiceBinder = getAAOSSystemService("car_service");

            if (carServiceBinder != null) {
                @SuppressLint("PrivateApi") Class<?> carServiceStub = Class.forName("android.car.ICar$Stub");
                Method asInterfaceMethod = carServiceStub.getMethod("asInterface", IBinder.class);
                Object carService = asInterfaceMethod.invoke(null, carServiceBinder);

                System.out.println("Car Service accessed successfully");

                // Try to get available methods
                Method[] methods = carService.getClass().getMethods();
                System.out.println("Available Car Service methods:");
                for (Method method : methods) {
                    if (method.getName().startsWith("get") || method.getName().startsWith("is")) {
                        System.out.println("  - " + method.getName() + "(" +
                                java.util.Arrays.toString(method.getParameterTypes()) + ")");
                    }
                }
                tryCarServiceMethod(carService, "getCarService", new Class[]{String.class}, "power");

            } else {
                System.out.println("Car Service not available");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void tryCarServiceMethod(Object carService, String methodName, Class<?>[] paramTypes, Object... args) {
        try {
            Method method = carService.getClass().getMethod(methodName, paramTypes);
            Object result = method.invoke(carService, args);
            System.out.println("Successfully called " + methodName + " with result: " + result);

            // If we got a BinderProxy, try to use it
            if (result != null && result.getClass().getName().contains("BinderProxy")) {
                exploreBinderProxy(result, methodName);
            }

        } catch (Exception e) {
            System.out.println("Failed to call " + methodName + ": " + e.getMessage());
        }
    }

    private static void exploreBinderProxy(Object binderProxy, String serviceName) {
        try {
            System.out.println("=== Exploring BinderProxy for " + serviceName + " ===");

            // Get the interface descriptor
            if (binderProxy instanceof IBinder) {
                IBinder binder = (IBinder) binderProxy;
                String interfaceDescriptor = binder.getInterfaceDescriptor();
                System.out.println("Interface Descriptor: " + interfaceDescriptor);

                // Try to create a service interface from the binder
                tryCreateServiceInterface(binder, interfaceDescriptor);
            }

            // List all methods available on this proxy
            Method[] methods = binderProxy.getClass().getMethods();
            System.out.println("Available methods on BinderProxy:");
            for (Method method : methods) {
                if (!method.getName().startsWith("java.") &&
                        !method.getName().equals("toString") &&
                        !method.getName().equals("hashCode") &&
                        !method.getName().equals("equals")) {
                    System.out.println("  - " + method.getName() + "(" +
                            java.util.Arrays.toString(method.getParameterTypes()) + ")");
                }
            }

        } catch (Exception e) {
            System.out.println("Error exploring BinderProxy: " + e.getMessage());
        }
    }

    private static void tryCreateServiceInterface(IBinder binder, String interfaceDescriptor) {
        try {
            System.out.println("Trying to create service interface for: " + interfaceDescriptor);

            // Common car service interface patterns
            String[] possibleInterfaces = {
                    "android.car.ICar$Stub",
                    "android.car.media.ICarAudio$Stub",
                    "android.car.hardware.property.ICarProperty$Stub",
                    "android.car.hardware.power.ICarPower$Stub",
                    "android.car.hardware.cabin.ICarCabin$Stub"
            };

            for (String interfaceName : possibleInterfaces) {
                try {
                    Class<?> stubClass = Class.forName(interfaceName);
                    Method asInterfaceMethod = stubClass.getMethod("asInterface", IBinder.class);
                    Object serviceInterface = asInterfaceMethod.invoke(null, binder);

                    if (serviceInterface != null) {
                        System.out.println("Successfully created interface: " + interfaceName);
                        exploreServiceInterface(serviceInterface, interfaceName);
                        return;
                    }
                } catch (Exception e) {
                    // Continue trying other interfaces
                }
            }

        } catch (Exception e) {
            System.out.println("Failed to create service interface: " + e.getMessage());
        }
    }

    /**
     * Explore methods available on a service interface
     */
    private static void exploreServiceInterface(Object serviceInterface, String interfaceName) {
        try {
            System.out.println("=== Methods available on " + interfaceName + " ===");

            Method[] methods = serviceInterface.getClass().getMethods();
            for (Method method : methods) {
                if (!method.getName().startsWith("java.") &&
                        !method.getName().equals("toString") &&
                        !method.getName().equals("hashCode") &&
                        !method.getName().equals("equals") &&
                        !method.getName().equals("asBinder")) {

                    System.out.println("  - " + method.getName() + "(" +
                            java.util.Arrays.toString(method.getParameterTypes()) + ") -> " +
                            method.getReturnType().getSimpleName());
                }
            }

            // Try to call some safe methods
            tryCallSafeMethods(serviceInterface);

        } catch (Exception e) {
            System.out.println("Error exploring service interface: " + e.getMessage());
        }
    }

    /**
     * Try to call some safe methods on the service interface
     */
    private static void tryCallSafeMethods(Object serviceInterface) {
        try {
            System.out.println("=== Trying safe method calls ===");

            // Try common getter methods
            String[] safeMethods = {
                    "getCarConnectionType",
                    "getCarAudioZoneIds",
                    "getAudioZoneIds",
                    "getVolumeGroupCount",
                    "getPropertyList",
                    "getSupportedProperties",
                    "isFeatureEnabled",
                    "getVersion"
            };

            for (String methodName : safeMethods) {
                try {
                    Method method = serviceInterface.getClass().getMethod(methodName);
                    Object result = method.invoke(serviceInterface);
                    System.out.println("  " + methodName + "() -> " + result);
                } catch (Exception e) {
                    // Method doesn't exist or failed, continue
                }
            }

        } catch (Exception e) {
            System.out.println("Error calling safe methods: " + e.getMessage());
        }
    }

    public static void exampleAAOSUsage() {
        System.out.println("=== AAOS System Service Access ===");
        //checkRunningServices();
        accessCarService();
    }


}


/*

Complete dynamic execution with path traversal using DEX - Getting complicated

import android.annotation.SuppressLint;
import android.util.Log;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.car.app.CarContext;
import androidx.car.app.Screen;
import androidx.car.app.annotations.ExperimentalCarApi;
import androidx.car.app.model.Action;
import androidx.car.app.model.Pane;
import androidx.car.app.model.PaneTemplate;
import androidx.car.app.model.Row;
import androidx.car.app.model.Template;

import androidx.car.app.hardware.CarHardwareManager;
import androidx.car.app.hardware.common.PropertyManager;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dalvik.system.BaseDexClassLoader;
import dalvik.system.DexFile;

public class CarDataScreen extends Screen {
    private static final String TAG = "CarDataScreen";
    private final Handler handler = new Handler(Looper.getMainLooper());

    // UI state
    private final List<Row> rows = new ArrayList<>();
    private final Map<String, Integer> rowIndexMap = new HashMap<>();

    // Holds real instances for REAL invocation
    private final Map<Class<?>, Object> instanceMap = new HashMap<>();

    // Cache of discovered hardware classes
    private List<String> cachedHardwareClasses;

    public CarDataScreen(@NonNull CarContext carContext) {
        super(carContext);
        initializeDefaultRows();
    }

    private void initializeDefaultRows() {
        addDynamicRow("STATUS", "Initializing...");
        addDynamicRow("PROGRESS", "Discovering hardware classes...");
    }

    @NonNull
    @Override
    public Template onGetTemplate() {
        // 1) Register real service instances
        //registerRealInstances();

        // 2) Offload discovery + execution to background
        new Thread(this::dumpAndExecuteHardware).start();

        return buildDynamicTemplate();
    }


    // -------------------- Instance Registration --------------------


    private void registerRealInstances() {
        try {
            CarHardwareManager hwMgr = getCarContext()
                    .getCarService(CarHardwareManager.class);
            instanceMap.put(hwMgr.getClass(), hwMgr);

            @SuppressLint("RestrictedApi") PropertyManager pm = getPropertyManager(hwMgr);
            if (pm != null) {
                instanceMap.put(PropertyManager.class, pm);
            } else {
                Log.w(TAG, "PropertyManager is null; REAL invocations will skip");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error registering real instances", e);
        }
    }

    @SuppressLint("RestrictedApi")
    private PropertyManager getPropertyManager(CarHardwareManager hwMgr) throws Exception {
        Object rawInfo = hwMgr.getCarInfo();
        if (!(rawInfo instanceof androidx.car.app.hardware.info.AutomotiveCarInfo)) {
            return null;
        }
        androidx.car.app.hardware.info.AutomotiveCarInfo info =
                (androidx.car.app.hardware.info.AutomotiveCarInfo) rawInfo;

        Field pmField = ReflectUtil.safeGetField(
                androidx.car.app.hardware.info.AutomotiveCarInfo.class,
                "mPropertyManager");
        return pmField == null
                ? null
                : (PropertyManager) ReflectUtil.safeGetInstanceObject(pmField, info);
    }


    // -------------------- Discovery & Execution --------------------

    private void dumpAndExecuteHardware() {
        List<String> classes = getAllHardwareClasses();
        Log.d(TAG, "Found " + classes.size() + " hardware classes");

        for (String fqcn : classes) {
            Log.d(TAG, "\n>>> Class: " + fqcn);
            Class<?> cls = ReflectUtil.safeForName(fqcn);
            if (cls == null) continue;

            dumpClassDetails(cls);

            // DUMMY pass: stub instances & args
            executeClassMembers(cls, "DUMMY ");

            // REAL pass: real instances from instanceMap
            //executeClassMembers(cls, "REAL  ");
        }
    }

    private List<String> getAllHardwareClasses() {
        if (cachedHardwareClasses != null) {
            return cachedHardwareClasses;
        }
        List<String> found = new ArrayList<>();
        try {
            ClassLoader loader = getClass().getClassLoader();
            @SuppressLint("DiscouragedPrivateApi") Field pathList = BaseDexClassLoader.class
                    .getDeclaredField("pathList");
            pathList.setAccessible(true);
            Object dexPathList = pathList.get(loader);

            Field dexElements = dexPathList.getClass()
                    .getDeclaredField("dexElements");
            dexElements.setAccessible(true);
            Object[] elements = (Object[]) dexElements.get(dexPathList);

            for (Object element : elements) {
                Field dexFileField = element.getClass()
                        .getDeclaredField("dexFile");
                dexFileField.setAccessible(true);
                DexFile dex = (DexFile) dexFileField.get(element);
                if (dex == null) continue;

                Enumeration<String> entries = dex.entries();
                while (entries.hasMoreElements()) {
                    String clsName = entries.nextElement();
                    if (clsName.startsWith("androidx.car.app.hardware")) {
                        found.add(clsName);
                    }
                }
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Log.e(TAG, "Failed to scan DexFiles", e);
        }
        cachedHardwareClasses = found;
        return found;
    }

    private void dumpClassDetails(Class<?> cls) {
        // 1) Class-level annotations
        java.lang.annotation.Annotation[] annos = cls.getAnnotations();
        if (annos.length > 0) {
            Log.d(TAG, "Annotations on " + cls.getSimpleName() + ":");
            for (var a : annos) {
                Log.d(TAG, "  @" + a.annotationType().getSimpleName());
            }
        }

        // 2) Fields
        Field[] fields = cls.getDeclaredFields();
        Log.d(TAG, "Fields (" + fields.length + "):");
        for (Field f : fields) {
            f.setAccessible(true);
            String line = "  " + Modifier.toString(f.getModifiers()) + " "
                    + f.getType().getSimpleName() + " " + f.getName();
            Log.d(TAG, line);
        }

        // 3) Methods
        Method[] methods = cls.getDeclaredMethods();
        Log.d(TAG, "Methods (" + methods.length + "):");
        for (Method m : methods) {
            m.setAccessible(true);
            StringBuilder sig = new StringBuilder("  ")
                    .append(Modifier.toString(m.getModifiers()))
                    .append(" ")
                    .append(m.getReturnType().getSimpleName())
                    .append(" ").append(m.getName())
                    .append("(");
            Class<?>[] pts = m.getParameterTypes();
            for (int i = 0; i < pts.length; i++) {
                sig.append(pts[i].getSimpleName());
                if (i < pts.length - 1) sig.append(", ");
            }
            sig.append(")");
            Log.d(TAG, sig.toString());
        }
    }

    private void executeClassMembers(Class<?> cls, String label) {
        Log.d(TAG, label + "Executing members of " + cls.getName());

        // FIELDS
        for (Field f : cls.getDeclaredFields()) {
            f.setAccessible(true);
            boolean isStatic = Modifier.isStatic(f.getModifiers());
            Object receiver = isStatic
                    ? null
                    : ("REAL".equals(label.trim())
                    ? instanceMap.get(cls)
                    : ReflectUtil.getDefaultValue(cls));
            try {
                Object val = f.get(receiver);
                Log.d(TAG, "FIELD " + label
                        + cls.getSimpleName() + "#" + f.getName()
                        + " => " + val);
            } catch (Exception e) {
                Log.w(TAG, "FIELD " + label
                        + cls.getSimpleName() + "#" + f.getName()
                        + " threw " + e.getClass().getSimpleName());
            }
        }

        // METHODS
        for (Method m : cls.getDeclaredMethods()) {
            m.setAccessible(true);
            Class<?>[] pts = m.getParameterTypes();
            Object[] args = new Object[pts.length];
            for (int i = 0; i < pts.length; i++) {
                args[i] = ReflectUtil.getDefaultValue(pts[i]);
            }
            boolean isStatic = Modifier.isStatic(m.getModifiers());
            Object receiver = isStatic
                    ? null
                    : ("REAL".equals(label.trim())
                    ? instanceMap.get(cls)
                    : ReflectUtil.getDefaultValue(cls));

            ReflectUtil.invokeMethod(m, receiver, args, label);
        }
    }

    // -------------------- UI Plumbing --------------------

    private void addDynamicRow(String key, String text) {
        handler.post(() -> {
            synchronized (rows) {
                rows.add(new Row.Builder().setTitle(text).build());
                rowIndexMap.put(key, rows.size() - 1);
                invalidate();
            }
        });
    }

    private void updateDynamicRow(String key, String text) {
        handler.post(() -> {
            synchronized (rows) {
                Integer idx = rowIndexMap.get(key);
                Row row = new Row.Builder().setTitle(text).build();
                if (idx != null) rows.set(idx, row);
                else {
                    rows.add(row);
                    rowIndexMap.put(key, rows.size() - 1);
                }
                invalidate();
            }
        });
    }

    private Template buildDynamicTemplate() {
        Pane.Builder builder = new Pane.Builder();
        synchronized (rows) {
            for (Row r : rows) {
                builder.addRow(r);
            }
        }
        return new PaneTemplate.Builder(builder.build())
                .setHeaderAction(Action.APP_ICON)
                .build();
    }
}

*/