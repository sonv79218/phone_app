package com.example.tuan17.models;

public class DanhGia {
    private int id;
    private int userId;
    private int productId;
    private int chitietdonhangId;
    private int rating;

    private String comment;
    private String ngayDanhGia;

    public DanhGia(int userId, int productId, int chitietdonhangId, int rating, String comment, String ngayDanhGia) {
        this.userId = userId;
        this.productId = productId;
        this.chitietdonhangId = chitietdonhangId;
        this.rating = rating;
        this.comment = comment;
        this.ngayDanhGia = ngayDanhGia;
    }

    // Nếu bạn muốn sử dụng setId từ SQLite tự động tạo id
    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) { this.userId = userId; }

    public int getProductId() {
        return productId;
    }
    public void setProductId(int productId) { this.productId = productId; }
    public void setChitietdonhangId(int chitietdonhangId) { this.chitietdonhangId = chitietdonhangId; }
    public int getChitietdonhangId() {
        return chitietdonhangId;
    }
    public int getRating() {
        return rating;
    }
    public void setRating(int rating) { this.rating = rating; }
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) { this.comment = comment; }

    public String getNgayDanhGia() {
        return ngayDanhGia;
    }
    public void setNgayDanhGia(String ngayDanhGia) { this.ngayDanhGia = ngayDanhGia; }
}
