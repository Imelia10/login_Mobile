package com.example.rewear_app1;

public class Produk {
    private int id;
    private String nama;
    private String gambarUri;
    private String harga;
    private String kategori;
    private String deskripsi;
    private String idPenjual;
    private String status;

    // Constructor lengkap
    public Produk(int id, String nama, String gambarUri, String harga, String kategori, String deskripsi, String idPenjual, String status) {
        this.id = id;
        this.nama = nama;
        this.gambarUri = gambarUri;
        this.harga = harga;
        this.kategori = kategori;
        this.deskripsi = deskripsi;
        this.idPenjual = idPenjual;
        this.status = status;
    }

    // Konstruktor kosong
    public Produk() {
    }

    // Getter
    public int getId() { return id; }
    public String getNama() { return nama; }
    public String getGambarUri() { return gambarUri; }
    public String getHarga() { return harga; }
    public String getKategori() { return kategori; }
    public String getDeskripsi() { return deskripsi; }
    public String getIdPenjual() { return idPenjual; }
    public String getStatus() { return status; }

    // Setter
    public void setId(int id) { this.id = id; }
    public void setNama(String nama) { this.nama = nama; }
    public void setGambarUri(String gambarUri) { this.gambarUri = gambarUri; }
    public void setHarga(String harga) { this.harga = harga; }
    public void setKategori(String kategori) { this.kategori = kategori; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }
    public void setIdPenjual(String idPenjual) { this.idPenjual = idPenjual; }
    public void setStatus(String status) { this.status = status; }
}
