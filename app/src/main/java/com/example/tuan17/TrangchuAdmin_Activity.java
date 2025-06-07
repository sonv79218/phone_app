package com.example.tuan17;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.example.tuan17.adapter.NhomSanPham_trangChuadmin_Adapter;
import com.example.tuan17.database.Database;
import com.example.tuan17.database.NhomSanPhamDB;
import com.example.tuan17.database.SanPhamDB;
import com.example.tuan17.helper.BottomBar_Admin_Helper;
import com.example.tuan17.models.NhomSanPham;
import com.example.tuan17.models.SanPham;

import java.util.ArrayList;

public class TrangchuAdmin_Activity extends AppCompatActivity {

    GridView grv2;
    GridView grv1;
    ArrayList<SanPham> mangSPgrv1; // Danh sách cho GridView

    ArrayList<NhomSanPham> mangNSPgrv2; // Danh sách cho ListView

    NhomSanPham_trangChuadmin_Adapter adapterGrv2;
    SanPham_TrangChuAdmin_Adapter adapterGrv1;
    Database database;
    SanPhamDB sanPhamDB;
    NhomSanPhamDB nhomSanPhamDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trangchu_admin);
        nhomSanPhamDB = new NhomSanPhamDB(this); // truyền Context vào constructor
        sanPhamDB = new SanPhamDB(this);
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


        Loaddulieugridview2();
        Loaddulieugridview1();
    }
    private void Loaddulieugridview2() {
        mangNSPgrv2.clear();
        mangNSPgrv2.addAll(nhomSanPhamDB.getRandomNhomSanPham(8));
        adapterGrv2.notifyDataSetChanged(); // Cập nhật adapter
    }

    private void Loaddulieugridview1() {
        mangSPgrv1.clear();
        mangSPgrv1.addAll(sanPhamDB.getRandomSanPham(8));
        adapterGrv1.notifyDataSetChanged();
    }
}