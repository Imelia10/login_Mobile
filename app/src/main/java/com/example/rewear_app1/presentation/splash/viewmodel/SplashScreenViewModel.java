package com.example.rewear_app1.presentation.splash.viewmodel;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SplashScreenViewModel extends ViewModel {

    private final MutableLiveData<String> navigationTarget = new MutableLiveData<>();

    public LiveData<String> getNavigationTarget() {
        return navigationTarget;
    }

    public void checkSession(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE);
        String email = sharedPreferences.getString("email_login", null);
        long expiresAt = sharedPreferences.getLong("session_expires_at", 0);

        long now = System.currentTimeMillis();

        if (email == null || now > expiresAt) {
            navigationTarget.setValue("login");
        } else if (email.equals("admin@gmail.com")) {
            navigationTarget.setValue("admin");
        } else {
            navigationTarget.setValue("user");
        }
    }
}
