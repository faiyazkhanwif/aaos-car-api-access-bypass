package com.example.carapiaccess;

import android.annotation.SuppressLint;
import android.car.Car;
import android.car.VehicleAreaType;
import android.car.hardware.CarPropertyConfig;
import android.car.hardware.CarPropertyValue;
import android.car.hardware.property.CarInternalErrorException;
import android.car.hardware.property.CarPropertyManager;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.ArraySet;
import android.util.Log;
import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.car.app.CarContext;
import androidx.car.app.Screen;
import androidx.car.app.annotations.ExperimentalCarApi;
import androidx.car.app.hardware.CarHardwareManager;
import androidx.car.app.hardware.common.CarPropertyResponse;
import androidx.car.app.hardware.common.CarValue;
import androidx.car.app.hardware.common.CarZone;
import androidx.car.app.hardware.common.OnCarPropertyResponseListener;
import androidx.car.app.hardware.common.PropertyManager;
import androidx.car.app.hardware.info.AutomotiveCarInfo;
import androidx.car.app.model.Action;
import androidx.car.app.model.Pane;
import androidx.car.app.model.PaneTemplate;
import androidx.car.app.model.Row;
import androidx.car.app.model.Template;

import org.lsposed.hiddenapibypass.HiddenApiBypass;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.lang.Class;


// Not needed for now
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

public class CarDataScreen extends Screen {
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Map<String, Integer> rowIndexMap = new HashMap<>();
    private final List<Row> rows = new ArrayList<>();

    public CarDataScreen(@NonNull CarContext carContext) {
        super(carContext);
        initializeDefaultRows();
    }

    private void initializeDefaultRows() {
        addDynamicRow("STATUS", "Initializing car data...");
        addDynamicRow("PROGRESS", "Discovering properties...");
    }

    @NonNull
    @Override
    public Template onGetTemplate() {
        fetchAllCarProperties();
        return buildDynamicTemplate();
    }

    @OptIn(markerClass = ExperimentalCarApi.class)
    @SuppressLint({"PrivateApi", "RestrictedApi"})
    private void fetchAllCarProperties() {
        try {
            CarHardwareManager hardwareManager = getCarContext().getCarService(CarHardwareManager.class);
            PropertyManager propertyManager = getPropertyManager(hardwareManager);

            if (propertyManager == null) {
                updateDynamicRow("STATUS", "Failed to access PropertyManager");
                return;
            }
            List<Integer> allProperties = getAllVehiclePropertyIds();
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
            Log.e("CarData", "Reflection failed", e);
        }
    }


    //--------------------------------------Test Block--------------------------------------

    //--------------------------------------------------------------------------------------


    @SuppressLint("RestrictedApi")
    private PropertyManager getPropertyManager(CarHardwareManager hardwareManager) throws Exception {
        AutomotiveCarInfo carInfo = (AutomotiveCarInfo) hardwareManager.getCarInfo();
        Field pmField = AutomotiveCarInfo.class.getDeclaredField("mPropertyManager");
        pmField.setAccessible(true);
        return (PropertyManager) pmField.get(carInfo);
    }

    private List<Integer> getAllVehiclePropertyIds() throws Exception {
        List<Integer> propertyIds = new ArrayList<>();
        Class<?> cls = Class.forName("android.car.VehiclePropertyIds");

        // Get all property IDs
        for (Field field : cls.getDeclaredFields()) {
            if (field.getType() == int.class) {
                //int propId = field.getInt(null);
                //propertyIds.add(propId);
                //New
                try {
                    int propId = field.getInt(null);
                    propertyIds.add(propId);
                    Log.d("CarData", "Discovered property: " + field.getName() + " = " + propId);
                } catch (Exception e) {
                    Log.e("CarData", "Failed to access field: " + field.getName(), e);
                    CarDataLogger.logError(getCarContext(), e, "Failed to fetch car properties");
                }
            }
        }

        // Patch permission map
        Class<?> utilsClass = Class.forName("androidx.car.app.hardware.common.PropertyUtils");
        Field permField = utilsClass.getDeclaredField("PERMISSION_READ_PROPERTY");
        permField.setAccessible(true);
        SparseArray<String> permMap = (SparseArray<String>) permField.get(null);

        // Add dummy permission for missing properties
        String dummyPermission = "android.car.permission.CAR_VENDOR_EXTENSION";
        for (int propId : propertyIds) {
            if (permMap.get(propId) == null) {
                permMap.put(propId, dummyPermission);
                Log.d("CarData", "Patched permission for property: " + propId);
            }
        }

        return propertyIds;
    }

