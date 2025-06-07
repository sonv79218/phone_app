package com.example.tuan17.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;


import com.example.tuan17.models.NhomSanPham;
import com.example.tuan17.models.SanPham;

import java.util.ArrayList;

public class SanPhamDB {
    private SQLiteDatabase db;
    public SanPhamDB(Context context){
        if(context == null){
            throw new IllegalArgumentException("Context khong duoc null");

        }
        DatabaseHelper helper = new DatabaseHelper(context);
        db = helper.getWritableDatabase();

    }

    public ArrayList<SanPham> getRandomSanPham(int limit){
        ArrayList<SanPham> list = new ArrayList<>();
        Cursor sp = db.rawQuery("SELECT * FROM sanpham ORDER BY RANDOM() LIMIT ?", new String[]{String.valueOf(limit)});
//        Cursor sp = database.GetData("SELECT * FROM sanpham order by random() limit 8");

        if (sp != null && sp.moveToFirst()) {
            do {
                String masp = sp.getString(0);
                String tensp = sp.getString(1);
                float dongia = sp.getFloat(2); // Giữ nguyên là float
                String mota = sp.getString(3);
                String ghichu = sp.getString(4);
                int soluongkho = sp.getInt(5); // Giữ nguyên là int
                String maso = sp.getString(6);
                byte[] blob = sp.getBlob(7);
                list.add(new SanPham(masp,tensp,dongia,mota,ghichu,soluongkho,maso,blob));
            } while (sp.moveToNext());
        }
        sp.close();
        return list;
    }
    public ArrayList<SanPham> searchSanPhamByName(String name) {
        ArrayList<SanPham> sanPhamList = new ArrayList<>();
//        SQLiteDatabase db = this.getReadableDatabase();

        // Sử dụng LIKE để tìm kiếm gần đúng
        String query = "SELECT * FROM sanpham WHERE tensp LIKE ?";
        Cursor cursor = db.rawQuery(query, new String[]{"%" + name + "%"});

        if (cursor.moveToFirst()) {
            do {
                // Lấy chỉ số cột
                int maspIndex = cursor.getColumnIndex("masp");
                int tenspIndex = cursor.getColumnIndex("tensp");
                int dongiaIndex = cursor.getColumnIndex("dongia");
                int motaIndex = cursor.getColumnIndex("mota");
                int ghichuIndex = cursor.getColumnIndex("ghichu");
                int soluongkhoIndex = cursor.getColumnIndex("soluongkho");
                int manhomsanphamIndex = cursor.getColumnIndex("maso");
                int anhIndex = cursor.getColumnIndex("anh");

                // Kiểm tra và lấy giá trị
                if (maspIndex != -1 && tenspIndex != -1) {
                    String masp = cursor.getString(maspIndex);
                    String tensp = cursor.getString(tenspIndex);
                    float dongia = (dongiaIndex != -1) ? cursor.getFloat(dongiaIndex) : 0.0f; // Sửa lại kiểu dữ liệu thành float
                    String mota = (motaIndex != -1) ? cursor.getString(motaIndex) : "";
                    String ghichu = (ghichuIndex != -1) ? cursor.getString(ghichuIndex) : "";
                    int soluongkho = (soluongkhoIndex != -1) ? cursor.getInt(soluongkhoIndex) : 0;
                    String manhomsanpham = (manhomsanphamIndex != -1) ? cursor.getString(manhomsanphamIndex) : "";
                    byte[] anh = (anhIndex != -1) ? cursor.getBlob(anhIndex) : null;

                    SanPham sanPham = new SanPham(masp, tensp, dongia, mota, ghichu, soluongkho, manhomsanpham, anh);
                    sanPhamList.add(sanPham);
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return sanPhamList;
    }

    public ArrayList<SanPham> getAllSanPham(){
        ArrayList<SanPham> list = new ArrayList<>();
        Cursor sp = db.rawQuery("SELECT * FROM sanpham",new String[]{});

        if (sp != null && sp.moveToFirst()) {
            do {
                String masp = sp.getString(0);
                String tensp = sp.getString(1);
                float dongia = sp.getFloat(2); // Giữ nguyên là float
                String mota = sp.getString(3);
                String ghichu = sp.getString(4);
                int soluongkho = sp.getInt(5); // Giữ nguyên là int
                String maso = sp.getString(6);
                byte[] blob = sp.getBlob(7);
                list.add(new SanPham(masp,tensp,dongia,mota,ghichu,soluongkho,maso,blob));
            } while (sp.moveToNext());
        }
        sp.close();
        return list;
    }
}

