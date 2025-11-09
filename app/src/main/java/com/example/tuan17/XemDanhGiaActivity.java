package com.example.tuan17;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class XemDanhGiaActivity extends AppCompatActivity {
    private TextView tvNoiDung, tvNgayDanhGia;
    private RatingBar ratingBar;

    private int chitietDonhangId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xem_danhgia);

        // Ánh xạ
        tvNoiDung = findViewById(R.id.tvNoiDung);
        tvNgayDanhGia = findViewById(R.id.tvNgayDanhGia);
        ratingBar = findViewById(R.id.ratingBarXem);

        // Lấy dữ liệu từ Intent lấy userid từ đơn hanàng
        chitietDonhangId = getIntent().getIntExtra("chitietdonhangId", -1);

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
        }

        else {
            Toast.makeText(this, "Thiếu dữ liệu đầu vào!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }


}
