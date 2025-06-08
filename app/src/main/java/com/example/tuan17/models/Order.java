package com.example.tuan17.models;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private int id; // ID đơn hàng
    private int userId; // ID người dùng
    private String diaChi;
    private String sdt;
    private float tongTien;
    private String ngayDatHang;
    private List<ChiTietDonHang> chiTietList; // Danh sách chi tiết đơn hàng

    public Order(int id, int userId, String diaChi, String sdt, float tongTien, String ngayDatHang) {
        this.id = id;
        this.userId = userId;
        this.diaChi = diaChi;
        this.sdt = sdt;
        this.tongTien = tongTien;
        this.ngayDatHang = ngayDatHang;
        this.chiTietList = new ArrayList<>(); // Khởi tạo danh sách chi tiết
    }

    // Getter methods
    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public String getSdt() {
        return sdt;
    }

    public float getTongTien() {
        return tongTien;
    }

    public String getNgayDatHang() {
        return ngayDatHang;
    }

    public List<ChiTietDonHang> getChiTietList() {
        return chiTietList;
    }

    public void setChiTietList(List<ChiTietDonHang> chiTietList) {
        this.chiTietList = chiTietList;
    }
}
