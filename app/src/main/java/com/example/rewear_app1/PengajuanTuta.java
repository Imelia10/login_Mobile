    package com.example.rewear_app1;

    public class PengajuanTuta {
        private int id;
        private int produkId;
        private String namaProduk;
        private String namaBarangTukar;
        private String hargaTukar;
        private String metodePembayaran;
        private String tanggal;
        private String gambarUri;
        private String emailPengaju;
        private String emailPemilik;
        private  String gambar_tuta;
        private  String status;

        public PengajuanTuta() {}

        public PengajuanTuta(int id, int produkId, String namaProduk, String namaBarangTukar,
                             String hargaTukar, String metodePembayaran, String tanggal,
                             String gambarUri, String emailPengaju, String emailPemilik, String gambar_tuta, String status) {
            this.id = id;
            this.produkId = produkId;
            this.namaProduk = namaProduk;
            this.namaBarangTukar = namaBarangTukar;
            this.hargaTukar = hargaTukar;
            this.metodePembayaran = metodePembayaran;
            this.tanggal = tanggal;
            this.gambarUri = gambarUri;
            this.emailPengaju = emailPengaju;
            this.emailPemilik = emailPemilik;
            this.gambar_tuta = gambar_tuta;
            this.status = status;
        }


        // Getter dan Setter

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getProdukId() {
            return produkId;
        }

        public void setProdukId(int produkId) {
            this.produkId = produkId;
        }

        public String getNamaProduk() {
            return namaProduk;
        }

        public void setNamaProduk(String namaProduk) {
            this.namaProduk = namaProduk;
        }

        public String getNamaBarangTukar() {
            return namaBarangTukar;
        }

        public void setNamaBarangTukar(String namaBarangTukar) {
            this.namaBarangTukar = namaBarangTukar;
        }

        public String getHargaTukar() {
            return hargaTukar;
        }

        public void setHargaTukar(String hargaTukar) {
            this.hargaTukar = hargaTukar;
        }

        public String getMetodePembayaran() {
            return metodePembayaran;
        }

        public void setMetodePembayaran(String metodePembayaran) {
            this.metodePembayaran = metodePembayaran;
        }

        public String getTanggal() {
            return tanggal;
        }

        public void setTanggal(String tanggal) {
            this.tanggal = tanggal;
        }

        public String getGambarUri() {
            return gambarUri;
        }

        public void setGambarUri(String gambarUri) {
            this.gambarUri = gambarUri;
        }
        public String getEmailPengaju() {
            return emailPengaju;
        }
        public void setEmailPengaju(String emailPengaju) {
            this.emailPengaju = emailPengaju;
        }
        public String getEmailPemilik() {
            return emailPemilik;
        }

        public void setEmailPemilik(String emailPemilik) {
            this.emailPemilik = emailPemilik;
        }
        public String getGambar_tuta() {
            return gambar_tuta;
        }

        public void setGambar_tuta(String gambarTuta) {
            this.gambar_tuta = gambarTuta;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
