package com.example.tuan17;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.tuan17.adapter.DonHang_Adapter;
import com.example.tuan17.database.DonHangDB;
import com.example.tuan17.helper.BottomBar_Admin_Helper;
import com.example.tuan17.models.Order;

import java.util.List;

public class DonHang_admin_Activity extends AppCompatActivity {

    private ListView listView;
    private DonHang_Adapter donHangAdapter;
    // khai báo đối tượng donHangDB
    private DonHangDB donHangDB;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_don_hang_admin);
        donHangDB = new DonHangDB(this); // gán giá trị cho biến donhang đã được khai báo bên ngoài - làm việc tại activity này
        // Khởi tạo các thành phần
        listView = findViewById(R.id.listViewChiTiet);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Order order = donHangAdapter.getItem(position);

                if (order != null) {
                    // Hiển thị Toast với ID đơn hàng
                    Toast.makeText(DonHang_admin_Activity.this, "ID đơn hàng: " + order.getId(), Toast.LENGTH_SHORT).show();
                    // Gửi thông tin đơn hàng qua Intent
                    Intent intent = new Intent(DonHang_admin_Activity.this, ChiTietDonHang_Admin_Activity.class);
                    intent.putExtra("donHangId", String.valueOf(order.getId())); // Đảm bảo rằng ID là chuỗi
                    startActivity(intent);
                }
            }
        });
        // xử lý bottom bar
      BottomBar_Admin_Helper.setupBottomBar(this);
        loadDonHang(); // Gọi phương thức loadDonHang
    }

    private void loadDonHang() {
        // Lấy danh sách đơn hàng từ cơ sở dữ liệu
        List<Order> orders = donHangDB.getAllDonHang(); // lưu vào mảng đối tượng
        if (orders.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy đơn hàng nào!", Toast.LENGTH_SHORT).show();
        } else {
            // Sử dụng DonHangAdapter để hiển thị danh sách đơn hàng
            donHangAdapter = new DonHang_Adapter(this, orders);
            listView.setAdapter(donHangAdapter); // Gán adapter cho ListView
        }
    }
}