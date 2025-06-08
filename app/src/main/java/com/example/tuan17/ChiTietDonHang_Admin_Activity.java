package com.example.tuan17;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.example.tuan17.adapter.ChiTietDonHangAdapter;
import com.example.tuan17.adapter.ChiTietDonHang_admin_adapter;
import com.example.tuan17.database.ChiTietDonHangDB;
import com.example.tuan17.database.Database;
import com.example.tuan17.database.DatabaseHelper;
import com.example.tuan17.helper.BottomBar_Admin_Helper;
import com.example.tuan17.models.ChiTietDonHang;

import java.util.List;

public class ChiTietDonHang_Admin_Activity extends AppCompatActivity {

//    DatabaseHelper dbdata;
//    Database database;
    ListView listViewChiTiet; // Danh sách hiển thị chi tiết đơn hàng
    ChiTietDonHang_admin_adapter chiTietAdapter; // Adapter để hiển thị chi tiết

    private ChiTietDonHangDB chiTietDonHangDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chi_tiet_don_hang_admin);


        chiTietDonHangDB = new ChiTietDonHangDB(this);

        // Khởi tạo ListView để hiển thị chi tiết đơn hàng
        listViewChiTiet = findViewById(R.id.listtk); // Đảm bảo rằng bạn đã định nghĩa ListView trong layout

        // Lấy ID đơn hàng từ Intent
        String donHangIdStr = getIntent().getStringExtra("donHangId");

        if (donHangIdStr != null) {
            try {
                // Chuyển đổi chuỗi donHangId thành kiểu int
                int donHangId = Integer.parseInt(donHangIdStr);

                // Lấy chi tiết đơn hàng từ database
                List<ChiTietDonHang> chiTietList = chiTietDonHangDB.getChiTietByOrderId(donHangId);

                // Kiểm tra danh sách chi tiết
                if (chiTietList != null && !chiTietList.isEmpty()) {
                    // Sử dụng adapter để hiển thị chi tiết đơn hàng
                    chiTietAdapter = new ChiTietDonHang_admin_adapter(this, chiTietList);
                    listViewChiTiet.setAdapter(chiTietAdapter); // Gán adapter cho ListView
                } else {
                    Toast.makeText(this, "Không tìm thấy chi tiết cho đơn hàng!", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "ID đơn hàng không hợp lệ!", Toast.LENGTH_SHORT).show();
            }
        }

BottomBar_Admin_Helper.setupBottomBar(this);
    }

}