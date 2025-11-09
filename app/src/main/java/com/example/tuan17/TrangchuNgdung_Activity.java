package com.example.tuan17;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.tuan17.adapter.NhomSanPhamAdapter;
import com.example.tuan17.adapter.SanPhamAdapter;
import com.example.tuan17.helper.BottomBar_Helper;
import com.example.tuan17.models.NhomSanPham;
import com.example.tuan17.models.SanPham;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TrangchuNgdung_Activity extends AppCompatActivity {
    GridView grv2;
    GridView grv1;
    ArrayList<SanPham> mangSPgrv1; // Danh sách cho GridView
    ArrayList<NhomSanPham> mangNSPgrv2; // Danh sách cho ListView
    NhomSanPhamAdapter adapterGrv2;
    SanPhamAdapter adapterGrv1;
    String serverUrl = "http://10.0.2.2:3000"; // hoặc IP máy thật khi dùng điện thoại


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trangchu_ngdung);

        EditText timkiem = findViewById(R.id.timkiem);
        grv2 = findViewById(R.id.grv2);
        grv1 = findViewById(R.id.grv1);

        grv2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Lấy đối tượng nhóm sản phẩm từ adapter
                NhomSanPham nhomSanPham = mangNSPgrv2.get(position);

                if (nhomSanPham != null) {
                    // Chuyển đến DanhMucSanPham_Activity và truyền mã của nhóm sản phẩm
                    Intent intent = new Intent(TrangchuNgdung_Activity.this, DanhMucSanPham_Activity.class);
                    intent.putExtra("nhomSpId", nhomSanPham.getMa()); // Gửi mã nhóm sản phẩm
                    startActivity(intent);
                }
            }
        });

        BottomBar_Helper.setupBottomBar(this);
        timkiem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TrangchuNgdung_Activity.this, TimKiemSanPham_Activity.class);
                // Gửi tên đăng nhập qua Intent
//                intent.putExtra("tendn", tendn); // Sử dụng biến tendn đã được lấy từ SharedPreferences
                startActivity(intent);
            }
        });

        // Khởi tạo danh sách và adapter
        mangNSPgrv2 = new ArrayList<>();
        mangSPgrv1 = new ArrayList<>();

        adapterGrv2 = new NhomSanPhamAdapter(this, mangNSPgrv2, false);
        adapterGrv1 = new SanPhamAdapter(this, mangSPgrv1, false);

        grv2.setAdapter(adapterGrv2);
        grv1.setAdapter(adapterGrv1);

//        Loaddulieugridview2();
//        Loaddulieugridview1();
        // Gọi API backend
        loadNhomSanPhamAPI();
        loadSanPhamAPI();
    }


//    private void Loaddulieugridview2() {
//        mangNSPgrv2.clear();
//        mangNSPgrv2.addAll(nhomSanPhamDB.getRandomNhomSanPham(8));
//        adapterGrv2.notifyDataSetChanged(); // Cập nhật adapter
//    }
    private void loadNhomSanPhamAPI() {
        String url = serverUrl + "/nhomsanpham/random?limit=8";
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    mangNSPgrv2.clear();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            String ma = obj.getString("maso");
                            String ten = obj.getString("tennsp");
                            byte[] anh = android.util.Base64.decode(obj.getString("anh"), android.util.Base64.DEFAULT);
                            mangNSPgrv2.add(new NhomSanPham(ma, ten, anh));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    adapterGrv2.notifyDataSetChanged();
                },
                error -> Toast.makeText(this, "Không thể tải nhóm sản phẩm", Toast.LENGTH_SHORT).show()
        );
        Volley.newRequestQueue(this).add(request);
    }
//    private void Loaddulieugridview1() {
//        mangSPgrv1.clear();
//        mangSPgrv1.addAll(sanPhamDB.getRandomSanPham(8));
//        adapterGrv1.notifyDataSetChanged();
//    }
    private void loadSanPhamAPI() {
        String url = serverUrl + "/sanpham/random?limit=8";
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    mangSPgrv1.clear();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            String masp = obj.getString("masp");
                            String tensp = obj.getString("tensp");
                            float dongia = (float) obj.getDouble("dongia");
                            String mota = obj.getString("mota");
                            String ghichu = obj.getString("ghichu");
                            int soluongkho = obj.getInt("soluongkho");
                            String maso = obj.getString("maso");
                            byte[] anh = android.util.Base64.decode(obj.getString("anh"), android.util.Base64.DEFAULT);

                            mangSPgrv1.add(new SanPham(masp, tensp, dongia, mota, ghichu, soluongkho, maso, anh));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    adapterGrv1.notifyDataSetChanged();
                },
                error -> Toast.makeText(this, "Không thể tải sản phẩm", Toast.LENGTH_SHORT).show()
        );
        Volley.newRequestQueue(this).add(request);
    }
}
