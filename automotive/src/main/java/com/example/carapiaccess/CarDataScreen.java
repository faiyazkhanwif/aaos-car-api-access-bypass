package com.example.carapiaccess;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.os.Handler;
import android.os.Looper;

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

import androidx.car.app.AppInfo;
import androidx.car.app.AppManager;
//import androidx.car.app.CarAppBinder;
import androidx.car.app.CarAppMetadataHolderService;
import androidx.car.app.CarAppPermission;
import androidx.car.app.CarAppPermissionActivity;
import androidx.car.app.CarAppService;
import androidx.car.app.CarContext;
import androidx.car.app.CarToast;
import androidx.car.app.FailureResponse;
import androidx.car.app.HandshakeInfo;
import androidx.car.app.HostCall;
import androidx.car.app.HostDispatcher;
import androidx.car.app.HostException;
import androidx.car.app.HostInfo;
import androidx.car.app.IAppHost;
import androidx.car.app.IAppManager;
import androidx.car.app.ICarApp;
import androidx.car.app.ICarHost;
import androidx.car.app.IOnDoneCallback;
import androidx.car.app.IOnRequestPermissionsListener;
import androidx.car.app.IStartCarApp;
import androidx.car.app.ISurfaceCallback;
import androidx.car.app.OnDoneCallback;
import androidx.car.app.OnRequestPermissionsListener;
import androidx.car.app.OnScreenResultListener;
import androidx.car.app.Screen;
//import androidx.car.app.hardware.common.PropertyRequestProcessor;
import androidx.car.app.ScreenManager;
import androidx.car.app.Session;
import androidx.car.app.SessionInfo;
import androidx.car.app.SessionInfoIntentEncoder;
import androidx.car.app.SurfaceCallback;
import androidx.car.app.SurfaceContainer;
import androidx.car.app.media.AutomotiveCarAudioRecord;
import androidx.car.app.media.CarAudioCallback;
import androidx.car.app.media.CarAudioCallbackDelegate;
import androidx.car.app.media.CarAudioRecord;
import androidx.car.app.media.ICarAudioCallback;
import androidx.car.app.media.IMediaPlaybackHost;
import androidx.car.app.media.MediaPlaybackManager;
import androidx.car.app.media.OpenMicrophoneRequest;
import androidx.car.app.media.OpenMicrophoneResponse;
import androidx.car.app.messaging.model.CarMessage;
import androidx.car.app.messaging.model.ConversationCallback;
import androidx.car.app.messaging.model.ConversationCallbackDelegate;
import androidx.car.app.messaging.model.ConversationItem;
import androidx.car.app.messaging.model.IConversationCallback;
import androidx.car.app.messaging.MessagingServiceConstants;
import androidx.car.app.navigation.NavigationManager;
import androidx.car.app.navigation.NavigationManagerCallback;
import androidx.car.app.navigation.INavigationHost;
import androidx.car.app.navigation.INavigationManager;
import androidx.car.app.navigation.model.Destination;
import androidx.car.app.navigation.model.IPanModeListener;
import androidx.car.app.navigation.model.Lane;
import androidx.car.app.navigation.model.LaneDirection;
import androidx.car.app.navigation.model.Maneuver;
import androidx.car.app.navigation.model.MapController;
import androidx.car.app.navigation.model.MapTemplate;
import androidx.car.app.navigation.model.MapWithContentTemplate;
import androidx.car.app.navigation.model.MessageInfo;
import androidx.car.app.navigation.model.NavigationTemplate;
import androidx.car.app.navigation.model.PanModeDelegate;
import androidx.car.app.navigation.model.PanModeDelegateImpl;
import androidx.car.app.navigation.model.PanModeListener;
import androidx.car.app.navigation.model.PlaceListNavigationTemplate;
import androidx.car.app.navigation.model.RoutePreviewNavigationTemplate;
import androidx.car.app.navigation.model.RoutingInfo;
import androidx.car.app.navigation.model.Step;
import androidx.car.app.navigation.model.TravelEstimate;
import androidx.car.app.navigation.model.Trip;
import androidx.car.app.model.Action;
import androidx.car.app.model.ActionStrip;
import androidx.car.app.model.Alert;
import androidx.car.app.model.AlertCallback;
import androidx.car.app.model.AlertCallbackDelegate;
import androidx.car.app.model.AlertCallbackDelegateImpl;
import androidx.car.app.model.Badge;
import androidx.car.app.model.CarColor;
import androidx.car.app.model.CarIcon;
import androidx.car.app.model.CarIconSpan;
import androidx.car.app.model.CarLocation;
import androidx.car.app.model.CarSpan;
import androidx.car.app.model.CarText;
import androidx.car.app.model.ClickableSpan;
import androidx.car.app.model.Content;
import androidx.car.app.model.DateTimeWithZone;
import androidx.car.app.model.Distance;
import androidx.car.app.model.DistanceSpan;
import androidx.car.app.model.DurationSpan;
import androidx.car.app.model.ForegroundCarColorSpan;
import androidx.car.app.model.GridItem;
import androidx.car.app.model.GridTemplate;
import androidx.car.app.model.Header;
import androidx.car.app.model.IAlertCallback;
import androidx.car.app.model.IInputCallback;
import androidx.car.app.model.IOnCheckedChangeListener;
import androidx.car.app.model.IOnClickListener;
import androidx.car.app.model.IOnContentRefreshListener;
import androidx.car.app.model.IOnItemVisibilityChangedListener;
import androidx.car.app.model.IOnSelectedListener;
import androidx.car.app.model.ISearchCallback;
import androidx.car.app.model.ITabCallback;
import androidx.car.app.model.InputCallback;
import androidx.car.app.model.InputCallbackDelegate;
import androidx.car.app.model.InputCallbackDelegateImpl;
import androidx.car.app.model.Item;
import androidx.car.app.model.ItemList;
import androidx.car.app.model.ListTemplate;
import androidx.car.app.model.LongMessageTemplate;
import androidx.car.app.model.MessageTemplate;
import androidx.car.app.model.Metadata;
import androidx.car.app.model.ModelUtils;
import androidx.car.app.model.OnCheckedChangeDelegate;
import androidx.car.app.model.constraints.ActionsConstraints;
import androidx.car.app.model.constraints.CarColorConstraints;
import androidx.car.app.model.constraints.CarIconConstraints;
import androidx.car.app.model.constraints.CarTextConstraints;
import androidx.car.app.model.constraints.RowConstraints;
import androidx.car.app.model.constraints.RowListConstraints;
import androidx.car.app.model.constraints.TabContentsConstraints;
import androidx.car.app.model.constraints.TabsConstraints;
import androidx.car.app.model.signin.InputSignInMethod;
import androidx.car.app.model.signin.PinSignInMethod;
import androidx.car.app.model.signin.ProviderSignInMethod;
import androidx.car.app.model.signin.SignInTemplate;
import androidx.car.app.model.signin.QRCodeSignInMethod;
import androidx.car.app.constraints.ConstraintManager;
import androidx.car.app.constraints.IConstraintHost;
import androidx.car.app.activity.renderer.ICarAppActivity;
import androidx.car.app.activity.renderer.IInsetsListener;
import androidx.car.app.activity.renderer.IProxyInputConnection;
import androidx.car.app.activity.renderer.IRendererCallback;
import androidx.car.app.activity.renderer.IRendererService;
import androidx.car.app.activity.renderer.surface.ISurfaceControl;
import androidx.car.app.activity.renderer.surface.ISurfaceListener;
import androidx.car.app.activity.renderer.surface.LegacySurfacePackage;
import androidx.car.app.activity.renderer.surface.OnBackPressedListener;
import androidx.car.app.activity.renderer.surface.OnCreateInputConnectionListener;
//import androidx.car.app.activity.renderer.surface.RemoteProxyInputConnection;
import androidx.car.app.activity.renderer.surface.SurfaceControlCallback;
import androidx.car.app.activity.renderer.surface.SurfaceHolderListener;
import androidx.car.app.activity.renderer.surface.SurfaceWrapper;
import androidx.car.app.activity.renderer.surface.SurfaceWrapperProvider;
import androidx.car.app.activity.renderer.surface.TemplateSurfaceView;
import androidx.car.app.activity.ui.ErrorMessageView;
import androidx.car.app.activity.ui.LoadingView;
//import androidx.car.app.activity.ActivityLifecycleDelegate;
import androidx.car.app.activity.BaseCarAppActivity;
import androidx.car.app.activity.CarAppActivity;
import androidx.car.app.activity.CarAppViewModel;
//import androidx.car.app.activity.CarAppViewModelFactory;
import androidx.car.app.activity.ErrorHandler;
import androidx.car.app.activity.HostUpdateReceiver;
import androidx.car.app.activity.LauncherActivity;
import androidx.car.app.activity.LogTags;
import androidx.car.app.activity.ResultManagerAutomotive;
import androidx.car.app.activity.ServiceConnectionManager;
import androidx.car.app.activity.ServiceDispatcher;
import androidx.car.app.annotations.ExperimentalCarApi;
import androidx.car.app.annotations.CarProtocol;
import androidx.car.app.annotations.RequiresCarApi;
import androidx.car.app.annotations.KeepFields;
import androidx.car.app.connection.CarConnection;
import androidx.car.app.managers.Manager;
import androidx.car.app.managers.ManagerFactory;
import androidx.car.app.managers.ManagerCache;
import androidx.car.app.managers.ResultManager;
import androidx.car.app.mediaextensions.MetadataExtras;
import androidx.car.app.suggestion.model.Suggestion;
import androidx.car.app.suggestion.SuggestionManager;
import androidx.car.app.suggestion.ISuggestionHost;
import androidx.car.app.validation.HostValidator;
import androidx.car.app.versioning.CarAppApiLevels;
import androidx.car.app.versioning.CarAppApiLevel;
import androidx.car.app.serialization.Bundleable;
import androidx.car.app.serialization.BundlerException;
import androidx.car.app.serialization.Bundler;
import androidx.car.app.utils.CollectionUtils;
import androidx.car.app.utils.CommonUtils;
//import androidx.car.app.utils.LogTags;
import androidx.car.app.utils.RemoteUtils;
import androidx.car.app.utils.StringUtils;
import androidx.car.app.utils.ThreadUtils;
import androidx.car.app.utils.ThreadUtils;
import androidx.car.app.notification.CarAppExtender;
import androidx.car.app.notification.CarNotificationManager;
import androidx.car.app.notification.CarPendingIntent;
import androidx.car.app.notification.CarAppNotificationBroadcastReceiver;
import androidx.car.app.R;
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
import androidx.collection.ArraySet;

