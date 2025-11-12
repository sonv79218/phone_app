package com.example.tuan17.models;

public class SanPham {
    // Optional server-side id (may differ from masp). Can be null.
    String id;
    String masp;
    String tensp;
    Float dongia;
    String mota,ghichu;
    int soluongkho;

String mansp;
//    byte[] anh;
String anh;
    public SanPham(String masp, String tensp, Float dongia, String mota, String ghichu, int soluongkho, String mansp, String anh) {
        this.masp = masp;
        this.tensp = tensp;
        this.dongia = dongia;
        this.mota = mota;
        this.ghichu = ghichu;
        this.soluongkho = soluongkho;
        this.mansp = mansp;
        this.anh = anh;
    }

    // Overloaded constructor with server id
    public SanPham(String id, String masp, String tensp, Float dongia, String mota, String ghichu, int soluongkho, String mansp, String anh) {
        this.id = id;
        this.masp = masp;
        this.tensp = tensp;
        this.dongia = dongia;
        this.mota = mota;
        this.ghichu = ghichu;
        this.soluongkho = soluongkho;
        this.mansp = mansp;
        this.anh = anh;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMasp() {
        return masp;
    }

    public void setMasp(String masp) {
        this.masp = masp;
    }

    public String getTensp() {
        return tensp;
    }

    public void setTensp(String tensp) {
        this.tensp = tensp;
    }

    public Float getDongia() {
        return dongia;
    }

    public void setDongia(Float dongia) {
        this.dongia = dongia;
    }

    public String getMota() {
        return mota;
    }

    public void setMota(String mota) {
        this.mota = mota;
    }

    public String getGhichu() {
        return ghichu;
    }

    public void setGhichu(String ghichu) {
        this.ghichu = ghichu;
    }

    public int getSoluongkho() {
        return soluongkho;
    }

    public void setSoluongkho(int soluongkho) {
        this.soluongkho = soluongkho;
    }

    public String getMansp() {
        return mansp;
    }

    public void setMansp(String ma) {
        this.mansp = ma;
    }

    public String getAnh() {
        return anh;
    }

    public void setAnh(String anh) {
        this.anh = anh;
    }

}
