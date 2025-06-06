package com.example.tuan17.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.tuan17.models.ChiTietDonHang;

import java.util.ArrayList;
import java.util.List;

// xử lý lấy dữ liệu chi tiết đơn hàng
public class ChiTietDonHangDB {
    private final SQLiteDatabase db;

    public ChiTietDonHangDB(Context context){
        if (context == null) {
            throw new IllegalArgumentException("Context không được null");
        }
        DatabaseHelper helper = new DatabaseHelper(context);
        db = helper.getWritableDatabase();  // Kết nối DB đã tạo bảng ở DatabaseHelper
    }

    // chi tiết đơn hàng theo ID
    public List<ChiTietDonHang> getChiTietByOrderId(int orderId) {
        List<ChiTietDonHang> chiTietList = new ArrayList<>();
//        SQLiteDatabase database = this.getReadableDatabase();

        // Thực hiện truy vấn
        Cursor cursor = db.rawQuery("SELECT Chitietdonhang.* FROM Chitietdonhang INNER JOIN sanpham ON sanpham.masp = Chitietdonhang.masp INNER JOIN Dathang ON Dathang.id_dathang = Chitietdonhang.id_dathang WHERE Dathang.id_dathang = ?", new String[]{String.valueOf(orderId)});

        if (cursor != null) {
            while (cursor.moveToNext()) {
                // Kiểm tra chỉ mục của các cột trước khi truy cập
                int idIndex = cursor.getColumnIndex("id_chitiet");
                int dathangIdIndex = cursor.getColumnIndex("id_dathang");
                int maSpIndex = cursor.getColumnIndex("masp");
                int soLuongIndex = cursor.getColumnIndex("soluong");
                int donGiaIndex = cursor.getColumnIndex("dongia");
                int anhIndex = cursor.getColumnIndex("anh");

                // Kiểm tra các chỉ mục trước khi sử dụng
                if (idIndex != -1 && maSpIndex != -1 && soLuongIndex != -1 && donGiaIndex != -1 && anhIndex != -1 && dathangIdIndex != -1) {
                    // Khởi tạo và thêm ChiTietDonHang vào danh sách
                    ChiTietDonHang chiTiet = new ChiTietDonHang(
                            cursor.getInt(idIndex),
                            cursor.getInt(dathangIdIndex),
                            cursor.getInt(maSpIndex),
                            cursor.getInt(soLuongIndex),
                            cursor.getFloat(donGiaIndex),
                            cursor.getBlob(anhIndex) // Sử dụng getBlob để lấy ảnh dưới dạng byte[]
                    );
                    chiTietList.add(chiTiet);
                } else {
                    // In thông báo cảnh báo nếu có cột không hợp lệ
                    Log.w("Database", "One of the column indexes is -1. Check if the column exists in the database.");
                }
            }
            cursor.close();
        }
        return chiTietList;
    }

    // chi tiết toàn bộ ??


}
