package com.example.tuan17.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.tuan17.models.ChiTietDonHang;
import com.example.tuan17.models.SanPham;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "banhang.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {


        // bang taikhoan
        db.execSQL("CREATE TABLE IF NOT EXISTS taikhoan (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "tendn TEXT UNIQUE NOT NULL, " +
                "matkhau TEXT NOT NULL, " +
                "email TEXT, " +
                "sdt TEXT, " +
                "hoten TEXT, " +
                "diachi TEXT, " +
                "quyen TEXT DEFAULT 'user', " +
                "ngaytao DATETIME DEFAULT CURRENT_TIMESTAMP)");

        db.execSQL("INSERT INTO taikhoan (tendn, matkhau, email, sdt, hoten, diachi, quyen) " +
                "VALUES ('admin', 'admin123', 'admin@example.com', '0123456789', 'Quản trị viên', 'Hà Nội', 'admin');");



        db.execSQL("CREATE TABLE IF NOT EXISTS nhomsanpham ("
                + "maso INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "tennsp NVARCHAR(200), "
                + "anh BLOB)");

        // Tạo bảng Chitietdonhang
        db.execSQL("CREATE TABLE IF NOT EXISTS Chitietdonhang (" +
                "id_chitiet INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id_dathang INTEGER, " +
                "masp INTEGER, " +
                "soluong INTEGER, " +
                "dongia REAL, " +
                "anh BLOB, " +
                "FOREIGN KEY(id_dathang) REFERENCES Dathang(id_dathang) ON DELETE CASCADE, " +// xóa khi dathang bị xóa
                "FOREIGN KEY(masp) REFERENCES sanpham(masp) ON DELETE CASCADE" + // xóa khi sản phẩm bị xóa
                ");");


//        Log.d("DatabaseHelper", "Tables created successfully");

// tạo bảng đơn hàng
        db.execSQL("CREATE TABLE IF NOT EXISTS Dathang (" +
                "id_dathang INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "tenkh TEXT, " +
                "diachi TEXT, " +
                "sdt TEXT, " +
                "tongthanhtoan REAL, " +
                "ngaydathang DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY(user_id) REFERENCES taikhoan(id) ON DELETE CASCADE" +
                ");");



        db.execSQL("CREATE TABLE IF NOT EXISTS sanpham(" +
                "masp INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "tensp NVARCHAR(200), " +
                "dongia FLOAT, " +
                "mota TEXT, " +
                "ghichu TEXT, " +
                "soluongkho INTEGER, " +
                "maso INTEGER, " +
                "anh BLOB, " +
                "FOREIGN KEY(maso) REFERENCES nhomsanpham(maso) ON DELETE SET NULL)");


        // bảng đánh giá
        db.execSQL("CREATE TABLE IF NOT EXISTS danhgia (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id_chitietdonhang INTEGER, " +
                "user_id INTEGER, " +
                "masp INTEGER, " +
                "rating INTEGER, " +
                "comment TEXT, " +
                "ngay_danhgia TEXT, " +
                "FOREIGN KEY(user_id) REFERENCES taikhoan(id), " +
                "FOREIGN KEY(id_chitietdonhang) REFERENCES Chitietdonhang(id_chitiet) ON DELETE CASCADE, " +
                "FOREIGN KEY(masp) REFERENCES sanpham(masp) ON DELETE CASCADE" +
                ");");

    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }



    public String getTenSanPhamByMaSp(int masp) {
        String tensp = null;
        SQLiteDatabase db = this.getReadableDatabase();

        // Thực hiện truy vấn
        Cursor cursor = db.rawQuery("SELECT tensp FROM sanpham WHERE masp = ?", new String[]{String.valueOf(masp)});

        // Kiểm tra cursor không null và di chuyển đến bản ghi đầu tiên
        if (cursor != null && cursor.moveToFirst()) {
            // Lấy tên sản phẩm từ cursor
            int tenspIndex = cursor.getColumnIndex("tensp");
            if (tenspIndex != -1) {
                tensp = cursor.getString(tenspIndex);
            } else {
                Log.e("Database Error", "Column 'tensp' not found.");
            }
        } else {
            Log.e("Database Error", "Cursor is empty or null.");
        }

        // Đóng cursor
        if (cursor != null) {
            cursor.close();
        }

        return tensp; // Trả về tên sản phẩm
    }

    public List<SanPham> getProductsByNhomSpId(String nhomSpId) {
        List<SanPham> productList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Truy vấn để lấy sản phẩm theo nhomSpId
        Cursor cursor = db.rawQuery("SELECT * FROM sanpham WHERE maso = ?", new String[]{nhomSpId});

        if (cursor.moveToFirst()) {
            do {
                String masp = cursor.getString(cursor.getColumnIndexOrThrow("masp"));
                String tensp = cursor.getString(cursor.getColumnIndexOrThrow("tensp"));
                Float dongia = cursor.getFloat(cursor.getColumnIndexOrThrow("dongia"));
                String mota = cursor.getString(cursor.getColumnIndexOrThrow("mota"));
                String ghichu = cursor.getString(cursor.getColumnIndexOrThrow("ghichu"));
                int soluongkho = cursor.getInt(cursor.getColumnIndexOrThrow("soluongkho"));
                String mansp = cursor.getString(cursor.getColumnIndexOrThrow("maso"));
                byte[] anh = cursor.getBlob(cursor.getColumnIndexOrThrow("anh"));

                SanPham sanPham = new SanPham(masp, tensp, dongia, mota, ghichu, soluongkho, mansp, anh);
                productList.add(sanPham);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return productList;
    }

    // Phương thức tìm kiếm sản phẩm theo tên
    public ArrayList<SanPham> searchSanPhamByName(String name) {
        ArrayList<SanPham> sanPhamList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

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
    public int getUserIdByChiTietDonHangId(int idChiTietDonHang) {
        int userId = -1;
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT d.user_id FROM Dathang d " +
                "INNER JOIN Chitietdonhang ct ON d.id_dathang = ct.id_dathang " +
                "WHERE ct.id_chitiet = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(idChiTietDonHang)});
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"));
            }
            cursor.close();
        }
        return userId;
    }


}