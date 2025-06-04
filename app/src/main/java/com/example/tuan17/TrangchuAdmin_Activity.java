package com.example.tuan17;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.tuan17.helper.BottomBar_Admin_Helper;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trangchu_admin);
//        TextView textTendn = findViewById(R.id.tendn); // TextView hiển thị tên đăng nhập
        grv2 = findViewById(R.id.grv2);
        grv1 = findViewById(R.id.grv1);
        // Lấy tên đăng nhập từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String tendn = sharedPreferences.getString("tendn", null);

//        // Kiểm tra tên đăng nhập
//        if (tendn != null) {
//            textTendn.setText(tendn);
//        } else {
//            Intent intent = new Intent(TrangchuNgdung_Activity.this, Login_Activity.class);
//            startActivity(intent);
//            finish(); // Kết thúc activity nếu chưa đăng nhập
//            return;
//        }
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
        adapterGrv2 = new NhomSanPham_trangChuadmin_Adapter(this, mangNSPgrv2, false) ;

         // false để hiển thị 4 thuộc tính
        grv2.setAdapter(adapterGrv2);

        adapterGrv1= new SanPham_TrangChuAdmin_Adapter(this, mangSPgrv1, false) ;

        grv1.setAdapter(adapterGrv1);


        database = new Database(this, "banhang.db", null, 1);

        Loaddulieugridview2();
        Loaddulieugridview1();
    }
    private void Loaddulieugridview2() {
        Cursor dataNhomSP = database.GetData("SELECT * from nhomsanpham order by random() limit 8 ");
        mangNSPgrv2.clear();

        while (dataNhomSP.moveToNext()) {
            String ma = dataNhomSP.getString(0);

            String ten = dataNhomSP.getString(1);
            byte[] blob = dataNhomSP.getBlob(2); // Lấy mảng byte từ cột chứa ảnh
            mangNSPgrv2.add(new NhomSanPham(ma,ten, blob));
        }

        adapterGrv2.notifyDataSetChanged(); // Cập nhật adapter
    }

    private void Loaddulieugridview1() {
        Cursor sp = database.GetData("SELECT * FROM sanpham order by random() limit 8");
        mangSPgrv1.clear();

        if (sp != null && sp.moveToFirst()) {
            do {
                String masp = sp.getString(0);
                String tensp = sp.getString(1);
                float dongia = sp.getFloat(2); // Giữ nguyên là float
                String mota = sp.getString(3);
                String ghichu = sp.getString(4);
                int soluongkho = sp.getInt(5); // Giữ nguyên là int
                String maso = sp.getString(6);
                byte[] blob = sp.getBlob(7);
                mangSPgrv1.add(new SanPham(masp,tensp,dongia,mota,ghichu,soluongkho,maso,blob));
            } while (sp.moveToNext());
        } else {
            Toast.makeText(this, "Null load dữ liệu", Toast.LENGTH_SHORT).show();
        }

        adapterGrv1.notifyDataSetChanged();
    }
}