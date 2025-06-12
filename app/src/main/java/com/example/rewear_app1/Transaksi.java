package com.example.rewear_app1;

public class Transaksi {
    private int idUser;
    private int id;
    private String emailPembeli;
    private String emailUser;

    private int idPembeli;
    private int idProduk;
    private String namaBarang;
    private String tanggal;
    private String alamat;
    private String metodePembayaran;
    private double ongkir;
    private double diskon;
    private double total;
    private String status;
    private String voucherCode; // Add this field


    // Getter dan Setter
    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public int getIdUser() {
        return idUser;
    }

    // Tambahkan setter dan getter ini:
    public String getEmailUser() {
        return emailUser;
    }

    public void setEmailUser(String emailUser) {
        this.emailUser = emailUser;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getEmailPembeli() { return emailPembeli; }
    public void setEmailPembeli(String emailPembeli) { this.emailPembeli = emailPembeli; }

    public int getIdPembeli() { return idPembeli; }
    public void setIdPembeli(int idPembeli) { this.idPembeli = idPembeli; }

    public int getIdProduk() { return idProduk; }
    public void setIdProduk(int idProduk) { this.idProduk = idProduk; }

    public String getNamaBarang() { return namaBarang; }
    public void setNamaBarang(String namaBarang) { this.namaBarang = namaBarang; }

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
    public String getVoucherCode() {
        return voucherCode;
    }

    public void setVoucherCode(String voucherCode) {
        this.voucherCode = voucherCode;
    }
}
