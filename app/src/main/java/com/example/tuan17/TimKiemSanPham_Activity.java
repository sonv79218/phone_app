package com.example.tuan17;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tuan17.adapter.SanPham_TimKiem_Adapter;
import com.example.tuan17.database.DatabaseHelper;
import com.example.tuan17.database.SanPhamDB;
import com.example.tuan17.helper.BottomBar_Helper;
import com.example.tuan17.helper.SharedPrefHelper;
import com.example.tuan17.models.SanPham;

import java.util.ArrayList;

public class TimKiemSanPham_Activity extends AppCompatActivity {

    private GridView grv;
    private ArrayList<SanPham> productList; // Change to ArrayList
    private SanPham_TimKiem_Adapter productAdapter;
    private DatabaseHelper dbHelper;
    String tendn;
    private SanPhamDB sanPhamDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tim_kiem_san_pham);
        sanPhamDB = new SanPhamDB(this);
        EditText timkiem=findViewById(R.id.timkiem);
        timkiem.requestFocus();
        // Initialize the GridView and DatabaseHelper
        grv = findViewById(R.id.grv);
        dbHelper = new DatabaseHelper(this);
        productList = new ArrayList<>();
        // Initialize and set the adapter with the product list
        productAdapter = new SanPham_TimKiem_Adapter(this, productList, false);
        grv.setAdapter(productAdapter);
        String tendn = SharedPrefHelper.getUsername(this);
        if (tendn == null) {
            tendn = getIntent().getStringExtra("tendn");
        }

        TextView textTendn = findViewById(R.id.tendn);
        // Kiểm tra giá trị tendn
        if (tendn != null) {
            textTendn.setText(tendn);
        } else {
            // Nếu không có tên đăng nhập, chuyển đến trang login
            Intent intent = new Intent(TimKiemSanPham_Activity.this, Login_Activity.class);
            startActivity(intent);
            finish(); // Kết thúc activity nếu chưa đăng nhập
            return;
        }
BottomBar_Helper.setupBottomBar(this);

        timkiem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = timkiem.getText().toString().trim();
                if (!query.isEmpty()) {
                    // Gọi phương thức tìm kiếm trong DatabaseHelper
                    productList.clear(); // Xóa danh sách trước khi thêm kết quả mới
                    ArrayList<SanPham> foundProducts = dbHelper.searchSanPhamByName(query);
//                    ArrayList<SanPham> foundProducts = sanPhamDB.searchSanPhamByName(query);
                    if (foundProducts.isEmpty()) {
                        Toast.makeText(TimKiemSanPham_Activity.this, "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
                    } else {
                        productList.addAll(foundProducts);
                    }
                    productAdapter.notifyDataSetChanged(); // Cập nhật adapter
                } else {
                    Toast.makeText(TimKiemSanPham_Activity.this, "Vui lòng nhập tên sản phẩm", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}