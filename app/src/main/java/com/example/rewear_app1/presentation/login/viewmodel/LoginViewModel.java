package com.example.rewear_app1.presentation.login.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.rewear_app1.DatabaseHelper;
import com.example.rewear_app1.User;

public class LoginViewModel extends ViewModel {

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> loginStatus = new MutableLiveData<>();
    private final MutableLiveData<User> loggedInUser = new MutableLiveData<>();

    private DatabaseHelper dbHelper;

    public void setDatabaseHelper(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getLoginStatus() {
        return loginStatus;
    }

    public LiveData<User> getLoggedInUser() {
        return loggedInUser;
    }


    public void login(String email, String password) {
        if (email.isEmpty()) {
            loginStatus.setValue("Email tidak boleh kosong!");
            return;
        }

        if (password.isEmpty()) {
            loginStatus.setValue("Password tidak boleh kosong!");
            return;
        }

        isLoading.setValue(true);

        new Thread(() -> {
            try {
                Thread.sleep(500); // Simulasi delay

                // Admin Login
                if (email.equals("admin@gmail.com") && password.equals("123")) {
                    loginStatus.postValue("admin");
                } else if (dbHelper != null) {
                    // User Login dari DB
                    User user = dbHelper.getUserByEmailAndPassword(email, password);
                    if (user != null) {
                        loggedInUser.postValue(user);
                        loginStatus.postValue("user");
                    } else {
                        loginStatus.postValue("Email atau Password salah!");
                    }
                } else {
                    loginStatus.postValue("Database error");
                }
            } catch (Exception e) {
                loginStatus.postValue("Login gagal: " + e.getMessage());
            } finally {
                isLoading.postValue(false);
            }
        }).start();
    }
}