//Android.Car Imports
//import android.car.ApiVersion;
import android.car.Car;
import android.car.CarAppFocusManager;
import android.car.CarInfoManager;
import android.car.CarNotConnectedException;
import android.car.CarOccupantZoneManager;
//import android.car.CarVersion;
import android.car.EvConnectorType;
import android.car.FuelType;
//import android.car.GsrComplianceType;
//import android.car.PlatformVersion;
//import android.car.PlatformVersionMismatchException;
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
//import android.car.hardware.property.AreaIdConfig;
import android.car.hardware.property.CarInternalErrorException;
import android.car.hardware.property.CarPropertyManager;
//import android.car.hardware.property.EvChargeState;
import android.car.hardware.property.EvChargingConnectorType;
//import android.car.hardware.property.EvRegenerativeBrakingState;
//import android.car.hardware.property.LocationCharacterization;
import android.car.hardware.property.PropertyAccessDeniedSecurityException;
import android.car.hardware.property.PropertyNotAvailableAndRetryException;
//import android.car.hardware.property.PropertyNotAvailableErrorCode;
import android.car.hardware.property.PropertyNotAvailableException;
import android.car.hardware.property.VehicleElectronicTollCollectionCardStatus;
import android.car.hardware.property.VehicleElectronicTollCollectionCardType;
//import android.car.input.CarInputManager;
import android.car.media.CarAudioManager;
import android.car.media.CarMediaIntents;
//import android.car.remoteaccess.CarRemoteAccessManager;
//import android.car.remoteaccess.RemoteTaskClientRegistrationInfo;
import android.car.watchdog.CarWatchdogManager;
import android.car.watchdog.IoOveruseStats;
import android.car.watchdog.PerStateBytes;
import android.car.watchdog.ResourceOveruseStats;

