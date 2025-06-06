package com.example.tuan17.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.example.tuan17.DoiMatKhau_Activity;
import com.example.tuan17.ThemTaiKhoan_Activity;
import com.example.tuan17.models.TaiKhoan;

import java.util.ArrayList;
import java.util.List;

public class TaiKhoanDB {
    private final SQLiteDatabase db;
    public TaiKhoanDB(Context context){
        if(context == null){
            throw new IllegalArgumentException("Context không được null");
        }
        DatabaseHelper helper = new DatabaseHelper(context);
        db = helper.getWritableDatabase();  // Kết nối DB đã tạo bảng ở DatabaseHelper
    }

    public boolean checkLogin(String username, String password) {
//        SQLiteDatabase db = db.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM taikhoan WHERE tendn = ? AND matkhau = ?",
                new String[]{username, password});
        boolean isValid = cursor.getCount() > 0;
        cursor.close();
        return isValid;
    }

    public String getQuyenByUsername(String username) {
        String quyen = "";
//        SQLiteDatabase db = db.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT quyen FROM taikhoan WHERE tendn = ?",
                new String[]{username});

        if (cursor.moveToFirst()) {
            int index = cursor.getColumnIndex("quyen");
            if (index != -1) {
                quyen = cursor.getString(index);
            }
        }
        cursor.close();
        return quyen;
    }

    public Cursor GetData(String sql){
//        SQLiteDatabase database=getReadableDatabase();
        return db.rawQuery(sql,null);
    }



    // hàm lấy dữ liệu mảng tài khoản từ db
    public List<TaiKhoan> getAllTaiKhoan() {
        List<TaiKhoan> danhSach = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM taikhoan", null);

        if (cursor.moveToFirst()) {
            do {
                String tdn = cursor.getString(0);
                String mk = cursor.getString(1);
                String q = cursor.getString(2);
                danhSach.add(new TaiKhoan(tdn, mk, q));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return danhSach;
    }

    public boolean addTaiKhoan(String username, String password, String quyen){
//        db.rawQuery("INSERT INTO taikhoan VALUES('" + username + "', '" + password + "', '" + quyen + "')");
        try {
            db.execSQL("INSERT INTO taikhoan (tendn, matkhau, quyen) VALUES (?, ?, ?)",
                    new Object[]{username, password, quyen});
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean dangky(String username, String password){
        try {
            String quyen = "user";
            db.execSQL("INSERT INTO taikhoan (tendn, matkhau, quyen) VALUES (?, ?, ?)",
                    new Object[]{username, password,quyen});
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }
//public boolean suaTaiKhoan(String username, String password , String quyen){
//    if (username == null || username.trim().isEmpty()
//            || password == null || password.trim().isEmpty()
//            || quyen == null || quyen.trim().isEmpty()) {
//        // Thiếu thông tin
//        return false;
//    }
//        try{
//            db.execSQL("UPDATE taikhoan SET matkhau = ?, quyen = ? WHERE tendn = ?",
//                    new Object[]{password, quyen, username});
//
//            // nếu tên đn trùng
//            // nếu sửa không thay đổi
//            // nếu thiếu thông tin ...
//            return true;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//}
public boolean suaTaiKhoan(String username, String password, String quyen) {
    try {
        ContentValues values = new ContentValues();
        values.put("matkhau", password);
        values.put("quyen", quyen);

        int rows = db.update("taikhoan", values, "tendn = ?", new String[]{username});
        return rows > 0;
    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}

public boolean xoaTaiKhoan(String username) {
        try {
            int rows = db.delete("taikhoan", "tendn = ?", new String[]{username});
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean doiMatKhau(String username, String password) {
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM taikhoan WHERE tendn = ?", new String[]{username});
            if (cursor.getCount() <= 0) {
                cursor.close();
                return false; // Không tìm thấy tài khoản
            }
            cursor.close();

            ContentValues values = new ContentValues();
            values.put("matkhau", password);
            int rows = db.update("taikhoan", values, "tendn = ?", new String[]{username});
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


}
