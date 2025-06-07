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
import com.example.tuan17.helper.BottomBar_Helper;
import com.example.tuan17.helper.SharedPrefHelper;
import com.example.tuan17.models.Order;

import java.util.List;

public class DonHang_User_Activity extends AppCompatActivity {
//    private Database database;
    private ListView listView;
    private DonHang_Adapter donHangAdapter;
    private DonHangDB donHangDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_don_hang_user);
        donHangDB = new DonHangDB(this);
        // Khởi tạo các thành phần
        listView = findViewById(R.id.listViewChiTiet);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Order order = donHangAdapter.getItem(position);

                if (order != null) {
                    // Hiển thị Toast với ID đơn hàng
                    Toast.makeText(DonHang_User_Activity.this, "ID đơn hàng: " + order.getId(), Toast.LENGTH_SHORT).show();

                    // Gửi thông tin đơn hàng qua Intent
                    Intent intent = new Intent(DonHang_User_Activity.this, ChiTietDonHang_Activity.class);
                    intent.putExtra("donHangId", String.valueOf(order.getId())); // Đảm bảo rằng ID là chuỗi
                    startActivity(intent);
                }
            }


        });


        // Tạo bảng nếu chưa tồn tại
//        createTableIfNotExists();
String tenDN = SharedPrefHelper.getUsername(this);
if( tenDN == null){
    // Lấy tên đăng nhập từ Intent
    tenDN = getIntent().getStringExtra("tendn");
}


// Kiểm tra giá trị tenDN
        if (tenDN == null || tenDN.isEmpty()) {
            Toast.makeText(this, "Tên đăng nhập không hợp lệ!", Toast.LENGTH_SHORT).show();
            finish(); // Kết thúc activity nếu không có tên đăng nhập
            return;
        }

        loadDonHang(tenDN); // Gọi phương thức loadDonHang với tenDN

BottomBar_Helper.setupBottomBar(this);
    }
    private void loadDonHang(String tenKh) {
        // Kiểm tra tên khách hàng trước khi truy vấn
        if (tenKh == null || tenKh.isEmpty()) {
            Toast.makeText(this, "Tên khách hàng không hợp lệ!", Toast.LENGTH_SHORT).show();
            return; // Dừng lại nếu tên khách hàng là null hoặc rỗng
        }

        // Lấy danh sách đơn hàng từ cơ sở dữ liệu
        List<Order> orders = donHangDB.getDonHangByTenKh(tenKh);
        if (orders.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy đơn hàng cho khách hàng này!", Toast.LENGTH_SHORT).show();
        } else {
            // Sử dụng DonHangAdapter để hiển thị danh sách đơn hàng
            donHangAdapter = new DonHang_Adapter(this, orders);
            listView.setAdapter(donHangAdapter); // Gán adapter cho ListView
        }
    }
}