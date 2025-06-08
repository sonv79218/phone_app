package com.example.tuan17;

import android.os.Bundle;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tuan17.database.DanhGiaDB;
import com.example.tuan17.models.DanhGia;

public class XemDanhGiaActivity extends AppCompatActivity {
    private TextView tvNoiDung, tvNgayDanhGia;
    private RatingBar ratingBar;
    private DanhGiaDB danhGiaDB;

    private int userId, sanPhamId, chitietDonhangId;

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

        // Mở DB
        danhGiaDB = new DanhGiaDB(this);

        // Lấy đánh giá từ DB
        DanhGia danhGia = danhGiaDB.layDanhGia(userId, sanPhamId, chitietDonhangId);

        if (danhGia != null) {
            ratingBar.setRating(danhGia.getRating());
            tvNoiDung.setText(danhGia.getComment());
            tvNgayDanhGia.setText("Ngày đánh giá: " + danhGia.getNgayDanhGia());
        } else {
            Toast.makeText(this, "Người dùng chưa đánh giá!", Toast.LENGTH_SHORT).show();
            finish(); // Đóng nếu không có dữ liệu
        }
    }
}
