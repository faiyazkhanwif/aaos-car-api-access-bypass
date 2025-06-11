package com.example.carapiaccess;

import android.annotation.SuppressLint;
import android.util.Log;
import android.util.SparseArray;
import android.os.Handler;
import android.os.Looper;

//AndroidX imports
import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.car.app.CarContext;
import androidx.car.app.Screen;
import androidx.car.app.annotations.ExperimentalCarApi;
import androidx.car.app.hardware.CarHardwareManager;
import androidx.car.app.hardware.AutomotiveCarHardwareManager;
import androidx.car.app.hardware.common.CarValue;
import androidx.car.app.hardware.common.CarZone;
import androidx.car.app.hardware.common.CarPropertyResponse;
import androidx.car.app.hardware.common.PropertyManager;
import androidx.car.app.hardware.common.CarUnit;
import androidx.car.app.hardware.common.CarZoneAreaIdConstants;
import androidx.car.app.hardware.common.CarZoneUtils;
import androidx.car.app.hardware.common.GlobalCarZoneAreaIdConverter;
import androidx.car.app.hardware.common.SeatCarZoneAreaIdConverter;
import androidx.car.app.hardware.common.CarPropertyProfile;
import androidx.car.app.hardware.common.CarValueUtils;
import androidx.car.app.hardware.common.GetPropertyRequest;
import androidx.car.app.hardware.common.PropertyIdAreaId;
import androidx.car.app.hardware.common.PropertyUtils;
import androidx.car.app.hardware.common.OnCarPropertyResponseListener;
import androidx.car.app.hardware.common.OnCarDataAvailableListener;
import androidx.car.app.hardware.common.CarSetOperationStatusCallback;
import androidx.car.app.hardware.common.CarZoneAreaIdConverter;
import androidx.car.app.hardware.climate.AutomotiveCarClimate;
import androidx.car.app.hardware.climate.CabinTemperatureProfile;
import androidx.car.app.hardware.climate.CarClimate;
import androidx.car.app.hardware.climate.CarClimateFeature;
import androidx.car.app.hardware.climate.CarClimateProfileCallback;
import androidx.car.app.hardware.climate.CarClimateStateCallback;
import androidx.car.app.hardware.climate.CarZoneMappingInfoProfile;
import androidx.car.app.hardware.climate.ClimateProfileRequest;
import androidx.car.app.hardware.climate.ClimateStateRequest;
import androidx.car.app.hardware.climate.DefrosterProfile;
import androidx.car.app.hardware.climate.ElectricDefrosterProfile;
import androidx.car.app.hardware.climate.FanDirectionProfile;
import androidx.car.app.hardware.climate.FanSpeedLevelProfile;
import androidx.car.app.hardware.climate.HvacAcProfile;
import androidx.car.app.hardware.climate.HvacAutoModeProfile;
import androidx.car.app.hardware.climate.HvacAutoRecirculationProfile;
import androidx.car.app.hardware.climate.HvacDualModeProfile;
import androidx.car.app.hardware.climate.HvacMaxAcModeProfile;
import androidx.car.app.hardware.climate.HvacPowerProfile;
import androidx.car.app.hardware.climate.HvacRecirculationProfile;
import androidx.car.app.hardware.climate.MaxDefrosterProfile;
import androidx.car.app.hardware.climate.RegisterClimateStateRequest;
import androidx.car.app.hardware.climate.SeatTemperatureProfile;
import androidx.car.app.hardware.climate.SeatVentilationProfile;
import androidx.car.app.hardware.climate.SteeringWheelHeatProfile;
import androidx.car.app.hardware.info.Accelerometer;
import androidx.car.app.hardware.info.AutomotiveCarInfo;
import androidx.car.app.hardware.info.AutomotiveCarSensors;
import androidx.car.app.hardware.info.CarHardwareLocation;
import androidx.car.app.hardware.info.CarInfo;
import androidx.car.app.hardware.info.CarSensors;
import androidx.car.app.hardware.info.Compass;
import androidx.car.app.hardware.info.EnergyLevel;
import androidx.car.app.hardware.info.EnergyProfile;
import androidx.car.app.hardware.info.EvStatus;
import androidx.car.app.hardware.info.Gyroscope;
import androidx.car.app.hardware.info.Mileage;
import androidx.car.app.hardware.info.Model;
import androidx.car.app.hardware.info.Speed;
import androidx.car.app.hardware.info.TollCard;
import androidx.car.app.model.Action;
import androidx.car.app.model.Pane;
import androidx.car.app.model.PaneTemplate;
import androidx.car.app.model.Row;
import androidx.car.app.model.Template;

