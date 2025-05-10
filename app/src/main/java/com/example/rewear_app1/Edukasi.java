package com.example.rewear_app1;

public class Edukasi {
    private int id;
    private String judul;
    private String deskripsi;
    private String tipe;
    private String videoUri; // null jika artikel

    public Edukasi(int id, String judul, String deskripsi, String tipe, String videoUri) {
        this.id = id;
        this.judul = judul;
        this.deskripsi = deskripsi;
        this.tipe = tipe;
        this.videoUri = videoUri;
    }

    public int getId() {
        return id;
    }

    public String getJudul() {
        return judul;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public String getTipe() {
        return tipe;
    }

    public String getVideoUri() {
        return videoUri;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public void setTipe(String tipe) {
        this.tipe = tipe;
    }

    public void setVideoUri(String videoUri) {
        this.videoUri = videoUri;
    }
}
