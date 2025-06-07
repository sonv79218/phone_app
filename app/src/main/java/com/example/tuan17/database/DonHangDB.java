package com.example.tuan17.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.tuan17.models.Order;

import java.util.ArrayList;
import java.util.List;

public class DonHangDB {
    private final SQLiteDatabase db;
    public DonHangDB(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context không được null");
        }
        DatabaseHelper helper = new DatabaseHelper(context);
        db = helper.getWritableDatabase();  // Kết nối DB đã tạo bảng ở DatabaseHelper
    }
    // lấy tất cả đơn hàng
    public List<Order> getAllDonHang() {
        List<Order> orders = new ArrayList<>();
        Cursor cursor = null;

        try {
//            String query = "SELECT * FROM Dathang";
            String query = "SELECT * FROM Dathang ORDER BY ngaydathang DESC LIMIT 8";

            cursor = db.rawQuery(query, null); // Cần đối số thứ 2 là null

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(0); // id_dathang
                    String tenKh = cursor.getString(1); // tenkh
                    String diaChi = cursor.getString(2); // diachi
                    String sdt = cursor.getString(3); // sdt
                    float tongThanhToan = cursor.getFloat(4); // tongthanhtoan
                    String ngayDatHang = cursor.getString(5); // ngaydathang

                    // Tạo đối tượng Order và thêm vào danh sách
                    orders.add(new Order(id, tenKh, diaChi, sdt, tongThanhToan, ngayDatHang));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace(); // In ra lỗi nếu có
        } finally {
            if (cursor != null) {
                cursor.close(); // Đóng cursor để tránh rò rỉ bộ nhớ
            }
//            db.close(); // Đóng database sau khi hoàn thành
        }

        return orders;
    }

    // lấy đơn hàng theo tên khách hàng

    public List<Order> getDonHangByTenKh(String tenKh) {
        List<Order> orders = new ArrayList<>();
//        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM Dathang WHERE tenkh = ?";
        Cursor cursor = db.rawQuery(query, new String[]{tenKh});

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0); // id_dathang
                String diaChi = cursor.getString(2); // diachi
                String sdt = cursor.getString(3); // sdt
                float tongThanhToan = cursor.getFloat(4); // tongthanhtoan
                String ngayDatHang = cursor.getString(5); // ngaydathang

                // Tạo đối tượng Order và thêm vào danh sách
                orders.add(new Order(id, tenKh, diaChi, sdt, tongThanhToan, ngayDatHang));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return orders;
    }

}
