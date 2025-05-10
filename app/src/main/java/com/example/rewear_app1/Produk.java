package com.example.rewear_app1;
public class Produk {
    private int id;
    private String nama;
    private String gambarUri;
    private String harga;
    private String kategori;

    public Produk(int id, String nama, String gambarUri, String harga, String kategori) {
        this.id = id;
        this.nama = nama;
        this.gambarUri = gambarUri;
        this.harga = harga;
        this.kategori = kategori;
    }

    public int getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public String getGambarUri() {
        return gambarUri;
    }

    public String getHarga() {
        return harga;
    }

    public String getKategori() {
        return kategori;
    }
}
