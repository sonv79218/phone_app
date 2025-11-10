package com.example.tuan17;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.tuan17.adapter.DonHang_Adapter;
import com.example.tuan17.database.DonHangDB;
import com.example.tuan17.helper.BottomBar_Admin_Helper;
import com.example.tuan17.models.Order;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DonHang_admin_Activity extends AppCompatActivity {

    private ListView listView;
    private DonHang_Adapter donHangAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_don_hang_admin);
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
    String url = "http://10.0.2.2:3000/dathang/all";
//    Log.d("abc", "userId" + userId);
    JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
            response -> {
                List<Order> orders = new ArrayList<>();

                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject obj = response.getJSONObject(i);

//                        int idDatHang = obj.getInt("id_dathang");
                        int idDatHang = obj.getInt("id");
                        String tenkh = obj.getString("tenkh");
                        String diachi = obj.getString("diachi");
                        String sdt = obj.getString("sdt");
                        float tong = (float) obj.getDouble("tongthanhtoan");
                        String ngaydat = obj.getString("ngaydathang");
//                        Log.d(TAG, "loadDonHang: " + tenkh);
                        orders.add(new Order(idDatHang, tenkh, diachi, sdt, tong, ngaydat));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (orders.isEmpty()) {
                    Toast.makeText(this, "Không tìm thấy đơn hàng!", Toast.LENGTH_SHORT).show();
                } else {
                    donHangAdapter = new DonHang_Adapter(this, orders);
                    listView.setAdapter(donHangAdapter);
                }
            },
            error -> {
                Toast.makeText(this, "Lỗi kết nối hoặc không có đơn hàng", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
    );

    Volley.newRequestQueue(this).add(request);
}
}