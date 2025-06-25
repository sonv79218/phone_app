package com.example.tuan17;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.tuan17.adapter.DonHang_Adapter;
import com.example.tuan17.database.DonHangDB;
import com.example.tuan17.helper.BottomBar_Helper;
import com.example.tuan17.helper.SharedPrefHelper;
import com.example.tuan17.models.Order;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DonHang_User_Activity extends AppCompatActivity {
//    private Database database;
    private ListView listView;
    private DonHang_Adapter donHangAdapter;
//    private DonHangDB donHangDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_don_hang_user);
//        donHangDB = new DonHangDB(this);
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
// lấy userId
 int user_id = SharedPrefHelper.getUserId(getApplicationContext());
//        Log.d(TAG, "onCreate: "+ user_id);

        if (user_id == -1) {
            Toast.makeText(this, "không hợp lệ!", Toast.LENGTH_SHORT).show();
            finish(); // Kết thúc activity nếu không có tên đăng nhập
            return;
        }
// Kiểm tra giá trị tenDN
//        if (tenDN == null || tenDN.isEmpty()) {
//            Toast.makeText(this, "Tên đăng nhập không hợp lệ!", Toast.LENGTH_SHORT).show();
//            finish(); // Kết thúc activity nếu không có tên đăng nhập
//            return;
//        }

        loadDonHang(user_id); // Gọi phương thức loadDonHang với tenDN

BottomBar_Helper.setupBottomBar(this);
    }
//    private void loadDonHang(int id) {
//        // Kiểm tra tên khách hàng trước khi truy vấn
//        if (id == -1 ) {
//            Toast.makeText(this, "không hợp lệ!", Toast.LENGTH_SHORT).show();
//            return; // Dừng lại nếu tên khách hàng là null hoặc rỗng
//        }
//
//        // Lấy danh sách đơn hàng từ cơ sở dữ liệu
//        List<Order> orders = donHangDB.getDonHangById(id);
//        Log.d(TAG, "loadDonHang: "+ orders);
//        if (orders.isEmpty()) {
//            Toast.makeText(this, "Không tìm thấy đơn hàng cho khách hàng này!", Toast.LENGTH_SHORT).show();
//        } else {
//            // Sử dụng DonHangAdapter để hiển thị danh sách đơn hàng
//            donHangAdapter = new DonHang_Adapter(this, orders);
//            listView.setAdapter(donHangAdapter); // Gán adapter cho ListView
//        }
//    }
private void loadDonHang(int userId) {
    String url = "http://10.0.2.2:3000/dathang/user/" + userId;
//    Log.d("abc", "userId" + userId);
    JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
            response -> {
                List<Order> orders = new ArrayList<>();

                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject obj = response.getJSONObject(i);

                        int idDatHang = obj.getInt("id_dathang");
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