package com.example.tuan17;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tuan17.adapter.SanPhamAdapter;
import com.example.tuan17.database.Database;
import com.example.tuan17.database.SanPhamDB;
import com.example.tuan17.helper.BottomBar_Admin_Helper;
import com.example.tuan17.models.SanPham;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class Sanpham_admin_Activity extends AppCompatActivity {

    private ListView lv;
    private FloatingActionButton addButton;
    private ArrayList<SanPham> mangSP;
    private SanPhamAdapter adapter;

//    private SanPhamDB sanPhamDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sanpham_admin);
//        sanPhamDB = new SanPhamDB(this);

        lv = findViewById(R.id.listtk);
        addButton = findViewById(R.id.btnthem);

        mangSP = new ArrayList<>();
        adapter = new SanPhamAdapter(Sanpham_admin_Activity.this, mangSP, true);
        lv.setAdapter(adapter);
        loadData();
        addButton.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), ThemSanPham_Activity.class);
            startActivity(intent);
        });
        BottomBar_Admin_Helper.setupBottomBar(this);
    }


//    private void loadData() {
//        String url = "http://10.0.2.2:3000/sanpham/all";
//        StringRequest request = new StringRequest(Request.Method.GET, url,
//                response -> {
//            try{
//                JSONArray jsonArray = new JSONArray(response);
//                mangSP.clear();
//                for( int i = 0; i< jsonArray.length(); i++){
//                    JSONObject obj = jsonArray.getJSONObject(i);
//                    String maso = obj.getString("maso");
//                    String tensp = obj.getString("tensp");
//                    String ghichu = obj.getString("ghichu");
//                    String dongia = obj.getString("dongia");
//                    String soluongkho = obj.getString("soluongkho");
//                    String anh = obj.getString("anh");
//                    byte[] imageBytes = Base64.decode(base64Image, Base64.DEFAULT);
//                    SanPham sp = new SanPham()
//                }
//            }
//                },
//                error -> {})
//
//        mangSP.clear();
//        mangSP.addAll(sanPhamDB.getAllSanPham());
//        adapter.notifyDataSetChanged();
//    }
private void loadData() {
    String url = "http://10.0.2.2:3000/sanpham/all";

    StringRequest request = new StringRequest(Request.Method.GET, url,
            response -> {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    mangSP.clear(); // xóa dữ liệu cũ nếu có

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);

                        String masp = obj.getString("masp");
                        String tensp = obj.getString("tensp");
                        String dongiaStr = obj.getString("dongia");
                        String mota = obj.getString("mota");
                        String ghichu = obj.getString("ghichu");
                        String soluongkhoStr = obj.getString("soluongkho");
                        String maso = obj.getString("maso");
                        String anhBase64 = obj.getString("anh");

                        // Parse dữ liệu
                        float dongia = Float.parseFloat(dongiaStr);
                        int soluongkho = Integer.parseInt(soluongkhoStr);
                        byte[] imageBytes = Base64.decode(anhBase64, Base64.DEFAULT);

                        // Tạo sản phẩm theo constructor đầy đủ
                        SanPham sp = new SanPham(
                                masp, tensp, dongia, mota, ghichu,
                                soluongkho, maso, imageBytes
                        );

                        mangSP.add(sp);
                    }

                    adapter.notifyDataSetChanged(); // cập nhật hiển thị

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Lỗi định dạng JSON", Toast.LENGTH_SHORT).show();
                }
            },
            error -> {
                error.printStackTrace();
                Toast.makeText(this, "Lỗi kết nối API", Toast.LENGTH_SHORT).show();
            });

    Volley.newRequestQueue(this).add(request);
}


    private byte[] convertBitmapToByteArray(int resourceId) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resourceId);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }
}