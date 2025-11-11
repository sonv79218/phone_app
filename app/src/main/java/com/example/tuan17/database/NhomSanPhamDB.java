//package com.example.tuan17.database;
//
//import android.content.Context;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//
//import com.example.tuan17.models.NhomSanPham;
//import com.example.tuan17.models.SanPham;
//
//import java.util.ArrayList;
//
//public class NhomSanPhamDB {
//    private SQLiteDatabase db;
//    public NhomSanPhamDB(Context context){
//        if(context == null){
//            throw new IllegalArgumentException("Context khong duoc null");
//
//        }
//        DatabaseHelper helper = new DatabaseHelper(context);
//        db = helper.getWritableDatabase();
//
//    }
//    public ArrayList<NhomSanPham> getRandomNhomSanPham(int limit){
//        ArrayList<NhomSanPham> list = new ArrayList<>();
//        Cursor cursor = db.rawQuery("SELECT * FROM nhomsanpham ORDER BY RANDOM() LIMIT ?", new String[]{String.valueOf(limit)});
//        if (cursor != null && cursor.moveToFirst()){
//            do{
//                String ma = cursor.getString(0);
//                String ten = cursor.getString(1);
//                byte[] anh = cursor.getBlob(2);
//                list.add((new NhomSanPham(ma,ten,anh)));
//            } while (cursor.moveToNext());
//        }
//        cursor.close();
//        return list;
//    }
//
//    public ArrayList<NhomSanPham> getAllNhomSanPham(){
//        ArrayList<NhomSanPham> list = new ArrayList<>();
//        Cursor cursor = db.rawQuery("SELECT * FROM nhomsanpham",new String[]{});
//        if (cursor != null && cursor.moveToFirst()){
//            do{
//                String ma = cursor.getString(0);
//                String ten = cursor.getString(1);
//                byte[] anh = cursor.getBlob(2);
//                list.add(new NhomSanPham(ma,ten,anh));
//            } while (cursor.moveToNext());
//        }
//        cursor.close();
//        return list;
//
//    }
//
//}
