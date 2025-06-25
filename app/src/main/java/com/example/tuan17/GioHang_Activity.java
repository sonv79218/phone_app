package com.example.tuan17;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tuan17.adapter.GioHangAdapter;
import com.example.tuan17.database.Database;
import com.example.tuan17.database.DonHangDB;
import com.example.tuan17.helper.BottomBar_Helper;
import com.example.tuan17.helper.SharedPrefHelper;
import com.example.tuan17.models.GioHang;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GioHang_Activity extends AppCompatActivity {
    private ListView listView;
    private GioHangAdapter adapter;
    private GioHangManager gioHangManager;
    private Button thanhtoan;
    private OrderManager orderManager;
    private TextView txtTongTien; // Khai báo TextView cho tổng tiền
    private DonHangDB donHangDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gio_hang);
        thanhtoan = findViewById(R.id.btnthanhtoan);
        listView = findViewById(R.id.listtk);
        TextView textTendn = findViewById(R.id.tendn);

        // Lấy tendn từ SharedPreferences
        String tendn = SharedPrefHelper.getUsername(this);

        if (tendn != null) {
            textTendn.setText(tendn);
        } else {
            Intent intent = new Intent(GioHang_Activity.this, Login_Activity.class);
            startActivity(intent);
            finish(); // Kết thúc activity nếu chưa đăng nhập
            return;
        }

        txtTongTien = findViewById(R.id.tongtien); // Khởi tạo TextView cho tổng tiền


        gioHangManager = GioHangManager.getInstance();
        orderManager = new OrderManager(this);

        // Lấy danh sách giỏ hàng và cập nhật giao diện
        List<GioHang> gioHangList = gioHangManager.getGioHangList();
        adapter = new GioHangAdapter(this, gioHangList, txtTongTien);
        listView.setAdapter(adapter);

        // Cập nhật tổng tiền ngay từ giỏ hàng
        txtTongTien.setText(String.valueOf(gioHangManager.getTongTien()));

        // Xử lý sự kiện click thanh toán
        thanhtoan.setOnClickListener(v -> showPaymentDialog());
        BottomBar_Helper.setupBottomBar(this);

    }

    private void showPaymentDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.activity_thong_tin_thanh_toan);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER);

        EditText edtTenKh = dialog.findViewById(R.id.tenkh);
        EditText edtDiaChi = dialog.findViewById(R.id.diachi);
        EditText edtSdt = dialog.findViewById(R.id.sdt);
        Button btnLuu = dialog.findViewById(R.id.btnxacnhandathang);
        TextView tvTongTien = dialog.findViewById(R.id.tienthanhtoan);

        String tongTien = txtTongTien.getText().toString();
        tvTongTien.setText(tongTien);

        btnLuu.setOnClickListener(v -> {
            String tenKh = edtTenKh.getText().toString().trim();
            String diaChi = edtDiaChi.getText().toString().trim();
            String sdt = edtSdt.getText().toString().trim();

            if (tenKh.isEmpty() || diaChi.isEmpty() || sdt.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            } else {
                float tongThanhToan;
                try {
                    tongThanhToan = Float.parseFloat(tongTien.replace(",", ""));
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Có lỗi xảy ra với tổng tiền!", Toast.LENGTH_SHORT).show();
                    return;
                }
//
//                if (orderManager != null) {
//                    long orderId = orderManager.addOrder(tenKh, diaChi, sdt, tongThanhToan); // trả về id đơn hàng sau khi thực hiện
//                    if (orderId > 0) {
//                        // Lưu thông tin chi tiết đơn hàng
//                        List<GioHang> gioHangList = gioHangManager.getGioHangList();
//                        for (GioHang item : gioHangList) {
//                            String masp = item.getSanPham().getMasp();
//                            int soluong = item.getSoLuong();
//                            float dongia = item.getSanPham().getDongia();
//                            byte[] anhByteArray = item.getSanPham().getAnh();
//
//                            // Gọi phương thức addOrderDetails
//                            orderManager.addOrderDetails((int) orderId, masp, soluong, dongia, anhByteArray);
//                        }
//
//                        Toast.makeText(this, "Đặt hàng thành công!", Toast.LENGTH_SHORT).show();
//                        gioHangManager.clearGioHang(); // Xóa giỏ hàng
//                        txtTongTien.setText("0"); // Đặt tổng tiền về 0
//
//                        adapter.notifyDataSetChanged(); // Cập nhật lại giao diện
//                        Intent a = new Intent(GioHang_Activity.this, TrangchuNgdung_Activity.class);
//                        startActivity(a);
//                    } else {
//                        Toast.makeText(this, "Đặt hàng thất bại!", Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    Toast.makeText(this, "Không thể xử lý đơn hàng, hãy thử lại!", Toast.LENGTH_SHORT).show();
//                }
//                dialog.dismiss(); // Đóng dialog sau khi xử lý
                int userId = SharedPrefHelper.getUserId(this); // Lấy user_id từ SharedPreferences

                String urlOrder = "http://10.0.2.2:3000/dathang";
                StringRequest request = new StringRequest(Request.Method.POST, urlOrder,
                        response -> {
                            try {
                                JSONObject obj = new JSONObject(response);
                                int orderId = obj.getInt("id"); // ID đơn hàng trả về từ server

                                // Sau khi thêm đơn hàng thành công, thêm chi tiết đơn hàng
                                List<GioHang> gioHangList = gioHangManager.getGioHangList();
                                for (GioHang item : gioHangList) {
                                    String masp = item.getSanPham().getMasp();
                                    int soluong = item.getSoLuong();
                                    float dongia = item.getSanPham().getDongia();
                                    byte[] anhByte = item.getSanPham().getAnh();
                                    String base64Image = Base64.encodeToString(anhByte, Base64.DEFAULT);

                                    // Gọi API thêm chi tiết
                                    themChiTietDonHang(orderId, masp, soluong, dongia, base64Image);
                                }

                                Toast.makeText(this, "Đặt hàng thành công!", Toast.LENGTH_SHORT).show();
                                gioHangManager.clearGioHang();
                                txtTongTien.setText("0");
                                adapter.notifyDataSetChanged();
                                startActivity(new Intent(this, TrangchuNgdung_Activity.class));
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(this, "Lỗi phân tích dữ liệu!", Toast.LENGTH_SHORT).show();
                            }
                        },
                        error -> {
                            Toast.makeText(this, "Lỗi đặt hàng!", Toast.LENGTH_SHORT).show();
                            error.printStackTrace();
                        }
                ) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> data = new HashMap<>();
                        data.put("user_id", String.valueOf(userId));
                        data.put("tenkh", tenKh);
                        data.put("diachi", diaChi);
                        data.put("sdt", sdt);
                        data.put("tongthanhtoan", String.valueOf(tongThanhToan));
                        return data;
                    }
                };

                Volley.newRequestQueue(this).add(request);
                dialog.dismiss(); // Đóng dialog sau khi gửi request

            }
        });

        dialog.show();
    }
    private void themChiTietDonHang(int idDonHang, String masp, int soluong, float dongia, String base64Anh) {
        String url = "http://10.0.2.2:3000/chitietdathang";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    // Có thể log nếu cần
                },
                error -> {
                    error.printStackTrace();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_dathang", String.valueOf(idDonHang));
                params.put("masp", masp);
                params.put("soluong", String.valueOf(soluong));
                params.put("dongia", String.valueOf(dongia));
                params.put("anh", base64Anh);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

}
