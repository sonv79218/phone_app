package com.example.tuan17.models;

public class NhomSanPham {

    String ma;
    String tennhom;
    String anh;

    public NhomSanPham(String ma, String tennhom, String anh) {
        this.ma = ma;
        this.tennhom = tennhom;
        this.anh = anh;
    }

    public String getMa() {
        return ma;
    }

    public void setMa(String ma) {
        this.ma = ma;
    }
    public String getTennhom() {
        return tennhom;
    }
    public void setTennhom(String tennhom) {
        this.tennhom = tennhom;
    }
    public String getAnh() {
        return anh;
    }
    public void setAnh(String anh) {
        this.anh = anh;
    }
    public String toString() {
        return tennhom; // Hiển thị tên nhóm sản phẩm
    }
}
