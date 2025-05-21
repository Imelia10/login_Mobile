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

    // Constructor
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

    // Getter dan Setter
    public int getId() { return id; }
    public String getNama() { return nama; }
    public String getGambarUri() { return gambarUri; }
    public String getHarga() { return harga; }
    public String getKategori() { return kategori; }
    public String getDeskripsi() { return deskripsi; }
    public String getIdPenjual() { return idPenjual; }
    public String getStatus() { return status; }
}
