package com.example.rewear_app1;

public class Voucher {
    private int id;
    private String kode;
    private int potongan;
    private int syarat;

    // Getter
    public int getId() {
        return id;
    }

    public String getKode() {
        return kode;
    }

    public int getPotongan() {
        return potongan;
    }

    public int getSyarat() {
        return syarat;
    }

    // Setter
    public void setId(int id) {
        this.id = id;
    }

    public void setKode(String kode) {
        this.kode = kode;
    }

    public void setPotongan(int potongan) {
        this.potongan = potongan;
    }

    public void setSyarat(int syarat) {
        this.syarat = syarat;  // <--- tambahkan ini jika belum ada
    }
}
