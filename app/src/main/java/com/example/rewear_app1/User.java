//package com.example.rewear_app1;
//
//public class User {
//    private int id;
//    private String firstName;
//    private String lastName;
//    private String phone;
//    private String email;
//    private String password;
//    private String alamat;
//    private String ttl;
//    private String photoUri;
//
//
//    // Constructor tanpa ID
//    public User(String firstName, String lastName, String phone, String email, String password, String alamat, String ttl, String photoUri) {
//        this.firstName = firstName;
//        this.lastName = lastName;
//        this.phone = phone;
//        this.email = email;
//        this.password = password;
//        this.alamat = alamat;
//        this.ttl = ttl;
//        this.photoUri = photoUri;
//    }
//
//    // Constructor dengan ID
//    public User(int id, String firstName, String lastName, String phone, String email, String password, String alamat, String ttl, String photoUri) {
//        this.id = id;
//        this.firstName = firstName;
//        this.lastName = lastName;
//        this.phone = phone;
//        this.email = email;
//        this.password = password;
//        this.alamat = alamat;
//        this.ttl = ttl;
//        this.photoUri = photoUri;
//    }
//
//    // Getter dan Setter
//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }
//
//    public String getFirstName() {
//        return firstName;
//    }
//
//    public void setFirstName(String firstName) {
//        this.firstName = firstName;
//    }
//
//    public String getLastName() {
//        return lastName;
//    }
//
//    public void setLastName(String lastName) {
//        this.lastName = lastName;
//    }
//
//    public String getPhone() {
//        return phone;
//    }
//
//    public void setPhone(String phone) {
//        this.phone = phone;
//    }
//
//    public String getEmail() {
//        return email;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }
//
//    public String getPassword() {
//        return password;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }
//
//    public String getAlamat() {
//        return alamat;
//    }
//
//    public void setAlamat(String alamat) {
//        this.alamat = alamat;
//    }
//
//    public String getTtl() {
//        return ttl;
//    }
//
//    public void setTtl(String ttl) {
//        this.ttl = ttl;
//    }
//
//    public String getPhotoUri() {
//        return photoUri;
//    }
//
//    public void setPhotoUri(String photoUri) {
//        this.photoUri = photoUri;
//    }
//}


package com.example.rewear_app1;

public class User {
    private int id;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String password;
    private String alamat;
    private String ttl;
    private String photoUri;
    private double saldo; // Tambahkan field saldo


    public User(int id, String firstName, String lastName, String phone, String email, String password, String alamat, String ttl, String photoUri, double saldo) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.alamat = alamat;
        this.ttl = ttl;
        this.photoUri = photoUri;
        this.saldo = saldo;

    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getAlamat() {
        return alamat;
    }
    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }
    public String getTtl() {
        return ttl;
    }
    public void setTtl(String ttl) {
        this.ttl = ttl;
    }
    public String getPhotoUri() {
        return photoUri;
    }
    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }
}

