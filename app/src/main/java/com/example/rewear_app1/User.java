package com.example.rewear_app1;

public class User {
    private String firstName, lastName, phone, email, password, alamat, ttl;

    public User(String firstName, String lastName, String phone, String email, String password, String alamat, String ttl) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.alamat = alamat;
        this.ttl = ttl;
    }

    public String getFirstName() {
        return firstName;
    }
}
