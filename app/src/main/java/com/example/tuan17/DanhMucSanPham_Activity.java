package com.example.tuan17;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.tuan17.adapter.SanPham_DanhMuc_Adapter;
import com.example.tuan17.adapter.SanPham_DanhMuc_Admin_Adapter;
//import com.example.tuan17.database.DatabaseHelper;
//import com.example.tuan17.database.SanPhamDB;
import com.example.tuan17.helper.BottomBar_Helper;
import com.example.tuan17.models.SanPham;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList; // Import ArrayList
import java.util.List;

public class DanhMucSanPham_Activity extends AppCompatActivity {
    private GridView grv;
    private ArrayList<SanPham> productList; // Change to ArrayList
    private SanPham_DanhMuc_Adapter productAdapter;
//    private DatabaseHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_danh_muc_san_pham);
        BottomBar_Helper.setupBottomBar(this);
        // Initialize the GridView and DatabaseHelper
        grv = findViewById(R.id.grv);
//        dbHelper = new DatabaseHelper(this);

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
            Intent intent = new Intent(DanhMucSanPham_Activity.this, ChiTietSanPham_Admin_Activity.class);

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
                            // Xử lý masp: nếu không có thì dùng maso, nếu maso cũng null thì dùng index
                            String masp = obj.optString("masp", null);
                            if (masp == null || masp.equals("null")) {
                                Object masoObj = obj.opt("maso");
                                if (masoObj != null && !masoObj.toString().equals("null")) {
                                    masp = String.valueOf(masoObj);
                                } else {
                                    masp = "SP" + i;
                                }
                            }
                            
                            String tensp = obj.optString("tensp", "");
                            // Xử lý dongia: có thể là string hoặc number
                            float dongia = 0;
                            if (obj.has("dongia") && !obj.isNull("dongia")) {
                                if (obj.get("dongia") instanceof String) {
                                    dongia = Float.parseFloat(obj.getString("dongia"));
                                } else {
                                    dongia = (float) obj.getDouble("dongia");
                                }
                            }
                            String mota = obj.optString("mota", "");
                            String ghichu = obj.optString("ghichu", "");
                            int soluongkho = obj.optInt("soluongkho", 0);
                            // Xử lý maso có thể null
                            Object masoObj = obj.opt("maso");
                            String masoSp = (masoObj != null && !masoObj.toString().equals("null")) ? String.valueOf(masoObj) : null;
                            // Xử lý picurl có thể null
                            String anh = obj.optString("picurl", null);
                            if (anh != null && (anh.equals("null") || anh.isEmpty())) {
                                anh = null;
                            }

                            SanPham sp = new SanPham(masp, tensp, dongia, mota, ghichu, soluongkho, masoSp, anh);
                            productList.add(sp);
                        }

                        productAdapter = new SanPham_DanhMuc_Adapter(this, productList, false);
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
