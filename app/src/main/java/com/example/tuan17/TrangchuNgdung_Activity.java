package com.example.tuan17;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class TrangchuNgdung_Activity extends AppCompatActivity {
    GridView grv2;
    GridView grv1;
    ArrayList<SanPham> mangSPgrv1; // Danh sách cho GridView
    ArrayList<NhomSanPham> mangNSPgrv2; // Danh sách cho ListView
    NhomSanPhamAdapter adapterGrv2;
    SanPhamAdapter adapterGrv1;
    Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trangchu_ngdung);
        ImageButton btntimkiem = findViewById(R.id.btntimkiem);
        ImageButton btntrangchu = findViewById(R.id.btntrangchu);
        ImageButton btncard = findViewById(R.id.btncart);
        ImageButton btndonhang = findViewById(R.id.btndonhang);
        ImageButton btncanhan = findViewById(R.id.btncanhan);
        EditText timkiem = findViewById(R.id.timkiem);
        TextView textTendn = findViewById(R.id.tendn); // TextView hiển thị tên đăng nhập
        grv2 = findViewById(R.id.grv2);
        grv1 = findViewById(R.id.grv1);

        // Lấy tên đăng nhập từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String tendn = sharedPreferences.getString("tendn", null);

        // Kiểm tra tên đăng nhập
        if (tendn != null) {
            textTendn.setText(tendn);
        } else {
            Intent intent = new Intent(TrangchuNgdung_Activity.this, Login_Activity.class);
            startActivity(intent);
            finish(); // Kết thúc activity nếu chưa đăng nhập
            return;
        }
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

        // Gửi tên đăng nhập qua Intent trong sự kiện click
        btntrangchu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TrangchuNgdung_Activity.this, DonHang_User_Activity.class);
                // Gửi tên đăng nhập qua Intent
                intent.putExtra("tendn", tendn); // sử dụng biến tendn đã được xác nhận
                startActivity(intent);
            }
        });
        btntimkiem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TrangchuNgdung_Activity.this, TimKiemSanPham_Activity.class);

                // Gửi tên đăng nhập qua Intent
                intent.putExtra("tendn", tendn); // Sử dụng biến tendn đã được lấy từ SharedPreferences

                startActivity(intent);
            }
        });

        btndonhang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TrangchuNgdung_Activity.this, DonHang_User_Activity.class);
                intent.putExtra("tendn", tendn); // Gửi tên đăng nhập
                startActivity(intent);
            }
        });
        btncard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TrangchuNgdung_Activity.this, GioHang_Activity.class);
                intent.putExtra("tendn", tendn); // Gửi tên đăng nhập
                startActivity(intent);
            }
        });
        btncanhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TrangchuNgdung_Activity.this, TrangCaNhan_nguoidung_Activity.class);
                intent.putExtra("tendn", tendn); // Gửi tên đăng nhập
                startActivity(intent);
            }
        });
        // Các sự kiện khác
        timkiem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TrangchuNgdung_Activity.this, TimKiemSanPham_Activity.class);

                // Gửi tên đăng nhập qua Intent
                intent.putExtra("tendn", tendn); // Sử dụng biến tendn đã được lấy từ SharedPreferences

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

        database = new Database(this, "banhang.db", null, 1);

        Loaddulieubacsigridview2();
        Loaddulieubacsigridview1();
    }

    private void Loaddulieubacsigridview2() {
        Cursor dataCongViec = database.GetData("SELECT * from nhomsanpham order by random() limit 8 ");
        mangNSPgrv2.clear();

        while (dataCongViec.moveToNext()) {
            String ma = dataCongViec.getString(0);
            String ten = dataCongViec.getString(1);
            byte[] blob = dataCongViec.getBlob(2); // Lấy mảng byte từ cột chứa ảnh
            mangNSPgrv2.add(new NhomSanPham(ma, ten, blob));
        }

        adapterGrv2.notifyDataSetChanged(); // Cập nhật adapter
    }

    private void Loaddulieubacsigridview1() {
        Cursor cursor = database.GetData("SELECT * FROM sanpham order by random() limit 8");
        mangSPgrv1.clear();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String masp = cursor.getString(0);
                String tensp = cursor.getString(1);
                float dongia = cursor.getFloat(2); // Giữ nguyên là float
                String mota = cursor.getString(3);
                String ghichu = cursor.getString(4);
                int soluongkho = cursor.getInt(5); // Giữ nguyên là int
                String maso = cursor.getString(6);
                byte[] blob = cursor.getBlob(7);
                mangSPgrv1.add(new SanPham(masp, tensp, dongia, mota, ghichu, soluongkho, maso, blob));
            } while (cursor.moveToNext());
        } else {
            Toast.makeText(this, "Null load dữ liệu", Toast.LENGTH_SHORT).show();
        }

        adapterGrv1.notifyDataSetChanged();
    }
}
