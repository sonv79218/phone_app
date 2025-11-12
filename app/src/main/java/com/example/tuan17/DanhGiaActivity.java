package com.example.tuan17;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

//import com.example.tuan17.database.DanhGiaDB;
import com.example.tuan17.models.DanhGia;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;


public class DanhGiaActivity extends AppCompatActivity {
    private RatingBar ratingBar;
    private EditText editTextNoiDung;
    private Button btnGui;
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
//        danhGiaDB = new DanhGiaDB(this);

        ChitietDonhangId = getIntent().getIntExtra("chitietdonhangId", -1);
        sanPhamId = getIntent().getIntExtra("sanPhamId", -1);
        userId = getIntent().getIntExtra("userId", -1);  // nên truyền từ màn hình đăng nhập
        
        btnGui.setOnClickListener(v -> {
            int soSao = (int) ratingBar.getRating();
            String noiDung = editTextNoiDung.getText().toString().trim();

            if (noiDung.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập nội dung đánh giá", Toast.LENGTH_SHORT).show();
                return;
            }

            String ngayHienTai = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

            String url = "http://10.0.2.2:3000/danhgia";  // Đổi IP nếu cần

            StringRequest request = new StringRequest(Request.Method.POST, url,
                    response -> {
                        try {
                            JSONObject res = new JSONObject(response);
                            if (res.getBoolean("success")) {
                                Toast.makeText(this, "Đánh giá đã được gửi!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(this, UserMainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(this, "Gửi đánh giá thất bại!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    },
                    error -> {
                        error.printStackTrace();
                        Toast.makeText(this, "Lỗi kết nối API!", Toast.LENGTH_SHORT).show();
                    }
            ) {
                @Override
                protected java.util.Map<String, String> getParams() {
                    java.util.Map<String, String> params = new java.util.HashMap<>();
                    params.put("user_id", String.valueOf(userId));
                    params.put("masp", String.valueOf(sanPhamId));
                    params.put("id_chitietdonhang", String.valueOf(ChitietDonhangId));
                    params.put("rating", String.valueOf(soSao));
                    params.put("comment", noiDung);
                    params.put("ngay", ngayHienTai);
                    return params;
                }
            };

            Volley.newRequestQueue(this).add(request);
        });

    }
}
