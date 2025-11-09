package com.example.tuan17;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tuan17.adapter.TaiKhoanAdapter;
import com.example.tuan17.models.TaiKhoan;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

public class DoiMatKhau_Activity extends AppCompatActivity {
    EditText edtCu, edtMoi, edtLai;
    Button btnDoi;
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doi_mat_khau);

        edtCu = findViewById(R.id.edtMatKhauCu);
        edtMoi = findViewById(R.id.edtMatKhauMoi);
        edtLai = findViewById(R.id.edtNhapLai);
        btnDoi = findViewById(R.id.btnDoiMatKhau);

        SharedPreferences sp = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        userId = sp.getInt("user_id", -1);

        btnDoi.setOnClickListener(v -> {
            String cu = edtCu.getText().toString().trim();
            String moi = edtMoi.getText().toString().trim();
            String lai = edtLai.getText().toString().trim();

            if (cu.isEmpty() || moi.isEmpty() || lai.isEmpty()) {
                Toast.makeText(this, "Không được bỏ trống", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!moi.equals(lai)) {
                Toast.makeText(this, "Mật khẩu mới không khớp", Toast.LENGTH_SHORT).show();
                return;
            }

            doiMatKhau(userId, cu, moi);
        });
    }

    private void doiMatKhau(int id, String cu, String moi) {
        String url = "http://10.0.2.2:3000/taikhoan/doimatkhau";
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getBoolean("success")) {
                            Toast.makeText(this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, json.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> m = new HashMap<>();
                m.put("id", String.valueOf(id));
                m.put("matkhau_cu", cu);
                m.put("matkhau_moi", moi);
                return m;
            }
        };

        queue.add(request);
    }
}
