package com.example.tuan17;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.tuan17.adapter.ChiTietDonHangAdapter;
import com.example.tuan17.database.ChiTietDonHangDB;
import com.example.tuan17.database.Database;
import com.example.tuan17.database.DatabaseHelper;
import com.example.tuan17.database.DonHangDB;
import com.example.tuan17.helper.BottomBar_Helper;
import com.example.tuan17.models.ChiTietDonHang;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ChiTietDonHang_Activity extends AppCompatActivity {

//    DatabaseHelper dbdata;
//    Database database;
    ListView listViewChiTiet; // Danh sách hiển thị chi tiết đơn hàng
    ChiTietDonHangAdapter chiTietAdapter; // Adapter để hiển thị chi tiết

//    private ChiTietDonHangDB chiTietDonHangDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chi_tiet_don_hang);
        BottomBar_Helper.setupBottomBar(this);

//        chiTietDonHangDB = new ChiTietDonHangDB(this);

        // Khởi tạo ListView để hiển thị chi tiết đơn hàng
        listViewChiTiet = findViewById(R.id.listtk); // Đảm bảo rằng bạn đã định nghĩa ListView trong layout

        // Lấy ID đơn hàng từ Intent
        String donHangIdStr = getIntent().getStringExtra("donHangId");
        if (donHangIdStr != null) {
            try {
                int donHangId = Integer.parseInt(donHangIdStr);
                loadChiTietDonHangFromAPI(donHangId);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "ID đơn hàng không hợp lệ!", Toast.LENGTH_SHORT).show();
            }
        }

    }
    private void loadChiTietDonHangFromAPI(int orderId) {
        String url = "http://10.0.2.2:3000/chitietdathang/" + orderId;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    List<ChiTietDonHang> chiTietList = new ArrayList<>();

                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            int id_chitiet = obj.getInt("id_chitiet");
                            int masp = obj.getInt("masp");
                            int soluong = obj.getInt("soluong");
                            float dongia = (float) obj.getDouble("dongia");
                            byte[] anh = null;

                            if (!obj.isNull("anh")) {
                                String base64Image = obj.getString("anh");
                                anh = Base64.decode(base64Image, Base64.DEFAULT);
                            }

                            chiTietList.add(new ChiTietDonHang(id_chitiet, masp, soluong, dongia, anh));
                            Log.d("abc","mes " +anh);// đã có ảnh
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    if (chiTietList.isEmpty()) {
                        Toast.makeText(this, "Không có chi tiết đơn hàng!", Toast.LENGTH_SHORT).show();
                    } else {
                        chiTietAdapter = new ChiTietDonHangAdapter(this, chiTietList);
                        listViewChiTiet.setAdapter(chiTietAdapter);
                    }
                },
                error -> {
                    Toast.makeText(this, "Lỗi khi tải chi tiết đơn hàng", Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                }
        );

        Volley.newRequestQueue(this).add(request);
    }


}
