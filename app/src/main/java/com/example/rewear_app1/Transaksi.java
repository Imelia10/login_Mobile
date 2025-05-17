package com.example.rewear_app1;

public class Transaksi {
    private int id;
    private String emailPembeli;
    private int idProduk;
    private String tanggal;
    private String alamat;
    private String metodePembayaran;
    private double ongkir;
    private double diskon;
    private double total;
    private String status; // pending, completed, cancelled

    public Transaksi() {}

    public Transaksi(int id, String emailPembeli, int idProduk, String tanggal,
                     String alamat, String metodePembayaran, double ongkir,
                     double diskon, double total, String status) {
        this.id = id;
        this.emailPembeli = emailPembeli;
        this.idProduk = idProduk;
        this.tanggal = tanggal;
        this.alamat = alamat;
        this.metodePembayaran = metodePembayaran;
        this.ongkir = ongkir;
        this.diskon = diskon;
        this.total = total;
        this.status = status;
    }

    // Getter dan Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getEmailPembeli() { return emailPembeli; }
    public void setEmailPembeli(String emailPembeli) { this.emailPembeli = emailPembeli; }

    public int getIdProduk() { return idProduk; }
    public void setIdProduk(int idProduk) { this.idProduk = idProduk; }

    public String getTanggal() { return tanggal; }
    public void setTanggal(String tanggal) { this.tanggal = tanggal; }

    public String getAlamat() { return alamat; }
    public void setAlamat(String alamat) { this.alamat = alamat; }

    public String getMetodePembayaran() { return metodePembayaran; }
    public void setMetodePembayaran(String metodePembayaran) { this.metodePembayaran = metodePembayaran; }

    public double getOngkir() { return ongkir; }
    public void setOngkir(double ongkir) { this.ongkir = ongkir; }

    public double getDiskon() { return diskon; }
    public void setDiskon(double diskon) { this.diskon = diskon; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}