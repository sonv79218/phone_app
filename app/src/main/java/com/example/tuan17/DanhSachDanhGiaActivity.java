package com.example.tuan17;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.tuan17.adapter.DanhGiaAdapter;
import com.example.tuan17.models.DanhGia;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DanhSachDanhGiaActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DanhGiaAdapter adapter;
    private ArrayList<DanhGia> danhGiaList = new ArrayList<>();

    private int masp, chitietdonhangId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_danhsach_danhgia);

        recyclerView = findViewById(R.id.recyclerViewDanhGia);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DanhGiaAdapter(this, danhGiaList);
        recyclerView.setAdapter(adapter);

        // Lấy dữ liệu
        masp = getIntent().getIntExtra("masp", -1);
        chitietdonhangId = getIntent().getIntExtra("chitietdonhangId", -1);

        if (chitietdonhangId != -1) {
            loadDanhGiaTheoDonHang(chitietdonhangId);
        } else if (masp != -1) {
            loadDanhSachDanhGiaTheoMasp(masp);
        } else {
            Toast.makeText(this, "Không có mã sản phẩm hoặc chi tiết đơn hàng!", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadDanhGiaTheoDonHang(int id) {
        String url = "http://10.0.2.2:3000/danhgia/lay?id_chitietdonhang=" + id;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.getBoolean("success")) {
                            JSONObject data = response.getJSONObject("data");
                            int rating = data.getInt("rating");
                            String comment = data.getString("comment");
                            String ngay = data.getString("ngay_danhgia");

                            danhGiaList.clear();
                            danhGiaList.add(new DanhGia(rating, comment, ngay));
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(this, "Không có đánh giá!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Lỗi xử lý dữ liệu!", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Lỗi kết nối máy chủ!", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(this).add(request);
    }
    private void loadDanhSachDanhGiaTheoMasp(int masp) {
        String url = "http://10.0.2.2:3000/danhgia/sanpham/" + masp;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    danhGiaList.clear();
                    try {
                        if (response.getBoolean("success")) {
                            JSONArray dataArray = response.getJSONArray("data");
                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject obj = dataArray.getJSONObject(i);
                                int rating = obj.getInt("rating");
                                String comment = obj.getString("comment");
                                String ngay = obj.getString("ngay_danhgia");

                                // Cắt lấy phần ngày (bỏ T...Z)
                                ngay = ngay.split("T")[0];

                                danhGiaList.add(new DanhGia(rating, comment, ngay));
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(this, "Không có đánh giá!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Lỗi xử lý dữ liệu!", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Không thể kết nối đến máy chủ!", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(this).add(request);
    }

}
