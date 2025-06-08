package com.example.tuan17;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.tuan17.database.ChiTietDonHangDB;
import com.example.tuan17.database.DatabaseHelper;
import com.example.tuan17.helper.SharedPrefHelper;

public class OrderManager {
    private Context context;
    private DatabaseHelper dbHelper;
//    private ChiTietDonHangDB chiTietDonHangDB;

    public OrderManager(Context context) {
        this.context = context; // Gán context
        this.dbHelper = new DatabaseHelper(context); // Khởi tạo DB helper
//        this.chiTietDonHangDB = new ChiTietDonHangDB(context);
    }

    // ✅ Thêm đơn hàng mới vào bảng Dathang
    public long addOrder(String tenKh, String diaChi, String sdt, float tongTien) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
// tự động tạo id
        int userId = SharedPrefHelper.getUserId(context); // Lấy user_id
        values.put("user_id", userId);
        values.put("tenkh", tenKh);
        values.put("diachi", diaChi);
        values.put("sdt", sdt);
        values.put("tongthanhtoan", tongTien);
// ngày đặt hàng đc tạo tự động
        long id = db.insert("Dathang", null, values);
        db.close();
        return id;
    }

    // ✅ Thêm chi tiết đơn hàng vào bảng Chitietdonhang
    public long addOrderDetails(int idDonHang, String masp, int soluong, float dongia, byte[] anh) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("id_dathang", idDonHang);
        values.put("masp", masp);
        values.put("soluong", soluong);
        values.put("dongia", dongia);
        values.put("anh", anh);

        long id = db.insert("Chitietdonhang", null, values);
        db.close();
        return id;
    }
}
