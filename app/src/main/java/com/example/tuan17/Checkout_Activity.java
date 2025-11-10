package com.example.tuan17;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tuan17.helper.SharedPrefHelper;
import com.example.tuan17.models.GioHang;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Checkout_Activity extends AppCompatActivity {

    private ArrayList<GioHang> selectedItems;
    private float selectedTotal;
    private GioHangManager gioHangManager;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thong_tin_thanh_toan);

        gioHangManager = GioHangManager.getInstance();
        requestQueue = Volley.newRequestQueue(this);

        Intent intent = getIntent();
        selectedItems = intent.getParcelableArrayListExtra("selectedItems");
        selectedTotal = intent.getFloatExtra("selectedTotal", 0f);

        EditText edtTenKh = findViewById(R.id.tenkh);
        EditText edtDiaChi = findViewById(R.id.diachi);
        EditText edtSdt = findViewById(R.id.sdt);
        Button btnXacNhan = findViewById(R.id.btnxacnhandathang);
        TextView tvTongTien = findViewById(R.id.tienthanhtoan);

        tvTongTien.setText(String.valueOf(selectedTotal));

        btnXacNhan.setOnClickListener(v -> {
            if (selectedItems == null || selectedItems.isEmpty()) {
                Toast.makeText(this, "Không có sản phẩm được chọn!", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            String tenKh = edtTenKh.getText().toString().trim();
            String diaChi = edtDiaChi.getText().toString().trim();
            String sdt = edtSdt.getText().toString().trim();

            if (tenKh.isEmpty() || diaChi.isEmpty() || sdt.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }

            int userId = SharedPrefHelper.getUserId(this);
            String urlOrder = "http://10.0.2.2:3000/dathang";

            StringRequest request = new StringRequest(Request.Method.POST, urlOrder,
                    response -> {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.has("id")) {
                                Toast.makeText(this, "Không có mã đơn hàng trả về!", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            int orderId = obj.getInt("id");

                            // Gửi chi tiết từng sản phẩm
                            for (GioHang item : selectedItems) {
                                String masp = item.getSanPham().getMasp();
                                int soluong = item.getSoLuong();
                                float dongia = item.getSanPham().getDongia();
                                byte[] anhByte = item.getSanPham().getAnh();
                                String base64Image = (anhByte != null)
                                        ? Base64.encodeToString(anhByte, Base64.DEFAULT)
                                        : "";
                                themChiTietDonHang(orderId, masp, soluong, dongia, base64Image);
                            }

                            Toast.makeText(this, "Đặt hàng thành công!", Toast.LENGTH_SHORT).show();

                            // Xóa hàng đã chọn khỏi giỏ
                            Set<String> ids = new HashSet<>();
                            for (GioHang g : selectedItems) ids.add(g.getSanPham().getMasp());
                            gioHangManager.removeByProductIds(ids);

                            startActivity(new Intent(this, UserMainActivity.class));
                            finish();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Lỗi phân tích dữ liệu!", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        Toast.makeText(this, "Lỗi đặt hàng!", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> data = new HashMap<>();
                    data.put("user_id", String.valueOf(userId));
                    data.put("tenkh", tenKh);
                    data.put("diachi", diaChi);
                    data.put("sdt", sdt);
                    data.put("tongthanhtoan", String.valueOf(selectedTotal));
                    return data;
                }
            };

            requestQueue.add(request);
        });
    }

    private void themChiTietDonHang(int idDonHang, String masp, int soluong, float dongia, String base64Anh) {
        String url = "http://10.0.2.2:3000/chitietdathang";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {},
                error -> error.printStackTrace()) {
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

        requestQueue.add(request);
    }
}
