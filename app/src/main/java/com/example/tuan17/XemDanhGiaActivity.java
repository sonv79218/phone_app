package com.example.tuan17;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tuan17.database.DanhGiaDB;
import com.example.tuan17.models.DanhGia;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class XemDanhGiaActivity extends AppCompatActivity {
    private TextView tvNoiDung, tvNgayDanhGia;
    private RatingBar ratingBar;
    private DanhGiaDB danhGiaDB;

    private int userId, sanPhamId, chitietDonhangId, masp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xem_danhgia);

        // Ánh xạ
        tvNoiDung = findViewById(R.id.tvNoiDung);
        tvNgayDanhGia = findViewById(R.id.tvNgayDanhGia);
        ratingBar = findViewById(R.id.ratingBarXem);

        // Lấy dữ liệu từ Intent lấy userid từ đơn hanàng
        userId = getIntent().getIntExtra("userId", -1);
        sanPhamId = getIntent().getIntExtra("sanPhamId", -1);
        chitietDonhangId = getIntent().getIntExtra("chitietdonhangId", -1);
        masp = getIntent().getIntExtra("masp",-1);
        // Mở DB
//        danhGiaDB = new DanhGiaDB(this);
//
//        // Lấy đánh giá từ DB
//        DanhGia danhGia = danhGiaDB.layDanhGia(userId, sanPhamId, chitietDonhangId);
//
//        if (danhGia != null) {
//            ratingBar.setRating(danhGia.getRating());
//            tvNoiDung.setText(danhGia.getComment());
//            tvNgayDanhGia.setText("Ngày đánh giá: " + danhGia.getNgayDanhGia());
//        } else {
//            Toast.makeText(this, "Người dùng chưa đánh giá!", Toast.LENGTH_SHORT).show();
//            finish(); // Đóng nếu không có dữ liệu
//        }
//        String url = "http://10.0.2.2:3000/danhgia/lay?user_id=" + userId +
//                "&masp=" + sanPhamId + "&id_chitietdonhang=" + chitietDonhangId;
//        String url = "http://10.0.2.2:3000/danhgia/lay?id_chitietdonhang=" + chitietDonhangId;
//        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
//                response -> {
//                    try {
//                        if (response.getBoolean("success")) {
//                            JSONObject data = response.getJSONObject("data");
//
//                            int rating = data.getInt("rating");
//                            String comment = data.getString("comment");
//                            String ngay = data.getString("ngay_danhgia");
//
//                            ratingBar.setRating(rating);
//                            tvNoiDung.setText(comment);
//                            tvNgayDanhGia.setText("Ngày đánh giá: " + ngay);
//                        } else {
//                            Toast.makeText(this, "Người dùng chưa đánh giá!", Toast.LENGTH_SHORT).show();
////                            finish();
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                        Toast.makeText(this, "Lỗi xử lý dữ liệu!", Toast.LENGTH_SHORT).show();
//                    }
//                },
//                error -> {
//                    error.printStackTrace();
//                    Toast.makeText(this, "Lỗi kết nối máy chủ!", Toast.LENGTH_SHORT).show();
//                }
//        );
//
//        Volley.newRequestQueue(this).add(request);
        if (chitietDonhangId != -1) {
            // ✅ Trường hợp 1: Lấy theo chi tiết đơn hàng
            String url = "http://10.0.2.2:3000/danhgia/lay?id_chitietdonhang=" + chitietDonhangId;

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                    response -> {
                        try {
                            if (response.getBoolean("success")) {
                                JSONObject data = response.getJSONObject("data");
                                int rating = data.getInt("rating");
                                String comment = data.getString("comment");
                                String ngay = data.getString("ngay_danhgia");

                                ratingBar.setRating(rating);
                                tvNoiDung.setText(comment);
                                tvNgayDanhGia.setText("Ngày đánh giá: " + ngay);
                            } else {
                                Toast.makeText(this, "Không có đánh giá!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Lỗi xử lý dữ liệu!", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        error.printStackTrace();
                        Toast.makeText(this, "Lỗi kết nối máy chủ!", Toast.LENGTH_SHORT).show();
                    });

            Volley.newRequestQueue(this).add(request);
        } else if (masp != -1) {
//             ✅ Trường hợp 2: Lấy nhiều đánh giá theo sản phẩm (masp)
//             → Gợi ý: bạn nên chuyển sang 1 RecyclerView để hiển thị danh sách đánh giá

            Intent intent = new Intent(this, DanhSachDanhGiaActivity.class);
            intent.putExtra("masp", masp);
            startActivity(intent);
            finish(); // kết thúc activity hiện tại vì chuyển sang danh sách đánh giá
        } else {
            Toast.makeText(this, "Thiếu dữ liệu đầu vào!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }


}
