package com.example.rewear_app1;



public class Produk {
    private int id;
    private String nama;
    private String gambarUri;
    private String harga;
    private String kategori;
    private String deskripsi;
    private String idPenjual;  // Ubah ke String karena menggunakan userId sebagai String

    // Constructor
    public Produk(int id, String nama, String gambarUri, String harga, String kategori, String deskripsi, String idPenjual) {
        this.id = id;
        this.nama = nama;
        this.gambarUri = gambarUri;
        this.harga = harga;
        this.kategori = kategori;
        this.deskripsi = deskripsi;
        this.idPenjual = idPenjual;
    }

    // Getter dan Setter
    public int getId() { return id; }
    public String getNama() { return nama; }
    public String getGambarUri() { return gambarUri; }
    public String getHarga() { return harga; }
    public String getKategori() { return kategori; }
    public String getDeskripsi() { return deskripsi; }
    public String getIdPenjual() { return idPenjual; }
}