    //Data Handling------------------------------------
    /* Was kinda working
    @OptIn(markerClass = ExperimentalCarApi.class)
    private Map<Integer, List<CarZone>> createGlobalRequest(List<Integer> propertyIds) {
        Map<Integer, List<CarZone>> request = new HashMap<>();
        for (Integer propId : propertyIds) {
            request.put(propId, Collections.singletonList(CarZone.CAR_ZONE_GLOBAL));
        }
        return request;
    }
    */
    @OptIn(markerClass = ExperimentalCarApi.class)
    private Map<Integer, List<CarZone>> createGlobalRequest(List<Integer> propertyIds) {
        Map<Integer, List<CarZone>> request = new HashMap<>();
        // Use valid CarZone builder methods
        CarZone globalZone = new CarZone.Builder()
                .setRow(CarZone.CAR_ZONE_ROW_ALL)
                .setColumn(CarZone.CAR_ZONE_COLUMN_ALL)
                .build();

        for (Integer propId : propertyIds) {
            request.put(propId, Collections.singletonList(globalZone));
        }
        return request;
    }

    /* Working - but other one better
    @SuppressLint("RestrictedApi")
    private void handlePropertyResponses(@SuppressLint("RestrictedApi") List<CarPropertyResponse<?>> responses) {
        for (CarPropertyResponse<?> response : responses) {
            if (response.getStatus() != CarValue.STATUS_SUCCESS) continue;

            String propName = getPropertyName(response.getPropertyId());
            String value = String.valueOf(response.getValue());
            String key = "PROP_" + response.getPropertyId();

            updateDynamicRow(key, String.format("%s: %s", propName, value));
        }
    }
    */

    @SuppressLint({"RestrictedApi", "DefaultLocale"})
    private void handlePropertyResponses(@SuppressLint("RestrictedApi") List<CarPropertyResponse<?>> responses) {
        Map<Integer, Boolean> successMap = new HashMap<>();

        for (CarPropertyResponse<?> response : responses) {
            @SuppressLint("RestrictedApi") int propId = response.getPropertyId();
            @SuppressLint("RestrictedApi") boolean success = response.getStatus() == CarValue.STATUS_SUCCESS;
            successMap.put(propId, success);

            if (success) {
                // Update UI with successful property
                updateDynamicRow("PROP_" + propId,
                        String.format("%s: %s",
                                getPropertyName(propId),
                                response.getValue()));
            }
        }

        // Update status with successful count
        long successCount = successMap.values().stream().filter(b -> b).count();
        updateDynamicRow("STATUS",
                String.format("Success: %d/%d properties",
                        successCount,
                        successMap.size()));
    }


    // UI Management -----------------------
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

    // Property Name Resolution
    private String getPropertyName(int propId) {
        try {
            Class<?> cls = Class.forName("android.car.VehiclePropertyIds");
            for (Field field : cls.getDeclaredFields()) {
                if (field.getInt(null) == propId) {
                    return field.getName().replace("VEHICLE_PROPERTY_", "");
                }
            }
        } catch (Exception e) {
            CarDataLogger.logError(getCarContext(), e, "Property name resolution failed");
        }
        return "UNKNOWN_PROPERTY_" + propId;
    }
}