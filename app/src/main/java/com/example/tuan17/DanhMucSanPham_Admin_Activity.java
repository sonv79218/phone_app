package com.example.tuan17;

import android.content.Intent;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.tuan17.adapter.SanPham_DanhMuc_Admin_Adapter;
import com.example.tuan17.database.DatabaseHelper;
import com.example.tuan17.helper.BottomBar_Admin_Helper;
import com.example.tuan17.models.SanPham;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DanhMucSanPham_Admin_Activity extends AppCompatActivity {
    private GridView grv;
    private ArrayList<SanPham> productList; // Change to ArrayList
    private SanPham_DanhMuc_Admin_Adapter productAdapter;
//    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_danh_muc_san_pham_admin);

        // Initialize the GridView and DatabaseHelper
        BottomBar_Admin_Helper.setupBottomBar(this);
        grv = findViewById(R.id.grv);
//        dbHelper = new DatabaseHelper(this);
        // Retrieve nhomSpId from the Intent
        String nhomSpId = getIntent().getStringExtra("nhomSpId");

        if (nhomSpId != null) {
            loadSanPhamTheoNhom(nhomSpId);
        } else {
            Toast.makeText(this, "ID nhóm sản phẩm không hợp lệ!", Toast.LENGTH_SHORT).show();
        }


        grv.setOnItemClickListener((parent, view, position, id) -> {
            // Lấy sản phẩm tại vị trí được click
            SanPham sanPham = productList.get(position);

            // Tạo Intent để chuyển sang ChiTietSanPham_Activity
            Intent intent = new Intent(DanhMucSanPham_Admin_Activity.this, ChiTietSanPham_Admin_Activity.class);

            // Truyền dữ liệu sản phẩm qua Intent
            intent.putExtra("masp", sanPham.getMasp());
            intent.putExtra("tensp", sanPham.getTensp());
            intent.putExtra("dongia", sanPham.getDongia());
            intent.putExtra("mota", sanPham.getMota());
            intent.putExtra("ghichu", sanPham.getGhichu());
            intent.putExtra("soluongkho", sanPham.getSoluongkho());
            intent.putExtra("maso", sanPham.getMansp());
            intent.putExtra("anh", sanPham.getAnh());

            // Chuyển đến trang ChiTietSanPham_Activity
            startActivity(intent);
        });

    }
    private void loadSanPhamTheoNhom(String maso) {
        String url = "http://10.0.2.2:3000/sanpham/nhom/" + maso;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray dataArray = response.getJSONArray("data");
                        productList = new ArrayList<>();

                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject obj = dataArray.getJSONObject(i);
                            String masp = obj.getString("masp");
                            String tensp = obj.getString("tensp");
                            float dongia = (float) obj.getDouble("dongia");
                            String mota = obj.getString("mota");
                            String ghichu = obj.getString("ghichu");
                            int soluongkho = obj.getInt("soluongkho");
                            String masoSp = obj.getString("maso");
                            String anhBase64 = obj.optString("anh", null);
                            byte[] anhBytes = (anhBase64 != null) ? android.util.Base64.decode(anhBase64, android.util.Base64.DEFAULT) : null;

                            SanPham sp = new SanPham(masp, tensp, dongia, mota, ghichu, soluongkho, masoSp, anhBytes);
                            productList.add(sp);
                        }

                        productAdapter = new SanPham_DanhMuc_Admin_Adapter(this, productList, false);
                        grv.setAdapter(productAdapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Lỗi định dạng JSON", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Lỗi kết nối đến API", Toast.LENGTH_SHORT).show();
                }
        );

        Volley.newRequestQueue(this).add(request);
    }

}
