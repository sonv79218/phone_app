package com.example.tuan17;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tuan17.database.DanhGiaDB;
import com.example.tuan17.models.DanhGia;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DanhGiaActivity extends AppCompatActivity {
    private RatingBar ratingBar;
    private EditText editTextNoiDung;
    private Button btnGui;
    private DanhGiaDB danhGiaDB;
    private int sanPhamId;  // bạn truyền vào khi gọi Activity
    private int userId;     // truyền
    private int ChitietDonhangId; // truyền

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_danh_gia);

        ratingBar = findViewById(R.id.ratingBar);
        editTextNoiDung = findViewById(R.id.editTextNoiDung);


        btnGui = findViewById(R.id.btnGuiDanhGia);
        danhGiaDB = new DanhGiaDB(this);

        ChitietDonhangId = getIntent().getIntExtra("chitietdonhangId", -1);
        sanPhamId = getIntent().getIntExtra("sanPhamId", -1);
        userId = getIntent().getIntExtra("userId", -1);  // nên truyền từ màn hình đăng nhập

//        boolean daDanhGia = danhGiaDB.daDanhGia(userId, sanPhamId, ChitietDonhangId);

//        if(daDanhGia){
//
//        }
//        else {
//
//        }
//

        btnGui.setOnClickListener(v -> {
            int soSao = (int) ratingBar.getRating();
            String noiDung = editTextNoiDung.getText().toString().trim();

            if (noiDung.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập nội dung đánh giá", Toast.LENGTH_SHORT).show();
                return;
            }
            String ngayHienTai = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            DanhGia danhGia = new DanhGia(userId, sanPhamId, ChitietDonhangId, soSao, noiDung, ngayHienTai);
            danhGia.setUserId(userId);
            danhGia.setProductId(sanPhamId);
            danhGia.setChitietdonhangId(ChitietDonhangId);
            danhGia.setRating(soSao);
            danhGia.setComment(noiDung);
            danhGia.setNgayDanhGia(ngayHienTai);

            boolean success = danhGiaDB.themDanhGia(danhGia);
            if (success) {
                Toast.makeText(this, "Đánh giá đã được gửi!", Toast.LENGTH_SHORT).show();
                finish(); // hoặc chuyển về danh sách sản phẩm

                // Ghi log thông tin đánh giá
//                Log.d("DanhGiaActivity", "Đánh giá mới: userId=" + userId
//                        + ", productId=" + sanPhamId
//                        + ", rating=" + soSao
//                        + ", noiDung=" + noiDung
//                        + ", ngay=" + ngayHienTai);
            } else {
                Toast.makeText(this, "Gửi đánh giá thất bại!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