//Reflection Imports
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

            dumpCarHardwareHierarchyAndroid();
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
            case "messaging":
                return new String[]{
                        "androidx.car.app.messaging.MessagingServiceConstants",
                        "androidx.car.app.messaging.model.CarMessage",
                        "androidx.car.app.messaging.model.ConversationCallback",
                        "androidx.car.app.messaging.model.ConversationCallbackDelegate",
                        "androidx.car.app.messaging.model.ConversationItem",
                        "androidx.car.app.messaging.model.IConversationCallback"
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
                        //"androidx.car.app.activity.renderer.surface.RemoteProxyInputConnection",
                        "androidx.car.app.activity.renderer.surface.SurfaceControlCallback",
                        "androidx.car.app.activity.renderer.surface.SurfaceHolderListener",
                        "androidx.car.app.activity.renderer.surface.SurfaceWrapper",
                        "androidx.car.app.activity.renderer.surface.SurfaceWrapperProvider",
                        "androidx.car.app.activity.renderer.surface.TemplateSurfaceView",
                        // ui
                        "androidx.car.app.activity.ui.ErrorMessageView",
                        "androidx.car.app.activity.ui.LoadingView"
                };
            case "annotations":
                return new String[]{
                        "androidx.car.app.annotations.ExperimentalCarApi",
                        "androidx.car.app.annotations.CarProtocol",
                        "androidx.car.app.annotations.RequiresCarApi",
                        "androidx.car.app.annotations.KeepFields"
                };
            case "connection":
                return new String[]{
                        "androidx.car.app.connection.CarConnection"
                };
            case "managers":
                return new String[]{
                        "androidx.car.app.managers.Manager",
                        "androidx.car.app.managers.ManagerFactory",
                        "androidx.car.app.managers.ManagerCache",
                        "androidx.car.app.managers.ResultManager"
                };
            case "mediaextensions":
                return new String[]{
                        "androidx.car.app.mediaextensions.MetadataExtras"
                };
            case "suggestion":
                return new String[]{
                        "androidx.car.app.suggestion.SuggestionManager",
                        "androidx.car.app.suggestion.ISuggestionHost",
                        "androidx.car.app.suggestion.model.Suggestion"
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
            case "serialization":
                return new String[]{
                        "androidx.car.app.serialization.Bundleable",
                        "androidx.car.app.serialization.BundlerException",
                        "androidx.car.app.serialization.Bundler"
                };*/
            case "utils":
                return new String[]{
                        //"androidx.car.app.utils.CollectionUtils",
                        //"androidx.car.app.utils.CommonUtils",
                        //"androidx.car.app.utils.LogTags",
                        //"androidx.car.app.utils.RemoteUtils",
                        //"androidx.car.app.utils.StringUtils",
                        //"androidx.car.app.utils.ThreadUtils"
                };
            case "notification":
                return new String[]{
                        //"androidx.car.app.notification.CarAppExtender",
                        //"androidx.car.app.notification.CarNotificationManager",
                        //"androidx.car.app.notification.CarPendingIntent",
                        //"androidx.car.app.notification.CarAppNotificationBroadcastReceiver"
                };
            case "hardware":
                return new String[]{
                        //"androidx.car.app.hardware.CarHardwareManager",
                        //"androidx.car.app.hardware.AutomotiveCarHardwareManager"
                };
            case "hardware.common":
                return new String[]{
                        //"androidx.car.app.hardware.common.CarValue",
                        //"androidx.car.app.hardware.common.CarZone",
                        "androidx.car.app.hardware.common.CarPropertyResponse",
                        "androidx.car.app.hardware.common.PropertyManager",
                        //"androidx.car.app.hardware.common.CarUnit",
                        //"androidx.car.app.hardware.common.CarZoneAreaIdConstants",
                        "androidx.car.app.hardware.common.CarZoneUtils",
                        "androidx.car.app.hardware.common.PropertyResponseCache",
                        //"androidx.car.app.hardware.common.GlobalCarZoneAreaIdConverter",
                        //"androidx.car.app.hardware.common.SeatCarZoneAreaIdConverter",
                        "androidx.car.app.hardware.common.CarPropertyProfile",
                        "androidx.car.app.hardware.common.CarValueUtils",
                        "androidx.car.app.hardware.common.GetPropertyRequest",
                        "androidx.car.app.hardware.common.PropertyIdAreaId",
                        "androidx.car.app.hardware.common.PropertyUtils",
                        "androidx.car.app.hardware.common.CarInternalError",
                        "androidx.car.app.hardware.common.OnCarPropertyResponseListener",
                        //"androidx.car.app.hardware.common.OnCarDataAvailableListener",
                        //"androidx.car.app.hardware.common.CarSetOperationStatusCallback",
                        //"androidx.car.app.hardware.common.CarZoneAreaIdConverter",
                        "androidx.car.app.hardware.common.PropertyRequestProcessor"
                };
            case "hardware.climate":
                return new String[]{
                        "androidx.car.app.hardware.climate.AutomotiveCarClimate",
                        //"androidx.car.app.hardware.climate.CabinTemperatureProfile",
                        //"androidx.car.app.hardware.climate.CarClimate",
                        //"androidx.car.app.hardware.climate.CarClimateFeature",
                        //"androidx.car.app.hardware.climate.CarClimateProfileCallback",
                        //"androidx.car.app.hardware.climate.CarClimateStateCallback",
                        //"androidx.car.app.hardware.climate.CarZoneMappingInfoProfile",
                        //"androidx.car.app.hardware.climate.ClimateProfileRequest",
                        //"androidx.car.app.hardware.climate.ClimateStateRequest",
                        //"androidx.car.app.hardware.climate.DefrosterProfile",
                        //"androidx.car.app.hardware.climate.ElectricDefrosterProfile",
                        //"androidx.car.app.hardware.climate.FanDirectionProfile",
                        //"androidx.car.app.hardware.climate.FanSpeedLevelProfile",
                        //"androidx.car.app.hardware.climate.HvacAcProfile",
                        //"androidx.car.app.hardware.climate.HvacAutoModeProfile",
                        //"androidx.car.app.hardware.climate.HvacAutoRecirculationProfile",
                        //"androidx.car.app.hardware.climate.HvacDualModeProfile",
                        //"androidx.car.app.hardware.climate.HvacMaxAcModeProfile",
                        //"androidx.car.app.hardware.climate.HvacPowerProfile",
                        //"androidx.car.app.hardware.climate.HvacRecirculationProfile",
                        //"androidx.car.app.hardware.climate.MaxDefrosterProfile",
                        //"androidx.car.app.hardware.climate.RegisterClimateStateRequest",
                        //"androidx.car.app.hardware.climate.SeatTemperatureProfile",
                        //"androidx.car.app.hardware.climate.SeatVentilationProfile",
                        //"androidx.car.app.hardware.climate.SteeringWheelHeatProfile"
                };
            case "hardware.info":
                return new String[]{
                        //"androidx.car.app.hardware.info.Accelerometer",
                        "androidx.car.app.hardware.info.AutomotiveCarInfo",
                        "androidx.car.app.hardware.info.AutomotiveCarSensors"
                        //"androidx.car.app.hardware.info.CarHardwareLocation",
                        //"androidx.car.app.hardware.info.CarInfo",
                        //"androidx.car.app.hardware.info.CarSensors",
                        //"androidx.car.app.hardware.info.Compass",
                        //"androidx.car.app.hardware.info.EnergyLevel",
                        //"androidx.car.app.hardware.info.EnergyProfile",
                        //"androidx.car.app.hardware.info.EvStatus",
                        //"androidx.car.app.hardware.info.Gyroscope",
                        //"androidx.car.app.hardware.info.Mileage",
                        //"androidx.car.app.hardware.info.Model",
                        //"androidx.car.app.hardware.info.Speed",
                        //"androidx.car.app.hardware.info.TollCard"
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
                                Log.d(TAG, "fetchCarPropertyValues → values=" + values.size()
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
                                                "  [%s] area=%s → %s",
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