//Android.Car Imports
import android.car.ApiVersion;
import android.car.Car;
import android.car.CarAppFocusManager;
import android.car.CarInfoManager;
import android.car.CarNotConnectedException;
import android.car.CarOccupantZoneManager;
import android.car.CarVersion;
import android.car.EvConnectorType;
import android.car.FuelType;
import android.car.GsrComplianceType;
import android.car.PlatformVersion;
import android.car.PlatformVersionMismatchException;
import android.car.PortLocationType;
import android.car.VehicleAreaSeat;
import android.car.VehicleAreaType;
import android.car.VehicleAreaWheel;
import android.car.VehicleGear;
import android.car.VehicleIgnitionState;
import android.car.VehiclePropertyIds;
import android.car.VehicleUnit;
import android.car.content.pm.CarPackageManager;
import android.car.drivingstate.CarUxRestrictions;
import android.car.drivingstate.CarUxRestrictionsManager;
import android.car.hardware.CarPropertyConfig;
import android.car.hardware.CarPropertyValue;
import android.car.hardware.CarSensorEvent;
import android.car.hardware.CarSensorManager;
import android.car.hardware.power.CarPowerManager;
import android.car.hardware.power.CarPowerPolicy;
import android.car.hardware.power.CarPowerPolicyFilter;
import android.car.hardware.power.PowerComponent;
import android.car.hardware.property.AreaIdConfig;
import android.car.hardware.property.CarInternalErrorException;
import android.car.hardware.property.CarPropertyManager;
import android.car.hardware.property.EvChargeState;
import android.car.hardware.property.EvChargingConnectorType;
import android.car.hardware.property.EvRegenerativeBrakingState;
import android.car.hardware.property.LocationCharacterization;
import android.car.hardware.property.PropertyAccessDeniedSecurityException;
import android.car.hardware.property.PropertyNotAvailableAndRetryException;
import android.car.hardware.property.PropertyNotAvailableErrorCode;
import android.car.hardware.property.PropertyNotAvailableException;
import android.car.hardware.property.VehicleElectronicTollCollectionCardStatus;
import android.car.hardware.property.VehicleElectronicTollCollectionCardType;
import android.car.input.CarInputManager;
import android.car.media.CarAudioManager;
import android.car.media.CarMediaIntents;
import android.car.remoteaccess.CarRemoteAccessManager;
import android.car.remoteaccess.RemoteTaskClientRegistrationInfo;
import android.car.watchdog.CarWatchdogManager;
import android.car.watchdog.IoOveruseStats;
import android.car.watchdog.PerStateBytes;
import android.car.watchdog.ResourceOveruseStats;

//Reflection Imports
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CarDataScreen extends Screen {
    private static final String TAG = "CarDataScreen";
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Map<String, Integer> rowIndexMap = new HashMap<>();
    private final List<Row> rows = new ArrayList<>();

    // Hold real instances so we can call their methods
    private final Map<Class<?>,Object> instanceMap = new HashMap<>();


    public CarDataScreen(@NonNull CarContext carContext) {
        super(carContext);
        initializeDefaultRows();
    }

    private void initializeDefaultRows() {
        addDynamicRow("STATUS","Init...");
        addDynamicRow("PROGRESS","Discovering...");
    }

    @NonNull
    @Override
    public Template onGetTemplate() {
        //fetchAllCarProperties();
        //registerRealInstances();
        //dumpCarAppHardwareHierarchyAndroidX();
        dumpCarHardwareHierarchyAndroid();
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
                    5.0f,
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

    // NEW: returns the list of class FQCNs for each sub-package
    private String[] getClassListForAndroidX(String sub) {
        switch (sub) {
            case "common":
                return new String[]{
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
            case "climate":
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
            case "info":
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
            default:
                return new String[0];
        }
    }

    private void dumpCarAppHardwareHierarchyAndroidX() {
        String[] subs = {"common", "climate", "info"};
        for (String sub : subs) {
            String pkg = "androidx.car.app.hardware." + sub;
            Log.d(TAG, "====================================================================");
            Log.d(TAG, "====================================================================");
            Log.d(TAG, "====================================================================");
            Log.d(TAG, "\n--- Subpackage: " + pkg + " ---");

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
                //executeClassMembers(cls, "REAL  ");
            }
        }
    }

    //For android.car
    private String[] getClassListForAndroid(String sub) {
        switch (sub) {
            case "hardware":
                return new String[]{
                        "android.car.hardware.CarPropertyConfig",
                        "android.car.hardware.CarPropertyValue",
                        "android.car.hardware.CarSensorEvent",
                        "android.car.hardware.CarSensorManager"
                };
            case "hardware.power":
                return new String[]{
                        "android.car.hardware.power.CarPowerManager",
                        "android.car.hardware.power.CarPowerPolicy",
                        "android.car.hardware.power.CarPowerPolicyFilter",
                        "android.car.hardware.power.PowerComponent"
                };
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

            // PropertyManager (hidden inside AutomotiveCarHardwareManager → AutomotiveCarInfo)
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