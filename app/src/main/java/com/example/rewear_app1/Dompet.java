package com.example.rewear_app1;


public class Dompet {
    private int id;
    private int idUser;
    private int idProduk;
    private String namaProduk;
    private double jumlah;
    private String tanggal;

    // Constructor
    public Dompet() {}

    public Dompet(int id, int idUser, int idProduk, String namaProduk, double jumlah, String tanggal) {
        this.id = id;
        this.idUser = idUser;
        this.idProduk = idProduk;
        this.namaProduk = namaProduk;
        this.jumlah = jumlah;
        this.tanggal = tanggal;
    }

    // Getter dan Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public int getIdProduk() {
        return idProduk;
    }

    public void setIdProduk(int idProduk) {
        this.idProduk = idProduk;
    }

    public String getNamaProduk() {
        return namaProduk;
    }

    public void setNamaProduk(String namaProduk) {
        this.namaProduk = namaProduk;
    }

    public double getJumlah() {
        return jumlah;
    }

    public void setJumlah(double jumlah) {
        this.jumlah = jumlah;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }
}
