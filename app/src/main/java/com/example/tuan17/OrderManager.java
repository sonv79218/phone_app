package com.example.tuan17;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tuan17.database.ChiTietDonHangDB;
import com.example.tuan17.database.DatabaseHelper;
import com.example.tuan17.helper.SharedPrefHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

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
//    public long addOrder(String tenKh, String diaChi, String sdt, float tongTien) {
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//        ContentValues values = new ContentValues();
//// tự động tạo id
//        int userId = SharedPrefHelper.getUserId(context); // Lấy user_id
//        values.put("user_id", userId);
//        values.put("tenkh", tenKh);
//        values.put("diachi", diaChi);
//        values.put("sdt", sdt);
//        values.put("tongthanhtoan", tongTien);
//// ngày đặt hàng đc tạo tự động
//        long id = db.insert("Dathang", null, values);
//        db.close();
//        return id;
//    }
    public void addOrder(String tenKh, String diaChi, String sdt, float tongTien, Consumer<Integer> callback) {
        String url = "http://10.0.2.2:3000/dathang";

        int userId = SharedPrefHelper.getUserId(context);

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        int id = obj.getInt("id");
                        callback.accept(id); // Gọi callback khi thành công
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callback.accept(-1);
                    }
                },
                error -> {
                    error.printStackTrace();
                    callback.accept(-1); // Lỗi
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> data = new HashMap<>();
                data.put("user_id", String.valueOf(userId));
                data.put("tenkh", tenKh);
                data.put("diachi", diaChi);
                data.put("sdt", sdt);
                data.put("tongthanhtoan", String.valueOf(tongTien));
                return data;
            }
        };

        Volley.newRequestQueue(context).add(request);
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
