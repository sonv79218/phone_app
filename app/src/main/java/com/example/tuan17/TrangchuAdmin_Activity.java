package com.example.tuan17;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.tuan17.adapter.NhomSanPham_trangChuadmin_Adapter;
import com.example.tuan17.database.Database;
import com.example.tuan17.database.NhomSanPhamDB;
import com.example.tuan17.database.SanPhamDB;
import com.example.tuan17.helper.BottomBar_Admin_Helper;
import com.example.tuan17.models.NhomSanPham;
import com.example.tuan17.models.SanPham;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TrangchuAdmin_Activity extends AppCompatActivity {

    GridView grv2;
    GridView grv1;
    ArrayList<SanPham> mangSPgrv1; // Danh sách cho GridView

    ArrayList<NhomSanPham> mangNSPgrv2; // Danh sách cho ListView

    NhomSanPham_trangChuadmin_Adapter adapterGrv2;
    SanPham_TrangChuAdmin_Adapter adapterGrv1;
//    Database database;
//    SanPhamDB sanPhamDB;
//    NhomSanPhamDB nhomSanPhamDB;
    String serverUrl = "http://10.0.2.2:3000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trangchu_admin);
//        nhomSanPhamDB = new NhomSanPhamDB(this); // truyền Context vào constructor
//        sanPhamDB = new SanPhamDB(this);
        grv2 = findViewById(R.id.grv2);
        grv1 = findViewById(R.id.grv1);

        grv2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Lấy đối tượng nhóm sản phẩm từ adapter
                NhomSanPham nhomSanPham = mangNSPgrv2.get(position);
                if (nhomSanPham != null) {
                    // Chuyển đến DanhMucSanPham_Activity và truyền mã của nhóm sản phẩm
                    Intent intent = new Intent(TrangchuAdmin_Activity.this, DanhMucSanPham_Admin_Activity.class);
                    intent.putExtra("nhomSpId", nhomSanPham.getMa()); // Gửi mã nhóm sản phẩm
                    startActivity(intent);
                }
            }
        });
        BottomBar_Admin_Helper.setupBottomBar(this);

        grv2=findViewById(R.id.grv2);
        grv1=findViewById(R.id.grv1);
        mangNSPgrv2= new ArrayList<>(); // Khởi tạo danh sách
        mangSPgrv1= new ArrayList<>(); // Khởi tạo danh sách
        // gắn vào adapter
        adapterGrv2 = new NhomSanPham_trangChuadmin_Adapter(this, mangNSPgrv2, false) ;
        grv2.setAdapter(adapterGrv2);

        adapterGrv1= new SanPham_TrangChuAdmin_Adapter(this, mangSPgrv1, false) ;
        grv1.setAdapter(adapterGrv1);
        loadNhomSanPhamAPI();
        loadSanPhamAPI();
//
//        Loaddulieugridview2();
//        Loaddulieugridview1();
    }
//    private void Loaddulieugridview2() {
//        mangNSPgrv2.clear();
//        mangNSPgrv2.addAll(nhomSanPhamDB.getRandomNhomSanPham(8));
//        adapterGrv2.notifyDataSetChanged(); // Cập nhật adapter
//    }

//    private void Loaddulieugridview1() {
//        mangSPgrv1.clear();
//        mangSPgrv1.addAll(sanPhamDB.getRandomSanPham(8));
//        adapterGrv1.notifyDataSetChanged();
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
//                            Log.d("tag",ma);
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