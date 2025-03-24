package com.example.carapiaccess;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.car.app.CarAppService;
import androidx.car.app.Session;
import androidx.car.app.validation.HostValidator;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class MyCarAppService extends CarAppService {
    @Override
    public void onCreate() {
        super.onCreate();
    }


    @NonNull
    @Override
    public Session onCreateSession() {
        return new CarDataSession();
    }

    @NonNull
    @Override
    public HostValidator createHostValidator() {
        // Use the default validator for trusted hosts
        return HostValidator.ALLOW_ALL_HOSTS_VALIDATOR; // Allows all hosts (for development purposes)

        // For production, restrict it to trusted hosts only:
        // return HostValidator.DEFAULT;
    }
}
