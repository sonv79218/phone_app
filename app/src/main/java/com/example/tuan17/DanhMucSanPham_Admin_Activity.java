package com.example.tuan17;

import android.content.Intent;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tuan17.adapter.SanPham_DanhMuc_Adapter;
import com.example.tuan17.adapter.SanPham_DanhMuc_Admin_Adapter;
import com.example.tuan17.helper.BottomBar_Admin_Helper;
import com.example.tuan17.models.SanPham;

import java.util.ArrayList;
import java.util.List;

public class DanhMucSanPham_Admin_Activity extends AppCompatActivity {
    private GridView grv;
    private ArrayList<SanPham> productList; // Change to ArrayList
    private SanPham_DanhMuc_Admin_Adapter productAdapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_danh_muc_san_pham_admin);

        // Initialize the GridView and DatabaseHelper
        BottomBar_Admin_Helper.setupBottomBar(this);
        grv = findViewById(R.id.grv);
        dbHelper = new DatabaseHelper(this);
        // Retrieve nhomSpId from the Intent
        String nhomSpId = getIntent().getStringExtra("nhomSpId");
        // Check if nhomSpId is not null
        if (nhomSpId != null) {
            // Get the list of products by nhomSpId
            List<SanPham> tempProductList = dbHelper.getProductsByNhomSpId(nhomSpId); // Use a temporary variable
            if (tempProductList != null && !tempProductList.isEmpty()) {
                // Convert List to ArrayList
                productList = new ArrayList<>(tempProductList);
                // Initialize and set the adapter with the product list
                productAdapter = new SanPham_DanhMuc_Admin_Adapter(this, productList, false);
                grv.setAdapter(productAdapter);
            } else {
                Toast.makeText(this, "Không tìm thấy sản phẩm nào trong nhóm này!", Toast.LENGTH_SHORT).show();
            }
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
}
