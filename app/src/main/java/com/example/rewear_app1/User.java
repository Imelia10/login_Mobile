package com.example.rewear_app1;

public class User {
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String password;
    private String alamat;
    private String ttl;
    private String photoUri;

    // Constructor
    public User(String firstName, String lastName, String phone, String email, String password, String alamat, String ttl, String photoUri) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.alamat = alamat;
        this.ttl = ttl;
        this.photoUri = photoUri;
    }

    // Getters
    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {  // Pastikan method ini ada!
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getAlamat() {
        return alamat;
    }

    public String getTtl() {
        return ttl;
    }

    public String getPhotoUri() {
        return photoUri;
    }
}

