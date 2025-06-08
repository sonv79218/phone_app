package com.example.tuan17.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.tuan17.models.DanhGia;

import java.util.ArrayList;
import java.util.List;

public class DanhGiaDB {
    private final SQLiteDatabase db;

    public DanhGiaDB(Context context) {
        DatabaseHelper helper = new DatabaseHelper(context);
        db = helper.getWritableDatabase();
    }

    public boolean themDanhGia(DanhGia danhGia) {
        try {
            ContentValues values = new ContentValues();
            values.put("user_id", danhGia.getUserId());
            values.put("masp", danhGia.getProductId());
            values.put("id_chitietdonhang", danhGia.getChitietdonhangId());
            values.put("rating", danhGia.getRating());
            values.put("comment", danhGia.getComment());
            values.put("ngay_danhgia", danhGia.getNgayDanhGia());

            long result = db.insert("danhgia", null, values);
            return result != -1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
//    private void loadDanhGia(int productId) {
//        DatabaseHelper db = new DatabaseHelper(this);
//        SQLiteDatabase database = db.getReadableDatabase();
//
//        Cursor cursor = database.rawQuery("SELECT * FROM danhgia WHERE product_id = ?", new String[]{String.valueOf(productId)});
//        danhGiaList.clear();
//
//        if (cursor.moveToFirst()) {
//            do {
//                DanhGia dg = new DanhGia();
//                dg.setNgay(cursor.getString(cursor.getColumnIndex("ngay_danhgia")));
//                dg.setRating(cursor.getInt(cursor.getColumnIndex("rating")));
//                dg.setComment(cursor.getString(cursor.getColumnIndex("comment")));
//                // Add user_id or other info if needed
//                danhGiaList.add(dg);
//            } while (cursor.moveToNext());
//        }
//
//        cursor.close();
//        database.close();
//        danhGiaAdapter.notifyDataSetChanged();
//    }

    // kiểm tra đánh giá
    public boolean daDanhGia(int userId, int sanPhamId, int chiTietDonHangId) {
//        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT 1 FROM danhgia WHERE user_id = ? AND masp = ? AND id_chitietdonhang = ?",
                new String[]{String.valueOf(userId), String.valueOf(sanPhamId), String.valueOf(chiTietDonHangId)}
        );

        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    public DanhGia layDanhGia(int userId, int productId, int chitietDonhangId) {
//        SQLiteDatabase db = this.getReadableDatabase();
        DanhGia danhGia = null;

        Cursor cursor = db.rawQuery("SELECT * FROM danhgia WHERE user_id = ? AND  masp = ? AND id_chitietdonhang = ?",
                new String[]{String.valueOf(userId), String.valueOf(productId), String.valueOf(chitietDonhangId)});

        if (cursor != null && cursor.moveToFirst()) {
            int rating = cursor.getInt(cursor.getColumnIndexOrThrow("rating"));
            String comment = cursor.getString(cursor.getColumnIndexOrThrow("comment"));
            String ngay = cursor.getString(cursor.getColumnIndexOrThrow("ngay_danhgia"));

            danhGia = new DanhGia(userId, productId, chitietDonhangId, rating, comment, ngay);
            cursor.close();
        }

        return danhGia;
    }

    public List<DanhGia> layDanhSachDanhGia(int productId) {
        List<DanhGia> danhGiaList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM danhgia WHERE product_id = ?", new String[]{String.valueOf(productId)});
        if (cursor.moveToFirst()) {
            do {
//                int userId = cursor.getInt(cursor.getColumnIndex("user_id"));
                int userId = getIntFromCursor(cursor, "user_id");
                int chitietdonhangId = getIntFromCursor(cursor, "id_chitietdonhang");
//                int rating = cursor.getInt(cursor.getColumnIndex("rating"));
                int rating = getIntFromCursor(cursor, "rating");
//                String comment = cursor.getString(cursor.getColumnIndex("comment"));
                String comment = getStringFromCursor(cursor, "comment");
//                String ngay = cursor.getString(cursor.getColumnIndex("ngay_danhgia"));
                String ngay = getStringFromCursor(cursor,"ngay_danhgia");
                danhGiaList.add(new DanhGia(userId, productId,chitietdonhangId, rating, comment, ngay));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return danhGiaList;
    }
    public double tinhTrungBinhSoSao(int sanPhamId) {
        double trungBinh = 0;
        Cursor cursor = db.rawQuery(
                "SELECT AVG(soSao) as trungBinh FROM danhgia WHERE sanPhamId = ?",
                new String[]{String.valueOf(sanPhamId)}
        );

        if (cursor.moveToFirst()) {
            int index = cursor.getColumnIndex("trungBinh");
            if (index >= 0) {
                trungBinh = cursor.getDouble(index);
            }
        }
        cursor.close();
        return trungBinh;
    }


    private String getStringFromCursor(Cursor cursor, String columnName) {
        int index = cursor.getColumnIndex(columnName);
        if (index >= 0) {
            return cursor.getString(index);
        }
        return null; // hoặc giá trị mặc định
    }

    private int getIntFromCursor(Cursor cursor, String columnName) {
        int index = cursor.getColumnIndex(columnName);
        if (index >= 0) {
            return cursor.getInt(index);
        }
        return -1; // hoặc giá trị mặc định
    }

}
