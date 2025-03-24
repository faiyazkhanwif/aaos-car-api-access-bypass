package com.example.carapiaccess;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import org.lsposed.hiddenapibypass.HiddenApiBypass;

import java.lang.reflect.Method;

public class MyApplication extends Application {
/* Was causing warnings, Using HiddenApiBypass from LSPose instead
    static {
        try {
            // Bypass Android's hidden API restrictions
            Class<?> vmRuntime = Class.forName("dalvik.system.VMRuntime");
            Method getRuntime = vmRuntime.getDeclaredMethod("getRuntime");
            Object runtime = getRuntime.invoke(null);

            Method setHiddenApiExemptions = vmRuntime.getDeclaredMethod(
                    "setHiddenApiExemptions", String[].class
            );
            // Wildcard exemption for all hidden APIs
            setHiddenApiExemptions.invoke(runtime, (Object) new String[]{"L"});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
*/

    static {
        try {
            HiddenApiBypass.addHiddenApiExemptions("L");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(new FakeContext(base));
    }
}