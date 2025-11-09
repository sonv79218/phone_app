package com.example.tuan17.models;

public class TaiKhoan {
    private int id;
    private String tendn;
    private String matkhau;
    private String email;
    private String sdt;
    private String hoten;
    private String diachi;
    private String quyen;
    private String ngaytao;
    private int trangthai;

    public int getTrangthai() {
        return trangthai;
    }

    public void setTrangthai(int trangthai) {
        this.trangthai = trangthai;
    }

    // Constructor đầy đủ
    public TaiKhoan(int id, String tendn, String matkhau, String email,
                    String sdt, String hoten, String diachi,
                    String quyen, String ngaytao, int trangthai) {
        this.id = id;
        this.tendn = tendn;
        this.matkhau = matkhau;
        this.email = email;
        this.sdt = sdt;
        this.hoten = hoten;
        this.diachi = diachi;
        this.quyen = quyen;
        this.ngaytao = ngaytao;
        this.trangthai = trangthai;
    }

    public TaiKhoan(String tendn, String matkhau, String quyen) {
        this.tendn = tendn;
        this.matkhau = matkhau;
        this.quyen = quyen;
    }

    // Getter và Setter (nếu cần)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTdn() {
        return tendn;
    }

    public void setTdn(String tendn) {
        this.tendn = tendn;
    }

    public String getMk() {
        return matkhau;
    }

    public void setMk(String matkhau) {
        this.matkhau = matkhau;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public String getHoten() {
        return hoten;
    }

    public void setHoten(String hoten) {
        this.hoten = hoten;
    }

    public String getDiachi() {
        return diachi;
    }

    public void setDiachi(String diachi) {
        this.diachi = diachi;
    }

    public String getQuyen() {
        return quyen;
    }

    public void setQuyen(String quyen) {
        this.quyen = quyen;
    }

    public String getNgaytao() {
        return ngaytao;
    }

    public void setNgaytao(String ngaytao) {
        this.ngaytao = ngaytao;
    }
}
